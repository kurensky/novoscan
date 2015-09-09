package ru.novaris.novoscan.domain;

import java.util.Date;

public class Profiles implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private long profId;
	private long profAcctId;
	private long profAttrId;
	private String profAttrName;
	private String profValuev;
	private float profValuen;
	private Date profValued;
	private String profXml;
	private String profAttrType;
	
	private Date profDate;
	public Profiles() {
	}
	
	public Profiles(long profId,long profAcctId, String profAttrName, String profValuev, Date profDate) {
		this.profId = profId;
		this.profAcctId = profAcctId;
		this.profAttrName = profAttrName;
		this.profValuev = profValuev;
		this.profDate = profDate;
	}
	public Profiles(long profId,long profAcctId, String profAttrName, float profValuen, Date profDate) {
		this.profId = profId;
		this.profAcctId = profAcctId;
		this.profAttrName = profAttrName;
		this.profValuen = profValuen;
		this.profDate = profDate;
	}
	public Profiles(long profId,long profAcctId, String profAttrName, Date profValued, Date profDate) {
		this.profId = profId;
		this.profAcctId = profAcctId;
		this.profAttrName = profAttrName;
		this.profValued = profValued;
		this.profDate = profDate;
	}

	public Profiles(long profId,long profAcctId, String profAttrName, String profValuev) {
		this.profId = profId;
		this.profAcctId = profAcctId;
		this.profAttrName = profAttrName;
		this.profValuev = profValuev;
	}
	public Profiles(long profId,long profAcctId, String profAttrName, float profValuen) {
		this.profId = profId;
		this.profAcctId = profAcctId;
		this.profAttrName = profAttrName;
		this.profValuen = profValuen;
	}
	public Profiles(long profId,long profAcctId, String profAttrName, Date profValued) {
		this.profId = profId;
		this.profAcctId = profAcctId;
		this.profAttrName = profAttrName;
		this.profValued = profValued;
	}

	public Profiles(long profId, long profAcctId, String profAttrName, String profAttrType, String profXml) {
		this.profId = profId;
		this.profAcctId = profAcctId;
		this.profAttrName = profAttrName;
		this.profXml = profXml;
	}
	
	public long getProfId() {
		return profId;
	}
	public void setProfId(long profId) {
		this.profId = profId;
	}
	public String getProfAttrName() {
		return profAttrName;
	}
	public void setProfAttrName(String profAttrName) {
		this.profAttrName = profAttrName;
	}
	public String getProfValuev() {
		return profValuev;
	}
	public void setProfValuev(String profValuev) {
		this.profValuev = profValuev;
	}
	public long getProfAcctId() {
		return profAcctId;
	}
	public void setProfAcctId(long profAcctId) {
		this.profAcctId = profAcctId;
	}
	public Date getProfDate() {
		return profDate;
	}
	public void setProfDate(Date profDate) {
		this.profDate = profDate;
	}

	public long getProfAttrId() {
		return profAttrId;
	}

	public void setProfAttrId(long profAttrId) {
		this.profAttrId = profAttrId;
	}

	public float getProfValuen() {
		return profValuen;
	}

	public void setProfValuen(float profValuen) {
		this.profValuen = profValuen;
	}

	public Date getProfValued() {
		return profValued;
	}

	public void setProfValued(Date profValued) {
		this.profValued = profValued;
	}

	public String getProfAttrType() {
		return profAttrType;
	}

	public void setProfAttrType(String profAttrType) {
		this.profAttrType = profAttrType;
	}

	public String getProfXml() {
		return this.profXml;
	}

	public void setProfXml(String profXml) {
		this.profXml = profXml;
	}

}
