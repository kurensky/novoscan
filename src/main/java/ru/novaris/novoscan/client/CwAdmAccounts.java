package ru.novaris.novoscan.client;

import java.util.Comparator;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplFilters;
import ru.novaris.novoscan.domain.Accounts;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.events.FilterEvent;
import ru.novaris.novoscan.events.FilterEventHandler;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;

public class CwAdmAccounts extends CwBaseLayout implements ImplFilters {
	interface CwAdmAccountsUiBinder extends UiBinder<Widget, CwAdmAccounts> {
	}
	private static CwAdmAccountsUiBinder uiBinder = GWT
			.create(CwAdmAccountsUiBinder.class);
	
	@UiField(provided = true)
	CellTable<Accounts> cellTable;
	@UiField
	PushButton passwd;
	
	private final String title = constants.Users();
	private static final ProvidesKey<Accounts> KEY_PROVIDER = new ProvidesKey<Accounts>() {
		public Object getKey(Accounts item) {
			return item == null ? null : item.getAcctId();
		}
	};
	final SelectionModel<Accounts> selectionModel = new MultiSelectionModel<Accounts>(
			KEY_PROVIDER);
	final ListDataProvider<Accounts> dataProvider = new ListDataProvider<Accounts>();
	final ListHandler<Accounts> sortHandler = new ListHandler<Accounts>(
			dataProvider.getList());

	public CwAdmAccounts(Novoscan entryPoint) {
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
	
	private void initTableColumns(
			final SelectionModel<Accounts> selectionModel,
			ListHandler<Accounts> sortHandler) {
		Column<Accounts, Boolean> checkColumn = new Column<Accounts, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(Accounts object) {
				// Get the value from the selection model.
				return selectionModel.isSelected(object);
			}
		};
		cellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 40, Unit.PX);
		// Login name.
		Column<Accounts, String> loginColumn = new Column<Accounts, String>(
				new TextCell()) {
			@Override
			public String getValue(Accounts object) {
				return object.getAcctLogin();
			}
		};
		loginColumn.setSortable(true);
		sortHandler.setComparator(loginColumn, new Comparator<Accounts>() {
			@Override
			public int compare(Accounts o1, Accounts o2) {
				return o1.getAcctLogin().compareTo(o2.getAcctLogin());
			}
		});
		cellTable.addColumn(loginColumn, constants.Username());
		
		// Имя
		Column<Accounts, String> nameColumn = new Column<Accounts, String>(
				new TextCell()) {
			@Override
			public String getValue(Accounts object) {
				return object.getAcctName();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn, new Comparator<Accounts>() {
			@Override
			public int compare(Accounts o1, Accounts o2) {
				return o1.getAcctName().compareTo(o2.getAcctName());
			}
		});
		cellTable.addColumn(nameColumn, constants.FirstName());
		
		// Отчество
		Column<Accounts, String> name2Column = new Column<Accounts, String>(
				new TextCell()) {
			@Override
			public String getValue(Accounts object) {
				return object.getAcctName2();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(name2Column, new Comparator<Accounts>() {
			@Override
			public int compare(Accounts o1, Accounts o2) {
				return o1.getAcctName2().compareTo(o2.getAcctName2());
			}
		});
		cellTable.addColumn(name2Column, constants.MiddleName());

		// Фамилия
		Column<Accounts, String> name3Column = new Column<Accounts, String>(
				new TextCell()) {
			@Override
			public String getValue(Accounts object) {
				return object.getAcctName3();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(name3Column, new Comparator<Accounts>() {
			@Override
			public int compare(Accounts o1, Accounts o2) {
				return o1.getAcctName3().compareTo(o2.getAcctName3());
			}
		});
		cellTable.addColumn(name3Column, constants.LastName());
		
		// Email
		Column<Accounts, String> emailColumn = new Column<Accounts, String>(
				new TextCell()) {
			@Override
			public String getValue(Accounts object) {
				return object.getAcctEmail();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(emailColumn, new Comparator<Accounts>() {
			@Override
			public int compare(Accounts o1, Accounts o2) {
				return o1.getAcctEmail().compareTo(o2.getAcctEmail());
			}
		});
		cellTable.addColumn(emailColumn, constants.Email());
	}

	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		AsyncCallback<List<Accounts>> callback = new AsyncCallback<List<Accounts>>() {

			@Override
			public void onFailure(Throwable caught) {
				eventBusData.fireEvent(new CheckSessionEvent());
			}

			@Override
			public void onSuccess(List<Accounts> Accounts) {
				dataProvider.getList().clear();
				dataProvider.getList().addAll(Accounts);
				dataProvider.refresh();
				pager.setDisplay(cellTable);
			}

		};
		rpcObject.getAccounts(callback);
	}
	
	@UiHandler("passwd")
	void onChangePasswdClick(ClickEvent event) {
	}

	@Override
	public String getTableName() {
		return "Accounts";
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
		filterTable.setFieldKey("acctLogin");
		filterTable.setFieldName(constants.Username());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);	
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("acctName");
		filterTable.setFieldName(constants.NameHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);	
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("acctName2");
		filterTable.setFieldName(constants.NameHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("acctName3");
		filterTable.setFieldName(constants.NameHead());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
		
		filterTable = new FilterTable(getTableName(),filterTables.size());
		filterTable.setFieldKey("acctEmail");
		filterTable.setFieldName(constants.Email());
		filterTable.setFieldExpression(EXPR_STRING);
		filterTable.setFieldType(SQL_TYPE_STRING);
		filterTables.add(filterTable);
	}

	@Override
	public void initButtons() {
		passwd.setText(constants.Password());
		add.setText(constants.Add());
		delete.setText(constants.Delete());
		modify.setText(constants.Modify());
		filter.setText(constants.Find());
	}

	@Override
	public void initTable() {
		cellTable = new CellTable<Accounts>(KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);
		cellTable
				.setSelectionModel(selectionModel, DefaultSelectionEventManager
						.<Accounts> createCheckboxManager());
		cellTable.addColumnSortHandler(sortHandler);
	}

	@Override
	public void initAdd() {
		// TODO Auto-generated method stub
		
	}
}
