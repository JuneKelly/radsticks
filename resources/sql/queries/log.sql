-- name: -create-log-entry
INSERT INTO log (data) values (cast(:data as json));

-- name: get-log-entry
SELECT * FROM log
WHERE id = :id
