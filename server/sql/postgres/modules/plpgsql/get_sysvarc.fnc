
CREATE OR REPLACE FUNCTION get_sysvarc
 (v_name varchar -- имя константы.
 )
  RETURNS varchar AS
$BODY$DECLARE
  o_varv varchar(100);
BEGIN
  o_varv := NULL;
  SELECT svar_cvalue INTO o_varv FROM sys_variables WHERE svar_name = UPPER(v_name);
  RETURN o_varv;
END;$BODY$
  LANGUAGE 'plpgsql' STABLE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_sysvarc
 (v_name varchar
 )
 IS 'Процедура получения значение переменной типа VARCHAR(100)';

REVOKE ALL ON FUNCTION get_sysvarc
 (v_name varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_sysvarc
 (v_name varchar
 ) TO GROUP TRACK_SERVER_ALL;
