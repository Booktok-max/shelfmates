# Shelfmates Web

The browser edition of Shelfmates, built as a **Progressive Web App**. It is
a real client of the same backend as the Android app (Firebase project
`shelfmate-89898`), not a mock-up: a user who signs in with the same Google
account sees the same shelves, saved books, reading progress and club
memberships, and writes made here show up in the Android app.

- Live: <https://shelfmate-89898.web.app>
- Custom domain (attached, awaiting DNS): <https://shelfmates.atomic-shelf.com>
- Hosting: Firebase Hosting, site `shelfmate-89898`
- Feature parity and data sources: [FEATURE-PARITY.md](./FEATURE-PARITY.md)

## Stack

React 19 + TypeScript + Vite, Firebase Web SDK (Auth + Firestore), React
Router, `vite-plugin-pwa` (Workbox service worker), Vitest + Testing Library.

The web app lives entirely in `web/` and shares **no build tooling** with the
Android project. The Gradle build is untouched: `settings.gradle.kts` still
contains only `:app`.

## Local development

```bash
cd web
npm install
cp .env.example .env.local     # then fill in the values
npm run dev                    # http://localhost:5173
```

### Configuration

`.env.local` is gitignored. `.env.example` is committed with placeholders.

| Variable | Required | What it is |
| --- | --- | --- |
| `VITE_FIREBASE_API_KEY` | yes | Firebase Web API key (a public client identifier, not a secret). |
| `VITE_FIREBASE_AUTH_DOMAIN` | yes | `shelfmate-89898.firebaseapp.com` |
| `VITE_FIREBASE_PROJECT_ID` | yes | `shelfmate-89898` |
| `VITE_FIREBASE_STORAGE_BUCKET` | yes | `shelfmate-89898.firebasestorage.app` |
| `VITE_FIREBASE_MESSAGING_SENDER_ID` | yes | `686595151953` |
| `VITE_FIREBASE_APP_ID` | yes | The Web app id (`1:686595151953:web:…`) |
| `VITE_FIREBASE_MEASUREMENT_ID` | no | Analytics id; omit to disable analytics. |
| `VITE_GOOGLE_BOOKS_KEY` | no | Google Books API key. Unset ⇒ the app sends unauthenticated requests (lower quota), exactly like an Android build without a key. If a configured key is rejected (expired/revoked/referrer mismatch) the client automatically retries keyless rather than breaking search. The web build uses a dedicated key restricted to the `books.googleapis.com` API and to the app's own HTTP referrers. It is sent in the `X-Goog-Api-Key` header, so it never appears in a request URL or a service-worker cache key. **The key in the repo `.env` is still the expired one** — that file feeds Android builds, so it was deliberately left untouched. |

**No privileged secret is ever placed in this bundle.** There is no Admin SDK,
no service-role key, and no database credential in the web app. The Gemini API
key used by the Android app is deliberately **not** reproduced here, because a
browser bundle is not a safe place for it — see FEATURE-PARITY row 21.

## Scripts

| Command | What it does |
| --- | --- |
| `npm run dev` | Vite dev server with HMR |
| `npm run build` | `tsc -b` then `vite build` → `dist/` |
| `npm run typecheck` | Type check only |
| `npm test` | Vitest (unit + component) |
| `npm run lint` | oxlint |
| `npm run preview` | Serve the production build locally |

## Tests

- **Web unit/component tests** — `web/src/**/*.test.ts(x)`. Cover the
  Firestore document shapes (field-for-field identical to the Android
  `FirestoreShelfBook` / `FirestoreCustomShelf` / `syncReadingProgressToFirestore`
  payloads), the sign-in and role-selection flow, the route/role gate, and the
  book-search success/error/empty states.
- **Firestore rules tests** — `tests/firestore/`, run against the Firestore
  emulator. `web-rules.test.js` reproduces the exact access patterns the web
  client uses and asserts cross-user isolation and refusal of unauthorised
  writes. `rules.test.js` is the pre-existing Android suite, unchanged.

```bash
cd tests/firestore && npm test     # 101 tests
cd web && npm test                  # 32 tests
```

## Deployment

The site is deployed from the **web application root** and never interferes
with the Android pipeline:

```bash
cd web && npm ci && npm run build
cd .. && firebase deploy --only hosting --project shelfmate-89898
```

`firebase.json` at the repository root holds both the `firestore.rules`
reference and the `hosting` block whose `public` is `web/dist`. The Android CI
workflow never runs `firebase deploy`, and the web CI workflow never runs
Gradle.

### Hosting details

