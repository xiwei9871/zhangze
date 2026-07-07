#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
API_BASE="${API_BASE:-http://127.0.0.1:8080/api/v1/market/intelligence}"

"${ROOT_DIR}/scripts/setup-postgres.sh"

if ! curl -fsS "${API_BASE}/dashboard/summary" >/dev/null 2>&1; then
  echo "Backend is not running. Start it in another terminal:"
  echo "  cd ${ROOT_DIR}/api && mvn spring-boot:run"
  exit 1
fi

"${ROOT_DIR}/scripts/seed-real-route.sh"
curl -sS -X POST "${API_BASE}/crawl/run" >/dev/null
"${ROOT_DIR}/scripts/import-real-business-items.sh"

echo "Real route triggered."
echo "Frontend: http://127.0.0.1:5173/"
