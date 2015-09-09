package ru.novaris.novoscan.events;

import com.google.gwt.event.shared.GwtEvent;

public class ReportSelectEvent extends GwtEvent<ReportSelectEventHandler> {
	public static Type<ReportSelectEventHandler> TYPE = new Type<ReportSelectEventHandler>();
	@Override
	public Type<ReportSelectEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ReportSelectEventHandler handler) {
		handler.apply(this);
		
	}

}
