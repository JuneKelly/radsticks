CREATE TABLE IF NOT EXISTS snippet (
  id text primary key,
  user_id varchar(128) NOT NULL references user_account(id),
  content text NOT NULL,
  tags text ARRAY,
  created timestamp NOT NULL,
  updated timestamp NOT NULL
);
--;;
CREATE INDEX idx_snippet_user_id
ON snippet (user_id ASC);
--;;
CREATE INDEX idx_snippet_created
ON snippet (created ASC);
--;;
CREATE INDEX idx_snipet_updated
ON snippet (updated ASC);
