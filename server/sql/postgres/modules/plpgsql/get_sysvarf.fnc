
CREATE OR REPLACE FUNCTION get_sysvarf
 (v_name varchar -- имя константы.
 )
  RETURNS float AS
$BODY$DECLARE
  o_vari float;
BEGIN
  o_vari := NULL;
  SELECT svar_nvalue INTO o_vari FROM sys_variables WHERE svar_name = UPPER(v_name);
  RETURN o_vari;
END;$BODY$
  LANGUAGE 'plpgsql' STABLE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_sysvarf
 (v_name varchar
 )
 IS 'Процедура получения значение переменной типа FLOAT';

REVOKE ALL ON FUNCTION get_sysvarf
 (v_name varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_sysvarf
 (v_name varchar
 ) TO GROUP TRACK_SERVER_ALL;
