CREATE OR REPLACE FUNCTION get_object_attrf(spob bigint, attr integer)
  RETURNS double precision AS
$BODY$DECLARE
  o_vari float;
BEGIN
  o_vari := NULL;
  SELECT spat_numb INTO o_vari
    FROM sprv_objects_attr
   WHERE spat_spob_id = spob
     AND spat_attr = attr;
  RETURN o_vari;
END;$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;
ALTER FUNCTION get_object_attrf(bigint, integer)
  OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_object_attrf(bigint, integer) TO owner_track;
GRANT EXECUTE ON FUNCTION get_object_attrf(bigint, integer) TO track_server_all;
REVOKE ALL ON FUNCTION get_object_attrf(bigint, integer) FROM public;
COMMENT ON FUNCTION get_object_attrf(bigint, integer) IS 'Процедура получения значение атрибута типа FLOAT для объекта';
