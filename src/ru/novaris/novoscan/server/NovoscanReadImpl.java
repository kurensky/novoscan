package ru.novaris.novoscan.server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.novaris.novoscan.client.DatabaseRead;
import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.domain.AccountRoles;
import ru.novaris.novoscan.domain.Acl;
import ru.novaris.novoscan.domain.Accounts;
import ru.novaris.novoscan.domain.DataSensor;
import ru.novaris.novoscan.domain.DataSensorLast;
import ru.novaris.novoscan.domain.EventLog;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.domain.GisDataPoint;
import ru.novaris.novoscan.domain.Request;
import ru.novaris.novoscan.domain.RequestRoute;
import ru.novaris.novoscan.domain.RussiaOsmPoint;
import ru.novaris.novoscan.domain.Profiles;
import ru.novaris.novoscan.domain.Roles;
import ru.novaris.novoscan.domain.SprvClients;
import ru.novaris.novoscan.domain.SprvDeparts;
import ru.novaris.novoscan.domain.SprvModuleTypes;
import ru.novaris.novoscan.domain.SprvModules;
import ru.novaris.novoscan.domain.SprvObjectTypes;
import ru.novaris.novoscan.domain.SprvObjects;
import ru.novaris.novoscan.domain.SprvReportParameters;
import ru.novaris.novoscan.domain.SprvReportTypes;
import ru.novaris.novoscan.domain.SprvReports;
import ru.novaris.novoscan.domain.SprvSensorTypes;
import ru.novaris.novoscan.domain.SprvSensors;
import ru.novaris.novoscan.domain.SysConsts;
import ru.novaris.novoscan.domain.SysVariables;
import ru.novaris.novoscan.domain.ReportParamList;
import ru.novaris.novoscan.util.HibernateUtil;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.TripleDesCipher;

