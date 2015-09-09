package ru.novaris.novoscan.domain;

import java.math.BigDecimal;
import java.util.Date;

public class RequestRoute implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Long reqrId;

	private Long reqrReqId;

	private Long reqrReqrId;

	private String reqrPointName;

	private Date reqrDateArrival;

	private Long reqrTimeStay;

	private BigDecimal reqrDistance;

	private BigDecimal reqrRate;

	public RequestRoute() {
	}

	public RequestRoute(Long reqrId, Long reqrReqId, Long reqrReqrId,
			String reqrPointName, Date reqrDateArrival, Long reqrTimeStay,
			BigDecimal reqrDistance, BigDecimal reqrRate) {
		this.reqrId = reqrId;
		this.reqrReqrId = reqrReqrId;
		this.reqrReqId = reqrReqId;
		this.reqrPointName = reqrPointName;
		this.reqrDateArrival = reqrDateArrival;
		this.reqrTimeStay = reqrTimeStay;
		this.reqrDistance = reqrDistance;
		this.reqrRate = reqrRate;
	}

	public Long getReqrId() {
		return reqrId;
	}

	public void setReqrId(Long reqrId) {
		this.reqrId = reqrId;
	}

	public Long getReqrReqId() {
		return reqrReqId;
	}

	public void setReqrReqId(Long reqrReqId) {
		this.reqrReqId = reqrReqId;
	}

	public Long getReqrReqrId() {
		return reqrReqrId;
	}

	public void setReqrReqrId(Long reqrReqrId) {
		this.reqrReqrId = reqrReqrId;
	}

	public String getReqrPointName() {
		return reqrPointName;
	}

	public void setReqrPointName(String reqrPointName) {
		this.reqrPointName = reqrPointName;
	}

	public Date getReqrDateArrival() {
		return reqrDateArrival;
	}

	public void setReqrDateArrival(Date reqrDateArrival) {
		this.reqrDateArrival = reqrDateArrival;
	}

	public Long getReqrTimeStay() {
		return reqrTimeStay;
	}

	public void setReqrTimeStay(Long reqrTimeStay) {
		this.reqrTimeStay = reqrTimeStay;
	}

	public BigDecimal getReqrDistance() {
		return reqrDistance;
	}

	public void setReqrDistance(BigDecimal reqrDistance) {
		this.reqrDistance = reqrDistance;
	}

	public BigDecimal getReqrRate() {
		return reqrRate;
	}

	public void setReqrRate(BigDecimal reqrRate) {
		this.reqrRate = reqrRate;
	}

}
