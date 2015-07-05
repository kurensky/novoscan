package ru.novaris.novoscan.sensors;

public class SensorData {
	private int terminalType;
	private String sensorsData;
	public SensorData() {
		
	}
	public void setTerminalType(int terminalType) {
		this.terminalType = terminalType;
	}
	public int getTerminalType() {
		return terminalType;
	}
	public void setSensorsData(String sensorsData) {
		this.sensorsData = sensorsData;
	}
	public String getSensorsData() {
		return this.sensorsData;
	}

}
