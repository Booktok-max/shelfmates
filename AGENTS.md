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


## Local-work visibility and continuous change control

A remote branch is not a substitute for inspecting the user's local checkout. Before every task and after every meaningful milestone, capture and report:
- all relevant local repository paths and worktrees;
- branch, HEAD SHA, upstream and remote-tracking divergence (state whether remotes were fetched; do not fetch without permission if network/auth implications are unclear);
- porcelain status, staged/unstaged/untracked file lists, and diff statistics;
- stashes, linked worktrees and other branches that may contain task work;
- whether local changes are represented on a remote branch/PR, and exactly which changes remain local-only.

Local buildup policy:
1. Do not allow a feature milestone to accumulate substantial unreviewed local-only changes across multiple repositories without a written inventory.
2. Keep unrelated changes separate; never bundle them into a PR merely to make the tree clean.
3. At each milestone, reconcile the task's local changes to a named remote feature branch and reviewable PR only after explicit authorization to publish. Until then, label them LOCAL-ONLY and preserve them.
4. Maintain a local-change ledger in the recovery report: repository/path, branch, baseline SHA, current SHA, exact changed/untracked files, purpose, related PR/remote branch, tests, and next action.
5. Before switching tasks or repositories, record the current ledger and unresolved changes. Do not leave the status implicit in chat history.
6. At the start of each new work session, compare the local ledger with current GitHub state and call out drift before editing.
7. This is a checkpoint-based control, not background surveillance. If the local machine is not connected/visible, explicitly request a fresh read-only inventory rather than claiming to monitor it.

Recommended cadence: one baseline inventory now; a short status capture before and after each milestone; a full three-repository reconciliation before release. Avoid repeatedly dumping full diffs unless a specific change needs review; file lists and diff statistics are the default, with targeted diff inspection for risk or acceptance.


## Mandatory milestone checkpoint and remote synchronization

After every major implementation, bug-fix, integration, refactor, or documentation milestone, stop before beginning another substantial task.

1. Inspect the full local Git state and fetch the actual product remote.
2. Report repository, path, branch, local HEAD SHA, upstream branch/SHA, remote SHA, ahead/behind counts, modified/staged/deleted/untracked files, local-only commits, stashes/worktrees where relevant, and diff statistics.
3. Identify the exact files belonging to this milestone. Separate pre-existing and unrelated changes; preserve them.
4. Run the relevant milestone tests and record exact commands/results. Do not confuse passing unit tests with end-to-end acceptance.
5. Prepare a scoped checkpoint: proposed commit message, exact file list, target feature branch, and push destination.
6. STOP for explicit user approval before committing or pushing. A general request to implement a feature is not approval to publish unrelated or unreviewed files.
7. After approval, stage only the approved explicit paths (never use `git add .` or `git add -A` as a shortcut), inspect the staged diff, commit, and push to the approved feature branch.
8. Fetch again and verify the remote branch SHA equals the pushed local commit SHA. Report the commit SHA and PR URL.
9. Update `docs/PROJECT-STATUS.md` or the repository's designated status/recovery document with the checkpoint, remaining work, blockers and next three actions.

If publishing is blocked, preserve the local work and report the exact failure. Do not silently accumulate another milestone on top of an unpublished one.

Never force-push, reset, clean, stash, overwrite unrelated work, push directly to the default branch, merge, or deploy without explicit approval.

A milestone is not remotely synchronized until the remote SHA is verified. GitHub synchronization is not proof that product acceptance criteria have passed.

This is checkpoint-based oversight, not background monitoring. If the local machine/worktree is not accessible, explicitly request a fresh read-only local inventory; never claim to have inspected local state from GitHub alone.
