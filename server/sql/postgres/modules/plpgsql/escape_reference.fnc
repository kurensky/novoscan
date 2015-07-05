CREATE OR REPLACE FUNCTION escape_reference
 (p_string TEXT
 ) RETURNS TEXT
 AS
$BODY$
 DECLARE
  r_string TEXT;
 BEGIN
  r_string := p_string;
  r_string := replace(r_string,'&','&amp;');
  r_string := replace(r_string,'"','&quot;');
  r_string := replace(r_string,'<','&lt;');
  r_string := replace(r_string,'>','&gt;');
  r_string := replace(r_string,'№','N');
  RETURN(r_string);
 END;$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;

ALTER FUNCTION escape_reference
 (TEXT
 ) OWNER TO owner_track;
COMMENT ON FUNCTION escape_reference
 (TEXT
 ) IS 'Преобразование символов для отображения в XML';