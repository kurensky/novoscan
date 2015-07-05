package ru.novaris.novoscan.client;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ImplFilters;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.domain.SprvModules;
import ru.novaris.novoscan.events.FilterEvent;
import ru.novaris.novoscan.events.FilterEventHandler;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
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

public class CwAdmModules extends CwBaseLayout implements ImplConstantsGWT, ImplFilters {

	private static CwAdmModulesUiBinder uiBinder = GWT
			.create(CwAdmModulesUiBinder.class);

	interface CwAdmModulesUiBinder extends UiBinder<Widget, CwAdmModules> {
	}
	@UiField
	CellTable<SprvModules> cellTable;

	final String title = constants.Terminals();
	private static final ProvidesKey<SprvModules> KEY_PROVIDER = new ProvidesKey<SprvModules>() {
		public Object getKey(SprvModules item) {
			return item == null ? null : item.getSpmdId();
		}
	};
	final SelectionModel<SprvModules> selectionModel = new MultiSelectionModel<SprvModules>(
			KEY_PROVIDER);
	final ListDataProvider<SprvModules> dataProvider = new ListDataProvider<SprvModules>();
	final ListHandler<SprvModules> sortHandler = new ListHandler<SprvModules>(
			dataProvider.getList());
	
	public CwAdmModules(Novoscan entryPoint) {
		addHandler();
		initTable();
		initWidget(uiBinder.createAndBindUi(this));
		this.setTitle(title);
		initButtons();
		initFilters();
		initTableColumns(selectionModel, sortHandler);
		dataProvider.addDataDisplay(cellTable);
		initData();
	}


	/**
	 * Add the columns to the table.
	 */
	private void initTableColumns(
			final SelectionModel<SprvModules> selectionModel,
			ListHandler<SprvModules> sortHandler) {
		// Checkbox column. This table will uses a checkbox column for
		// selection.
		// Alternatively, you can call cellTable.setSelectionEnabled(true) to
		// enable
		// mouse selection.
		Column<SprvModules, Boolean> checkColumn = new Column<SprvModules, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(SprvModules object) {
				// Get the value from the selection model.
				return selectionModel.isSelected(object);
			}
		};
		cellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 40, Unit.PX);
		
		// Имя
		Column<SprvModules, String> nameColumn = new Column<SprvModules, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvModules object) {
				return object.getSpmdName();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn, new Comparator<SprvModules>() {
			@Override
			public int compare(SprvModules o1, SprvModules o2) {
				return o1.getSpmdName().compareTo(o2.getSpmdName());
			}
		});
		cellTable.addColumn(nameColumn, constants.NameHead());

		// Описание
		Column<SprvModules, String> descColumn = new Column<SprvModules, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvModules object) {
				return object.getSpmdDesc();
			}
		};
		descColumn.setSortable(true);
		sortHandler.setComparator(descColumn, new Comparator<SprvModules>() {
			@Override
			public int compare(SprvModules o1, SprvModules o2) {
				return o1.getSpmdDesc().compareTo(o2.getSpmdDesc());
			}
		});
		cellTable.addColumn(descColumn, constants.DescHead());

		// Имя объекта
		Column<SprvModules, String> objNameColumn = new Column<SprvModules, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvModules object) {
				return object.getSpmdSpobName();
			}
		};
		objNameColumn.setSortable(true);
		sortHandler.setComparator(objNameColumn, new Comparator<SprvModules>() {
			@Override
			public int compare(SprvModules o1, SprvModules o2) {
				return o1.getSpmdSpobName().compareTo(o2.getSpmdSpobName());
			}
		});
		cellTable.addColumn(objNameColumn, constants.Objects());
		
		// Тип объекта
		Column<SprvModules, String> typeColumn = new Column<SprvModules, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvModules object) {
				return object.getSpmdSpmtName();
			}
		};
		typeColumn.setSortable(true);
		sortHandler.setComparator(typeColumn, new Comparator<SprvModules>() {
			@Override
			public int compare(SprvModules o1, SprvModules o2) {
				return o1.getSpmdSpmtName().compareTo(o2.getSpmdSpmtName());
			}
		});
		cellTable.addColumn(typeColumn, constants.TypeHead());	

		// UID
		Column<SprvModules, Number> uidColumn = new Column<SprvModules, Number>(
				new NumberCell()) {
			@Override
			public Number getValue(SprvModules object) {
				return object.getSpmdUid();
			}
		};
		descColumn.setSortable(true);
		sortHandler.setComparator(descColumn, new Comparator<SprvModules>() {
			@Override
			public int compare(SprvModules o1, SprvModules o2) {
				return Double.compare(o1.getSpmdUid(),o2.getSpmdUid());
			}
		});
		cellTable.addColumn(uidColumn, constants.cwDataGridUid());
		
		// IMEI
		Column<SprvModules, String> imeiColumn = new Column<SprvModules, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvModules object) {
				return object.getSpmdImei();
			}
		};
		imeiColumn.setSortable(true);
		sortHandler.setComparator(imeiColumn, new Comparator<SprvModules>() {
			@Override
			public int compare(SprvModules o1, SprvModules o2) {
				return o1.getSpmdImei().compareTo(o2.getSpmdImei());
			}
		});
		cellTable.addColumn(imeiColumn, "IMEI");
		
		// Number
		Column<SprvModules, String> numbColumn = new Column<SprvModules, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvModules object) {
				return object.getSpmdNumb();
			}
		};
		numbColumn.setSortable(true);
		sortHandler.setComparator(numbColumn, new Comparator<SprvModules>() {
			@Override
			public int compare(SprvModules o1, SprvModules o2) {
				return o1.getSpmdNumb().compareTo(o2.getSpmdNumb());
			}
		});
		cellTable.addColumn(numbColumn, constants.cwDataGridPhoneNumber());
		
		// Дата создания
		Column<SprvModules, Date> dateColumn = new Column<SprvModules, Date>(
				new DateCell(DATE_TIME_FORMAT)) {
			@Override
			public Date getValue(SprvModules object) {
				return object.getSpmdDtCreate();
			}
		};
		dateColumn.setSortable(true);
		sortHandler.setComparator(dateColumn, new Comparator<SprvModules>() {
			@Override
			public int compare(SprvModules o1, SprvModules o2) {
				return o1.getSpmdDtCreate().compareTo(o2.getSpmdDtCreate());
			}
		});
		cellTable.addColumn(dateColumn, constants.cwDataGridDate());	
	}

	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		AsyncCallback<List<SprvModules>> callback = new AsyncCallback<List<SprvModules>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<SprvModules> SprvModules) {
				dataProvider.getList().clear();
				dataProvider.getList().addAll(SprvModules);
				dataProvider.refresh();
				pager.setDisplay(cellTable);
			}

		};
		rpcObject.getListModules(callback);
	}

	@Override
	public String getTableName() {
		return "SprvModules";
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
		// TODO Auto-generated method stub
		filterTables.clear();
		// Переменная часть полей.
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spmdId");
		filterTable.setFieldName(constants.ID());
		filterTable.setFieldExpression(EXPR_LONG);
		filterTable.setFieldType(SQL_TYPE_LONG);
		filterTables.add(filterTable);
		
	}

	@Override
	public void initTable() {
		cellTable = new CellTable<SprvModules>(KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);
		cellTable
				.setSelectionModel(selectionModel, DefaultSelectionEventManager
						.<SprvModules> createCheckboxManager());
		cellTable.addColumnSortHandler(sortHandler);
	}


	@Override
	public void initAdd() {
		// TODO Auto-generated method stub
		
	}

}