public class NovoscanReadImpl extends RemoteServiceServlet implements
		DatabaseRead, ImplConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<FilterTable> filters = new ArrayList<FilterTable>();

	private boolean isAdmin = false;

	private static String SCHEMA_ROUTING = "pgrouting";

	@SuppressWarnings("unused")
	private static int ALL_ROWS;

	private static int RESULT_OK;

	private static Long REF_ACCOUNTS;

	private static Long REF_OBJECTS;

	private static Long REF_CLIENTS;

	private static Long REF_DEPARTS;

	private static Long ACCL_USER;

	@SuppressWarnings("unused")
	private static Long ACCL_ADMIN;

	private static Long MIN_SATELLITE_USED; // минимальное количество
											// спутников

	private static Double MAX_SPEED; // максимальная скорость

	private static Double MIN_SPEED; // минимальная скорость

	private static Double MIN_PARKING_TIME; // минимальное время
											// стоянки.
	public Connection connection = null;

	private String resultINFO = null;

	private static final Logger logger = LoggerFactory
			.getLogger(NovoscanReadImpl.class);

	public Long getUserId() {
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession httpSession = request.getSession(false);
		Long userId = null;
		if (httpSession != null) {
			userId = Long.valueOf(httpSession.getAttribute(COOKIE_TAG_ID)
					.toString());
		}
		return userId;
	}

	final String sqlQuery = (new StringBuffer().append("SELECT DISTINCT s.*")
			.append("  FROM ").append("  (SELECT ob.*")
			.append("     FROM sprv_objects as ob")
			.append("         ,sprv_clients").append("         ,sprv_departs")
			.append("         ,account_lists")
			.append("    WHERE accl_ref_id1 = :userId")
			.append("      AND accl_rtype filter textef_type1 = :refTypeUser")
			.append("      AND accl_ref_type2 = :refTypeObject")
			.append("      AND spob_id = accl_ref_id2")
			.append("      AND spob_spcl_id = spcl_id")
			.append("      AND spcl_spdp_id = spdp_id").append("    UNION ")
			.append("   SELECT ob.*").append("     FROM sprv_objects ob")
			.append("         ,sprv_clients").append("         ,sprv_departs")
			.append("         ,account_lists")
			.append("    WHERE accl_ref_id1 = :userId")
			.append("      AND accl_ref_type1 = :refTypeUser")
			.append("      AND accl_ref_type2 = :refTypeClient")
			.append("      AND spcl_id = accl_ref_id2")
			.append("      AND spob_spcl_id = spcl_id")
			.append("      AND spcl_spdp_id = spdp_id ").append("    UNION ")
			.append("   SELECT ob.*").append("     FROM sprv_objects as ob")
			.append("         ,sprv_clients").append("         ,sprv_departs")
			.append("         ,account_lists")
			.append("    WHERE accl_ref_id1 = :userId")
			.append("      AND accl_ref_type1 = :refTypeUser")
			.append("      AND accl_ref_type2 = :refTypeDepart")
			.append("      AND spdp_id = accl_ref_id2")
			.append("      AND spob_spcl_id = spcl_id")
			.append("     AND spcl_spdp_id = spdp_id) s")).toString();

	private boolean rawDataTrack;

	private Criteria criteria;

	private final List<String> roles = new ArrayList<String>();

	private void initSysVariables() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<SysVariables> sysVariables = new ArrayList<SysVariables>(session
				.createCriteria(SysVariables.class).list());
		session.getTransaction().commit();
		for (int i = 0; i < sysVariables.size(); i++) {
			String svarName = sysVariables.get(i).getSvarName();
			if (svarName.equals("MIN_SATELLITE_USED")) {
				MIN_SATELLITE_USED = sysVariables.get(i).getSvarNvalue()
						.longValue();
			} else if (svarName.equals("MAX_SPEED")) {
				MAX_SPEED = sysVariables.get(i).getSvarNvalue();
			} else if (svarName.equals("MIN_SPEED")) {
				MIN_SPEED = sysVariables.get(i).getSvarNvalue();
			} else if (svarName.equals("MIN_PARKING_TIME")) {
				MIN_PARKING_TIME = sysVariables.get(i).getSvarNvalue();
			} else if (svarName.equals("SCHEMA_ROUTING")) {
				SCHEMA_ROUTING = sysVariables.get(i).getSvarCvalue();
			}
		}
	}

	private void initSysConsts() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<SysConsts> sysConsts = new ArrayList<SysConsts>(session
				.createCriteria(SysConsts.class).list());
		session.getTransaction().commit();
		for (int i = 0; i < sysConsts.size(); i++) {
			String scName = sysConsts.get(i).getScName();
			if (scName.equals("ALL_ROWS")) {
				ALL_ROWS = (sysConsts.get(i).getScNvalue().intValue());
			} else if (scName.equals("RESULT_OK")) {
				RESULT_OK = (sysConsts.get(i).getScNvalue().intValue());
			} else if (scName.equals("REF_ACCOUNTS")) {
				REF_ACCOUNTS = sysConsts.get(i).getScNvalue().longValue();
			} else if (scName.equals("REF_OBJECTS")) {
				REF_OBJECTS = sysConsts.get(i).getScNvalue().longValue();
			} else if (scName.equals("REF_CLIENTS")) {
				REF_CLIENTS = sysConsts.get(i).getScNvalue().longValue();
			} else if (scName.equals("REF_DEPARTS")) {
				REF_DEPARTS = sysConsts.get(i).getScNvalue().longValue();
			} else if (scName.equals("ROLE_USER")) {
				ACCL_USER = sysConsts.get(i).getScNvalue().longValue();
			} else if (scName.equals("ROLE_ADMIN")) {
				ACCL_ADMIN = sysConsts.get(i).getScNvalue().longValue();
			}
		}

	}

	public String getUserName() {
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		String userName = null;
		if (session != null) {
			userName = session.getAttribute(COOKIE_TAG_NAME).toString();
		}
		return userName;
	}

	/**
	 * Регистрация входа в систему
	 * 
	 * @param userId
	 * @param userName
	 */
	private void saveLoginInfo(Long userId, String userName) {
		// create session and store userid
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(true);
		session.setAttribute(COOKIE_TAG_ID, userId);
		session.setAttribute(COOKIE_TAG_NAME, userName);
		session.setAttribute(COOKIE_TAG_IP, getIpAddress());
	}

	/**
	 * Проверка логина и пароля (аутентификация)
	 */
	@Override
	public Long checkConnect(String userName, String userPasswd) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Long uId = null;
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey(GWT_DES_KEY);
		try {
			String uPasswd = cipher.decrypt(userPasswd);
			String uName = cipher.decrypt(userName);
			List<?> sqlResultList = session
					.createSQLQuery("SELECT chk_login(:U, :P, :I)")
					.setString("U", uName).setString("P", uPasswd)
					.setString("I", getIpAddress()).list();
			session.getTransaction().commit();
			uId = Long.valueOf(sqlResultList.get(0).toString());
			// заполняем переменные
			roles.clear();
			isAdmin = false;
			saveLoginInfo(uId, uName);
			initRoles();
			initSysConsts();
			initSysVariables();
		} catch (HibernateException | DataLengthException
				| IllegalStateException | InvalidCipherTextException e) {
			session.getTransaction().rollback();
			resultINFO = e.getLocalizedMessage();
			saveLoginInfo(null, null);
			logger.warn("auth", "Logon denied : " + this.getUserName()
					+ " IP : " + getIpAddress());
		}
		return uId;
	}

	private void initRoles() {
		List<AccountRoles> userListRoles = getAccountRoles();
		for (int i = 0; i < userListRoles.size(); i++) {
			roles.add(userListRoles.get(i).getAcrlRoleName());
		}
		if (roles.contains("DEVELOPER")
				|| roles.contains("I01_ADM_ADMINISTRATOR")) {
			isAdmin = true;
		} else {
			isAdmin = false;
		}
	}

	/**
	 * Завершение соединения с базой
	 */
	public Integer closeConnect() {
		try {
			connection.close();
		} catch (Exception e) {
			resultINFO = e.getMessage();
			return ACCESS_DENIED;
		}
		return RESULT_OK;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Roles> getRoles() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		List<Roles> listRoles = new ArrayList<Roles>();
		session.beginTransaction();
		try {
			listRoles = new ArrayList<Roles>(session
					.createCriteria(Roles.class).list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return listRoles;

	}

	/**
	 * Список объектов пользователя
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<Acl> getObjects() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Acl> acl = new ArrayList<Acl>();
		try {
			acl = new ArrayList<Acl>(session
					.createCriteria(Acl.class)
					.add(Restrictions.and(Restrictions.and(Restrictions.eq(
							"acclRefId1", getUserId().longValue()),
							Restrictions.eq("acclAcl", ACCL_USER)),
							Restrictions.eq("acclRefType1", REF_ACCOUNTS)))
					.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

		return acl;
	}

	/**
	 * Список пользователей
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<Accounts> getAccounts() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Accounts> accounts = new ArrayList<Accounts>();
		try {
			criteria = session.createCriteria(Accounts.class);
			setCriteriaFilters("Accounts");
			criteria.addOrder(Order.asc("acctName"))
					.addOrder(Order.asc("acctName3"))
					.addOrder(Order.asc("acctName2"));
			accounts = new ArrayList<Accounts>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return accounts;
	}

	/**
	 * Дополнительная информация по исполнению запросов (в случае ошибки, её
	 * детализация)
	 */
	public String getResultINFO() {
		return resultINFO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataSensorLast> getDataSensorLast() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<DataSensorLast> dataLastsInfo = new ArrayList<DataSensorLast>();
		try {
			dataLastsInfo = new ArrayList<DataSensorLast>(session
					.createCriteria(DataSensorLast.class).list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

		return dataLastsInfo;
	}

	/**
	 * Последняя информация по объектам с учётом прав доступа
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<DataSensorLast> getListSensor() {
		StringBuffer sqlQuery;
		Date maxDateModify = null;
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession httpSession = request.getSession(false);
		sqlQuery = new StringBuffer()
				.append("SELECT ds.dasl_Id")
				.append("      ,ds.dasl_Uid")
				.append("      ,ds.dasl_Datetime")
				.append("      ,ds.dasl_Latitude")
				.append("      ,ds.dasl_Longitude")
				.append("      ,ds.dasl_Status")
				.append("      ,ds.dasl_Sat_Used")
				.append("      ,ds.dasl_Zone_Alarm")
				.append("      ,ds.dasl_Macro_Id")
				.append("      ,ds.dasl_Macro_Src")
				.append("      ,ds.dasl_Sog")
				.append("      ,ds.dasl_Course")
				.append("      ,ds.dasl_Hdop")
				.append("      ,ds.dasl_Hgeo")
				.append("      ,ds.dasl_Hmet")
				.append("      ,ds.dasl_Gpio")
				.append("      ,ds.dasl_Adc")
				.append("      ,ds.dasl_Temp")
				.append("      ,ds.dasl_Type")
				.append("      ,ds.dasl_Xml")
				.append("      ,ds.dasl_Dtm")
				.append("      ,ds.dasl_Spsn_Id")
				.append("      ,ds.dasl_Vehicle")
				.append("      ,ds.dasl_Dasn_Id")
				.append("      ,ob.spob_name||' ('||ob.spob_desc||')' dasl_Object_Info")
				.append("      ,ob.spob_name dasl_Object_Name")
				.append("  FROM (").append("       SELECT spob_id")
				.append("             ,spcl_id")
				.append("             ,spdp_id")
				.append("             ,spob_name")
				.append("             ,spob_desc")
				.append("             ,spob_dt_create")
				.append("             ,spob_dt_modify")
				.append("             ,spob_dt_close")
				.append("             ,MIN(accl_ref_type2) AS accl_ref_type2")
				.append("         FROM (")
				.append("              SELECT spob_id")
				.append("                    ,spcl_id")
				.append("                    ,spdp_id")
				.append("                    ,spob_name")
				.append("                    ,spob_desc")
				.append("                    ,spob_dt_create")
				.append("                    ,spob_dt_modify")
				.append("                    ,spob_dt_close")
				.append("                    ,accl_ref_type2")
				.append("                FROM sprv_objects")
				.append("                    ,sprv_clients")
				.append("                    ,sprv_departs")
				.append("                    ,account_lists")
				.append("               WHERE accl_ref_id1 = :userId")
				.append("                 AND accl_ref_type1 = :refTypeUser")
				.append("                 AND accl_ref_type2 = :refTypeObject")
				.append("                 AND spob_id = accl_ref_id2")
				.append("                 AND spob_spcl_id = spcl_id")
				.append("                 AND spcl_spdp_id = spdp_id")
				.append("               UNION")
				.append("              SELECT spob_id")
				.append("                    ,spcl_id")
				.append("                    ,spdp_id")
				.append("                    ,spob_name")
				.append("                    ,spob_desc")
				.append("                    ,spob_dt_create")
				.append("                    ,spob_dt_modify")
				.append("                    ,spob_dt_close")
				.append("                    ,accl_ref_type2")
				.append("                FROM sprv_objects")
				.append("                    ,sprv_clients")
				.append("                    ,sprv_departs")
				.append("                    ,account_lists")
				.append("               WHERE accl_ref_id1 = :userId")
				.append("                 AND accl_ref_type1 = :refTypeUser")
				.append("                 AND accl_ref_type2 = :refTypeClient")
				.append("                 AND spcl_id = accl_ref_id2")
				.append("                 AND spob_spcl_id = spcl_id")
				.append("                 AND spcl_spdp_id = spdp_id")
				.append("               UNION")
				.append("              SELECT spob_id")
				.append("                    ,spcl_id")
				.append("                    ,spdp_id")
				.append("                    ,spob_name")
				.append("                    ,spob_desc")
				.append("                    ,spob_dt_create")
				.append("                    ,spob_dt_modify")
				.append("                    ,spob_dt_close")
				.append("                    ,accl_ref_type2")
				.append("                FROM sprv_objects")
				.append("                    ,sprv_clients")
				.append("                    ,sprv_departs")
				.append("                    ,account_lists")
				.append("               WHERE accl_ref_id1 = :userId")
				.append("                 AND accl_ref_type1 = :refTypeUser")
				.append("                 AND accl_ref_type2 = :refTypeDepart")
				.append("                 AND spdp_id = accl_ref_id2")
				.append("                 AND spob_spcl_id = spcl_id")
				.append("                 AND spcl_spdp_id = spdp_id")
				.append("               ) s")
				.append("          GROUP BY spob_id")
				.append("                  ,spcl_id")
				.append("                  ,spdp_id")
				.append("                  ,spob_name")
				.append("                  ,spob_desc")
				.append("                  ,spob_dt_create")
				.append("                  ,spob_dt_modify")
				.append("                  ,spob_dt_close")
				.append("          ORDER BY spob_name")
				.append("         ) AS ob")
				.append("         ,data_sensor_last AS ds")
				.append("         ,sprv_sensors AS sn")
				.append("         ,sprv_modules AS md")
				.append("    WHERE md.spmd_spob_id = ob.spob_id")
				.append("      AND md.spmd_id = sn.spsn_spmd_id")
				.append("      AND ds.dasl_spsn_id = sn.spsn_id")
				;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<DataSensorLast> sqlResult = new ArrayList<DataSensorLast>();
		try {
			if (httpSession.getAttribute(COOKIE_LAST_SENSOR_DATE) != null) {
				maxDateModify = new Date(
						(Long) httpSession
								.getAttribute(COOKIE_LAST_SENSOR_DATE));
				sqlQuery.append("      AND ds.dasl_Dtm > :dtm");
				sqlResult = new ArrayList<DataSensorLast>(session
						.createSQLQuery(sqlQuery.toString())
						.addEntity(DataSensorLast.class)
						.setLong("userId", getUserId())
						.setLong("refTypeUser", REF_ACCOUNTS)
						.setLong("refTypeObject", REF_OBJECTS)
						.setLong("refTypeClient", REF_CLIENTS)
						.setLong("refTypeDepart", REF_DEPARTS)
						.setDate("dtm", maxDateModify).list());
			} else {
				sqlResult = new ArrayList<DataSensorLast>(session
						.createSQLQuery(sqlQuery.toString())
						.addEntity(DataSensorLast.class)
						.setLong("userId", getUserId())
						.setLong("refTypeUser", REF_ACCOUNTS)
						.setLong("refTypeObject", REF_OBJECTS)
						.setLong("refTypeClient", REF_CLIENTS)
						.setLong("refTypeDepart", REF_DEPARTS).list());
			}
			session.getTransaction().commit();

			for (int i = 0; i < sqlResult.size(); i++) {
				if (maxDateModify == null) {
					maxDateModify = sqlResult.get(i).getDaslDtm();
				} else {
					if (maxDateModify.before(sqlResult.get(i).getDaslDtm())) {
						maxDateModify = sqlResult.get(i).getDaslDtm();
					}
				}
			}
			if (maxDateModify != null) {
				httpSession.setAttribute(COOKIE_LAST_SENSOR_DATE,
						maxDateModify.getTime());
			}
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	/**
	 * Процедура для получения общего количества строк выборки
	 */

	@Override
	public Long getCount(String string) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Long sqlResult = null;
		try {
			List<?> sqlResultList = session.createSQLQuery(string).list();
			sqlResult = Long.valueOf(sqlResultList.get(0).toString());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	/**
	 * Работа с таблицей предприятий
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<SprvDeparts> getListDeparts() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SprvDeparts> sqlResult = new ArrayList<SprvDeparts>();
		try {
			criteria = session.createCriteria(SprvDeparts.class);
			setCriteriaFilters("SprvDeparts");
			sqlResult = new ArrayList<SprvDeparts>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;

	}

	/**
	 * Добавление предприятия
	 * 
	 * @return
	 */

	@Override
	public Long addDeparts(SprvDeparts object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.save(object);
			session.getTransaction().commit();
			return object.getSpdpId();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	/**
	 * Модификация предприятия
	 */

	@Override
	public void updDeparts(SprvDeparts object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.update(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	/**
	 * Удаление предприятия
	 */

	@Override
	public void delDeparts(SprvDeparts object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	/**
	 * Работа с таблицей клиентов
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SprvClients> getListClients() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SprvClients> sqlResult = new ArrayList<SprvClients>();
		try {
			criteria = session.createCriteria(SprvClients.class);
			setCriteriaFilters("SprvClients");
			sqlResult = new ArrayList<SprvClients>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	@Override
	public Long addClients(SprvClients object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.save(object);
			session.getTransaction().commit();
			return object.getSpclId();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void updClients(SprvClients object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.update(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void delClients(SprvClients object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	@Override
	public void delObjects(SprvObjects object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void updObjects(SprvObjects object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.update(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public Long addObjects(SprvObjects object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.save(object);
			session.getTransaction().commit();
			return object.getSpobId();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SprvObjects> getListObjects(boolean checkAdmin) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SprvObjects> sqlResult = new ArrayList<SprvObjects>();
		String sqlUserObjId = (new StringBuffer().append(" spob_id IN (")
				.append(" SELECT DISTINCT s.spob_id").append("   FROM")
				.append("       (SELECT ob.* ")
				.append("          FROM sprv_objects as ob")
				.append("              ,sprv_clients")
				.append("              ,sprv_departs")
				.append("              ,account_lists ")
				.append("         WHERE accl_ref_id1=").append(getUserId())
				.append("           AND accl_ref_type1=").append(REF_ACCOUNTS)
				.append("           AND accl_ref_type2=").append(REF_OBJECTS)
				.append("           AND spob_id=accl_ref_id2")
				.append("           AND spob_spcl_id=spcl_id")
				.append("           AND spcl_spdp_id=spdp_id")
				.append("         UNION").append("        SELECT ob.*")
				.append("          FROM sprv_objects ob")
				.append("              ,sprv_clients")
				.append("              ,sprv_departs")
				.append("              ,account_lists")
				.append("         WHERE accl_ref_id1=").append(getUserId())
				.append("           AND accl_ref_type1=").append(REF_ACCOUNTS)
				.append("           AND accl_ref_type2=").append(REF_CLIENTS)
				.append("           AND spcl_id=accl_ref_id2")
				.append("           AND spob_spcl_id=spcl_id")
				.append("           AND spcl_spdp_id=spdp_id")
				.append("         UNION").append("        SELECT ob.*")
				.append("          FROM sprv_objects as ob")
				.append("              ,sprv_clients")
				.append("              ,sprv_departs")
				.append("              ,account_lists")
				.append("         WHERE accl_ref_id1=").append(getUserId())
				.append("           AND accl_ref_type1=").append(REF_ACCOUNTS)
				.append("           AND accl_ref_type2=").append(REF_DEPARTS)
				.append("           AND spdp_id=accl_ref_id2")
				.append("           AND spob_spcl_id=spcl_id")
				.append("           AND spcl_spdp_id=spdp_id) s)")).toString();

		try {
			criteria = session.createCriteria(SprvObjects.class);
			setCriteriaFilters("SprvObjects");
			if (checkAdmin) {
				if (!isAdmin) {
					criteria.add(Restrictions.sqlRestriction(sqlUserObjId));
				}
			} else {
				criteria.add(Restrictions.sqlRestriction(sqlUserObjId));
			}
			sqlResult = new ArrayList<SprvObjects>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		// logger.debug("Фильтр : " + sqlWhere + " сортировка : " + order);
		return sqlResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SprvModules> getListModules() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SprvModules> sqlResult = new ArrayList<SprvModules>();
		try {
			criteria = session.createCriteria(SprvModules.class);
			setCriteriaFilters("SprvModules");
			sqlResult = new ArrayList<SprvModules>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		// logger.debug("Фильтр : " + sqlWhere + " сортировка : " + order);
		return sqlResult;
	}

	@Override
	public Long addModules(SprvModules object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.save(object);
			SprvSensorTypes objectSensorsTypes = new SprvSensorTypes();
			objectSensorsTypes.setSpstId(0);
			objectSensorsTypes.setSpstName("UNDEF");
			SprvSensors objectSensors = new SprvSensors();
			objectSensors.setSprvModules(object);
			objectSensors.setSprvSensorTypes(objectSensorsTypes);
			objectSensors.setSpsnDesc("Universal Sensor");
			objectSensors.setSpsnName(object.getSpmdName());
			objectSensors.setSpsnUin(object.getSpmdUid());
			session.save(objectSensors);
			session.getTransaction().commit();
			return objectSensors.getSpsnId();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void updModules(SprvModules object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.update(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void delModules(SprvModules object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			Criteria sqlTemp = session.createCriteria(SprvSensors.class);
			sqlTemp.add(Restrictions.eq("spsnUin", object.getSpmdUid()));
			List<SprvSensors> sqlResult = new ArrayList<SprvSensors>();
			sqlResult = new ArrayList<SprvSensors>(sqlTemp.list());
			for (int i = 0; i < sqlResult.size(); i++) {
				session.delete(sqlResult.get(i));
			}
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Accounts> getListAccounts() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Accounts> sqlResult = new ArrayList<Accounts>();
		try {
			criteria = session.createCriteria(Accounts.class);
			setCriteriaFilters("Accounts");
			sqlResult = new ArrayList<Accounts>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		// logger.debug("Фильтр : " + sqlWhere + " сортировка : " + order);
		return sqlResult;
	}

	@Override
	public Long addAccounts(Accounts object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.save(object);
			session.getTransaction().commit();
			return object.getAcctId();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void updAccounts(Accounts object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			object.setAcctPasswd(getCrypt(object, session));
			session.update(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	private String getCrypt(Accounts object, Session session) {
		String sqlResult;
		List<?> sqlResultList = session.createSQLQuery("SELECT mkpasswd(:P)")
				.setString("P", object.getAcctPasswd()).list();
		sqlResult = (String) sqlResultList.get(0);
		// logger.info("auth",
		// "Change password for user : " + object.getAcctLogin());
		return sqlResult;
	}

	@Override
	public void delAccounts(Accounts object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Acl> getAcl() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Acl> sqlResult = new ArrayList<Acl>();
		try {
			criteria = session.createCriteria(Acl.class);
			setCriteriaFilters("Acl");
			sqlResult = new ArrayList<Acl>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		// logger.debug("Фильтр : " + sqlWhere + " сортировка : " + order);
		return sqlResult;

	}

	@Override
	public Long addAcl(Acl object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.save(object);
			session.getTransaction().commit();
			return object.getAcclId();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void updAcl(Acl object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.update(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void delAcl(Acl object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SprvObjectTypes> getObjectTypes() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SprvObjectTypes> sqlResult = new ArrayList<SprvObjectTypes>();
		try {
			sqlResult = new ArrayList<SprvObjectTypes>(session.createCriteria(
					SprvObjectTypes.class).list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SprvModuleTypes> getListModuleTypes() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SprvModuleTypes> sqlResult = new ArrayList<SprvModuleTypes>();
		try {
			sqlResult = new ArrayList<SprvModuleTypes>(session.createCriteria(
					SprvModuleTypes.class).list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	@Override
	public String getObjectInfo(String daslVehicle) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String sqlResult = null;
		try {
			List<?> sqlResultList = session
					.createSQLQuery(
							(new StringBuffer()
									.append("SELECT b.spob_name||' ('||b.spob_desc||')'")
									.append("  FROM sprv_objects b")
									.append("      ,sprv_modules m")
									.append(" WHERE m.spmd_uid||''=:V")
									.append("   AND m.spmd_spob_id = b.spob_id"))
									.toString()).setString("V", daslVehicle)
					.list();
			session.getTransaction().commit();
			sqlResult = (String) sqlResultList.get(0);
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			resultINFO = e.getMessage();
			sqlResult = null;
		}
		return sqlResult;
	}

	public Long getModuleType(long uid) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Long sqlResult = null;
		try {
			List<?> sqlResultList = session
					.createSQLQuery(
							(new StringBuffer().append("SELECT m.spmd_spmt_id")
									.append("  FROM sprv_sensors s")
									.append("      ,sprv_modules m")
									.append("      ,sprv_module_types t")
									.append(" WHERE s.spsn_spmd_id=m.spmd_id")
									.append("   AND t.spmt_id=m.spmd_spmt_id")
									.append("   AND m.spmd_uid=:U")).toString())
					.setLong("U", uid).list();
			session.getTransaction().commit();
			sqlResult = Long.valueOf(sqlResultList.get(0).toString());

		} catch (HibernateException e) {
			session.getTransaction().rollback();
			resultINFO = e.getMessage();
			sqlResult = null;
		}
		return sqlResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.novaris.novoscan.client.DatabaseRead#getDataSensor(long,
	 * java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DataSensor> getDataSensor(Long dasnUid, Date begDate,
			Date endDate) {
		Long modType = getModuleType(dasnUid);
		// logger.warn("Тип модуля: "+ modType.longValue());
		if (modType.longValue() == 303) { // подумать о критерии возврата
											// необработанных данных
			MIN_SATELLITE_USED = (long) 0;
			rawDataTrack = true;
		} else if (modType.longValue() == 304) {
			MIN_SATELLITE_USED = (long) 0;
			rawDataTrack = true;
		} else if (modType.longValue() == 305) {
			MIN_SPEED = 0.0;
			MIN_SATELLITE_USED = (long) 0;
			rawDataTrack = false;
		} else {
			MIN_SATELLITE_USED = (long) 3;
			MIN_SPEED = 5.0;
			rawDataTrack = false;
		}
		Calendar calBeg = Calendar.getInstance();
		calBeg.setTime(begDate);
		begDate = new java.util.Date(calBeg.getTimeInMillis());
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		endDate = new java.util.Date(calEnd.getTimeInMillis());
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<DataSensor> sqlResult = new ArrayList<DataSensor>();
		try {
			Criteria sqlTemp = session.createCriteria(DataSensor.class);
			sqlTemp.add(Restrictions.eq("dasnUid", dasnUid));
			sqlTemp.add(Restrictions.ge("dasnDatetime", begDate));
			sqlTemp.add(Restrictions.le("dasnDatetime", endDate));
			sqlTemp.add(Restrictions.and(
					Restrictions.ne("dasnLatitude", (double) 0),
					Restrictions.ne("dasnLongitude", (double) 0)));
			if (rawDataTrack) {
				sqlTemp.add(Restrictions.ge("dasnSatUsed", MIN_SATELLITE_USED));
			}
			sqlTemp.add(Restrictions.le("dasnSog", MAX_SPEED));
			sqlTemp.addOrder(Order.asc("dasnDatetime"));
			sqlTemp.addOrder(Order.asc("dasnId"));
			sqlResult = new ArrayList<DataSensor>(sqlTemp.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

		List<DataSensor> sqlResultTrack = new ArrayList<DataSensor>();
		int tmpSize = 4;
		DataSensor tmpResult[] = new DataSensor[tmpSize];
		boolean lastDrive = false;
		long timeDrive = 0;
		long timeParking = 0;
		long timeParkingFull = 0;
		double longCourse = 0;
		double longDrive = 0;
		DataSensor dataTrack = null;
		Calendar cal = Calendar.getInstance();
		if (rawDataTrack) {
			for (int i = 0; i < sqlResult.size(); i++) {
				cal.setTime(sqlResult.get(i).getDasnDatetime());
				sqlResult.get(i).setDasnDatetime(cal.getTime());
				sqlResult.get(i).setDasnType(TRACK_ROUND); // окружность.
				sqlResultTrack.add(sqlResult.get(i));
				tmpResult[1] = sqlResult.get(i);
			}
			if (sqlResult.size() > 0) {
				tmpResult[1].setDasnType(TRACK_POINT);
				sqlResultTrack.add(sqlResultTrack.size(), tmpResult[1]);
			}

		} else {
			int dataLast = sqlResult.size() - 1;
			for (int i = 0; i < sqlResult.size(); i++) {
				if (i == 0) {
					if (sqlResult.get(i).getDasnSog() > MIN_SPEED) {
						lastDrive = true;
					} else {
						lastDrive = false;
					}
					dataTrack = sqlResult.get(i);
					dataTrack.setDasnType(TRACK_BEGIN);
					sqlResultTrack.add(dataTrack);
				} else if (i == dataLast) {
					dataTrack = sqlResult.get(i);
					dataTrack.setDasnType(TRACK_END);
					sqlResultTrack.add(dataTrack);
				} else {
					cal.setTime(sqlResult.get(i).getDasnDatetime());
					sqlResult.get(i).setDasnDatetime(cal.getTime());
					if (sqlResult.get(i).getDasnSog() < MIN_SPEED) {
						// стоянка
						if (lastDrive) {
							// Последний статус был движение
							dataTrack = sqlResult.get(i);
						}
						lastDrive = false;
					} else {
						// Движемся
						if ((!lastDrive) && (dataTrack != null)
								&& (sqlResult.get(i) != null)) {
							// Последний статус была стоянка
							// вычисление времени стоянки
							timeParking = timeParking
									+ (sqlResult.get(i).getDasnDatetime()
											.getTime() - dataTrack
											.getDasnDatetime().getTime())
									/ 1000;
							if (timeParking > MIN_PARKING_TIME) {
								// стояли более минимального времени стоянки.
								dataTrack.setDasnType(TRACK_STOP);
								int parkHour = (int) (timeParking / 3600);
								int parkMin = (int) ((timeParking - parkHour * 3600) / 60);
								int parkSec = (int) (timeParking - parkHour
										* 3600 - parkMin * 60);
								dataTrack.setDasnXml((new StringBuffer()
										.append(parkHour).append(" ч. ")
										.append(parkMin).append(" м. ")
										.append(parkSec).append(" с."))
										.toString());
								sqlResultTrack.add(dataTrack);
								timeParkingFull = timeParkingFull + timeParking;
							}
							timeParking = 0;
						}
						sqlResultTrack.add(sqlResult.get(i));

						// Секунды
						timeDrive = timeDrive
								+ (sqlResult.get(i).getDasnDatetime().getTime() - sqlResult
										.get(i - 1).getDasnDatetime().getTime())
								/ 1000;
						double deltaDistance = getDistance(
								sqlResult.get(i - 1), sqlResult.get(i));
						longDrive = longDrive + deltaDistance;
						longCourse = longCourse + deltaDistance;
						if ((Math.abs(sqlResult.get(i - 1).getDasnCourse()
								- sqlResult.get(i).getDasnCourse()) > MIN_CHANGE_COURSE)
								|| (longCourse > MIN_LONG_COURSE) && lastDrive) {
							longCourse = 0;
							sqlResult.get(i).setDasnType(TRACK_DRIVE);
							sqlResultTrack.add(sqlResult.get(i));
						}

						lastDrive = true;
					}

				}
				for (int k = tmpSize; k > 1;) {
					k--;
					tmpResult[k] = tmpResult[k - 1];
				}
				tmpResult[1] = sqlResult.get(i);
			}
			// Вернём типы данных (движение, стоянка, указатели направления)
			if (sqlResult.size() > 0) {
				int driveHour = (int) ((timeDrive) / 3600);
				int driveMin = (int) ((timeDrive - driveHour * 3600) / 60);
				int driveSec = (int) (timeDrive - driveHour * 3600 - driveMin * 60);
				tmpResult[1].setDasnXml((new StringBuffer()
						.append("Длина пути : ")
						.append(Math.round(longDrive / 10) / 100)
						.append("км. Время : ").append(driveHour)
						.append(" ч. ").append(driveMin).append(" м. ")
						.append(driveSec).append(" с.")).toString());
				tmpResult[1].setDasnType(TRACK_POINT);
				sqlResultTrack.add(sqlResultTrack.size(), tmpResult[1]);
			}
		}
		return sqlResultTrack;
	}

	/*
	 * Вычисление дистанции.
	 */
	public double getDistance(DataSensor dataSensorPrev,
			DataSensor dataSensorLast) {
		// Вычисление дистанции
		// косинусы и синусы широт и разниц долгот
		double lat1 = dataSensorPrev.getDasnLatitude() * PI;
		double lat2 = dataSensorLast.getDasnLatitude() * PI;
		double cl1 = Math.cos(lat1);
		double cl2 = Math.cos(lat2);
		double sl1 = Math.sin(lat1);
		double sl2 = Math.sin(lat2);
		double delta = dataSensorLast.getDasnLongitude() * PI
				- dataSensorPrev.getDasnLongitude() * PI;
		double cdelta = Math.cos(delta);
		double sdelta = Math.sin(delta);
		// вычисления длины большого круга
		double p1 = Math.pow(cl2 * sdelta, 2);
		double p2 = Math.pow(((cl1 * sl2) - (sl1 * cl2 * cdelta)), 2);
		return (Math.atan(Math.pow(p1 + p2, 0.5)
				/ (sl1 * sl2 + cl1 * cl2 * cdelta)))
				* EARTH_RADIUS;
	}

	@Override
	public List<Double> getDistanceWay(String src, String dst) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Double> sqlResult = new ArrayList<Double>();
		// double sqlResult = Double.valueOf(-1);
		try {
			/*
			 * Получение идентификатора
			 */
			List<?> sqlResultList = session
					.createSQLQuery(
							(new StringBuffer().append("SELECT * FROM ")
									.append(SCHEMA_ROUTING)
									.append(".get_wadc_id(:src, :dst)"))
									.toString()).setText("src", src)
					.setText("dst", dst).list();
			Long wadc = Long.valueOf(sqlResultList.get(0).toString());
			if (wadc != null) {
				sqlResultList.clear();
				sqlResultList = session
						.createSQLQuery(
								(new StringBuffer().append("SELECT * FROM ")
										.append(SCHEMA_ROUTING)
										.append(".get_distance_wadc(:wadc_id)"))
										.toString())
						.setLong("wadc_id", wadc.longValue()).list();
				sqlResult.add(0, (Double) sqlResultList.get(0));
				sqlResultList.clear();
				sqlResultList = session
						.createSQLQuery(
								(new StringBuffer().append("SELECT * FROM ")
										.append(SCHEMA_ROUTING)
										.append(".get_distance_real_wadc(:wadc,:user)"))
										.toString())
						.setLong("wadc", wadc.longValue())
						.setLong("user", getUserId().longValue()).list();
				if (!sqlResultList.isEmpty()) {
					sqlResult.add(1, (Double) sqlResultList.get(0));
				} else {
					sqlResult.add(1, null);
				}
			} else {
				sqlResult.add(0, null);
				sqlResult.add(1, null);
			}
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			resultINFO = e.getLocalizedMessage();
			sqlResult.add(0, null);
			sqlResult.add(1, null);
			logger.warn(resultINFO);
		}
		return sqlResult;
	}

	@Override
	public List<Double> getDistanceWay(Long osm_src, Long osm_dst) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Double> sqlResult = new ArrayList<Double>();
		// double sqlResult = Double.valueOf(-1);
		try {
			/*
			 * Получение идентификатора
			 */
			List<?> sqlResultList = session
					.createSQLQuery(
							(new StringBuffer().append("SELECT * FROM ")
									.append(SCHEMA_ROUTING)
									.append(".get_wadc_id(:src, :dst)"))
									.toString()).setLong("src", osm_src)
					.setLong("dst", osm_dst).list();
			// logger.debug("Точки: " + osm_src + " - " + osm_dst);
			Long wadc = Long.valueOf(sqlResultList.get(0).toString());
			if (wadc != null) {
				sqlResultList.clear();
				sqlResultList = session
						.createSQLQuery(
								(new StringBuffer().append("SELECT * FROM ")
										.append(SCHEMA_ROUTING)
										.append(".get_distance_wadc(:wadc_id)"))
										.toString())
						.setLong("wadc_id", wadc.longValue()).list();
				sqlResult.add(0, (Double) sqlResultList.get(0));
				sqlResultList.clear();
				sqlResultList = session
						.createSQLQuery(
								(new StringBuffer().append("SELECT * FROM ")
										.append(SCHEMA_ROUTING)
										.append(".get_distance_real_wadc(:wadc,:user)"))
										.toString())
						.setLong("wadc", wadc.longValue())
						.setLong("user", getUserId().longValue()).list();
				if (!sqlResultList.isEmpty()) {
					sqlResult.add(1, (Double) sqlResultList.get(0));
				} else {
					sqlResult.add(1, null);
				}
			} else {
				sqlResult.add(0, null);
				sqlResult.add(1, null);
			}
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			resultINFO = e.getLocalizedMessage();
			sqlResult.add(0, null);
			sqlResult.add(1, null);
			logger.warn(resultINFO);
		}
		return sqlResult;
	}

	/*
	 * Получение IP адреса
	 */
	public String getIpAddress() {
		HttpServletRequest request = this.getThreadLocalRequest();
		return request.getRemoteAddr();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventLog> getListEvent() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<EventLog> sqlResult = new ArrayList<EventLog>();
		try {
			criteria = session.createCriteria(EventLog.class);
			setCriteriaFilters("EventLog");
			sqlResult = new ArrayList<EventLog>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;

	}

	@Override
	public Long getCountEventLog(String sqlWhere) {
		return Long.valueOf(getListEvent().size());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccountRoles> getAccountRoles() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<AccountRoles> sqlResult = new ArrayList<AccountRoles>();
		try {
			criteria = session.createCriteria(AccountRoles.class);
			// setCriteriaFilters("AccountRoles");
			criteria.add(Restrictions.eq("acrlAcctId", getUserId()));
			sqlResult = new ArrayList<AccountRoles>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccountRoles> getAccountRolesAll() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<AccountRoles> sqlResult = new ArrayList<AccountRoles>();
		try {
			criteria = session.createCriteria(AccountRoles.class);
			setCriteriaFilters("AccountRoles");
			sqlResult = new ArrayList<AccountRoles>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	@Override
	public Long addAccountRoles(AccountRoles object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.save(object);
			session.getTransaction().commit();
			return object.getAcrlId();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}

	}

	@Override
	public void updAccountRoles(AccountRoles object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.update(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void delAccountRoles(AccountRoles object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Profiles> getProfiles() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Profiles> sqlResult = new ArrayList<Profiles>();
		try {
			Criteria sqlTemp = session.createCriteria(Profiles.class);
			sqlTemp.add(Restrictions.eq("profAcctId", getUserId()));
			sqlResult = new ArrayList<Profiles>(sqlTemp.list());
			session.getTransaction().commit();
			boolean profExist = false;
			for (Profiles prof: sqlResult) {
				// проверяем наличие xml профиля.
				if(prof.getProfAttrType().equals("X") && prof.getProfAttrId() == 1) {
					profExist = true;
				}
			}
			// если профил не существует создадим профиль по умолчанию.
			if(!profExist) {
				createDefautlProfile();
			}
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	private void createDefautlProfile() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			Profiles prof = new Profiles();
			prof.setProfAcctId(getUserId());
			prof.setProfAttrType("X");
			prof.setProfAttrId(1);
			prof.setProfValuen(0);
			prof.setProfXml("<profile><map refresh=\"2000\"/></profile>");
			prof.setProfDate(new Date());
			session.save(prof);
			session.getTransaction().commit();		
	    } catch (HibernateException e) {
	    	session.getTransaction().rollback();
	    	throw e;
		}
	}
	

	@Override
	public void setFilter(List<FilterTable> filters) {
		this.filters = filters;
	}

	public void setCriteriaFilters(String table) {
		// logger.debug("Количество условий: "+ filters.size());
		Object value1;
		Object value2;
		Criterion lhs = null;
		Criterion rhs = null;
		for (int i = 0; i < filters.size(); i++) {
			// logger.debug("Таблица: "+ filters.get(i).getFilterTable());
			if (table.equalsIgnoreCase(filters.get(i).getFilterTable()))
				if (filters.get(i).getFieldValue1() != null
						&& filters.get(i).getFieldValue1().length() > 0) {
					// logger.debug("Условие: "+filters.get(i).getFieldKey()+filters.get(i).getExpression()+filters.get(i).getFieldValue1());
					rhs = lhs;
					if (filters.get(i).getFieldType().equals(SQL_TYPE_LONG)) {
						value1 = Long.valueOf(filters.get(i).getFieldValue1());
					} else if (filters.get(i).getFieldType()
							.equals(SQL_TYPE_DOUBLE)) {
						value1 = Double
								.valueOf(filters.get(i).getFieldValue1());
					} else {
						value1 = filters.get(i).getFieldValue1();
					}
					if (filters.get(i).getExpression().equals(SQL_EQUAL)) {
						lhs = Restrictions.eq(filters.get(i).getFieldKey(),
								value1);
					} else if (filters.get(i).getExpression().equals(SQL_LIKE)) {
						lhs = Restrictions.like(filters.get(i).getFieldKey(),
								value1);
					} else if (filters.get(i).getExpression().equals(SQL_GT)) {
						lhs = Restrictions.gt(filters.get(i).getFieldKey(),
								value1);
					} else if (filters.get(i).getExpression().equals(SQL_LT)) {
						lhs = Restrictions.lt(filters.get(i).getFieldKey(),
								value1);
					} else if (filters.get(i).getExpression()
							.equals(SQL_BETWEEN)) {
						if (filters.get(i).getFieldValue1() != null
								&& filters.get(i).getFieldValue2() != null) {
							if (filters.get(i).getFieldType()
									.equals(SQL_TYPE_LONG)) {
								value2 = Long.valueOf(filters.get(i)
										.getFieldValue2());
							} else if (filters.get(i).getFieldType()
									.equals(SQL_TYPE_DOUBLE)) {
								value2 = Double.valueOf(filters.get(i)
										.getFieldValue2());
							} else {
								value2 = filters.get(i).getFieldValue2();
							}
							lhs = Restrictions.between(filters.get(i)
									.getFieldKey(), value1, value2);
						}
					}
					if (rhs != null) {
						if (filters.get(i - 1).getFieldLogical().equals(SQL_OR)) {
							criteria.add(Restrictions.or(lhs, rhs));
						} else {
							criteria.add(Restrictions.and(lhs, rhs));
						}
					}
				}

		}
		if ((rhs == null) && (lhs != null)) {
			criteria.add(lhs);
		}

	}

	@Override
	public String getCriteriaFind() {
		return criteria.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RussiaOsmPoint> getOsmData(String ilikeName) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<RussiaOsmPoint> sqlResult = new ArrayList<RussiaOsmPoint>();
		try {
			Criteria criteriaOsm = session.createCriteria(RussiaOsmPoint.class);
			criteriaOsm.add(Restrictions.and(Restrictions.and(
					Restrictions.isNotNull("osmType"),
					Restrictions.isNotNull("osmName")), Restrictions.ilike(
					"osmName", ilikeName.concat("%"))));
			criteriaOsm.addOrder(Order.asc("osmName"));
			criteriaOsm.setMaxResults(SEARCH_POINT_LIMIT);

			sqlResult = new ArrayList<RussiaOsmPoint>(criteriaOsm.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	@Override
	public void delRequest(Request object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public void delRequestRoute(RequestRoute object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public long addRequest(Request object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.save(object);
			session.getTransaction().commit();
			return object.getReqId();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@Override
	public long addRequestRoute(RequestRoute object) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			session.save(object);
			session.getTransaction().commit();
			return object.getReqrId();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SprvReports> getListReports() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SprvReports> sqlResult = new ArrayList<SprvReports>();
		try {
			criteria = session.createCriteria(SprvReports.class);
			setCriteriaFilters("SprvReports");
			sqlResult = new ArrayList<SprvReports>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SprvReportTypes> getListReportTypes() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SprvReportTypes> sqlResult = new ArrayList<SprvReportTypes>();
		try {
			criteria = session.createCriteria(SprvReportTypes.class);
			setCriteriaFilters("SprvReportTypes");
			sqlResult = new ArrayList<SprvReportTypes>(criteria.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SprvReportParameters> getListReportParameters(long reportId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SprvReportParameters> sqlResult = new ArrayList<SprvReportParameters>();
		try {
			Criteria criteriaReport = session
					.createCriteria(SprvReportParameters.class);
			criteriaReport.add(Restrictions.eq("spprSprpId", reportId));
			sqlResult = new ArrayList<SprvReportParameters>(
					criteriaReport.list());
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return sqlResult;
	}

	@Override
	public void logout() {
		HttpServletRequest request = this.getThreadLocalRequest();
		// dont create a new one -> false
		HttpSession session = request.getSession(false);
		if (session == null)
			return;
		// do some logout stuff ...
		session.removeAttribute(COOKIE_LAST_SENSOR_DATE);
		session.removeAttribute(COOKIE_TAG_ID);
		session.removeAttribute(COOKIE_TAG_NAME);
		session.removeAttribute(COOKIE_TAG_IP);
		session.invalidate();
	}

	@Override
	public int checkSession() {
		int status = 0;
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession(false);
			if (session == null) {
				status = -1;
			}
		} catch (Exception e) {
			status = -2;
		}
		return status;
	}

	@Override
	public List<ReportParamList> getHashMap(String spprHashMap) {
		String sqlQuery = spprHashMap.replace(":userId",
				String.valueOf(getUserId()));
		List<ReportParamList> list = new ArrayList<ReportParamList>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			@SuppressWarnings("rawtypes")
			List object = session.createSQLQuery(sqlQuery)
					.addScalar("key", new StringType())
					.addScalar("value", new StringType()).list();
			Iterator<?> iter = object.iterator();
			while (iter.hasNext()) {
				ReportParamList param = new ReportParamList();
				Object[] objArray = (Object[]) iter.next();
				param.setKey((String) objArray[0]);
				param.setValue((String) objArray[1]);
				list.add(param);
			}

			session.getTransaction().commit();
			// System.out.println("key : "+list.get(0).getKey());

		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GisDataPoint> getGeomPointObjects() {
		List<SprvObjects> objects = getListObjects(false);
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<GisDataPoint> sqlResult = new ArrayList<GisDataPoint>();
		Object[] gisTypes = {(long) 1001,(long) 1002}; //
		try {

			StringBuffer sql = new StringBuffer();
			for (int i = 0; i < objects.size(); i++) {
				sql.setLength(0);
				sql.append("get_access_gsob(")
						.append(objects.get(i).getSpobId())
						.append(",gsdt_gsob_id) > 0");
				Criteria criteriaData = session
						.createCriteria(GisDataPoint.class);
				criteriaData.add(Restrictions.and(
						Restrictions.in("gsdtGsptId", gisTypes),
						Restrictions.sqlRestriction(sql.toString())));
				sqlResult.addAll(new ArrayList<GisDataPoint>(criteriaData.list()));
			}
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		List<GisDataPoint> uniqZones = new ArrayList<GisDataPoint>(new HashSet<GisDataPoint>(sqlResult));
		return uniqZones;
	}

	/*
	 * SELECT ST_X(geom), ST_Y(geom) FROM ( SELECT (ST_DumpPoints(g.geom)).*
	 * FROM (SELECT gsdt_multiline_geom AS geom FROM gis_data ) AS g ) j;
	 */

}