
CREATE OR REPLACE FUNCTION get_road_name_piket
 (f_x IN FLOAT -- координата X
 ,f_y IN FLOAT -- координата Y
 ,f_z IN FLOAT -- координата Z
 ,v_road_name OUT VARCHAR -- имя дороги
 ,v_piket OUT NUMERIC -- пикет
 ) AS
$BODY$
DECLARE
  point_id INT8;
BEGIN
  point_id    := get_point(f_x,f_y,f_z);
  IF point_id IS NOT NULL THEN
     v_piket       := get_piket(point_id);
     v_road_name   := get_road_name_by_point(point_id);
  ELSE
     v_piket       := NULL;
     v_road_name   := 'Неизв.';
  END IF;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_road_name_piket
 (FLOAT -- координата X
 ,FLOAT -- координата Y
 ,FLOAT -- координата Z
 ,OUT VARCHAR -- имя дороги
 ,OUT NUMERIC -- пикет
 ) IS 'Процедура наименования дороги и имя пикета точки';

REVOKE ALL ON FUNCTION get_road_name_piket
 (FLOAT -- координата X
 ,FLOAT -- координата Y
 ,FLOAT -- координата Z
 ,OUT VARCHAR -- имя дороги
 ,OUT NUMERIC  -- пикет
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_road_name_piket
 (FLOAT -- координата X
 ,FLOAT -- координата Y
 ,FLOAT -- координата Z
 ,OUT VARCHAR -- имя дороги
 ,OUT NUMERIC  -- пикет
 ) TO t03_sprv_dev;


--- Перегружаемая функция

CREATE OR REPLACE FUNCTION get_road_name_piket
 (IN F_X FLOAT8
 ,IN F_Y FLOAT8
 ,IN F_Z FLOAT8
 ,IN GSOB_ID INT4
 ,OUT O_GSOB INT4
 ,OUT V_ROAD_NAME VARCHAR
 ,OUT V_PIKET NUMERIC 
 ) AS
$BODY$
DECLARE
  point_id INT8;
  gs CURSOR(f_x FLOAT, f_y FLOAT, f_z FLOAT, n_rad FLOAT, n_gsob INT4)
  IS
    SELECT d.gsdt_id
          ,o.gsob_name
          ,o.gsob_id
          ,d.gsdt_piket 
      FROM gis_data d
          ,gis_objects o
          ,gis_types t
     WHERE d.gsdt_gsob_id = o.gsob_id
       AND o.gsob_gstp_id = t.gstp_id
       AND o.gsob_id      = n_gsob
       AND t.gstp_code    = 'ROAD'
       AND d.gsdt_point_geom&&Expand(GeomFromEWKT('SRID=4326;POINT('||f_x||' '||f_y||' '||f_z||')'),n_rad) 
       AND Distance(GeomFromEWKT('SRID=4326;POINT('||f_x||' '||f_y||' '||f_z||')'),d.gsdt_point_geom) < n_rad
       ORDER BY Distance(GeomFromEWKT('SRID=4326;POINT('||f_x||' '||f_y||' '||f_z||')'),d.gsdt_point_geom)
       ;
  gs1 CURSOR(f_x FLOAT, f_y FLOAT, f_z FLOAT, n_rad FLOAT)
  IS
    SELECT d.gsdt_id
          ,o.gsob_name
          ,o.gsob_id
          ,d.gsdt_piket 
      FROM gis_data d
          ,gis_objects o
          ,gis_types t
     WHERE d.gsdt_gsob_id = o.gsob_id
       AND o.gsob_gstp_id = t.gstp_id
       AND t.gstp_code    = 'ROAD'
       AND d.gsdt_point_geom&&Expand(GeomFromEWKT('SRID=4326;POINT('||f_x||' '||f_y||' '||f_z||')'),n_rad) 
       AND Distance(GeomFromEWKT('SRID=4326;POINT('||f_x||' '||f_y||' '||f_z||')'),d.gsdt_point_geom) < n_rad
       ORDER BY Distance(GeomFromEWKT('SRID=4326;POINT('||f_x||' '||f_y||' '||f_z||')'),d.gsdt_point_geom)
       ;
BEGIN
  IF gsob_id IS NOT NULL THEN
    OPEN gs(f_x,f_y,f_z,get_sysvarf('PIKET_RADIUS'),gsob_id);
    FETCH gs INTO point_id, v_road_name, o_gsob, v_piket;
    CLOSE gs;
  END IF;
  IF point_id IS NULL THEN
   -- не нашли точки принадлежащей дороге
   -- ищем любой пикет ближайший
    OPEN gs1(f_x,f_y,f_z,get_sysvarf('PIKET_RADIUS'));
    FETCH gs1 INTO point_id, v_road_name, o_gsob, v_piket;
    CLOSE gs1;
  END IF;
-------------------------------------
  IF point_id IS NULL THEN
   -- вообще не нашли точки в радиусе
    v_piket       := NULL;
    v_road_name   := 'Неизв.';
    point_id      := NULL;
    o_gsob        := NULL;
  END IF;
------------------------------------
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_road_name_piket
 (IN FLOAT8
 ,IN FLOAT8
 ,IN FLOAT8
 ,IN INT4
 ,OUT INT4
 ,OUT VARCHAR
 ,OUT NUMERIC
 ) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_road_name_piket
 (IN FLOAT8
 ,IN FLOAT8
 ,IN FLOAT8
 ,IN INT4
 ,OUT INT4
 ,OUT VARCHAR
 ,OUT NUMERIC
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION get_road_name_piket
 (IN FLOAT8
 ,IN FLOAT8
 ,IN FLOAT8
 ,IN INT4
 ,OUT INT4
 ,OUT VARCHAR
 ,OUT NUMERIC
 ) TO t03_sprv_dev;
COMMENT ON FUNCTION get_road_name_piket
 (IN FLOAT8
 ,IN FLOAT8
 ,IN FLOAT8
 ,IN INT4
 ,OUT INT4
 ,OUT VARCHAR
 ,OUT NUMERIC
 ) IS 'Процедура наименования дороги и имя пикета точки';
