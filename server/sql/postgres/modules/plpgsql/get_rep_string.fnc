
CREATE OR REPLACE FUNCTION get_rep_string
 (rrow  IN INTEGER
 ,works IN TEXT
 ,dtbeg IN TIMESTAMP
 ,dtend IN TIMESTAMP
 ,tint  IN FLOAT
 ,road  IN TEXT
 ,dist  IN FLOAT
 ,speed IN FLOAT
 ,lat_beg IN FLOAT
 ,lon_beg IN FLOAT
 ,lat_end IN FLOAT
 ,lon_end IN FLOAT
 ,pik_beg IN NUMERIC
 ,pik_end IN NUMERIC
 )
  RETURNS text AS
$BODY$

DECLARE
  dt_format text := get_sysvarc('NLS_DATE_FORMAT');
BEGIN
  RETURN rrow::text||';'
       ||COALESCE(works,'')||';'
       ||to_char(dtbeg, dt_format)||';'
       ||to_char(dtend, dt_format)||';'
       ||(tint*interval '1 second')::text||';'
       ||COALESCE(road,'')||';'
       ||round((dist/1000.0)::numeric,2)::text||';'
       ||speed::text||';'
       ||COALESCE(lat_beg::text,'')||';'
       ||COALESCE(lon_beg::text,'')||';'
       ||COALESCE(lat_end::text,'')||';'
       ||COALESCE(lon_end::text,'')||';'
       ||COALESCE(pik_beg::text,'')||';'
       ||COALESCE(pik_end::text,'')||';'
  ;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  SECURITY DEFINER;

COMMENT ON FUNCTION get_rep_string
 (INTEGER
 ,TEXT
 ,TIMESTAMP
 ,TIMESTAMP
 ,FLOAT
 ,TEXT
 ,FLOAT
 ,FLOAT
 ,FLOAT
 ,FLOAT
 ,FLOAT
 ,FLOAT
 ,NUMERIC
 ,NUMERIC
 )
 IS 'Форматирование строки для отчёта';
