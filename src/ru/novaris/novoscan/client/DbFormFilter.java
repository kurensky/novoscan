package ru.novaris.novoscan.client;

import java.util.ArrayList;
import java.util.List;

import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.events.FilterEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.datepicker.client.DateBox;


public class DbFormFilter extends DialogBox implements ImplConstantsGWT, ImplConstants {

	private static DbFormFilterUiBinder uiBinder = GWT
			.create(DbFormFilterUiBinder.class);

	interface DbFormFilterUiBinder extends UiBinder<Widget, DbFormFilter> {
	}

	@UiField
	PushButton clear;
	@UiField
	PushButton apply;
	@UiField
	PushButton cancel;
	@UiField
	DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
	@UiField
	ScrollPanel scrollPanel = new ScrollPanel();

	private List<FilterTable> filters = new ArrayList<FilterTable>();

	private static final int HEADER_ROW_INDEX = 0;

	private FlexTable flexTable = new FlexTable();
	
	private String filterString;
	private final SimpleEventBus eventBus;
	private String tableName;
	
	public DbFormFilter(final SimpleEventBus eventBus, String tableName) {
		this.eventBus = eventBus;
		this.tableName = tableName;
	}

	public void viewFilter(List<FilterTable> filters) {
		// Create a Pager to control the table.
		this.filters.clear();
		for (int i = 0; i < filters.size(); i++) {
			if(filters.get(i).getFilterTable().equals(tableName)) {
				this.filters.add(filters.get(i));
			}
		}
		// Do not refresh the headers and footers every time the data is
		// updated.
		setWidget(uiBinder.createAndBindUi(this));
		initButtons();
		initFilters();
		scrollPanel.add(flexTable);
	}

	private void initFilters() {
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
		addColumn(constants.ExprHead());
		addColumn(constants.ValueHead());
		addColumn(constants.LogicalHead());
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
		int rowIndex = flexTable.getRowCount();		
		for (int i = 0; i < filters.size(); i++) {
			final ListBox listLogicalBox = new ListBox();
			listLogicalBox.addItem(SQL_AND);
			listLogicalBox.addItem(SQL_OR);
			flexTable.setText(rowIndex, 0, filters.get(i).getFieldName());
			final ListBox listBox = new ListBox();
			final int row = rowIndex;
			for (int l = 0; l < filters.get(i).getFieldExpression().size(); l++) {
				listBox.addItem(filters.get(i).getFieldExpression().get(l));
				if(filters.get(i).getFieldExpression().get(l).equals(filters.get(i).getExpression())) {
					listBox.setSelectedIndex(l);
				}
			}
			for (int k = 0; k < listLogicalBox.getItemCount(); k++) {
				if(listLogicalBox.getItemText(k).equals(filters.get(i).getFieldLogical())) {
					listLogicalBox.setSelectedIndex(k);
				}
			}
			listBox.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					int selectedIndex = listBox.getSelectedIndex();
					// ToDo изменение списка переменных.
					Widget widget2 = addWidget(filters.get(row - 1));
					if (listBox.getValue(selectedIndex).equals(SQL_BETWEEN)) {
						widget2.setVisible(true);
					} else {
						widget2.setVisible(false);
					}
					flexTable.setWidget(row, 3,
							widget2);
				}

			});
			
			
			flexTable.setWidget(row, 1, listBox);
			Widget widget1 = addWidget(filters.get(row - 1));
			flexTable.setWidget(row, 2, widget1);
			Widget widget2 = addWidget(filters.get(row - 1));
			if (filters.get(i).getExpression().equals(SQL_BETWEEN)) {
				widget2.setVisible(true);
			} else {
				widget2.setVisible(false);
			}
			flexTable.setWidget(row, 3, widget2);
			flexTable.setWidget(row, 4, listLogicalBox);
			applyDataRowStyles();
			flexTable.setCellSpacing(0);
			flexTable.addStyleName("FlexTable");
			flexTable.setWidth("90%");
			rowIndex++;
		}
	}

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
		clear.setText(constants.Clear());
		cancel.setText(constants.Cancel());
		apply.setText(constants.Apply());
	}

	@UiHandler("clear")
	void onClearClick(ClickEvent event) {
		this.clearFilter();
	}

	private void clearFilter() {
		flexTable.removeAllRows();
		for (int i = 0; i < filters.size(); i++) {
			filters.get(i).setFieldValue1(null);
			filters.get(i).setFieldValue2(null);
		}
		initFilters();
	}

	@UiHandler("cancel")
	void onCancelClick(ClickEvent event) {
		this.removeFromParent();
	}

	@UiHandler("apply")
	void onApplyClick(ClickEvent event) {
		this.applyFilter();
		this.setFilter();
	}

	private void applyFilter() {
		int i = HEADER_ROW_INDEX;
			ListBox listBox = new ListBox();
			int row = flexTable.getRowCount() - 1;
			int rowFlexTable;
			for (i = HEADER_ROW_INDEX; i < row; i++) {
				rowFlexTable = i + 1;
				listBox = (ListBox) flexTable.getWidget(rowFlexTable, 1);
				filters.get(i).setExpression(
						listBox.getValue(listBox.getSelectedIndex()));
				filters.get(i).setFieldValue1(getWidgetValue(flexTable.getWidget(rowFlexTable, 2), i));
				if (filters.get(i).getExpression().equals(SQL_BETWEEN)) {
					filters.get(i).setFieldValue2(
							getWidgetValue(flexTable.getWidget(rowFlexTable, 3), i));
				}
				listBox = (ListBox) flexTable.getWidget(rowFlexTable, 4);
				filters.get(i).setFieldLogical(listBox.getValue(listBox.getSelectedIndex()));
			}
			
	
	}
	
	private void setFilter() {
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				new DbMessage(MessageType.ERROR, caught.getMessage());
			}
			@Override
			public void onSuccess(Void result) {
				setFilterString("");
				eventBus.fireEvent(new FilterEvent());
				removeFromParent();
			}
		};
		rpcObject.setFilter(filters, callback);
	}


	protected void setFilterString(String filterString) {
		this.filterString = filterString;
	}
	
	public String getFilterString() {
		return this.filterString;
	}

	private String getWidgetValue(Widget widget, int row) {
		String value = "";
		if (filters.get(row).getFieldType().equals(SQL_TYPE_STRING)) {
			value = ((TextBox) widget).getValue();
		} else if (filters.get(row).getFieldType().equals(SQL_TYPE_LONG)) {
			value = ((TextBox) widget).getValue();
			try {
				value = String.valueOf(Long.valueOf(value));
			} catch (Exception e) {
				value = null;
				((TextBox) widget).setValue("");
			}
		} else if (filters.get(row).getFieldType().equals(SQL_TYPE_DATE)) {
			value = String.valueOf((DateBox) widget);
		} else {
			value = ((TextBox) widget).getValue();
		}
		return value;
	}

}
