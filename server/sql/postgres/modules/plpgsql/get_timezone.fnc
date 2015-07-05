CREATE OR REPLACE FUNCTION get_timezone()
  RETURNS float AS
$BODY$
DECLARE
  time_zone FLOAT;
BEGIN
  SELECT EXTRACT(timezone_hour FROM CURRENT_TIME) INTO time_zone;
  RETURN time_zone;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_timezone() OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_timezone() TO public;
GRANT EXECUTE ON FUNCTION get_timezone() TO owner_track;
GRANT EXECUTE ON FUNCTION get_timezone() TO track_server_all;
GRANT EXECUTE ON FUNCTION get_timezone() TO owner_dev;
COMMENT ON FUNCTION get_timezone() IS 'Получение тайм зоны из БД';