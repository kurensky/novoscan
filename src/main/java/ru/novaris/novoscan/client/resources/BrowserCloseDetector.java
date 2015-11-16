package ru.novaris.novoscan.client.resources;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

public class BrowserCloseDetector {
    private static final String COOKIE = "detector";
    private static BrowserCloseDetector instance;

    private BrowserCloseDetector() {
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(Window.ClosingEvent closingEvent) {
                Cookies.setCookie(COOKIE, "");
            }
        });
    }

    public static BrowserCloseDetector get() {
        return (instance == null) ? instance = new BrowserCloseDetector() : instance;
    }

    public boolean wasClosed() {
        return Cookies.getCookie(COOKIE) == null;
    }
}