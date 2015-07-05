/**
 * 
 */
package ru.novaris.novoscan.client;
import java.util.Comparator;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ImplFilters;
import ru.novaris.novoscan.domain.AddRecord;
import ru.novaris.novoscan.domain.FilterTable;
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
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;


/**
 * @author kurensky
 * 
 */
public class CwAdmDeparts extends CwBaseLayout implements ImplConstantsGWT, ImplFilters {
	/**
	 * The provider that holds the list of contacts in the database.
	 */
	private static CwAdmDepartsUiBinder uiBinder = GWT
			.create(CwAdmDepartsUiBinder.class);

	@UiField(provided = true)
	CellTable<SprvDeparts> cellTable;

	private final String title = constants.Departs();
	private static final ProvidesKey<SprvDeparts> KEY_PROVIDER = new ProvidesKey<SprvDeparts>() {
		public Object getKey(SprvDeparts item) {
			return item == null ? null : item.getSpdpId();
		}
	};
	final SelectionModel<SprvDeparts> selectionModel = new SingleSelectionModel<SprvDeparts>(
			KEY_PROVIDER);
	final ListDataProvider<SprvDeparts> dataProvider = new ListDataProvider<SprvDeparts>();
	final ListHandler<SprvDeparts> sortHandler = new ListHandler<SprvDeparts>(
			dataProvider.getList());	
	
	interface CwAdmDepartsUiBinder extends UiBinder<Widget, CwAdmDeparts> {
	}
	
	public CwAdmDeparts(Novoscan entryPoint) {
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

	@Override
	public void initAdd() {
		addRecords.clear();
		AddRecord addRecord = new AddRecord();
		addRecord.setFieldKey("spdpName");
		addRecord.setFieldName(constants.Departs());
		addRecord.setFieldType(SQL_TYPE_STRING);
		addRecord.setMandatory(true);
		addRecord.setEditable(true);
		addRecord.setSaveTable(getTableName());
		addRecords.add(addRecord);		
	}

	@Override
	public void initFilters() {
		filterTables.clear();
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spdpId");
		filterTable.setFieldName(constants.ID());
		filterTable.setFieldExpression(EXPR_LONG);
		filterTable.setFieldType(SQL_TYPE_LONG);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("spdpName");
		filterTable.setFieldName(constants.Departs());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);		
	}

	private void initTableColumns(
			final SelectionModel<SprvDeparts> selectionModel,
			ListHandler<SprvDeparts> sortHandler) {
		Column<SprvDeparts, Boolean> checkColumn = new Column<SprvDeparts, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(SprvDeparts object) {
				return selectionModel.isSelected(object);
			}
		};
		cellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 40, Unit.PX);

		// First name.
		Column<SprvDeparts, String> nameColumn = new Column<SprvDeparts, String>(
				new TextCell()) {
			@Override
			public String getValue(SprvDeparts object) {
				return object.getSpdpName();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn, new Comparator<SprvDeparts>() {
			@Override
			public int compare(SprvDeparts o1, SprvDeparts o2) {
				return o1.getSpdpName().compareTo(o2.getSpdpName());
			}
		});
		cellTable.addColumn(nameColumn, constants.Departs());
	}

	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		AsyncCallback<List<SprvDeparts>> callback = new AsyncCallback<List<SprvDeparts>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<SprvDeparts> departs) {
				dataProvider.getList().clear();
				dataProvider.getList().addAll(departs);
				dataProvider.refresh();
				pager.setDisplay(cellTable);
			}

		};
		rpcObject.getListDeparts(callback);
	}


	@Override
	public final String getTableName() {
		return "SprvDeparts";
	}
	
	@Override
	public final SprvDeparts getObject() {
		return new SprvDeparts();
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
	public void initTable() {
		cellTable = new CellTable<SprvDeparts>(KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);
		cellTable
				.setSelectionModel(selectionModel, DefaultSelectionEventManager
						.<SprvDeparts> createCheckboxManager());
		cellTable.addColumnSortHandler(sortHandler);
	}
	
	@Override
	public void initDataProvider() {
		refreshData();
		dataProvider.addDataDisplay(cellTable);	
	}
	
	@Override
	public SprvDeparts getSelectedObject() {
		SprvDeparts sprvDeparts = null;
		for (int i = 0; i < dataProvider.getList().size(); i++) {
			if(cellTable.getSelectionModel().isSelected(dataProvider.getList().get(i))) {
				sprvDeparts = dataProvider.getList().get(i);
			}
		}
		if (sprvDeparts.getSpdpId() < 1000) {
			sprvDeparts = null;
		} 
		return sprvDeparts;
	}
	
}
