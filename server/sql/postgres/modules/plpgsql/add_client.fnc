CREATE OR REPLACE FUNCTION add_client
 (dp_id int8
 ,cl_name varchar
 ,cl_desc varchar
 ,cl_type int8
 )
 RETURNS int8 AS
$BODY$
DECLARE
  n_cl int8;
  n_tp int8;
BEGIN
  SELECT COUNT(*) 
    INTO n_cl
    FROM sprv_clients 
   WHERE UPPER(spcl_name)=UPPER(trim(cl_name))
   ;
  IF cl_type != 0 THEN
    n_tp := 1;
  ELSE
    n_tp := cl_type;
  END IF;
  IF n_cl = 0 THEN
    SELECT nextval('spcl_seq') INTO n_cl;
    INSERT INTO sprv_clients
     (spcl_id
     ,spcl_spdp_id
     ,spcl_name
     ,spcl_desc
     ,spcl_type
     ) VALUES 
     (n_cl
     ,dp_id
     ,trim(cl_name)
     ,cl_desc
     ,n_tp
     );
  ELSE
    n_cl := NULL;
  END IF;
  RETURN n_cl;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;

ALTER FUNCTION add_client
 (dp_id int8
 ,cl_name varchar
 ,cl_desc varchar
 ,cl_type int8
 ) OWNER TO owner_track;

REVOKE EXECUTE ON FUNCTION add_client
 (dp_id int8
 ,cl_name varchar
 ,cl_desc varchar
 ,cl_type int8
 ) FROM public;

GRANT EXECUTE ON FUNCTION add_client
 (dp_id int8
 ,cl_name varchar
 ,cl_desc varchar
 ,cl_type int8
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION add_client
 (dp_id int8
 ,cl_name varchar
 ,cl_desc varchar
 ,cl_type int8
 ) TO t03_sprv_dev;

REVOKE ALL ON FUNCTION add_client
 (dp_id int8
 ,cl_name varchar
 ,cl_desc varchar
 ,cl_type int8
 ) FROM public;

COMMENT ON FUNCTION add_client
 (dp_id int8
 ,cl_name varchar
 ,cl_desc varchar
 ,cl_type int8
 ) IS 'Процедура добавления клиента, cl_name - имя,  cl_desc - описание, cl_type - 0 - население 1 - предприятие';
