-- name: -create-log-entry!
INSERT INTO log (data, created)
VALUES (cast(:data as json), :created);

-- name: get-log-entry
SELECT * FROM log
WHERE id = :id
