-- Function: get_attribute_name(bigint)

-- DROP FUNCTION get_attribute_name(bigint);

CREATE OR REPLACE FUNCTION get_attribute_name(attrid bigint)
  RETURNS character varying AS
$BODY$
DECLARE
  a_name character varying(30);
  gatn CURSOR(n_id BIGINT)
    IS
    SELECT r.attr_name
      FROM attributes r
     WHERE r.attr_id = n_id
     ;

BEGIN
  OPEN gatn(attrid);
  FETCH gatn INTO a_name;
  CLOSE gatn;
  RETURN a_name;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;
ALTER FUNCTION get_attribute_name(bigint)
  OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_attribute_name(bigint) TO public;
GRANT EXECUTE ON FUNCTION get_attribute_name(bigint) TO owner_track;
GRANT EXECUTE ON FUNCTION get_attribute_name(bigint) TO track_server_all;
COMMENT ON FUNCTION get_attribute_name(bigint) IS 'Имя атрибута';

