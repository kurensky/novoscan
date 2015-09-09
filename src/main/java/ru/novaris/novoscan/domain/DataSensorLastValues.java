package ru.novaris.novoscan.domain;

import java.sql.Date;

public class DataSensorLastValues implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long dalvId;

	private long dalvDaslId;

	private long dalvSpmdId;

	private String dalvKey;

	private Date dalvDatetime;

	private float dalvValue;

	public DataSensorLastValues() {
	}

	public DataSensorLastValues(long dalvId, long dalvSpmdId, long dalvDaslId,
			String dalvKey, float dalvValue, Date dalvDatetime) {
		this.dalvId = dalvId;
		this.dalvDaslId = dalvDaslId;
		this.dalvKey = dalvKey;
		this.dalvDatetime = dalvDatetime;
		this.dalvValue = dalvValue;
		this.dalvSpmdId = dalvSpmdId;
	}

	public DataSensorLastValues(long dalvId, long dalvSpmdId, String dalvKey,
			float dalvValue, Date dalvDatetime) {
		this.dalvId = dalvId;
		this.dalvKey = dalvKey;
		this.dalvDatetime = dalvDatetime;
		this.dalvValue = dalvValue;
		this.dalvSpmdId = dalvSpmdId;
	}

	public long getDalvId() {
		return dalvId;
	}

	public void setDalvId(long dalvId) {
		this.dalvId = dalvId;
	}

	public long getDalvDaslId() {
		return dalvDaslId;
	}

	public void setDalvDaslId(long dalvDaslId) {
		this.dalvDaslId = dalvDaslId;
	}

	public String getdalvKey() {
		return dalvKey;
	}

	public void setDalvKey(String dalvKey) {
		this.dalvKey = dalvKey;
	}

	public float getDalvValue() {
		return dalvValue;
	}

	public void setDalvValue(float dalvValue) {
		this.dalvValue = dalvValue;
	}

	public Date getDalvDatetime() {
		return dalvDatetime;
	}

	public void setDalvDatetime(Date dalvDatetime) {
		this.dalvDatetime = dalvDatetime;
	}

	public long getDalvSpmdId() {
		return dalvSpmdId;
	}

	public void setDalvSpmdId(long dalvSpmdId) {
		this.dalvSpmdId = dalvSpmdId;
	}

}
