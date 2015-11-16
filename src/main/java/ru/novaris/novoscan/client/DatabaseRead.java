package ru.novaris.novoscan.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import ru.novaris.novoscan.domain.*;

@RemoteServiceRelativePath("servlet")
public interface DatabaseRead extends RemoteService {
	Long checkConnect(String login, String passwd);

	Integer closeConnect();

	String getResultINFO();

	List<Accounts> getAccounts();

	List<DataSensorLast> getDataSensorLast();

	List<Acl> getObjects();

	List<SprvDeparts> getListDeparts();

	Long addDeparts(SprvDeparts object);

	void updDeparts(SprvDeparts object);

	void delDeparts(SprvDeparts object);

	Long getCount(String string);

	List<SprvClients> getListClients();

	Long addClients(SprvClients object);

	void updClients(SprvClients object);

	void delClients(SprvClients object);

	void delObjects(SprvObjects object);

	void updObjects(SprvObjects object);

	Long addObjects(SprvObjects object);

	List<SprvModules> getListModules();

	Long addModules(SprvModules object);

	void updModules(SprvModules object);

	void delModules(SprvModules object);

	List<Accounts> getListAccounts();

	Long addAccounts(Accounts object);

	void updAccounts(Accounts object);

	void delAccounts(Accounts object);

	List<Acl> getAcl();

	Long addAcl(Acl object);

	void updAcl(Acl object);

	void delAcl(Acl object);

	List<SprvObjects> getListObjects(boolean checkAdmin);

	List<SprvObjectTypes> getObjectTypes();

	List<SprvModuleTypes> getListModuleTypes();

	String getObjectInfo(String daslVehicle);

	List<DataSensor> getDataSensor(Long dasnUid, Date begDate, Date endDate);

	List<Double> getDistanceWay(String src, String dst);

	List<EventLog> getListEvent();

	Long getCountEventLog(String where);

	List<AccountRoles> getAccountRoles();

	Long addAccountRoles(AccountRoles object);

	void updAccountRoles(AccountRoles object);

	void delAccountRoles(AccountRoles object);
	
	List<Roles> getRoles();

	List<Profiles> getProfiles();

	void setFilter(List<FilterTable> filters);

	String getCriteriaFind();

	List<DataSensorLast> getListSensor();

	List<RussiaOsmPoint> getOsmData(String ilikeName);

	List<Double> getDistanceWay(Long src, Long dst);

	void delRequest(Request request);

	void delRequestRoute(RequestRoute requestRoute);

	long addRequest(Request request);

	long addRequestRoute(RequestRoute requestRoute);

	List<AccountRoles> getAccountRolesAll();

	List<SprvReports> getListReports();

	List<SprvReportTypes> getListReportTypes();

	List<SprvReportParameters> getListReportParameters(long reportId);

	void logout();

	int checkSession();

	List<ReportParamList> getHashMap(String spprHashMap);

	List<GisDataPoint> getGeomPointObjects();

	List<GisObjects> getListGisObjects();

}
