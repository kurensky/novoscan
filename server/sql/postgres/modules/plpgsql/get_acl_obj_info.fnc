CREATE OR REPLACE FUNCTION get_acl_obj_info
 (i_acct bigint
 ) RETURNS text AS
$BODY$
DECLARE
  resp	  TEXT;
  rec     RECORD;
  rrow	  BIGINT;
  lastdt  TIMESTAMP;
  
BEGIN
  resp := '';
  rrow := 0;
  FOR rec IN
  SELECT spob_id
	,spcl_id
	,spdp_id
        ,spob_name
        ,spob_desc
        ,spob_dt_create
        ,spob_dt_modify
        ,spob_dt_close
        ,MIN(accl_ref_type2) AS accl_ref_type2
    FROM (
  SELECT spob_id
	,spcl_id
	,spdp_id
        ,spob_name
        ,spob_desc
        ,spob_dt_create
        ,spob_dt_modify
        ,spob_dt_close
        ,accl_ref_type2
    FROM sprv_objects
        ,sprv_clients
        ,sprv_departs
        ,account_lists
   WHERE accl_ref_id1 = i_acct
     AND accl_ref_type1 = 100
     AND accl_ref_type2 = 101 
     AND spob_id = accl_ref_id2
     AND spob_spcl_id = spcl_id
     AND spcl_spdp_id = spdp_id
   UNION
  SELECT spob_id
	,spcl_id
	,spdp_id
        ,spob_name
        ,spob_desc
        ,spob_dt_create
        ,spob_dt_modify
        ,spob_dt_close
        ,accl_ref_type2
    FROM sprv_objects
        ,sprv_clients
        ,sprv_departs
        ,account_lists
   WHERE accl_ref_id1 = i_acct
     AND accl_ref_type1 = 100
     AND accl_ref_type2 = 103 
     AND spcl_id = accl_ref_id2
     AND spob_spcl_id = spcl_id
     AND spcl_spdp_id = spdp_id
   UNION
  SELECT spob_id
	,spcl_id
	,spdp_id
        ,spob_name
        ,spob_desc
        ,spob_dt_create
        ,spob_dt_modify
        ,spob_dt_close
        ,accl_ref_type2
    FROM sprv_objects
        ,sprv_clients
        ,sprv_departs
        ,account_lists
   WHERE accl_ref_id1 = i_acct
     AND accl_ref_type1 = 100
     AND accl_ref_type2 = 104 
     AND spdp_id = accl_ref_id2
     AND spob_spcl_id = spcl_id
     AND spcl_spdp_id = spdp_id
  ) s
  GROUP BY spob_id
          ,spcl_id
          ,spdp_id
          ,spob_name
          ,spob_desc
          ,spob_dt_create
          ,spob_dt_modify
          ,spob_dt_close
  ORDER BY spob_name
  LOOP
    rrow := rrow + 1;
    SELECT o_dt INTO lastdt
      FROM get_obj_last_point(rec.spob_id,now()::timestamp);
    resp := resp||'<OBJECT>'
                ||'<oid>'||rec.spob_id::text||'</oid>'
                ||'<did>'||rec.spdp_id::text||'</did>'
                ||'<cid>'||rec.spcl_id::text||'</cid>'
                ||'<name>'||escape_reference(rec.spob_name)||'</name>'
                ||'<desc>'||escape_reference(rec.spob_desc)||'</desc>'
                ||'<create>'||to_char(rec.spob_dt_create,'DD.MM.YYYY HH24:MI:SS')||'</create>'
                ||'<modify>'||to_char(rec.spob_dt_modify,'DD.MM.YYYY HH24:MI:SS')||'</modify>'
                ||'<close>'||COALESCE(to_char(rec.spob_dt_close,'DD.MM.YYYY HH24:MI:SS'),'')||'</close>'
                ||'<acl>'||rec.accl_ref_type2::text||'</acl>'
                ||'<lastdate>'||COALESCE(to_char(lastdt,'DD.MM.YYYY HH24:MI:SS'),'')||'</lastdate>'
                ||'</OBJECT>';

  END LOOP;
  resp := resp||'<ROWS>'||rrow::text||'</ROWS>';
  
  RETURN '<?xml version="1.0" encoding="UTF-8"?><RESPONSE>'||resp||'</RESPONSE>';
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_acl_obj_info
 (bigint
 ) OWNER TO owner_track;
COMMENT ON FUNCTION get_acl_obj_info
 (bigint
 ) IS 'Информация по правам доступа к объектам';
