/**
 * 
 */
package ru.novaris.novoscan.client.resources;


import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.Icon;
import org.gwtopenmaps.openlayers.client.Pixel;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.geometry.Point;

import ru.novaris.novoscan.client.DatabaseRead;
import ru.novaris.novoscan.client.DatabaseReadAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * @author kur
 * 
 */
public interface ImplConstantsGWT {
	public final SimpleEventBus eventBusData = new SimpleEventBus();
	
	public static final Constants constants = GWT.create(Constants.class);

	public static final ImagesNovoscan images = GWT
			.create(ImagesNovoscan.class);

	public static final DatabaseReadAsync rpcObject = GWT
			.create(DatabaseRead.class);

	public static final Point UNKNOWN_POINT = new Point(94.15, 66.25);

	public static final Bounds UNKNOWN_BOUND = new Bounds(74.15, 46.25, 114.15,
			76.25);
	public static final String DST_PROJ_NAME = "EPSG:900913";

	public static final String SRC_PROJ_NAME = "EPSG:4326";

	public static final Projection DST_PROJ = new Projection(DST_PROJ_NAME);

	public static final Projection SRC_PROJ = new Projection(SRC_PROJ_NAME);
	

	public static final DateTimeFormat DATE_TIME_SHORT_FORMAT = DateTimeFormat
			.getFormat(constants.dtfs());

	public static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat
			.getFormat(constants.dtf());
	
	public static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(constants.dtfd());
	
	public static final DateTimeFormat DATE_FORMAT_JAVA = DateTimeFormat
			.getFormat(PredefinedFormat.ISO_8601);
	
	
	public static final Size BUBBLE_SIZE = new Size(170, 100);

	public static final Icon SHADOW_ICON = new Icon("", new Size(0, 0),
			new Pixel(0, 0));

	public static final Size ICON_SIZE = new Size(16, 16);

	public static final Pixel ICON_OFFSET = new Pixel(-8, -8);

	public static final Size PARKING_SIZE = new Size(20, 20);

	public static final Pixel PARKING_OFFSET = new Pixel(-10, -10);
	
	public static final NumberFormat FORMAT_LAT_LON = NumberFormat
			.getFormat("###.000000");
	
	public static final String REPORT_SERVER_SERVLET = "ReportServlet";
	
	public static final int BAR_SIZE = 20;
	
	public static final int MENU_SIZE = 10;
	
	public static final int HEADER_ROW_INDEX = 0;
	
	public enum MessageType { ERROR, WARNING, INFO };
	
	public static final String DIV_CONTENT = "Content";
	
	public static final String DIV_MENU = "MenuBar2Container";
	
	public static final int KEY_COLUMN = 3;

	public static final int  FORMAT_GPX = 3;

	public static final int  FORMAT_KMZ = 2;

	public static final int  FORMAT_KML = 1;
	
	public static final String[] FORMATS = {"osm","kml","kmz","gpx"};

	public static final int  FORMAT_OPENSTREET = 0;
	
	public static final int OBJECT_REFRESH_INTERVAL = 5000;

	public static final int BORDER_SIZE = 10;
	
	public static final int UNAVAILABLE_TIME_LONG = 86400000; // 1000 * 3600 * 24
	
	public static final int STOP_TIME_LONG = 3600000; // 1000 * 3600
	
	public static final int WMS_NUM_ZOOM_LEVEL = 30; // 
	
	public static final String NOVOSCAN_MAP_SERVER = "http://maps.novaris.ru:8081/geoserver/novoscan/wms";

	public static final String NOVOSCAN_MAP_LAYER = "novoscan:novoscan";
	
}
