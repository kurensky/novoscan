package ru.novaris.novoscan.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.gwtopenmaps.openlayers.client.LonLat;

import ru.novaris.novoscan.client.resources.CheckSessionEvent;
import ru.novaris.novoscan.client.resources.CheckSessionEventHandler;
import ru.novaris.novoscan.client.resources.ImagesNovoscan;
import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;
import ru.novaris.novoscan.client.resources.LastDataEvent;
import ru.novaris.novoscan.domain.AccountRoles;
import ru.novaris.novoscan.domain.DataSensor;
import ru.novaris.novoscan.domain.DataSensorLast;
import ru.novaris.novoscan.domain.Profiles;
import ru.novaris.novoscan.domain.SprvReports;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Novoscan implements EntryPoint, ImplConstants, ImplConstantsGWT {

	private Novoscan entryPoint = this;

	private ImagesNovoscan images;

	private Integer clientTimeOffset;

	private int refreshInterval = OBJECT_REFRESH_INTERVAL;

	private VerticalPanel emptyPanel = new VerticalPanel();

	private List<AccountRoles> listRoles = new ArrayList<AccountRoles>();

	private List<DataSensorLast> dataSensorLast = new ArrayList<DataSensorLast>();

	private int heigth = 0;

	private int width = 0;

	private int top = 0;

	private int left = 0;

	private int stopTimeLong = STOP_TIME_LONG; // время которое считается
											// недоступностью
	private boolean needReCenter = true;

	private boolean needReZoom = true;

	private boolean needReCalc = true;

	private List<SprvReports> sprvReports = new ArrayList<SprvReports>();

	public int getStopTimeLong() {
		return this.stopTimeLong;
	}

	public void setStopTimeLong(int stopTimeLong) {
		this.stopTimeLong = stopTimeLong;
	}

	private List<DataSensor> dataSensor = new ArrayList<DataSensor>();

	private Date trackDataBegin;

	private Date trackDataEnd;

	private long uid;

	private String trackInfo;

	private MainMenu menuPanel;

	private final int borderSize = BORDER_SIZE;

	protected HashMap<String, Float> mapProfilesN = new HashMap<String, Float>();

	protected HashMap<String, String> mapProfilesV = new HashMap<String, String>();

	protected HashMap<String, Date> mapProfilesD = new HashMap<String, Date>();

	private LonLat mapCenter;

	private String timeZoneID;

	private int unavTimeLong = UNAVAILABLE_TIME_LONG; // 

	private Boolean speed;
	
	public Integer getClientTimeOffset() {
		return clientTimeOffset;
	}

	public long getUserId() {
		if (Cookies.getCookie(COOKIE_TAG_ID) == null
				&& Cookies.getCookie(COOKIE_TAG_ID).isEmpty()) {
			return (long) -1;
		} else {
			return Long.parseLong(Cookies.getCookie(COOKIE_TAG_ID));
		}
	}

	public void setContent(Widget W) {
		W.setStyleName(DIV_CONTENT);
		RootPanel.get(DIV_CONTENT).clear();
		RootPanel.get(DIV_CONTENT).add(W);
	}

	public static Widget getContent() {
		return RootPanel.get(DIV_CONTENT);
	}

	// *************************************************************************************
	@SuppressWarnings("deprecation")
	public void onModuleLoad() {
		Date today = new Date();
		clientTimeOffset = today.getTimezoneOffset();
		Cookies.setCookie(COOKIE_TIMEZONE_OFFSET, String.valueOf(clientTimeOffset));
		CwLogonEntry cwLogonEntry = new CwLogonEntry(this);
		cwLogonEntry.setStyleName("gwt-Login-Form");
		RootPanel.get(DIV_CONTENT).clear();
		RootPanel.get(DIV_CONTENT).add(cwLogonEntry);
	}

	// *************************************************************************************
	public void logoff() {
		//Cookies.removeCookie(COOKIE_TAG_NAME,"/");
		Cookies.removeCookie(COOKIE_TAG_PASSWORD,"/");
		Cookies.removeCookie(COOKIE_TAG_ID,"/");
		clearProfiles();
		clearRoles();
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {

			}

			@Override
			public void onFailure(Throwable caught) {

			}
		};
		rpcObject.logout(callback);
		Window.Location.reload();
	}

	private void clearRoles() {
		listRoles.clear();
	}
	
	private String getWindowWidth() {
		int width = Window.getClientWidth() + Window.getScrollLeft() - BAR_SIZE;
		return width + Unit.PX.getType();
	}

	private String getWindowHeight() {
		int height = Window.getClientHeight() + Window.getScrollTop() - BAR_SIZE - MENU_SIZE;
		return height + Unit.PX.getType();
	}

	// ************************************************************************************

	public List<String> getRoles() {
		List<String> roles = new ArrayList<String>();
		for (int i = 0; i < listRoles.size(); i++) {
			roles.add(listRoles.get(i).getAcrlRoleName());
		}
		return roles;
	}

	// *********************************************

	private void clearProfiles() {
		mapProfilesN.clear();
		mapProfilesD.clear();
		mapProfilesV.clear();
	}

	private void loadProfile() {
		AsyncCallback<List<Profiles>> callback = new AsyncCallback<List<Profiles>>() {
			public void onFailure(Throwable caught) {
				checkSession("Ошибка загрузки профиля!");
			}

			@Override
			public void onSuccess(List<Profiles> result) {
				// профиль.
				for (Profiles rec : result) {
					if (rec.getProfAttrType().equalsIgnoreCase("V")) {
						mapProfilesV.put(rec.getProfAttrName(),
								rec.getProfValuev());
					} else if (rec.getProfAttrType().equalsIgnoreCase("N")) {
						mapProfilesN.put(rec.getProfAttrName(),
								rec.getProfValuen());
					} else if (rec.getProfAttrType().equalsIgnoreCase("D")) {
						mapProfilesD.put(rec.getProfAttrName(),
								rec.getProfValued());
					}
				}
			}
		};
		clearProfiles();
		rpcObject.getProfiles(callback);
	}

	// *******************************************
	private void loadRoles() {
		AsyncCallback<List<AccountRoles>> callroles = new AsyncCallback<List<AccountRoles>>() {
			public void onFailure(Throwable caught) {
				checkSession("Ошибка загрузки ролей!");
			}
			public void onSuccess(List<AccountRoles> result) {
				listRoles = result;
				emptyPanel.addStyleName(DIV_CONTENT);
				Label emptyLabel = new Label(constants.WorkingInfo());
				emptyLabel.addStyleName("screenMessage");
				emptyPanel.add(emptyLabel);
				emptyPanel.setSize(getWindowWidth(),getWindowHeight());
				VerticalPanel panelInfo = new VerticalPanel();
				panelInfo
						.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				RootPanel.get(DIV_CONTENT).add(emptyPanel);
				heigth = emptyPanel.getOffsetHeight();
				width = emptyPanel.getOffsetWidth();
				left = emptyPanel.getAbsoluteLeft();
				top = emptyPanel.getAbsoluteTop();
				AsyncCallback<List<SprvReports>> callreport = new AsyncCallback<List<SprvReports>>() {
					@Override
					public void onFailure(Throwable caught) {
						checkSession("Ошибка получения списка отчётов!");
						menuPanel = new MainMenu(entryPoint);
					}

					@Override
					public void onSuccess(List<SprvReports> reports) {
						sprvReports = reports;
						menuPanel = new MainMenu(entryPoint);
					}

				};
				rpcObject.getListReports(callreport);
			}
		};
		clearRoles();
		rpcObject.getAccountRoles(callroles);
	}

	public void loadDataLast() {
		AsyncCallback<List<DataSensorLast>> callback = new AsyncCallback<List<DataSensorLast>>() {
			public void onFailure(Throwable caught) {
				checkSession("Ошибка загрузки последних данных!");
			}

			public void onSuccess(List<DataSensorLast> result) {
				dataSensorLast = result;
				for (int i = 0; i < dataSensorLast.size(); i++) {
					dataSensorLast.get(i).setDaslSog(
							(double) Math.round(dataSensorLast.get(i)
									.getDaslSog()));
				}
				eventBusData.fireEvent(new LastDataEvent());
			}
		};
		rpcObject.getListSensor(callback);
	}

	public List<DataSensorLast> getDataSensorLast() {
		return dataSensorLast;
	}

	public void clearDataSensorLast() {
		dataSensorLast.clear();
	}

	public void viewMain() {
		RootPanel.getBodyElement().getStyle().setProperty("cursor", "wait");
		addListeners();
		loadProfile();
		loadRoles();
	}

	private void addListeners() {
		eventBusData.addHandler(CheckSessionEvent.TYPE,
				new CheckSessionEventHandler() {

					@Override
					public void apply(CheckSessionEvent checkEvent) {
						checkSession("Ошибка запроса данных!");
					}
				});
	}

	private void checkSession(final String errorMessage) {
		AsyncCallback<Integer> session = new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				logoff();
			}

			@Override
			public void onSuccess(Integer result) {
				new DbMessage(MessageType.ERROR, errorMessage);
				if (result < 0) {
					logoff();
				}
			}
		};
		rpcObject.checkSession(session);
	}

	public int getBorderSize() {
		return this.borderSize;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.heigth;
	}

	public int getLeft() {
		return this.left;
	}

	public int getTop() {
		return this.top;
	}

	public int getRefreshInterval() {
		return this.refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public void setMapZoom(int mapZoom) {
		Cookies.setCookie(COOKIE_MAP_ZOOM, Integer.toString(mapZoom));
	}

	public int getMapZoom() {
		if (Cookies.getCookie(COOKIE_MAP_ZOOM) == null 
		 || Cookies.getCookie(COOKIE_MAP_ZOOM).isEmpty()) {
			return ZOOM_MIN;
		} else {
			return Integer.parseInt(Cookies.getCookie(COOKIE_MAP_ZOOM));
		}
	}

	public void refreshMapData() {
		menuPanel.refreshMapData();
	}

	public void setTrackData(List<DataSensor> dataSensor) {
		addTrackDataSensor(dataSensor);
	}

	private void addTrackDataSensor(List<DataSensor> dataSensor) {
		this.dataSensor = dataSensor;
	}

	public List<DataSensor> getTrackDataSensor() {
		return this.dataSensor;
	}

	public void addTrack() {
		menuPanel.addTrack();
	}

	public void clearTrack() {
		menuPanel.clearTrack();
	}

	public void setTrackDataBegin(Date trackDataBegin) {
		this.trackDataBegin = trackDataBegin;
	}

	public void setTrackDataEnd(Date trackDataEnd) {
		this.trackDataEnd = trackDataEnd;
	}

	public Date getTrackDataBegin() {
		return this.trackDataBegin;
	}

	public Date getTrackDataEnd() {
		return this.trackDataEnd;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getUid() {
		return this.uid;
	}

	public void setTrackInfo(String trackInfo) {
		this.trackInfo = trackInfo;
	}

	public String getTrackInfo() {
		return this.trackInfo;
	}

	public boolean isNeedReCenter() {
		return this.needReCenter;
	}

	public void setNeedReCenter(boolean needReCenter) {
		this.needReCenter = needReCenter;
	}

	public boolean isNeedReZoom() {
		return needReZoom;
	}

	public void setNeedReZoom(boolean needReZoom) {
		this.needReZoom = needReZoom;
	}

	public boolean isNeedReCalc() {
		return needReCalc;
	}

	public void setNeedReCalc(boolean needReCalc) {
		this.needReCalc = needReCalc;
	}

	public String getUserName() {
		return Cookies.getCookie(COOKIE_TAG_NAME);
	}

	public ImagesNovoscan getImages() {
		return images;
	}

	public void setImages(ImagesNovoscan images) {
		this.images = images;
	}

	public SimpleEventBus getEventBusData() {
		return eventBusData;
	}

	public void setMapCenter(LonLat mapCenter) {
		Cookies.setCookie(COOKIE_MAP_CENTER_LON, String.valueOf(mapCenter.lon()));
		Cookies.setCookie(COOKIE_MAP_CENTER_LAT, String.valueOf(mapCenter.lat()));
	}

	public LonLat getMapCenter() {
		
		if ( Cookies.getCookie(COOKIE_MAP_CENTER_LON) == null
		 || Cookies.getCookie(COOKIE_MAP_CENTER_LON).isEmpty() 
		 || Cookies.getCookie(COOKIE_MAP_CENTER_LAT) == null
		 || Cookies.getCookie(COOKIE_MAP_CENTER_LAT).isEmpty()) {
			//
		} else {
			mapCenter = new LonLat(Double.valueOf(Cookies.getCookie(COOKIE_MAP_CENTER_LON)),
			Double.valueOf(Cookies.getCookie(COOKIE_MAP_CENTER_LAT)));
		}
		return mapCenter;
	}

	public List<SprvReports> getSprvReports() {
		return sprvReports;
	}

	public void setSprvReports(List<SprvReports> sprvReports) {
		this.sprvReports = sprvReports;
	}

	public String getTimeZoneID() {
		return timeZoneID;
	}

	public void setTimeZoneID(String timeZoneID) {
		this.timeZoneID = timeZoneID;
	}

	public int getUnavTimeLong() {
		return unavTimeLong;
	}

	public void setSpeed(Boolean speed) {
		this.speed = speed;
	}
	public boolean isSpeed() {
		return speed;
	}

	public void removeTrackData() {
		dataSensor.clear();	
	}
}