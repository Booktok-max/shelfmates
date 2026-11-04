# Agent and contributor operating rules

Before doing any work:
1. Read this file, the repository PRD/product requirements, `docs/DELIVERY-RECOVERY-2026-11-04.md`, and any relevant existing status/decision documents.
2. Confirm repository, branch, HEAD SHA, upstream/default branch and current dirty/staged/untracked state. If local state is unavailable, say so; never infer it from GitHub.
3. Inspect existing branches, open PRs, prior implementations and tests before creating a replacement.
4. Map the task to explicit PRD requirements and acceptance criteria. If no criterion exists, propose one before implementation.
5. Preserve unrelated and user-owned work. Never reset, clean, stash, force-push, overwrite or silently discard changes.

Delivery rules:
- Work toward the complete user-facing journey, not just the immediate technical defect.
- Track overall milestone progress as well as task progress. Report scope drift as soon as it appears.
- Status values must be exactly: DONE — VERIFIED; IMPLEMENTED — UNVERIFIED; PARTIAL; MISSING; BLOCKED; PARKED.
- A passing unit test, API response, rendered component or database row does not prove a user journey is complete.
- Do focused tests while diagnosing; run integration tests after coherent feature groups; reserve full regression for milestone/release acceptance. Do not let repeated micro-tests replace delivery.
- When a problem is understood, stop investigating and implement or record a specific blocker. Continue independent work where safe.
- Update the delivery recovery document at meaningful milestones, including evidence, changed files, tests, remaining gaps and next three actions.
- Keep PRDs, acceptance criteria and implementation status aligned. Record decisions and scope changes.

Cross-repository work:
- Identify the authoritative owner of each data entity and the exact API/URL/payload/authentication contract.
- Verify both sides of every integration. A producer-side link or consumer-side endpoint alone is not an end-to-end integration.
- Do not declare cross-repository features complete until the user journey succeeds from source app through destination app and persists after refresh/revisit.

Safety and permissions:
- No production writes, database migrations, credential changes, external sends, DNS changes, merges or deployments without explicit user approval.
- Do not restore or invent database credentials. Respect deliberately disconnected database environments.
- Do not commit or push local work unless authorized. For remote documentation or code changes, use a reviewable feature branch and PR; do not write directly to the default branch.
- Never claim a task, feature or release is complete without reproducible evidence.

Every progress report must include:
- overall completion %, remaining %, and ETA (or why it cannot yet be estimated);
- PRD/scope coverage % and drift;
- repository/path/branch/baseline SHA/current SHA;
- dirty, staged and untracked state (or explicitly unavailable);
- exact files changed and preservation of unrelated files;
- tests run and actual results;
- acceptance criteria passed/failed/unverified;
- blockers and production-gated items;
- next three actions.
