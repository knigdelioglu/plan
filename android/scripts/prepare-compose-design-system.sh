#!/usr/bin/env bash
# Prepare an isolated local checkout of the same Compose revision used by GitHub Actions.
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
pin_file="$repo_root/.github/compose-design-system.ref"
revision="$(tr -d '\r\n' < "$pin_file")"
if [[ ! "$revision" =~ ^[a-f0-9]{40}$ ]]; then
  echo "Invalid Compose revision: $pin_file" >&2
  exit 1
fi

target="$repo_root/android/.compose-deps/compose-design-system"
mkdir -p "$(dirname "$target")"

if [[ -e "$target" && ! -d "$target/.git" ]]; then
  echo "Refusing to overwrite non-Git directory: $target" >&2
  exit 1
fi
if [[ ! -d "$target/.git" ]]; then
  git clone --filter=blob:none --no-checkout https://github.com/knigdelioglu/compose-design-system.git "$target"
fi
if [[ -n "$(git -C "$target" status --porcelain)" ]]; then
  echo "Refusing to replace local edits in $target. Commit or move them first." >&2
  exit 1
fi

git -C "$target" fetch --quiet origin main
git -C "$target" cat-file -e "$revision^{commit}" || {
  echo "Pinned revision is unavailable in $target: $revision" >&2
  exit 1
}
git -C "$target" checkout --quiet --detach "$revision"
actual="$(git -C "$target" rev-parse HEAD)"
if [[ "$actual" != "$revision" || ! -d "$target/design-system/src/main/java" ]]; then
  echo "Compose source verification failed" >&2
  exit 1
fi

printf 'Compose Design System ready: %s\n' "$revision"
printf 'Run: cd %s/android && ./gradlew testDebugUnitTest assembleDebug\n' "$repo_root"
