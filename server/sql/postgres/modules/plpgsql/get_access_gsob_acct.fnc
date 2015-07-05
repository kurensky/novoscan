
CREATE OR REPLACE FUNCTION get_access_gsob_acct(i_gsob integer, i_acct bigint)
  RETURNS integer AS
$BODY$DECLARE
  is_grant_object CURSOR (gsob integer, acct bigint)
    FOR
      SELECT 1
        FROM account_lists
       WHERE accl_ref_type2 = 200
         AND accl_ref_id2 = gsob
	 AND accl_ref_type1 = 100
	 AND accl_ref_id1 = acct
        LIMIT 1 
       ;

  ret INTEGER;
BEGIN
  OPEN is_grant_object(i_gsob, i_acct);
  FETCH is_grant_object INTO ret;
  CLOSE is_grant_object;
  IF ret IS NULL THEN
    ret := 0; 
  END IF;
  RETURN ret;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION get_access_gsob_acct(i_gsob integer, i_acct bigint)
  OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_access_gsob_acct(i_gsob integer, i_acct bigint) TO public;
GRANT EXECUTE ON FUNCTION get_access_gsob_acct(i_gsob integer, i_acct bigint) TO owner_track;
GRANT EXECUTE ON FUNCTION get_access_gsob_acct(i_gsob integer, i_acct bigint) TO track_server_all;
COMMENT ON FUNCTION get_access_gsob_acct(i_gsob integer, i_acct bigint) IS 'Проверка доступа к GSOB по acct';
