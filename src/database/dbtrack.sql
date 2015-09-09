
/* Drop Indexes */

DROP INDEX IF EXISTS dalv_dasl_idx;
DROP INDEX IF EXISTS dalv_spsn_idx;
DROP INDEX IF EXISTS dalv_datetime_idx;
DROP INDEX IF EXISTS dalv_spmd_idx;
DROP INDEX IF EXISTS dalv_key_idx;
DROP INDEX IF EXISTS dasv_dasn_idx;
DROP INDEX IF EXISTS dasv_spsn_idx;
DROP INDEX IF EXISTS dasv_datetime_idx;
DROP INDEX IF EXISTS dasv_spmd_idx;
DROP INDEX IF EXISTS role_idx;
DROP INDEX IF EXISTS role_name_idx;
DROP INDEX IF EXISTS acrl_idx;
DROP INDEX IF EXISTS acrl_name_idx;
DROP INDEX IF EXISTS acrl_acct_idx;
DROP INDEX IF EXISTS acrl_role_idx;
DROP INDEX IF EXISTS spci_gstp_code_uk;
DROP INDEX IF EXISTS spun_spun_uk;



/* Drop Tables */

DROP TABLE IF EXISTS profiles;
DROP TABLE IF EXISTS attributes;
DROP TABLE IF EXISTS data_sensor_last_values;
DROP TABLE IF EXISTS data_sensor_values;
DROP TABLE IF EXISTS request_route;
DROP TABLE IF EXISTS request;
DROP TABLE IF EXISTS owner_track.account_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS sprv_report_parameters;
DROP TABLE IF EXISTS sprv_reports;
DROP TABLE IF EXISTS sprv_report_types;
DROP TABLE IF EXISTS owner_track.accounts;
DROP TABLE IF EXISTS owner_track.account_lists;
DROP TABLE IF EXISTS owner_track.data_sensor;
DROP TABLE IF EXISTS owner_track.data_sensor_last;
DROP TABLE IF EXISTS owner_track.event_log;
DROP TABLE IF EXISTS owner_track.gis_objects_attr;
DROP TABLE IF EXISTS owner_track.gis_attr_types;
DROP TABLE IF EXISTS owner_track.gis_data;
DROP TABLE IF EXISTS owner_track.gis_errors;
DROP TABLE IF EXISTS owner_track.gis_objects;
DROP TABLE IF EXISTS owner_track.gis_point_types;
DROP TABLE IF EXISTS owner_track.sprv_cis_info;
DROP TABLE IF EXISTS owner_track.gis_types;
DROP TABLE IF EXISTS owner_track.queue_module_exec;
DROP TABLE IF EXISTS owner_track.sprv_area;
DROP TABLE IF EXISTS owner_track.sprv_calibrate;
DROP TABLE IF EXISTS owner_track.sprv_objects_attr;
DROP TABLE IF EXISTS owner_track.sprv_sensors;
DROP TABLE IF EXISTS owner_track.sprv_modules;
DROP TABLE IF EXISTS owner_track.sprv_objects;
DROP TABLE IF EXISTS owner_track.sprv_clients;
DROP TABLE IF EXISTS owner_track.sprv_departs;
DROP TABLE IF EXISTS owner_track.sprv_module_types;
DROP TABLE IF EXISTS owner_track.sprv_object_types;
DROP TABLE IF EXISTS owner_track.sprv_sensor_types;
DROP TABLE IF EXISTS owner_track.sprv_units;
DROP TABLE IF EXISTS owner_track.sys_consts;
DROP TABLE IF EXISTS owner_track.sys_dictonary;
DROP TABLE IF EXISTS owner_track.sys_variables;



/* Drop Sequences */

DROP SEQUENCE IF EXISTS owner_track.accl_seq;
DROP SEQUENCE IF EXISTS owner_track.acct_seq;
DROP SEQUENCE IF EXISTS owner_track.acrl_seq;
DROP SEQUENCE IF EXISTS owner_track.dasl_seq;
DROP SEQUENCE IF EXISTS owner_track.dasn_seq;
DROP SEQUENCE IF EXISTS owner_track.evlg_seq;
DROP SEQUENCE IF EXISTS owner_track.gsat_seq;
DROP SEQUENCE IF EXISTS owner_track.gsdt_seq;
DROP SEQUENCE IF EXISTS owner_track.gsob_seq;
DROP SEQUENCE IF EXISTS owner_track.gstp_seq;
DROP SEQUENCE IF EXISTS owner_track.gstt_seq;
DROP SEQUENCE IF EXISTS owner_track.qumx_seq;
DROP SEQUENCE IF EXISTS owner_track.sc_seq;
DROP SEQUENCE IF EXISTS owner_track.sdic_seq;
DROP SEQUENCE IF EXISTS owner_track.spar_seq;
DROP SEQUENCE IF EXISTS owner_track.spcb_seq;
DROP SEQUENCE IF EXISTS owner_track.spci_seq;
DROP SEQUENCE IF EXISTS owner_track.spcl_seq;
DROP SEQUENCE IF EXISTS owner_track.spdp_seq;
DROP SEQUENCE IF EXISTS owner_track.spmd_seq;
DROP SEQUENCE IF EXISTS owner_track.spmt_seq;
DROP SEQUENCE IF EXISTS owner_track.spob_seq;
DROP SEQUENCE IF EXISTS owner_track.spot_seq;
DROP SEQUENCE IF EXISTS owner_track.spsn_seq;
DROP SEQUENCE IF EXISTS owner_track.spst_seq;
DROP SEQUENCE IF EXISTS owner_track.spun_seq;
DROP SEQUENCE IF EXISTS owner_track.svar_seq;



/* Drop Tablespaces */

DROP TABLESPACE IF EXISTS user_data;


DROP TABLESPACE IF EXISTS user_ind;




/* Create Tablespaces */

CREATE TABLESPACE user_data
 OWNER owner_track
 LOCATION '/datadir/user_data.dbf'
;


CREATE TABLESPACE user_ind
 OWNER owner_track
 LOCATION '/datadir/user_ind.dbf'
;



/* Create Sequences */

CREATE SEQUENCE owner_track.accl_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.acct_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.acrl_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000;
CREATE SEQUENCE owner_track.dasl_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE owner_track.dasn_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE owner_track.evlg_seq INCREMENT 1 MINVALUE 1 MAXVALUE 999999999 START 1 CACHE 1 CYCLE;
CREATE SEQUENCE owner_track.gsat_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.gsdt_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE owner_track.gsob_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.gstp_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.gstt_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE owner_track.qumx_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE owner_track.sc_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.sdic_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.spar_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE owner_track.spcb_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE owner_track.spci_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE owner_track.spcl_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.spdp_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.spmd_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.spmt_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.spob_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.spot_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.spsn_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.spst_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;
CREATE SEQUENCE owner_track.spun_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE owner_track.svar_seq INCREMENT 1 MINVALUE 1000 MAXVALUE 9223372036854775807 START 1000 CACHE 1;



