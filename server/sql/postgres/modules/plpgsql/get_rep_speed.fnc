CREATE OR REPLACE FUNCTION get_rep_speed
(i_dist IN FLOAT
,i_time IN INT8
)
  RETURNS numeric AS
$BODY$
DECLARE
  o_speed numeric;
BEGIN
  IF i_time = 0 THEN
    o_speed := 0;
  ELSE
    o_speed := round((i_dist*3.6/i_time::float)::numeric,2);
  END IF;
  RETURN o_speed;
END;
$BODY$
  LANGUAGE 'plpgsql' STABLE SECURITY DEFINER;

ALTER FUNCTION get_rep_speed
(FLOAT
,INT8
) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION get_rep_speed
(FLOAT
,INT8
) TO owner_track;
GRANT EXECUTE ON FUNCTION get_rep_speed
(FLOAT
,INT8
) TO track_server_all;
COMMENT ON FUNCTION get_rep_speed
(FLOAT
,INT8
) IS 'Процедура получения скорости движения объекта для отчёта';
