package ru.novaris.novoscan.client;



import java.util.Comparator;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ImplFilters;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.domain.GisObjects;

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

public class CwAdmGisObjects extends CwBaseLayout implements ImplConstantsGWT, ImplFilters  {

	private static CwAdmGisObjectsUiBinder uiBinder = GWT
			.create(CwAdmGisObjectsUiBinder.class);

	interface CwAdmGisObjectsUiBinder extends UiBinder<Widget, CwAdmGisObjects> {
	}

	/**
	 * The main CellTable.
	 */
	@UiField(provided = true)
	CellTable<GisObjects> cellTable;
	
	final String title = constants.GisObjects();
	private static final ProvidesKey<GisObjects> KEY_PROVIDER = new ProvidesKey<GisObjects>() {
		public Object getKey(GisObjects item) {
			return item == null ? null : item.getGsobId();
		}
	};
	final SelectionModel<GisObjects> selectionModel = new MultiSelectionModel<GisObjects>(
			KEY_PROVIDER);
	final ListDataProvider<GisObjects> dataProvider = new ListDataProvider<GisObjects>();
	final ListHandler<GisObjects> sortHandler = new ListHandler<GisObjects>(
			dataProvider.getList());

	public CwAdmGisObjects(Novoscan entryPoint) {
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
			final SelectionModel<GisObjects> selectionModel,
			ListHandler<GisObjects> sortHandler) {
		Column<GisObjects, Boolean> checkColumn = new Column<GisObjects, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(GisObjects object) {
				// Get the value from the selection model.
				return selectionModel.isSelected(object);
			}
		};
		cellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 40, Unit.PX);
		// Имя
		Column<GisObjects, String> nameColumn = new Column<GisObjects, String>(
				new TextCell()) {
			@Override
			public String getValue(GisObjects object) {
				return object.getGsobName();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn, new Comparator<GisObjects>() {
			@Override
			public int compare(GisObjects o1, GisObjects o2) {
				return o1.getGsobName().compareTo(o2.getGsobName());
			}
		});
		cellTable.addColumn(nameColumn, constants.GisObjects());

		// Описание
		Column<GisObjects, String> descColumn = new Column<GisObjects, String>(
				new TextCell()) {
			@Override
			public String getValue(GisObjects object) {
				return object.getGsobDesc();
			}
		};
		descColumn.setSortable(true);
		sortHandler.setComparator(descColumn, new Comparator<GisObjects>() {
			@Override
			public int compare(GisObjects o1, GisObjects o2) {
				return o1.getGsobDesc().compareTo(o2.getGsobDesc());
			}
		});
		cellTable.addColumn(descColumn, constants.DescHead());
		
	}

	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		AsyncCallback<List<GisObjects>> callback = new AsyncCallback<List<GisObjects>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<GisObjects> gisObjects) {
				dataProvider.getList().clear();
				dataProvider.getList().addAll(gisObjects);
				dataProvider.refresh();
				pager.setDisplay(cellTable);
			}

		};
		rpcObject.getListGisObjects(callback);
	}

	@Override
	public String getTableName() {
		return "GisObjects";
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
		filterTable.setFieldKey("gsobId");
		filterTable.setFieldName(constants.ID());
		filterTable.setFieldExpression(EXPR_LONG);
		filterTable.setFieldType(SQL_TYPE_LONG);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("gsobName");
		filterTable.setFieldName(constants.GisObjects());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("gsobDesc");
		filterTable.setFieldName(constants.DescHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
	}

	@Override
	public void initTable() {
		cellTable = new CellTable<GisObjects>(KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);
		cellTable
				.setSelectionModel(selectionModel, DefaultSelectionEventManager
						.<GisObjects> createCheckboxManager());
		cellTable.addColumnSortHandler(sortHandler);
	}


	@Override
	public void initAdd() {
	}
	
	@Override
	public GisObjects getSelectedObject() {
		GisObjects GisObjects = null;
		for (int i = 0; i < dataProvider.getList().size(); i++) {
			if(cellTable.getSelectionModel().isSelected(dataProvider.getList().get(i))) {
				GisObjects = dataProvider.getList().get(i);
			}
		}
		return GisObjects;
	}
	@Override
	public final GisObjects getObject() {
		return new GisObjects();
	}

}
