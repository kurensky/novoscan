/**
 * 
 */
package ru.novaris.novoscan.client.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


/**
 * @author kur
 * 
 */
public interface ImplConstants {
	public static final int ZOOM_MIN = 22;

	public static final String NO_DATA = "Нет данных!";

	public static final int ZOOM_MAX = 3;

	public static final Double PM = 0.7;

	public static final int CONST_ADMIN = 0;

	public static final int CONST_USER = 100;

	public static final int CONST_OBJECT = 101;

	public static final int CONST_MODULE = 102;

	public static final int CONST_CLIENT = 103;

	public static final int CONST_DEPART = 104;

	public static final int CONST_READONLY = 1;

	public static final int CONST_READWRITE = 2;


	public static final int TRACK_STOP = -1;

	public static final int TRACK_BEGIN = -2;

	public static final int TRACK_END = -3;

	public static final int TRACK_DRIVE = -100;

	public static final int TRACK_ROUND = -101;

	public static final int TRACK_POINT = -200;
	
	public static final double BAD_COORDINATE = -9999;


	public static final String SQL_EQUAL = "=";

	public static final String SQL_LT = "<";

	public static final String SQL_GT = ">";

	public static final String SQL_LIKE = "LIKE";

	public static final String SQL_BETWEEN = "BETWEEN";

	public static final String SQL_AND = "AND";

	public static final String SQL_OR = "OR";

	public static final String SQL_TYPE_LONG = "LONG";

	public static final String SQL_TYPE_DOUBLE = "DOUBLE";

	public static final String SQL_TYPE_STRING = "STRING";

	public static final String SQL_TYPE_DATE = "DATE";
	
	public static final String SQL_TYPE_LIST_STRING = "LIST_STRING";

	static final List<String> EXPR_STRING = new ArrayList<String>(
			Arrays.asList(SQL_EQUAL, SQL_LIKE));

	static final List<String> EXPR_DATE = new ArrayList<String>(Arrays.asList(
			SQL_EQUAL, SQL_LT, SQL_GT, SQL_BETWEEN));

	static final List<String> EXPR_LONG = new ArrayList<String>(Arrays.asList(
			SQL_EQUAL, SQL_LT, SQL_GT, SQL_BETWEEN));

	public static final String CTRL = "\r\n";

	public static final int SEARCH_POINT_LIMIT = 100;

	public static final double DELTA_DISTANCE = 1.13;

	public static final int FIRST_DATA_ROW = 1;

	public static final String IMAGES_PATH = "images/";

	public static final int ICON_HEIGHT = 20;

	public static final int ICON_WIDTH = 20;

	public static final int MIN_SPEED = 5;

	public static final int MAX_INFO_OBJECTS = 50;

	public static final int INFO_PANEL_WIDTH_DIV = 2;

	public static final int INFO_PANEL_HEIGHT_DIV = 4;

	public static final int ZINDEX_POPUP = 99;

	public static final int ZINDEX_NORMAL = 80;

	public static final int DIVID = 3;

	public static final byte[] GWT_DES_KEY = 
		    { (byte) 0x45, (byte) 0x51,
			(byte) 0x43, (byte) 0x37, (byte) 0x75, (byte) 0x52, (byte) 0x57,
			(byte) 0x66, (byte) 0x75, (byte) 0x56, (byte) 0x4b, (byte) 0x52,
			(byte) 0x52, (byte) 0x76, (byte) 0x4c, (byte) 0x55, (byte) 0x34,
			(byte) 0x76, (byte) 0x50, (byte) 0x71, (byte) 0x76, (byte) 0x7a,
			(byte) 0x57, (byte) 0x4b
			};
	public static final int ACCESS_DENIED = -5;
	
	public static final String SUCCESS = "success";
	
	public static final Double MIN_LONG_COURSE = 3000.0; 

	public static final Double MIN_CHANGE_COURSE = 180.0; 
	
	public static double EARTH_RADIUS = 6372795;

	public static final double PI = Math.PI / 180;
	
	
	public static final String JASPER_FILE_EXTENSION = ".jasper";
	
	
	public static final String REPORT_FILE = "REPORT_FILE_NAME";
	
	public static final String REPORT_FILE_DIR = "/reports/";
	
	public static final String FORMAT_FILE = "FORMAT"; 
	
	public static final String TEMP_FILE_DIR = "/temp/";
	
    public final String COOKIE_TAG_PASSWORD = "userPassword";
	
    public final String COOKIE_TAG_NAME = "userName";
    
    public final String COOKIE_TAG_ID = "userId";
    
    public final String COOKIE_TAG_IP = "userIpAddress";
        
    public final String  COOKIE_TIMEZONE_OFFSET = "TimeZoneOffset";
    
    public final String COOKIE_MAP_ZOOM = "mapZoom";
    
    public final String COOKIE_MAP_CENTER_LON = "mapCenterLon";
    
    public final String COOKIE_MAP_CENTER_LAT = "mapCenterLat";
    
    public final String COOKIE_LAST_SENSOR_DATE = "lastSensorDate";
    
    public enum LogonCheck { UNKNOWN, FAIL, DENIED, ACCEPTED };

    public final Integer G_HYBRID_ZOOM_LEVEL = 20;
    
    public final Integer G_NORMAL_ZOOM_LEVEL = 22;
    
    public final Integer G_SATELLITE_ZOOM_LEVEL = 20;
    
    public final Integer G_TERRAIN_ZOOM_LEVEL = 16;

    
	
}
