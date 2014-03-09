-- generate safe, random eight-character strings
CREATE OR REPLACE FUNCTION random_slug()
RETURNS text
AS
$$ SELECT CAST(
     regexp_replace(
       encode(
         gen_random_bytes(6), 'base64'),
         '[/=+]',
         '-', 'g'
   ) AS text);$$
LANGUAGE SQL;
