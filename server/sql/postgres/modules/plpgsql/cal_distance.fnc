CREATE OR REPLACE FUNCTION cal_distance
 (vehicle varchar
 ,dt1 timestamp
 ,dt2 timestamp
 )
  RETURNS text AS
$BODY$
DECLARE
  n_dist float;
  lat1 float;
  lat2 float;
  lon1 float;
  lon2 float;
  dat record;
  pi float := 3.14159265358979; --  число PI
  rad float := 6372795; -- радиус земли в метрах
  cl1 float;
  cl2 float;
  sl1 float;
  sl2 float;
  delta float;
  cdelta float;
  sdelta float;
  p1 float;
  p2 float;
  t1 timestamp;
  t2 timestamp;
  t_works integer;
  t_sleep integer;
  t_int int8;
  
BEGIN
  lat1 := NULL;
  lon1 := NULL;
  n_dist := 0;
  t_works := 0;
  t_sleep := 0;
  FOR dat IN 
    SELECT dasn_latitude*pi/180 AS latitude
          ,dasn_longitude*pi/180 AS longitude
          ,dasn_sog AS dasn_kmh
          ,dasn_datetime AS datetime
      FROM data_sensor
     WHERE dasn_vehicle = vehicle
       AND dasn_datetime BETWEEN dt1 AND dt2
     ORDER BY dasn_datetime
  LOOP
    lat1 := dat.latitude;
    lon1 := dat.longitude;
    t1  := dat.datetime;
    IF lat1 IS NOT NULL AND lat2 IS NOT NULL THEN
       --Вычисление дистанции
       --косинусы и синусы широт и разниц долгот
       cl1 := cos(lat1);
       cl2 := cos(lat2);
       sl1 := sin(lat1);
       sl2 := sin(lat2);
       delta := lon2 - lon1;
       cdelta := cos(delta);
       sdelta := sin(delta);
       --вычисления длины большого круга
       p1 := (cl2*sdelta)^2;
       p2 := ((cl1*sl2) - (sl1*cl2*cdelta))^2;
       n_dist := (atan((p1 + p2)^0.5/(sl1*sl2+cl1*cl2*cdelta)))*rad + n_dist;
       --Вычисление интервала движения
       t_int := EXTRACT(EPOCH FROM t1)::int8 - EXTRACT(EPOCH FROM t2)::int8;
       IF dat.dasn_kmh > 0.5 THEN
          t_works := t_int + t_works;
       ELSE
          t_sleep := t_int + t_sleep;
       END IF;
    END IF;
    lat2 := lat1;
    lon2 := lon1;
    t2   := t1;
  END LOOP;
  --RETURN '<INFO><DIST>'||(n_dist)::text||'</DIST><TIMEWORK>'||t_works::text||'</TIMEWORK><TIMESLEEP>'||t_sleep||'</TIMESLEEP></INFO>';
  RETURN (n_dist)::text||';'||t_works::text||';'||t_sleep||';';
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;


GRANT EXECUTE ON FUNCTION cal_distance
 (vehicle varchar
 ,dt1 timestamp
 ,dt2 timestamp
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION cal_distance
 (vehicle varchar
 ,dt1 timestamp
 ,dt2 timestamp
 ) TO t03_sprv_dev;
COMMENT ON FUNCTION cal_distance
 (vehicle varchar
 ,dt1 timestamp
 ,dt2 timestamp
 ) IS 'Процедура вычисления пройденной дистанции за указанный интервал дат';



CREATE OR REPLACE FUNCTION cal_distance
 (lat_beg float8
 ,lon_beg float8
 ,lat_end float8
 ,lon_end float8
 ,alt_beg float8
 ,alt_end float8
 )
  RETURNS float8 AS
$BODY$
DECLARE
  dist FLOAT;
  cl1  FLOAT;
  cl2  FLOAT;
  sl1  FLOAT;
  sl2  FLOAT;
  p1  FLOAT;
  p2  FLOAT;
  delta  FLOAT;
  cdelta FLOAT;
  sdelta FLOAT;
  rad    FLOAT := get_constf('EARTH_RADIUS'); -- радиус земли в метрах


BEGIN
  --Вычисление дистанции
  --косинусы и синусы широт и разниц долгот
  cl1 := cos(lat_beg);
  cl2 := cos(lat_end);
  sl1 := sin(lat_beg);
  sl2 := sin(lat_end);
  delta := (lon_end - lon_beg);
  cdelta := cos(delta);
  sdelta := sin(delta);
  --вычисления длины большого круга
  p1 := (cl2*sdelta)^2;
  p2 := ((cl1*sl2) - (sl1*cl2*cdelta))^2;
  dist := (atan((p1 + p2)^0.5/(sl1*sl2+cl1*cl2*cdelta)))*rad;

  RETURN dist;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION cal_distance
 (float8
 ,float8
 ,float8
 ,float8
 ,float8
 ,float8
 ) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION cal_distance
 (float8
 ,float8
 ,float8
 ,float8
 ,float8
 ,float8
 ) TO public;
GRANT EXECUTE ON FUNCTION cal_distance
 (float8
 ,float8
 ,float8
 ,float8
 ,float8
 ,float8
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION cal_distance
 (float8
 ,float8
 ,float8
 ,float8
 ,float8
 ,float8
 ) TO t03_sprv_dev;
COMMENT ON FUNCTION cal_distance
 (float8
 ,float8
 ,float8
 ,float8
 ,float8
 ,float8
 ) IS 'Процедура вычисления дистанции';
