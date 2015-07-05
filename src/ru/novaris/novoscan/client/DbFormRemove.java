package ru.novaris.novoscan.client;

import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.domain.AccountRoles;
import ru.novaris.novoscan.domain.Accounts;
import ru.novaris.novoscan.domain.Acl;
import ru.novaris.novoscan.domain.Request;
import ru.novaris.novoscan.domain.RequestRoute;
import ru.novaris.novoscan.domain.SprvClients;
import ru.novaris.novoscan.domain.SprvDeparts;
import ru.novaris.novoscan.domain.SprvModules;
import ru.novaris.novoscan.domain.SprvObjects;
import ru.novaris.novoscan.events.FilterEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;


public class DbFormRemove extends DialogBox implements ImplConstantsGWT  {
	
	private static DbFormRemoveUiBinder uiBinder = GWT
			.create(DbFormRemoveUiBinder.class);

	interface DbFormRemoveUiBinder extends UiBinder<Widget, DbFormRemove> {
	}
	@UiField
	PushButton apply;
	@UiField
	PushButton cancel;	
	@UiField Label removeInfo;
	@UiField DockLayoutPanel panel;

	private final SimpleEventBus eventBus;
	private Object record;
	
	public DbFormRemove(final SimpleEventBus eventBus, Object record) {
		this.eventBus = eventBus;
		this.record = record;
		this.setText(constants.Delete());
		if (record == null) {
			new DbMessage(MessageType.WARNING, "Невозможно удалить. Выберите объект!");
			this.removeFromParent();
		} else {
			setWidget(uiBinder.createAndBindUi(this));
			initButtons();
		}
	}

	private void initButtons() {
		removeInfo.setText(constants.Delete()+"?");
		cancel.setText(constants.Cancel());
		apply.setText(constants.Apply());
	}
	@UiHandler("cancel")
	void onCancelClick(ClickEvent event) {
		this.removeFromParent();
	}

	@UiHandler("apply")
	void onApplyClick(ClickEvent event) {
		this.removeRecord();
	}

	private void removeRecord() {
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				new DbMessage(MessageType.ERROR, "Ошибка удаления объекта : "+caught.getLocalizedMessage());
			}
			@Override
			public void onSuccess(Void result) {
				eventBus.fireEvent(new FilterEvent());
				removeFromParent();
			}
			
		};
		if(record instanceof SprvDeparts) {
			SprvDeparts sprvDeparts = (SprvDeparts) record;
			rpcObject.delDeparts(sprvDeparts, callback);
		} else if (record instanceof SprvClients) {
			SprvClients sprvClients = (SprvClients) record;
			rpcObject.delClients(sprvClients, callback);
		} else if (record instanceof SprvObjects) {
			SprvObjects sprvObjects = (SprvObjects) record;
			rpcObject.delObjects(sprvObjects, callback);
		} else if (record instanceof SprvModules) {
			SprvModules sprvModules = (SprvModules) record;
			rpcObject.delModules(sprvModules, callback);
		} else if (record instanceof Request) {
			Request request = (Request) record;
			rpcObject.delRequest(request, callback);
		} else if (record instanceof RequestRoute) {
			RequestRoute requestRoute = (RequestRoute) record;
			rpcObject.delRequestRoute(requestRoute, callback);
		} else if (record instanceof Accounts) {
			Accounts accounts = (Accounts) record;
			rpcObject.delAccounts(accounts, callback);
		} else if (record instanceof Acl) {
			Acl acl = (Acl) record;
			rpcObject.delAcl(acl, callback);	
		} else if (record instanceof AccountRoles) {
			AccountRoles accountRoles = (AccountRoles) record;
			rpcObject.delAccountRoles(accountRoles, callback);
		}
	}

}
