
CREATE OR REPLACE FUNCTION get_constc
 (v_name varchar -- имя константы.
 )
  RETURNS varchar AS
$BODY$DECLARE
  c_constc varchar;
BEGIN
  c_constc := NULL;
  SELECT sc_cvalue INTO c_constc FROM sys_consts WHERE sc_name = UPPER(v_name);
  RETURN c_constc;
END;$BODY$
  LANGUAGE 'plpgsql' STABLE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_constc
 (v_name varchar
 )
 IS 'Процедура получения значение константы типа VARCHAR(100)';

REVOKE ALL ON FUNCTION get_constc
 (v_name varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION get_constc
 (v_name varchar
 ) TO GROUP TRACK_SERVER_ALL;
