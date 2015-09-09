package ru.novaris.novoscan.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ImplFilters;
import ru.novaris.novoscan.client.resources.RefRecord;
import ru.novaris.novoscan.domain.AddRecord;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.domain.SprvClients;
import ru.novaris.novoscan.domain.SprvDeparts;
import ru.novaris.novoscan.events.FilterEvent;
import ru.novaris.novoscan.events.FilterEventHandler;

import com.google.gwt.cell.client.CheckboxCell;
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

public class CwAdmClients extends CwBaseLayout implements ImplConstantsGWT, ImplFilters  {

	private static CwAdmClientsUiBinder uiBinder = GWT
			.create(CwAdmClientsUiBinder.class);

	interface CwAdmClientsUiBinder extends UiBinder<Widget, CwAdmClients> {
	}

	/**
	 * The main CellTable.
	 */
	@UiField(provided = true)
	CellTable<SprvClients> cellTable;
	
	final String title = constants.Clients();
	private static final ProvidesKey<SprvClients> KEY_PROVIDER = new ProvidesKey<SprvClients>() {
		public Object getKey(SprvClients item) {
			return item == null ? null : item.getSpclId();
		}
	};
	final SelectionModel<SprvClients> selectionModel = new MultiSelectionModel<SprvClients>(
			KEY_PROVIDER);
	final ListDataProvider<SprvClients> dataProvider = new ListDataProvider<SprvClients>();
	final ListHandler<SprvClients> sortHandler = new ListHandler<SprvClients>(
			dataProvider.getList());

	public CwAdmClients(Novoscan entryPoint) {
		addHandler();
		initTable();
		initWidget(uiBinder.createAndBindUi(this));
		this.setTitle(title);
		initButtons();
		initFilters();
		initAdd();
		initTableColumns(selectionModel, sortHandler);
		dataProvider.addDataDisplay(cellTable);
		initData();
	}


