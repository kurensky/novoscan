package ru.novaris.novoscan.client;

import ru.novaris.novoscan.client.resources.ImplConstantsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Image;

public class DbMessage extends DialogBox implements ImplConstantsGWT {
	private static DbMessageUiBinder uiBinder = GWT
			.create(DbMessageUiBinder.class);

	interface DbMessageUiBinder extends UiBinder<Widget, DbMessage> {
	}
	@UiField
	PushButton apply;

	@UiField DockPanel panel;
	@UiField TextArea textArea;
	@UiField Image imageMessage;
	
	private MessageType messageType;
	
	public DbMessage(MessageType messageType, String messageInfo) {
		this.messageType = messageType;
		setWidget(uiBinder.createAndBindUi(this));
		initMessage();
		textArea.setText(messageInfo);
		this.setModal(true);
		this.setAnimationEnabled(true);
		//this.setGlassEnabled(true);
		this.center();
		this.show();
	}

	private void initMessage() {
		apply.setText(constants.Apply());
		if(messageType.equals(MessageType.ERROR)) {
			imageMessage.setResource(images.error());
		} else if (messageType.equals(MessageType.INFO)) {
			imageMessage.setResource(images.information());
		} else if (messageType.equals(MessageType.WARNING)) {
			imageMessage.setResource(images.warning());
		} else {
			imageMessage.setResource(images.information());
		}
		
	}

	@UiHandler("apply")
	void onApplyClick(ClickEvent event) {
		this.removeFromParent();
	}

}
