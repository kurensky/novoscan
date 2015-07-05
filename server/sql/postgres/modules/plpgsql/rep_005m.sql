DECLARE
--- Справочные значения
  min_speed_cur float := get_sysvarf('MIN_SPEED');
  min_interval  float := get_sysvarf('MIN_INTERVAL');
  dt_format     text  := get_sysvarc('NLS_DATE_FORMAT');
  pi            float := get_constf('PI'); --  число PI
  ra            float := get_constf('PI')/180.;
--------
  rec         record;
  rec_row     integer;
  road_count  integer;
  -- Дороги
--
  dist          float;
  dist_summ_cur float;
-- Последние полученные значения
  lat_cur     float;
  lon_cur     float;
  lat_rad_cur float;
  lon_rad_cur float;
  alt_cur     float;
  piket_cur   gis_data.gsdt_piket%TYPE;
  road_cur    gis_objects.gsob_name%TYPE; -- текущее вычисленное имя дороги
  tm_cur      timestamp;
  speed_cur   float;
  gsob_cur    int4;
  time_cur    int8;
-- Значения полученные на 1 шаге назад
  lat_pre1     float;
  lon_pre1     float;
  lat_rad_pre1 float;
  lon_rad_pre1 float;
  alt_pre1     float;
  piket_pre1   gis_data.gsdt_piket%TYPE;
  road_pre1    gis_objects.gsob_name%TYPE; 
  tm_pre1      timestamp;
  speed_pre1   float;
  gsob_pre1    int4;
-- Значения полученные на 2 шаге назад
  lat_pre2     float;
  lon_pre2     float;
  lat_rad_pre2 float;
  lon_rad_pre2 float;
  alt_pre2     float;
  piket_pre2   gis_data.gsdt_piket%TYPE;
  road_pre2    gis_objects.gsob_name%TYPE; 
  tm_pre2      timestamp;
  speed_pre2   float;
  gsob_pre2    int4;
-- Значения используемые при выводе
  piket_beg gis_data.gsdt_piket%TYPE;
  piket_end gis_data.gsdt_piket%TYPE;
  lat_beg   float;
  lon_beg   float;
  lat_end   float;
  lon_end   float;
  date_beg  timestamp;
  date_end  timestamp;

-- Статусы движения объекта
  works        text;
  works_status integer;
  status_new   integer := 0;
  status_move  integer := 1;
  status_stop  integer := 2;
-- Промежуточные результаты
  
  delta_time      int8;
--
BEGIN
  lat_cur := NULL;
  lon_cur := NULL;
  alt_cur := NULL;
  dist  := 0;
  rec_row := 0;
  piket_beg := NULL;
  piket_end := NULL;
