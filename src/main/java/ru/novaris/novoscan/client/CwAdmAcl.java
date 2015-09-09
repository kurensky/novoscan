package ru.novaris.novoscan.client;

import java.util.Comparator;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ImplFilters;
import ru.novaris.novoscan.domain.Acl;
import ru.novaris.novoscan.domain.FilterTable;
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

import java.util.HashMap;

public class CwAdmAcl  extends CwBaseLayout implements ImplConstantsGWT, ImplFilters {

	private static CwAdmAclUiBinder uiBinder = GWT
			.create(CwAdmAclUiBinder.class);

	interface CwAdmAclUiBinder extends UiBinder<Widget, CwAdmAcl> {
	}
	@UiField(provided = true)
	CellTable<Acl> cellTable;
	
	private static final HashMap<Integer, String>  mapObjectTypes = new HashMap<Integer, String>();

	private static final HashMap<Integer, String> mapAccess = new HashMap<Integer, String>();
	
	private final String title = constants.Acls();
	private static final ProvidesKey<Acl> KEY_PROVIDER = new ProvidesKey<Acl>() {
		public Object getKey(Acl item) {
			return item == null ? null : item.getAcclId();
		}
	};
	final SelectionModel<Acl> selectionModel = new MultiSelectionModel<Acl>(
			KEY_PROVIDER);
	final ListDataProvider<Acl> dataProvider = new ListDataProvider<Acl>();
	final ListHandler<Acl> sortHandler = new ListHandler<Acl>(
			dataProvider.getList());
	



	public CwAdmAcl(Novoscan entryPoint) {
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
	private void initTableColumns(final SelectionModel<Acl> selectionModel,
			ListHandler<Acl> sortHandler) {
		addHandler();
		Column<Acl, Boolean> checkColumn = new Column<Acl, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(Acl object) {
				// Get the value from the selection model.
				return selectionModel.isSelected(object);
			}
		};
		cellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 40, Unit.PX);
		// Имя
		Column<Acl, String> loginColumn = new Column<Acl, String>(
				new TextCell()) {
			@Override
			public String getValue(Acl object) {
				return object.getAcclRefName1();
			}
		};
		loginColumn.setSortable(true);
		sortHandler.setComparator(loginColumn, new Comparator<Acl>() {
			@Override
			public int compare(Acl o1, Acl o2) {
				return o1.getAcclRefName1().compareTo(o2.getAcclRefName1());
			}
		});
		cellTable.addColumn(loginColumn, constants.Username());
		// Объект
		Column<Acl, String> objectColumn = new Column<Acl, String>(
				new TextCell()) {
			@Override
			public String getValue(Acl object) {
				return object.getAcclRefName2();
			}
		};
		objectColumn.setSortable(true);
		sortHandler.setComparator(objectColumn, new Comparator<Acl>() {
			@Override
			public int compare(Acl o1, Acl o2) {
				return o1.getAcclRefName2().compareTo(o2.getAcclRefName2());
			}
		});
		cellTable.addColumn(objectColumn, constants.Objects());
		// Тип
		Column<Acl, String> typeObjectColumn = new Column<Acl, String>(
				new TextCell()) {
			@Override
			public String getValue(Acl object) {
				return getObjectType(object);
			}
		};
		typeObjectColumn.setSortable(true);
		sortHandler.setComparator(typeObjectColumn, new Comparator<Acl>() {
			@Override
			public int compare(Acl o1, Acl o2) {
				return getObjectType(o1).compareTo(getObjectType(o2));
			}
		});
		cellTable.addColumn(typeObjectColumn, constants.TypeHead());
		
		// Тип разрешения
		Column<Acl, String> typeAccessColumn = new Column<Acl, String>(
				new TextCell()) {
			@Override
			public String getValue(Acl object) {
				return getAccessType(object);
			}
		};
		typeAccessColumn.setSortable(true);
		sortHandler.setComparator(typeAccessColumn, new Comparator<Acl>() {
			@Override
			public int compare(Acl o1, Acl o2) {
				return getAccessType(o1).compareTo(getAccessType(o2));
			}

		});
		cellTable.addColumn(typeAccessColumn, constants.Acls());
	}
	private String getAccessType(Acl object) {
		String typeAccessName = "Неопределён";
		if(!mapObjectTypes.get((int) object.getAcclAcl()).isEmpty()) {
		  typeAccessName = mapAccess.get((int) object.getAcclAcl());
		}
		return typeAccessName;
	}
	private String getObjectType(Acl object) {
		String typeName = "Неопределён";
		if(!mapObjectTypes.get((int) object.getAcclRefType2()).isEmpty()) {
		  typeName = mapObjectTypes.get((int) object.getAcclRefType2());
		}
		return typeName;
	}

	
	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		AsyncCallback<List<Acl>> callback = new AsyncCallback<List<Acl>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<Acl> acl) {
				dataProvider.getList().clear();
				dataProvider.getList().addAll(acl);
				dataProvider.refresh();
				pager.setDisplay(cellTable);
			}

		};
		rpcObject.getAcl(callback);
	}

	@Override
	public String getTableName() {
		return "Acl";
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
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("acclRefName1");
		filterTable.setFieldName(constants.Username());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);	

	}
	@Override
	public void initTable() {
		if (mapObjectTypes.isEmpty() || mapAccess.isEmpty()) {
			mapObjectTypes.put(CONST_ADMIN, constants.admin());
			for (int i = 1; i < CONST_USER; i++) {
				mapObjectTypes.put(i, "Неопределён");
			}
			mapObjectTypes.put(CONST_USER, constants.user());
			mapObjectTypes.put(CONST_OBJECT, constants.object());
			mapObjectTypes.put(CONST_MODULE, constants.terminal());
			mapObjectTypes.put(CONST_CLIENT, constants.client());
			mapObjectTypes.put(CONST_DEPART, constants.depart());
			mapAccess.put(CONST_ADMIN, constants.full());
			mapAccess.put(CONST_READONLY, constants.read());
			mapAccess.put(CONST_READWRITE, constants.readwrite());
		}
		cellTable = new CellTable<Acl>(KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);
		cellTable
				.setSelectionModel(selectionModel, DefaultSelectionEventManager
						.<Acl> createCheckboxManager());
		cellTable.addColumnSortHandler(sortHandler);
	}
	@Override
	public void initAdd() {
		// TODO Auto-generated method stub
		
	}

}
