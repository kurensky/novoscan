-- Function: add_sensor(sn_uin float8, sn_spst int8, sn_spmd int8, sn_name "varchar", sn_desc "varchar")

-- DROP FUNCTION add_sensor(sn_uin float8, sn_spst int8, sn_spmd int8, sn_name "varchar", sn_desc "varchar");

CREATE OR REPLACE FUNCTION add_sensor
 (sn_uin float8
 ,sn_spst int8
 ,sn_spmd int8
 ,sn_name varchar
 ,sn_desc varchar
 ) RETURNS int8 AS
$BODY$
DECLARE
  n_sn int8;
BEGIN
  SELECT COUNT(*) 
    INTO n_sn
    FROM sprv_sensors 
        ,sprv_modules
   WHERE (UPPER(spsn_name)=UPPER(trim(sn_name)) OR spsn_uin = sn_uin)
     AND spsn_spmd_id = spmd_id
     AND spmd_dt_close IS NULL
   ;
  IF n_sn = 0 THEN
    SELECT nextval('spsn_seq') INTO n_sn;
    INSERT INTO sprv_sensors
     (spsn_id
     ,spsn_uin
     ,spsn_name
     ,spsn_desc
     ,spsn_spst_id
     ,spsn_spmd_id
     ) VALUES 
     (n_sn
     ,sn_uin
     ,trim(sn_name)
     ,sn_desc
     ,sn_spst
     ,sn_spmd
     );
  ELSE
    n_sn := NULL;
    ROLLBACK;
    RAISE EXCEPTION 'Sensor whith name="%" or uid="%" exist or NULL', md_name, md_uid;
  END IF;
  RETURN n_sn;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION add_sensor
 (sn_uin float8
 ,sn_spst int8
 ,sn_spmd int8
 ,sn_name varchar
 ,sn_desc varchar
 ) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION add_sensor
 (sn_uin float8
 ,sn_spst int8
 ,sn_spmd int8
 ,sn_name varchar
 ,sn_desc varchar
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION add_sensor
 (sn_uin float8
 ,sn_spst int8
 ,sn_spmd int8
 ,sn_name varchar
 ,sn_desc varchar
 ) TO t03_sprv_dev;


REVOKE ALL ON FUNCTION add_sensor
 (sn_uin float8
 ,sn_spst int8
 ,sn_spmd int8
 ,sn_name varchar
 ,sn_desc varchar
 ) FROM public;

COMMENT ON FUNCTION add_sensor
 (sn_uin float8
 ,sn_spst int8
 ,sn_spmd int8
 ,sn_name varchar
 ,sn_desc varchar
 ) IS 'Процедура добавления сенсора, sn_uin - УИН сенсора,  sn_spst - тип сенсора, sn_spmd - ид модуля, sn_name - Наименование датчика, sn_desc - Описание датчика';
