-- Function: get_obj_last_point(bigint, timestamp without time zone)

-- DROP FUNCTION get_obj_last_point(bigint, timestamp without time zone);

CREATE OR REPLACE FUNCTION get_obj_last_point
(IN i_spob_id bigint
,IN i_dt timestamp without time zone
,OUT o_dt timestamp without time zone
,OUT o_lat numeric
,OUT o_lon numeric
) RETURNS record AS
$BODY$
DECLARE
  dt1        TIMESTAMP;
  gsdt_id    int8;
BEGIN
    SELECT (dasl_datetime + get_timezone() * interval '1 hours') as dasl_datetime
          ,dasl_latitude
          ,dasl_longitude
      INTO o_dt
          ,o_lat
          ,o_lon
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
     ORDER BY dasl_datetime DESC LIMIT 1
     ;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION get_obj_last_point
(IN i_spob_id bigint
,IN i_dt timestamp without time zone
,OUT o_dt timestamp without time zone
,OUT o_lat numeric
,OUT o_lon numeric
)
 OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_obj_last_point
(IN i_spob_id bigint
,IN i_dt timestamp without time zone
,OUT o_dt timestamp without time zone
,OUT o_lat numeric
,OUT o_lon numeric
)
 TO public;
GRANT EXECUTE ON FUNCTION get_obj_last_point
(IN i_spob_id bigint
,IN i_dt timestamp without time zone
,OUT o_dt timestamp without time zone
,OUT o_lat numeric
,OUT o_lon numeric
) TO owner_track;
GRANT EXECUTE ON FUNCTION get_obj_last_point
(IN i_spob_id bigint
,IN i_dt timestamp without time zone
,OUT o_dt timestamp without time zone
,OUT o_lat numeric
,OUT o_lon numeric
) TO track_server_all;
COMMENT ON FUNCTION get_obj_last_point
(IN i_spob_id bigint
,IN i_dt timestamp without time zone
,OUT o_dt timestamp without time zone
,OUT o_lat numeric
,OUT o_lon numeric
) IS 'Дата последней активности объекта';