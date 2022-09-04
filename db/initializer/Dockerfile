FROM postgres:14-alpine

COPY static_foods.sql /opt/static_foods.sql
COPY static_nutrient.sql /opt/static_nutrient.sql
COPY static_food_nutrient.sql /opt/static_food_nutrient.sql
COPY tables.sql /opt/tables.sql
COPY views.sql /opt/views.sql
COPY functions.sql /opt/functions.sql
COPY insert_default_user.sql /opt/insert_default_user.sql

COPY init.sh /opt/init.sh

RUN chmod +x /opt/init.sh

ENTRYPOINT /opt/init.sh
