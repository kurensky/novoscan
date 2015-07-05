package ru.novaris.novoscan.client.resources;

import com.google.gwt.event.shared.EventHandler;

public interface CheckSessionEventHandler  extends EventHandler {
    void apply(CheckSessionEvent checkSessionEvent);
}
