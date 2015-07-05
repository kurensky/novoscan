-- Function: rep_005(i_spob_id int8, dt1 "timestamp", dt2 "timestamp")

-- DROP FUNCTION rep_005(i_spob_id int8, dt1 "timestamp", dt2 "timestamp");

CREATE OR REPLACE FUNCTION rep_005(i_spob_id int8, dt1 "timestamp", dt2 "timestamp")
  RETURNS SETOF text AS
$BODY$
DECLARE
  rec record;
  n_dist float;
  n_dist_dist float;
  lat1 float;
  lat2 float;
  lon1 float;
  lon2 float;
  alt1 float;
  alt2 float;
  lat_beg float;
  lon_beg float;
  lat_end float;
  lon_end float;
  pi float  := get_constf('PI'); --  число PI
  rad float := get_constf('EARTH_RADIUS'); -- радиус земли в метрах
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
  t_works text;
  t_int int8;
  rec_row integer;
  s_speed float;
  min_speed    float := get_sysvarf('MIN_SPEED');
  min_interval float := get_sysvarf('MIN_INTERVAL');
  t_stat integer;
  t_beg timestamp;
  t_end timestamp;
  t_int_int int8;
  dt_format text := get_sysvarc('NLS_DATE_FORMAT');
  road_name gis_objects.gsob_name%TYPE;
  road_name_prev gis_objects.gsob_name%TYPE;
  piket gis_data.gsdt_piket%TYPE; 
  piket_prev gis_data.gsdt_piket%TYPE; 
  road_new BOOLEAN;
  piket_beg gis_data.gsdt_piket%TYPE; 
  piket_end gis_data.gsdt_piket%TYPE;
  gsob_id INT4;
  ra float := pi/180;

BEGIN
  lat1 := NULL;
  lon1 := NULL;
  alt1 := NULL;
  n_dist := 0;
  rec_row := 0;
  road_new := FALSE;
  piket_beg := NULL;
  piket_end := NULL;
--  RETURN NEXT '№;Событие;Начало;Конец;Время;Дорога;Протяженность;Средняя скорость;Пикет';
  FOR rec IN  
    SELECT dasn_latitude AS latitude
          ,dasn_longitude AS longitude
          ,dasn_sog AS dasn_kmh
          ,dasn_datetime AS datetime
          ,dasn_hgeo AS z
      FROM sprv_objects b 
          ,sprv_modules m 
          ,sprv_clients a
          ,sprv_departs d
          ,data_sensor  s
  WHERE a.spcl_id = b.spob_spcl_id 
    AND d.spdp_id = a.spcl_spdp_id
    AND m.spmd_spob_id = b.spob_id
    AND b.spob_id = i_spob_id
    AND m.spmd_uid = s.dasn_vehicle
    AND s.dasn_datetime BETWEEN dt1 AND dt2
  GROUP BY dasn_latitude
          ,dasn_longitude
          ,dasn_sog
          ,dasn_datetime
          ,dasn_hgeo
  ORDER BY s.dasn_datetime 
  LOOP
    --