	/**
	 * Add the columns to the table.
	 */
	private void initTableColumns(
			final SelectionModel<SprvClients> selectionModel,
			ListHandler<SprvClients> sortHandler) {
		Column<SprvClients, Boolean> checkColumn = new Column<SprvClients, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(SprvClients object) {
				// Get the value from the selection model.
				return selectionModel.isSelected(object);
			}
		};
		cellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 40, Unit.PX);
		// Имя
		Column<SprvClients, String> nameColumn = new Column<SprvClients, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvClients object) {
				return object.getSpclName();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn, new Comparator<SprvClients>() {
			@Override
			public int compare(SprvClients o1, SprvClients o2) {
				return o1.getSpclName().compareTo(o2.getSpclName());
			}
		});
		cellTable.addColumn(nameColumn, constants.Clients());
		
		// Департамент
		Column<SprvClients, String> nameDept = new Column<SprvClients, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvClients object) {
				return object.getSpclSpdpName();
			}
		};
		nameDept.setSortable(true);
		sortHandler.setComparator(nameDept, new Comparator<SprvClients>() {
			@Override
			public int compare(SprvClients o1, SprvClients o2) {
				return o1.getSpclSpdpName().compareTo(o2.getSpclSpdpName());
			}
		});
		cellTable.addColumn(nameDept, constants.Departs());

		// Описание
		Column<SprvClients, String> descColumn = new Column<SprvClients, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvClients object) {
				return object.getSpclDesc();
			}
		};
		descColumn.setSortable(true);
		sortHandler.setComparator(descColumn, new Comparator<SprvClients>() {
			@Override
			public int compare(SprvClients o1, SprvClients o2) {
				return o1.getSpclDesc().compareTo(o2.getSpclDesc());
			}
		});
		cellTable.addColumn(descColumn, constants.DescHead());
		
		// Тип
		Column<SprvClients, String> typeColumn = new Column<SprvClients, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvClients object) {
				return object.getSpclTypeName();
			}
		};
		typeColumn.setSortable(true);
		sortHandler.setComparator(typeColumn, new Comparator<SprvClients>() {
			@Override
			public int compare(SprvClients o1, SprvClients o2) {
				return o1.getSpclTypeName().compareTo(o2.getSpclTypeName());
			}
		});
		cellTable.addColumn(typeColumn, constants.TypeHead());
	}

	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		AsyncCallback<List<SprvClients>> callback = new AsyncCallback<List<SprvClients>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<SprvClients> clients) {
				dataProvider.getList().clear();
				dataProvider.getList().addAll(clients);
				dataProvider.refresh();
				pager.setDisplay(cellTable);
			}

		};
		rpcObject.getListClients(callback);
	}

	@Override
	public String getTableName() {
		return "SprvClients";
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
		filterTable.setFieldKey("spclId");
		filterTable.setFieldName(constants.ID());
		filterTable.setFieldExpression(EXPR_LONG);
		filterTable.setFieldType(SQL_TYPE_LONG);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spclName");
		filterTable.setFieldName(constants.Clients());
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
		filterTable.setFieldKey("spclDesc");
		filterTable.setFieldName(constants.DescHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
	}

	@Override
	public void initTable() {
		cellTable = new CellTable<SprvClients>(KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);
		cellTable
				.setSelectionModel(selectionModel, DefaultSelectionEventManager
						.<SprvClients> createCheckboxManager());
		cellTable.addColumnSortHandler(sortHandler);
	}


	@Override
	public void initAdd() {
		AsyncCallback<List<SprvDeparts>> callback = new AsyncCallback<List<SprvDeparts>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<SprvDeparts> departs) {
				List<RefRecord> refRecords = new ArrayList<RefRecord>();
				for(SprvDeparts dep: departs) {
					refRecords.add(new RefRecord(String.valueOf(dep.getSpdpId()), dep.getSpdpName()));
				}
				addRecords.clear();
				AddRecord addRecord = new AddRecord();
				addRecord.setFieldKey("spclName");
				addRecord.setFieldName(constants.Clients());
				addRecord.setFieldType(SQL_TYPE_STRING);
				addRecord.setMandatory(true);
				addRecord.setEditable(true);
				addRecord.setSaveTable(getTableName());
				addRecords.add(addRecord);
				addRecord = new AddRecord();
				addRecord.setFieldKey("spclDesc");
				addRecord.setFieldName(constants.DescHead());
				addRecord.setFieldType(SQL_TYPE_STRING);
				addRecord.setMandatory(false);
				addRecord.setEditable(true);
				addRecord.setSaveTable(getTableName());
				addRecords.add(addRecord);
				addRecord = new AddRecord();
				addRecord.setFieldKey("spclSpdpId");
				addRecord.setFieldName(constants.Departs());
				addRecord.setFieldType(SQL_TYPE_LIST_STRING);
				addRecord.setMandatory(true);
				addRecord.setEditable(true);
				addRecord.setListRefRecord(refRecords);
				addRecord.setSaveTable(getTableName());
				addRecords.add(addRecord);
				addRecord = new AddRecord();
				List<RefRecord> typeRecords = new ArrayList<RefRecord>();
				typeRecords.add(new RefRecord("0", constants.depart()));
				typeRecords.add(new RefRecord("1", constants.pop()));
				addRecord.setFieldKey("spclType");
				addRecord.setFieldName(constants.TypeHead());
				addRecord.setFieldType(SQL_TYPE_LIST_STRING);
				addRecord.setMandatory(true);
				addRecord.setEditable(true);
				addRecord.setListRefRecord(typeRecords);
				addRecord.setSaveTable(getTableName());
				addRecords.add(addRecord);
			}

		};
		rpcObject.getListDeparts(callback);

		
	}
	
	@Override
	public SprvClients getSelectedObject() {
		SprvClients sprvClients = null;
		for (int i = 0; i < dataProvider.getList().size(); i++) {
			if(cellTable.getSelectionModel().isSelected(dataProvider.getList().get(i))) {
				sprvClients = dataProvider.getList().get(i);
			}
		}
		if (sprvClients.getSpclId() < 1000) {
			sprvClients = null;
		} 
		return sprvClients;
	}
	@Override
	public final SprvClients getObject() {
		return new SprvClients();
	}

}
