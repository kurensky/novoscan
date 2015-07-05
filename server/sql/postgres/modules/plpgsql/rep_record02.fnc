-- Function: rep_record02(timestamp without time zone, timestamp without time zone, character varying, integer)

-- DROP FUNCTION rep_record02(timestamp without time zone, timestamp without time zone, character varying, integer);

CREATE OR REPLACE FUNCTION rep_record02(i_date_beg timestamp without time zone, i_date_end timestamp without time zone, i_spmd_uid character varying, i_time_offset integer)
  RETURNS SETOF record02 AS
$BODY$
DECLARE
	max_speed BIGINT := get_sysvari('MAX_SPEED');
	min_speed BIGINT := get_sysvari('MIN_SPEED');
	min_interval  FLOAT := get_sysvarf('MIN_INTERVAL');
	min_sattelite INTEGER := get_sysvari('MIN_SATELLITE_USED');
	max_timeout INTEGER := 600; -- 10 минут
	spob_name VARCHAR(100);

	mviews RECORD;
	geom RECORD;
	rid BIGINT;
	rec record02%rowtype;
	time_zone INTERVAL := i_time_offset * INTERVAL '1 minute';
--
  --
	lat DOUBLE PRECISION;
	lon DOUBLE PRECISION;
	alt DOUBLE PRECISION;
	dt TIMESTAMP;
--
	lat0 DOUBLE PRECISION;
	lon0 DOUBLE PRECISION;
	alt0 DOUBLE PRECISION;
	dt0 TIMESTAMP;
--
	lat1 DOUBLE PRECISION;
	lon1 DOUBLE PRECISION;
	alt1 DOUBLE PRECISION;
	dt1 TIMESTAMP;
--
	dtime BIGINT;
	dtime_summ0 BIGINT;

	i_dt1 TIMESTAMP;
	i_dt2 TIMESTAMP;

  --
	status integer;
	status0 integer;
	status1 integer;
	status_new   integer := 0;
	status_move  integer := 1;
	status_stop  integer := 2;
	status_unknown  integer := 3;
	status_polygon_in integer := 101;
	status_polygon_out integer := 102;
  
	status_bad integer := -1;

	--polygon1 geometry := ST_BdMPolyFromText('MULTILINESTRING((73.730 60.95,73.770 60.95),(73.770 60.95, 73.770 60.962),(73.770 60.962, 73.730 60.962),(73.730 60.962, 73.730 60.95))', 4326);
	polygon1 geometry;
	point1 geometry;
	point0 geometry;
	--geozone_name1 varchar := 'Геозона №1'; 
	geozone_name1 varchar; 

BEGIN
	i_dt1 := i_date_beg + time_zone;
	i_dt2 := i_date_end + time_zone;


FOR geom IN 
  SELECT gsdt_multiline_geom
        ,gsdt_info
        ,trim(both ' ' from uid) AS uid
    FROM gis_data
        ,gis_objects
	,regexp_split_to_table(i_spmd_uid,',') uid
   WHERE gsdt_gspt_id = 1002 -- MULTILINESTRING
     AND gsdt_gsob_id = gsob_id
     AND gsob_gstp_id = 200
     AND get_access_gsob_uid(gsob_id, trim(both ' ' from uid)) > 0
  ORDER BY uid, gsob_name	
LOOP
spob_name := get_obj_name_by_uid(geom.uid);
rid := 0;
dtime_summ0 := 0;
status0 := status_unknown;
status1 := status_unknown;
dt0 := NULL;
dt1 := NULL;
polygon1 := ST_BdMPolyFromText(st_astext(geom.gsdt_multiline_geom), 4326);
geozone_name1 :=  geom.gsdt_info||' ('||spob_name||') ';
FOR mviews IN
  SELECT MAX(dasn_id) AS dasn_id
        ,dasn_uid
        ,(dasn_datetime - time_zone) AS dasn_datetime
        ,dasn_latitude
        ,dasn_longitude
        ,dasn_hgeo
  FROM data_sensor
 WHERE dasn_vehicle = geom.uid
   AND (dasn_latitude != 0 AND dasn_longitude != 0)
   AND dasn_datetime BETWEEN i_dt1 AND i_dt2
   AND dasn_sog < max_speed
   AND dasn_sat_used > min_sattelite
  GROUP BY dasn_uid
        ,dasn_datetime
        ,dasn_latitude
        ,dasn_longitude
        ,dasn_hgeo 
  ORDER BY dasn_uid 
          ,dasn_datetime
          ,dasn_id
