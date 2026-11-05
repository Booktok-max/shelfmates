# Local-to-remote milestone checkpoints

This document defines the required workflow for preventing valuable work from accumulating invisibly in local worktrees.

## Core rule

After every major milestone, reconcile the local worktree with the actual product GitHub repository. Keep local work visible, scoped, reviewed and accounted for. Do not wait until the end of a project to discover weeks of unpublished changes.

## When a checkpoint is required

- A feature or meaningful implementation batch is complete.
- A significant bug fix is verified.
- A cross-repository integration milestone is complete.
- A substantial refactor or migration preparation is complete.
- A PRD, acceptance, or recovery milestone is complete.
- Before switching to another substantial task or repository when meaningful local changes exist.

Tiny experiments that do not materially change the project do not require a publish checkpoint, but their changes must still be included in the next inventory.

## Read-only PowerShell inventory

Run this in a fresh PowerShell window. It fetches remote references and inspects state; it does not stage, commit, push, reset, clean, stash or modify project files.

```powershell
Set-Location "$HOME\Documents\GitHub\shelfmates"

Write-Host "`n=== REPOSITORY ==="
git rev-parse --show-toplevel

Write-Host "`n=== BRANCH / LOCAL SHA ==="
git branch --show-current
git rev-parse HEAD

Write-Host "`n=== FETCH ACTUAL PRODUCT REMOTE ==="
git fetch origin

Write-Host "`n=== UPSTREAM ==="
git rev-parse --abbrev-ref --symbolic-full-name '@{u}' 2>$null
git rev-parse '@{u}' 2>$null

Write-Host "`n=== LOCAL / REMOTE DIVERGENCE ==="
git status -sb
git status --porcelain=v1
git rev-list --left-right --count 'HEAD...@{u}' 2>$null

Write-Host "`n=== MODIFIED / DELETED FILES ==="
git diff --name-status

Write-Host "`n=== STAGED FILES ==="
git diff --cached --name-status

Write-Host "`n=== UNTRACKED FILES ==="
git ls-files --others --exclude-standard

Write-Host "`n=== DIFF SUMMARY ==="
git diff --stat
git diff --cached --stat

Write-Host "`n=== LOCAL COMMITS NOT ON UPSTREAM ==="
git log --oneline '@{u}..HEAD' 2>$null

Write-Host "`n=== WHITESPACE CHECK ==="
git diff --check
git diff --cached --check
```

If no upstream is configured, do not guess one. Identify the correct remote and feature branch first. If `git fetch` fails, report that remote comparison is unverified.

## Required checkpoint report

Every milestone report must state:

- Repository path, branch, baseline SHA and current local SHA.
- Remote name, target branch, remote SHA and fetch time/result.
- Ahead/behind counts and local-only commits.
- Modified, staged, deleted and untracked files.
- Exact files belonging to this milestone.
- Unrelated/pre-existing changes and how they were preserved.
- Tests/build commands and actual results.
- Acceptance criteria completed, partial, blocked or unverified.
- Proposed commit message, exact staged paths and push destination.
- Remaining work, blockers, ETA (or why not estimable), and next three actions.

Maintain a local-change ledger in the recovery/status document. Each entry should include repository/path, branch, baseline/current SHA, exact changed files, purpose, related remote branch/PR, tests, publication state and next action.

## Publish only after explicit approval

Do not run a commit or push merely because a milestone is finished. First show the user the exact file list and staged diff. Obtain explicit approval for that specific checkpoint.

After approval, use explicit file paths, for example:

```powershell
Set-Location "$HOME\Documents\GitHub\shelfmates"

git add -- path/to/approved-file-a path/to/approved-file-b
git diff --cached --check
git diff --cached --stat
git diff --cached

# Stop if the staged diff contains anything outside the approved scope.
git commit -m "type(scope): concise milestone description"
git push origin <approved-feature-branch>

git fetch origin
git rev-parse HEAD
git rev-parse origin/<approved-feature-branch>
git status -sb
```

Replace the example paths, message and branch with the exact values approved for the current milestone. Do not copy placeholders literally. If the local branch is behind or has diverged, stop and reconcile safely; never force-push.

## Completion rule

A milestone is remotely synchronized only when the approved commit is present on the intended remote branch and the SHA has been verified. A PR being open, a local commit existing, or tests passing does not by itself prove synchronization or end-to-end product completion.
