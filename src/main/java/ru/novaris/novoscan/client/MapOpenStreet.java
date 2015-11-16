package ru.novaris.novoscan.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.StyleMap;
import org.gwtopenmaps.openlayers.client.control.Attribution;
import org.gwtopenmaps.openlayers.client.control.Control;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.Measure;
import org.gwtopenmaps.openlayers.client.control.MeasureOptions;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.MousePositionOptions;
import org.gwtopenmaps.openlayers.client.control.MousePositionOutput;
import org.gwtopenmaps.openlayers.client.control.NavToolbar;
import org.gwtopenmaps.openlayers.client.control.OverviewMap;
import org.gwtopenmaps.openlayers.client.control.PanZoom;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.control.ScaleLineOptions;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.control.SelectFeatureOptions;
import org.gwtopenmaps.openlayers.client.event.MapZoomListener;
import org.gwtopenmaps.openlayers.client.event.MeasureEvent;
import org.gwtopenmaps.openlayers.client.event.MeasureListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureUnselectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.LineString;
import org.gwtopenmaps.openlayers.client.geometry.LinearRing;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.handler.PathHandler;
import org.gwtopenmaps.openlayers.client.handler.PathHandlerOptions;
import org.gwtopenmaps.openlayers.client.handler.PolygonHandler;
import org.gwtopenmaps.openlayers.client.layer.Bing;
import org.gwtopenmaps.openlayers.client.layer.BingOptions;
import org.gwtopenmaps.openlayers.client.layer.BingType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.popup.FramedCloud;
import org.gwtopenmaps.openlayers.client.popup.Popup;
import org.gwtopenmaps.openlayers.client.protocol.ProtocolType;
import org.gwtopenmaps.openlayers.client.util.Attributes;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;

import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.LastDataEvent;
import ru.novaris.novoscan.client.resources.LastDataEventHandler;
import ru.novaris.novoscan.domain.DataSensor;
import ru.novaris.novoscan.domain.DataSensorLast;
import ru.novaris.novoscan.domain.GisDataPoint;
import ru.novaris.novoscan.events.MapCenterEvent;
import ru.novaris.novoscan.events.MapCenterEventHandler;
import ru.novaris.novoscan.events.MapResizeEvent;
import ru.novaris.novoscan.events.MapResizeEventHandler;

public class MapOpenStreet implements ImplConstantsGWT, ImplConstants {

	private MapWidget mapWidget;

	private Map map;

	private Novoscan entryPoint;

	private VectorFeature lineFeature = null;

	private Vector markers = null;

	private Vector courses = null;

	private Vector parking = null;

	private Vector vectorLayer = null;

	private Vector geoZones = null;

	private Bounds bounds = null;

	private final WidgMaps widget;

	private List<DataSensor> dataTrack = null;

	private Timer refreshTimer;

	private static final StringBuffer coordinate = new StringBuffer();

	private final DialogBox infoBox;

	private final CwObjInfoTable infoPanel;

	private DataSensorLast daslSensor = new DataSensorLast();

	private final long stopTimeLong;

	private final SimpleEventBus eventBusData;

	private int imageHeight;

	private int imageWidth;

	private SelectFeature selectFeature;

	private List<DataSensorLast> dataSensorLast = new ArrayList<DataSensorLast>();

	private int courseCurrent;

	private List<DataSensorLast> dataSensorCurrent = new ArrayList<DataSensorLast>();

	private Long markerId;

	private DrawFeature drawPolygonControl = null;

	private List<Point> pointArray;

	private int lastZoom;

	private LonLat lastCenter;

	public MapOpenStreet(final WidgMaps widget, final Novoscan entryPoint) {
		stopRefreshTimer();
		this.entryPoint = entryPoint;
		this.infoPanel = new CwObjInfoTable(entryPoint);
		this.infoBox = new DialogBox();
		this.initInfoPanel();
		this.widget = widget;
		this.widget.addStyleDependentName("tobackground");
		this.eventBusData = entryPoint.getEventBusData();
		stopTimeLong = entryPoint.getStopTimeLong();
		MapRunnable mapProcess = new MapRunnable();
		mapProcess.run();
	}

	class MapRunnable implements Runnable {
		public void run() {
			buildOMaps();
			mapDataLast();
			refreshTimer = new Timer() {
				public void run() {
					mapDataLast();
				}
			};
			refreshTimer.scheduleRepeating(entryPoint.getRefreshInterval());
		}
	}

