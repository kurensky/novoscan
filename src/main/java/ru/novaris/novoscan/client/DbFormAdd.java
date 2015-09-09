package ru.novaris.novoscan.client;

import java.util.List;

import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.RefRecord;
import ru.novaris.novoscan.domain.AddRecord;
import ru.novaris.novoscan.domain.RecordFields;
import ru.novaris.novoscan.events.FilterEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class DbFormAdd extends DialogBox implements ImplConstantsGWT,
		ImplConstants {

	private static DbFormAddUiBinder uiBinder = GWT
			.create(DbFormAddUiBinder.class);

	interface DbFormAddUiBinder extends UiBinder<Widget, DbFormAdd> {
	}

	@UiField
	PushButton apply;
	@UiField
	PushButton clear;
	@UiField
	PushButton cancel;
	@UiField
	DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
	@UiField
	ScrollPanel scrollPanel = new ScrollPanel();

	private final FlexTable flexTable;
	private final SimpleEventBus eventBus;
	private Object object;
	private List<AddRecord> addRecord;
	private static final int HEADER_ROW_INDEX = 0;

	public DbFormAdd(final SimpleEventBus eventBus, Object record) {
		this.flexTable = new FlexTable();
		this.eventBus = eventBus;
		this.object = record;
	}

	public DbFormAdd(final SimpleEventBus eventBus, Object record,
			FlexTable flexTable) {
		this.flexTable = flexTable;
		this.eventBus = eventBus;
		this.object = record;
	}

	public void viewAdd(List<AddRecord> addRecord) {
		this.addRecord = addRecord;
		setWidget(uiBinder.createAndBindUi(this));
		initButtons();
		initRecord();
		scrollPanel.add(flexTable);

	}

	private void initRecord() {
		addHeader();
		addRows();
	}

	private void addHeader() {

		flexTable.insertRow(HEADER_ROW_INDEX);
		flexTable.getRowFormatter().addStyleName(HEADER_ROW_INDEX,
				"FlexTable-Header");
		addColumn(constants.NameHead());
		addColumn(constants.ValueHead());
		addColumn(constants.Mandatory());
		addColumn(constants.FieldHead());
		flexTable.getColumnFormatter().setWidth(KEY_COLUMN, "0pt");
	}

	/**
	 * Add a row to the flex table.
	 */
	private void addRows() {
		int rowIndex = flexTable.getRowCount();
		for (int i = 0; i < addRecord.size(); i++) {
			Widget widget = addWidget(addRecord.get(rowIndex - 1));
			flexTable.setText(rowIndex, 0, addRecord.get(i).getFieldName());
			flexTable.setWidget(rowIndex, 1, widget);
			flexTable.setText(rowIndex, 2,
					getMandatory(addRecord.get(rowIndex - 1)));
			flexTable.setText(rowIndex, KEY_COLUMN ,addRecord.get(i).getFieldKey());
			applyDataRowStyles();
			flexTable.setCellSpacing(0);
			flexTable.addStyleName("FlexTable");
			flexTable.setWidth("90%");
			rowIndex++;
		}
	}

	private String getMandatory(AddRecord record) {
		return record.isMandatory() ? "*" : " ";
	}

	private Widget addWidget(AddRecord recordTable) {
		Widget widget = new Widget();
		if (recordTable.getFieldType().equals(SQL_TYPE_STRING)) {
			widget = new TextBox();
			if (recordTable.getFieldType() != null) {
				((TextBox) widget).setValue(recordTable.getFieldValue());
				((TextBox) widget).setEnabled(recordTable.isEditable());
			}
		} else if (recordTable.getFieldType().equals(SQL_TYPE_DATE)) {
			widget = new DateBox();
			((DateBox) widget).setEnabled(recordTable.isEditable());
		} else if (recordTable.getFieldType().equals(SQL_TYPE_LIST_STRING)) {
			final ListBox listBox = new ListBox();
			listBox.setEnabled(recordTable.isEditable());
			for (RefRecord rec : recordTable.getListRefRecord()) {
				listBox.addItem(rec.getValue(), String.valueOf(rec.getKey()));
			}

			widget = listBox;
		} else {
			widget = new TextBox();
			if (recordTable.getFieldValue() != null) {
				((TextBox) widget).setValue(recordTable.getFieldValue());
				((TextBox) widget).setEnabled(recordTable.isEditable());
			}
		}
		return widget;
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

	private void initButtons() {
		cancel.setText(constants.Cancel());
		apply.setText(constants.Apply());
		clear.setText(constants.Clear());
	}

	@UiHandler("cancel")
	void onCancelClick(ClickEvent event) {
		this.removeFromParent();
	}

	@UiHandler("apply")
	void onApplyClick(ClickEvent event) {
		this.saveRecord();
	}

	private void saveRecord() {
		AsyncCallback<Long> callback = new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				new DbMessage(MessageType.ERROR, "Ошибка добавления : "
						+ caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Long result) {
				new DbMessage(MessageType.INFO, "Ид объекта: " + result);
				eventBus.fireEvent(new FilterEvent());
				removeFromParent();
			}

		};
		RecordFields recordFields = new RecordFields(object, flexTable);
		recordFields.setRecord(addRecord);
		recordFields.addRecord(callback);

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

}
