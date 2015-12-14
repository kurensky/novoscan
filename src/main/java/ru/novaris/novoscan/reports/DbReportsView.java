/**
 * 
 */
package ru.novaris.novoscan.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.novaris.novoscan.client.DbMessage;
import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.DateTimeBox;
import ru.novaris.novoscan.domain.ReportParamList;
import ru.novaris.novoscan.domain.SprvReportParameters;
import ru.novaris.novoscan.domain.SprvReports;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * @author kurensky
 * 
 */
public class DbReportsView extends DialogBox implements ImplConstantsGWT,
		ImplConstants {

	@UiField
	PushButton clear;
	@UiField
	PushButton apply;
	@UiField
	PushButton cancel;
	@UiField
	TabLayoutPanel panel = new TabLayoutPanel(BAR_SIZE, Unit.PX);
	@UiField
	ScrollPanel scrollPanel = new ScrollPanel();
	@UiField
	FlexTable flexTable = new FlexTable();
	@UiField
	Frame frame = new Frame();
	@UiField
	HTMLPanel paramsPanel;
	@UiField
	Label reportName = new Label();

	private List<SprvReportParameters> parameters = new ArrayList<SprvReportParameters>();
	private final SprvReports sprvReports;
	private final int reportHeight;
	private final int reportWidth;

	private static DbReportsViewUiBinder uiBinder = GWT
			.create(DbReportsViewUiBinder.class);

	interface DbReportsViewUiBinder extends UiBinder<Widget, DbReportsView> {
	}

	public DbReportsView(SprvReports sprvReports) {
		this.sprvReports = sprvReports;
		this.setTitle(constants.Reports());
		setWidget(uiBinder.createAndBindUi(this));
		reportHeight = RootPanel.get(DIV_CONTENT).getOffsetHeight() * 9 / 10;
		reportWidth = RootPanel.get(DIV_CONTENT).getOffsetWidth() * 9 / 10;
		panel.setWidth(String.valueOf(reportWidth));
		panel.setHeight(String.valueOf(reportHeight));
		frame.setWidth(String.valueOf(reportWidth));
		frame.setHeight(String.valueOf(reportHeight - BAR_SIZE));
		initButtons();
		initParameters();
	}

	private void initParameters() {
		AsyncCallback<List<SprvReportParameters>> callback = new AsyncCallback<List<SprvReportParameters>>() {

			@Override
			public void onFailure(Throwable caught) {
				showDefaultCursor();
				new DbMessage(MessageType.ERROR, "Ошибка получения параметров!");
				removeFromParent();
			}

			@Override
			public void onSuccess(List<SprvReportParameters> reportParameters) {
				parameters.clear();
				parameters.addAll(reportParameters);
				addHeader();
				addRows();
				showDefaultCursor();
			}

		};
		showWaitCursor();
		rpcObject.getListReportParameters(sprvReports.getSprpId(), callback);
	}

	private void addColumn(Object columnHeading) {
		Widget widget = createCellWidget(columnHeading);
		int cell = flexTable.getCellCount(HEADER_ROW_INDEX);
		widget.setWidth("100%");
		widget.addStyleName("FlexTable-ColumnLabel");
		flexTable.setWidget(HEADER_ROW_INDEX, cell, widget);
		flexTable.getCellFormatter().addStyleName(HEADER_ROW_INDEX, cell,
				"FlexTable-ColumnLabelCell");

	}

	private Widget createCellWidget(Object cellObject) {
		Widget widget = null;
		if (cellObject instanceof Widget)
			widget = (Widget) cellObject;
		else
			widget = new Label(cellObject.toString());
		return widget;
	}

	private void addHeader() {
		flexTable.insertRow(HEADER_ROW_INDEX);
		flexTable.getRowFormatter().addStyleName(HEADER_ROW_INDEX,
				"FlexTable-Header");
		addColumn(constants.NameHead());
		addColumn(constants.ValueHead());
		addColumn("");
	}

	private void applyDataRowStyles() {
		HTMLTable.RowFormatter rf = flexTable.getRowFormatter();

		for (int row = 1; row < flexTable.getRowCount(); ++row) {
			if ((row % 2) != 0) {
				rf.addStyleName(row, "FlexTable-OddRow");
			} else {
				rf.addStyleName(row, "FlexTable-EvenRow");
			}
		}
	}

	private void initButtons() {
		reportName.setText(sprvReports.getSprpDesc());
		cancel.setText(constants.Cancel());
		apply.setText(constants.Apply());
		clear.setText(constants.Clear());
	}

	@UiHandler("apply")
	void onClick(ClickEvent event) {
		final RequestBuilder requestBuilder = new RequestBuilder(
				RequestBuilder.POST, GWT.getModuleBaseURL()
						+ REPORT_SERVER_SERVLET);
		requestBuilder.setHeader("Content-type",
				"application/x-www-form-urlencoded");
		final StringBuffer postData = new StringBuffer();
		int rowIndex = HEADER_ROW_INDEX + 1;
		Widget widget = new Widget();
		postData.append(COOKIE_TAG_ID).append("=")
				.append(URL.encode(Cookies.getCookie(COOKIE_TAG_ID)))
				.append("&");
		postData.append(COOKIE_TIMEZONE_OFFSET).append("=")
				.append(URL.encode(Cookies.getCookie(COOKIE_TIMEZONE_OFFSET)))
				.append("&");
		for (int i = 0; i < parameters.size(); i++) {
			postData.append(URL.encode(parameters.get(i).getSpprName()))
					.append("=");
			widget = flexTable.getWidget(rowIndex, 1);
			if (parameters.get(i).getSpprType().equals(SQL_TYPE_DATE)) {
				postData.append(URL.encode(getText((FlexTable) widget)))
						.append("&");
			} else if (parameters.get(i).getSpprType()
					.equals(SQL_TYPE_LIST_STRING)) {
				ListBox listBox = (ListBox) widget;
				postData.append(
						URL.encode(listBox.getValue(listBox.getSelectedIndex())))
						.append("&");
			} else {
				postData.append(URL.encode(((TextBox) widget).getValue()))
						.append("&");
			}
			rowIndex++;
		}
		postData.append(REPORT_FILE).append("=")
				.append(sprvReports.getSprpSource());
		requestBuilder.setRequestData(postData.toString());
		requestBuilder.setCallback(new RequestCallback() {
			@Override
			public void onError(Request request, Throwable exception) {
				showDefaultCursor();
				new DbMessage(MessageType.ERROR, "Ошибка формирования отчёта: "
						+ exception.getMessage());
			}

			@Override
			public void onResponseReceived(Request request, Response response) {
				showDefaultCursor();
				if (response.getStatusCode() == Response.SC_OK) {
					frame.setUrl(GWT.getHostPageBaseURL() + response.getText());
					panel.selectTab(1);
				} else {
					new DbMessage(MessageType.ERROR,
							"Ошибка формирования отчёта: "
									+ response.getStatusCode());
				}

			}
		});
		try {
			showWaitCursor();
			requestBuilder.send();
		} catch (RequestException ex) {
			showDefaultCursor();
			ex.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	private String getText(FlexTable widget) {
		Date date = ((DateBox) widget.getWidget(0, 0)).getValue();
		ListBox hoursBox = (ListBox) widget.getWidget(0, 1);
		ListBox minutesBox = (ListBox) widget.getWidget(0, 2);
		ListBox secondsBox = (ListBox) widget.getWidget(0, 3);
		date.setHours(Integer.parseInt(hoursBox.getItemText(hoursBox
				.getSelectedIndex())));
		date.setMinutes(Integer.parseInt(minutesBox.getItemText(minutesBox
				.getSelectedIndex())));
		date.setSeconds(Integer.parseInt(secondsBox.getItemText(secondsBox
				.getSelectedIndex())));
		return DATE_TIME_FORMAT.format(date);
	}

	@UiHandler("cancel")
	void onCancelClick(ClickEvent event) {
		this.removeFromParent();
	}

	/**
	 * Add a row to the flex table.
	 */
	private void addRows() {
		int rowIndex = flexTable.getRowCount();
		for (int i = 0; i < parameters.size(); i++) {
			flexTable.setText(rowIndex, 0, parameters.get(i).getSpprDesc());
			flexTable.setWidget(rowIndex, 1, addWidget(parameters.get(i)));
			TextBox widget = new TextBox();
			widget.setText(parameters.get(i).getSpprName());
			widget.setVisible(false);
			widget.setEnabled(false);
			flexTable.setWidget(rowIndex, 2, widget);
			applyDataRowStyles();
			flexTable.setCellSpacing(0);
			flexTable.addStyleName("FlexTable");
			flexTable.setWidth("90%");
			rowIndex++;
		}
	}

	@SuppressWarnings("deprecation")
	private Widget addWidget(SprvReportParameters paramTable) {

		if (paramTable.getSpprType().equals(SQL_TYPE_STRING)) {
			final TextBox textBox = new TextBox();
			if (paramTable.getSpprValuev() != null) {
				textBox.setValue(paramTable.getSpprValuev());
			}
			return textBox;
		} else if (paramTable.getSpprType().equals(SQL_TYPE_LONG)) {
			final LongBox longBox = new LongBox();
			if (paramTable.getSpprValuen() != null) {
				longBox.setValue(paramTable.getSpprValuen().longValue());
			}
			return longBox;
		} else if (paramTable.getSpprType().equals(SQL_TYPE_DATE)) {
			DateTimeBox dateTimeBox;
			if (paramTable.getSpprValued() != null) {
				dateTimeBox = new DateTimeBox(paramTable.getSpprValued());
			} else {
				Date date = new Date();
				if (paramTable.getSpprName().equalsIgnoreCase("date_beg")) {
					date.setHours(0);
					date.setMinutes(0);
					date.setSeconds(0);
				} else if (paramTable.getSpprName()
						.equalsIgnoreCase("date_end")) {
					date.setHours(23);
					date.setMinutes(59);
					date.setSeconds(59);
				}
				dateTimeBox = new DateTimeBox(date);
			}
			return dateTimeBox.getDateTimeBox();
		} else if (paramTable.getSpprType().equals(SQL_TYPE_LIST_STRING)) {
			final ListBox widgetListBox = new ListBox();
			AsyncCallback<List<ReportParamList>> callback = new AsyncCallback<List<ReportParamList>>() {
				@Override
				public void onFailure(Throwable caught) {
					eventBusData.fireEvent(new CheckSessionEvent());
				}

				@Override
				public void onSuccess(List<ReportParamList> result) {
					for (ReportParamList entry : result) {
						widgetListBox
								.addItem(
										new StringBuffer()
												.append(entry.getValue())
												.append('(')
												.append(entry.getKey())
												.append(')').toString(),
										entry.getKey());
					}
				}

			};
			rpcObject.getHashMap(paramTable.getSpprHashMap(), callback);
			return widgetListBox;
		} else {
			final TextBox widget = new TextBox();
			if (paramTable.getSpprValuev() != null) {
				widget.setValue(paramTable.getSpprValuev());
			}
			return widget;
		}
	}

	private static void showWaitCursor() {
		RootPanel.getBodyElement().getStyle().setProperty("cursor", "wait");
	}

	private static void showDefaultCursor() {
		RootPanel.getBodyElement().getStyle().setProperty("cursor", "default");
	}
}
