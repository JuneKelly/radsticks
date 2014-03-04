-- name: -reset-user!
DROP TABLE IF EXISTS user_account;
CREATE TABLE user_account (
  id varchar(128) primary key,
  password text not NULL,
  name text not NULL,
  created timestamp not NULL,
  last_login timestamp
);

-- name: -reset-log!
DROP TABLE IF EXISTS log;
CREATE TABLE log (
  id serial primary key,
  data json not NULL
);
