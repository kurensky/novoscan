<?php
echo "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n";
echo "<head><title>Пример простого запроса к данным сенсоров.</title></head>";
echo "<html>\n";
echo "<body>\n";
// Connecting, selecting database
$dbconn = pg_connect("host=localhost dbname=postgres user=owner_tech password=qwerty12")
    or die('Could not connect: ' . pg_last_error());

// Performing SQL query
$query = 'SELECT dasn_datetime -- дата генерации записи
      ,dasn_dtm -- дата модификации записи
      ,dasn_latitude -- широта
      ,dasn_longitude -- долгота
      ,spmd_name -- имя модуля
      ,spmd_uid -- идентификатор модуля
  FROM sprv_modules
      ,sprv_sensors 
      ,sprv_clients
      ,sprv_objects
      ,data_sensor
 WHERE spcl_name = \'UNDEF\' -- имя клиента
   AND spcl_id = spob_spcl_id
   AND spob_id = spmd_spob_id 
   AND spsn_spmd_id=spmd_id
   AND dasn_spsn_id=spsn_id
   AND dasn_dtm BETWEEN date \'2001-09-28\' AND date \'2007-11-28\' -- интервал дат
;';
$result = pg_query($query) or die('Query failed: ' . pg_last_error());

// Printing results in HTML
echo "<h4>Выполняемый запрос: </h4>\n";
echo "<br/>";
echo "<PRE>$query</PRE>";
echo "<br/>";
echo "<br/>";
echo "<h4>Результат запроса : </h4>\n";
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
