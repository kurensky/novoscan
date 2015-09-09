package ru.novaris.novoscan.client;

import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ImplFilters;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class CwAdmRequests  extends CwBaseLayout implements ImplConstantsGWT, ImplFilters {

	private static CwAdmRequestsUiBinder uiBinder = GWT
			.create(CwAdmRequestsUiBinder.class);

	interface CwAdmRequestsUiBinder extends UiBinder<Widget, CwAdmRequests> {
	}
	

	public CwAdmRequests() {
		addHandler();
		initTable();
		initWidget(uiBinder.createAndBindUi(this));
		initButtons();
		initFilters();
		//initTableColumns(selectionModel, sortHandler);
		//dataProvider.addDataDisplay(cellTable);
		initData();
		
	}


	private void initData() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initDataProvider() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addHandler() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initTable() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initFilters() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initAdd() {
		// TODO Auto-generated method stub
		
	}

}
