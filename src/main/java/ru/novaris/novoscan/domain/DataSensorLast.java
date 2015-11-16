package ru.novaris.novoscan.domain;


import java.util.Date;

/**
 * DataSensorLast generated by hbm2java
 */
public class DataSensorLast implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long daslId;

	private long daslUid;

	private Date daslDatetime;

	private double daslLatitude;

	private double daslLongitude;

	private long daslStatus;

	private Long daslSatUsed;

	private Long daslZoneAlarm;

	private Long daslMacroId;

	private Long daslMacroSrc;

	private Double daslSog;

	private Double daslCourse;

	private Double daslHdop;

	private Double daslHgeo;

	private Double daslHmet;

	private Long daslGpio;

	private Long daslAdc;

	private Double daslTemp;

	private long daslType;

	private String daslXml;

	private Date daslDtm;

	private long daslSpsnId;

	private String daslVehicle;

	private long daslDasnId;
	
	private String daslObjectInfo;

	private String daslObjectName;
	
	private String daslAddress;

	private String daslIgnition;
	
	private String daslFuel;
	
	public DataSensorLast() {
	}

	public DataSensorLast(long daslId, long daslUid, Date daslDatetime,
			double daslLatitude, double daslLongitude, long daslStatus,
			long daslType, String daslXml, Date daslDtm, long daslSpsnId,
			long daslDasnId) {
		this.daslId = daslId;
		this.daslUid = daslUid;
		this.daslDatetime = daslDatetime;
		this.daslLatitude = daslLatitude;
		this.daslLongitude = daslLongitude;
		this.daslStatus = daslStatus;
		this.daslType = daslType;
		this.daslXml = daslXml;
		this.daslDtm = daslDtm;
		this.daslSpsnId = daslSpsnId;
		this.daslDasnId = daslDasnId;
	}

	public DataSensorLast(long daslId, long daslUid, Date daslDatetime,
			double daslLatitude, double daslLongitude, long daslStatus,
			Long daslSatUsed, Long daslZoneAlarm, Long daslMacroId,
			Long daslMacroSrc, Double daslSog, Double daslCourse,
			Double daslHdop, Double daslHgeo, Double daslHmet, Long daslGpio,
			Long daslAdc, Double daslTemp, long daslType, String daslXml,
			Date daslDtm, long daslSpsnId, String daslVehicle, long daslDasnId) {
		this.daslId = daslId;
		this.daslUid = daslUid;
		this.daslDatetime = daslDatetime;
		this.daslLatitude = daslLatitude;
		this.daslLongitude = daslLongitude;
		this.daslStatus = daslStatus;
		this.daslSatUsed = daslSatUsed;
		this.daslZoneAlarm = daslZoneAlarm;
		this.daslMacroId = daslMacroId;
		this.daslMacroSrc = daslMacroSrc;
		this.daslSog = daslSog;
		this.daslCourse = daslCourse;
		this.daslHdop = daslHdop;
		this.daslHgeo = daslHgeo;
		this.daslHmet = daslHmet;
		this.daslGpio = daslGpio;
		this.daslAdc = daslAdc;
		this.daslTemp = daslTemp;
		this.daslType = daslType;
		this.daslXml = daslXml;
		this.daslDtm = daslDtm;
		this.daslSpsnId = daslSpsnId;
		this.daslVehicle = daslVehicle;
		this.daslDasnId = daslDasnId;
	}

	public long getDaslId() {
		return this.daslId;
	}

	public void setDaslId(long daslId) {
		this.daslId = daslId;
	}

	public long getDaslUid() {
		return this.daslUid;
	}

	public void setDaslUid(long daslUid) {
		this.daslUid = daslUid;
	}

	public Date getDaslDatetime() {
		return this.daslDatetime;
	}

	public void setDaslDatetime(Date daslDatetime) {
		this.daslDatetime = daslDatetime;
	}

	public double getDaslLatitude() {
		return this.daslLatitude;
	}

	public void setDaslLatitude(double daslLatitude) {
		this.daslLatitude = daslLatitude;
	}

	public double getDaslLongitude() {
		return this.daslLongitude;
	}

	public void setDaslLongitude(double daslLongitude) {
		this.daslLongitude = daslLongitude;
	}

	public long getDaslStatus() {
		return this.daslStatus;
	}

	public void setDaslStatus(long daslStatus) {
		this.daslStatus = daslStatus;
	}

	public Long getDaslSatUsed() {
		return this.daslSatUsed;
	}

	public void setDaslSatUsed(Long daslSatUsed) {
		this.daslSatUsed = daslSatUsed;
	}

	public Long getDaslZoneAlarm() {
		return this.daslZoneAlarm;
	}

	public void setDaslZoneAlarm(Long daslZoneAlarm) {
		this.daslZoneAlarm = daslZoneAlarm;
	}

	public Long getDaslMacroId() {
		return this.daslMacroId;
	}

	public void setDaslMacroId(Long daslMacroId) {
		this.daslMacroId = daslMacroId;
	}

	public Long getDaslMacroSrc() {
		return this.daslMacroSrc;
	}

	public void setDaslMacroSrc(Long daslMacroSrc) {
		this.daslMacroSrc = daslMacroSrc;
	}

	public Double getDaslSog() {
		return this.daslSog;
	}

	public void setDaslSog(Double daslSog) {
		this.daslSog = daslSog;
	}

	public Double getDaslCourse() {
		return this.daslCourse;
	}

	public void setDaslCourse(Double daslCourse) {
		this.daslCourse = daslCourse;
	}

	public Double getDaslHdop() {
		return this.daslHdop;
	}

	public void setDaslHdop(Double daslHdop) {
		this.daslHdop = daslHdop;
	}

	public Double getDaslHgeo() {
		return this.daslHgeo;
	}

	public void setDaslHgeo(Double daslHgeo) {
		this.daslHgeo = daslHgeo;
	}

	public Double getDaslHmet() {
		return this.daslHmet;
	}

	public void setDaslHmet(Double daslHmet) {
		this.daslHmet = daslHmet;
	}

	public Long getDaslGpio() {
		return this.daslGpio;
	}

	public void setDaslGpio(Long daslGpio) {
		this.daslGpio = daslGpio;
	}

	public Long getDaslAdc() {
		return this.daslAdc;
	}

	public void setDaslAdc(Long daslAdc) {
		this.daslAdc = daslAdc;
	}

	public Double getDaslTemp() {
		return this.daslTemp;
	}

	public void setDaslTemp(Double daslTemp) {
		this.daslTemp = daslTemp;
	}

	public long getDaslType() {
		return this.daslType;
	}

	public void setDaslType(long daslType) {
		this.daslType = daslType;
	}

	public String getDaslXml() {
		return this.daslXml;
	}

	public void setDaslXml(String daslXml) {
		this.daslXml = daslXml;
	}

	public Date getDaslDtm() {
		return this.daslDtm;
	}

	public void setDaslDtm(Date daslDtm) {
		this.daslDtm = daslDtm;
	}

	public long getDaslSpsnId() {
		return this.daslSpsnId;
	}

	public void setDaslSpsnId(long daslSpsnId) {
		this.daslSpsnId = daslSpsnId;
	}

	public String getDaslVehicle() {
		return this.daslVehicle;
	}

	public void setDaslVehicle(String daslVehicle) {
		this.daslVehicle = daslVehicle;
	}

	public long getDaslDasnId() {
		return this.daslDasnId;
	}

	public void setDaslDasnId(long daslDasnId) {
		this.daslDasnId = daslDasnId;
	}

	public void setDaslObjectInfo(String daslObjectInfo) {
		this.daslObjectInfo = daslObjectInfo;
	}

	public String getDaslObjectInfo() {
		return daslObjectInfo;
	}
	
	public void setDaslObjectName(String daslObjectName) {
		this.daslObjectName = daslObjectName;
	}

	public String getDaslObjectName() {
		return daslObjectName;
	}

	public String getDaslAddress() {
		return daslAddress;
	}

	public void setDaslAddress(String daslAddress) {
		this.daslAddress = daslAddress;
	}
	
	public void setDaslIgnition(String daslIgnition) {
		this.daslIgnition = daslIgnition;
	}	

	public String getDaslIgnition() {
		return daslIgnition;
	}

	public String getDaslFuel() {
		return daslFuel;
	}

	public void setDaslFuel(String daslFuel) {
		this.daslFuel = daslFuel;
	}	

}
