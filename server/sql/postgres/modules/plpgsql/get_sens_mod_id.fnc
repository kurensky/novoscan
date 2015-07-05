

CREATE OR REPLACE FUNCTION get_sens_mod_id(i_uid character varying, i_type bigint
, o_spsn OUT  bigint
,o_spmd OUT bigint)
  RETURNS record AS
$BODY$DECLARE
  mviews RECORD;
BEGIN
  o_spsn := NULL;
  o_spmd := NULL;
  FOR mviews IN
  SELECT spsn_id
        ,spmd_id
    FROM sprv_sensors
        ,sprv_modules
   WHERE spsn_spmd_id = spmd_id
     AND spmd_uid::varchar = i_uid
     AND spmd_spmt_id = i_type
     ORDER BY spsn_id
  LOOP
    o_spsn := mviews.spsn_id;
    o_spmd := mviews.spmd_id;
    EXIT;
  END LOOP;
  RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;
ALTER FUNCTION get_sens_mod_id(character varying, bigint)
  OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_sens_mod_id(character varying, bigint) TO owner_track;
GRANT EXECUTE ON FUNCTION get_sens_mod_id(character varying, bigint) TO public;
GRANT EXECUTE ON FUNCTION get_sens_mod_id(character varying, bigint) TO track_server_all;
COMMENT ON FUNCTION get_sens_mod_id(character varying, bigint) IS 'Процедура получения идентификатора сенсора и модуля по UID модуля и типу';

