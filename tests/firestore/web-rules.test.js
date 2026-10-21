/**
 * Emulator tests for the WEB client's Firestore access patterns.
 *
 * The web app (web/src) talks to the same Firebase project as the Android app
 * (project shelfmate-89898) and the same `users/{uid}` subtree. These tests
 * mirror the exact calls the web client makes, so a rules change that would
 * break the browser client fails here instead of in production.
 *
 * Web client calls (web/src/data/shelves.ts, web/src/pages/*.tsx):
 *   doc('users', uid).get()                              -> profile + role read
 *   doc('users', uid).set(profile, {merge})              -> profile sync
 *   doc('users', uid).set({role}, {merge})               -> role assignment
 *   collection('users', uid, 'bookshelf').onSnapshot()   -> shelf list
 *   doc('users', uid, 'bookshelf', id).set/set/update    -> save / move book
 *   doc('users', uid, 'bookshelf', id).delete()          -> remove book
 *   collection('users', uid, 'custom_shelves').onSnapshot()
 *   doc('users', uid, 'custom_shelves', id).set/delete
 *   collection('users', uid, 'reading_progress').onSnapshot()
 *   doc('users', uid, 'reading_progress', id).set({merge})
 *   collection('users', uid, 'club_memberships').onSnapshot()
 *
 * Run: npm test   (from this directory)
 */

const {
  initializeTestEnvironment,
  assertSucceeds,
  assertFails,
} = require("@firebase/rules-unit-testing");

const { readFileSync } = require("fs");
const path = require("path");

const HOST = "127.0.0.1";
const PORT = 8080;
const PROJECT_ID = "demo-shelfmates";

const WEB_USER = "web_reader_uid";
const OTHER_USER = "other_reader_uid";

let testEnv;

function rules() {
  return readFileSync(path.join(__dirname, "..", "..", "firestore.rules"), "utf8");
}

function client(uid) {
  return testEnv.authenticatedContext(uid).firestore();
}

function anon() {
  return testEnv.unauthenticatedContext().firestore();
}

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: { host: HOST, port: PORT },
    rules: rules(),
  });
});

after(async () => {
  await testEnv.cleanup();
});

afterEach(async () => {
  await testEnv.clearFirestore();
});

describe("web client - profile and role", () => {
  it("allows the web client to write the profile merge it sends", async () => {
    const db = client(WEB_USER);
    await assertSucceeds(
      db.doc("users/" + WEB_USER).set({
        uid: WEB_USER,
        email: "reader@example.com",
        displayName: "Web Reader",
        photoUrl: "",
        lastActive: 1700000000000,
        isAnonymous: false,
      }, { merge: true }),
    );
  });

  it("allows the web client to read back the role it stored", async () => {
    const db = client(WEB_USER);
    await assertSucceeds(db.doc("users/" + WEB_USER).set({ role: "READER" }, { merge: true }));
    const snap = await assertSucceeds(db.doc("users/" + WEB_USER).get());
    if (snap.exists) {
      const data = snap.data();
      if (data.role !== "READER") throw new Error("expected role READER");
    }
  });

  it("refuses the web client from setting ADMIN", async () => {
    const db = client(WEB_USER);
    await assertFails(
      db.doc("users/" + WEB_USER).set({ role: "ADMIN" }, { merge: true }),
    );
  });

  it("refuses the web client from reading another user's profile", async () => {
    const db = client(WEB_USER);
    await assertFails(db.doc("users/" + OTHER_USER).get());
  });
});

