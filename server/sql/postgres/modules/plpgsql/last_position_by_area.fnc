-- Function: last_position_by_area(integer)

-- DROP FUNCTION last_position_by_area(integer);

CREATE OR REPLACE FUNCTION last_position_by_area
    (
    area INTEGER -- ID района для поиска
    )
  RETURNS SETOF record AS
$BODY$
DECLARE
 rec record;
 rec2 record;
 rec_out record;
BEGIN
FOR rec IN
    SELECT spob_id, spcl_id, spmd_id, spmd_name
      FROM sprv_objects, sprv_clients, sprv_modules, sprv_area
      WHERE spmd_spob_id=spob_id
       AND spob_spcl_id=spcl_id
       AND spcl_spar_id=spar_id
       AND spar_id = area
LOOP
  FOR rec2 IN
    SELECT dasn_latitude AS lat, dasn_longitude AS lon, (dasn_datetime + get_timezone() * interval '1 hours') AS date
    FROM data_sensor
     WHERE dasn_vehicle=rec.spmd_name::text
    ORDER BY dasn_datetime DESC LIMIT 1
  LOOP
    FOR rec_out IN
       SELECT rec.spob_id::INTEGER AS object, rec.spcl_id::INTEGER AS client , rec.spmd_id::INTEGER AS module,
       rec2.lat::FLOAT AS latitude, rec2.lon::FLOAT AS longitude, rec2.date::TIMESTAMP WITHOUT TIME ZONE
    LOOP
        RETURN NEXT rec_out;
    END LOOP;
  END LOOP;
END LOOP;
RETURN;

END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
ALTER FUNCTION last_position_by_area(integer) OWNER TO owner_track;
GRANT EXECUTE ON FUNCTION last_position_by_area
    (integer
    ) TO public;
GRANT EXECUTE ON FUNCTION last_position_by_area
    (integer
    ) TO owner_track;
GRANT EXECUTE ON FUNCTION last_position_by_area
    (integer
    ) TO track_server_all;
GRANT EXECUTE ON FUNCTION last_position_by_area
    (integer
    ) TO owner_dev;
COMMENT ON FUNCTION last_position_by_area
    (integer
    )
    IS 'Последние положения объектов по району. (int, int, int, float, float, timestamp)';