/* Create Tables */

CREATE TABLE attributes
(
	attr_id bigint NOT NULL CONSTRAINT attr_pk UNIQUE,
	attr_name varchar(30) UNIQUE,
	attr_type varchar(1) NOT NULL,
	PRIMARY KEY (attr_id)
) WITHOUT OIDS;


CREATE TABLE data_sensor_last_values
(
	dalv_id bigserial NOT NULL,
	dalv_dasl_id bigint,
	dalv_spmd_id bigint NOT NULL,
	-- Ключ значения
	dalv_key varchar(10) NOT NULL,
	dalv_value numeric NOT NULL,
	dalv_datetime timestamp NOT NULL,
	dalv_dtm timestamp NOT NULL,
	PRIMARY KEY (dalv_id),
	CONSTRAINT dalv_dasl_key_uk UNIQUE (dalv_dasl_id, dalv_key)
) WITHOUT OIDS;


CREATE TABLE data_sensor_values
(
	dasv_id bigserial NOT NULL,
	-- Ссылка на таблицу data_sensor_last
	dasv_dasn_id bigint,
	-- Ссылка на модуль/терминал
	dasv_spmd_id bigint NOT NULL,
	-- Ключ значения
	dasv_key varchar(10) NOT NULL,
	dasv_datetime timestamp NOT NULL,
	dasv_value numeric NOT NULL,
	PRIMARY KEY (dasv_id)
) WITHOUT OIDS;


CREATE TABLE profiles
(
	prof_id bigint NOT NULL CONSTRAINT prof_pk UNIQUE,
	prof_acct_id bigint NOT NULL,
	prof_attr_id bigint NOT NULL,
	prof_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	prof_valuev varchar(1024) NOT NULL,
	prof_valuen numeric,
	prof_valued timestamp,
	PRIMARY KEY (prof_id)
) WITHOUT OIDS;


CREATE TABLE request
(
	-- Ид заявки
	req_id bigint NOT NULL CONSTRAINT req_uk UNIQUE req_pk,
	req_acct_id bigint NOT NULL,
	req_spob_id bigint NOT NULL,
	-- Имя заявки
	req_name varchar(60) NOT NULL,
	-- Описание
	req_desc text,
	-- Дата создания заявки
	req_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	-- Дата модификации заявки
	req_date_modify timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	-- Дата начала заявки
	req_date_begin timestamp NOT NULL,
	-- Дата окончания заявки
	req_date_end timestamp,
	-- Процент увеличения дистанции заложенный при расчёте фактической дистанции
	req_delta numeric DEFAULT 1.13 NOT NULL,
	PRIMARY KEY (req_id)
) WITHOUT OIDS;


CREATE TABLE request_route
(
	-- Ид
	reqr_id bigint NOT NULL CONSTRAINT reqr_uk UNIQUE reqr_pk,
	-- Ид заявки
	reqr_req_id bigint NOT NULL req_pk,
	-- Ид
	reqr_reqr_id bigint,
	-- Имя нас пункта
	reqr_point_name varchar(256) NOT NULL,
	-- Ожидаемая дата прибытия в точку.
	reqr_date_arrival timestamp NOT NULL,
	-- Ожидание стоянки в секундах.
	reqr_time_stay bigint DEFAULT 0 NOT NULL,
	-- Дистанция в км от предыдущей точки
	reqr_distance numeric DEFAULT 0 NOT NULL,
	-- Ставка
	reqr_rate numeric DEFAULT 0 NOT NULL,
	-- Расчитанная стоимость километра
	reqr_price numeric,
	PRIMARY KEY (reqr_id)
) WITHOUT OIDS;


CREATE TABLE roles
(
	role_id bigserial NOT NULL CONSTRAINT role_pk UNIQUE,
	-- Имя роли
	role_name varchar(30) NOT NULL UNIQUE,
	-- Описание роли
	role_desc text,
	role_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	PRIMARY KEY (role_id)
) WITHOUT OIDS;


-- Таблица описания отчётов
CREATE TABLE sprv_reports
(
	sprp_id bigint NOT NULL CONSTRAINT sprp_pk UNIQUE,
	sprp_sprt_id bigint NOT NULL,
	-- Имя отчёта
	sprp_name varchar(256) NOT NULL UNIQUE,
	-- Имя файла отчёта
	sprp_source varchar(256) NOT NULL,
	-- Описание отчёта
	sprp_desc text,
	PRIMARY KEY (sprp_id)
) WITHOUT OIDS;


-- Параметры отчётов
CREATE TABLE sprv_report_parameters
(
	sppr_id bigint NOT NULL UNIQUE,
	-- ссылка на отчёт
	sppr_sprp_id bigint NOT NULL,
	-- Имя параметра
	sppr_name varchar(100) NOT NULL,
	-- Тип параметра
	sppr_type varchar(100) NOT NULL,
	-- Описание параметра
	sppr_desc varchar(1024),
	sppr_valuen numeric,
	sppr_valued timestamp,
	sppr_valuev varchar(1024),
	-- запрос для формирования hashmap
	sppr_hash_map text,
	PRIMARY KEY (sppr_id)
) WITHOUT OIDS;


