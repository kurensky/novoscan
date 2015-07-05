
CREATE OR REPLACE FUNCTION close_module
 (n_spmd INT8 -- ид модуля
 ,d_date TIMESTAMP -- дата закрытия
 ) RETURNS INTEGER
  AS
$BODY$DECLARE
BEGIN
  UPDATE sprv_modules 
     SET spmd_dt_close = d_date
   WHERE spmd_id = n_spmd
     AND (spmd_dt_close IS NULL OR spmd_dt_close > d_date)
   ; 
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Module "%" not found',n_spmd;
  END IF;
  RETURN 0;
END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION close_module
 (INT8
 ,TIMESTAMP
 )
 IS 'Процедура снятия модуля';

REVOKE ALL ON FUNCTION close_module
 (INT8
 ,TIMESTAMP
 ) FROM public;

GRANT EXECUTE ON FUNCTION close_module
 (INT8
 ,TIMESTAMP
 ) TO t03_sprv_dev;
