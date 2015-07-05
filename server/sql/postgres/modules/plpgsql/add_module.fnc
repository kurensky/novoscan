
CREATE OR REPLACE FUNCTION add_module
 (MD_UID FLOAT -- UID модуля уникальный идентификатор
 ,MD_NAME VARCHAR -- имя модуля
 ,MD_IMEI VARCHAR -- IMEI SIM карты
 ,MD_NUMB VARCHAR -- номер SIM карты
 ,MD_DESC VARCHAR -- описание
 ,MD_SPOB_ID INT8 -- ссылка на объект
 ,MD_SPMT_ID INT8 -- ссылка на тип модуля
 )
  RETURNS int8 AS
$BODY$
DECLARE
  n_md int8;
BEGIN
  SELECT COUNT(*) 
    INTO n_md
    FROM sprv_modules
   WHERE (UPPER(spmd_name)=UPPER(trim(md_name)) OR spmd_uid = md_uid)
   ;
  IF n_md = 0 THEN
    SELECT nextval('spmd_seq') INTO n_md;
    INSERT INTO sprv_modules
	(SPMD_ID
	,SPMD_UID
	,SPMD_NAME
	,SPMD_DT_CREATE
	,SPMD_DT_CLOSE
	,SPMD_USER
	,SPMD_IMEI
	,SPMD_NUMB
	,SPMD_DESC
	,SPMD_SPOB_ID
	,SPMD_SPMT_ID
	) VALUES 
	(n_md
	,md_uid
	,md_name
	,now()
	,NULL
	,USER
	,md_imei
	,md_numb
	,md_desc
	,md_spob_id
	,md_spmt_id
	);
  ELSE
    n_md := NULL;
    ROLLBACK;
    RAISE EXCEPTION 'Module whith name="%" or uid="%" exist or NULL', md_name, md_uid;
  END IF;
  RETURN n_md;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION add_module
 (md_uid float
 ,md_name varchar
 ,md_imei varchar
 ,md_numb varchar
 ,md_desc varchar
 ,md_spob_id int8
 ,md_spmt_id int8
 ) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION add_module
 (md_uid float
 ,md_name varchar
 ,md_imei varchar
 ,md_numb varchar
 ,md_desc varchar
 ,md_spob_id int8
 ,md_spmt_id int8
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION add_module
 (md_uid float
 ,md_name varchar
 ,md_imei varchar
 ,md_numb varchar
 ,md_desc varchar
 ,md_spob_id int8
 ,md_spmt_id int8
 ) TO t03_sprv_dev;


REVOKE ALL ON FUNCTION add_module
 (md_uid float
 ,md_name varchar
 ,md_imei varchar
 ,md_numb varchar
 ,md_desc varchar
 ,md_spob_id int8
 ,md_spmt_id int8
 ) FROM public;

COMMENT ON FUNCTION add_module
 (md_uid float
 ,md_name varchar
 ,md_imei varchar
 ,md_numb varchar
 ,md_desc varchar
 ,md_spob_id int8
 ,md_spmt_id int8
 ) IS 'Процедура добавления модуля';
