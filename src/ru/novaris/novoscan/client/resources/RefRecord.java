package ru.novaris.novoscan.client.resources;


public class RefRecord implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String key;
	private String value;
	public RefRecord() {
		
	}

	public RefRecord(String key, String value) {
		this.key = key;
		this.value = value;
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}


}
