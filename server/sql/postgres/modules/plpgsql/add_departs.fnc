
CREATE OR REPLACE FUNCTION add_departs
 (dp_name varchar -- Наименование департамента
 )
  RETURNS int8 AS
$BODY$DECLARE
  n_dp int8;
BEGIN
  SELECT COUNT(*) INTO n_dp
    FROM sprv_departs 
   WHERE UPPER(spdp_name)=UPPER(trim(dp_name))
   ;
  IF n_dp = 0 THEN
    SELECT nextval('spdp_seq') INTO n_dp;
    INSERT INTO sprv_departs(spdp_id,spdp_name) VALUES (n_dp,trim(dp_name));
  ELSE
    n_dp := NULL;
  END IF;
  RETURN n_dp;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION add_departs
 (dp_name varchar 
 )
 IS 'Процедура добавления предприятия';

REVOKE ALL ON FUNCTION add_departs
 (dp_name varchar 
 ) FROM public;

GRANT EXECUTE ON FUNCTION add_departs
 (dp_name varchar -- 
 ) TO GROUP T03_SPRV_DEV;
