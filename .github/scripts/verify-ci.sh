#!/usr/bin/env bash
set -euo pipefail

repo="$1"
sha="$2"

# Fetch check runs for the commit, excluding the release workflow's own jobs
checks=$(gh api "repos/${repo}/commits/${sha}/check-runs" \
  --jq '.check_runs[] | select(.name != "Verify CI passed") | {name, conclusion, status}')

if [ -z "$checks" ]; then
  echo "::error::No CI check runs found for this commit. Push to main and wait for CI before tagging."
  exit 1
fi

echo "Check runs found:"
echo "$checks" | jq -r '"  \(.name): \(.conclusion // .status)"'

# Fail if any check is not successful
failed=$(echo "$checks" | jq -r 'select(.conclusion != "success") | .name')
if [ -n "$failed" ]; then
  echo "::error::The following checks have not passed: $failed"
  exit 1
fi
