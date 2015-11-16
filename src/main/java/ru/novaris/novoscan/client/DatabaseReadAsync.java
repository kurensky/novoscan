package ru.novaris.novoscan.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import ru.novaris.novoscan.domain.*;

public interface DatabaseReadAsync {

	void checkConnect(String login, String passwd, AsyncCallback<Long> callback);

	void closeConnect(AsyncCallback<Integer> callback);

	void getAccounts(AsyncCallback<List<Accounts>> callback);

	void getResultINFO(AsyncCallback<String> callback);

	void getDataSensorLast(AsyncCallback<List<DataSensorLast>> callback);

	void getListSensor(AsyncCallback<List<DataSensorLast>> callback);

	void getObjects(AsyncCallback<List<Acl>> callback);

	void getListDeparts(AsyncCallback<List<SprvDeparts>> callback);

	void addDeparts(SprvDeparts object, AsyncCallback<Long> callback);

	void updDeparts(SprvDeparts object,
			AsyncCallback<Void> callback);

	void delDeparts(SprvDeparts object,
			AsyncCallback<Void> callback);

	void getCount(String string, AsyncCallback<Long> callback);

	void getListClients(AsyncCallback<List<SprvClients>> callback);

	void addClients(SprvClients object, AsyncCallback<Long> callback);

	void updClients(SprvClients object,
			AsyncCallback<Void> callback);

	void delClients(SprvClients object,
			AsyncCallback<Void> callback);

	void delObjects(SprvObjects object,
			AsyncCallback<Void> callback);

	void updObjects(SprvObjects object,
			AsyncCallback<Void> callback);

	void addObjects(SprvObjects object, AsyncCallback<Long> callback);

	void getListModules(AsyncCallback<List<SprvModules>> callback);

	void addModules(SprvModules object, AsyncCallback<Long> callback);

	void updModules(SprvModules object,
			AsyncCallback<Void> callback);

	void delModules(SprvModules object,
			AsyncCallback<Void> callback);

	void getListAccounts(AsyncCallback<List<Accounts>> callback);

	void addAccounts(Accounts object, AsyncCallback<Long> callback);

	void updAccounts(Accounts object, AsyncCallback<Void> callback);

	void delAccounts(Accounts object,
			AsyncCallback<Void> callback);

	void getAcl(AsyncCallback<List<Acl>> callback);

	void addAcl(Acl object, AsyncCallback<Long> callback);

	void updAcl(Acl object, AsyncCallback<Void> callback);

	void delAcl(Acl object, AsyncCallback<Void> callback);

	void getObjectTypes(AsyncCallback<List<SprvObjectTypes>> callback);

	void getListModuleTypes(AsyncCallback<List<SprvModuleTypes>> callback);

	void getListObjects(boolean checkAdmin,
			AsyncCallback<List<SprvObjects>> callback);

	void getObjectInfo(String daslVehicle, AsyncCallback<String> callback);

	void getDataSensor(Long dasnUid, Date begDate, Date endDate,
			AsyncCallback<List<DataSensor>> callback);

	void getDistanceWay(String src, String dst,
			AsyncCallback<List<Double>> callback);

	void getListEvent(AsyncCallback<List<EventLog>> callback);

	void getCountEventLog(String where, AsyncCallback<Long> callback);

	void getAccountRoles(AsyncCallback<List<AccountRoles>> callback);

	void addAccountRoles(AccountRoles object, AsyncCallback<Long> callback);

	void updAccountRoles(AccountRoles object,
			AsyncCallback<Void> callback);

	void delAccountRoles(AccountRoles object,
			AsyncCallback<Void> callback);

	void getRoles(AsyncCallback<List<Roles>> callback);
	
	void getProfiles(AsyncCallback<List<Profiles>> callback);

	void setFilter(List<FilterTable> filters, AsyncCallback<Void> callback);

	void getCriteriaFind(AsyncCallback<String> callback);

	void getOsmData(String ilikeName, AsyncCallback<List<RussiaOsmPoint>> callback);	

	void getDistanceWay(Long src, Long dst,
			AsyncCallback<List<Double>> callback);

	void delRequest(Request request, AsyncCallback<Void> callback);

	void delRequestRoute(RequestRoute requestRoute, AsyncCallback<Void> callback);

	void addRequest(Request request, AsyncCallback<Long> callback);

	void addRequestRoute(RequestRoute requestRoute, AsyncCallback<Long> callback);

	void getAccountRolesAll(AsyncCallback<List<AccountRoles>> callback);

	void getListReports(AsyncCallback<List<SprvReports>> callback);

	void getListReportTypes(AsyncCallback<List<SprvReportTypes>> callback);

	void getListReportParameters(long reportId,
			AsyncCallback<List<SprvReportParameters>> callback);

	void logout(AsyncCallback<Void> callback);

	void checkSession(AsyncCallback<Integer> callback);

	void getHashMap(String spprHashMap,
			AsyncCallback<List<ReportParamList>> callback);

	void getGeomPointObjects(AsyncCallback<List<GisDataPoint>> callback);

	void getListGisObjects(AsyncCallback<List<GisObjects>> callback);


}
