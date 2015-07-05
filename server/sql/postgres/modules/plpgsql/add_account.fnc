CREATE OR REPLACE FUNCTION add_account
 (ac_login varchar -- Логин
 ,ac_passwd varchar -- Пароль
 ,ac_name varchar -- Наименование
 ,ac_email varchar -- Е-майл
 )
 RETURNS int8 AS
$BODY$
DECLARE
  n_ac int8;
BEGIN
  SELECT COUNT(*) 
    INTO n_ac
    FROM accounts
   WHERE UPPER(acct_login)=UPPER(trim(ac_login))
   ;
  IF n_ac = 0 THEN
    SELECT nextval('acct_seq') INTO n_ac;
    INSERT INTO accounts
     (acct_id
     ,acct_login
     ,acct_passwd
     ,acct_name
     ,acct_dt
     ,acct_email
     ) VALUES 
     (n_ac
     ,ac_login
     ,mkpasswd(ac_passwd)
     ,ac_name
     ,now()
     ,ac_email
     );
  ELSE
    n_ac := NULL;
  END IF;
  RETURN n_ac;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;

ALTER FUNCTION add_account
 (ac_login varchar
 ,ac_passwd varchar
 ,ac_name varchar
 ,ac_email varchar
 ) OWNER TO owner_track;

REVOKE EXECUTE ON FUNCTION add_account
 (ac_login varchar 
 ,ac_passwd varchar
 ,ac_name varchar
 ,ac_email varchar
 ) FROM public;

GRANT EXECUTE ON FUNCTION add_account
 (ac_login varchar -- Логин
 ,ac_passwd varchar -- Пароль
 ,ac_name varchar -- Наименование
 ,ac_email varchar -- Е-майл
 ) TO owner_track;
GRANT EXECUTE ON FUNCTION add_account
 (ac_login varchar 
 ,ac_passwd varchar
 ,ac_name varchar
 ,ac_email varchar
 ) TO t03_sprv_dev;

REVOKE ALL ON FUNCTION add_account
 (ac_login varchar 
 ,ac_passwd varchar
 ,ac_name varchar
 ,ac_email varchar
 ) FROM public;

COMMENT ON FUNCTION add_account
 (ac_login varchar 
 ,ac_passwd varchar
 ,ac_name varchar
 ,ac_email varchar
 ) IS 'Процедура добавления аккаунта';
