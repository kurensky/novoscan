package ru.novaris.novoscan.client;

import java.util.ArrayList;
import java.util.List;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ProgressPanel;
import ru.novaris.novoscan.domain.Request;
import ru.novaris.novoscan.domain.RussiaOsmPoint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;

public class CwGisDistance extends CwBaseLayout implements ImplConstantsGWT {

	private static CwGisDistanceUiBinder uiBinder = GWT
			.create(CwGisDistanceUiBinder.class);
	@UiField
	PushButton addRecord;
	@UiField
	PushButton delRecord;
	@UiField
	PushButton calc;
	@UiField
	PushButton clear;
	@UiField
	DoubleBox fullDistance;
	@UiField
	DoubleBox fullCost;
	@UiField
	FlexTable flexTable;
	@UiField
	DoubleBox averageCost;
	@UiField DoubleBox fullDistanceFact;
	@UiField DoubleBox fullCostFact;
	@UiField DoubleBox averageCostFact;
	private double longDistance;
	private double longDistanceFact;
	private double longDistanceReal;
	private double tmpDistance;
	private double tmpDistanceFact;
	private double tmpCost;
	private double tmpRow;
	/*
	 * Коэфициент увеличения дистанции.
	 */

	private final ProgressPanel progressPanel = new ProgressPanel();
	
	private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
	
	private final List<Long> oracleOsm = new ArrayList<Long>();

	interface CwGisDistanceUiBinder extends UiBinder<Widget, CwGisDistance> {
	}

	public CwGisDistance(Novoscan entryPoint) {
		initWidget(uiBinder.createAndBindUi(this));
		add.setEnabled(false);
		
		addHeader(flexTable);
		addRow(flexTable);
	}

	private void refreshOracleList(String ilikeName) {
		AsyncCallback <List<RussiaOsmPoint>> callback = new AsyncCallback <List<RussiaOsmPoint>>() {

			@Override
			public void onFailure(Throwable caught) {
				Alert("Ошибка получения списка населённых пунктов!");
			}

			@Override
			public void onSuccess(List<RussiaOsmPoint> result) {
				oracle.clear();
				oracleOsm.clear();				
				for (int i = 0; i < result.size(); i++) {
					oracle.add(result.get(i).getOsmName());
					oracleOsm.add(result.get(i).getOsmId());
				}
				progressPanel.hideProgress();				
			}
			
		};
		progressPanel.showProgress();
		rpcObject.getOsmData(ilikeName, callback);
	}

	@UiHandler("calc")
	void onCalcClick(ClickEvent event) {
		int numRows = flexTable.getRowCount();
		longDistance = 0;
		longDistanceFact = 0;
		tmpDistance = 0;
		tmpDistanceFact = 0;
		tmpCost = 0;
		String citySrc;
		String cityDst;
		Long rate;
		tmpRow = numRows - FIRST_DATA_ROW;
		for (int row = FIRST_DATA_ROW; row < numRows; row++) {
			citySrc = ((SuggestBox) flexTable.getWidget(row, 1)).getValue();
			cityDst = ((SuggestBox) flexTable.getWidget(row, 2)).getValue();
			rate = ((LongBox) flexTable.getWidget(row, 3)).getValue();
			calcDistance(citySrc, cityDst, row, rate);
		}
		if(tmpRow > 0) {
			add.setEnabled(true);
		}
	}

