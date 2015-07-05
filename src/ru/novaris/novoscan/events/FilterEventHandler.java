package ru.novaris.novoscan.events;

import com.google.gwt.event.shared.EventHandler;

public interface FilterEventHandler extends EventHandler {
    void apply(FilterEvent filterEvent);
}
