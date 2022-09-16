#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 \
  --set=caloreePassword="$CALOREE_USER_PASSWORD" \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER caloree WITH NOCREATEDB PASSWORD '${CALOREE_USER_PASSWORD}';
  CREATE DATABASE caloree OWNER caloree;
EOSQL
