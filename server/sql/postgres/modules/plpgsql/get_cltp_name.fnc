-- Function: get_cltp_name(integer)

-- DROP FUNCTION get_cltp_name(integer);

CREATE OR REPLACE FUNCTION get_cltp_name
 (i_spcl_type integer
 ) RETURNS text AS
$BODY$
DECLARE
  cltp_name TEXT;
BEGIN
  IF i_spcl_type = 1 THEN
    cltp_name := 'Предприятие';
  ELSIF i_spcl_type = 0 THEN
    cltp_name := 'Население';
  ELSE
    cltp_name := 'Неопределено';
  END IF;
  RETURN cltp_name;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_cltp_name
 (integer
 ) OWNER TO owner_track;
COMMENT ON FUNCTION get_cltp_name
 (integer
 ) IS 'Описание типа клиента по идентификатору типа';
