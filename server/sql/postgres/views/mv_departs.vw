DROP VIEW mv_departs
;

CREATE OR REPLACE VIEW mv_departs AS 
 SELECT d.*,accl_ref_id1 AS acct_id
   FROM sprv_departs d
       ,account_lists l
  WHERE l.accl_ref_id2   = spdp_id
    AND l.accl_ref_type2 = 104 -- таблица sprv_departs
    AND l.accl_ref_type1 = 100 -- таблица accounts
;


ALTER TABLE mv_departs OWNER TO owner_track;
GRANT ALL ON TABLE mv_departs TO owner_track;
GRANT SELECT ON TABLE mv_departs TO track_server_all;
COMMENT ON VIEW mv_departs IS 'Информация по департаментам аккаунта с учётом прав доступа';
