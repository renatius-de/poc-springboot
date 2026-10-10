#!/usr/bin/env bash
# Lists (default) or deletes branches merged into the remote default branch.
# Usage: scripts/git-cleanup-branches.sh [--apply] [--remote]
#   --apply  actually delete local merged branches (git branch -d)
#   --remote also include merged origin/* branches (requires --apply to delete)
set -euo pipefail

APPLY=false
REMOTE=false
for arg in "$@"; do
  case "$arg" in
    --apply) APPLY=true ;;
    --remote) REMOTE=true ;;
    *) echo "Unknown option: $arg" >&2; exit 2 ;;
  esac
done

git fetch --all --prune

DEFAULT=$(git symbolic-ref --short refs/remotes/origin/HEAD 2>/dev/null | sed 's@^origin/@@' || true)
DEFAULT=${DEFAULT:-main}
PROTECTED="$DEFAULT|master|main|develop"

local_branches() {
  git branch --merged "origin/$DEFAULT" | grep -vE "^\*|^\+|^\s*($PROTECTED)$" | sed 's@^\s*@@' || true
}
remote_branches() {
  git branch -r --merged "origin/$DEFAULT" | grep -vE "origin/(HEAD|$PROTECTED)\b|->" | sed 's@^\s*origin/@@' || true
}

echo "Default branch: $DEFAULT"
echo "Merged local branches:"
local_branches | sed 's@^@  @'
if $REMOTE; then
  echo "Merged remote branches:"
  remote_branches | sed 's@^@  @'
fi

if ! $APPLY; then
  echo "Dry run. Re-run with --apply to delete."
  exit 0
fi

local_branches | xargs -r git branch -d
if $REMOTE; then
  remote_branches | xargs -r git push origin --delete
  git fetch --prune
fi
git remote prune origin
git gc --auto
