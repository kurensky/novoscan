
CREATE OR REPLACE FUNCTION get_constf
 (v_name varchar -- имя константы.
 )
  RETURNS float AS
$BODY$DECLARE
  o_consti float;
BEGIN
  o_consti := NULL;
  SELECT sc_nvalue INTO o_consti FROM sys_consts WHERE sc_name = UPPER(v_name);
  RETURN o_consti;
END;$BODY$
  LANGUAGE 'plpgsql' STABLE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_constf
 (v_name varchar
 )
 IS 'Процедура получения значение константы типа INTEGER';

REVOKE ALL ON FUNCTION get_constf
 (v_name varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_constf
 (v_name varchar
 ) TO GROUP TRACK_SERVER_ALL;
