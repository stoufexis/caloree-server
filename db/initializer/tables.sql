create table "user"
(
    id              bigserial primary key,
    username        text  not null unique,
    hashed_password bytea not null
);

create table food
(
    id                   bigint primary key,
    f_type               varchar not null,
    description          text    not null,
    description_tsvector tsvector generated always as (to_tsvector('english', description)) stored
);

create index search_food on food using gin (description_tsvector);

create table custom_food
(
    id                   bigserial primary key,
    description          text                                                       not null,
    user_id              bigint                                                     not null references "user",
    description_tsvector tsvector generated
                             always as (to_tsvector('english', description)) stored not null
);

create index search_custom_food on custom_food using gin (description_tsvector);

create table nutrient
(
    id        bigint primary key,
    "name"    text    not null,
    unit_name varchar not null
);

create table token
(
    id           bigserial primary key,
    user_id      bigint                              not null references "user",
    token        uuid      default gen_random_uuid() not null,
    generated_at timestamp default (now() at time zone 'utc'::text)
);

create index tokens_by_time_descending on token (generated_at desc);

create table food_nutrient
(
    id          bigint  not null primary key,
    food_id     integer not null references food,
    nutrient_id integer not null references nutrient,
    amount      real    not null,
    constraint unique_food_id_nutrient_id unique (food_id, nutrient_id)
);

create table custom_food_nutrient
(
    id             bigserial primary key,
    custom_food_id bigint not null references custom_food on delete cascade,
    nutrient_id    bigint not null references nutrient,
    amount         real   not null,
    constraint unique_custom_food_id_nutrient_id unique (custom_food_id, nutrient_id)
);

create table "log"
(
    id             bigserial primary key,
    food_id        bigint references food,
    custom_food_id bigint references custom_food,
    amount         integer  not null,
    "day"          date     not null,
    "minute"       smallint not null,
    user_id        bigint   not null references "user" (id),
    generated_at   timestamp without time zone default now(),
    check ( "minute" >= 0 and "minute" < 1440),
    check ((food_id is not null and custom_food_id is null)
        or (food_id is null and custom_food_id is not null))
);

create table user_target_nutrients
(
    id          bigserial primary key,
    user_id     bigint not null references "user",
    nutrient_id bigint not null references nutrient,
    amount      real   not null,
    constraint unique_user_id_nutrient_id unique (user_id, nutrient_id)
);
