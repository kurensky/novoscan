CREATE OR REPLACE FUNCTION get_obj_rep_list
 (i_spob_id int8
 )
  RETURNS text AS
$BODY$
DECLARE
  rep_list TEXT;
BEGIN
  rep_list := 
 '<REPORT>
   <rid>1</rid>
   <name type="rdbms">rep_005html</name>
   <kml_name type="file">rep_005kmz.php</kml_name>
   <desc>Отчёт по движению за период времени</desc>
   <parameters>
    <p><num>1</num><name>id</name><type>int8</type><desc>Ид объекта</desc><form>no</form></p>
    <p><num>2</num><name>dt1</name><type>timestamp without time zone</type><desc>Дата начала</desc><form>yes</form><opt>required</opt><default>today</default></p>
    <p><num>3</num><name>dt2</name><type>timestamp without time zone</type><desc>Дата окончания</desc><form>yes</form><opt>required</opt><default>tomorrow</default></p>
   </parameters>
  </REPORT>';
  rep_list := '<?xml version="1.0" encoding="UTF-8"?><RESPONSE>'||rep_list||'</RESPONSE>';
  RETURN rep_list;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION get_obj_rep_list
 (int8
 ) OWNER TO owner_track;
COMMENT ON FUNCTION get_obj_rep_list
 (int8
 ) IS 'Список доступных отчётов для объекта';
