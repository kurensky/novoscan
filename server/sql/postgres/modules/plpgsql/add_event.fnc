CREATE OR REPLACE FUNCTION add_event (IN i_type          integer,
                                      IN i_user          CHARACTER VARYING,
                                      IN i_code          integer,
                                      IN i_prefix_code   CHARACTER VARYING,
                                      IN i_modtype       integer,
                                      IN i_modname       CHARACTER VARYING,
                                      IN i_text          text,
                                      IN i_info          text
                                      )
   RETURNS int8
AS
   $BODY$
   DECLARE
      n_evlg int8;
   BEGIN
      SELECT nextval('evlg_seq') INTO n_evlg;
      INSERT INTO event_log (evlg_id,
                             evlg_user,
                             evlg_type,
                             evlg_scheme,
                             evlg_version,
                             evlg_modtype,
                             evlg_modname,
                             evlg_prefix_code,
                             evlg_code,
                             evlg_text,
                             evlg_info,
                             evlg_repeat)
           VALUES (n_evlg,
                   i_user,
                   i_type,
                   current_schema(),
                   '2.0',
                   i_modtype,
                   i_modname,
                   i_prefix_code,
                   i_code,
                   i_text,
                   i_info,
                   1);
     RETURN n_evlg;
   END;
   $BODY$
   LANGUAGE plpgsql;
ALTER FUNCTION add_event
 (integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  text,
  text) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION add_event
 (integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  text,
  text) TO owner_track;
GRANT EXECUTE ON FUNCTION add_event
 (integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  text,
  text) TO t02_adm_www;
GRANT EXECUTE ON FUNCTION add_event
 (integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  text,
  text) TO t02_adm_dev;
COMMENT ON FUNCTION add_event
 (integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  integer,
  CHARACTER VARYING,
  text,
  text) IS 'Процедура регистрации событий';