	/**
	 * Запрос расстояния по дорогам
	 */
	public void calcDistance(final String src, final String dst, final int row,
			final double rate) {
		AsyncCallback <List<Double>> callback = new AsyncCallback <List<Double>>() {

			@Override
			public void onFailure(Throwable caught) {
				tmpRow = tmpRow - 1;
				Alert("Ошибка исполнения процедуры (-100): " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Double> result) {
				if (result.get(0).floatValue() < 0) {
					Alert("Ошибка исполнения процедуры! Нет данных: " + src + "--" + dst);
				} else {
					if (result.get(0) == null) {
						longDistance = 0;
					} else {
						longDistance = result.get(0);
					}
					if (result.get(1) == null) {
						longDistanceReal = 0;
						longDistanceFact = longDistance * DELTA_DISTANCE;
					} else {
						longDistanceReal = result.get(1);
						longDistanceFact = longDistanceReal;
					}
					
					if (longDistance > 0) {
						tmpDistance = longDistance + tmpDistance;
						tmpDistanceFact = longDistanceFact + tmpDistanceFact; 
						flexTable.setWidget(row, 4,
								getDoubleBox(true, longDistance));
						flexTable.setWidget(row, 5,
								getDoubleBox(true,(longDistance * DELTA_DISTANCE)));
						flexTable.setWidget(row, 6,
								getDoubleBox(true, longDistanceReal));
						double cost = rate;
						tmpCost = tmpCost + cost;
						flexTable.setWidget(row, 7,
								getDoubleBox(true, (cost / longDistanceFact)));
						averageCost.setValue(tmpCost / tmpDistance);
						averageCostFact.setValue(tmpCost / tmpDistanceFact);
						fullDistance.setValue(tmpDistance);
						fullDistanceFact.setValue(tmpDistanceFact);
						fullCost.setValue(tmpCost);
						fullCostFact.setValue(tmpCost);
					} else {
						flexTable.setWidget(row, 6, getDoubleBox(true, 0));
					}
				}
				tmpRow = tmpRow - 1;
				if (tmpRow == 0) {
					progressPanel.hideProgress();
				} else {
					progressPanel.showProgress();
				}

			}

		};
		if ((src != null) && (!src.isEmpty()) && (dst != null) && (!dst.isEmpty())) {
			progressPanel.showProgress();
			rpcObject.getDistanceWay(src, dst, callback);
		} else {
			Alert("Введите значение поля!");
		}
	}

	@UiHandler("addRecord")
	void onAddRecordClick(ClickEvent event) {
		addRow(flexTable);
	}

	/**
	 * Add a row to the flex table.
	 */
	private void addRow(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
		boolean allowAdd = false;
		String sourceCity = "";
		long tarif = 0;
		boolean sourceReadOnly = true;
		if (numRows > FIRST_DATA_ROW) {
			sourceCity = ((SuggestBox) flexTable.getWidget(numRows - 1, 2)).getValue();
			tarif = ((LongBox) flexTable.getWidget(numRows - 1, 3)).getValue();
			sourceReadOnly = false;
			if	(sourceCity.length() > 2) {
				allowAdd = true;
			} else { 
				allowAdd = false;
			}
		} else {
			sourceCity = "";
			allowAdd = true;
		}
		if (allowAdd) {
			if (numRows > FIRST_DATA_ROW) {
				((CheckBox) flexTable.getWidget(numRows - 1, 0)).setEnabled(false);
				((SuggestBox) flexTable.getWidget(numRows - 1, 1)).setEnabled(false);
				((SuggestBox) flexTable.getWidget(numRows - 1, 2)).setEnabled(false);
			}
			flexTable.setWidget(numRows, 0, getCheckBox());
			((CheckBox) flexTable.getWidget(numRows, 0)).setWidth("20");
			flexTable.setWidget(numRows, 1, getSuggestBox(sourceReadOnly, sourceCity));
			((SuggestBox) flexTable.getWidget(numRows, 1)).setWidth("100");
			flexTable.setWidget(numRows, 2, getSuggestBox(""));
			((SuggestBox) flexTable.getWidget(numRows, 2)).setWidth("100");
			flexTable.setWidget(numRows, 3, getLongBox(false, tarif));
			((LongBox) flexTable.getWidget(numRows, 3)).setWidth("50");
			flexTable.setWidget(numRows, 4, getLongBox(true));
			((LongBox) flexTable.getWidget(numRows, 4)).setWidth("50");
			flexTable.setWidget(numRows, 5, getLongBox(true));
			((LongBox) flexTable.getWidget(numRows, 5)).setWidth("60");
			flexTable.setWidget(numRows, 6, getLongBox(true));
			((LongBox) flexTable.getWidget(numRows, 6)).setWidth("60");
			flexTable.setWidget(numRows, 7, getLongBox(true));
			((LongBox) flexTable.getWidget(numRows, 7)).setWidth("40");
			//
			applyDataRowStyles();
		} else {
			Alert("Ошибка! Введите наименование города.");
		}
	}
	


	private SuggestBox getSuggestBox(String value) {
		final SuggestBox suggestBox = new SuggestBox(oracle);
		suggestBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				refreshOracleList(event.getValue());
				suggestBox.refreshSuggestionList();
			}
		});
		suggestBox.setValue(value);
		return suggestBox;
	}

	private SuggestBox getSuggestBox(boolean editable, String value) {
		SuggestBox suggestBox = getSuggestBox(value);
		suggestBox.setEnabled(editable);
		return suggestBox;
	}

	private LongBox getLongBox(boolean editable, long value) {
		LongBox longBox = getLongBox(editable);
		longBox.setValue(value);
		return longBox;
	}

	private DoubleBox getDoubleBox(boolean editable, double value) {
		DoubleBox doubleBox = getDoubleBox(editable);
		doubleBox.setValue(value);
		return doubleBox;
	}

	private DoubleBox getDoubleBox(boolean editable) {
		DoubleBox doubleBox = new DoubleBox();
		doubleBox.setReadOnly(editable);
		return doubleBox;
	}

	private void applyDataRowStyles() {
		RowFormatter rf = flexTable.getRowFormatter();
		for (int row = FIRST_DATA_ROW; row < flexTable.getRowCount(); ++row) {
			if ((row % 2) != 0) {
				rf.addStyleName(row, "FlexTable-OddRow");
			} else {
				rf.addStyleName(row, "FlexTable-EvenRow");
			}
		}
	}

	private CheckBox getCheckBox() {
		CheckBox checkBox = new CheckBox();
		checkBox.setEnabled(true);
		return checkBox;
	}

	private LongBox getLongBox(boolean editable) {
		LongBox longBox = new LongBox();
		longBox.setReadOnly(editable);
		return longBox;
	}

	/**
	 * Add a row to the flex table.
	 */
	private void addHeader(FlexTable flexTable) {
		flexTable.setWidget(HEADER_ROW_INDEX, 0, new Label(" "));
		flexTable.setWidget(HEADER_ROW_INDEX, 1, new Label("Город отправления"));
		flexTable.setWidget(HEADER_ROW_INDEX, 2, new Label("Город назначения"));
		flexTable.setWidget(HEADER_ROW_INDEX, 3, new Label("Ставка (руб.)"));
		flexTable.setWidget(HEADER_ROW_INDEX, 4, new Label("Расстояние (км.)"));
		flexTable.setWidget(HEADER_ROW_INDEX, 5, new Label("Расстояние + "+String.valueOf(Math.round(DELTA_DISTANCE*100-100))+"% (км.)"));
		flexTable.setWidget(HEADER_ROW_INDEX, 6, new Label("Расстояние факт (км.)"));
		flexTable.setWidget(HEADER_ROW_INDEX, 7, new Label("Цена (руб./км.)"));
		flexTable.getRowFormatter().setStyleName(HEADER_ROW_INDEX,
				"FlexTable-Header");
	}


	/**
	 * Remove a row from the flex table.
	 */
	private void removeRow(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
		if (numRows == (FIRST_DATA_ROW + 2)) {
			flexTable.removeRow(numRows - 1);
			((SuggestBox) flexTable.getWidget(FIRST_DATA_ROW, 1)).setEnabled(true);
			((SuggestBox) flexTable.getWidget(FIRST_DATA_ROW, 2)).setEnabled(true);
		}
		if (numRows > (FIRST_DATA_ROW + 2)) {
			flexTable.removeRow(numRows - 1);
			((SuggestBox) flexTable.getWidget(numRows - 2, 2))
					.setEnabled(true);
		}
	}

	@UiHandler("delRecord")
	void onDelRecordClick(ClickEvent event) {
		removeRow(flexTable);
	}

	public static void showWaitCursor() {
		RootPanel.getBodyElement().getStyle().setProperty("cursor", "wait");
	}

	public static void showDefaultCursor() {
		RootPanel.getBodyElement().getStyle().setProperty("cursor", "default");
	}

	@UiHandler("clear")
	void onClearClick(ClickEvent event) {
		flexTable.removeAllRows();
		addHeader(flexTable);
		addRow(flexTable);
		fullDistance.setValue(null);
		fullDistanceFact.setValue(null);
		fullCost.setValue(null);
		fullCostFact.setValue(null);
		averageCost.setValue(null);
		averageCostFact.setValue(null);
		add.setEnabled(false);
	}
	@UiHandler("add")
	void onSaveClick(ClickEvent event) {
		Request record = new Request();
		DbFormAdd dbFormAdd = new DbFormAdd(eventBusData, record, flexTable);
		dbFormAdd.show();
	}
	
	public void Alert(String info) {
		progressPanel.hideProgress();
		eventBusData.fireEvent(new CheckSessionEvent());
		new DbMessage(MessageType.WARNING, info);
	}
}
