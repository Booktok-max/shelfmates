# Shelfmates Web — feature parity & data-source matrix

Source of truth: the Android app on `feat/shelf-design-system` (SHA `d9ef3c1`).
Every row below was read out of the actual Kotlin sources, not inferred from
screen names.

## Legend

| Symbol | Meaning |
| --- | --- |
| ✅ | Shipped in the web release, backed by real data |
| 🟡 | Shipped, but with a narrower scope than Android (documented per row) |
| ❌ | Not on the web. Reason and the requirement to enable it are stated. |

## Data-source classification

| # | Android feature | Data source | Web status | Notes / remaining requirement |
| --- | --- | --- | --- | --- |
| 1 | Google sign-in | Firebase Auth (Credential Manager → Firebase) | ✅ | Web SDK `signInWithPopup` against the same project `shelfmate-89898`. Same Firebase uid as Android. |
| 2 | Guest / anonymous sign-in | Firebase Auth (anonymous) | ✅ | `signInAnonymously`. Real anonymous uid; the same `users/{uid}` subtree the Android guest uses. |
| 3 | Role assignment (READER / AUTHOR / BOOK_CLUB_MEMBER) | Firestore `users/{uid}.role` | ✅ | Set once via a merge that never carries a role in the profile payload. `ADMIN` is not offered and the rules refuse it. |
| 4 | User profile (name, email, photo, role) | Firestore `users/{uid}` | ✅ | `buildProfileDoc` mirrors `FirebaseService.syncUserProfileToFirestore` field for field. |
| 5 | Home shelves (Currently Reading / To Read / Finished) | Firestore `users/{uid}/bookshelf` | ✅ | `category` field on the shelf book document. |
| 6 | Custom shelves | Firestore `users/{uid}/custom_shelves` | ✅ | Create / rename-less edit / delete, `shelf_{uid}_{name}` id convention. |
| 7 | Saved books | Firestore `users/{uid}/bookshelf` | ✅ | Saved from a book detail page. Writes `saved_{uid}_{googleBooksId}`. |
| 8 | Book discovery / search | External API: Google Books `volumes` | 🟡 | Direct browser call, same `googleapis.com/books/v1` endpoint the Android client uses. The expired key was replaced by a dedicated key restricted to `books.googleapis.com` and to the app's own HTTP referrers; it is sent in the `X-Goog-Api-Key` header so it never appears in a URL or a cache key, and the client still falls back to keyless if a key is rejected. Live search returns results, but **Google's Books backend is degraded provider-side**: `volumes?q=` returned `503 backendFailed` on 80–92% of requests during verification (see "Upstream risk" below). A bounded exponential-backoff retry is the only client-side mitigation available. |
| 9 | Book details | External API: Google Books `volumes/{id}` | 🟡 | Title, authors, categories, page count, rating, cover, description, ISBN-13. Same degraded-upstream caveat as #8; single-volume lookups were observed to succeed more often than `?q=` searches. |
| 10 | Save to shelf from a book page | Firestore | ✅ | Shelf selector on the detail page. |
| 11 | Move a book between shelves / remove | Firestore | ✅ | Update and delete on the shelf document. |
| 12 | Reading progress | Firestore `users/{uid}/reading_progress` | 🟡 | The web reader writes progress in the Android document shape. The *source* differs: the web reader opens a local EPUB/PDF file the user picks, because ARC manuscripts are not exposed by any shared public API today (see #20). |
| 13 | EPUB / PDF reading | Android: Supabase Storage download; web: local file | 🟡 | Browser equivalent: user-supplied EPUB/PDF is parsed in the browser (JSZip + pdf.js) and read with the same theme/size options. No shared manuscript catalogue exists, so the web cannot read the Android ARC library yet. |
| 14 | Club membership list | Firestore `users/{uid}/club_memberships` | 🟡 | The web shows which clubs the account has joined. It cannot browse or open clubs: club rows, threads and replies live only in the Android Room database (see #15). |
| 15 | Club browsing, threads, replies, member chat | **Android Room only** (`public_clubs`, `club_threads`, `thread_replies`) | ❌ | No Firestore path, no public API. Enabling it needs a shared club collection + rules, i.e. an approved backend change. Nothing is faked on the web. |
| 16 | ARC campaigns, applications, reviews | **Android Room only** (`arc_clubs`, `arc_applications`, `arc_reviews`) | ❌ | Same reason as #15. |
| 17 | Rewards / bookmarks wallet, quests, redemptions | **Android Room only** (`bookmark_transactions`, `bookmark_redemptions`, `redemption_requests`) | ❌ | Same reason as #15. |
| 18 | Notifications | **Android Room only** (`notifications`), and "push" is simulated | ❌ | There is no FCM client in the app; the rows are inserted locally. Nothing real to share. |
| 19 | Live audio rooms | **Simulated** (hard-coded participant list, in-memory room state) | ❌ | Not a real feature in the Android app either. Not presented on the web. |
| 20 | ARC manuscript downloads | Supabase Storage, `SUPABASE_*` unset in this checkout | ❌ | No bucket/anon key is configured, and the bucket would need a public-read policy plus CORS to be usable from a browser. Requires an owner-supplied Supabase configuration and a CORS policy. |
| 21 | AI chat (Gemini) | External API, key injected at build time | ❌ | A Gemini API key cannot be shipped in a public browser bundle. Needs a server-side proxy (Cloud Function or Worker) that holds the key. |
| 22 | Live voice companion (Gemini + Android STT/TTS) | External API + Android speech APIs | ❌ | Needs the same proxy as #21; the Android `SpeechRecognizer` / `TextToSpeech` pieces have no direct browser equivalent that reaches the same backend. |
| 23 | NYT bestsellers showcase | **Static list** in `NytBestsellerModels.kt`; `NYT_BOOKS_KEY` is declared but never used | ❌ | There is no NYT API integration to reuse, and the Android list is a hard-coded editorial array. Not presented as live data on the web. |
| 24 | Author dispatches / broadcasts | **Android Room only** (`broadcasts`) | ❌ | Same reason as #15. |
| 25 | Follows / follower counts | **Android Room only** (`follows`) | ❌ | Same reason as #15. |
| 26 | Book logs (reading log entries) | Firestore `users/{uid}/book_logs` (writer exists) **and** Room | 🟡 | The Firestore path and rules exist and the web could write it, but the Android UI writes to Room first and the sync path is not exercised by the app today, so the web does not create rows that Android would never read back. |
| 27 | Author tools (CSV export, promo booking, Author Pro) | **Android Room only** / simulated | ❌ | No shared data path. |
| 28 | Atomic Shelf analytics webhook | Simulated (seeded rows) | ❌ | No real webhook client exists. |
| 29 | Notifications badge / "mark all read" | Android Room | ❌ | Same reason as #18. |
| 30 | PWA install, offline shell, home-screen launch | — | ✅ | Service worker precaches the app shell only (21 entries). The one runtime cache holds public Google Books catalogue responses (`NetworkFirst`, 50 entries, 1 hour); its key is the plain request URL because the API key travels in a header, so no credential reaches the Cache Storage. Firestore and Firebase Auth responses are never cached, so private data never lands there. |

## What "shared" means precisely

The web client and the Android app talk to the **same Firebase project**
(`shelfmate-89898`) and the **same Firestore subtree** (`users/{uid}`), under
the **same security rules** (`firestore.rules`, unchanged by this work). A user
who signs in on the web with the same Google account gets the same Firebase
uid and therefore the same shelf, custom shelves, saved books, reading progress
and club memberships as on Android — and writes made on the web appear in the
Android app.

Everything in the "Android Room only" rows is **local to the device**. The web
app does not display it, does not approximate it, and does not claim it
synchronises. Getting those features onto the web needs an approved shared
backend for them, which is out of scope here.

## Verification performed

| Check | Where | Result |
| --- | --- | --- |
| Web access patterns vs. the rules | `tests/firestore/web-rules.test.js` | 20 tests, all passing against the Firestore emulator |
| Cross-user isolation | same file | Reads and writes against another user's subtree are refused |
| Role immutability / no self-ADMIN | same file + `rules.test.js` | Refused by the rules; the web never sends `role` in a profile merge |
| Android suite unaffected | `tests/firestore/rules.test.js` | 81 tests, unchanged, all passing |
| Web unit + component tests | `web/src/**/*.test.ts(x)` | 32 tests, all passing |
| Build + type check | `web/` | `tsc -b` clean, `vite build` clean |
| Android build + unit tests | `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest` | 21 tests, 0 failures |

### Live production checks (against `https://shelfmate-89898.web.app`)

| Check | Result |
| --- | --- |
| Unauthenticated visit to `/` redirects to `/signin` | Pass |
| Anonymous (guest) sign-in reaches the app | Pass |
| First-run role gate blocks the app until a role is chosen | Pass |
| Role choice persists and is not offered twice | Pass |
| Create custom shelf → persisted to Firestore → survives reload → delete | Pass (real write/read/delete against production Firestore) |
| Reading progress write from a local PDF | Pass (Firestore Write RPC 200; shape pinned by `Reader.test.tsx` and by the emulator rules tests) |
| App shell precached, navigation fallback present | Pass (21 precache entries incl. `/index.html`; re-verified on a fresh install) |
| No Firestore / Auth data in Cache Storage | Pass (cache audit found zero matches) |
| No credential in any cache key | Pass (13/13 live Books requests carried the key in `X-Goog-Api-Key`; zero `?key=` in URLs or cache keys) |
| Google Books search, keyed | Pass (live: 20 results of 325) — but see the upstream risk below |
| Google Books rejected-key fallback | Pass (unit-tested; degrades to keyless and explains the failure) |
| Custom domain `shelfmates.atomic-shelf.com` | **Blocked on DNS write access** — see the deployment notes in `README.md` |

## Upstream risk: Google Books API degradation

Google's `books/v1/volumes?q=` endpoint is currently unreliable for every
caller, independent of project, key, and region. Measured from this machine
against project `shelfmate-89898` with a valid, referrer-restricted key:

| Sample | HTTP 200 |
| --- | --- |
| 12 sequential requests, ~1.2 s apart | 1 / 12 |
| 15 requests, app's parameter set | 5 / 15 |
| 15 requests, bare `q=` | 3 / 15 |
| 8 requests, `X-Goog-Api-Key` + `Origin` | 1 / 8 |

Failures are HTTP 503 `backendFailed` ("Service temporarily unavailable"), not
quota or auth errors. Public reports describe the same behaviour since April
2026 and a marked worsening in October 2026. Single-volume lookups
(`volumes/{id}`) are materially more reliable than `?q=` searches.

Mitigation shipped: up to 4 attempts per request with exponential backoff
(400 / 900 / 1800 ms plus jitter), retrying only 5xx and network errors. 429 is
*not* retried because it is a daily quota. The UI therefore either returns
results or states plainly that Google Books is temporarily unavailable; it never
shows stale or fabricated results. Replacing the provider is a product decision
outside this change.

## Known defects found during verification (fixed)

- **Stale PWA shell after deploys.** Two causes. First, `firebase.json` set
  `no-cache` on `/index.html` but Firebase matches header sources against the
  *request* path, so navigation to `/` still got `Cache-Control: max-age=3600`
  and could serve an hour-old shell pointing at deleted asset hashes; explicit
  `/`, `**/*.html` and `/sw.js` no-cache rules fixed that. Second, the injected
  registration shim only called `register()`, so a tab that stayed open across a
  deploy kept the old document after the new worker took control and its next
  lazy chunk request hit the SPA rewrite (HTML instead of JavaScript).
  Registration now lives in `src/pwa.ts` and reloads once on
  `controllerchange`; first-time visitors are not reloaded, and a long-lived tab
  re-checks for updates when it is foregrounded.
- **The registration shim is no longer generated.** `injectRegister` is off and
  `web/src/pwa.ts` owns registration, so there is no unhashed `registerSW.js` for
  the one-year `immutable` rule to pin. That rule remains scoped to
  `**/assets/**`.
- **An expired Google Books key broke search outright** with a raw
  `Google Books error 400`. The client now degrades to the keyless quota and
  explains the failure instead of surfacing a status code.
- **The API key was persisted in Cache Storage.** The Google Books runtime
  cache keyed entries by the full request URL, including `?key=`, so a public
  client credential was written to disk. The key is now sent in the
  `X-Goog-Api-Key` header (allowed by Google's CORS preflight), leaving request
  URLs and cache keys credential-free.
- **Transient 503s surfaced as a raw status code.** `Google Books error 503`
  was shown to users. Transient upstream failures are now retried with
  exponential backoff and then reported as a temporary outage.
