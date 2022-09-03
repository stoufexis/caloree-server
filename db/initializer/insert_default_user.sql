insert into "user" (username, hashed_password)
values (:'defaultUsername', sha256(:'defaultPassword'::bytea))
