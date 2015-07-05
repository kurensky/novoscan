package ru.novaris.novoscan.domain;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import ru.novaris.novoscan.client.DbMessage;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.domain.Request;
import ru.novaris.novoscan.domain.RequestRoute;
import ru.novaris.novoscan.domain.SprvClients;
import ru.novaris.novoscan.domain.SprvDeparts;
import ru.novaris.novoscan.domain.SprvModules;
import ru.novaris.novoscan.domain.SprvObjects;
import ru.novaris.novoscan.domain.AccountRoles;
import ru.novaris.novoscan.domain.Accounts;
import ru.novaris.novoscan.domain.Acl;

public class RecordFields implements ImplConstantsGWT {
	private Object object;
	private FlexTable flexTable;
	private List<AddRecord> addRecord;

	

	public RecordFields() {
	}

	public RecordFields(Object object, FlexTable flexTable) {
		this.object = object;
		this.flexTable = flexTable;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public void setFlexTable(FlexTable flexTable) {
		this.flexTable = flexTable;
	}

	public void setRecord(List<AddRecord> addRecord) {
		this.addRecord = addRecord;
	}

	public void addRecord(AsyncCallback<Long> callback) {
		if (addRecord != null && addRecord.size() > 0) {
			if (object instanceof SprvDeparts) {
				SprvDeparts sprvDeparts = (SprvDeparts) object;
				for (int i = 0; i < addRecord.size(); i++) {
					for (int k = 1; k < flexTable.getRowCount(); k++) {
						if (addRecord.get(i).getFieldKey()
								.equalsIgnoreCase(flexTable.getText(k, KEY_COLUMN))) {
							sprvDeparts.setSpdpName(((TextBox) flexTable
									.getWidget(k, 1)).getValue());
						}
					}
				}
				rpcObject.addDeparts(sprvDeparts, callback);
			} else if (object instanceof SprvClients) {
				SprvClients sprvClients = (SprvClients) object;
				for (int i = 0; i < addRecord.size(); i++) {
					for (int k = 1; k < flexTable.getRowCount(); k++) {
						GWT.log("Поле : " + addRecord.get(k).getFieldKey());
						if (addRecord.get(k).getFieldKey()
								.equalsIgnoreCase("spclId")) {
							sprvClients.setSpclId(Long.parseLong(((TextBox) flexTable
									.getWidget(k, 1)).getValue()));
						} else if (addRecord.get(i).getFieldKey()
								.equalsIgnoreCase("spclName")) {
							sprvClients.setSpclName(((TextBox) flexTable
									.getWidget(k, 1)).getValue());
						} else if (addRecord.get(i).getFieldKey()
								.equalsIgnoreCase("spclSpdpId")) {
							ListBox depaListBox = (ListBox) flexTable
									.getWidget(k, 1);
							depaListBox.getSelectedValue();
							sprvClients.setSpclSpdpId(Long.parseLong(depaListBox.getSelectedValue()));
						}  else if (addRecord.get(i).getFieldKey()
								.equalsIgnoreCase("spclType")) {
							ListBox depaListBox = (ListBox) flexTable
									.getWidget(k, 1);
							depaListBox.getSelectedValue();
							sprvClients.setSpclType(Integer.parseInt(depaListBox.getSelectedValue()));
						} else if (addRecord.get(i).getFieldKey()
								.equalsIgnoreCase("spclDesc")) {
							sprvClients.setSpclDesc(((TextBox) flexTable
									.getWidget(k, 1)).getValue());	
						}
							
					}
				}
				rpcObject.addClients(sprvClients, callback);
			} else if (object instanceof SprvObjects) {
				SprvObjects sprvObjects = (SprvObjects) object;
				rpcObject.addObjects(sprvObjects, callback);
			} else if (object instanceof SprvModules) {
				SprvModules sprvModules = (SprvModules) object;
				rpcObject.addModules(sprvModules, callback);
			} else if (object instanceof Request) {
				Request request = (Request) object;
				rpcObject.addRequest(request, callback);
			} else if (object instanceof RequestRoute) {
				RequestRoute requestRoute = (RequestRoute) object;
				rpcObject.addRequestRoute(requestRoute, callback);
			} else if (object instanceof Accounts) {
				Accounts accounts = (Accounts) object;
				rpcObject.addAccounts(accounts, callback);
			} else if (object instanceof Acl) {
				Acl acl = (Acl) object;
				rpcObject.addAcl(acl, callback);
			} else if (object instanceof AccountRoles) {
				AccountRoles accountRoles = (AccountRoles) object;
				rpcObject.addAccountRoles(accountRoles, callback);
			}
		} else {
			DbMessage message = new DbMessage(MessageType.ERROR, "Ошибка добавления записи!");
			message.center();
		}
	}

	public void updRecord(AsyncCallback<Long> callback) {
		//
	}

	public void delRecord(AsyncCallback<Long> callback) {
		//
	}

}