- SPA rewrites send every path to `/index.html`.
- **Cache headers are deliberate and load-bearing for PWA updates.** Firebase
  matches `headers.source` against the *request* path, so `no-cache` must be
  declared for `/` as well as `/index.html`; a rule for `/index.html` alone
  leaves the site root with Firebase's default `max-age=3600`, which serves an
  hour-old shell that points at asset hashes the new release has already
  deleted. `**/*.html`, `/sw.js` and `/registerSW.js` are `no-cache` for the
  same reason.
- The `immutable` one-year rule is scoped to `**/assets/**`, which only contains
  Vite's content-hashed output. A broader `**/*.js` glob would also match the
  unhashed `sw.js` and `registerSW.js` and freeze them for a year.
- The service worker precaches the **app shell only** (JS/CSS/HTML/icons).
  Firestore and Firebase Auth responses are never written to the Cache Storage,
  so a signed-out shared device cannot expose another user's data through the
  offline cache. The only runtime cache holds **public** Google Books catalogue
  responses (`NetworkFirst`, 50 entries, 1 hour).
- **The Google Books key is sent as `X-Goog-Api-Key`, not `?key=`.** Google
  allows that header in its CORS preflight response. Keeping it out of the URL
  means it cannot leak into browser history, referrer headers, access logs, or
  a service-worker cache key, and the runtime cache is keyed by the plain URL.
- **Google Books is retried, not trusted.** The upstream `volumes?q=` endpoint
  is currently degraded provider-side (80–92% HTTP 503 `backendFailed` during
  verification). Requests get up to four attempts with exponential backoff and
  jitter; only 5xx and network errors are retried (a 429 is a daily quota). If
  every attempt fails the UI says the service is temporarily unavailable — it
  never falls back to stale or invented results.

### Custom domain

`shelfmates.atomic-shelf.com` is attached to the Firebase Hosting site
`shelfmate-89898`.

Firebase Hosting serves the apex domain of a site automatically; a subdomain
is served via a DNS `CNAME` to `shelfmate-89898.web.app`. The record is
managed in Cloudflare (the zone `atomic-shelf.com` uses Cloudflare nameservers
`anton.ns.cloudflare.com` / `gene.ns.cloudflare.com`) and must be a single
`CNAME` for the `shelfmates` label only, **unproxied (DNS-only)** — Cloudflare's
orange cloud rewrites the target to Cloudflare edge IPs, which breaks Firebase's
host/ownership check and certificate issuance. No other record on the zone is
touched, and the zone's nameservers are left unchanged.

Required records (current values are reported by the Firebase Hosting API):

| Type | Name | Value | Proxy |
| --- | --- | --- | --- |
| `CNAME` | `shelfmates` | `shelfmate-89898.web.app` | DNS only |
| `TXT` | `_acme-challenge.shelfmates` | *ACME token from the API* | n/a |

The TXT token rotates, so re-read it from
`projects/686595151953/sites/shelfmate-89898/customDomains/shelfmates.atomic-shelf.com`
before adding it.

The domain also has to be listed in Firebase Authentication's authorised
domains, otherwise Google sign-in refuses to complete the redirect. It is
already present. **No Google Cloud OAuth-client change is required:** the
Firebase Web SDK signs in through the `shelfmate-89898.firebaseapp.com/__/auth/handler`
redirect, where Google validates the *redirect URI* and Firebase validates the
requesting origin against its own authorised-domains list. This was verified
empirically against a preview-channel hostname that was never added to the
OAuth client's JavaScript origins and which still reached Google's sign-in
normally.

**Status: blocked on DNS write access.** The Firebase custom domain exists but
reports `ownershipState: OWNERSHIP_MISSING`, `hostState: HOST_UNHOSTED` and a
temporary certificate that never finishes validating, because neither record
exists in public DNS. The deployment credentials available in this environment
hold Cloudflare `zone:read` only; the DNS API returns HTTP 403 for both listing
and creating records, so the change cannot be applied here. Adding the two
records above in the Cloudflare dashboard (or with a token carrying
`Zone:DNS:Edit` for `atomic-shelf.com`) unblocks Firebase's automatic ownership
check and certificate issuance — no further Firebase-side action is needed.

## What is intentionally not on the web

See [FEATURE-PARITY.md](./FEATURE-PARITY.md) for the full matrix. In short:
anything the Android app keeps in its **local Room database** (club content
and threads, ARC campaigns, rewards, notifications, broadcasts, follows) has
no shared backend, so it is not on the web. The Gemini chat and live-voice
features need a server-side proxy before a browser can use them safely. The web
app never displays placeholder or fabricated data in place of these — where a
feature is unavailable the UI says so.
