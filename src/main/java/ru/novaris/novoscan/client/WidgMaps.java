/**
 * 
 */
package ru.novaris.novoscan.client;

import ru.novaris.novoscan.client.resources.ImplConstantsGWT;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author kur Отображение карт
 */
public class WidgMaps extends VerticalPanel implements ImplConstantsGWT {

	private final MapOpenStreet omap;


	public MapOpenStreet getOmap() {
		return omap;
	}
	
	public WidgMaps(Novoscan entryPoint) {
    	omap = new MapOpenStreet(this, entryPoint);
	}

	public void stopRefreshTimer() {
		omap.stopRefreshTimer();
	}


	public void refreshData() {
		omap.refreshData();
	}

	public void addTrack() {
		omap.addTrack();
	}

	public void clearTrack() {
		omap.clearTrack();
	}

	public void deleteInfoPanel() {
		omap.deleteInfoPanel();
	}
	public void showInfoPanel() {
		omap.showInfoPanel();
	}

}
