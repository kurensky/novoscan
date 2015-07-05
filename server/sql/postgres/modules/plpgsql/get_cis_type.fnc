-- Function: get_cis_type(spci int4)

-- DROP FUNCTION get_cis_type(spci int4);

CREATE OR REPLACE FUNCTION get_cis_type(spci int4)
  RETURNS int4 AS
$BODY$
DECLARE
  n_type int4;
BEGIN
  SELECT spci_code INTO n_type
    FROM sprv_cis_info
   WHERE spci_id = spci
   ;
  RETURN n_type;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_cis_type(spci int4) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_cis_type(spci int4) TO public;
GRANT EXECUTE ON FUNCTION get_cis_type(spci int4) TO owner_track;
GRANT EXECUTE ON FUNCTION get_cis_type(spci int4) TO t03_sprv_dev;
COMMENT ON FUNCTION get_cis_type(spci int4) IS 'Процедура получения кода принадлежности дороги';
