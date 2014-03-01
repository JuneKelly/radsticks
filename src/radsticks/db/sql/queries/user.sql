-- SQL Queries for User Entity

-- name: get-user-profile
-- Gets profile fields for a single user by email id
select id, name, created from user
where id = :email


-- name: get-user-credentials!
-- gets credentials for a single user
select id, password from user
where id = :email
