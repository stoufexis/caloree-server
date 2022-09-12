#!/bin/bash

test ! -f /opt/check/has_ran &&
  sleep 5 &&
  cat \
    /opt/tables.sql \
    /opt/static_foods.sql \
    /opt/static_nutrient.sql \
    /opt/static_food_nutrient.sql \
    /opt/views.sql \
    /opt/functions.sql \
    /opt/insert_default_user.sql |
  psql \
    --set=defaultUsername="$DEFAULT_USERNAME" \
    --set=defaultPassword="$DEFAULT_PASSWORD" \
    -h postgres -p 5432 -U postgres -d postgres --single-transaction &&
  touch /opt/check/has_ran
