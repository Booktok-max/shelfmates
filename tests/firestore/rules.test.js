/**
 * Emulator tests for ../../firestore.rules
 *
 * These exercise the access patterns that actually exist in
 * app/src/main/java/com/shelfmates/data/remote/FirebaseService.kt. The two that
 * matter most are:
 *
 *   - syncUserProfileToFirestore() merges a payload with NO `role` key.
 *   - saveUserRole()               merges a payload with ONLY `role`.
 *
 * Both must be allowed for the app to function, while a self-assigned ADMIN
 * must be refused. Those three cases are what the role rules exist to balance.
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

const ALICE = "alice_uid";
const MALLORY = "mallory_uid";

let testEnv;

function rules() {
  return readFileSync(path.join(__dirname, "..", "..", "firestore.rules"), "utf8");
}

/** A client authenticated as `uid` (null = signed out). */
function client(uid) {
  return testEnv.authenticatedContext(uid).firestore();
}

function anon() {
  return testEnv.unauthenticatedContext().firestore();
}

/**
 * Runs `fn` against the backend with rules bypassed — stands in for "the
 * server" that grants a staff role via the Admin SDK.
 *
 * withSecurityRulesDisabled() resolves to void, so the Firestore handle must be
 * used inside the callback rather than returned from it.
 */
async function asAdmin(fn) {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await fn(context.firestore());
  });
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

describe("users/{uid} — ownership", () => {
  it("allows a user to write their own profile", async () => {
    // Mirrors syncUserProfileToFirestore(): merge, no `role` key.
    await assertSucceeds(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set(
          { uid: ALICE, email: "alice@example.com", lastActive: 1 },
          { merge: true }
        )
    );
  });

  it("allows a user to read their own document", async () => {
    await assertSucceeds(client(ALICE).collection("users").doc(ALICE).get());
  });

  it("refuses reading another user's document", async () => {
    await assertFails(client(MALLORY).collection("users").doc(ALICE).get());
  });

  it("refuses writing to another user's document", async () => {
    await assertFails(
      client(MALLORY)
        .collection("users")
        .doc(ALICE)
        .set({ email: "pwned@example.com" }, { merge: true })
    );
  });

  it("refuses all access when signed out", async () => {
    await assertFails(anon().collection("users").doc(ALICE).get());
    await assertFails(
      anon().collection("users").doc(ALICE).set({ email: "x@example.com" })
    );
  });

  it("refuses deleting a user document", async () => {
    await assertSucceeds(
      client(ALICE).collection("users").doc(ALICE).set({}, { merge: true })
    );
    await assertFails(client(ALICE).collection("users").doc(ALICE).delete());
  });
});

describe("users/{uid} — role assignment", () => {
  it("allows assigning a self-service role on first write", async () => {
    // Mirrors saveUserRole() on an account with no role yet.
    await assertSucceeds(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ role: "AUTHOR" }, { merge: true })
    );
  });

  it("allows each self-service role", async () => {
    // Each role gets a fresh account: once a role is written it is immutable,
    // so reusing one document would (correctly) fail on the second iteration.
    const accounts = ["uid_reader", "uid_author", "uid_club"];
    const roles = ["READER", "AUTHOR", "BOOK_CLUB_MEMBER"];

    for (let i = 0; i < roles.length; i++) {
      await assertSucceeds(
        client(accounts[i])
          .collection("users")
          .doc(accounts[i])
          .set({ role: roles[i] }, { merge: true })
      );
    }
  });

  it("REFUSES self-assigning ADMIN", async () => {
    // The escalation this whole file exists to prevent.
    await assertFails(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ role: "ADMIN" }, { merge: true })
    );
  });

  it("REFUSES creating a document with role ADMIN", async () => {
    await assertFails(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ role: "ADMIN", email: "alice@example.com" })
    );
  });

  it("REFUSES escalating an existing role", async () => {
    await assertSucceeds(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ role: "READER" }, { merge: true })
    );
    // Same value again is not an escalation.
    await assertSucceeds(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ role: "READER" }, { merge: true })
    );
    // Changing it is refused.
    await assertFails(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ role: "AUTHOR" }, { merge: true })
    );
    await assertFails(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ role: "ADMIN" }, { merge: true })
    );
  });

  it("REFUSES a client overwriting a server-granted ADMIN role", async () => {
    // Server grants ADMIN via the Admin SDK (rules bypassed).
    await asAdmin((db) => db.collection("users").doc(ALICE).set(
      { uid: ALICE, role: "ADMIN" },
      { merge: true }
    ));

    // The holder cannot downgrade it either — role is immutable from the client.
    await assertFails(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ role: "READER" }, { merge: true })
    );
  });

  it("allows a profile merge after a role is set", async () => {
    // Regression guard: once a role exists, the normal profile sync (which
    // carries no `role` key) must still succeed. A rule that rejected this
    // would break lastActive/photoUrl updates for every signed-in user.
    await assertSucceeds(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ role: "READER" }, { merge: true })
    );
    await assertSucceeds(
      client(ALICE)
        .collection("users")
        .doc(ALICE)
        .set({ displayName: "Alice", lastActive: 999 }, { merge: true })
    );
  });
});

