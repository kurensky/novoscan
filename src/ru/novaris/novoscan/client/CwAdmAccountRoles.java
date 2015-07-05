/**
 * 
 */
package ru.novaris.novoscan.client;

import java.util.Comparator;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplFilters;
import ru.novaris.novoscan.domain.AccountRoles;
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

/**
 * @author kurensky
 * 
 */
public class CwAdmAccountRoles extends CwBaseLayout implements ImplFilters {
	interface CwAdmAccountRolesUiBinder extends
			UiBinder<Widget, CwAdmAccountRoles> {
	}
	private static CwAdmAccountRolesUiBinder uiBinder = GWT
			.create(CwAdmAccountRolesUiBinder.class);
	
	@UiField(provided = true)
	CellTable<AccountRoles> cellTable;

	
	final String title = constants.Roles();
	private static final ProvidesKey<AccountRoles> KEY_PROVIDER = new ProvidesKey<AccountRoles>() {
		public Object getKey(AccountRoles item) {
			return item == null ? null : item.getAcrlId();
		}
	};
	final ListDataProvider<AccountRoles> dataProvider = new ListDataProvider<AccountRoles>();
	final ListHandler<AccountRoles> sortHandler = new ListHandler<AccountRoles>(
			dataProvider.getList());
	final SelectionModel<AccountRoles> selectionModel = new MultiSelectionModel<AccountRoles>(
			KEY_PROVIDER);

	public CwAdmAccountRoles(Novoscan entryPoint) {
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
			final SelectionModel<AccountRoles> selectionModel,
			ListHandler<AccountRoles> sortHandler) {
		
		Column<AccountRoles, Boolean> checkColumn = new Column<AccountRoles, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(AccountRoles object) {
				// Get the value from the selection model.
				return selectionModel.isSelected(object);
			}
		};
		cellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 40, Unit.PX);

		// Login name.
		Column<AccountRoles, String> loginColumn = new Column<AccountRoles, String>(
				new TextCell()) {
			@Override
			public String getValue(AccountRoles object) {
				return object.getAcrlLoginName();
			}
		};
		loginColumn.setSortable(true);
		sortHandler.setComparator(loginColumn, new Comparator<AccountRoles>() {
			@Override
			public int compare(AccountRoles o1, AccountRoles o2) {
				return o1.getAcrlLoginName().compareTo(o2.getAcrlLoginName());
			}
		});
		cellTable.addColumn(loginColumn, constants.Username());
		// Роли
		Column<AccountRoles, String> roleColumn = new Column<AccountRoles, String>(
				new TextCell()) {
			@Override
			public String getValue(AccountRoles object) {
				return object.getAcrlRoleName();
			}
		};
		roleColumn.setSortable(true);
		sortHandler.setComparator(roleColumn, new Comparator<AccountRoles>() {
			@Override
			public int compare(AccountRoles o1, AccountRoles o2) {
				return o1.getAcrlRoleName().compareTo(o2.getAcrlRoleName());
			}
		});
		cellTable.addColumn(roleColumn, constants.Roles());
	}

	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		AsyncCallback<List<AccountRoles>> callback = new AsyncCallback<List<AccountRoles>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<AccountRoles> accountRoles) {
				dataProvider.getList().clear();
				dataProvider.getList().addAll(accountRoles);
				dataProvider.refresh();
				pager.setDisplay(cellTable);
			}

		};
		rpcObject.getAccountRolesAll(callback);
	}

	@Override
	public String getTableName() {
		return "AccountRoles";
	}

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
		filterTable.setFieldKey("acrlRoleName");
		filterTable.setFieldName(constants.Roles());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);	
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("acrlLoginName");
		filterTable.setFieldName(constants.Login());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);	

	}

	@Override
	public void initTable() {
		cellTable = new CellTable<AccountRoles>(KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);
		cellTable.addColumnSortHandler(sortHandler);
		cellTable.setSelectionModel(selectionModel,
				DefaultSelectionEventManager
						.<AccountRoles> createCheckboxManager());
	}

	@Override
	public void initDataProvider() {
		refreshData();
		dataProvider.addDataDisplay(cellTable);
		pager.setDisplay(cellTable);
	}

	@Override
	public void refreshData() {
		initData();		
	}

	@Override
	public void initAdd() {
		// TODO Auto-generated method stub
		
	}
}
