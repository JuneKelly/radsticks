-- name: -create-snippet<!
INSERT INTO snippet (id, user_id, content, tags, created, updated)
VALUES (
  random_slug(),
  :email,
  :content,
  ARRAY[ :tags ],
  :created,
  :updated
);


-- name: -update-snippet!
UPDATE snippet
SET content = :content,
    tags = ARRAY[ :tags ],
    updated = :updated
WHERE id = :id


--name: -snippet-exists?
SELECT exists(
  SELECT 1 from snippet
  WHERE id = :id
)

-- name: -get-snippet
SELECT * from snippet
WHERE id = :id


-- name: -get-user-snippets
SELECT * from snippet
WHERE user_id = :email


-- name: -delete-snippet!
DELETE FROM snippet
WHERE id = :id
