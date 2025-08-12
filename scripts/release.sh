#!/usr/bin/env bash
set -euo pipefail

# Simple local release helper for Project KARL
# Usage:
#   scripts/release.sh 0.1.1
# or bump patch automatically:
#   scripts/release.sh --bump-patch

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

if [[ ${1:-} == "--help" || ${1:-} == "-h" ]]; then
  echo "Usage: $0 <version>|--bump-patch"
  exit 0
fi

current_version=$(cat VERSION 2>/dev/null || echo "1.0.0")
new_version=""

if [[ ${1:-} == "--bump-patch" ]]; then
  IFS='.' read -r MAJ MIN PATCH <<<"$current_version"
  PATCH=$((PATCH+1))
  new_version="$MAJ.$MIN.$PATCH"
else
  if [[ $# -lt 1 ]]; then
    echo "Error: provide a version or --bump-patch" >&2
    exit 1
  fi
  new_version="$1"
fi

echo "$new_version" > VERSION

if [[ $new_version =~ ^0 ]]; then
  echo "Error: Native distribution requires MAJOR version > 0 (got $new_version)" >&2
  exit 1
fi

echo "==> Building release version $new_version"
./gradlew clean prepareRelease --no-daemon

echo "==> Creating git tag v$new_version"
git add VERSION
if ! git diff --cached --quiet; then
  git commit -m "chore(release): v$new_version"
fi
git tag -a "v$new_version" -m "Project KARL $new_version"

echo "==> Push with: git push origin main --tags"
