
CREATE OR REPLACE FUNCTION mkpasswd
  (text
  )
  RETURNS text AS
$BODY$
	my $passwd = $_[0];
	my $i, $j, $crypted;
	
	srand (time() ^ (getppid()<<15));
	
	my $salt = '$1$';
	
	for ($i = 0; $i < 5; $i++)
	{
		my @test;
		@test = split(/ */, "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
		$j = rand(2147483647) % 64;
		$salt .= $test[$j];
		undef(@test);
	}
	$salt .= "0";
	$crypted = crypt($passwd, $salt);
	return $crypted;
$BODY$
  LANGUAGE 'plperl' VOLATILE;

COMMENT ON FUNCTION mkpasswd
  (text
  )
 IS 'Процедура шифрования пароля';

REVOKE ALL ON FUNCTION mkpasswd
  (text
  ) FROM public;

GRANT EXECUTE ON FUNCTION mkpasswd
  (text
  ) TO GROUP TRACK_SERVER_ALL;
