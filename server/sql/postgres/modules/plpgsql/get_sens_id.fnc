
CREATE OR REPLACE FUNCTION get_sens_id
 (i_vehicle_id varchar -- идентификатор сенсора. На текущий момент используется структура 1 сенсор - 1 модуль (один к одному). Поэтому количество сенсоров должно быть не более 1.
 )
  RETURNS int8 AS
$BODY$
DECLARE
  o_spsn_id int8;
  mviews RECORD;
BEGIN
  o_spsn_id := NULL;
  FOR mviews IN
  SELECT spsn_id
    FROM sprv_sensors
        ,sprv_modules
   WHERE spsn_spmd_id = spmd_id
     AND spmd_uid::varchar = i_vehicle_id
     ORDER BY spsn_id
  LOOP
    o_spsn_id := mviews.spsn_id;
    EXIT;
  END LOOP;
  RETURN o_spsn_id;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_sens_id
 (i_vehicle_id varchar
 )
 IS 'Процедура получения идентификатора сенсора по UID модуля';

REVOKE ALL ON FUNCTION get_sens_id
 (i_vehicle_id varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_sens_id
 (i_vehicle_id varchar
 ) TO GROUP TRACK_SERVER_ALL;


-- Function: get_sens_id(character varying)

-- DROP FUNCTION get_sens_id(character varying);

CREATE OR REPLACE FUNCTION get_sens_id
 (i_uid character varying
 ,i_type int8
 )
  RETURNS bigint AS
$BODY$DECLARE
  o_spsn_id int8;
  mviews RECORD;
BEGIN
  o_spsn_id := NULL;
  FOR mviews IN
  SELECT spsn_id
    FROM sprv_sensors
        ,sprv_modules
   WHERE spsn_spmd_id = spmd_id
     AND spmd_uid::varchar = i_uid
     AND spmd_spmt_id = i_type
     ORDER BY spsn_id
  LOOP
    o_spsn_id := mviews.spsn_id;
    EXIT;
  END LOOP;
  RETURN o_spsn_id;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_sens_id
 (i_uid character varying
 ,i_type int8
 ) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_sens_id
 (i_uid character varying
 ,i_type int8
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION get_sens_id
 (i_uid character varying
 ,i_type int8
 ) TO track_server_all;
COMMENT ON FUNCTION get_sens_id
 (i_uid character varying
 ,i_type int8
 ) IS 'Процедура получения идентификатора сенсора по UID модуля и типу';
