-- name: create-log-entry
INSERT INTO log (data) values (:data)

-- name: get-log-entry
SELECT * FROM log
WHERE id = :id
