package ru.novaris.novoscan.client.resources;

import com.google.gwt.event.shared.GwtEvent;

public class CheckSessionEvent extends GwtEvent<CheckSessionEventHandler> {
	public static Type<CheckSessionEventHandler> TYPE = new Type<CheckSessionEventHandler>();
	@Override
	public Type<CheckSessionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CheckSessionEventHandler handler) {
		handler.apply(this);
		
	}

}