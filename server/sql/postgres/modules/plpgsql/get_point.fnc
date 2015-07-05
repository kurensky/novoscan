
CREATE OR REPLACE FUNCTION get_point
 (f_x FLOAT -- координата X
 ,f_y FLOAT -- координата Y
 ,f_z FLOAT -- координата Z
 )
  RETURNS int8 AS
$BODY$
DECLARE
  n_gsdt1 int8;
  gs CURSOR(n_x FLOAT, n_y FLOAT, n_z FLOAT, n_rad FLOAT)
    IS
    SELECT d.gsdt_id
      FROM gis_data d
     WHERE d.gsdt_point_geom&&Expand(GeomFromEWKT('SRID=4326;POINT('||f_x||' '||f_y||' '||f_z||')'),n_rad) 
       AND Distance(GeomFromEWKT('SRID=4326;POINT('||f_x||' '||f_y||' '||f_z||')'),d.gsdt_point_geom) < n_rad
       ORDER BY Distance(GeomFromEWKT('SRID=4326;POINT('||f_x||' '||f_y||' '||f_z||')'),d.gsdt_point_geom)
     ;

BEGIN
  OPEN gs(f_x,f_y,f_z,get_sysvarf('PIKET_RADIUS'));
  FETCH gs INTO n_gsdt1;
  CLOSE gs;
  RETURN n_gsdt1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_point
 (FLOAT
 ,FLOAT
 ,FLOAT
 )
 IS 'Процедура поиска ближайшей точки в справочнике ГИС';

REVOKE ALL ON FUNCTION get_point
 (FLOAT
 ,FLOAT
 ,FLOAT
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_point
 (FLOAT
 ,FLOAT
 ,FLOAT
 ) TO t03_sprv_dev;
