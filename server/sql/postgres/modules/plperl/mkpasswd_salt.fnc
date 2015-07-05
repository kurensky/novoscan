
CREATE OR REPLACE FUNCTION mkpasswd_salt
  (text -- пароль
  ,text -- хэш ключ (salt)
  )
  RETURNS text AS
$BODY$
	my $passwd = $_[0];
	my $salt   = $_[1];
	$crypted = crypt($passwd, $salt);
	return $crypted;
$BODY$
  LANGUAGE 'plperl' VOLATILE;

COMMENT ON FUNCTION mkpasswd_salt
  (text
  ,text
  )
 IS 'Процедура получения шифрованного пароля по хэш и паролю';

REVOKE ALL ON FUNCTION mkpasswd_salt
  (text
  ,text
  ) FROM public;

GRANT EXECUTE ON FUNCTION mkpasswd_salt
  (text
  ,text
  ) TO GROUP TRACK_SERVER_ALL;
