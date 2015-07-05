-- Function: add_object(cl_id int8, ot_id int8, ob_name "varchar", ob_desc "varchar")

-- DROP FUNCTION add_object(cl_id int8, ot_id int8, ob_name "varchar", ob_desc "varchar");

CREATE OR REPLACE FUNCTION add_object
 (cl_id int8
 ,ot_id int8
 ,ob_name varchar
 ,ob_desc varchar
 )
  RETURNS int8 AS
$BODY$
DECLARE
  n_ob int8;
BEGIN
  SELECT COUNT(*) 
    INTO n_ob
    FROM sprv_objects 
   WHERE UPPER(spob_name)=UPPER(trim(ob_name))
     AND spob_dt_close IS NULL
   ;
  IF n_ob = 0 THEN
    SELECT nextval('spob_seq') INTO n_ob;
    INSERT INTO sprv_objects
     (spob_id
     ,spob_spcl_id
     ,spob_spot_id
     ,spob_name
     ,spob_desc
     ,spob_dt_create
     ,spob_dt_modify
     ) VALUES 
     (n_ob
     ,cl_id
     ,ot_id
     ,trim(ob_name)
     ,ob_desc
     ,now()
     ,now()
     );
  ELSE
    n_ob := NULL;
  END IF;
  RETURN n_ob;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION add_object
 (cl_id int8
 ,ot_id int8
 ,ob_name varchar
 ,ob_desc varchar
 ) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION add_object
 (cl_id int8
 ,ot_id int8
 ,ob_name varchar
 ,ob_desc varchar
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION add_object
 (cl_id int8
 ,ot_id int8
 ,ob_name varchar
 ,ob_desc varchar
 ) TO t03_sprv_dev;

REVOKE ALL ON FUNCTION add_object
 (cl_id int8
 ,ot_id int8
 ,ob_name varchar
 ,ob_desc varchar
 ) FROM public;

COMMENT ON FUNCTION add_object
 (cl_id int8
 ,ot_id int8
 ,ob_name varchar
 ,ob_desc varchar
 ) IS 'Процедура добавления объекта, cl_id - ид клиента,  ot_id -  ид типа объекта, ob_name - имя объекта, ob_desc - описание объекта';
