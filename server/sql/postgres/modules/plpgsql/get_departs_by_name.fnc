CREATE OR REPLACE FUNCTION get_departs_by_name
 (v_name varchar
 )
  RETURNS int8 AS
$BODY$
DECLARE
  dp_id INT8;
BEGIN
  SELECT spdp_id
    INTO dp_id
    FROM sprv_departs
   WHERE UPPER(spdp_name) = UPPER(v_name)
  ;
  RETURN dp_id;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

COMMENT ON FUNCTION get_departs_by_name
 (v_name varchar
 ) IS 'Функция получения идентификатора департамета по его имени';

REVOKE ALL ON FUNCTION get_departs_by_name
 (v_name varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_departs_by_name
 (v_name varchar
 ) TO TRACK_SERVER_ALL;
