

CREATE OR REPLACE FUNCTION act_sensor
 (i_vehicle varchar
 ,d_dt1 timestamp
 ,d_dt2 timestamp
 )
  RETURNS int4 AS
$BODY$
DECLARE
  n_ret int;
  cl CURSOR(vehicle varchar,dt1 timestamp,dt2 timestamp) IS
    SELECT 1  
      FROM data_sensor
     WHERE dasn_vehicle = vehicle
       AND dasn_datetime BETWEEN dt1 AND dt2
     ;
BEGIN
  n_ret := 0;
  IF i_vehicle IS NULL OR d_dt1 IS NULL OR d_dt2 IS NULL THEN
  -- неверное задание параметров
    RETURN n_ret;
  END IF;
  OPEN cl(i_vehicle,d_dt1,d_dt2);
  FETCH cl INTO n_ret;
  CLOSE cl;
  RETURN COALESCE(n_ret,0);
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION act_sensor
 (i_vehicle varchar
 ,d_dt1 timestamp
 ,d_dt2 timestamp
 ) OWNER TO owner_track;

REVOKE EXECUTE ON FUNCTION act_sensor
 (i_vehicle varchar
 ,d_dt1 timestamp
 ,d_dt2 timestamp
 ) FROM public;

GRANT EXECUTE ON FUNCTION act_sensor
 (i_vehicle varchar
 ,d_dt1 timestamp
 ,d_dt2 timestamp
 ) TO owner_track;

GRANT EXECUTE ON FUNCTION act_sensor
 (i_vehicle varchar
 ,d_dt1 timestamp
 ,d_dt2 timestamp
 ) TO track_server_all;

COMMENT ON FUNCTION act_sensor
 (i_vehicle varchar
 ,d_dt1 timestamp
 ,d_dt2 timestamp
 ) IS 'Процедура проверки активности модуля в интервале дат 0 - не был автивен, 1 - был активен';
