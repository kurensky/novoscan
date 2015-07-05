
CREATE OR REPLACE FUNCTION add_gis_point_3d
 (n_gsob INT4 -- ид объекта GIS
 ,n_gspt INT4 -- ид типа точки
 ,f_x VARCHAR -- координата X
 ,f_y VARCHAR -- координата Y
 ,f_z VARCHAR -- координата Z
 ,n_srid INT4 -- SRID данных
 ,v_piket NUMERIC -- значение пикета
 ,v_info VARCHAR -- доп. информация
 )
  RETURNS int8 AS
$BODY$
DECLARE
  n_gsdt int8;
  gpgm gis_data.gsdt_point_geom%TYPE;
  n_gis int;
BEGIN
  gpgm := GeomFromEWKT('SRID='||n_srid||';POINT('||f_x||' '||f_y||' '||f_z||')');
  n_gis := NULL;
  SELECT gsdt_id INTO n_gis FROM gis_data WHERE gsdt_point_geom = gpgm LIMIT 1;
  IF n_gis IS NOT NULL THEN
    RAISE EXCEPTION 'Point whith "SRID=%;POINT(% % %)"  exist. gsdt_id="%"',n_srid, f_x, f_y, f_z, n_gis;
  END IF;
  SELECT nextval('gsdt_seq') INTO n_gsdt;
  INSERT INTO gis_data
   (gsdt_id
   ,gsdt_dt -- Дата создания
   ,gsdt_gsob_id -- ссылка на GIS_OBJECTS
   ,gsdt_gspt_id -- признак "замечательной" точки (начало дороги, ИССО, перекрестки, примыкания и т.д.) 0 - обычная точка, другие значения - "замечательная" точка
   ,gsdt_info -- Дополнительная информация
   ,gsdt_piket -- Пикетажное положение
   ,gsdt_line_geom
   ,gsdt_point_geom -- 3 мерная точка
   ) VALUES 
   (n_gsdt
   ,now()
   ,n_gsob
   ,n_gspt
   ,v_info
   ,v_piket
   ,NULL
   ,gpgm
   );
  RETURN n_gsdt;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION add_gis_point_3d
 (INT4
 ,INT4
 ,VARCHAR
 ,VARCHAR
 ,VARCHAR
 ,INT4
 ,NUMERIC
 ,VARCHAR
 )
 IS 'Процедура добавления в справочник GIS трёхмерной точки';

REVOKE ALL ON FUNCTION add_gis_point_3d
 (INT4
 ,INT4
 ,VARCHAR
 ,VARCHAR
 ,VARCHAR
 ,INT4
 ,NUMERIC
 ,VARCHAR
 ) FROM public;

GRANT EXECUTE ON FUNCTION add_gis_point_3d
 (INT4
 ,INT4
 ,VARCHAR
 ,VARCHAR
 ,VARCHAR
 ,INT4
 ,NUMERIC
 ,VARCHAR
 ) TO t03_sprv_dev;
