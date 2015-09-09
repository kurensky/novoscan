package ru.novaris.novoscan.client;

import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;

import com.google.gwt.user.client.Random;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;

import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.TripleDesCipher;
import com.google.gwt.user.client.Cookies;

public class CwLogonEntry extends DialogBox implements HasText, ImplConstants,
		ImplConstantsGWT {

	private final Novoscan novoscanEntry;

	private boolean hadCookie = false;

	private LogonCheck logonCheck;

	private String resultInfo = null;

	private final int cryptMod = 4;

	private final int cryptPad = 2;

	private static CwLogonEntryUB uiBinder = GWT.create(CwLogonEntryUB.class);

	interface CwLogonEntryUB extends UiBinder<Widget, CwLogonEntry> {
	};

	public CwLogonEntry(Novoscan entryPoint) {
		novoscanEntry = entryPoint;
		logonCheck = LogonCheck.UNKNOWN;
		setWidget(uiBinder.createAndBindUi(this));
		this.center();
		this.setTitle(constants.Authorization());
		this.setText(constants.Authorization());
		busyImage.setVisible(false);
		if (Cookies.getCookie(COOKIE_TAG_NAME) != null && !Cookies.getCookie(COOKIE_TAG_NAME).isEmpty()) {
			logonUserName.setText(Cookies.getCookie(COOKIE_TAG_NAME));
		}
		if (Cookies.getCookie(COOKIE_TAG_NAME) != null 
			&& !Cookies.getCookie(COOKIE_TAG_NAME).isEmpty()
			&& Cookies.getCookie(COOKIE_TAG_PASSWORD) != null
			&& !Cookies.getCookie(COOKIE_TAG_PASSWORD).isEmpty()) {
			hadCookie = true;
			logonPassword.setText(encrypt(Cookies
					.getCookie(COOKIE_TAG_PASSWORD)));
			checkCredentials(rpcObject);
			if (logonCheck == LogonCheck.ACCEPTED) {
				logonRememberCheck.setValue(true);
				viewMainPage();
			} else {
				setLogonPage();
			}

		} else {
			logonRememberCheck.setValue(false);
		}
	}

	// -------------------------------------------------------------
	private void setLogonPage() {
		if (logonCheck == LogonCheck.FAIL) {
			setText(constants.Authorization() + " : " + constants.ServerError());
			novLogoImg.setResource(images.denied());

		} else if (logonCheck == LogonCheck.DENIED) {
			novLogoImg.setResource(images.denied());
		}
		logonUserName.setFocus(true);
		busyImage.setVisible(false);
		this.show();
	}

	private void viewMainPage() {
		Cookies.setCookie(COOKIE_TAG_NAME, logonUserName.getText(), null, null,
				"/", false);
		Cookies.setCookie(COOKIE_TAG_PASSWORD,
				decrypt(logonPassword.getText()), null, null, "/", false);
		this.setText(constants.Authorization());
		logonUserName.setText(null);
		logonPassword.setText(null);
		novLogoImg.setResource(images.logo());
		busyImage.setVisible(false);
		this.removeFromParent();
		novoscanEntry.viewMain();
	}

	// -------------------------------------------------------------
	private void checkCredentials(final DatabaseReadAsync object) {

		AsyncCallback<Long> callback = new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				logonCheck = LogonCheck.FAIL;
				setLogonPage();
			}

			@Override
			public void onSuccess(Long result) {
				if (result != null && result >= 0) {
					logonCheck = LogonCheck.ACCEPTED;
					Cookies.setCookie(COOKIE_TAG_ID, String.valueOf(result),
							null, null, "/", false);
					if (logonRememberCheck.getValue() && hadCookie) {
						// TODO
					}
					viewMainPage();
				} else {
					logonCheck = LogonCheck.DENIED;
					getResultInfo(object);
					setText(constants.Authorization() + " : " + result + ". "
							+ resultInfo);
					setLogonPage();
				}
			}
		};
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey(GWT_DES_KEY);
		try {
			String encryptPassword = cipher.encrypt(logonPassword.getText());
			String encryptUsername = cipher.encrypt(logonUserName.getText());
			logonCheck = LogonCheck.UNKNOWN;
			object.checkConnect(encryptUsername, encryptPassword, callback);

		} catch (DataLengthException e1) {
			setLogonPage();
		} catch (IllegalStateException e1) {
			setLogonPage();
		} catch (InvalidCipherTextException e1) {
			setLogonPage();
		}
	}

	@UiField
	TextBox logonUserName;

	@UiHandler("logonUserName")
	void onKeyUserName(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			busyImage.setVisible(true);
			checkCredentials(rpcObject);
		}
	}

	@UiField
	PasswordTextBox logonPassword;

	@UiHandler("logonPassword")
	void onKeyPassword(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			busyImage.setVisible(true);
			checkCredentials(rpcObject);
		}
	}

	@UiField
	Button logonSubmit;

	@UiHandler("logonSubmit")
	void onClickSubmit(ClickEvent e) {
		busyImage.setVisible(true);
		checkCredentials(rpcObject);
	}

	@UiField
	Button logonCancel;

	@UiHandler("logonCancel")
	void onClickCancel(ClickEvent e) {
		logonUserName.setText(null);
		logonPassword.setText(null);
		this.setText(constants.Authorization());
		novLogoImg.setResource(images.logo());
	}

	@UiField
	CheckBox logonRememberCheck;

	@UiField
	Image novLogoImg;

	@UiField
	Image busyImage;

	public void getResultInfo(final DatabaseReadAsync object) {
		AsyncCallback<String> callback = new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				new DbMessage(MessageType.ERROR, constants.ErrorGetResult());
			}

			@Override
			public void onSuccess(String result) {
				resultInfo = result;
			}
		};
		object.getResultINFO(callback);
	};

	private String encrypt(String decryptString) {

		StringBuffer cryptString = new StringBuffer();
		try {
			int inL = decryptString.length();
			inL = inL
					- (decryptString.charAt(inL - 1) % cryptMod + cryptPad + 1);
			char cryptChar;
			while (inL > 0) {
				cryptChar = decryptString.charAt(inL - 1);
				cryptString.append(cryptChar);
				inL = inL - (cryptChar % cryptMod + cryptPad + 1);
			}
			return cryptString.toString();
		} catch (Exception e) {
			return null;
		}
	}

	private String decrypt(String cryptString) {
		final int cryptBase = 65;
		final int cryptLimits = 122;
		int inL = cryptString.length();
		StringBuffer decryptString = new StringBuffer();
		int bL;
		while (inL > 0) {
			inL--;
			bL = cryptString.charAt(inL) % cryptMod + cryptPad;
			for (int i = 0; i < bL; i++) {
				decryptString
						.append((char) (cryptBase + (int) (cryptLimits - cryptBase)
								* Random.nextDouble()));
			}
			decryptString.append(cryptString.charAt(inL));
		} // while
		char lastChar = (char) (cryptBase + (int) (cryptLimits - cryptBase)
				* Random.nextDouble());
		bL = lastChar % cryptMod + cryptPad;
		;
		for (int i = 0; i < bL; i++) {
			decryptString
					.append((char) (cryptBase + (int) (cryptLimits - cryptBase)
							* Random.nextDouble()));
		}
		decryptString.append(lastChar);
		return decryptString.toString();
	}
}
