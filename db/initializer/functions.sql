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
    select food_id,
           null                                      as custom_food_id,
           "day",
           "minute",
           f.description,
           lav.user_id,
           amount,
           (fwnv.energy * (lav.amount / 100))::real  as energy,
           (fwnv.protein * (lav.amount / 100))::real as protein,
           (fwnv.carbs * (lav.amount / 100))::real   as carbs,
           (fwnv.fat * (lav.amount / 100))::real     as fat,
           (fwnv.fiber * (lav.amount / 100))::real   as fiber
    from log_aggregated_with_offset($1) lav
             inner join food f on f.id = lav.food_id
             inner join foods_with_nutrients_view fwnv on fwnv.id = f.id
    union
    select null                                       as food_id,
           custom_food_id,
           "day",
           "minute",
           cf.description,
           lav.user_id,
           amount,
           (cfwnv.energy * (lav.amount / 100))::real  as energy,
           (cfwnv.protein * (lav.amount / 100))::real as protein,
           (cfwnv.carbs * (lav.amount / 100))::real   as carbs,
           (cfwnv.fat * (lav.amount / 100))::real     as fat,
           (cfwnv.fiber * (lav.amount / 100))::real   as fiber
    from log_aggregated_with_offset($1) lav
             inner join custom_food cf on cf.id = lav.custom_food_id
             inner join custom_food_with_nutrients_view cfwnv on cfwnv.id = cf.id;
' language sql;