--    lat1           := rec.latitude*pi/180;
--    lon1           := rec.longitude*pi/180;
    lat1           := rec.latitude; --*pi/180;
    lon1           := rec.longitude; --*pi/180;
    alt1           := rec.z;
    t1             := rec.datetime;
    -- Геграфия
    SELECT o_gsob
          ,v_piket
          ,v_road_name
      INTO gsob_id
          ,piket
          ,road_name
      FROM get_road_name_piket(lat1,lon1,alt1,gsob_id)
    ;
    IF road_name != road_name_prev  THEN
       road_new  := TRUE;
    ELSE
       road_new := FALSE;
    END IF;
    IF lat1 IS NOT NULL AND lat2 IS NOT NULL THEN
       --Вычисление дистанции
       --косинусы и синусы широт и разниц долгот
       cl1 := cos(lat1*ra);
       cl2 := cos(lat2*ra);
       sl1 := sin(lat1*ra);
       sl2 := sin(lat2*ra);
       delta := (lon2 - lon1)*ra;
       cdelta := cos(delta);
       sdelta := sin(delta);
       --вычисления длины большого круга
       p1 := (cl2*sdelta)^2;
       p2 := ((cl1*sl2) - (sl1*cl2*cdelta))^2;
       n_dist := (atan((p1 + p2)^0.5/(sl1*sl2+cl1*cl2*cdelta)))*rad;
     /*  n_dist := atan(Distance(GeomFromEWKT('SRID=4326;POINT('||lat1*ra||' '||lon1*ra||' '||alt1||')')
                         ,GeomFromEWKT('SRID=4326;POINT('||lat2*ra||' '||lon2*ra||' '||alt2||')')))*rad;*/

       --Вычисление интервала движения
       t_int := EXTRACT(EPOCH FROM t1)::int8 - EXTRACT(EPOCH FROM t2)::int8;
       -- Определение средней скорости
       IF t_int = 0 THEN
         s_speed := 0;
       ELSE
         s_speed := round((n_dist*3.6/t_int)::numeric,2);
       END IF;

       -- Отсеим с интервалом менее 10 сек (защитный интервал)
       IF t_int < min_interval THEN
         t_int_int   := t_int_int   + t_int;
         n_dist_dist := n_dist_dist + n_dist;
         t_end       := t1;
       -- Если скорость более 1 км в час или интервал передачи данных менее 280 скунд - то машина движется 
       ELSIF s_speed > min_speed OR t_int < 280 THEN
         IF t_stat = 0 THEN
           t_stat      := 1;
           t_works     := 'Движение';
           t_int_int   := t_int_int   + t_int;
           n_dist_dist := n_dist_dist + n_dist;
           t_end       := t1;
           lat_end     := lat1*ra;
           lon_end     := lon1*ra;
           piket_beg   := piket;
           road_new    := FALSE;
         ELSIF t_stat != 1 THEN
           n_dist_dist := 0;
           IF t_int_int = 0 THEN
             s_speed := 0;
           ELSE
             s_speed     := round((n_dist_dist*3.6/t_int_int)::numeric,2);
           END IF;
           rec_row     := rec_row + 1;
           RETURN NEXT
           get_rep_string(rec_row
                         ,t_works
                         ,t_beg
                         ,t_end
                         ,t_int_int
                         ,road_name
                         ,n_dist_dist
                         ,s_speed
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
           t_stat      := 1;
           t_beg       := t2;
           t_end       := t1;
           t_works     := 'Движение';
           n_dist_dist := 0;
           t_int_int   := 0;
         ELSE
           IF road_new THEN
             IF t_int_int = 0 THEN
               s_speed := 0;
             ELSE
               s_speed := round((n_dist_dist*3.6/t_int_int)::numeric,2);
             END IF;
             rec_row     := rec_row + 1;
             RETURN NEXT 
             get_rep_string(rec_row
                           ,t_works
                           ,t_beg
                           ,t_end
                           ,t_int_int
                           ,road_name
                           ,n_dist_dist
                           ,s_speed
                           ,lat_beg
                           ,lon_beg
                           ,lat_end
                           ,lon_end
                           ,piket_beg
                           ,piket_end
                           );
             t_stat      := 1;
             t_beg       := t2;
             t_end       := t1;
             lat_beg     := lat_end;
             lon_beg     := lon_end;
             n_dist_dist := n_dist;
             t_int_int   := t_int;
             piket_beg   := piket;
             piket_end   := piket;
           ELSE
             t_int_int   := t_int_int   + t_int;
             n_dist_dist := n_dist_dist + n_dist;
             t_end       := t1;
             lat_end     := lat1*ra;
             lon_end     := lon1*ra;
             piket_end   := piket;
           END IF;
         END IF;
       ELSE
         IF t_stat = 0 THEN
           t_stat      := 2;
           t_works     := 'Стоянка';
           t_int_int   := t_int_int   + t_int;
           n_dist_dist := n_dist_dist + n_dist;
           t_end       := t1;
           lat_end     := lat1*ra;
           lon_end     := lon1*ra;
           piket_end   := piket;
         ELSIF t_stat != 2 THEN
           IF t_int_int = 0 THEN
             s_speed := 0;
           ELSE
             s_speed := round((n_dist_dist*3.6/t_int_int)::numeric,2);
           END IF;
           rec_row     := rec_row + 1;
           RETURN NEXT
           get_rep_string(rec_row
                         ,t_works
                         ,t_beg
                         ,t_end
                         ,t_int_int
                         ,road_name
                         ,n_dist_dist
                         ,s_speed
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
           t_stat      := 2;
           t_beg       := t2;
           t_end       := t1;
           lat_beg     := lat_end;
           lon_beg     := lon_end;
           t_works     := 'Стоянка';
           n_dist_dist := n_dist;
           t_int_int   := t_int;
           piket_beg   := piket_end;
           piket_end   := piket_end;
         ELSE
           t_int_int   := t_int_int   + t_int;
           n_dist_dist := n_dist_dist + n_dist;
           t_end       := t1;
           lat_end     := lat1*ra;
           lon_end     := lon1*ra;
           piket_end   := piket;
         END IF;
     END IF;
    ELSE
       t_works     := 'Первая точка';
       t_stat      := 0;
       t_beg       := t1;
       t_end       := t1;
       n_dist      := 0;
       t_int       := 0;
       s_speed     := 0;
       n_dist_dist := 0;
       t_int_int   := 0;
       rec_row     := rec_row + 1;
       lat_beg     := lat1*ra;
       lat_end     := lat_beg;
       lon_beg     := lon1*ra;
       lon_end     := lon_beg;
       piket_beg   := piket;
       piket_end   := piket;
       RETURN NEXT
           get_rep_string(rec_row
                         ,t_works
                         ,t_beg
                         ,t_end
                         ,t_int_int
                         ,road_name
                         ,n_dist_dist
                         ,s_speed
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
    END IF;
    lat2           := lat1;
    lon2           := lon1;
    alt2           := alt1;
    t2             := t1;
    road_name_prev := road_name;
    piket_prev     := piket;
    road_new       := FALSE;
  END LOOP;
  
  -- Обработка последней точки
  IF rec_row != 0 THEN
    -- если записи были
    rec_row     := rec_row + 1;
    RETURN NEXT
           get_rep_string(rec_row
                         ,t_works
                         ,t_beg
                         ,t_end
                         ,t_int_int
                         ,road_name
                         ,n_dist_dist
                         ,s_speed
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
    IF now() < dt2 THEN
      t2       := now();
    ELSE
      t2       := dt2;
    END IF;
    t_int_int   := (EXTRACT(EPOCH FROM t2)::int8 - EXTRACT(EPOCH FROM t_end)::int8);
    t_beg       := t_end;
    t_end       := t2;
    n_dist_dist := 0;
    IF t_stat = 1 AND t_int_int > 360 THEN
      -- если перед этим было движение и интервал  превысил 6 минут поставим стоянку
      t_stat      := 2;
      t_works     := 'Стоянка';
      s_speed     := 0;
      n_dist_dist := 0;
      rec_row     := rec_row + 1;
      piket_end   := piket_beg;
      lat_end     := lat_beg;
      lon_end     := lon_beg;
      RETURN NEXT            
           get_rep_string(rec_row
                         ,t_works
                         ,t_beg
                         ,t_end
                         ,t_int_int
                         ,road_name
                         ,n_dist_dist
                         ,s_speed
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
    END IF;
  ELSE
    SELECT o_dt
          ,o_road
          ,o_piket
      INTO t_beg
          ,road_name
          ,piket_beg
      FROM get_obj_last_activity(i_spob_id,dt1)
    ;
    t_works     := 'Нет связи';
    t_beg       := COALESCE(t_beg,dt1 - interval '7 day');
    t_end       := dt2;
    t_int_int   := EXTRACT(EPOCH FROM dt2)::int8 - EXTRACT(EPOCH FROM t_beg)::int8;
    s_speed     := 0;
    n_dist_dist := 0;
    s_speed     := 0;
    piket_end   := piket_beg;
    RETURN NEXT            
           get_rep_string(rec_row
                         ,t_works
                         ,t_beg
                         ,t_end
                         ,t_int_int
                         ,road_name
                         ,n_dist_dist
                         ,s_speed
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
  END IF;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION rep_005(i_spob_id int8, dt1 "timestamp", dt2 "timestamp") OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION rep_005(i_spob_id int8, dt1 "timestamp", dt2 "timestamp") TO public;
GRANT EXECUTE ON FUNCTION rep_005(i_spob_id int8, dt1 "timestamp", dt2 "timestamp") TO owner_track;
GRANT EXECUTE ON FUNCTION rep_005(i_spob_id int8, dt1 "timestamp", dt2 "timestamp") TO track_server_all;
GRANT EXECUTE ON FUNCTION rep_005(i_spob_id int8, dt1 "timestamp", dt2 "timestamp") TO owner_dev;
COMMENT ON FUNCTION rep_005(i_spob_id int8, dt1 "timestamp", dt2 "timestamp") IS 'Статистика движения объекта за интервал дат';
