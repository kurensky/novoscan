package ru.novaris.novoscan.client;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ImplFilters;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.domain.SprvObjects;
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

public class CwAdmObjects extends CwBaseLayout implements ImplConstantsGWT, ImplFilters {

	private static CwAdmObjectsUiBinder uiBinder = GWT
			.create(CwAdmObjectsUiBinder.class);

	interface CwAdmObjectsUiBinder extends UiBinder<Widget, CwAdmObjects> {
	}
	
	@UiField(provided = true)
	CellTable<SprvObjects> cellTable;
	final String title = constants.Objects();
	public static final ProvidesKey<SprvObjects> KEY_PROVIDER = new ProvidesKey<SprvObjects>() {
		public Object getKey(SprvObjects item) {
			return item == null ? null : item.getSpobId();
		}
	};
	final ListDataProvider<SprvObjects> dataProvider = new ListDataProvider<SprvObjects>();
	final ListHandler<SprvObjects> sortHandler = new ListHandler<SprvObjects>(
			dataProvider.getList());
	final SelectionModel<SprvObjects> selectionModel = new MultiSelectionModel<SprvObjects>(
			KEY_PROVIDER);
	
	public CwAdmObjects(Novoscan entryPoint)  {
		addHandler();
		initTable();
		initWidget(uiBinder.createAndBindUi(this));
		initButtons();
		initFilters();
		initTableColumns(selectionModel, sortHandler);
		dataProvider.addDataDisplay(cellTable);
		initData();
	}
	
	public CwAdmObjects(Novoscan entryPoint, boolean checkAdmin)  {
		this.checkAdmin = checkAdmin;
		addHandler();
		initTable();
		initWidget(uiBinder.createAndBindUi(this));
		initButtons();
		initFilters();
		initTableColumns(selectionModel, sortHandler);
		dataProvider.addDataDisplay(cellTable);
		initData();
	}
	private void initTableColumns(
			final SelectionModel<SprvObjects> selectionModel,
			ListHandler<SprvObjects> sortHandler) {
		Column<SprvObjects, Boolean> checkColumn = new Column<SprvObjects, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(SprvObjects object) {
				return selectionModel.isSelected(object);
			}
		};
		cellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 40, Unit.PX);
		
		// Имя
		Column<SprvObjects, String> nameColumn = new Column<SprvObjects, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvObjects object) {
				return object.getSpobName();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn, new Comparator<SprvObjects>() {
			@Override
			public int compare(SprvObjects o1, SprvObjects o2) {
				return o1.getSpobName().compareTo(o2.getSpobName());
			}
		});
		cellTable.addColumn(nameColumn, constants.NameHead());

		// Описание
		Column<SprvObjects, String> descColumn = new Column<SprvObjects, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvObjects object) {
				return object.getSpobDesc();
			}
		};
		descColumn.setSortable(true);
		sortHandler.setComparator(descColumn, new Comparator<SprvObjects>() {
			@Override
			public int compare(SprvObjects o1, SprvObjects o2) {
				return o1.getSpobDesc().compareTo(o2.getSpobDesc());
			}
		});
		cellTable.addColumn(descColumn, constants.DescHead());

		// Клиент
		Column<SprvObjects, String> clieColumn = new Column<SprvObjects, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvObjects object) {
				return object.getSpobSpclName();
			}
		};
		clieColumn.setSortable(true);
		sortHandler.setComparator(clieColumn, new Comparator<SprvObjects>() {
			@Override
			public int compare(SprvObjects o1, SprvObjects o2) {
				return o1.getSpobSpclName().compareTo(o2.getSpobSpclName());
			}
		});
		cellTable.addColumn(clieColumn, constants.Clients());
		
		// Тип объекта
		Column<SprvObjects, String> typeColumn = new Column<SprvObjects, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvObjects object) {
				return object.getSpobSpotName();
			}
		};
		typeColumn.setSortable(true);
		sortHandler.setComparator(typeColumn, new Comparator<SprvObjects>() {
			@Override
			public int compare(SprvObjects o1, SprvObjects o2) {
				return o1.getSpobSpotName().compareTo(o2.getSpobSpotName());
			}
		});
		cellTable.addColumn(typeColumn, constants.TypeHead());	

		
		// Дата создания
		Column<SprvObjects, Date> dateColumn = new Column<SprvObjects, Date>(
				new DateCell(DATE_TIME_FORMAT)) {
			@Override
			public Date getValue(SprvObjects object) {
				return object.getSpobDtCreate();
			}
		};
		dateColumn.setSortable(true);
		sortHandler.setComparator(dateColumn, new Comparator<SprvObjects>() {
			@Override
			public int compare(SprvObjects o1, SprvObjects o2) {
				return o1.getSpobDtCreate().compareTo(o2.getSpobDtCreate());
			}
		});
		cellTable.addColumn(dateColumn, constants.cwDataGridDate());	
		
		// Дата изменения
		Column<SprvObjects, Date> dateModifyColumn = new Column<SprvObjects, Date>(
				new DateCell(DATE_TIME_FORMAT)) {
			@Override
			public Date getValue(SprvObjects object) {
				return object.getSpobDtModify();
			}
		};
		dateModifyColumn.setSortable(true);
		sortHandler.setComparator(dateModifyColumn, new Comparator<SprvObjects>() {
			@Override
			public int compare(SprvObjects o1, SprvObjects o2) {
				return o1.getSpobDtModify().compareTo(o2.getSpobDtModify());
			}
		});
		cellTable.addColumn(dateModifyColumn, constants.cwDataGridDateModify());
	}

	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		AsyncCallback<List<SprvObjects>> callback = new AsyncCallback<List<SprvObjects>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<SprvObjects> SprvObjects) {
				dataProvider.getList().clear();
				dataProvider.getList().addAll(SprvObjects);
				dataProvider.refresh();
				pager.setDisplay(cellTable);
			}

		};
		rpcObject.getListObjects(checkAdmin, callback);
	}


	@Override
	public String getTableName() {
		return "SprvObjects";
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
		filterTables.clear();
		// Переменная часть полей.		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spobId");
		filterTable.setFieldName(constants.ID());
		filterTable.setFieldExpression(EXPR_LONG);
		filterTable.setFieldType(SQL_TYPE_LONG);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spobName");
		filterTable.setFieldName(constants.NameHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spobDesc");
		filterTable.setFieldName(constants.DescHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spobSpclName");
		filterTable.setFieldName(constants.Clients());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spobSpotName");
		filterTable.setFieldName(constants.TypeHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		/* TODO
		 *  Научить работать с датами
		 */
	/*	
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spobDtCreate");
		filterTable.setFieldName(constants.cwDataGridDate());
		filterTable.setFieldExpression(exprDate);
		filterTable.setFieldType(SQL_TYPE_DATE);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spobDtModify");
		filterTable.setFieldName(constants.cwDataGridDateModify());
		filterTable.setFieldExpression(exprDate);
		filterTable.setFieldType(SQL_TYPE_DATE);
		filterTables.add(filterTable);
		*/
	}


	@Override
	public void initTable() {
		cellTable = new CellTable<SprvObjects>(KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);
		cellTable.setSelectionModel(selectionModel, DefaultSelectionEventManager
						.<SprvObjects> createCheckboxManager());
		cellTable.addColumnSortHandler(sortHandler);
	}
	@Override
	public void initAdd() {
		// TODO Auto-generated method stub
		
	}

}
