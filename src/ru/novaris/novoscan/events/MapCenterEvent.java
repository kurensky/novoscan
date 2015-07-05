package ru.novaris.novoscan.events;

import com.google.gwt.event.shared.GwtEvent;

public class MapCenterEvent extends GwtEvent<MapCenterEventHandler> {
	public static Type<MapCenterEventHandler> TYPE = new Type<MapCenterEventHandler>();
	@Override
	public Type<MapCenterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MapCenterEventHandler handler) {
		handler.apply(this);
		
	}

}
