
CREATE OR REPLACE FUNCTION get_acl_info
 (i_acct bigint
 ) RETURNS text AS
$BODY$

DECLARE
  resp	  TEXT;
  rec     RECORD;
  rrow	  BIGINT;
BEGIN
  resp := '';
  rrow := 0;
  resp := resp||'<DEPARTS>';
  FOR rec IN 
  SELECT spdp_id
        ,spdp_name
    FROM mv_departs
   WHERE acct_id = i_acct
   ORDER BY spdp_id
  LOOP
    rrow := rrow + 1;
    resp := resp||'<xId ref="'||rec.spdp_id::text||'"><xName>'
                ||escape_reference(rec.spdp_name::text)
                ||'</xName></xId>';
  END LOOP;
  resp := resp||'<ROWS>'||rrow::text||'</ROWS>';
  resp := resp||'</DEPARTS>';
  rrow := 0;
  resp := resp||'<CLIENTS>';
  FOR rec IN 
  SELECT spcl_id
        ,spcl_name
        ,spcl_type_name
        ,spcl_desc
	,spcl_spdp_id
    FROM mv_clients
   WHERE acct_id = i_acct
   ORDER BY spcl_id
  LOOP
    rrow := rrow + 1;
    resp := resp||'<xId ref="'||rec.spcl_id::text||'" depa="'||rec.spcl_spdp_id||'"><xName>'
                ||escape_reference(rec.spcl_name::text)||'</xName><xType>'
                ||escape_reference(rec.spcl_type_name::text)||'</xType><xDesc>'
                ||escape_reference(rec.spcl_type_name::text)||'</xDesc></xId>'
             ;
  END LOOP;
  resp := resp||'<ROWS>'||rrow::text||'</ROWS>';
  resp := resp||'</CLIENTS>';
  
  RETURN '<?xml version="1.0" encoding="UTF-8"?><RESPONSE>'||resp||'</RESPONSE>';
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_acl_info
 (bigint
 ) OWNER TO owner_track;
COMMENT ON FUNCTION get_acl_info
 (bigint
 ) IS 'Информация по правам доступа к справочной информации';
