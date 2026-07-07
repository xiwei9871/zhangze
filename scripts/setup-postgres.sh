#!/usr/bin/env bash
set -euo pipefail

DB_NAME="${MARKET_DB_NAME:-market_intelligence}"
DB_USER="${MARKET_DB_USERNAME:-market}"
DB_PASSWORD="${MARKET_DB_PASSWORD:-market}"
PG_FORMULA="${MARKET_PG_FORMULA:-postgresql@16}"

if ! brew list --versions "${PG_FORMULA}" >/dev/null 2>&1; then
  brew install "${PG_FORMULA}" || true
fi

PG_PREFIX="$(brew --prefix "${PG_FORMULA}")"
PG_BIN="${PG_PREFIX}/bin"
export PATH="${PG_BIN}:$PATH"
PG_DATA="${MARKET_PG_DATA:-/opt/homebrew/var/${PG_FORMULA}}"
PG_LOG="${MARKET_PG_LOG:-/opt/homebrew/var/log/${PG_FORMULA}.log}"

if ! pg_isready -h 127.0.0.1 -p 5432 >/dev/null 2>&1; then
  brew services start "${PG_FORMULA}" >/dev/null 2>&1 || true
  sleep 2
fi

if ! pg_isready -h 127.0.0.1 -p 5432 >/dev/null 2>&1; then
  brew services stop "${PG_FORMULA}" >/dev/null 2>&1 || true
  mkdir -p "$(dirname "${PG_LOG}")"
  if [ ! -f "${PG_DATA}/PG_VERSION" ]; then
    mkdir -p "${PG_DATA}"
    initdb -D "${PG_DATA}" --locale=C -E UTF-8 >/dev/null
  fi
  pg_ctl -D "${PG_DATA}" -l "${PG_LOG}" start >/dev/null
fi

until pg_isready -h 127.0.0.1 -p 5432 >/dev/null 2>&1; do
  sleep 1
done

if ! psql -h 127.0.0.1 -p 5432 -d postgres -tAc "select 1 from pg_roles where rolname='${DB_USER}'" | grep -q 1; then
  createuser -h 127.0.0.1 -p 5432 "${DB_USER}"
fi

psql -h 127.0.0.1 -p 5432 -d postgres -v ON_ERROR_STOP=1 \
  -c "alter user ${DB_USER} with password '${DB_PASSWORD}'" >/dev/null

if ! psql -h 127.0.0.1 -p 5432 -d postgres -tAc "select 1 from pg_database where datname='${DB_NAME}'" | grep -q 1; then
  createdb -h 127.0.0.1 -p 5432 -O "${DB_USER}" "${DB_NAME}"
fi

psql -h 127.0.0.1 -p 5432 -d "${DB_NAME}" -v ON_ERROR_STOP=1 \
  -c "grant all privileges on database ${DB_NAME} to ${DB_USER}" >/dev/null

echo "PostgreSQL is ready: postgresql://127.0.0.1:5432/${DB_NAME}"
