#!/usr/bin/env bash
# Local CI: build + tests (+ optional Docker image). No GitHub Actions needed.
# Usage: scripts/ci.sh [--docker]
set -euo pipefail
cd "$(dirname "$0")/.."

if command -v mvn >/dev/null 2>&1; then MVN=mvn; else MVN=./mvnw; fi
command -v java >/dev/null || { echo "Java 17 is required"; exit 1; }
java -version 2>&1 | head -1

echo "==> Secret scan (tracked files)"
if git ls-files | grep -E '(^|/)\.env$' ; then echo "ERROR: .env is tracked"; exit 1; fi
if git grep -nE 'password\s*[=:]\s*[^$ {]+' -- 'src/main/**' 'docker-compose*.yml' ; then
  echo "ERROR: hardcoded password found"; exit 1
fi

echo "==> Build and test"
"$MVN" -B clean verify

if [[ "${1:-}" == "--docker" || "${CI_DOCKER:-0}" == "1" ]]; then
  echo "==> Docker build"
  docker build -t price-calculation-microservice:ci .
fi
echo "==> CI green"
