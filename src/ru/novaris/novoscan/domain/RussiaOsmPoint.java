package ru.novaris.novoscan.domain;

public class RussiaOsmPoint implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long osmId;
	private String osmName;
	private String osmType;
	public RussiaOsmPoint() {
		
	}
	public Long getOsmId() {
		return osmId;
	}
	public void setOsmId(Long osmId) {
		this.osmId = osmId;
	}
	public String getOsmName() {
		return osmName;
	}
	public void setOsmName(String osmName) {
		this.osmName = osmName;
	}
	public String getOsmType() {
		return osmType;
	}
	public void setOsmType(String osmType) {
		this.osmType = osmType;
	}
	
	

}
