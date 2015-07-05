
CREATE OR REPLACE FUNCTION get_road_name_by_point
 (n_gsdt INT8 -- ид точки
 )
  RETURNS varchar AS
$BODY$DECLARE
  v_name VARCHAR(100);
BEGIN
  SELECT o.gsob_name
    INTO v_name 
    FROM gis_objects o
        ,gis_data d
   WHERE d.gsdt_gsob_id = o.gsob_id
     AND d.gsdt_id = n_gsdt
   ;
   RETURN v_name;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_road_name_by_point
 (INT8
 )
 IS 'Процедура получения имени объекта по точке';

REVOKE ALL ON FUNCTION get_road_name_by_point
 (INT8
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_piket
 (INT8
 ) TO t03_sprv_dev;
