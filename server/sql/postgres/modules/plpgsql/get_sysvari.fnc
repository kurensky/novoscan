
CREATE OR REPLACE FUNCTION get_sysvari
 (v_name varchar -- имя константы.
 )
  RETURNS int8 AS
$BODY$DECLARE
  o_vari int8;
BEGIN
  o_vari := NULL;
  SELECT trunc(svar_nvalue) INTO o_vari FROM sys_variables WHERE svar_name = UPPER(v_name);
  RETURN o_vari;
END;$BODY$
  LANGUAGE 'plpgsql' STABLE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_sysvari
 (v_name varchar
 )
 IS 'Процедура получения значение переменной типа INTEGER';

REVOKE ALL ON FUNCTION get_sysvari
 (v_name varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_sysvari
 (v_name varchar
 ) TO GROUP TRACK_SERVER_ALL;
