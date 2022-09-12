create materialized view foods_with_nutrients_view as
select f.id,
       f.f_type,
       f.description,
       coalesce(sum(case when n.id = 1008 then fn.amount end), 0::real) as energy,
       coalesce(sum(case when n.id = 1003 then fn.amount end), 0::real) as protein,
       coalesce(sum(case when n.id = 1005 then fn.amount end), 0::real) as carbs,
       coalesce(sum(case when n.id = 1004 then fn.amount end), 0::real) as fat,
       coalesce(sum(case when n.id = 1079 then fn.amount end), 0::real) as fiber
from food f
         join food_nutrient fn on f.id = fn.food_id
         join nutrient n on n.id = fn.nutrient_id
group by f.id;

refresh materialized view foods_with_nutrients_view;

create view custom_food_with_nutrients_view as
select cf.id,
       cf.user_id,
       cf.description,
       coalesce(sum(case when n.id = 1008 then cfn.amount end), 0::real) as energy,
       coalesce(sum(case when n.id = 1003 then cfn.amount end), 0::real) as protein,
       coalesce(sum(case when n.id = 1005 then cfn.amount end), 0::real) as carbs,
       coalesce(sum(case when n.id = 1004 then cfn.amount end), 0::real) as fat,
       coalesce(sum(case when n.id = 1079 then cfn.amount end), 0::real) as fiber
from custom_food cf
         join custom_food_nutrient cfn on cf.id = cfn.custom_food_id
         join nutrient n on n.id = cfn.nutrient_id
group by cf.id;

create view user_with_target_nutrients_view as
select u.id,
       u.username,
       coalesce(sum(case when n.id = 1008 then utn.amount end), 0::real) as energy,
       coalesce(sum(case when n.id = 1003 then utn.amount end), 0::real) as protein,
       coalesce(sum(case when n.id = 1005 then utn.amount end), 0::real) as carbs,
       coalesce(sum(case when n.id = 1004 then utn.amount end), 0::real) as fat,
       coalesce(sum(case when n.id = 1079 then utn.amount end), 0::real) as fiber
from "user" u
         join user_target_nutrients utn on u.id = utn.user_id
         join nutrient n on n.id = utn.nutrient_id
group by u.id;