-- Типы отчётов
CREATE TABLE sprv_report_types
(
	sprt_id bigint NOT NULL,
	-- Имя типа отчёта
	sprt_name varchar(30) NOT NULL,
	-- Описание типа
	sprt_desc text,
	PRIMARY KEY (sprt_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.accounts
(
	acct_id bigint NOT NULL,
	-- Логин
	acct_login varchar(30) NOT NULL UNIQUE,
	-- Пароль
	acct_passwd varchar(1024) NOT NULL,
	-- Имя
	acct_name varchar(1024) NOT NULL,
	-- Имя
	acct_name2 varchar(1024),
	-- Отчество
	acct_name3 varchar(1024),
	-- Дата создания
	acct_dt timestamp NOT NULL,
	-- Е-майл
	acct_email varchar(300),
	CONSTRAINT acct_pk PRIMARY KEY (acct_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.account_lists
(
	accl_id bigint NOT NULL,
	-- ИД объекта 1 которому предоставляются права
	accl_ref_id1 bigint NOT NULL,
	-- Тип объекта 1
	accl_ref_type1 bigint NOT NULL,
	-- ИД объекта 2 на который предоставляются права
	accl_ref_id2 bigint NOT NULL,
	-- Тип объекта 2
	accl_ref_type2 bigint NOT NULL,
	-- Тип доступа
	accl_acl bigint NOT NULL,
	-- Дата создание ACL
	accl_dt timestamp NOT NULL,
	CONSTRAINT accl_pk PRIMARY KEY (accl_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.account_roles
(
	-- ИД
	acrl_id bigint NOT NULL CONSTRAINT acrl_pk UNIQUE,
	acrl_acct_id bigint NOT NULL,
	acrl_role_id bigint NOT NULL,
	PRIMARY KEY (acrl_id),
	CONSTRAINT acrl_uk UNIQUE (acrl_acct_id, acrl_role_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.data_sensor
(
	dasn_id bigint NOT NULL,
	-- Идентификатор записи лога
	dasn_uid bigint NOT NULL,
	-- Дата время с таймзоной
	dasn_datetime timestamp NOT NULL,
	-- Географическая долгота
	dasn_latitude float NOT NULL,
	-- Географическая широта
	dasn_longitude float NOT NULL,
	-- Флаг состояний
	dasn_status bigint NOT NULL,
	-- Количество спутников
	dasn_sat_used bigint,
	-- Состояние тревога зон охраны
	dasn_zone_alarm bigint,
	-- Номер макроса
	dasn_macro_id bigint,
	-- Код источника
	dasn_macro_src bigint,
	-- Скорость в м;с
	dasn_sog float,
	-- Курс в градусах
	dasn_course float,
	-- Значение HDOP
	dasn_hdop float,
	-- Значение HGEO
	dasn_hgeo float,
	-- Значение HMET
	dasn_hmet float,
	-- Состояние входов-выходов в позиционно-битовом коде
	dasn_gpio bigint,
	-- Состояние аналоговых входов
	dasn_adc bigint,
	-- Температура С
	dasn_temp float,
	-- Тип данных
	dasn_type bigint NOT NULL,
	-- Дополнтельные данные.
	dasn_xml text NOT NULL,
	-- Дата модификации
	dasn_dtm timestamp NOT NULL,
	dasn_spsn_id bigint NOT NULL,
	-- Реальный идентификатор устройства
	dasn_vehicle varchar
) WITHOUT OIDS TABLESPACE user_data;


CREATE TABLE owner_track.data_sensor_last
(
	dasl_id bigint NOT NULL,
	-- Идентификатор записи лога
	dasl_uid bigint NOT NULL,
	-- Дата время с таймзоной
	dasl_datetime timestamp NOT NULL,
	-- Географическая долгота
	dasl_latitude float NOT NULL,
	-- Географическая широта
	dasl_longitude float NOT NULL,
	-- Флаг состояний
	dasl_status bigint NOT NULL,
	-- Количество спутников
	dasl_sat_used bigint,
	-- Состояние тревога зон охраны
	dasl_zone_alarm bigint,
	-- Номер макроса
	dasl_macro_id bigint,
	-- Код источника
	dasl_macro_src bigint,
	-- Скорость в м;с
	dasl_sog float,
	-- Курс в градусах
	dasl_course float,
	-- Значение HDOP
	dasl_hdop float,
	-- Значение HGEO
	dasl_hgeo float,
	-- Значение HMET
	dasl_hmet float,
	-- Состояние входов-выходов в позиционно-битовом коде
	dasl_gpio bigint,
	-- Состояние аналоговых входов
	dasl_adc bigint,
	-- Температура С
	dasl_temp float,
	-- Тип данных
	dasl_type bigint NOT NULL,
	-- Дополнтельные данные.
	dasl_xml text NOT NULL,
	-- Дата модификации
	dasl_dtm timestamp NOT NULL,
	dasl_spsn_id bigint NOT NULL,
	-- Реальный идентификатор устройства
	dasl_vehicle varchar,
	-- Ссылка на таблицу DATA_SENSOR
	dasl_dasn_id bigint NOT NULL,
	CONSTRAINT dasl_pk PRIMARY KEY (dasl_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.event_log
(
	-- Идентификатор события
	evlg_id bigserial,
	-- Тип события
	evlg_type int NOT NULL,
	-- Дата и время события
	evlg_dt timestamp DEFAULT now() NOT NULL,
	-- Пользователь
	evlg_user varchar(20) NOT NULL,
	-- Имя схемы
	evlg_scheme varchar(50) NOT NULL,
	-- Версия системы Новоскан
	evlg_version varchar(50) NOT NULL,
	-- Тип модуля источника
	evlg_modtype int NOT NULL,
	-- Имя модуля источника
	evlg_modname varchar(50) NOT NULL,
	-- Префикс для аббревиатуры
	evlg_prefix_code varchar(4) NOT NULL,
	-- Код события, код ошибки
	evlg_code bigint NOT NULL,
	-- Текст события
	evlg_text text NOT NULL,
	-- Дополнительная информация
	evlg_info text,
	-- Количество повторений событий
	evlg_repeat int NOT NULL
) WITHOUT OIDS;


CREATE TABLE owner_track.gis_attr_types
(
	gstt_id int NOT NULL,
	-- Имя типа
	gstt_name varchar(30) NOT NULL,
	-- Описание
	gstt_desc varchar(1024) NOT NULL,
	CONSTRAINT gstt_pk PRIMARY KEY (gstt_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.gis_data
(
	gsdt_id int NOT NULL,
	-- Дата создания
	gsdt_dt date NOT NULL,
	gsdt_gsob_id int NOT NULL,
	-- Пикетажное положение точки на дороге
	gsdt_piket numeric(12,3) NOT NULL,
	-- признак "замечательной" точки (начало дороги, ИССО, перекрестки, примыкания и т.д.) 0 - обычная точка, другие значения - "замечательная" точка
	gsdt_gspt_id int NOT NULL,
	-- Дополнительная информация
	gsdt_info varchar(100),
	CONSTRAINT gsdt_pk PRIMARY KEY (gsdt_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.gis_errors
(
	-- Код ошибки
	gser_id int NOT NULL,
	-- Описание ошибки
	gser_desc varchar(1024),
	-- Дата создания
	gser_dt date NOT NULL,
	CONSTRAINT gser_pk PRIMARY KEY (gser_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.gis_objects
(
	gsob_id int NOT NULL,
	-- Код объекта
	gsob_name varchar(30) NOT NULL,
	-- Описания объекта
	gsob_desc varchar(2048) NOT NULL,
	-- Дата создания объекта
	gsob_dt date NOT NULL,
	-- Дата закрытия объекта
	gsob_dt_close date,
	-- Ид объекта в системе откуда пришла информация
	gsob_code varchar(100),
	gsob_gstp_id int,
	gsob_gser_id int,
	CONSTRAINT gsob_pk PRIMARY KEY (gsob_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.gis_objects_attr
(
	gsat_id int NOT NULL,
	-- Атрибут типа INTEGER
	gsat_attr int,
	-- Начало интервального атрибута
	gsat_attr1 float,
	-- Конец интервального атрибута
	gsat_attr2 float,
	gsat_gsob_id int,
	gsat_gstt_id int,
	CONSTRAINT gsat_pk PRIMARY KEY (gsat_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.gis_point_types
(
	gspt_id int NOT NULL,
	-- Наименование точки
	gspt_name varchar(100) NOT NULL,
	-- Описание точки
	gspt_desc varchar(1024),
	-- Дата создания
	gspt_dt date NOT NULL,
	CONSTRAINT gspt_pk PRIMARY KEY (gspt_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.gis_types
(
	gstp_id int NOT NULL,
	-- Кодировка
	gstp_code varchar(30) NOT NULL,
	-- Краткое наименование
	gstp_name varchar(100) NOT NULL,
	-- Описание
	gstp_desc varchar(1024),
	-- Дата создания
	gstp_dt date NOT NULL,
	CONSTRAINT gstp_pk PRIMARY KEY (gstp_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.queue_module_exec
(
	qumx_id bigint NOT NULL,
	-- Команда
	qumx_command text NOT NULL,
	-- Состояние команды
	qumx_status int NOT NULL,
	-- Дата постановки в очередь
	qumx_date timestamp NOT NULL,
	qumx_spmd_id bigint,
	-- Статус исполнения
	qumx_status_exec int,
	-- Результат возврата оборудования
	qumx_result text,
	-- Дата исполнения
	qumx_date_exec timestamp,
	-- Пользователь поставивиший команду
	qumx_user varchar(240),
	CONSTRAINT qumx_pk PRIMARY KEY (qumx_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_area
(
	spar_id bigint NOT NULL,
	-- Наименование области
	spar_name varchar(1024) NOT NULL UNIQUE,
	-- Дата создания
	spar_dt timestamp NOT NULL,
	CONSTRAINT spar_pk PRIMARY KEY (spar_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_calibrate
(
	spcb_spsn_id bigint NOT NULL,
	spcb_spun_id int NOT NULL,
	-- Данные калибровки
	spcb_data text NOT NULL,
	-- Тип датчика
	spcb_type int NOT NULL,
	-- Дата создания записи
	spcb_dt_create date NOT NULL,
	-- Дата модификации
	spcb_dt_modify date NOT NULL,
	-- Дата окончания действия
	spcb_dt_close date,
	spcb_id int NOT NULL,
	CONSTRAINT spcb_pk PRIMARY KEY (spcb_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_cis_info
(
	spci_id int NOT NULL,
	-- Код типа
	spci_code int NOT NULL,
	-- Описание
	spci_desc varchar(240),
	spci_gstp_id int NOT NULL,
	CONSTRAINT spci_pk PRIMARY KEY (spci_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_clients
(
	spcl_id bigint NOT NULL,
	-- Имя клиента
	spcl_name varchar(100) NOT NULL,
	-- Тип клиента
	spcl_type int NOT NULL,
	-- Описание
	spcl_desc varchar(1024),
	spcl_spdp_id bigint NOT NULL,
	spcl_spar_id bigint,
	CONSTRAINT spcl_pk PRIMARY KEY (spcl_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_departs
(
	spdp_id bigint NOT NULL,
	-- Наименование организации
	spdp_name varchar(2048) NOT NULL,
	CONSTRAINT spdp_pk PRIMARY KEY (spdp_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_modules
(
	spmd_id bigint NOT NULL,
	-- Уникальный идентификатор
	spmd_uid float NOT NULL,
	-- Наименование блока
	spmd_name varchar(100) NOT NULL,
	-- Дата регистрации блока
	spmd_dt_create timestamp NOT NULL,
	-- Дата закрытия блока
	spmd_dt_close timestamp,
	-- Пользователь создавший блок
	spmd_user varchar(30),
	-- IMEI
	spmd_imei varchar(30) NOT NULL,
	-- Номер телефона
	spmd_numb varchar(15) NOT NULL,
	-- Описание блока
	spmd_desc varchar(1024) NOT NULL,
	spmd_spob_id bigint NOT NULL,
	spmd_spmt_id bigint NOT NULL,
	CONSTRAINT spmd_pk PRIMARY KEY (spmd_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_module_types
(
	spmt_id bigint NOT NULL,
	-- Наименование типа
	spmt_name varchar(100) NOT NULL,
	-- Описание типа
	spmt_desc varchar(2048) NOT NULL,
	CONSTRAINT spmt_pk PRIMARY KEY (spmt_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_objects
(
	spob_id bigint NOT NULL,
	-- Наименование устройства
	spob_name varchar(100) NOT NULL UNIQUE,
	-- Описание устройства
	spob_desc varchar(2048) NOT NULL,
	-- Дата создания
	spob_dt_create timestamp NOT NULL,
	-- Дата модификации
	spob_dt_modify timestamp NOT NULL,
	-- Дата закрытия
	spob_dt_close timestamp,
	spob_spcl_id bigint NOT NULL,
	spob_spot_id bigint NOT NULL,
	CONSTRAINT spob_pk PRIMARY KEY (spob_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_objects_attr
(
	spat_id int NOT NULL,
	-- ИД атрибута
	spat_attr int NOT NULL,
	-- ИД типа атрибута 1 - FLOAT, 2 - TEXT, 3 - DATE
	spat_attr_type int NOT NULL,
	-- Значение атрибута FLOAT
	spat_numb float,
	-- Значение атрибута TEXT
	spat_text text,
	-- Значение атрибута DATE
	spat_date timestamp,
	spat_spob_id bigint NOT NULL,
	CONSTRAINT spat_pk PRIMARY KEY (spat_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_object_types
(
	spot_id bigint NOT NULL,
	-- Наименование типа
	spot_name varchar(100) NOT NULL,
	-- Описание типа
	spot_desc varchar(2048) NOT NULL,
	CONSTRAINT spot_pk PRIMARY KEY (spot_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_sensors
(
	spsn_id bigint NOT NULL,
	-- Идентификатор датчика
	spsn_uin float NOT NULL,
	-- Наименование датчика
	spsn_name varchar(30) NOT NULL,
	-- Описание датчика
	spsn_desc varchar(1024),
	spsn_spst_id bigint,
	spsn_spmd_id bigint NOT NULL,
	CONSTRAINT spsn_pk PRIMARY KEY (spsn_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_sensor_types
(
	spst_id bigint NOT NULL,
	-- Нименование типа
	spst_name varchar(30) NOT NULL,
	-- Описание типа
	spst_desc varchar(1024) NOT NULL,
	CONSTRAINT spst_pk PRIMARY KEY (spst_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sprv_units
(
	-- Код системы измерений
	spun_spus_id int NOT NULL,
	-- Наименование единицы измерения
	spun_name varchar(30) NOT NULL,
	-- Сокращение EN
	spun_abbr_en varchar(30) NOT NULL,
	-- Сокращение RU
	spun_abbr_ru varchar(30) NOT NULL,
	spun_id int NOT NULL,
	CONSTRAINT spun_pk PRIMARY KEY (spun_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sys_consts
(
	sc_id bigint NOT NULL,
	-- Наименование переменной
	sc_name varchar(240) NOT NULL UNIQUE,
	-- Значение типа номер
	sc_nvalue float,
	-- Значение типа строка
	sc_cvalue varchar(100),
	-- Значение типа дата
	sc_dvalue timestamp,
	-- Дата создания
	sc_dt_create timestamp NOT NULL,
	-- Описание переменной
	sc_desc varchar(1024) NOT NULL,
	CONSTRAINT sc_pk PRIMARY KEY (sc_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sys_dictonary
(
	sdic_id bigint NOT NULL,
	-- Наименование типа
	sdic_name varchar(100) NOT NULL,
	-- Идентификатор типа
	sdic_type float NOT NULL,
	-- Значение типа
	sdic_cvalue varchar(240) NOT NULL,
	-- Дата создания
	sdic_dt_create timestamp NOT NULL,
	CONSTRAINT sdic_pk PRIMARY KEY (sdic_id)
) WITHOUT OIDS;


CREATE TABLE owner_track.sys_variables
(
	svar_id bigint NOT NULL,
	-- Имя переменной
	svar_name varchar(100) NOT NULL UNIQUE,
	-- Значение типа число
	svar_nvalue float,
	-- Значение типа строка
	svar_cvalue varchar(240),
	-- Значение типа дата
	svar_dvalue timestamp,
	-- Дата создания
	svar_dt_create timestamp NOT NULL,
	-- Дата модификации
	svar_dt_modify timestamp NOT NULL,
	-- Описание
	svar_desc varchar(240) NOT NULL,
	CONSTRAINT svar_pk PRIMARY KEY (svar_id)
) WITHOUT OIDS;



/* Create Foreign Keys */

ALTER TABLE profiles
	ADD FOREIGN KEY (prof_attr_id)
	REFERENCES attributes (attr_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE request_route
	ADD FOREIGN KEY (reqr_req_id)
	REFERENCES request (req_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE request_route
	ADD FOREIGN KEY (reqr_reqr_id)
	REFERENCES request_route (reqr_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE owner_track.account_roles
	ADD FOREIGN KEY (acrl_role_id)
	REFERENCES roles (role_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE sprv_report_parameters
	ADD FOREIGN KEY (sppr_sprp_id)
	REFERENCES sprv_reports (sprp_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE sprv_reports
	ADD FOREIGN KEY (sprp_sprt_id)
	REFERENCES sprv_report_types (sprt_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE profiles
	ADD FOREIGN KEY (prof_acct_id)
	REFERENCES owner_track.accounts (acct_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE owner_track.account_roles
	ADD FOREIGN KEY (acrl_acct_id)
	REFERENCES owner_track.accounts (acct_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE request
	ADD CONSTRAINT req_acct_fk FOREIGN KEY (req_acct_id)
	REFERENCES owner_track.accounts (acct_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE data_sensor_last_values
	ADD FOREIGN KEY (dalv_dasl_id)
	REFERENCES owner_track.data_sensor_last (dasl_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE owner_track.gis_objects_attr
	ADD CONSTRAINT gsat_gstt_fk FOREIGN KEY (gsat_gstt_id)
	REFERENCES owner_track.gis_attr_types (gstt_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.gis_objects_attr
	ADD CONSTRAINT gsat_gsob_fk FOREIGN KEY (gsat_gsob_id)
	REFERENCES owner_track.gis_objects (gsob_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.gis_data
	ADD CONSTRAINT gsdt_gsob_fk FOREIGN KEY (gsdt_gsob_id)
	REFERENCES owner_track.gis_objects (gsob_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.gis_data
	ADD CONSTRAINT gsdt_gspt_fk FOREIGN KEY (gsdt_gspt_id)
	REFERENCES owner_track.gis_point_types (gspt_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_cis_info
	ADD CONSTRAINT spci_gstp_fk FOREIGN KEY (spci_gstp_id)
	REFERENCES owner_track.gis_types (gstp_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.gis_objects
	ADD CONSTRAINT gsob_gstp_fk FOREIGN KEY (gsob_gstp_id)
	REFERENCES owner_track.gis_types (gstp_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_objects
	ADD CONSTRAINT spob_spcl_fk FOREIGN KEY (spob_spcl_id)
	REFERENCES owner_track.sprv_clients (spcl_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_clients
	ADD CONSTRAINT spcl_spdp_fk FOREIGN KEY (spcl_spdp_id)
	REFERENCES owner_track.sprv_departs (spdp_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE data_sensor_last_values
	ADD FOREIGN KEY (dalv_spmd_id)
	REFERENCES owner_track.sprv_modules (spmd_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE owner_track.queue_module_exec
	ADD CONSTRAINT qumx_spmd_fk FOREIGN KEY (qumx_spmd_id)
	REFERENCES owner_track.sprv_modules (spmd_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_sensors
	ADD CONSTRAINT spsn_spmd_fk FOREIGN KEY (spsn_spmd_id)
	REFERENCES owner_track.sprv_modules (spmd_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_modules
	ADD CONSTRAINT spmd_spmt_fk FOREIGN KEY (spmd_spmt_id)
	REFERENCES owner_track.sprv_module_types (spmt_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_objects_attr
	ADD CONSTRAINT spat_spob_fk FOREIGN KEY (spat_spob_id)
	REFERENCES owner_track.sprv_objects (spob_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_modules
	ADD CONSTRAINT spmd_spob_fk FOREIGN KEY (spmd_spob_id)
	REFERENCES owner_track.sprv_objects (spob_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE request
	ADD CONSTRAINT req_spob_pk FOREIGN KEY (req_spob_id)
	REFERENCES owner_track.sprv_objects (spob_id)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE owner_track.sprv_objects
	ADD CONSTRAINT spob_spot_fk FOREIGN KEY (spob_spot_id)
	REFERENCES owner_track.sprv_object_types (spot_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_calibrate
	ADD CONSTRAINT spcb_spsn_fk FOREIGN KEY (spcb_spsn_id)
	REFERENCES owner_track.sprv_sensors (spsn_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_sensors
	ADD CONSTRAINT spsn_spst_fk FOREIGN KEY (spsn_spst_id)
	REFERENCES owner_track.sprv_sensor_types (spst_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE owner_track.sprv_calibrate
	ADD CONSTRAINT spcb_spun_fk FOREIGN KEY (spcb_spun_id)
	REFERENCES owner_track.sprv_units (spun_id)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;



/* Create Indexes */

CREATE INDEX dalv_dasl_idx ON data_sensor_last_values ();
CREATE INDEX dalv_spsn_idx ON data_sensor_last_values ();
CREATE INDEX dalv_datetime_idx ON data_sensor_last_values (dalv_datetime);
CREATE INDEX dalv_spmd_idx ON data_sensor_last_values (dalv_spmd_id);
CREATE INDEX dalv_key_idx ON data_sensor_last_values (dalv_key);
CREATE INDEX dasv_dasn_idx ON data_sensor_values (dasv_dasn_id);
CREATE INDEX dasv_spsn_idx ON data_sensor_values (dasv_key);
CREATE INDEX dasv_datetime_idx ON data_sensor_values (dasv_datetime);
CREATE INDEX dasv_spmd_idx ON data_sensor_values USING BTREE (dasv_spmd_id);
CREATE UNIQUE INDEX role_idx ON roles USING BTREE (role_id);
CREATE UNIQUE INDEX role_name_idx ON roles USING BTREE (role_name);
CREATE UNIQUE INDEX acrl_idx ON owner_track.account_roles USING BTREE (acrl_id);
CREATE UNIQUE INDEX acrl_name_idx ON owner_track.account_roles ();
CREATE INDEX acrl_acct_idx ON owner_track.account_roles USING BTREE (acrl_acct_id);
CREATE INDEX acrl_role_idx ON owner_track.account_roles USING BTREE (acrl_role_id);
CREATE UNIQUE INDEX spci_gstp_code_uk ON owner_track.sprv_cis_info USING BTREE (spci_code, spci_gstp_id);
CREATE UNIQUE INDEX spun_spun_uk ON owner_track.sprv_units USING BTREE (spun_spus_id, spun_name);



/* Comments */

COMMENT ON COLUMN data_sensor_last_values.dalv_key IS 'Ключ значения';
COMMENT ON COLUMN data_sensor_values.dasv_dasn_id IS 'Ссылка на таблицу data_sensor_last';
COMMENT ON COLUMN data_sensor_values.dasv_spmd_id IS 'Ссылка на модуль/терминал';
COMMENT ON COLUMN data_sensor_values.dasv_key IS 'Ключ значения';
COMMENT ON COLUMN request.req_id IS 'Ид заявки';
COMMENT ON COLUMN request.req_name IS 'Имя заявки';
COMMENT ON COLUMN request.req_desc IS 'Описание';
COMMENT ON COLUMN request.req_date IS 'Дата создания заявки';
COMMENT ON COLUMN request.req_date_modify IS 'Дата модификации заявки';
COMMENT ON COLUMN request.req_date_begin IS 'Дата начала заявки';
COMMENT ON COLUMN request.req_date_end IS 'Дата окончания заявки';
COMMENT ON COLUMN request.req_delta IS 'Процент увеличения дистанции заложенный при расчёте фактической дистанции';
COMMENT ON COLUMN request_route.reqr_id IS 'Ид';
COMMENT ON COLUMN request_route.reqr_req_id IS 'Ид заявки';
COMMENT ON COLUMN request_route.reqr_reqr_id IS 'Ид';
COMMENT ON COLUMN request_route.reqr_point_name IS 'Имя нас пункта';
COMMENT ON COLUMN request_route.reqr_date_arrival IS 'Ожидаемая дата прибытия в точку.';
COMMENT ON COLUMN request_route.reqr_time_stay IS 'Ожидание стоянки в секундах.';
COMMENT ON COLUMN request_route.reqr_distance IS 'Дистанция в км от предыдущей точки';
COMMENT ON COLUMN request_route.reqr_rate IS 'Ставка';
COMMENT ON COLUMN request_route.reqr_price IS 'Расчитанная стоимость километра';
COMMENT ON COLUMN roles.role_name IS 'Имя роли';
COMMENT ON COLUMN roles.role_desc IS 'Описание роли';
COMMENT ON TABLE sprv_reports IS 'Таблица описания отчётов';
COMMENT ON COLUMN sprv_reports.sprp_name IS 'Имя отчёта';
COMMENT ON COLUMN sprv_reports.sprp_source IS 'Имя файла отчёта';
COMMENT ON COLUMN sprv_reports.sprp_desc IS 'Описание отчёта';
COMMENT ON TABLE sprv_report_parameters IS 'Параметры отчётов';
COMMENT ON COLUMN sprv_report_parameters.sppr_sprp_id IS 'ссылка на отчёт';
COMMENT ON COLUMN sprv_report_parameters.sppr_name IS 'Имя параметра';
COMMENT ON COLUMN sprv_report_parameters.sppr_type IS 'Тип параметра';
COMMENT ON COLUMN sprv_report_parameters.sppr_desc IS 'Описание параметра';
COMMENT ON COLUMN sprv_report_parameters.sppr_hash_map IS 'запрос для формирования hashmap';
COMMENT ON TABLE sprv_report_types IS 'Типы отчётов';
COMMENT ON COLUMN sprv_report_types.sprt_name IS 'Имя типа отчёта';
COMMENT ON COLUMN sprv_report_types.sprt_desc IS 'Описание типа';
COMMENT ON COLUMN owner_track.accounts.acct_login IS 'Логин';
COMMENT ON COLUMN owner_track.accounts.acct_passwd IS 'Пароль';
COMMENT ON COLUMN owner_track.accounts.acct_name IS 'Имя';
COMMENT ON COLUMN owner_track.accounts.acct_name2 IS 'Имя';
COMMENT ON COLUMN owner_track.accounts.acct_name3 IS 'Отчество';
COMMENT ON COLUMN owner_track.accounts.acct_dt IS 'Дата создания';
COMMENT ON COLUMN owner_track.accounts.acct_email IS 'Е-майл';
COMMENT ON COLUMN owner_track.account_lists.accl_ref_id1 IS 'ИД объекта 1 которому предоставляются права';
COMMENT ON COLUMN owner_track.account_lists.accl_ref_type1 IS 'Тип объекта 1';
COMMENT ON COLUMN owner_track.account_lists.accl_ref_id2 IS 'ИД объекта 2 на который предоставляются права';
COMMENT ON COLUMN owner_track.account_lists.accl_ref_type2 IS 'Тип объекта 2';
COMMENT ON COLUMN owner_track.account_lists.accl_acl IS 'Тип доступа';
COMMENT ON COLUMN owner_track.account_lists.accl_dt IS 'Дата создание ACL';
COMMENT ON COLUMN owner_track.account_roles.acrl_id IS 'ИД';
COMMENT ON COLUMN owner_track.data_sensor.dasn_uid IS 'Идентификатор записи лога';
COMMENT ON COLUMN owner_track.data_sensor.dasn_datetime IS 'Дата время с таймзоной';
COMMENT ON COLUMN owner_track.data_sensor.dasn_latitude IS 'Географическая долгота';
COMMENT ON COLUMN owner_track.data_sensor.dasn_longitude IS 'Географическая широта';
COMMENT ON COLUMN owner_track.data_sensor.dasn_status IS 'Флаг состояний';
COMMENT ON COLUMN owner_track.data_sensor.dasn_sat_used IS 'Количество спутников';
COMMENT ON COLUMN owner_track.data_sensor.dasn_zone_alarm IS 'Состояние тревога зон охраны';
COMMENT ON COLUMN owner_track.data_sensor.dasn_macro_id IS 'Номер макроса';
COMMENT ON COLUMN owner_track.data_sensor.dasn_macro_src IS 'Код источника';
COMMENT ON COLUMN owner_track.data_sensor.dasn_sog IS 'Скорость в м;с';
COMMENT ON COLUMN owner_track.data_sensor.dasn_course IS 'Курс в градусах';
COMMENT ON COLUMN owner_track.data_sensor.dasn_hdop IS 'Значение HDOP';
COMMENT ON COLUMN owner_track.data_sensor.dasn_hgeo IS 'Значение HGEO';
COMMENT ON COLUMN owner_track.data_sensor.dasn_hmet IS 'Значение HMET';
COMMENT ON COLUMN owner_track.data_sensor.dasn_gpio IS 'Состояние входов-выходов в позиционно-битовом коде';
COMMENT ON COLUMN owner_track.data_sensor.dasn_adc IS 'Состояние аналоговых входов';
COMMENT ON COLUMN owner_track.data_sensor.dasn_temp IS 'Температура С';
COMMENT ON COLUMN owner_track.data_sensor.dasn_type IS 'Тип данных';
COMMENT ON COLUMN owner_track.data_sensor.dasn_xml IS 'Дополнтельные данные.';
COMMENT ON COLUMN owner_track.data_sensor.dasn_dtm IS 'Дата модификации';
COMMENT ON COLUMN owner_track.data_sensor.dasn_vehicle IS 'Реальный идентификатор устройства';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_uid IS 'Идентификатор записи лога';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_datetime IS 'Дата время с таймзоной';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_latitude IS 'Географическая долгота';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_longitude IS 'Географическая широта';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_status IS 'Флаг состояний';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_sat_used IS 'Количество спутников';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_zone_alarm IS 'Состояние тревога зон охраны';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_macro_id IS 'Номер макроса';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_macro_src IS 'Код источника';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_sog IS 'Скорость в м;с';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_course IS 'Курс в градусах';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_hdop IS 'Значение HDOP';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_hgeo IS 'Значение HGEO';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_hmet IS 'Значение HMET';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_gpio IS 'Состояние входов-выходов в позиционно-битовом коде';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_adc IS 'Состояние аналоговых входов';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_temp IS 'Температура С';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_type IS 'Тип данных';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_xml IS 'Дополнтельные данные.';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_dtm IS 'Дата модификации';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_vehicle IS 'Реальный идентификатор устройства';
COMMENT ON COLUMN owner_track.data_sensor_last.dasl_dasn_id IS 'Ссылка на таблицу DATA_SENSOR';
COMMENT ON COLUMN owner_track.event_log.evlg_id IS 'Идентификатор события';
COMMENT ON COLUMN owner_track.event_log.evlg_type IS 'Тип события';
COMMENT ON COLUMN owner_track.event_log.evlg_dt IS 'Дата и время события';
COMMENT ON COLUMN owner_track.event_log.evlg_user IS 'Пользователь';
COMMENT ON COLUMN owner_track.event_log.evlg_scheme IS 'Имя схемы';
COMMENT ON COLUMN owner_track.event_log.evlg_version IS 'Версия системы Новоскан';
COMMENT ON COLUMN owner_track.event_log.evlg_modtype IS 'Тип модуля источника';
COMMENT ON COLUMN owner_track.event_log.evlg_modname IS 'Имя модуля источника';
COMMENT ON COLUMN owner_track.event_log.evlg_prefix_code IS 'Префикс для аббревиатуры';
COMMENT ON COLUMN owner_track.event_log.evlg_code IS 'Код события, код ошибки';
COMMENT ON COLUMN owner_track.event_log.evlg_text IS 'Текст события';
COMMENT ON COLUMN owner_track.event_log.evlg_info IS 'Дополнительная информация';
COMMENT ON COLUMN owner_track.event_log.evlg_repeat IS 'Количество повторений событий';
COMMENT ON COLUMN owner_track.gis_attr_types.gstt_name IS 'Имя типа';
COMMENT ON COLUMN owner_track.gis_attr_types.gstt_desc IS 'Описание';
COMMENT ON COLUMN owner_track.gis_data.gsdt_dt IS 'Дата создания';
COMMENT ON COLUMN owner_track.gis_data.gsdt_piket IS 'Пикетажное положение точки на дороге';
COMMENT ON COLUMN owner_track.gis_data.gsdt_gspt_id IS 'признак "замечательной" точки (начало дороги, ИССО, перекрестки, примыкания и т.д.) 0 - обычная точка, другие значения - "замечательная" точка';
COMMENT ON COLUMN owner_track.gis_data.gsdt_info IS 'Дополнительная информация';
COMMENT ON COLUMN owner_track.gis_errors.gser_id IS 'Код ошибки';
COMMENT ON COLUMN owner_track.gis_errors.gser_desc IS 'Описание ошибки';
COMMENT ON COLUMN owner_track.gis_errors.gser_dt IS 'Дата создания';
COMMENT ON COLUMN owner_track.gis_objects.gsob_name IS 'Код объекта';
COMMENT ON COLUMN owner_track.gis_objects.gsob_desc IS 'Описания объекта';
COMMENT ON COLUMN owner_track.gis_objects.gsob_dt IS 'Дата создания объекта';
COMMENT ON COLUMN owner_track.gis_objects.gsob_dt_close IS 'Дата закрытия объекта';
COMMENT ON COLUMN owner_track.gis_objects.gsob_code IS 'Ид объекта в системе откуда пришла информация';
COMMENT ON COLUMN owner_track.gis_objects_attr.gsat_attr IS 'Атрибут типа INTEGER';
COMMENT ON COLUMN owner_track.gis_objects_attr.gsat_attr1 IS 'Начало интервального атрибута';
COMMENT ON COLUMN owner_track.gis_objects_attr.gsat_attr2 IS 'Конец интервального атрибута';
COMMENT ON COLUMN owner_track.gis_point_types.gspt_name IS 'Наименование точки';
COMMENT ON COLUMN owner_track.gis_point_types.gspt_desc IS 'Описание точки';
COMMENT ON COLUMN owner_track.gis_point_types.gspt_dt IS 'Дата создания';
COMMENT ON COLUMN owner_track.gis_types.gstp_code IS 'Кодировка';
COMMENT ON COLUMN owner_track.gis_types.gstp_name IS 'Краткое наименование';
COMMENT ON COLUMN owner_track.gis_types.gstp_desc IS 'Описание';
COMMENT ON COLUMN owner_track.gis_types.gstp_dt IS 'Дата создания';
COMMENT ON COLUMN owner_track.queue_module_exec.qumx_command IS 'Команда';
COMMENT ON COLUMN owner_track.queue_module_exec.qumx_status IS 'Состояние команды';
COMMENT ON COLUMN owner_track.queue_module_exec.qumx_date IS 'Дата постановки в очередь';
COMMENT ON COLUMN owner_track.queue_module_exec.qumx_status_exec IS 'Статус исполнения';
COMMENT ON COLUMN owner_track.queue_module_exec.qumx_result IS 'Результат возврата оборудования';
COMMENT ON COLUMN owner_track.queue_module_exec.qumx_date_exec IS 'Дата исполнения';
COMMENT ON COLUMN owner_track.queue_module_exec.qumx_user IS 'Пользователь поставивиший команду';
COMMENT ON COLUMN owner_track.sprv_area.spar_name IS 'Наименование области';
COMMENT ON COLUMN owner_track.sprv_area.spar_dt IS 'Дата создания';
COMMENT ON COLUMN owner_track.sprv_calibrate.spcb_data IS 'Данные калибровки';
COMMENT ON COLUMN owner_track.sprv_calibrate.spcb_type IS 'Тип датчика';
COMMENT ON COLUMN owner_track.sprv_calibrate.spcb_dt_create IS 'Дата создания записи';
COMMENT ON COLUMN owner_track.sprv_calibrate.spcb_dt_modify IS 'Дата модификации';
COMMENT ON COLUMN owner_track.sprv_calibrate.spcb_dt_close IS 'Дата окончания действия';
COMMENT ON COLUMN owner_track.sprv_cis_info.spci_code IS 'Код типа';
COMMENT ON COLUMN owner_track.sprv_cis_info.spci_desc IS 'Описание';
COMMENT ON COLUMN owner_track.sprv_clients.spcl_name IS 'Имя клиента';
COMMENT ON COLUMN owner_track.sprv_clients.spcl_type IS 'Тип клиента';
COMMENT ON COLUMN owner_track.sprv_clients.spcl_desc IS 'Описание';
COMMENT ON COLUMN owner_track.sprv_departs.spdp_name IS 'Наименование организации';
COMMENT ON COLUMN owner_track.sprv_modules.spmd_uid IS 'Уникальный идентификатор';
COMMENT ON COLUMN owner_track.sprv_modules.spmd_name IS 'Наименование блока';
COMMENT ON COLUMN owner_track.sprv_modules.spmd_dt_create IS 'Дата регистрации блока';
COMMENT ON COLUMN owner_track.sprv_modules.spmd_dt_close IS 'Дата закрытия блока';
COMMENT ON COLUMN owner_track.sprv_modules.spmd_user IS 'Пользователь создавший блок';
COMMENT ON COLUMN owner_track.sprv_modules.spmd_imei IS 'IMEI';
COMMENT ON COLUMN owner_track.sprv_modules.spmd_numb IS 'Номер телефона';
COMMENT ON COLUMN owner_track.sprv_modules.spmd_desc IS 'Описание блока';
COMMENT ON COLUMN owner_track.sprv_module_types.spmt_name IS 'Наименование типа';
COMMENT ON COLUMN owner_track.sprv_module_types.spmt_desc IS 'Описание типа';
COMMENT ON COLUMN owner_track.sprv_objects.spob_name IS 'Наименование устройства';
COMMENT ON COLUMN owner_track.sprv_objects.spob_desc IS 'Описание устройства';
COMMENT ON COLUMN owner_track.sprv_objects.spob_dt_create IS 'Дата создания';
COMMENT ON COLUMN owner_track.sprv_objects.spob_dt_modify IS 'Дата модификации';
COMMENT ON COLUMN owner_track.sprv_objects.spob_dt_close IS 'Дата закрытия';
COMMENT ON COLUMN owner_track.sprv_objects_attr.spat_attr IS 'ИД атрибута';
COMMENT ON COLUMN owner_track.sprv_objects_attr.spat_attr_type IS 'ИД типа атрибута 1 - FLOAT, 2 - TEXT, 3 - DATE';
COMMENT ON COLUMN owner_track.sprv_objects_attr.spat_numb IS 'Значение атрибута FLOAT';
COMMENT ON COLUMN owner_track.sprv_objects_attr.spat_text IS 'Значение атрибута TEXT';
COMMENT ON COLUMN owner_track.sprv_objects_attr.spat_date IS 'Значение атрибута DATE';
COMMENT ON COLUMN owner_track.sprv_object_types.spot_name IS 'Наименование типа';
COMMENT ON COLUMN owner_track.sprv_object_types.spot_desc IS 'Описание типа';
COMMENT ON COLUMN owner_track.sprv_sensors.spsn_uin IS 'Идентификатор датчика';
COMMENT ON COLUMN owner_track.sprv_sensors.spsn_name IS 'Наименование датчика';
COMMENT ON COLUMN owner_track.sprv_sensors.spsn_desc IS 'Описание датчика';
COMMENT ON COLUMN owner_track.sprv_sensor_types.spst_name IS 'Нименование типа';
COMMENT ON COLUMN owner_track.sprv_sensor_types.spst_desc IS 'Описание типа';
COMMENT ON COLUMN owner_track.sprv_units.spun_spus_id IS 'Код системы измерений';
COMMENT ON COLUMN owner_track.sprv_units.spun_name IS 'Наименование единицы измерения';
COMMENT ON COLUMN owner_track.sprv_units.spun_abbr_en IS 'Сокращение EN';
COMMENT ON COLUMN owner_track.sprv_units.spun_abbr_ru IS 'Сокращение RU';
COMMENT ON COLUMN owner_track.sys_consts.sc_name IS 'Наименование переменной';
COMMENT ON COLUMN owner_track.sys_consts.sc_nvalue IS 'Значение типа номер';
COMMENT ON COLUMN owner_track.sys_consts.sc_cvalue IS 'Значение типа строка';
COMMENT ON COLUMN owner_track.sys_consts.sc_dvalue IS 'Значение типа дата';
COMMENT ON COLUMN owner_track.sys_consts.sc_dt_create IS 'Дата создания';
COMMENT ON COLUMN owner_track.sys_consts.sc_desc IS 'Описание переменной';
COMMENT ON COLUMN owner_track.sys_dictonary.sdic_name IS 'Наименование типа';
COMMENT ON COLUMN owner_track.sys_dictonary.sdic_type IS 'Идентификатор типа';
COMMENT ON COLUMN owner_track.sys_dictonary.sdic_cvalue IS 'Значение типа';
COMMENT ON COLUMN owner_track.sys_dictonary.sdic_dt_create IS 'Дата создания';
COMMENT ON COLUMN owner_track.sys_variables.svar_name IS 'Имя переменной';
COMMENT ON COLUMN owner_track.sys_variables.svar_nvalue IS 'Значение типа число';
COMMENT ON COLUMN owner_track.sys_variables.svar_cvalue IS 'Значение типа строка';
COMMENT ON COLUMN owner_track.sys_variables.svar_dvalue IS 'Значение типа дата';
COMMENT ON COLUMN owner_track.sys_variables.svar_dt_create IS 'Дата создания';
COMMENT ON COLUMN owner_track.sys_variables.svar_dt_modify IS 'Дата модификации';
COMMENT ON COLUMN owner_track.sys_variables.svar_desc IS 'Описание';



