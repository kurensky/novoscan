-- Function: get_cis_desc(spci int4)

-- DROP FUNCTION get_cis_desc(spci int4);

CREATE OR REPLACE FUNCTION get_cis_desc(spci int4)
  RETURNS text AS
$BODY$
DECLARE
  v_name text;
BEGIN
  SELECT spci_desc INTO v_name
    FROM sprv_cis_info
   WHERE spci_id = spci
   ;
  RETURN v_name;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_cis_desc(spci int4) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_cis_desc(spci int4) TO public;
GRANT EXECUTE ON FUNCTION get_cis_desc(spci int4) TO owner_track;
GRANT EXECUTE ON FUNCTION get_cis_desc(spci int4) TO t03_sprv_dev;
COMMENT ON FUNCTION get_cis_desc(spci int4) IS 'Процедура получения описания принадлежности дороги';
