package ru.novaris.novoscan.domain;

import java.util.Date;

public class Request implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Long reqId;
	
	private Long reqAcctId;

	private Long reqSpobId;
	
	private String reqName;
	
	private String reqDesc;
	
	private Date reqDate;
	
	private Date reqDateModify;
	
	private Date reqDateBegin;
	
	private Date reqDateEnd;

	private Double reqDelta;
	

	public Request() {
	}
	
	public Request
	        (long reqId
	        ,long reqAcctId
	        ,long reqSpobId
	        ,String reqName
			,String reqDesc
			,Date reqDate
			,Date reqDateModify
			,Date reqDateBegin
			,Date reqDateEnd
			,Double reqDelta
			) 
	{
		this.reqId = reqId;
		this.reqAcctId = reqAcctId;
		this.reqSpobId = reqSpobId;
		this.reqName = reqName;
		this.reqDesc = reqDesc;
		this.reqDate = reqDate;
		this.reqDateModify = reqDateModify;
		this.reqDateBegin = reqDateBegin;
		this.reqDateEnd = reqDateEnd;
		this.reqDelta = reqDelta;
	}

	public Long getReqId() {
		return reqId;
	}

	public void setReqId(Long reqId) {
		this.reqId = reqId;
	}

	public Long getReqAcctId() {
		return reqAcctId;
	}

	public void setReqAcctId(Long reqAcctId) {
		this.reqAcctId = reqAcctId;
	}

	public Long getReqSpobId() {
		return reqSpobId;
	}

	public void setReqSpobId(Long reqSpobId) {
		this.reqSpobId = reqSpobId;
	}

	public String getReqName() {
		return reqName;
	}

	public void setReqName(String reqName) {
		this.reqName = reqName;
	}

	public String getReqDesc() {
		return reqDesc;
	}

	public void setReqDesc(String reqDesc) {
		this.reqDesc = reqDesc;
	}

	public Date getReqDate() {
		return reqDate;
	}

	public void setReqDate(Date reqDate) {
		this.reqDate = reqDate;
	}

	public Date getReqDateModify() {
		return reqDateModify;
	}

	public void setReqDateModify(Date reqDateModify) {
		this.reqDateModify = reqDateModify;
	}

	public Date getReqDateBegin() {
		return reqDateBegin;
	}

	public void setReqDateBegin(Date reqDateBegin) {
		this.reqDateBegin = reqDateBegin;
	}

	public Date getReqDateEnd() {
		return reqDateEnd;
	}

	public void setReqDateEnd(Date reqDateEnd) {
		this.reqDateEnd = reqDateEnd;
	}

	public Double getReqDelta() {
		return reqDelta;
	}

	public void setReqDelta(Double reqDelta) {
		this.reqDelta = reqDelta;
	}

}
