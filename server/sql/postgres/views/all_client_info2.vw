CREATE OR REPLACE VIEW all_client_info2 
 AS 
 SELECT b.spob_name AS object_name
       ,b.spob_id AS object_id
       ,b.spob_desc AS object_desc
       ,a.spcl_name AS client_name
       ,a.spcl_id AS client_id
       ,d.spdp_name AS depart_name
       ,d.spdp_id AS depart_id
       ,m.spmd_name AS module_name
       ,m.spmd_uid AS module_uid
       ,count(*) AS count_day
   FROM sprv_objects b LEFT JOIN (data_sensor s JOIN sprv_modules m ON m.spmd_uid = s.dasn_vehicle) ON m.spmd_spob_id = b.spob_id
       ,sprv_clients a
       ,sprv_departs d
  WHERE a.spcl_id = b.spob_spcl_id 
    AND d.spdp_id = a.spcl_spdp_id 
    AND s.dasn_datetime >= (now() - interval '24 hour') AND s.dasn_datetime <= now()
  GROUP BY b.spob_name
          ,b.spob_id
          ,b.spob_desc
          ,a.spcl_name
          ,a.spcl_id
          ,d.spdp_name
          ,d.spdp_id
          ,m.spmd_name
          ,m.spmd_uid
  ORDER BY a.spcl_name;

ALTER TABLE all_client_info2 OWNER TO owner_track;

GRANT SELECT ON TABLE all_client_info2 TO GROUP TRACK_SERVER_ALL;

COMMENT ON VIEW all_client_info2 
     IS 'Отображение только автивных модулей за последний день';