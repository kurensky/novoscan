/**
 * 
 */
package ru.novaris.novoscan.client;

import java.util.ArrayList;
import java.util.List;

import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.domain.AddRecord;
import ru.novaris.novoscan.domain.FilterTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.view.client.ListDataProvider;


/**
 * @author kurensky
 *
 */
public abstract class CwBaseLayout extends Composite implements ImplConstantsGWT, ImplConstants {
	
	public List<FilterTable> filterTables = new ArrayList<FilterTable>();
	
	public List<AddRecord> addRecords = new ArrayList<AddRecord>();
	
	public FilterTable filterTable;
	
	private final SimplePager.Resources pagerResources = GWT
			.create(SimplePager.Resources.class);	
	@UiField
	PushButton add;
	@UiField
	PushButton delete;
	@UiField
	PushButton modify;
	@UiField
	PushButton filter;
	/**
	 * The pager used to change the range of data.
	 * 
	 * @UiField(provided = true)
	 */
	@UiField(provided = true)
	SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0,
			true);

		
	String title;
	
	ListDataProvider<Object> dataProvider;

	boolean checkAdmin = false;


	public void initButtons() {
		add.setText(constants.Add());
		delete.setText(constants.Delete());
		modify.setText(constants.Modify());
		filter.setText(constants.Find());
	}
	
	@UiHandler("filter")
	void onFilterClick(ClickEvent event) {
		final DbFormFilter dbFormFilter = new DbFormFilter(eventBusData,getTableName());
		dbFormFilter.viewFilter(filterTables);
		dbFormFilter.setTitle(constants.Find());
		dbFormFilter.setText(constants.Find());
		dbFormFilter.setModal(true);
		dbFormFilter.setVisible(true);
		dbFormFilter.show();
	}
	
	@UiHandler("add")
	void onAddClick(ClickEvent event) {
		final DbFormAdd dbFormAdd = new DbFormAdd(eventBusData,getObject());
		dbFormAdd.viewAdd(addRecords);
		dbFormAdd.setTitle(constants.Add());
		dbFormAdd.setText(constants.Add());
		dbFormAdd.setModal(true);
		dbFormAdd.setVisible(true);
		dbFormAdd.show();
	}

	@UiHandler("delete")
	void onDeleteClick(ClickEvent event) {
		if(getSelectedObject() == null) {
			DbMessage message = new DbMessage(MessageType.WARNING, "Сделайте выбор для удаления");
			message.center();
		} else {
			DbFormRemove dbFormRemove = new DbFormRemove(eventBusData, getSelectedObject());
			dbFormRemove.show();
		}
	}

	@UiHandler("modify")
	void onModifyClick(ClickEvent event) {
	}

	
	public String getTableName() {
		return null;
	}
	
	public Object getObject() {
		return null;
	}
	
	public Object getSelectedObject() {
		return null;
	}
	
	public void setCheckAdmin(boolean checkAdmin) {
		this.checkAdmin = checkAdmin;
	}

}