--  RETURN NEXT '?;Событие;Начало;Конец;Время;Дорога;Протяженность;Средняя скорость;Пикет';
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
    lat_cur     := rec.latitude;
    lon_cur     := rec.longitude;
    alt_cur     := rec.z;
    tm_cur      := rec.datetime;
    lat_rad_cur := lat_cur*ra;
    lon_rad_cur := lon_cur*ra;
    IF lat_cur IS NOT NULL AND lat_pre1 IS NOT NULL THEN
       --Вычисление дистанции
       dist := cal_distance(lat_rad_pre1, lon_rad_pre1, lat_rad_cur, lon_rad_cur, alt_pre1, alt_cur);
       --Вычисление интервала движения
       delta_time := EXTRACT(EPOCH FROM tm_cur)::int8 - EXTRACT(EPOCH FROM tm_pre1)::int8;
       -- Определение средней скорости
       IF delta_time = 0 THEN
         speed_cur := 0;
       ELSE
         speed_cur := round((dist*3.6/delta_time)::numeric,2);
       END IF;
       -- Отсеим с интервалом менее 10 сек (защитный интервал)
       IF delta_time < min_interval THEN
         CONTINUE;
       -- Если скорость более 1 км в час или интервал передачи данных менее 280 скунд - то машина движется
       ELSIF speed_cur > min_speed_cur OR delta_time < 280 THEN
    -- Геграфия
         IF works_status = status_new THEN
           works_status      := status_move;
           works             := 'Движение';
           date_end          := tm_cur;
           lat_end           := lat_rad_cur;
           lon_end           := lon_rad_cur;
           piket_beg         := piket_end::numeric;
           road_pre1         := road_cur;
           time_cur          := EXTRACT(EPOCH FROM date_end)::int8 - EXTRACT(EPOCH FROM date_beg)::int8;
           dist_summ_cur     := dist_summ_cur + dist;
         ELSIF works_status <> status_move THEN
           dist_summ_cur := 0;
           speed_cur     := 0;
           rec_row   := rec_row + 1;
           RETURN NEXT
           get_rep_string(rec_row
                         ,works
                         ,date_beg
                         ,date_end
                         ,time_cur
                         ,road_cur
                         ,dist_summ_cur
                         ,speed_cur
                         ,lat_end
                         ,lon_end
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
           SELECT o_gsob,v_piket,v_road_name INTO gsob_cur,piket_cur,road_cur FROM get_road_name_piket(lat_pre1,lon_pre1,alt_pre1,gsob_cur);
           piket_beg         := piket_cur::numeric;
           piket_end         := piket_cur::numeric;
           works_status      := status_move;
           works             := 'Движение';
           date_beg          := tm_pre1;
           date_end          := tm_cur;
           time_cur          := EXTRACT(EPOCH FROM date_end)::int8 - EXTRACT(EPOCH FROM date_beg)::int8;
           dist_summ_cur     := 0;
           road_pre1         := road_cur;
         ELSE
           --------------
           SELECT o_gsob,v_piket,v_road_name INTO gsob_cur,piket_cur,road_cur FROM get_road_name_piket(lat_cur,lon_cur,alt_cur,gsob_cur);
           --------------
           IF gsob_pre1 != gsob_cur THEN
             IF road_count = 0 THEN
               date_end      := tm_cur;
               lat_end       := lat_rad_cur;
               lon_end       := lon_rad_cur;
           --    piket_pre1    := piket_end;
               piket_end     := piket_cur::numeric;
               road_count    := road_count + 1;
               time_cur      := EXTRACT(EPOCH FROM date_end)::int8 - EXTRACT(EPOCH FROM date_beg)::int8;
               dist_summ_cur := dist_summ_cur + dist;
             ELSE
               IF time_cur = 0 THEN
                 speed_cur := 0;
               ELSE
                 speed_cur := round((dist_summ_cur*3.6/time_cur)::numeric,2);
               END IF;
               rec_row     := rec_row + 1;
               RETURN NEXT
               get_rep_string(rec_row
                           ,works
                           ,date_beg
                           ,date_end
                           ,time_cur
                           ,road_pre1
                           ,dist_summ_cur
                           ,speed_cur
                           ,lat_beg
                           ,lon_beg
                           ,lat_end
                           ,lon_end
                           ,piket_beg
                           ,piket_pre1
                           );
               date_beg      := tm_pre1;
               date_end      := tm_cur;
               lat_beg       := lat_end;
               lon_beg       := lon_end;
               piket_beg     := piket_cur::numeric;
               piket_end     := piket_cur::numeric;
               road_count    := 0;
               road_pre1     := road_cur;
               time_cur      := EXTRACT(EPOCH FROM date_end)::int8 - EXTRACT(EPOCH FROM date_beg)::int8;
               dist_summ_cur := 0;
            END IF;
         ELSE
             date_end      := tm_cur;
             lat_end       := lat_rad_cur;
             lon_end       := lon_rad_cur;
             piket_end     := piket_cur::numeric;
             road_count    := 0;
             road_pre1     := road_cur;
             time_cur      := EXTRACT(EPOCH FROM date_end)::int8 - EXTRACT(EPOCH FROM date_beg)::int8;
             dist_summ_cur := dist_summ_cur + dist;
           END IF;
         END IF;
       ELSE
         IF works_status = status_new THEN
           works_status  := status_stop;
           works         := 'Стоянка';
           date_end      := tm_cur;
           lat_end       := lat_rad_cur;
           lon_end       := lon_rad_cur;
           piket_end     := piket_cur::numeric;
           time_cur      := EXTRACT(EPOCH FROM date_end)::int8 - EXTRACT(EPOCH FROM date_beg)::int8;
           dist_summ_cur := 0;
         ELSIF works_status <> status_stop THEN
           IF time_cur = 0 THEN
             speed_cur := 0;
           ELSE
             speed_cur := round((dist_summ_cur*3.6/time_cur)::numeric,2);
           END IF;
           rec_row := rec_row + 1;
           RETURN NEXT
           get_rep_string(rec_row
                         ,works
                         ,date_beg
                         ,date_end
                         ,time_cur
                         ,road_cur
                         ,dist_summ_cur
                         ,speed_cur
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
           works         := 'Стоянка';
           works_status  := status_stop;
           date_beg      := tm_pre1;
           date_end      := tm_cur;
           lat_beg       := lat_end;
           lon_beg       := lon_end;
           piket_beg     := piket_cur::numeric;
           piket_end     := piket_cur::numeric;
           time_cur      := EXTRACT(EPOCH FROM date_end)::int8 - EXTRACT(EPOCH FROM date_beg)::int8;
           dist_summ_cur := dist_summ_cur + dist;
         ELSE
           date_end      := tm_cur;
           lat_beg       := lat_rad_cur;
           lon_beg       := lon_rad_cur;
           lat_end       := lat_rad_cur;
           lon_end       := lon_rad_cur;
           time_cur      := EXTRACT(EPOCH FROM date_end)::int8 - EXTRACT(EPOCH FROM date_beg)::int8;
           dist_summ_cur := 0;
         END IF;
     END IF;
    ELSE
       SELECT o_gsob,v_piket,v_road_name INTO gsob_cur,piket_cur,road_cur FROM get_road_name_piket(lat_cur,lon_cur,alt_cur,gsob_cur);
       works         := 'Первая точка';
       works_status  := status_new;
       date_beg      := tm_cur;
       date_end      := tm_cur;
       dist          := 0;
       delta_time    := 0;
       speed_cur     := 0;
       dist_summ_cur := 0;
       time_cur      := 0;
       rec_row       := rec_row + 1;
       lat_beg       := lat_rad_cur;
       lat_end       := lat_rad_cur;
       lon_beg       := lon_rad_cur;
       lon_end       := lon_rad_cur;
       piket_beg     := piket_cur::numeric;
       piket_end     := piket_cur::numeric;
       RETURN NEXT
           get_rep_string(rec_row
                         ,works
                         ,date_beg
                         ,date_end
                         ,time_cur
                         ,road_cur
                         ,dist_summ_cur
                         ,speed_cur
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
    END IF;
    lat_pre1       := lat_cur;
    lon_pre1       := lon_cur;
    lat_rad_pre1   := lat_rad_cur;
    lon_rad_pre1   := lon_rad_cur;
    alt_pre1       := alt_cur;
    tm_pre1        := tm_cur;
  --  piket_pre1     := piket_cur;
  END LOOP;
/* ########################################################################################################## */
  -- Обработка последней точки
  IF rec_row <> 0 THEN
    -- если записи были
    rec_row     := rec_row + 1;
    RETURN NEXT
           get_rep_string(rec_row
                         ,works
                         ,date_beg
                         ,date_end
                         ,time_cur
                         ,road_cur
                         ,dist_summ_cur
                         ,speed_cur
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
    IF now() < dt2 THEN
      tm_pre1 := now();
    ELSE
      tm_pre1 := dt2;
    END IF;
    time_cur      := (EXTRACT(EPOCH FROM tm_pre1)::int8 - EXTRACT(EPOCH FROM date_end)::int8);
    date_beg      := date_end;
    date_end      := tm_pre1;
    dist_summ_cur := 0;
    IF works_status = status_move AND time_cur > 360 THEN
      -- если перед этим было движение и интервал  превысил 6 минут поставим стоянку
      works_status  := status_stop;
      works         := 'Стоянка';
      speed_cur     := 0;
      dist_summ_cur := 0;
      rec_row       := rec_row + 1;
      piket_beg     := piket_end::numeric;
      lat_beg       := lat_end;
      lon_beg       := lon_end;
      RETURN NEXT
           get_rep_string(rec_row
                         ,works
                         ,date_beg
                         ,date_end
                         ,time_cur
                         ,road_cur
                         ,dist_summ_cur
                         ,speed_cur
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
    END IF;
  ELSE
    -- нет связи
    SELECT o_dt,o_road,o_piket INTO date_beg,road_cur,piket_beg FROM get_obj_last_activity(i_spob_id,dt1);
    works         := 'Нет связи';
    date_beg      := COALESCE(date_beg,dt1 - interval '7 day');
    date_end      := dt2;
    time_cur      := EXTRACT(EPOCH FROM dt2)::int8 - EXTRACT(EPOCH FROM date_beg)::int8;
    speed_cur     := 0;
    dist_summ_cur := 0;
    speed_cur     := 0;
    piket_end     := piket_beg::numeric;
    RETURN NEXT 
           get_rep_string(rec_row
                         ,works
                         ,date_beg
                         ,date_end
                         ,time_cur
                         ,road_cur
                         ,dist_summ_cur
                         ,speed_cur
                         ,lat_beg
                         ,lon_beg
                         ,lat_end
                         ,lon_end
                         ,piket_beg
                         ,piket_end
                         );
  END IF;
/* ########################################################################################################## */
END;