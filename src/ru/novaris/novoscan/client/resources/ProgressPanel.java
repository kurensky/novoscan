package ru.novaris.novoscan.client.resources;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProgressPanel extends PopupPanel implements ImplConstantsGWT {
	private int left = RootPanel.get(DIV_CONTENT).getOffsetWidth()/2;
	private int top = RootPanel.get(DIV_CONTENT).getOffsetHeight()/2;
	
	public ProgressPanel() {
		this.hide();
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		verticalPanel.add(new Label(constants.Working()));
		verticalPanel.add(new Image("images/load_line1.gif"));
		this.add(verticalPanel);
		this.setPopupPosition(left, top);
		this.isAnimationEnabled();
		this.setModal(true);
	}
	
    public void setCenter(int left, int top) {
    	this.left = left;
    	this.top = top;
    }
	
	public void showProgress() {
		if (!this.isShowing()) {
			this.show();
		}

	}

	public void hideProgress() {
		if (this.isShowing()) {
			this.hide();
		}
	}
}
