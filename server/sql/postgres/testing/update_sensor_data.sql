select * from owner_track.data_sensor 
where dasn_datetime between to_date('2013.05.06','YYYY.MM.DD') and to_date('2013.05.08','YYYY.MM.DD')
and dasn_uid = 216314