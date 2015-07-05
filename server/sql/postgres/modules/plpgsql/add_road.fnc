
CREATE OR REPLACE FUNCTION add_road
 (ROAD_NAME VARCHAR -- имя 
 ,ROAD_DESC VARCHAR -- описание
 ,ROAD_STATUS INT4 -- код ошибки из таблицы gis_errors
 ,ROAD_BEG FLOAT  -- атрибут начала
 ,ROAD_END FLOAT  -- атрибут конца
 ,ROAD_CODE INT4    -- код дороги
 ) RETURNS int4 AS
$BODY$

DECLARE
  n_gsob int4;
BEGIN
  SELECT GSOB_ID INTO n_gsob FROM GIS_OBJECTS WHERE GSOB_CODE=road_code;
  IF n_gsob IS NOT NULL THEN
    RAISE EXCEPTION 'Road whith code="%" exist or NULL', road_code;
  END IF;
  SELECT nextval('gsob_seq') INTO n_gsob;
  BEGIN
	  INSERT INTO GIS_OBJECTS
	  (GSOB_ID
	  ,GSOB_NAME
	  ,GSOB_DESC
	  ,GSOB_CODE
	  ,GSOB_GSTP_ID
	  ,GSOB_GSER_ID
	  ,GSOB_DT
	  ) VALUES 
	  (n_gsob
	  ,road_name
	  ,road_desc
	  ,road_code
	  ,1
	  ,road_status
	  ,now()
	  );
	  IF road_beg IS NOT NULL OR road_end IS NOT NULL THEN
	    INSERT INTO GIS_OBJECTS_ATTR
	    (GSAT_ID
	    ,GSAT_GSTT_ID
	    ,GSAT_ATTR1
	    ,GSAT_ATTR2
	    ,GSAT_GSOB_ID
	    ) VALUES 
	    (nextval('gsat_seq')
	    ,1
	    ,road_beg
	    ,road_end
            ,n_gsob
	    );
	  END IF;
	  RETURN n_gsob;
  EXCEPTION 
     WHEN unique_violation THEN
	  ROLLBACK;
	  RETURN NULL;
  END;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;

GRANT EXECUTE ON FUNCTION add_road
 (VARCHAR
 ,VARCHAR
 ,INT4
 ,FLOAT
 ,FLOAT
 ,INT4
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION add_road
  (VARCHAR
 ,VARCHAR
 ,INT4
 ,FLOAT
 ,FLOAT
 ,INT4
 ) TO t03_sprv_dev;


REVOKE ALL ON FUNCTION add_road
 (VARCHAR
 ,VARCHAR
 ,INT4
 ,FLOAT
 ,FLOAT
 ,INT4
 ) FROM public;

COMMENT ON FUNCTION add_road
 (VARCHAR
 ,VARCHAR
 ,INT4
 ,FLOAT
 ,FLOAT
 ,INT4
 ) IS 'Процедура добавления дороги';