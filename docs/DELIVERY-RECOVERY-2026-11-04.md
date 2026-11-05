# Shelfmates — delivery recovery register

**Snapshot date:** 2026-11-04  
**Repository:** `Booktok-max/shelfmates`  
**Default branch:** `main`  
**Observed main SHA:** `7f66f968b7fe433311aab9638c944b5523e45c25`  
**Status:** Recovery inventory in progress; not a release declaration.

## Current verified findings

- The current remote `README.md` is still the generic “Built with AI Studio” starter text; it is not meaningful product documentation.
- No `AGENTS.md`, product PRD, acceptance document or PR template was found on main during this audit.
- The historical shared-catalogue baseline (2026-10-21) records Firebase Auth + Firestore for user-owned bookshelves, custom shelves and reading progress. Treat this as historical until checked against current code.
- The current main SHA observed is `7f66f968b7fe433311aab9638c944b5523e45c25`.

## Remote branch drift

Compared with `main`:
- `fix/milky-catalogue-recommendation-save-20261103`: **5 commits ahead, 3 behind**, touching `web/src/App.tsx`, `web/src/data/serialize.ts`, and `web/src/data/serialize.test.ts`.
- `feat/catalogue-save-handoff-20261103`: **0 commits ahead, 3 behind**; no unique file diff against current main.
- `feat/save-to-shelf-handoff` and `feat/shelf-design-system` also exist remotely; their current relationship to local dirty work must be reconciled.

Historical local inspection recorded a dirty `feat/shelf-design-system` checkout with changes in `web/src/api/catalogue.ts`, its test, `BookDetail.tsx` and `Discover.tsx`. That is not a live observation of the current machine; re-check local status before any sync or overwrite.

## Save to Shelf — mandatory unfinished acceptance

**Current status: PARTIAL / end-to-end UNVERIFIED.**

The known stopping point was shelf naming/selection initiated from Milky, not verified completion of the saved-book journey.

Required:
1. Authenticate the intended user.
2. Resolve the Milky handoff to the correct book; handle ambiguous title/author matches.
3. Display existing shelves and allow creating/naming a shelf.
4. Persist the book to the selected shelf under the correct Firebase user identity.
5. Confirm success only after Firestore write completion.
6. Re-open the shelf after refresh and later sign-in; confirm the book remains.
7. Prevent cross-user leakage and unintended duplicates.
8. Handle cancellation, expired auth, network errors and unsupported records honestly.

## Regression / drift register

| ID | Finding | Status | Required recovery |
|---|---|---|---|
| SH-01 | Product README remains generic starter copy | MISSING | Replace with accurate product/setup/architecture documentation after code audit |
| SH-02 | No permanent agent instructions or PRD/acceptance process on main | MISSING | Add and maintain repository docs |
| SH-03 | Save-to-Shelf work is split across divergent and stale branches | BLOCKED pending reconciliation | Compare commits, inspect tests and preserve useful work |
| SH-04 | Shelf naming was reached, but persistence and retrieval are not proven | PARTIAL | Complete end-to-end save, refresh and revisit tests |
| SH-05 | Historical architecture/persistence baseline may have drifted | IMPLEMENTED — UNVERIFIED | Re-audit Firebase auth, Firestore rules, shelf schema and current save path |
| SH-06 | No demonstrated cross-app contract acceptance with Milky | MISSING | Agree payload, route, auth continuation, matching and response semantics |
| SH-07 | Existing user shelves/books/progress preservation lacks current release evidence | IMPLEMENTED — UNVERIFIED | Regression-test existing accounts and records |

## Required next work

1. Reconcile the divergent recommendation-save branch and local `feat/shelf-design-system` changes.
2. Complete the save journey through persisted Firestore record and retrieval after refresh/revisit.
3. Add integration tests for identity isolation, duplicate handling, auth continuation and Milky contract compatibility.
