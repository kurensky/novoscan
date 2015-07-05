-- Function: get_access_gsob(bigint, integer)

-- DROP FUNCTION get_access_gsob(bigint, integer);

CREATE OR REPLACE FUNCTION get_access_gsob(i_spob bigint, i_gsob integer)
  RETURNS integer AS
$BODY$DECLARE
  is_grant_object CURSOR (i_spob BIGINT, i_gsob INTEGER)
    FOR
      SELECT 1
        FROM account_lists
       WHERE accl_ref_id1 =i_spob
         AND accl_ref_type1 = 101
         AND accl_ref_type2 = 200
         AND accl_ref_id2 = i_gsob
        LIMIT 1 
       ;

  ret INTEGER;
BEGIN
  OPEN is_grant_object(i_spob, i_gsob);
  FETCH is_grant_object INTO ret;
  CLOSE is_grant_object;
  IF ret IS NULL THEN
    ret := 0; 
  END IF;
  RETURN ret;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION get_access_gsob(bigint, integer)
  OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_access_gsob(bigint, integer) TO public;
GRANT EXECUTE ON FUNCTION get_access_gsob(bigint, integer) TO owner_track;
GRANT EXECUTE ON FUNCTION get_access_gsob(bigint, integer) TO track_server_all;
COMMENT ON FUNCTION get_access_gsob(bigint, integer) IS 'Проверка доступа к GSOB';
