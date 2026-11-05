# Shelfmates — product requirements and closeout scope

**Status:** Recovery specification; reconcile with the existing implementation before changing behavior.  
**Product:** Personal book discovery and reading shelves.

## Product objective

Shelfmates lets a reader discover books, organize a personal library into shelves, and return to saved books. The reader's authenticated account is the authority for personal shelves and reading progress.

## Known architecture (historical evidence; re-verify before implementation)

The 2026-10-21 shared-catalogue baseline identifies:
- Web application: Vite + React SPA.
- Authentication and user data: Firebase Authentication + Firestore.
- Existing user-owned collections: `users/{uid}/bookshelf`, `custom_shelves`, and `reading_progress`.
- Existing save/persistence code: `web/src/data/shelves.ts` and `web/src/data/serialize.ts`.
- Firebase project recorded in prior inventory: `shelfmate-89898`.

Do not replace or bypass existing user-data persistence without a reviewed migration plan. Reconfirm this architecture against current main before relying on it.

## Core acceptance criteria

### Identity and access
- [ ] Signed-in user identity is stable and used for every personal shelf operation.
- [ ] Signed-out users receive a clear sign-in path and return to the intended book after authentication.
- [ ] One user's shelves and saved books are never visible to another user.

### Shelves
- [ ] User can view existing shelves.
- [ ] User can create and name a shelf.
- [ ] Shelf names are validated and duplicate names handled clearly.
- [ ] User can rename or remove a shelf according to the existing product rules.
- [ ] Shelf creation and book saving have honest success/failure states.

### Save a book
- [ ] A book handed off from Milky resolves to the correct title/author/edition.
- [ ] Ambiguous matches are presented for user confirmation; no silent wrong match.
- [ ] User can select an existing shelf or create/name one.
- [ ] The book is persisted under the authenticated user's account.
- [ ] The saved book is visible after refresh, sign-out/sign-in and later revisit.
- [ ] Repeated saves do not create unintended duplicates.
- [ ] A failed write never produces a success message.

### Discovery and reading
- [ ] Discovery cards have meaningful destinations and accurate metadata.
- [ ] Cover failures use deliberate fallbacks.
- [ ] Local reading and reading progress continue to work for existing user content.
- [ ] Existing shelves, books and progress are preserved through changes.

## Cross-repository handoff contract

Milky is the producer; Shelfmates is a receiving application for the supported adult/general book audience. Shelfmates must document the exact route, query/body fields, contract version, authentication continuation and response behavior it accepts.

The end-to-end flow is not complete when the shelf-name UI appears. Completion requires persisted book retrieval after refresh/revisit.

## Status discipline

Every criterion must be labelled DONE — VERIFIED, IMPLEMENTED — UNVERIFIED, PARTIAL, MISSING, BLOCKED or PARKED, with evidence. Do not mark this PRD complete based only on unit tests.
