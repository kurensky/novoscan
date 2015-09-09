package ru.novaris.novoscan.client.resources;

import com.google.gwt.event.shared.GwtEvent;

public class LastDataEvent extends GwtEvent<LastDataEventHandler> {
	public static Type<LastDataEventHandler> TYPE = new Type<LastDataEventHandler>();
	@Override
	public Type<LastDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LastDataEventHandler handler) {
		handler.apply(this);
		
	}

}