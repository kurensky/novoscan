CREATE OR REPLACE FUNCTION get_obj_last_activity
 (i_spob_id INT8
 )
  RETURNS timestamp AS
$BODY$


DECLARE
  dt1 timestamp;
BEGIN
    SELECT (dasl_datetime + get_timezone() * interval '1 hours') as dasl_datetime
      INTO dt1
      FROM sprv_objects b 
          ,sprv_modules m 
          ,sprv_clients a
          ,sprv_departs d
          ,data_sensor_last  s
     WHERE a.spcl_id = b.spob_spcl_id 
       AND d.spdp_id = a.spcl_spdp_id
       AND m.spmd_spob_id = b.spob_id
       AND b.spob_id = i_spob_id
       AND m.spmd_uid::varchar = s.dasl_vehicle
       ORDER BY dasl_datetime
       LIMIT 1
       ;
  RETURN dt1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

COMMENT ON FUNCTION get_obj_last_activity
 (i_spob_id INT8
 ) IS 'Дата последней активности объекта';

REVOKE ALL ON FUNCTION get_obj_last_activity
 (i_spob_id INT8
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_obj_last_activity
 (i_spob_id INT8
 ) TO TRACK_SERVER_ALL;



CREATE OR REPLACE FUNCTION get_obj_last_activity
 (IN i_spob_id int8
 ,IN i_dt timestamp
 ,OUT o_dt timestamp
 ,OUT o_road varchar
 ,OUT o_piket numeric
 ) AS
$BODY$
DECLARE
  dt1        TIMESTAMP;
  gsdt_id    int8;
BEGIN
    SELECT (dasl_datetime + get_timezone() * interval '1 hours') as dasl_datetime
          ,get_point(dasl_latitude,dasl_longitude,dasl_hgeo)
      INTO o_dt
          ,gsdt_id
      FROM sprv_objects b 
          ,sprv_modules m 
          ,sprv_clients a
          ,sprv_departs d
          ,data_sensor_last  s
     WHERE a.spcl_id = b.spob_spcl_id 
       AND d.spdp_id = a.spcl_spdp_id
       AND m.spmd_spob_id = b.spob_id
       AND b.spob_id = i_spob_id
       AND m.spmd_uid::varchar = s.dasl_vehicle
     GROUP BY get_point(dasl_latitude,dasl_longitude,dasl_hgeo)
             ,dasl_datetime
     ORDER BY dasl_datetime DESC LIMIT 1
     ;
     IF o_dt IS NOT NULL THEN
       IF gsdt_id IS NOT NULL THEN
         o_piket := get_piket(gsdt_id);
         o_road  := get_road_name_by_point(gsdt_id);
       END IF;
       IF o_road IS NULL THEN
         o_road := 'Неизв.';
         o_piket:= NULL;
       END IF;
     ELSE
       o_road  := 'Нет данных';
       o_piket := NULL; 
     END IF;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION get_obj_last_activity
 (IN i_spob_id int8
 ,IN i_dt timestamp
 ,OUT o_dt timestamp
 ,OUT o_road varchar
 ,OUT o_piket numeric
 ) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_obj_last_activity
 (IN i_spob_id int8
 ,IN i_dt timestamp
 ,OUT o_dt timestamp
 ,OUT o_road varchar
 ,OUT o_piket numeric
 ) TO public;
GRANT EXECUTE ON FUNCTION get_obj_last_activity
 (IN i_spob_id int8
 ,IN i_dt timestamp
 ,OUT o_dt timestamp
 ,OUT o_road varchar
 ,OUT o_piket numeric
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION get_obj_last_activity
 (IN i_spob_id int8
 ,IN i_dt timestamp
 ,OUT o_dt timestamp
 ,OUT o_road varchar
 ,OUT o_piket numeric
 ) TO track_server_all;
COMMENT ON FUNCTION get_obj_last_activity
 (IN i_spob_id int8
 ,IN i_dt timestamp
 ,OUT o_dt timestamp
 ,OUT o_road varchar
 ,OUT o_piket numeric
 ) IS 'Дата последней активности объекта';
