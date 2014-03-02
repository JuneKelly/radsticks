-- name: get-user-profile
-- Gets profile fields for a single user by email id
SELECT id, name, created FROM user_account
WHERE id = :email


-- name: get-user-credentials!
-- gets credentials for a single user
SELECT id, password FROM user_account
WHERE id = :email


-- name: update-user
-- save changes to user record
UPDATE user_account
SET name = :name
WHERE id = :email

