
CREATE OR REPLACE FUNCTION get_cmd_next
 (i_spmd IN int8 -- Идентификатор модуля
 ,o_qumx OUT int8   -- Идентификатор команды в очереди
 ,o_command OUT text   -- Команда для исполнения
 ) RETURNS record AS
$BODY$
DECLARE
BEGIN
  SELECT qumx_command
        ,qumx_id
   INTO o_command
       ,o_qumx
   FROM queue_module_exec
  WHERE qumx_status = get_consti('CMD_QU')
    AND qumx_spmd_id = i_spmd
    ORDER BY qumx_date
    ;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_cmd_next
 (IN int8
 ,OUT int8
 ,OUT text
 )
 IS 'Процедура получения команды из очереди';

REVOKE ALL ON FUNCTION  get_cmd_next
 (IN int8
 ,OUT int8
 ,OUT text
 ) FROM public;
