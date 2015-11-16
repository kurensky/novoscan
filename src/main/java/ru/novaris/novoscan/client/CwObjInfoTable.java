/**
 * 
 */
package ru.novaris.novoscan.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.gwtopenmaps.openlayers.client.LonLat;

import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.ObjInfoResource;
import ru.novaris.novoscan.domain.DataSensorLast;
import ru.novaris.novoscan.domain.FilterTable;
import ru.novaris.novoscan.domain.SprvModules;
import ru.novaris.novoscan.events.MapCenterEvent;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.user.client.ui.DockLayoutPanel;

/**
 * @author kurensky
 * 
 */
public class CwObjInfoTable extends ResizeComposite implements
		ImplConstantsGWT, ImplConstants {
	private static WidgObjInfoTableUiBinder uiBinder = GWT
			.create(WidgObjInfoTableUiBinder.class);

	private final Novoscan entryPoint;

	// private final int stopTimeLong;

	private final static NumberFormat numberFormat = NumberFormat
			.getFormat("#");

	private final MapTrackData trackData;

	private ListDataProvider<DataSensorLast> dataProvider = new ListDataProvider<DataSensorLast>();

	public DataGrid<DataSensorLast> getInfoTable() {
		return dataGrid;
	}

	private static final ObjInfoResource objInfoResource = GWT
			.create(ObjInfoResource.class);
	static {
		objInfoResource.dataGrid().ensureInjected();
	}

	public static final ProvidesKey<DataSensorLast> KEY_PROVIDER = new ProvidesKey<DataSensorLast>() {
		public Object getKey(DataSensorLast item) {
			return item == null ? null : item.getDaslId();
		}
	};

	@UiField(provided = true)
	DataGrid<DataSensorLast> dataGrid = new DataGrid<DataSensorLast>(
			MAX_INFO_OBJECTS, objInfoResource, KEY_PROVIDER);
	@UiField
	DockLayoutPanel dockPanel;

	private List<DataSensorLast> lastData = new ArrayList<DataSensorLast>();

	private List<DataSensorLast> prevData = new ArrayList<DataSensorLast>();

	private boolean follow;

	private Handler<DataSensorLast> previewHandler;

	private Column<DataSensorLast, String> daslDateTime;

	private final int stopTimeLong;

	private final int unavTimeLong;

	interface WidgObjInfoTableUiBinder extends UiBinder<Widget, CwObjInfoTable> {
	}

	public CwObjInfoTable(final Novoscan entryPoint) {

		// GWT.log("Инициализация таблицы информации");
		this.entryPoint = entryPoint;
		// this.stopTimeLong = entryPoint.getStopTimeLong();
		this.trackData = new MapTrackData(entryPoint);
		stopTimeLong = entryPoint.getStopTimeLong();
		unavTimeLong = entryPoint.getUnavTimeLong();

		initWidget(uiBinder.createAndBindUi(this));

		/**
		 * Checkbox column. This table will uses a checkbox column for
		 * selection. Alternatively, you can call
		 * cellTable.setSelectionEnabled(true) to enable mouse selection.
		 * 
		 */

		final SelectionModel<DataSensorLast> selectionModel = new MultiSelectionModel<DataSensorLast>(
				KEY_PROVIDER);
		dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager
				.<DataSensorLast> createCheckboxManager());
		dockPanel.setWidth(String.valueOf(entryPoint.getWidth()
				/ INFO_PANEL_WIDTH_DIV - BAR_SIZE)
				+ Unit.PX);
		dockPanel.setHeight(String.valueOf(entryPoint.getHeight()
				/ INFO_PANEL_HEIGHT_DIV)
				+ Unit.PX);
		ListHandler<DataSensorLast> sortHandler = new ListHandler<DataSensorLast>(
				dataProvider.getList());
		dataGrid.addColumnSortHandler(sortHandler);
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setAutoFooterRefreshDisabled(true);
		dataGrid.setEmptyTableWidget(new Label(constants.cwDataGridEmpty()));
		this.setFollow(false);

		Column<DataSensorLast, Boolean> checkColumn = new Column<DataSensorLast, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(DataSensorLast object) {
				return selectionModel.isSelected(object);
			}
		};
		dataGrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		dataGrid.setColumnWidth(checkColumn, 40, Unit.PX);

		// Object name.
		Column<DataSensorLast, String> daslObjectInfo = new Column<DataSensorLast, String>(
				new TextCell()) {
			@Override
			public String getValue(DataSensorLast object) {
				return object.getDaslObjectInfo();
			}
		};
		daslObjectInfo.setSortable(true);
		sortHandler.setComparator(daslObjectInfo,
				new Comparator<DataSensorLast>() {
					@Override
					public int compare(DataSensorLast o1, DataSensorLast o2) {
						if (o1 == o2) {
							return 0;
						}
						return o1.getDaslObjectInfo().compareTo(
								o2.getDaslObjectInfo());
					}
				});
		daslObjectInfo.setCellStyleNames("daslObjectInfo");
		dataGrid.addColumn(daslObjectInfo, constants.cwDataGridObjectName());
		dataGrid.setColumnWidth(daslObjectInfo, 70, Unit.PCT);
		// Uid.
		Column<DataSensorLast, String> daslUid = new Column<DataSensorLast, String>(
				new TextCell()) {
			@Override
			public String getValue(DataSensorLast object) {
				return Long.toString(object.getDaslUid());
			}
		};
		daslUid.setSortable(true);
		sortHandler.setComparator(daslUid, new Comparator<DataSensorLast>() {
			@Override
			public int compare(DataSensorLast o1, DataSensorLast o2) {
				if (o1 == o2) {
					return 0;
				}

				// Compare the name columns.
				if (o1 != null) {
					return (o2 != null) ? compareTo(o1.getDaslUid(),
							o2.getDaslUid()) : 1;
				}
				return -1;
			}
		});
		dataGrid.addColumn(daslUid, constants.cwDataGridUid());
		dataGrid.setColumnWidth(daslUid, 50, Unit.PCT);
		daslUid.setCellStyleNames("daslUid");
		// Зажигание ignition
		Column<DataSensorLast, String> daslIgnition = new Column<DataSensorLast, String>(
				new TextCell()) {
			@Override
			public String getValue(DataSensorLast object) {
				return object.getDaslIgnition();
			}
		};
		daslIgnition.setCellStyleNames("daslIgnition");
		daslIgnition.setSortable(true);
		sortHandler.setComparator(daslIgnition,
				new Comparator<DataSensorLast>() {
					@Override
					public int compare(DataSensorLast o1, DataSensorLast o2) {
						if (o1 == o2) {
							return 0;
						}

						// Compare the name columns.
						if (o1 != null) {
							return (o2 != null) ? o1.getDaslIgnition()
									.compareTo(o2.getDaslIgnition()) : 1;
						}
						return -1;
					}
				});
		dataGrid.addColumn(daslIgnition, constants.cwDataGridIgnition()
				.substring(0, 4) + ".");
		dataGrid.setColumnWidth(daslIgnition, 20, Unit.PCT);
		// топливо
		Column<DataSensorLast, String> daslFuel = new Column<DataSensorLast, String>(
				new TextCell()) {
			@Override
			public String getValue(DataSensorLast object) {
				return object.getDaslFuel();
			}
		};
		daslFuel.setCellStyleNames("daslFuel");
		daslFuel.setSortable(true);
		sortHandler.setComparator(daslFuel,
				new Comparator<DataSensorLast>() {
					@Override
					public int compare(DataSensorLast o1, DataSensorLast o2) {
						if (o1 == o2) {
							return 0;
						}

						// Compare the name columns.
						if (o1 != null) {
							return (o2 != null) ? o1.getDaslFuel()
									.compareTo(o2.getDaslFuel()) : 1;
						}
						return -1;
					}
				});
		dataGrid.addColumn(daslFuel, constants.cwDataGridFuel());
		dataGrid.setColumnWidth(daslFuel, 30, Unit.PCT);
		
		// Скорость
		Column<DataSensorLast, String> daslSpeed = new Column<DataSensorLast, String>(
				new TextCell()) {
			@Override
			public String getValue(DataSensorLast object) {
				return numberFormat.format(object.getDaslSog());
			}
		};
		daslSpeed.setCellStyleNames("daslSpeed");
		daslSpeed.setSortable(true);
		sortHandler.setComparator(daslSpeed, new Comparator<DataSensorLast>() {
			@Override
			public int compare(DataSensorLast o1, DataSensorLast o2) {
				if (o1 == o2) {
					return 0;
				}

				// Compare the name columns.
				if (o1 != null) {
					return (o2 != null) ? compareTo(o1.getDaslSog(),
							o2.getDaslSog()) : 1;
				}
				return -1;
			}
		});
		dataGrid.addColumn(daslSpeed,
				constants.cwDataGridSpeed().substring(0, 4) + ".");
		dataGrid.setColumnWidth(daslSpeed, 30, Unit.PCT);
		// Спутники
		Column<DataSensorLast, String> daslSatUsed = new Column<DataSensorLast, String>(
				new TextCell()) {
			@Override
			public String getValue(DataSensorLast object) {
				return Long.toString(object.getDaslSatUsed());
			}
		};
		daslSatUsed.setCellStyleNames("daslSatUsed");
		daslSatUsed.setSortable(false);
		dataGrid.addColumn(daslSatUsed, constants.cwDataGridSatellites()
				.substring(0, 4) + ".");
		dataGrid.setColumnWidth(daslSatUsed, 30, Unit.PCT);
		// Время обновления
		daslDateTime = new Column<DataSensorLast, String>(new TextCell()) {
			@Override
			public String getValue(DataSensorLast object) {
				return DATE_TIME_FORMAT.format((object.getDaslDatetime()));
			}
		};
		daslDateTime.setSortable(true);
		daslDateTime.setCellStyleNames("daslDateTime");
		sortHandler.setComparator(daslDateTime,
				new Comparator<DataSensorLast>() {
					@Override
					public int compare(DataSensorLast o1, DataSensorLast o2) {
						if (o1 == o2) {
							return 0;
						}

						// Compare the name columns.
						if (o1 != null) {
							return (o2 != null) ? o1.getDaslDatetime()
									.compareTo(o2.getDaslDatetime()) : 1;
						}
						return -1;
					}
				});
		dataGrid.addColumn(daslDateTime, constants.cwDataGridDate());
		dataGrid.setColumnWidth(daslDateTime, 80, Unit.PCT);

		// Object name.
		Column<DataSensorLast, String> daslAddress = new Column<DataSensorLast, String>(
				new TextCell()) {
			@Override
			public String getValue(DataSensorLast object) {
				return object.getDaslAddress();
			}
		};
		daslAddress.setSortable(true);
		sortHandler.setComparator(daslAddress,
				new Comparator<DataSensorLast>() {
					@Override
					public int compare(DataSensorLast o1, DataSensorLast o2) {
						if (o1 == o2) {
							return 0;
						}
						return o1.getDaslAddress().compareTo(
								o2.getDaslAddress());
					}
				});
		daslAddress.setCellStyleNames("daslAddress");
		dataGrid.addColumn(daslAddress, constants.cwDataAddress());
		dataGrid.setColumnWidth(daslAddress, 200, Unit.PCT);

		// для предпросмотра
		previewHandler = new Handler<DataSensorLast>() {
			@Override
			public void onCellPreview(CellPreviewEvent<DataSensorLast> event) {
				if (event.getNativeEvent().getType().equals("click")) {
					LonLat lonLat = new LonLat(event.getValue()
							.getDaslLongitude(), event.getValue()
							.getDaslLatitude());
					lonLat.transform(SRC_PROJ_NAME, DST_PROJ_NAME);
					entryPoint.setMapCenter(lonLat);
					entryPoint.getEventBusData()
							.fireEvent(new MapCenterEvent());
					entryPoint.refreshMapData();
					if (event.getColumn() == 1) {
						if (trackData != null) {
							trackData.hide();
						}
						getPopupTrack(event);
					} else if (event.getColumn() == 2) {
						getObjectInfo(event);
					}
				}
			}
		};
		dataGrid.addCellPreviewHandler(previewHandler);
		dataProvider.addDataDisplay(dataGrid);
		refreshInfoTable();
	}

	/**
	 * Refresh all displays.
	 */
	public void refreshInfoTable() {
		lastData = entryPoint.getDataSensorLast();
		prevData = dataProvider.getList();
		for (DataSensorLast temp : lastData) {
			int i = 0;
			boolean notExist = true;
			for (DataSensorLast prev : prevData) {
				if (temp.getDaslId() == prev.getDaslId()) {
					dataProvider.getList().set(i, temp);
					notExist = false;
					break;
				}
				i++;
			}
			if (notExist) {
				dataProvider.getList().add(temp);
			}
		}
		dataProvider.refresh();
		/*
		 * Покраска строк.
		 */
		dataGrid.setRowStyles(new RowStyles<DataSensorLast>() {
			@Override
			public String getStyleNames(DataSensorLast row, int rowIndex) {
				Date date = new Date();
				Date dateDay = new Date();
				date.setTime(date.getTime() - stopTimeLong);
				dateDay.setTime(dateDay.getTime() - unavTimeLong);
				if (row.getDaslDatetime().before(dateDay)) {
					return "critical";
				} else if (row.getDaslDatetime().before(date)) {
					return "warning";
				} else
					return "default";
			}
		});
	}

	/**
	 * Отображение фильтра
	 * 
	 * @param event
	 */
	private void getPopupTrack(CellPreviewEvent<DataSensorLast> event) {
		ClickHandler listener = new ClickHandler() {

			@Override
			public final void onClick(ClickEvent event) {
				trackData.removeFromParent();
			}

		};
		setFollow(false);
		trackData.addListener(listener);
		trackData.setModal(false);
		trackData.setAnimationEnabled(true);
		trackData.setDasnData(event.getValue());
		trackData.setTitle("Трэк объекта "
				+ event.getValue().getDaslObjectInfo());
		trackData.setPopupPosition(entryPoint.getLeft(), entryPoint.getTop()
				+ entryPoint.getBorderSize());
		trackData.show();
	}

	private static String getTagValue(Document messageDom, String tagName,
			String pref, String post) {
		String tagValue;
		if (messageDom.getElementsByTagName(tagName).getLength() > 0) {
			tagValue = pref
					+ messageDom.getElementsByTagName(tagName).item(0)
							.getFirstChild().getNodeValue() + post;
		} else {
			tagValue = "";
		}

		return tagValue;
	}

	private void getObjectInfo(CellPreviewEvent<DataSensorLast> event) {
		final StringBuilder builder = new StringBuilder();
		final String xmlData = event.getValue().getDaslXml();
		if (xmlData.contains("<xml>") && xmlData.contains("</xml>")) {
			builder.delete(0, builder.length());
			try {
				// parse the XML document into a DOM
				Document messageDom = XMLParser.parse(xmlData);
				// find the sender's display name in an attribute of the <from>
				// tag
				builder.append(getTagValue(messageDom, "pw", "Пит.: ", "В.; "))
						.append(getTagValue(messageDom, "temp", "Темп.: ",
								" C.; "))
						.append(getTagValue(messageDom, "mcc", "MCC: ", "; "))
						.append(getTagValue(messageDom, "mnc", "MNC: ", "; "))
						.append(getTagValue(messageDom, "lac", "LAC: ", "; "))
						.append(getTagValue(messageDom, "cell", "CELL: ", "; "));
			} catch (Exception e) {
				builder.append("Ошибка: " + e.getLocalizedMessage());
			}
		}
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				new DbMessage(MessageType.ERROR, caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				AsyncCallback<List<SprvModules>> callback = new AsyncCallback<List<SprvModules>>() {

					@Override
					public void onFailure(Throwable caught) {
						new DbMessage(MessageType.ERROR, caught.getMessage());
					}

					@Override
					public void onSuccess(List<SprvModules> sprvModules) {

						if (sprvModules.size() > 0) {
							StringBuilder alertInfo = new StringBuilder();

							new DbMessage(MessageType.INFO, alertInfo
									.append("Ид объекта: ")
									.append(numberFormat.format(sprvModules
											.get(0).getSpmdUid()))
									.append(CTRL)
									.append("Номер телефона: ")
									.append(sprvModules.get(0).getSpmdNumb())
									.append(CTRL)
									.append("Тип терминала: ")
									.append(sprvModules.get(0)
											.getSpmdSpmtName()).append(CTRL)
									.append("Информация: ").append(builder)
									.append(CTRL).append("Исходные: ")
									.append(xmlData).toString());
						}
					}

				};
				rpcObject.getListModules(callback);
			}
		};
		List<FilterTable> filterTables = new ArrayList<FilterTable>();
		FilterTable filterTable = new FilterTable("SprvModules", 0);
		filterTable.setFieldKey("spmdUid");
		filterTable.setExpression(SQL_EQUAL);
		filterTable.setFieldType(SQL_TYPE_DOUBLE);
		filterTable.setFieldValue1(String
				.valueOf(event.getValue().getDaslUid()));
		filterTables.add(filterTable);
		rpcObject.setFilter(filterTables, callback);

	}

	private static int compareTo(long long1, long long2) {
		return long1 < long2 ? -1 : long1 == long2 ? 0 : 1;
	}

	private static int compareTo(double double1, double double2) {
		return double1 < double2 ? -1 : double1 == double2 ? 0 : 1;
	}

	public void clear() {
		dataProvider.getList().clear();
		dataGrid.removeFromParent();
		dataGrid = null;
	}

	public void setFollow(boolean follow) {
		this.follow = follow;
	}

	public boolean getFollow() {
		return this.follow;
	}

	public void removeWindow() {
		if (trackData != null) {
			if (trackData.isAttached()) {
				trackData.removeFromParent();
			}
		}
	}

	public void hide() {
		trackData.hide();
	}

	public void show() {
		trackData.show();
	}

	@Override
	public void onResize() {

	}

}
