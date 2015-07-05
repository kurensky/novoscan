-- Function: get_acct_info(i_acct int8)

-- DROP FUNCTION get_acct_info(i_acct int8);

CREATE OR REPLACE FUNCTION get_acct_info
 (i_acct int8
 )
  RETURNS text AS
$BODY$
DECLARE
  a_name  TEXT;
  a_dt    TEXT;
  a_email TEXT;
  a_login TEXT;
  a_id    TEXT;
BEGIN
  SELECT acct_id::text
        ,acct_login::text
        ,(acct_name||' '||COALESCE(acct_name2,'')||' '||COALESCE(acct_name3,''))::text
        ,COALESCE(acct_email,'Отстутствует')::text
        ,to_char(acct_dt,get_sysvarc('NLS_DATE_FORMAT'))::text
    INTO a_id
        ,a_login
        ,a_name
        ,a_email
        ,a_dt
    FROM accounts
   WHERE acct_id = i_acct
  ;

  RETURN 
'<?xml version="1.0" encoding="UTF-8"?>
<RESPONSE>
    <xId>'||a_login||'</xId>
    <xLogin>'||escape_reference(a_login)||'</xLogin>
    <xFIO>'||escape_reference(a_name)||'</xFIO>
    <xEmail>'||escape_reference(a_email)||'</xEmail>
    <xCdate>'||a_dt||'</xCdate>
    <xBals>Некоммерческий абонент</xBals>
</RESPONSE>';
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;

ALTER FUNCTION get_acct_info
(int8
) OWNER TO owner_track;

GRANT EXECUTE ON FUNCTION get_acct_info
(int8
) TO track_server_all;

REVOKE ALL ON FUNCTION get_acct_info
(int8
) FROM public;

COMMENT ON FUNCTION get_acct_info
(int8
) IS 'Информация по аккаунту';

