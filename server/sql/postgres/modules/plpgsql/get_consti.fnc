
CREATE OR REPLACE FUNCTION get_consti
 (v_name varchar -- имя константы.
 )
  RETURNS int8 AS
$BODY$DECLARE
  o_consti int8;
BEGIN
  o_consti := NULL;
  SELECT trunc(sc_nvalue) INTO o_consti FROM sys_consts WHERE sc_name = UPPER(v_name);
  RETURN o_consti;
END;$BODY$
  LANGUAGE 'plpgsql' STABLE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_consti
 (v_name varchar
 )
 IS 'Процедура получения значение константы типа INTEGER';

REVOKE ALL ON FUNCTION get_consti
 (v_name varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_consti
 (v_name varchar
 ) TO GROUP TRACK_SERVER_ALL;
