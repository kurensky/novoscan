
CREATE OR REPLACE FUNCTION add_cmd
 (i_spmd int8 -- Идентификатор модуля
 ,i_command text   -- Команда для исполнения
 )
  RETURNS int8 AS
$BODY$
DECLARE
  n_cmd int8;
BEGIN
  SELECT nextval('qumx_seq') INTO n_cmd;
  INSERT INTO queue_module_exec 
   (qumx_command
   ,qumx_status
   ,qumx_date
   ,qumx_spmd_id
   ,qumx_status_exec
   ,qumx_result
   ,qumx_date_exec
   ,qumx_user
   ,qumx_id
   ) VALUES 
   (i_command
   ,get_consti('CMD_QU')
   ,now()
   ,i_spmd
   ,NULL
   ,NULL
   ,NULL
   ,USER
   ,n_cmd
   );

  RETURN n_cmd;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION add_cmd
 (int8
 ,text
 )
 IS 'Процедура постановки команд в очередь';

REVOKE ALL ON FUNCTION add_cmd
 (int8
 ,text
 ) FROM public;
