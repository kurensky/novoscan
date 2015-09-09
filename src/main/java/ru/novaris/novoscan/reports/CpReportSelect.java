/**
 * 
 */
package ru.novaris.novoscan.reports;

import java.util.ArrayList;
import java.util.List;

import ru.novaris.novoscan.client.DbMessage;
import ru.novaris.novoscan.client.Novoscan;
import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.domain.SprvReports;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * @author kurensky
 * 
 */
public class CpReportSelect extends PopupPanel implements ImplConstantsGWT,
		ImplConstants {

	private static CpReportSelectUiBinder uiBinder = GWT
			.create(CpReportSelectUiBinder.class);

	interface CpReportSelectUiBinder extends UiBinder<Widget, CpReportSelect> {
	}

	@UiField
	PushButton apply;
	@UiField
	ListBox listReports;
	@UiField
	PushButton cancel;

	private List<FilterTable> filterTables = new ArrayList<FilterTable>();
	private FilterTable filterTable;
	private final long reportType;
	private List<SprvReports> sprvReports = new ArrayList<SprvReports>();

	/**
	 */
	public CpReportSelect(final Novoscan entryPoint, long reportType) {
		this.reportType = reportType;
		setWidget(uiBinder.createAndBindUi(this));
		this.setStyleDependentName("foreground", true);
		this.setTitle(constants.Reports());
		initButtons();
		initData();
	}

	public String getTableName() {
		return "SprvReportTypes";
	}

	private void initButtons() {
		cancel.setText(constants.Cancel());
		apply.setText(constants.Apply());
	}

	/**
	 * Запрос содержание таблицы
	 */
	private void initData() {
		filterTables.clear();
		// Переменная часть полей.
		filterTable = new FilterTable("SprvReports", filterTables.size());
		filterTable.setFieldKey("sprpSprtId");
		filterTable.setExpression(SQL_EQUAL);
		filterTable.setFieldType(SQL_TYPE_LONG);
		filterTable.setFieldValue1(String.valueOf(reportType));
		filterTables.add(filterTable);
		AsyncCallback<Void> filter = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				new DbMessage(MessageType.ERROR, caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				AsyncCallback<List<SprvReports>> callback = new AsyncCallback<List<SprvReports>>() {

					@Override
					public void onFailure(Throwable caught) {
						new DbMessage(MessageType.ERROR,
								"Ошибка получения списка отчётов!");
					}

					@Override
					public void onSuccess(List<SprvReports> reports) {
						listReports.clear();
						sprvReports.clear();
						sprvReports.addAll(reports);
						for (SprvReports reportType : reports) {
							listReports.addItem(reportType.getSprpName(),
									String.valueOf(reportType.getSprpId()));
						}
					}

				};
				rpcObject.getListReports(callback);
			}
		};
		rpcObject.setFilter(filterTables, filter);
	}

	@UiHandler("cancel")
	void onCancelClick(ClickEvent event) {
		this.removeFromParent();
	}

	@UiHandler("apply")
	void onApplyClick(ClickEvent event) {
		if (listReports.getSelectedIndex() < 0) {
			new DbMessage(MessageType.ERROR, "Выберите отчёт!");
		} else {
			long reportId = Long.valueOf(listReports.getValue(listReports
					.getSelectedIndex()));
			DbReportsView reportsView = new DbReportsView(
					getSprvReport(reportId));
			reportsView.setPopupPosition(RootPanel.get(DIV_CONTENT)
					.getAbsoluteLeft(), RootPanel.get(DIV_CONTENT)
					.getAbsoluteTop());
			reportsView.setStyleDependentName("foreground", true);
			reportsView.setModal(true);
			reportsView.setTitle("Параметры отчёта.");
			reportsView.show();
			this.removeFromParent();

		}
	}

	private SprvReports getSprvReport(long reportId) {
		for (int i = 0; i < sprvReports.size(); i++) {
			if (sprvReports.get(i).getSprpId() == reportId) {
				return sprvReports.get(i);
			}
		}
		return null;
	}
}
