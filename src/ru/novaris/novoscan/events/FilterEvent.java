package ru.novaris.novoscan.events;

import com.google.gwt.event.shared.GwtEvent;

public class FilterEvent extends GwtEvent<FilterEventHandler> {
	public static Type<FilterEventHandler> TYPE = new Type<FilterEventHandler>();
	@Override
	public Type<FilterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FilterEventHandler handler) {
		handler.apply(this);
		
	}

}
