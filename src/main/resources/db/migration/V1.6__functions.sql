create or replace function log_aggregated_with_offset(int)
    returns table
            (
                food_id        bigint,
                custom_food_id bigint,
                "day"          date,
                "minute"       smallint,
                user_id        bigint,
                amount         int
            )
as
'select food_id,
        custom_food_id,
        "day",
        "minute",
        user_id,
        sum(amount)
 from (select *
       from log
       order by generated_at desc
       offset $1) as l
 group by food_id, custom_food_id, "day", "minute", user_id;' language sql;


create or replace function log_with_nutrients_with_offset(int)
    returns table
            (
                food_id        bigint,
                custom_food_id bigint,
                "day"          date,
                "minute"       smallint,
                description    text,
                user_id        bigint,
                amount         int,
                energy         real,
                protein        real,
                carbs          real,
                fat            real,
                fiber          real
            )
as
'
    select food_id                                                   as food_id,
           null                                                      as custom_food_id,
           "day"                                                     as "day",
           "minute"                                                  as "minute",
           f.description                                             as description,
           lav.user_id                                               as user_id,
           amount                                                    as amount,
           (fwnv.energy * (lav.amount :: real / 100 :: real))::real  as energy,
           (fwnv.protein * (lav.amount :: real / 100 :: real))::real as protein,
           (fwnv.carbs * (lav.amount :: real / 100 :: real))::real   as carbs,
           (fwnv.fat * (lav.amount :: real / 100 :: real))::real     as fat,
           (fwnv.fiber * (lav.amount :: real / 100 :: real))::real   as fiber
    from log_aggregated_with_offset($1) lav
             inner join food f on f.id = lav.food_id
             inner join foods_with_nutrients_view fwnv on fwnv.id = f.id
    union
    select null                                                       as food_id,
           custom_food_id                                             as custom_food_id,
           "day"                                                      as "day",
           "minute"                                                   as "minute",
           cf.description                                             as description,
           lav.user_id                                                as user_id,
           amount                                                     as amount,
           (cfwnv.energy * (lav.amount :: real / 100 :: real))::real  as energy,
           (cfwnv.protein * (lav.amount :: real / 100 :: real))::real as protein,
           (cfwnv.carbs * (lav.amount :: real / 100 :: real))::real   as carbs,
           (cfwnv.fat * (lav.amount :: real / 100 :: real))::real     as fat,
           (cfwnv.fiber * (lav.amount :: real / 100 :: real))::real   as fiber
    from log_aggregated_with_offset($1) lav
             inner join custom_food cf on cf.id = lav.custom_food_id
             inner join custom_food_with_nutrients_view cfwnv on cfwnv.id = cf.id;
' language sql;

select food_id, custom_food_id, amount, "day", "minute", "user_id"
from log_aggregated_with_offset(0)
where amount > 0
order by "minute"
offset 1 limit 1;

select *
from log_aggregated_with_offset(0)
where amount > 0
order by "minute";

select *
from log_with_nutrients_with_offset(0)
where amount > 0
order by "minute";

select food_id,
       custom_food_id,
       "day",
       "minute",
       description,
       amount,
       energy,
       protein,
       carbs,
       fat,
       fiber
from log_with_nutrients_with_offset(0)
where user_id = 1
  and amount > 0
  and "minute" >= 0
  and "minute" < 1440
order by "minute"
limit 25 offset 0 * 25;

select food_id, custom_food_id, -amount, "day", "minute", "user_id"
from log_aggregated_with_offset(0)
where amount > 0
  and user_id = 1
order by "minute"