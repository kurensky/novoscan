CREATE OR REPLACE FUNCTION check_module_addition () RETURNS trigger AS 
$BODY$  
DECLARE
     spmd int8;
  BEGIN
--INSERT, UPDATE, or DELETE
    IF (TG_OP = 'INSERT') THEN
      IF NEW.spmd_dt_create > NEW.spmd_dt_close THEN
        RAISE EXCEPTION 'CREATE DATE "%" UPWARDS CLOSE DATE "%"',NEW.spmd_dt_create,NEW.spmd_dt_close;
      END IF;
      SELECT spmd_id INTO spmd 
        FROM sprv_modules 
       WHERE spmd_uid = NEW.spmd_uid
         AND (spmd_dt_close IS NULL 
           OR spmd_dt_close  >= NEW.spmd_dt_create 
           OR spmd_dt_create >= NEW.spmd_dt_create
             )
       ;
      IF FOUND THEN
        RAISE EXCEPTION 'MODULE EXIST ID="%"',spmd;
      END IF;
      RETURN NEW;
  ELSIF (TG_OP = 'UPDATE') THEN
      IF NEW.spmd_dt_create > NEW.spmd_dt_close THEN
        RAISE EXCEPTION 'CREATE DATE "%" UPWARDS CLOSE DATE "%"',NEW.spmd_dt_create,NEW.spmd_dt_close;
      END IF;
      IF NEW.spmd_uid != OLD.spmd_uid THEN
        RAISE EXCEPTION 'OLD UID "%" NOT GT NEW UID "%"',OLD.spmd_id,NEW.spmd_id;
      END IF;
      IF NEW.spmd_dt_create != OLD.spmd_dt_create THEN
        RAISE EXCEPTION 'UPDATE spmd_dt_create PROHIBITED';
      END IF;
      RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
      RAISE EXCEPTION 'DELETE PROHIBITED';
    END IF;
  END;
$BODY$
 LANGUAGE 'plpgsql';


CREATE TRIGGER check_module
BEFORE INSERT OR UPDATE OR DELETE
ON sprv_modules FOR EACH ROW
EXECUTE PROCEDURE check_module_addition();