	private void buildOMaps() {
		MapOptions mapOptions = new MapOptions();
		mapOptions.removeDefaultControls();
		mapOptions.setNumZoomLevels(ZOOM_MIN);
		mapOptions.setProjection(SRC_PROJ_NAME);
		MousePositionOutput mousePositionOutput = new MousePositionOutput() {
			public String format(LonLat lonLat, Map map) {
				lonLat.transform(DST_PROJ_NAME, SRC_PROJ_NAME);
				coordinate.delete(0, coordinate.length());
				coordinate.append(FORMAT_LAT_LON.format(lonLat.lon()));
				coordinate.append("  ");
				coordinate.append(FORMAT_LAT_LON.format(lonLat.lat()));
				return coordinate.toString();
			}
		};
		MousePositionOptions mousePositionOptions = new MousePositionOptions();
		mousePositionOptions.setFormatOutput(mousePositionOutput);
		// Measure option to the map
		MeasureOptions measureOptions = new MeasureOptions();
		measureOptions.setPersist(true);
		measureOptions.setGeodesic(true); // earth is not a cylinder
		// begin style the measure control... We want a blue dashed line.
		final Style measureStyle = new Style(); // create a Style to use
		measureStyle.setFillColor("white");
		// measureStyle.setGraphicName("x");
		measureStyle.setPointRadius(3);
		measureStyle.setStrokeWidth(2);
		measureStyle.setStrokeColor("#66FFFF");
		measureStyle.setStrokeDashstyle("dash");
		StyleMap measureStyleMap = new StyleMap(measureStyle);
		PathHandlerOptions pathHandlerOptions = new PathHandlerOptions();
		pathHandlerOptions.setStyleMap(measureStyleMap);
		measureOptions.setHandlerOptions(pathHandlerOptions);
		//
		Measure measure = new Measure(new PathHandler(), measureOptions);
		measure.addMeasureListener(new MeasureListener() {
			public void onMeasure(MeasureEvent eventObject) {
				new DbMessage(MessageType.INFO, "Дистанция "
						+ eventObject.getMeasure() + " "
						+ eventObject.getUnits());
			}
		});
		// measure\
		mapWidget = new MapWidget(String.valueOf(entryPoint.getWidth()),
				String.valueOf(entryPoint.getHeight()), mapOptions);
		map = mapWidget.getMap();
		map.addControl(new PanZoom());
		map.addControl(new MousePosition(mousePositionOptions));
		Control[] controlArray = { measure };
		NavToolbar navToolbar = new NavToolbar();
		navToolbar.addControls(controlArray);
		map.addControl(navToolbar);
		map.addControl(new LayerSwitcher());
		map.addControl(new Attribution());
		map.addControl(measure);
		// map.addControl(new NavigationHistory());
		ScaleLineOptions scaleLineOptions = new ScaleLineOptions();
		scaleLineOptions.getJSObject().setProperty("geodesic", true);
		map.addControl(new ScaleLine(scaleLineOptions));

		map.addControl(new OverviewMap());
		// OpenStreet
		OSM openStreetMap = OSM.Mapnik("Mapnik");
		openStreetMap.setIsBaseLayer(true);
		OSM openStreetMapCycle = OSM.CycleMap("CycleMap");
		openStreetMapCycle.setIsBaseLayer(true);

		// Google
		GoogleV3Options gHybridOptions = new GoogleV3Options();
		gHybridOptions.setNumZoomLevels(G_HYBRID_ZOOM_LEVEL); // 20
		gHybridOptions.setIsBaseLayer(true);
		gHybridOptions.setType(GoogleV3MapType.G_HYBRID_MAP);
		GoogleV3 gHybrid = new GoogleV3("Googe Гибрид", gHybridOptions);

		GoogleV3Options gNormalOptions = new GoogleV3Options();
		gNormalOptions.setNumZoomLevels(G_NORMAL_ZOOM_LEVEL); // 22
		gNormalOptions.setIsBaseLayer(true);
		gNormalOptions.setType(GoogleV3MapType.G_NORMAL_MAP);
		GoogleV3 gNormal = new GoogleV3("Googe Стандарт", gNormalOptions);

		GoogleV3Options gSatelliteOptions = new GoogleV3Options();
		gSatelliteOptions.setNumZoomLevels(G_SATELLITE_ZOOM_LEVEL); // 20
		gSatelliteOptions.setIsBaseLayer(true);
		gSatelliteOptions.setType(GoogleV3MapType.G_SATELLITE_MAP);
		GoogleV3 gSatellite = new GoogleV3("Googe Спутники", gSatelliteOptions);

		GoogleV3Options gTerrainOptions = new GoogleV3Options();
		gTerrainOptions.setNumZoomLevels(G_TERRAIN_ZOOM_LEVEL); // 16
		gTerrainOptions.setIsBaseLayer(true);
		gTerrainOptions.setType(GoogleV3MapType.G_TERRAIN_MAP);
		GoogleV3 gTerrain = new GoogleV3("Googe Местность", gTerrainOptions);

		// Bing
		final String key = "Apa9OOmwPmZUdapldunFKTG5PFrUomdGRCPn2s3zZv3F8vdV53QAdj4sXH5lt28u"; // Bing
																								// key
		// configuring road options
		BingOptions bingOptionRoad = new BingOptions("Bing Дороги", key,
				BingType.ROAD);
		bingOptionRoad.setProtocol(ProtocolType.HTTP);
		Bing bRoad = new Bing(bingOptionRoad);

		// configuring hybrid options
		BingOptions bingOptionHybrid = new BingOptions("Bing Гибрид", key,
				BingType.HYBRID);
		bingOptionRoad.setProtocol(ProtocolType.HTTP);
		Bing bHybrid = new Bing(bingOptionHybrid);

		// configuring aerial options
		BingOptions bingOptionAerial = new BingOptions("Bing Снимки", key,
				BingType.AERIAL);
		bingOptionRoad.setProtocol(ProtocolType.HTTP);
		Bing bAerial = new Bing(bingOptionAerial);
		
		// WMS
		// Create a WMS layer as base layer
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers(NOVOSCAN_MAP_LAYER);
		wmsParams.setStyles("");
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setUntiled();
		wmsLayerParams.setProjection(DST_PROJ_NAME);
		wmsLayerParams.setIsBaseLayer(true);
		wmsLayerParams.setNumZoomLevels(WMS_NUM_ZOOM_LEVEL);
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
		String wmsUrl = NOVOSCAN_MAP_SERVER;
		WMS wmsLayer = new WMS("Карта", wmsUrl, wmsParams, wmsLayerParams);

		map.addLayer(openStreetMap);
		map.addLayer(openStreetMapCycle);
		map.addLayer(gHybrid);
		map.addLayer(gNormal);
		map.addLayer(gSatellite);
		map.addLayer(gTerrain);
		map.addLayer(bHybrid);
		map.addLayer(bRoad);
		map.addLayer(bAerial);
		map.addLayer(wmsLayer);
		//
		Style style = new Style();
		style.setStrokeColor("blue");
		style.setStrokeWidth(3);
		style.setFillOpacity(1);
		final VectorOptions vectorOptions = new VectorOptions();
		vectorOptions.setStyle(style);
		vectorLayer = new Vector("Слой линий", vectorOptions);
		vectorLayer.setIsBaseLayer(false);
		map.addLayer(vectorLayer);
		//
		final VectorOptions markersOptions = new VectorOptions();
		markers = new Vector("Слой объектов", markersOptions);
		markers.setIsBaseLayer(false);
		map.addLayer(markers);
		// Слой направлений
		final VectorOptions coursesOptions = new VectorOptions();
		courses = new Vector("Слой направлений", coursesOptions);
		courses.setIsBaseLayer(false);
		map.addLayer(courses);
		// Слой парковок
		final VectorOptions parkingOptions = new VectorOptions();
		parking = new Vector("Слой стоянок", parkingOptions);
		parking.setIsBaseLayer(false);
		map.addLayer(parking);

		// First create a select control and make sure it is actived
		SelectFeatureOptions selectFeatureOptions = new SelectFeatureOptions();
		selectFeatureOptions.setHighlightOnly(true);

		selectFeature = new SelectFeature(markers, selectFeatureOptions);
		selectFeature.setAutoActivate(true);
		map.addControl(selectFeature);

		addGeoZones();
		this.addListeners();
		lastZoom = map.getZoom();
		lastCenter = map.getCenter();
	}

