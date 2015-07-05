
CREATE OR REPLACE FUNCTION del_cmd
 (i_qumx int8 -- Идентификатор команды
 )
  RETURNS int AS
$BODY$
DECLARE
BEGIN
  
  DELETE FROM queue_module_exec 
   WHERE qumx_id = i_qumx
     AND qumx_status = get_consti('CMD_QU');
  IF found THEN
    RETURN 1;
  ELSE
    RETURN 0;
  END IF;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION del_cmd
 (int8
 )
 IS 'Процедура удаления команды из очереди если ещё не исполнено';

REVOKE ALL ON FUNCTION del_cmd
 (int8
 ) FROM public;
