-- Function: get_acl_obj_info(bigint)

-- DROP FUNCTION get_acl_obj_info(bigint);

CREATE OR REPLACE FUNCTION get_mod_info(i_obj bigint)
  RETURNS text AS
$BODY$
DECLARE
  resp	  TEXT;
  rec     RECORD;
  rrow	  BIGINT;
BEGIN
  resp := '';
  rrow := 0;
  FOR rec IN
  SELECT * 
    FROM sprv_modules m
	,sprv_module_types t
    WHERE m.spmd_spob_id = i_obj
      AND m.spmd_spmt_id = t.spmt_id
  LOOP
    rrow := rrow + 1;
    resp := resp||'<MODULE>'
                ||'<oid>'||rec.spmd_spob_id::text||'</oid>'
                ||'<mid>'||rec.spmd_id::text||'</mid>'
                ||'<uid>'||rec.spmd_uid::text||'</uid>'
                ||'<name>'||escape_reference(rec.spmd_name)||'</name>'
                ||'<type>'||escape_reference(rec.spmt_name)||'</type>'
                ||'<type_desc>'||escape_reference(rec.spmt_desc)||'</type_desc>'
                ||'<desc>'||escape_reference(rec.spmd_desc)||'</desc>'
                ||'<create>'||to_char(rec.spmd_dt_create,'DD.MM.YYYY HH24:MI:SS')||'</create>'
                ||'<close>'||COALESCE(to_char(rec.spmd_dt_close,'DD.MM.YYYY HH24:MI:SS'),'')||'</close>'
                ||'<imei>'||rec.spmd_imei::text||'</imei>'
                ||'<numb>'||rec.spmd_numb::text||'</numb>'
                ||'</MODULE>';

  END LOOP;
  resp := resp||'<ROWS>'||rrow::text||'</ROWS>';
  
  RETURN '<?xml version="1.0" encoding="UTF-8"?><RESPONSE>'||resp||'</RESPONSE>';
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_mod_info
  (bigint
  ) OWNER TO owner_track;
COMMENT ON FUNCTION get_mod_info
  (bigint
  ) IS 'Информация по модулям подключенным к объектам';
