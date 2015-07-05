-- Function: set_cmd_status(bigint, character varying, timestamp without time zone)

-- DROP FUNCTION set_cmd_status(bigint, character varying, timestamp without time zone);

CREATE OR REPLACE FUNCTION set_cmd_status
(i_qumx bigint
,i_status character varying
,i_date timestamp without time zone
,i_cmd_info text
)
  RETURNS integer AS
$BODY$
DECLARE
BEGIN
  
  UPDATE queue_module_exec
     SET qumx_status = get_consti(i_status)
        ,qumx_date_exec = i_date
        ,qumx_user = USER
        ,qumx_result = i_cmd_info
   WHERE qumx_id = i_qumx
     AND qumx_status = get_consti('CMD_QU')
     ;
  IF found THEN
    RETURN 1;
  ELSE
    RETURN 0;
  END IF;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION set_cmd_status
(i_qumx bigint
,i_status character varying
,i_date timestamp without time zone
,i_cmd_info text
) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION set_cmd_status
(i_qumx bigint
,i_status character varying
,i_date timestamp without time zone
,i_cmd_info text
) TO owner_track;
COMMENT ON FUNCTION set_cmd_status
(i_qumx bigint
,i_status character varying
,i_date timestamp without time zone
,i_cmd_info text
) IS 'Процедура изменения статуса команды если ещё не исполнено';
