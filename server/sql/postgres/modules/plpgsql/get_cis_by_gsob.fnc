-- Function: get_cis_by_gsob(gsob int4)

-- DROP FUNCTION get_cis_by_gsob(gsob int4);

CREATE OR REPLACE FUNCTION get_cis_by_gsob(gsob int4)
  RETURNS int4 AS
$BODY$
DECLARE
  n_gsat int4;
BEGIN
  SELECT gsat_attr INTO n_gsat 
    FROM gis_objects
        ,gis_objects_attr
   WHERE gsob_id = gsat_gsob_id
     AND gsat_gstt_id = 2
     AND gsob_id = gsob
   LIMIT 1
   ;
  RETURN n_gsat;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_cis_by_gsob(gsob int4) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_cis_by_gsob(gsob int4) TO public;
GRANT EXECUTE ON FUNCTION get_cis_by_gsob(gsob int4) TO owner_track;
GRANT EXECUTE ON FUNCTION get_cis_by_gsob(gsob int4) TO t03_sprv_dev;
COMMENT ON FUNCTION get_cis_by_gsob(gsob int4) IS 'Процедура получения ссылки на принадлежность дороги';