describe("users/{uid} subcollections", () => {
  const subcollections = [
    ["reading_progress", "book_1"],
    ["book_logs", "log_1"],
    ["bookshelf", "gbook_1"],
    ["custom_shelves", "shelf_1"],
  ];

  for (const [name, docId] of subcollections) {
    describe(name, () => {
      it("allows the owner to write", async () => {
        await assertSucceeds(
          client(ALICE)
            .collection("users")
            .doc(ALICE)
            .collection(name)
            .doc(docId)
            .set({ userId: ALICE, value: 1 }, { merge: true })
        );
      });

      it("allows the owner to read", async () => {
        await asAdmin((db) => db
          .collection("users")
          .doc(ALICE)
          .collection(name)
          .doc(docId)
          .set({ value: 1 }));
        await assertSucceeds(
          client(ALICE)
            .collection("users")
            .doc(ALICE)
            .collection(name)
            .doc(docId)
            .get()
        );
      });

      it("allows the owner to query the collection", async () => {
        // fetchAllFirestoreBooks() / fetchAllFirestoreShelves() run a bare get().
        await assertSucceeds(
          client(ALICE)
            .collection("users")
            .doc(ALICE)
            .collection(name)
            .get()
        );
      });

      it("allows the owner to delete", async () => {
        await asAdmin((db) => db
          .collection("users")
          .doc(ALICE)
          .collection(name)
          .doc(docId)
          .set({ value: 1 }));
        await assertSucceeds(
          client(ALICE)
            .collection("users")
            .doc(ALICE)
            .collection(name)
            .doc(docId)
            .delete()
        );
      });

      it("refuses another user reading", async () => {
        await asAdmin((db) => db
          .collection("users")
          .doc(ALICE)
          .collection(name)
          .doc(docId)
          .set({ value: 1 }));
        await assertFails(
          client(MALLORY)
            .collection("users")
            .doc(ALICE)
            .collection(name)
            .doc(docId)
            .get()
        );
      });

      it("refuses another user writing", async () => {
        await assertFails(
          client(MALLORY)
            .collection("users")
            .doc(ALICE)
            .collection(name)
            .doc(docId)
            .set({ value: 999 }, { merge: true })
        );
      });

      it("refuses a signed-out write", async () => {
        await assertFails(
          anon()
            .collection("users")
            .doc(ALICE)
            .collection(name)
            .doc(docId)
            .set({ value: 1 })
        );
      });

      it("ignores a spoofed userId field in the payload", async () => {
        // Ownership comes from the path, not the payload. Mallory may write
        // into her OWN subtree, and the forged userId there is inert.
        await assertSucceeds(
          client(MALLORY)
            .collection("users")
            .doc(MALLORY)
            .collection(name)
            .doc(docId)
            .set({ userId: ALICE, value: 1 }, { merge: true })
        );
      });
    });
  }
});

describe("unmatched paths", () => {
  it("denies reads from an unknown top-level collection", async () => {
    await assertFails(anon().collection("some_other_collection").doc("x").get());
    await assertFails(client(ALICE).collection("some_other_collection").doc("x").get());
  });

  it("denies writing an unknown top-level collection", async () => {
    await assertFails(
      client(ALICE).collection("some_other_collection").doc("x").set({ a: 1 })
    );
  });
});