LOOP
	lat0     := mviews.dasn_latitude;
	lon0     := mviews.dasn_longitude;
	alt0     := mviews.dasn_hgeo;
	dt0      := mviews.dasn_datetime;
	point0   := ST_PointFromText('POINT('||lon0||' '||lat0||')', 4326);
	IF (rid = 0) THEN
		point1 := point0;
		dt1 := i_date_beg;
		rid := 1;
	END IF;
	dtime := EXTRACT(EPOCH FROM dt0)::int8 - EXTRACT(EPOCH FROM dt1)::int8;
	IF (dtime > max_timeout) THEN
		status0 := status_unknown;
	END IF;
     --
	IF (rid = 0 AND dtime > max_timeout) THEN
	-- Обработка 1 точки.
	        rid := rid + 1;
		dt := i_dt1;
		rec.rec_id := rid;
		rec.rec_datetime := dt;
		rec.rec_date := to_char(dt, 'YYYY.MM.DD');
		rec.rec_time := to_char(dt, 'HH24:MI:SS');
		rec.rec_address := geozone_name1;           
		rec.rec_type := status_unknown;
		rec.rec_long := dtime;
		RETURN NEXT rec;
		rid := rid + 1;
		dtime_summ0 := 0;
       -- Определение статуса следующей за разрывом точки 
		IF (ST_Contains(point0, polygon1)) THEN
			status1 := status_polygon_out;
			status0 := status_polygon_out;
       		ELSE
			status1 := status_polygon_in; 
			status0 := status_polygon_in; 
		END IF;
	ELSIF (status0 = status_unknown) THEN
	-- Проверяем статус точки до возникновения разрыва.
       		IF (ST_Contains(point1, polygon1)) THEN
			status0 := status_polygon_in; 
       		ELSE
			status0 := status_polygon_out; 
       		END IF;
       		dt := dt1 - dtime_summ0 * (INTERVAL '1' SECOND);
		rec.rec_id := rid;
		rec.rec_datetime := dt;
		rec.rec_date := to_char(dt, 'YYYY.MM.DD');
		rec.rec_time := to_char(dt, 'HH24:MI:SS');
		rec.rec_address := geozone_name1;           
		rec.rec_type := status0;
		rec.rec_long := dtime_summ0;
		RETURN NEXT rec;
		rid := rid + 1;
       -- Обработка отрезка разрыва	
		dt := dt1;
		rec.rec_id := rid;
		rec.rec_datetime := dt;
		rec.rec_date := to_char(dt, 'YYYY.MM.DD');
		rec.rec_time := to_char(dt, 'HH24:MI:SS');
		rec.rec_address := geozone_name1;           
		rec.rec_type := status_unknown;
		rec.rec_long := dtime;
		RETURN NEXT rec;
		rid := rid + 1;
		dtime_summ0 := 0;
       -- Определение статуса следующей за разрывом точки 
		IF (ST_Contains(point0, polygon1)) THEN
			status1 := status_polygon_out;
			status0 := status_polygon_out;
       		ELSE
			status1 := status_polygon_in; 
			status0 := status_polygon_in; 
		END IF;
	ELSE
		-- Нормальная обработка событий
		dtime_summ0 := dtime_summ0 + dtime;
		-- текущий статус точки
		IF (ST_Within(point0, polygon1) AND (NOT ST_Within(point1, polygon1))) THEN
			status0 := status_polygon_out;
		ELSIF ((NOT ST_Within(point0, polygon1)) AND ST_Within(point1, polygon1)) THEN   
			status0 := status_polygon_in;        
		END IF;
		-- Формирование записей.
		IF (status1 != status0) THEN
			dt := dt0 - dtime_summ0 * (INTERVAL '1' SECOND);
			rec.rec_id := rid;
			rec.rec_datetime := dt;
			rec.rec_date := to_char(dt, 'YYYY.MM.DD');
			rec.rec_time := to_char(dt, 'HH24:MI:SS');
			rec.rec_address := geozone_name1;           
			rec.rec_type := status0;
			rec.rec_long := dtime_summ0;
			RETURN NEXT rec;
			rid := rid + 1;
			dtime_summ0 := 0;
			status1 := status0;
		END IF;
	END IF;     
	point1   := point0;
	lat1     := lat0;
	lon1     := lon0;
	alt1     := alt0;
	dt1      := dt0;
     
--

END LOOP;
--
IF (dtime_summ0 > 0) THEN
	-- Определение статуса следующей за разрывом точки 
	IF (ST_Contains(point0, polygon1)) THEN
		status0 := status_polygon_in;
       	ELSE
		status0 := status_polygon_out; 
	END IF;
	--
	dt := dt0 - dtime_summ0 * (INTERVAL '1' SECOND);
	rec.rec_id := rid;
	rec.rec_datetime := dt;
	rec.rec_date := to_char(dt, 'YYYY.MM.DD');
	rec.rec_time := to_char(dt, 'HH24:MI:SS');
	rec.rec_address := geozone_name1;           
	rec.rec_type := status0;
	rec.rec_long := dtime_summ0;
	RETURN NEXT rec;
	dtime_summ0 := 0;
	rid := rid + 1;
	
END IF;
dtime := EXTRACT(EPOCH FROM i_dt2)::int8 - EXTRACT(EPOCH FROM dt0)::int8;
IF (dtime > max_timeout AND rid > 0) THEN
	dt := dt0;
	rec.rec_id := rid;
	rec.rec_datetime := dt;
	rec.rec_date := to_char(dt, 'YYYY.MM.DD');
	rec.rec_time := to_char(dt, 'HH24:MI:SS');
	rec.rec_address := geozone_name1;           
	rec.rec_type := status_unknown;
	rec.rec_long := dtime;
	RETURN NEXT rec;
END IF; 
END LOOP;
--
RETURN;
END;$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100
  ROWS 1000;
ALTER FUNCTION rep_record02(timestamp without time zone, timestamp without time zone, character varying, integer)
  OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION rep_record02(timestamp without time zone, timestamp without time zone, character varying, integer) TO public;
GRANT EXECUTE ON FUNCTION rep_record02(timestamp without time zone, timestamp without time zone, character varying, integer) TO owner_track;
GRANT EXECUTE ON FUNCTION rep_record02(timestamp without time zone, timestamp without time zone, character varying, integer) TO track_server_all;
COMMENT ON FUNCTION rep_record02(timestamp without time zone, timestamp without time zone, character varying, integer) IS 'Отчёт по нахождению в зоне';

