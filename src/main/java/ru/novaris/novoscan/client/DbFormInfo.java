package ru.novaris.novoscan.client;

import java.util.List;

import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.domain.FilterTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.datepicker.client.DateBox;


public class DbFormInfo extends DialogBox implements ImplConstantsGWT, ImplConstants {

	private static DbFormInfoUiBinder uiBinder = GWT
			.create(DbFormInfoUiBinder.class);

	interface DbFormInfoUiBinder extends UiBinder<Widget, DbFormInfo> {
	}

	@UiField
	PushButton apply;
	@UiField
	PushButton cancel;
	@UiField
	DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
	@UiField
	ScrollPanel scrollPanel = new ScrollPanel();

	private static final int HEADER_ROW_INDEX = 0;

	private FlexTable flexTable = new FlexTable();

	@SuppressWarnings("unused")
	private final SimpleEventBus eventBus;
	
	@SuppressWarnings("unused")
	private String tableName;
	
	public DbFormInfo(final SimpleEventBus eventBus, String tableName) {
		this.eventBus = eventBus;
		this.tableName = tableName;
	}

	public void viewFields(List<FilterTable> filters) {
		// Do not refresh the headers and footers every time the data is
		// updated.
		setWidget(uiBinder.createAndBindUi(this));
		initButtons();
		initFilds();
		scrollPanel.add(flexTable);
	}

	private void initFilds() {
		addHeader();
		addRows();
	}

	private void addColumn(Object columnHeading) {
		Widget widget = createCellWidget(columnHeading);
		int cell = flexTable.getCellCount(HEADER_ROW_INDEX);
		widget.setWidth("100%");
		widget.addStyleName("FlexTable-ColumnLabel");
		flexTable.setWidget(HEADER_ROW_INDEX, cell, widget);
		flexTable.getCellFormatter().addStyleName(HEADER_ROW_INDEX, cell,
				"FlexTable-ColumnLabelCell");

	}

	private Widget createCellWidget(Object cellObject) {
		Widget widget = null;
		if (cellObject instanceof Widget)
			widget = (Widget) cellObject;
		else
			widget = new Label(cellObject.toString());
		return widget;
	}

	private void addHeader() {
		flexTable.insertRow(HEADER_ROW_INDEX);
		flexTable.getRowFormatter().addStyleName(HEADER_ROW_INDEX,
				"FlexTable-Header");
		addColumn(constants.NameHead());
		addColumn(constants.DescHead());
		addColumn("");
	}

	private void applyDataRowStyles() {
		HTMLTable.RowFormatter rf = flexTable.getRowFormatter();

		for (int row = 1; row < flexTable.getRowCount(); ++row) {
			if ((row % 2) != 0) {
				rf.addStyleName(row, "FlexTable-OddRow");
			} else {
				rf.addStyleName(row, "FlexTable-EvenRow");
			}
		}
	}

	/**
	 * Add a row to the flex table.
	 */
	private void addRows() {


			applyDataRowStyles();
			flexTable.setCellSpacing(0);
			flexTable.addStyleName("FlexTable");
			flexTable.setWidth("90%");

	}

	@SuppressWarnings("unused")
	private Widget addWidget(FilterTable filterTable) {
		Widget widget = new Widget();
		if (filterTable.getFieldType().equals(SQL_TYPE_STRING)) {
			widget = new TextBox();
			if (filterTable.getFieldValue1() != null) {
				((TextBox) widget).setValue(filterTable.getFieldValue1());
			}
		} else if (filterTable.getFieldType().equals(SQL_TYPE_LONG)) {
			widget = new TextBox();
			if (filterTable.getFieldValue1() != null) {
				((TextBox) widget).setValue(filterTable.getFieldValue1());
			}
		} else if (filterTable.getFieldType().equals(SQL_TYPE_DATE)) {
			widget = new DateBox();
		} else {
			widget = new TextBox();
			if (filterTable.getFieldValue1() != null) {
				((TextBox) widget).setValue(filterTable.getFieldValue1());
			}
		}
		return widget;
	}

	private void initButtons() {
		cancel.setText(constants.Cancel());
		apply.setText(constants.Apply());
	}



	@UiHandler("cancel")
	void onCancelClick(ClickEvent event) {
		this.removeFromParent();
	}

	@UiHandler("apply")
	void onApplyClick(ClickEvent event) {
		this.applyData();
	}

	
	private void applyData() {
//		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				new DbMessage(MessageType.ERROR, caught.getMessage());
//			}
//			@Override
//			public void onSuccess(Void result) {
//				eventBus.fireEvent(new FilterEvent());
//				removeFromParent();
//			}
//		};
//		rpcObject.setGisObject(fields, callback);
	}

	


}
