-- Function: init_last_activity()

-- DROP FUNCTION init_last_activity();

CREATE OR REPLACE FUNCTION init_last_activity()
  RETURNS integer AS
$BODY$
DECLARE
  rec RECORD;
  n_dasl_id INT8;
  i_row INTEGER;

BEGIN
 i_row := 0;
 FOR rec IN (SELECT * 
               FROM data_sensor
              WHERE dasn_id IN
              (SELECT max(dasn_id) as id
                 FROM data_sensor 
                 GROUP BY dasn_vehicle
                 ORDER BY 1
              )
             ) LOOP
   SELECT nextval('dasl_seq') INTO n_dasl_id;
   INSERT INTO data_sensor_last
	  (dasl_id
	  ,dasl_uid
	  ,dasl_datetime-- Дата время с таймзоной
	  ,dasl_latitude -- Географическая долгота
	  ,dasl_longitude -- Географическая широта
	  ,dasl_status -- Флаг состояний
	  ,dasl_sat_used -- Количество спутников
	  ,dasl_zone_alarm -- Состояние тревога зон охраны
	  ,dasl_macro_id  -- Номер макроса
	  ,dasl_macro_src -- Код источника
	  ,dasl_sog  -- Скорость в м\с
	  ,dasl_course  -- Курс в градусах
	  ,dasl_hdop  -- Значение HDOP
	  ,dasl_hgeo  -- Значение HGEO
	  ,dasl_hmet  -- Значение HMET
	  ,dasl_gpio  -- Состояние входов-выходов в позиционно-битовом коде
	  ,dasl_adc -- Состояние аналоговых входов
	  ,dasl_temp  -- Температура С
	  ,dasl_type  -- Тип данных
	  ,dasl_xml -- Дополнтельные данные.
	  ,dasl_dtm  -- Дата модификации
	  ,dasl_spsn_id -- ид записи блока
	  ,dasl_vehicle -- реальный идентификатор устройства
	  ,dasl_dasn_id -- ид в data_sensor
	  ) VALUES 
	  (n_dasl_id
	  ,rec.dasn_uid
	  ,rec.dasn_datetime -- Дата время с таймзоной
	  ,rec.dasn_latitude  -- Географическая долгота
	  ,rec.dasn_longitude  -- Географическая широта
	  ,rec.dasn_status  -- Флаг состояний
	  ,rec.dasn_sat_used  -- Количество спутников
	  ,rec.dasn_zone_alarm  -- Состояние тревога зон охраны
	  ,rec.dasn_macro_id  -- Номер макроса
	  ,rec.dasn_macro_src  -- Код источника
	  ,rec.dasn_sog -- Скорость в м;с
	  ,rec.dasn_course  -- Курс в градусах
	  ,rec.dasn_hdop  -- Значение HDOP
	  ,rec.dasn_hgeo  -- Значение HGEO
	  ,rec.dasn_hmet -- Значение HMET
	  ,rec.dasn_gpio -- Состояние входов-выходов в позиционно-битовом коде
	  ,rec.dasn_adc -- Состояние аналоговых входов
	  ,rec.dasn_temp -- Температура С
	  ,rec.dasn_type  -- Тип данных
	  ,rec.dasn_xml-- Дополнтельные данные.
	  ,rec.dasn_dtm -- Дата модификации
	  ,rec.dasn_spsn_id-- ид блока
	  ,rec.dasn_vehicle -- реальный идентификатор устройства
	  ,rec.dasn_id
	  );
	  i_row := i_row + 1;
	  
 END LOOP;
 RETURN i_row;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION init_last_activity() OWNER TO owner_track;
