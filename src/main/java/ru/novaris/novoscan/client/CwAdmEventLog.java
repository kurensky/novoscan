package ru.novaris.novoscan.client;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ImplFilters;
import ru.novaris.novoscan.domain.EventLog;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.events.FilterEvent;
import ru.novaris.novoscan.events.FilterEventHandler;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;


public class CwAdmEventLog  extends CwBaseLayout implements ImplConstantsGWT, ImplFilters {

	private static CwAdmEventLogUiBinder uiBinder = GWT
			.create(CwAdmEventLogUiBinder.class);

	interface CwAdmEventLogUiBinder extends UiBinder<Widget, CwAdmEventLog> {
	}

	/**
	 * The main CellTable.
	 */
	@UiField(provided = true)
	CellTable<EventLog> cellTable;
	
	final String title = constants.EventLog();
	private static final ProvidesKey<EventLog> KEY_PROVIDER = new ProvidesKey<EventLog>() {
		public Object getKey(EventLog item) {
			return item == null ? null : item.getEvlgId();
		}
	};
	final SelectionModel<EventLog> selectionModel = new MultiSelectionModel<EventLog>(
			KEY_PROVIDER);
	final ListDataProvider<EventLog> dataProvider = new ListDataProvider<EventLog>();
	final ListHandler<EventLog> sortHandler = new ListHandler<EventLog>(
			dataProvider.getList());



	public CwAdmEventLog(Novoscan entryPoint) {
		addHandler();
		initTable();
		initWidget(uiBinder.createAndBindUi(this));
		initButtons();
		this.setTitle(title);
		this.add.setVisible(false);
		this.modify.setVisible(false);
		initFilters();
		initTableColumns(selectionModel, sortHandler);
		dataProvider.addDataDisplay(cellTable);
		initData();
	}
	private void initTableColumns(final SelectionModel<EventLog> selectionModel,
			ListHandler<EventLog> sortHandler) {
		addHandler();
		Column<EventLog, Boolean> checkColumn = new Column<EventLog, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(EventLog object) {
				return selectionModel.isSelected(object);
			}
		};
		cellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 40, Unit.PX);
		// Имя
		Column<EventLog, String> moduleColumn = new Column<EventLog, String>(
				new TextCell()) {
			@Override
			public String getValue(EventLog object) {
				return object.getEvlgModname();
			}
		};
		moduleColumn.setSortable(true);
		sortHandler.setComparator(moduleColumn, new Comparator<EventLog>() {
			@Override
			public int compare(EventLog o1, EventLog o2) {
				return o1.getEvlgModname().compareTo(o2.getEvlgModname());
			}
		});
		cellTable.addColumn(moduleColumn, constants.NameHead());
		// описание
		Column<EventLog, String> descColumn = new Column<EventLog, String>(
				new TextCell()) {
			@Override
			public String getValue(EventLog object) {
				return object.getEvlgText();
			}
		};
		descColumn.setSortable(true);
		sortHandler.setComparator(descColumn, new Comparator<EventLog>() {
			@Override
			public int compare(EventLog o1, EventLog o2) {
				return o1.getEvlgText().compareTo(o2.getEvlgText());
			}
		});
		cellTable.addColumn(descColumn, constants.DescHead());
		// информация
		Column<EventLog, String> infoColumn = new Column<EventLog, String>(
				new TextCell()) {
			@Override
			public String getValue(EventLog object) {
				return object.getEvlgInfo();
			}
		};
		infoColumn.setSortable(true);
		sortHandler.setComparator(infoColumn, new Comparator<EventLog>() {
			@Override
			public int compare(EventLog o1, EventLog o2) {
				return o1.getEvlgInfo().compareTo(o2.getEvlgInfo());
			}
		});
		cellTable.addColumn(infoColumn, constants.Information());
		// Дата создания
		Column<EventLog, Date> dateColumn = new Column<EventLog, Date>(
				new DateCell(DATE_TIME_FORMAT)) {
			@Override
			public Date getValue(EventLog object) {
				return object.getEvlgDt();
			}
		};
		dateColumn.setSortable(true);
		sortHandler.setComparator(dateColumn, new Comparator<EventLog>() {
			@Override
			public int compare(EventLog o1, EventLog o2) {
				return o1.getEvlgDt().compareTo(o2.getEvlgDt());
			}
		});
		cellTable.addColumn(dateColumn, constants.cwDataGridDate());
		// Тип разрешения
		Column<EventLog, String> typeColumn = new Column<EventLog, String>(
				new TextCell()) {
			@Override
			public String getValue(EventLog object) {
				return getLogType(object);
			}
		};
		typeColumn.setSortable(true);
		sortHandler.setComparator(typeColumn, new Comparator<EventLog>() {
			@Override
			public int compare(EventLog o1, EventLog o2) {
				return getLogType(o1).compareTo(getLogType(o2));
			}
		});
		cellTable.addColumn(typeColumn, constants.TypeHead());
	}

	private String getLogType(EventLog log) {
		String logTypeName = "";
		if(log.getEvlgType() == 0) {
			logTypeName = "Информация";
		} else if (log.getEvlgType() < 0) {
			logTypeName = "Ошибка: "+log.getEvlgType();
		} else {
			logTypeName = "Предупреждение: "+log.getEvlgType();	
		}
		return logTypeName;
	}	
	
	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		AsyncCallback<List<EventLog>> callback = new AsyncCallback<List<EventLog>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<EventLog> acl) {
				dataProvider.getList().clear();
				dataProvider.getList().addAll(acl);
				dataProvider.refresh();
				pager.setDisplay(cellTable);
			}

		};
		rpcObject.getListEvent(callback);
	}
	@Override
	public String getTableName() {
		return "EventLog";
	}
	@Override
	public void initDataProvider() {
		refreshData();
		dataProvider.addDataDisplay(cellTable);
	}
	@Override
	public void refreshData() {
		initData();		
	}
	@Override
	public void addHandler() {
		eventBusData.addHandler(FilterEvent.TYPE, new FilterEventHandler() {
			@Override
			public void apply(FilterEvent event) {
				refreshData();
			}
		});
	}
	@Override
	public void initFilters() {
		// Переменная часть полей.
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("evlgId");
		filterTable.setFieldName(constants.ID());
		filterTable.setFieldExpression(EXPR_LONG);
		filterTable.setFieldType(SQL_TYPE_LONG);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("evlgUser");
		filterTable.setFieldName(constants.Login());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spclSpdpName");
		filterTable.setFieldName(constants.Departs());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("evlgModname");
		filterTable.setFieldName(constants.NameHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("evlgInfo");
		filterTable.setFieldName(constants.Information());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("evlgText");
		filterTable.setFieldName(constants.DescHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
	}
	@Override
	public void initTable() {
		cellTable = new CellTable<EventLog>(KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);
		cellTable
				.setSelectionModel(selectionModel, DefaultSelectionEventManager
						.<EventLog> createCheckboxManager());
		cellTable.addColumnSortHandler(sortHandler);
	}
	@Override
	public void initAdd() {
		// TODO Auto-generated method stub
		
	}

}