	private void addListeners() {
		markers.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
			public void onFeatureSelected(FeatureSelectedEvent eventObject) {
				final Popup popup = new FramedCloud(String.valueOf(eventObject
						.getVectorFeature().getFeatureId()), eventObject
						.getVectorFeature().getCenterLonLat(), null,
						eventObject.getVectorFeature().getAttributes()
								.getAttributeAsString("info"), null, false);
				popup.setPanMapIfOutOfView(true); // this set the popup in a
													// strategic way, and pans
													// the map if needed.
				markerId = Long.valueOf(eventObject.getVectorFeature()
						.getFeatureId());
				popup.setAutoSize(true);
				eventObject.getVectorFeature().setPopup(popup);
				map.addPopupExclusive(eventObject.getVectorFeature().getPopup());
			}
		});

		markers.addVectorFeatureUnselectedListener(new VectorFeatureUnselectedListener() {
			public void onFeatureUnselected(FeatureUnselectedEvent eventObject) {
				map.removePopup(eventObject.getVectorFeature().getPopup());
				eventObject.getVectorFeature().resetPopup();
			}
		});
		map.addMapZoomListener(new MapZoomListener() {

			@Override
			public void onMapZoom(final MapZoomEvent eventObject) {
				if (eventObject.getSource().getZoom() != lastZoom
						&& eventObject.getSource().getCenter() != lastCenter) {
					addDrivePoints();
					lastZoom = eventObject.getSource().getZoom();
					lastCenter = eventObject.getSource().getCenter();
					Cookies.setCookie(COOKIE_MAP_CENTER_LON,
							String.valueOf(lastCenter.lon()));
					Cookies.setCookie(COOKIE_MAP_CENTER_LAT,
							String.valueOf(lastCenter.lat()));
					Cookies.setCookie(COOKIE_MAP_ZOOM,
							Integer.toString(lastZoom));
				}
			}
		});

		eventBusData.addHandler(LastDataEvent.TYPE, new LastDataEventHandler() {
			@Override
			public void apply(LastDataEvent event) {
				refreshInfoPanel();
				refreshObjects();
			}

		});
		eventBusData.addHandler(MapCenterEvent.TYPE,
				new MapCenterEventHandler() {

					@Override
					public void apply(MapCenterEvent centerEvent) {
						/*
						 * GWT.log("Центрирование : " +
						 * entryPoint.getMapCenter().lon() + ":" +
						 * entryPoint.getMapCenter().lat());
						 */
						map.setCenter(entryPoint.getMapCenter());

					}
				});

		eventBusData.addHandler(MapResizeEvent.TYPE,
				new MapResizeEventHandler() {

					@Override
					public void apply(MapResizeEvent resizeEvent) {
						// GWT.log("Размер : " + entryPoint.getMapZoom());
						map.zoomTo(entryPoint.getMapZoom());
					}
				});
	}

	private void mapDataLast() {
		entryPoint.loadDataLast();
	}

	private void refreshObjects() {
		dataSensorCurrent = entryPoint.getDataSensorLast();
		Point objPoint;
		bounds = new Bounds();
		boolean noDataPoint = true;
		for (DataSensorLast dasl : dataSensorCurrent) {
			noDataPoint = false;
			objPoint = new Point(dasl.getDaslLongitude(),
					dasl.getDaslLatitude());
			objPoint.transform(SRC_PROJ, DST_PROJ);
			bounds.extend(objPoint);
			daslSensor = dasl;
			String imageUrl = getImageUrl(images.green(),
					daslSensor.getDaslCourse(), daslSensor.getDaslSog(),
					daslSensor.getDaslDatetime(), daslSensor.getDaslDtm(),
					daslSensor.getDaslSatUsed(), true);
			Style objStyle = new Style();
			objStyle.setLabelYOffset(-imageHeight / DIVID);
			objStyle.setLabelAlign("ct");
			objStyle.setFontColor("#0000FF");
			objStyle.setFillOpacity(1.0);
			objStyle.setLabel(dasl.getDaslObjectName());
			objStyle.setGraphicSize(getImageWidth(), getImageHeight());
			objStyle.setExternalGraphic(imageUrl);
			if (courseCurrent != 0) {
				objStyle.setRotation(String.valueOf(courseCurrent));
			}
			final VectorFeature pointFeature = new VectorFeature(objPoint,
					objStyle);
			final Attributes attr = new Attributes();
			String popupInfo = getObjectInfoHTML(dasl);
			attr.setAttribute("info", popupInfo);
			pointFeature.setAttributes(attr);
			pointFeature.setFeatureId(String.valueOf(dasl.getDaslUid()));
			if (markers.getFeatureById(String.valueOf(dasl.getDaslUid())) == null) {
				markers.addFeature(pointFeature);
			} else {
				int k = getIndexOfUID(dasl);
				try {
					if (k >= 0) {
						final VectorFeature pointFeatureCurrent = markers
								.getFeatureById(String.valueOf(dasl
										.getDaslUid()));
						final Popup markerPopup = pointFeatureCurrent
								.getPopup();
						if (!dataSensorLast.get(k).getDaslDatetime()
								.equals(dasl.getDaslDatetime())) {
							LonLat lonLat = new LonLat(dasl.getDaslLongitude(),
									dasl.getDaslLatitude());
							pointFeatureCurrent.setStyle(objStyle);
							pointFeatureCurrent.setAttributes(attr);
							if (dasl.getDaslLongitude() != dataSensorLast
									.get(k).getDaslLongitude()
									|| dasl.getDaslLatitude() != dataSensorLast
											.get(k).getDaslLatitude()) {
								lonLat.transform(SRC_PROJ_NAME, DST_PROJ_NAME);
								if (lonLat != null) {
									pointFeatureCurrent.move(lonLat);
									if (markerId != null
											&& markerId == dasl.getDaslUid()) {
										/*
										 * GWT.log(
										 * "Обновляем информацию об объекте : "
										 * + dasl.getDaslUid());
										 */
										markerPopup.setLonLat(lonLat);
										markerPopup.setContentHTML(popupInfo);
										map.addPopupExclusive(markerPopup);
									}
								}
							}
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					new DbMessage(MessageType.ERROR, "Ошибка кол-во: "
							+ dataSensorLast.size());
				}
			}
		}
		dataSensorLast.clear();
		dataSensorLast.addAll(dataSensorCurrent);

		if (noDataPoint) {
			if (markers.getFeatureById(NO_DATA) == null) {
				objPoint = UNKNOWN_POINT;
				objPoint.transform(SRC_PROJ, DST_PROJ);
				bounds = UNKNOWN_BOUND;
				bounds.transform(SRC_PROJ, DST_PROJ);
				String imageUrl = getNoDataImageUrl();
				Style objStyle = new Style();
				objStyle.setLabelYOffset(-imageHeight / DIVID);
				objStyle.setLabelAlign("ct");
				objStyle.setFontColor("#0000FF");
				objStyle.setFillOpacity(1.0);
				objStyle.setLabel(NO_DATA);
				objStyle.setGraphicSize(getImageWidth(), getImageHeight());
				objStyle.setExternalGraphic(imageUrl);
				final VectorFeature pointFeature = new VectorFeature(objPoint,
						objStyle);
				pointFeature.setFeatureId(NO_DATA);
				markers.addFeature(pointFeature);
			}

		} else {
			if (markers.getFeatureById(NO_DATA) != null) {
				markers.getFeatureById(NO_DATA).destroy();
			}
		}
		DataView();
	}

	private int getIndexOfUID(DataSensorLast dasl) {
		int indexOfUID = -1;
		for (int i = 0; i < dataSensorLast.size(); i++) {
			if (dasl.getDaslUid() == dataSensorLast.get(i).getDaslUid()) {
				indexOfUID = i;
				break;
			}
		}
		return indexOfUID;
	}

	private void DataView() {
		reCalcMap();
		widget.add(mapWidget);
		entryPoint.setContent(widget);
		addTrack();
	}

	private void reCalcMap() {
		if (entryPoint.isNeedReCalc()) {
			LonLat mapCenter = bounds.getCenterLonLat();
			if (Cookies.getCookie(COOKIE_MAP_CENTER_LON) == null
					|| Cookies.getCookie(COOKIE_MAP_CENTER_LON).isEmpty()
					|| Cookies.getCookie(COOKIE_MAP_CENTER_LAT) == null
					|| Cookies.getCookie(COOKIE_MAP_CENTER_LAT).isEmpty()) {
				mapCenter = bounds.getCenterLonLat();
			} else {
				mapCenter = new LonLat(
						Double.valueOf(Cookies.getCookie(COOKIE_MAP_CENTER_LON)),
						Double.valueOf(Cookies.getCookie(COOKIE_MAP_CENTER_LAT)));
			}
			if (Cookies.getCookie(COOKIE_MAP_ZOOM) == null
					|| Cookies.getCookie(COOKIE_MAP_ZOOM).isEmpty()) {
				map.zoomToExtent(bounds);
			} else {
				map.zoomTo(Integer.parseInt(Cookies.getCookie(COOKIE_MAP_ZOOM)));
			}
			int mapZoom = map.getZoom();
			if (mapZoom > ZOOM_MIN) {
				mapZoom = ZOOM_MIN;
			}
			if (mapZoom < ZOOM_MAX) {
				mapZoom = ZOOM_MAX;
			}
			entryPoint.setMapCenter(mapCenter);
			entryPoint.setMapZoom(mapZoom);
			eventBusData.fireEvent(new MapCenterEvent());
			eventBusData.fireEvent(new MapResizeEvent());
			entryPoint.setNeedReCalc(false);
		}
	}

	public void stopRefreshTimer() {
		if (refreshTimer != null) {
			refreshTimer.cancel();
		}
		if (infoBox != null) {
			infoBox.setVisible(false);
		}
	}

	public void refreshData() {
		infoBox.setVisible(true);
		if (refreshTimer != null) {
			if (!refreshTimer.isRunning()) {
				refreshTimer.scheduleRepeating(entryPoint.getRefreshInterval());
			}
		} else {
			mapDataLast();
		}
	}

	public void addTrack() {
		int pointsNum = 0;
		this.dataTrack = entryPoint.getTrackDataSensor();
		if (this.dataTrack != null) {
			pointsNum = this.dataTrack.size();
		}
		if (pointsNum > 0) {
			pointArray = new ArrayList<Point>();
			for (int i = 0; i < pointsNum; i++) {
				final DataSensor dasnSensor = this.dataTrack.get(i);
				courseCurrent = 0;
				if (dasnSensor.getDasnType() == TRACK_STOP) {
					addPoint(
							dasnSensor,
							images.parking(),
							getParkingImageUrl(),
							getParkingInfoHTML(dasnSensor),
							parking,
							new StringBuffer()
									.append(DATE_TIME_SHORT_FORMAT
											.format(dasnSensor
													.getDasnDatetime()))
									.append("\n")
									.append(dasnSensor.getDasnXml()).toString(),
							true);
				} else if (dasnSensor.getDasnType() == TRACK_ROUND) {
					addPoint(dasnSensor, images.information(),
							getInfoImageUrl(), getCourseInfoHTML(dasnSensor),
							courses, null, true);
				} else if (dasnSensor.getDasnType() == TRACK_BEGIN) {
					addPoint(dasnSensor, images.information(),
							getInfoImageUrl(), getCourseInfoHTML(dasnSensor),
							courses, null, true);
				} else if (dasnSensor.getDasnType() == TRACK_END) {
					addPoint(dasnSensor, images.information(),
							getInfoImageUrl(), getCourseInfoHTML(dasnSensor),
							courses, null, true);

				} else {
					Point point = new Point(dasnSensor.getDasnLongitude(),
							dasnSensor.getDasnLatitude());
					point.transform(SRC_PROJ, DST_PROJ);
					pointArray.add(point);
				}
			}
			Style lineStyle = new Style();
			lineStyle.setStrokeColor("#0000FF");
			addDrivePoints();
			if (lineFeature == null) {
				LineString line = new LineString(
						pointArray.toArray(new Point[pointArray.size()]));
				lineFeature = new VectorFeature(line, lineStyle);
				vectorLayer.addFeature(lineFeature);
				map.zoomToExtent(vectorLayer.getDataExtent());
			} else {
				vectorLayer.addFeature(lineFeature);
			}

		}
	}

	private void addDrivePoints() {
		if (this.dataTrack != null) {

			courses.destroyFeatures();
			double distance = 0;
			/*
			 * TODO Надо будет коэфициент расчитать по размеру экрана.
			 */
			// GWT.log("Разрешение в зуме : " +
			// map.getResolutionForZoom(map.getZoom()));
			double minDistance = 3 * (Math.pow(2, (ZOOM_MIN - map.getZoom())));
			// GWT.log("Расчёт точек для дистанции: " + minDistance +
			// " масштаб "+ map.getZoom());
			DataSensor dasnSensorPrev = null;
			//
			LonLat lonLat = null;
			final Bounds bounds = map.getExtent();

			for (DataSensor dasnSensor : this.dataTrack) {
				//
				lonLat = new LonLat(dasnSensor.getDasnLongitude(),
						dasnSensor.getDasnLatitude());
				lonLat.transform(SRC_PROJ_NAME, DST_PROJ_NAME);
				// Проверка видимости точки
				if (bounds.containsLonLat(lonLat, true)) {

					// Дистанция
					if (dasnSensorPrev != null) {
						distance = distance
								+ getDistance(dasnSensorPrev, dasnSensor);
					}

					if (dasnSensor.getDasnType() == TRACK_POINT
							|| dasnSensor.getDasnType() == TRACK_DRIVE) {

						if (distance > minDistance) {
							distance = 0;
							if (dasnSensor.getDasnType() == TRACK_POINT) {
								entryPoint
										.setTrackInfo(dasnSensor.getDasnXml());
							} else {
								String imageUrl = getImageUrl(images.arrow2(),
										dasnSensor.getDasnCourse(),
										dasnSensor.getDasnSog(),
										dasnSensor.getDasnDatetime(),
										dasnSensor.getDasnDtm(),
										dasnSensor.getDasnSatUsed(), false);
								addPoint(dasnSensor, images.arrow2(), imageUrl,
										getCourseInfoHTML(dasnSensor), courses,
										null, false);
							}

						}

					}
					dasnSensorPrev = dasnSensor;
				}
			}

		}
		// GWT.log("Видимость точек: "+row);
	}

	private void addPoint(DataSensor dasnSensor, ImageResource image,
			String imageURL, String info, Vector layer, String label,
			boolean addPoint) {
		if (dasnSensor != null) {
			Point point = new Point(dasnSensor.getDasnLongitude(),
					dasnSensor.getDasnLatitude());
			point.transform(SRC_PROJ, DST_PROJ);
			Style objStyle = new Style();
			objStyle.setLabelYOffset(-imageHeight / DIVID);
			objStyle.setLabelAlign("ct");
			objStyle.setFontColor("#0000FF");
			objStyle.setFillOpacity(1.0);

			if (label == null && dasnSensor != null) {
				objStyle.setLabel(DATE_TIME_SHORT_FORMAT.format(dasnSensor
						.getDasnDatetime()));
			} else {
				objStyle.setLabel(label);
			}
			objStyle.setExternalGraphic(imageURL);
			objStyle.setGraphicSize(imageWidth, imageHeight);
			if (courseCurrent != 0) {
				objStyle.setRotation(String.valueOf(courseCurrent));
			}

			final VectorFeature pointFeature = new VectorFeature(point,
					objStyle);
			Attributes attr = new Attributes();
			attr.setAttribute("info", info);
			pointFeature.setAttributes(attr);
			if (dasnSensor == null) {
				pointFeature.setFeatureId(info);
			} else {
				pointFeature
						.setFeatureId(String.valueOf(dasnSensor.getDasnId()));
			}
			layer.addFeature(pointFeature);
			if (addPoint) {
				pointArray.add(point);
			}
		}
	}

	public void clearTrack() {
		if (lineFeature != null) {
			vectorLayer.removeFeature(lineFeature);
			lineFeature.destroy();
			lineFeature = null;
		}
		dataTrack = null;
		courses.destroyFeatures();
		parking.destroyFeatures();
		vectorLayer.removeAllFeatures();
		entryPoint.setTrackData(null);
		// Центрирование
		entryPoint.setMapCenter(map.getCenter());
		entryPoint.setMapZoom(map.getZoom());
	}

	public void writePosition() {
		if (map != null) {
			entryPoint.setMapZoom(map.getZoom());
			entryPoint.setMapCenter(map.getCenter());
		}
	}

	private void initInfoPanel() {
		infoPanel.setVisible(true);
		infoPanel.setTitle(constants.InfoPanelTitle());
		infoBox.add(infoPanel);
		infoBox.setModal(false);
		infoBox.setAnimationEnabled(true);
		infoBox.setPopupPosition(
				this.entryPoint.getLeft(),
				(this.entryPoint.getHeight()
						- (this.entryPoint.getHeight() / INFO_PANEL_HEIGHT_DIV) - this.entryPoint
						.getBorderSize()));
		infoBox.show();
	}

	public void refreshInfoPanel() {
		infoPanel.refreshInfoTable();
	}

	private String getImageUrl(ImageResource img, Double course, Double speed,
			Date lastdate, Date lastmodify, long satelite, boolean checkDate) {
		Date date = new Date();
		date.setTime(date.getTime() - stopTimeLong);
		courseCurrent = course.intValue();
		if (speed < MIN_SPEED) {
			if ((satelite == 0) && (speed == 0)) {
				img = images.lbs_yellow();
			} else {
				img = images.stop_blue();
			}
			courseCurrent = 0;
		}
		if (checkDate && lastdate.before(date) && lastmodify.before(date)) {
			if (speed < MIN_SPEED) {
				img = images.stop_red();
				courseCurrent = 0;
			} else {
				img = images.red();
			}
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
		String imageUri = (new StringBuilder().append(IMAGES_PATH).append(
				img.getName()).append(".png")).toString();
		return imageUri;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	private int getImageWidth() {
		return imageWidth;
	}

	private String getInfoImageUrl() {
		imageHeight = ICON_HEIGHT;
		imageWidth = ICON_WIDTH;
		String urlImage = images.information().getSafeUri().asString();
		return urlImage;
	}

	private String getNoDataImageUrl() {
		imageHeight = ICON_HEIGHT;
		imageWidth = ICON_WIDTH;
		String urlImage = images.nodata().getSafeUri().asString();
		return urlImage;
	}

	private String getParkingImageUrl() {
		imageHeight = ICON_HEIGHT;
		imageWidth = ICON_WIDTH;
		String urlImage = images.parking().getSafeUri().asString();
		return urlImage;
	}

	private String getCourseInfoHTML(DataSensor dasn) {
		StringBuilder info = new StringBuilder();
		info.append("Дата: ")
				.append(DATE_TIME_FORMAT.format(dasn.getDasnDatetime()))
				.append("</br>Скорость: ")
				.append(Math.round(dasn.getDasnSog()))
				.append("</br>Спутники: ").append(dasn.getDasnSatUsed());
		return info.toString();
	}

	private String getParkingInfoHTML(DataSensor dasn) {
		StringBuilder info = new StringBuilder();
		info.append("Дата: ")
				.append(DATE_TIME_FORMAT.format(dasn.getDasnDatetime()))
				.append("</br>Длительность стоянки: ")
				.append(dasn.getDasnXml()).append("</br>Спутники: ")
				.append(dasn.getDasnSatUsed());
		return info.toString();
	}

	private String getObjectInfoHTML(DataSensorLast dasl) {
		// setObjectInfo(temp.getDaslVehicle());
		StringBuilder info = new StringBuilder();
		info.append("Объект: ").append(dasl.getDaslObjectInfo())
				.append("</br>Дата: ")
				.append(DATE_TIME_FORMAT.format(dasl.getDaslDatetime()))
				.append("</br>Скорость: ").append(dasl.getDaslSog())
				.append("</br>Спутники: ").append(dasl.getDaslSatUsed());
		return info.toString();
	}

	public void showInfoPanel() {
		this.infoBox.show();
	}

	public void deleteInfoPanel() {
		this.infoPanel.hide();
		this.infoBox.hide();
	}

	private void addGeoZones() {
		// Геозоны
		geoZones = new Vector("Геозоны");
		geoZones.setIsBaseLayer(false);
		geoZones.setIsVisible(false);
		map.addLayer(geoZones);
		final DrawFeatureOptions drawGeoZoneOptions = new DrawFeatureOptions();
		// HandlerOptions geoZonesHandlerOptions = new HandlerOptions();
		// geoZonesHandlerOptions.setKeyMask(keyMask);;
		// drawGeoZoneOptions.setHandlerOptions(geoZonesHandlerOptions);
		PolygonHandler drawPolygonHandler = new PolygonHandler();
		//
		drawPolygonControl = new DrawFeature(geoZones, drawPolygonHandler,
				drawGeoZoneOptions);
		map.addControl(drawPolygonControl);
	    final SelectFeature selectFeature = new SelectFeature(geoZones);
	    selectFeature.setAutoActivate(true);
	    map.addControl(selectFeature);
		drawGeoZones();
		geoZones.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
            public void onFeatureSelected(FeatureSelectedEvent eventObject) {
            	Window.alert("Идентификатор объекта : " + eventObject.getVectorFeature().getFeatureId());
            	
                selectFeature.unSelect(eventObject.getVectorFeature());
            }
        });
	}

	private void drawGeoZones() {

		AsyncCallback<List<GisDataPoint>> callback = new AsyncCallback<List<GisDataPoint>>() {

			@Override
			public void onFailure(Throwable caught) {
				new DbMessage(MessageType.ERROR, "Ошибка загрузки геозон : "
						+ caught.getMessage());
			}

			@Override
			public void onSuccess(List<GisDataPoint> result) {
				for (int i = 0; i < result.size(); i++) {
					drawGeoZone(result.get(i));
				}
			}

			private void drawGeoZone(GisDataPoint gisDataPoint) {
				Style zoneStyle = new Style();
				zoneStyle.setStrokeColor("#00AAFF");
				zoneStyle.setFillColor("red");
				zoneStyle.setStrokeWidth(1);
				zoneStyle.setFillOpacity(0.1);

				if (gisDataPoint.getGsdtPoint().matches(
						"^MULTILINESTRING\\(\\(.*\\)\\)$")) {
					String[] points = gisDataPoint.getGsdtPoint()
							.replaceAll("MULTILINESTRING\\(\\(", "")
							.replaceAll("\\)\\)", "").split("\\),\\(");
					List<Point> pointArray = new ArrayList<Point>();
					String[] line = new String[2];
					double lon;
					double lat;
					for (int i = 0; i < points.length; i++) {
						line = points[i].split(",");
						line = line[0].split(" ");
						lon = Double.valueOf(line[0]);
						lat = Double.valueOf(line[1]);
						Point point = new Point(lon, lat);
						point.transform(SRC_PROJ, DST_PROJ);
						pointArray.add(point);
					}
					if (pointArray.size() > 2) {
						LinearRing linearRing = new LinearRing(
								pointArray.toArray(new Point[pointArray.size()]));
						zoneStyle.setLabel(gisDataPoint.getGsdtInfo());
						VectorFeature zoneFeature = new VectorFeature(
								linearRing, zoneStyle);
						zoneFeature.setFeatureId(String.valueOf(gisDataPoint.getGsdtId()));
						geoZones.addFeature(zoneFeature);
					}

				}

			}

		};

		rpcObject.getGeomPointObjects(callback);

	}

	public void deactivateGeoZones() {
		if (drawPolygonControl != null && drawPolygonControl.isActive()) {
			drawPolygonControl.deactivate();
		}
	}

	public void activateGeoZones() {
		if (drawPolygonControl != null && (!drawPolygonControl.isActive())) {
			drawPolygonControl.activate();
		}
	}

	/*
	 * Вычисление дистанции.
	 */
	public double getDistance(DataSensor dataSensorPrev,
			DataSensor dataSensorLast) {
		// Вычисление дистанции
		// косинусы и синусы широт и разниц долгот
		double lat1 = dataSensorPrev.getDasnLatitude() * PI;
		double lat2 = dataSensorLast.getDasnLatitude() * PI;
		double cl1 = Math.cos(lat1);
		double cl2 = Math.cos(lat2);
		double sl1 = Math.sin(lat1);
		double sl2 = Math.sin(lat2);
		double delta = dataSensorLast.getDasnLongitude() * PI
				- dataSensorPrev.getDasnLongitude() * PI;
		double cdelta = Math.cos(delta);
		double sdelta = Math.sin(delta);
		// вычисления длины большого круга
		double p1 = Math.pow(cl2 * sdelta, 2);
		double p2 = Math.pow(((cl1 * sl2) - (sl1 * cl2 * cdelta)), 2);
		return (Math.atan(Math.pow(p1 + p2, 0.5)
				/ (sl1 * sl2 + cl1 * cl2 * cdelta)))
				* EARTH_RADIUS;
	}

}
