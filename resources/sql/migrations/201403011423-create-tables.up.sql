CREATE TABLE IF NOT EXISTS user_account (
  id varchar(128) primary key,
  password text not NULL,
  name text not NULL,
  created timestamp not NULL,
  last_login timestamp
);



CREATE TABLE IF NOT EXISTS log (
  id serial primary key,
  data json not NULL,
  created timestamp not NULL
);
