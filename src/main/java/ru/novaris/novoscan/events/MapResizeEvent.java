package ru.novaris.novoscan.events;

import com.google.gwt.event.shared.GwtEvent;

public class MapResizeEvent extends GwtEvent<MapResizeEventHandler> {
	public static Type<MapResizeEventHandler> TYPE = new Type<MapResizeEventHandler>();
	@Override
	public Type<MapResizeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MapResizeEventHandler handler) {
		handler.apply(this);
		
	}

}
