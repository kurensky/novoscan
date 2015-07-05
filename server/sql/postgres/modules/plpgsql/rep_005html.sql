-- Function: rep_005html(bigint, timestamp without time zone, timestamp without time zone)

-- DROP FUNCTION rep_005html(bigint, timestamp without time zone, timestamp without time zone);

CREATE OR REPLACE FUNCTION rep_005html(i_spob_id bigint, dt1 timestamp without time zone, dt2 timestamp without time zone)
  RETURNS text AS
$BODY$

DECLARE
  rec record;
  t_data text;
  event text;
  intrv text;
  total_time_move bigint;
  total_time_stop bigint;
  total_dist float;
  dist float;
  t_time float;
  bcolor text;
 
BEGIN
  total_time_move := 0;
  total_time_stop := 0;
  total_dist := 0;
  t_data := '
<table border="0" cellpadding="2" cellspacing="1" bgcolor="#f4f4f4" style="margin-top : 12px;">
 <tr>
  <td colspan="2"><h1>Подробный отчет по механизму</h1></td>
 </tr>
 <tr>
  <td bgcolor="ffffff"><h3><i>Марка механизма: </i></h3></td>
  <td bgcolor="ffffff">'||COALESCE(get_obj_desc(i_spob_id),'-')||'</td>
 </tr>
 <tr>
  <td><h3><i>Гос.номер: </i></h3></td>
  <td>'||COALESCE(get_obj_name(i_spob_id),'-')||'</td>
 </tr>
 <tr>
  <td bgcolor="ffffff"><h3><i>Фирма-подрядчик: </i></h3></td>
  <td bgcolor="ffffff">'||COALESCE(get_obj_depa_name(i_spob_id),'-')||'</td>
 </tr>
 <tr>
  <td><h3><i>Подразделение: </i></h3></td>
  <td>'||COALESCE(get_obj_clie_name(i_spob_id),'-')||'</td>
 </tr>
 <tr>
  <td bgcolor="ffffff"><h3><i>Район: </i></h3></td>
  <td bgcolor="ffffff">'||COALESCE(get_obj_area_name(i_spob_id),'-')||'</td>
 </tr>
 <tr>
  <td><h3><i>Период времени: <i></h3></td>
  <td>c: <b>'||dt1||'</b> по: <b>'||dt2||'</b></td>
 </tr>
</table>
  ';
  t_data := t_data||'<table width="100%" border="0" cellpadding="6" cellspacing="1" bgcolor="#f4f4f4" style="margin-top : 8px;">';
  t_data := t_data||'
 <thead bgcolor="lightGray">
 <tr>
  <td rowspan=2>№пп</td>
  <td rowspan=2>Событие</td>
  <td colspan=3>Время</td>
  <td colspan=3>Местоположение</td>
  <td rowspan=2>Протяженность</td>
 </tr>
 <tr>
  <td>Начало</td>
  <td>Конец</td>
  <td>Протяж.</td>
  <td>Дорога</td>
  <td>нач. км.</td>
  <td>кон. км.</td>
 </tr>
 </thead>
<tbody bgcolor="#f1ffcf" valign="top" align="left">'
;
  FOR rec IN SELECT c.text FROM rep_005(i_spob_id,dt1,dt2) c LOOP
    dist := split_part(rec.text, ';',7)::float;
    event := split_part(rec.text, ';',2);
    intrv := split_part(rec.text, ';',5);
    total_dist := total_dist + dist;
    t_time := split_part(intrv, ':',1)::float*3600 + split_part(intrv, ':',2)::float*60 + split_part(intrv, ':',3)::float;
    bcolor := '#f1ffcf';
    IF (event = 'Движение') THEN
      bcolor := '#f4f4f4';
      total_time_move := total_time_move + t_time;
    ELSIF (event = 'Стоянка') THEN
      total_time_stop := total_time_stop + t_time;
    END IF;
    t_data := t_data
      ||'<tr bgcolor="'||bcolor||'"><td>'
      ||split_part(rec.text, ';',1)||'</td><td>'
      ||event||'</td><td>'
      ||split_part(rec.text, ';',3)||'</td><td>'
      ||split_part(rec.text, ';',4)||'</td><td>'
      ||intrv||'</td><td>'
      ||split_part(rec.text, ';',6)||'</td><td>'
      ||split_part(rec.text, ';',13)||'</td><td>'
      ||split_part(rec.text, ';',14)||'</td><td>'
      ||dist||'</td>
      </tr>';

  END LOOP;
  t_data := t_data||'</tbody></table>';
  t_data := t_data||
  '<table width="30%" border="0" cellpadding="3" cellspacing="1" bgcolor="#f4f4f4" style="margin-top : 12px;">
   <tr><td colspan="3"><b><i>Всего: </i></b></td></tr>
   <tr><td colspan="2"><b><i>Время движения: </i></b></td><td>"'||total_time_move* INTERVAL '1 second'||'" (час:мм:сек)</td></tr>
   <tr><td colspan="2"><b><i>Время простоя: </i></b></td><td>"'||total_time_stop * INTERVAL '1 second'||'" (час:мм:сек)</td></tr>
   <tr><td colspan="2"><b><i>Дистанция: </i></b></td><td>"'||total_dist||'" км.</td></tr>
   </table>';
 
  RETURN t_data;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION rep_005html(bigint, timestamp without time zone, timestamp without time zone) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION rep_005html(bigint, timestamp without time zone, timestamp without time zone) TO public;
GRANT EXECUTE ON FUNCTION rep_005html(bigint, timestamp without time zone, timestamp without time zone) TO owner_track;
GRANT EXECUTE ON FUNCTION rep_005html(bigint, timestamp without time zone, timestamp without time zone) TO track_server_all;
GRANT EXECUTE ON FUNCTION rep_005html(bigint, timestamp without time zone, timestamp without time zone) TO owner_dev;
COMMENT ON FUNCTION rep_005html(bigint, timestamp without time zone, timestamp without time zone) IS 'Статистика движения объекта за интервал дат в формате HTML';
