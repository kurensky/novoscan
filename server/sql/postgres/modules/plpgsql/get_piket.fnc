
CREATE OR REPLACE FUNCTION get_piket
 (n_gsdt INT8 -- ид точки
 )
  RETURNS numeric AS
$BODY$DECLARE
  v_piket gis_data.gsdt_piket%TYPE;
BEGIN
  SELECT gsdt_piket INTO v_piket FROM gis_data WHERE gsdt_id = n_gsdt;
  RETURN v_piket;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_piket
 (INT8
 )
 IS 'Процедура получения piket по точке';

REVOKE ALL ON FUNCTION get_piket
 (n_gsdt INT8 -- ид точки
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_piket
 (n_gsdt INT8 -- ид точки
 ) TO t03_sprv_dev;
