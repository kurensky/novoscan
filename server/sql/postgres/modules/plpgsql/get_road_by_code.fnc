
CREATE OR REPLACE FUNCTION get_road_by_code
 (v_code varchar -- код дороги во внешней системе.
 )
  RETURNS int4 AS
$BODY$DECLARE
  n_gsob int4;
BEGIN
  n_gsob := NULL;
  SELECT gsob_id INTO n_gsob FROM gis_objects WHERE gsob_code = v_code;
  RETURN n_gsob;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_road_by_code
 (varchar
 )
 IS 'Процедура получения ИД дороги по её коду во внешней системе';

REVOKE ALL ON FUNCTION get_road_by_code
 (varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_road_by_code
 (varchar
 ) TO GROUP TRACK_SERVER_ALL;
