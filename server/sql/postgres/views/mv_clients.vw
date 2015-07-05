
CREATE OR REPLACE VIEW mv_clients AS 
 SELECT l.accl_ref_id1 AS acct_id
        ,spcl_id
        ,spcl_name
        ,get_cltp_name(spcl_type) AS spcl_type_name
        ,spcl_desc
	,spcl_spdp_id
   FROM sprv_clients c
       ,account_lists l
  WHERE l.accl_ref_id2 = c.spcl_id 
    AND l.accl_ref_type2 = 103 
    AND l.accl_ref_type1 = 100
    ;

ALTER TABLE mv_clients OWNER TO owner_track;
GRANT ALL ON TABLE mv_clients TO owner_track;
GRANT SELECT ON TABLE mv_clients TO track_server_all;
COMMENT ON VIEW mv_clients IS 'Информация по клиентам аккаунта с учётом прав доступа';

