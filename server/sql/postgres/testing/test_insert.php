<?php
echo "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n";
echo "<head><title>Пример вставки в таблицу данных сенсора.</title></head>";
echo "<html>\n";
echo "<body>\n";
// Connecting, selecting database
$dbconn = pg_connect("host=localhost dbname=postgres user=owner_tech password=qwerty12")
    or die('Could not connect: ' . pg_last_error());

// Performing SQL query
$query = 'SELECT
add_data_sensor
(0::varchar -- UID i_vehicle_id
,1::int8 -- i_dasn_uid
,to_date(\'2007.02.02\',\'YYYY.MM.DD\')::timestamp -- Дата время с таймзоной i_dasn_datetime
,120.42::float8 -- Географическая долгота i_dasn_latitude
,22.34::float8 -- Географическая широта i_dasn_longitude
,12::int4 -- Флаг состояний i_dasn_status
,3::int4-- Количество спутников i_dasn_sat_used
,2::int4-- Состояние тревога зон охраны i_dasn_zone_alarm
,0::int4-- Номер макроса i_dasn_macro_id
,6::int4-- Код источника i_dasn_macro_src
,3.4::float8 -- Скорость в м\с i_dasn_sog
,17.18::float8 -- Курс в градусах  i_dasn_course
,22.3::float8 -- Значение HDOP i_dasn_hdop
,60.7::float8 -- Значение HGEO i_dasn_hgeo
,44.5::float8-- Значение HMET i_dasn_hmet
,11::int4 -- Состояние входов-выходов в позиционно-битовом коде i_dasn_gpio
,12::int4 -- Состояние аналоговых входов i_dasn_adc
,12.6::float8 -- Температура С i_dasn_temp
,1::int4 -- Тип данных i_dasn_type
,\'<INFO><REC>11</REC><ID>123</ID></INFO>\'::text -- Дополнтельные данные. i_dasn_xml
,now()::timestamp -- Дата модификации i_dasn_dtm
);';
$result = pg_query($query) or die('Query failed: ' . pg_last_error());

// Printing results in HTML
echo "<h4>Выполняемый запрос: </h4>\n";
echo "<br/>";
echo "<PRE>$query</PRE>";
echo "<br/>";
echo "<br/>";
echo "<h4>Результат вставки (Ид записи) : </h4>\n";
echo "<table>\n";
while ($line = pg_fetch_array($result, null, PGSQL_ASSOC)) {
    echo "\t<tr>\n";
    foreach ($line as $col_value) {
        echo "\t\t<td>$col_value</td>\n";
    }
    echo "\t</tr>\n";
}
echo "</table>\n";
// Free resultset
pg_free_result($result);


// Closing connection
pg_close($dbconn);
echo "</body>\n";
echo "</html>\n";
?>