describe("web client - bookshelf", () => {
  it("allows the web client to write the shelf book document it builds", async () => {
    const db = client(WEB_USER);
    const id = "saved_" + WEB_USER + "_abc123";
    await assertSucceeds(
      db.doc("users/" + WEB_USER + "/bookshelf/" + id).set({
        id,
        userId: WEB_USER,
        googleBooksId: "abc123",
        title: "Dune",
        authors: "Frank Herbert",
        coverUrl: "https://books.google.com/t.jpg",
        category: "To Read",
        description: "",
        genre: "Science Fiction",
        pageCount: 412,
        rating: 4.3,
        isbn13: "9780441013593",
        notes: "",
        personalRating: 0,
        savedAt: 1700000000000,
        lastUpdated: 1700000000000,
      }, { merge: true }),
    );
  });

  it("allows the web client to query its own bookshelf collection", async () => {
    const db = client(WEB_USER);
    const id = "saved_" + WEB_USER + "_abc123";
    await assertSucceeds(
      db.doc("users/" + WEB_USER + "/bookshelf/" + id).set({ userId: WEB_USER, title: "Dune" }, { merge: true }),
    );
    await assertSucceeds(db.collection("users/" + WEB_USER + "/bookshelf").get());
  });

  it("allows the web client to move a book between categories (update)", async () => {
    const db = client(WEB_USER);
    const id = "saved_" + WEB_USER + "_abc123";
    await assertSucceeds(
      db.doc("users/" + WEB_USER + "/bookshelf/" + id).set({ userId: WEB_USER, category: "To Read" }, { merge: true }),
    );
    await assertSucceeds(
      db.doc("users/" + WEB_USER + "/bookshelf/" + id).update({ category: "Currently Reading" }),
    );
  });

  it("allows the web client to remove a book from its shelf (delete)", async () => {
    const db = client(WEB_USER);
    const id = "saved_" + WEB_USER + "_abc123";
    await assertSucceeds(
      db.doc("users/" + WEB_USER + "/bookshelf/" + id).set({ userId: WEB_USER }, { merge: true }),
    );
    await assertSucceeds(db.doc("users/" + WEB_USER + "/bookshelf/" + id).delete());
  });

  it("refuses the web client from querying another user's bookshelf", async () => {
    const db = client(WEB_USER);
    await assertFails(db.collection("users/" + OTHER_USER + "/bookshelf").get());
  });

  it("refuses the web client from writing into another user's bookshelf", async () => {
    const db = client(WEB_USER);
    await assertFails(
      db.doc("users/" + OTHER_USER + "/bookshelf/saved_x").set({ userId: OTHER_USER }, { merge: true }),
    );
  });

  it("refuses a shelf document whose userId points at another account", async () => {
    const db = client(WEB_USER);
    await assertFails(
      db.doc("users/" + WEB_USER + "/bookshelf/bad").set({ userId: OTHER_USER }, { merge: true }),
    );
  });
});

describe("web client - custom shelves", () => {
  it("allows the web client to create and query its own custom shelf", async () => {
    const db = client(WEB_USER);
    const id = "shelf_" + WEB_USER + "_sci-fi_favorites";
    await assertSucceeds(
      db.doc("users/" + WEB_USER + "/custom_shelves/" + id).set({
        id,
        userId: WEB_USER,
        name: "Sci-Fi Favorites",
        iconEmoji: "📁",
        isDefault: false,
        createdAt: 1700000000000,
        lastUpdated: 1700000000000,
      }, { merge: true }),
    );
    await assertSucceeds(db.collection("users/" + WEB_USER + "/custom_shelves").get());
  });

  it("allows the web client to delete its own custom shelf", async () => {
    const db = client(WEB_USER);
    const id = "shelf_" + WEB_USER + "_sci-fi_favorites";
    await assertSucceeds(
      db.doc("users/" + WEB_USER + "/custom_shelves/" + id).set({ userId: WEB_USER }, { merge: true }),
    );
    await assertSucceeds(db.doc("users/" + WEB_USER + "/custom_shelves/" + id).delete());
  });

  it("refuses the web client from reading another user's custom shelves", async () => {
    const db = client(WEB_USER);
    await assertFails(db.collection("users/" + OTHER_USER + "/custom_shelves").get());
  });
});

describe("web client - reading progress", () => {
  it("allows the web client to write and read its own reading progress", async () => {
    const db = client(WEB_USER);
    await assertSucceeds(
      db.doc("users/" + WEB_USER + "/reading_progress/dune").set({
        userId: WEB_USER,
        bookId: "dune",
        lastPage: 12,
        lastChapterIndex: 12,
        totalPagesOrChapters: 40,
        progressPercent: 30,
        format: "EPUB",
        lastReadTimestamp: 1700000000000,
      }, { merge: true }),
    );
    await assertSucceeds(db.collection("users/" + WEB_USER + "/reading_progress").get());
  });

  it("refuses the web client from reading another user's reading progress", async () => {
    const db = client(WEB_USER);
    await assertFails(db.collection("users/" + OTHER_USER + "/reading_progress").get());
  });
});

describe("web client - club memberships", () => {
  it("allows the web client to list its own club memberships", async () => {
    const db = client(WEB_USER);
    await assertSucceeds(
      db.doc("users/" + WEB_USER + "/club_memberships/club_1").set({
        userId: WEB_USER,
        clubId: "club_1",
        joinedAt: 1700000000000,
      }, { merge: true }),
    );
    await assertSucceeds(db.collection("users/" + WEB_USER + "/club_memberships").get());
  });

  it("refuses the web client from listing another user's club memberships", async () => {
    const db = client(WEB_USER);
    await assertFails(db.collection("users/" + OTHER_USER + "/club_memberships").get());
  });
});

describe("web client - unauthenticated access", () => {
  it("refuses an anonymous browser from reading the profile collection", async () => {
    const db = anon();
    await assertFails(db.collection("users").get());
  });

  it("refuses an anonymous browser from writing a shelf book", async () => {
    const db = anon();
    await assertFails(
      db.doc("users/someone/bookshelf/x").set({ userId: "someone" }, { merge: true }),
    );
  });
});
