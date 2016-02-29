package ru.novaris.novoscan.client;

import ru.novaris.novoscan.client.resources.ImplConstants;
import ru.novaris.novoscan.client.resources.ImplConstantsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.TripleDesCipher;
import com.google.gwt.user.client.Cookies;

public class CwLogonEntry extends Composite implements HasText, ImplConstants,
		ImplConstantsGWT {

	private final Novoscan entryPoint;

	private boolean hadCookie = false;

	private LogonCheck check;

	private String resultInfo;

	private final int cryptMod = 4;

	private final int cryptPad = 2;
	

	private static CwLogonEntryUiBinder uiBinder = GWT.create(CwLogonEntryUiBinder.class);

	interface CwLogonEntryUiBinder extends UiBinder<Widget, CwLogonEntry> {
	};

	public CwLogonEntry(Novoscan entryPoint) {
		this.entryPoint = entryPoint;
		check = LogonCheck.UNKNOWN;
		initWidget(uiBinder.createAndBindUi(this));
		checkPanel.setStylePrimaryName("gwt-Login-Label");
		panel.getElement().setAttribute("type", "login");
		username.getElement().setAttribute("type", "username");
		username.addAttachHandler(new Handler() {
		    @Override
		    public void onAttachOrDetach(AttachEvent event) {
		        if (event.isAttached()) {
		        	username.getElement().setAttribute("placeholder", constants.Username());
		        }
		    }
		});
		password.getElement().setAttribute("type", "password");
		password.addAttachHandler(new Handler() {
		    @Override
		    public void onAttachOrDetach(AttachEvent event) {
		        if (event.isAttached()) {
		        	password.getElement().setAttribute("placeholder", constants.Password());
		        }
		    }
		});
		
		this.setTitle(constants.Authorization());
		this.setText(constants.Authorization());
		head.setText(constants.Authorization());
		errorLabel.setVisible(false);
		submit.setStylePrimaryName("gwt-Login-Button");
		submit.setTitle(constants.Login());
		if (Cookies.getCookie(COOKIE_TAG_NAME) != null && !Cookies.getCookie(COOKIE_TAG_NAME).isEmpty()) {
			username.setText(Cookies.getCookie(COOKIE_TAG_NAME));
		}
		if (Cookies.getCookie(COOKIE_TAG_NAME) != null 
			&& !Cookies.getCookie(COOKIE_TAG_NAME).isEmpty()
			&& Cookies.getCookie(COOKIE_TAG_PASSWORD) != null
			&& !Cookies.getCookie(COOKIE_TAG_PASSWORD).isEmpty()) {
			hadCookie = true;
			password.setText(encrypt(Cookies
					.getCookie(COOKIE_TAG_PASSWORD)));
			checkCredentials(rpcObject);
			if (check == LogonCheck.ACCEPTED) {
				errorLabel.setVisible(false);
				viewMainPage();
			} else {
				setLogonPage();
			}

		} else {
			remember.setValue(false);
		}
	}

	// -------------------------------------------------------------
	private void setLogonPage() {
		if (check == LogonCheck.FAIL) {
			setError(constants.Authorization() + " : " + constants.ServerError());

		} else if (check == LogonCheck.DENIED) {
			setError(constants.AccessDenied());
		}
		username.setFocus(true);
	}

	private void viewMainPage() {
		Cookies.setCookie(COOKIE_TAG_NAME, username.getText(), null, null,
				"/", false);
		Cookies.setCookie(COOKIE_TAG_PASSWORD,
				decrypt(password.getText()), null, null, "/", false);
		this.setText(constants.Authorization());
		this.removeFromParent();
		entryPoint.viewMain();
	}

	// -------------------------------------------------------------
	private void checkCredentials(final DatabaseReadAsync object) {

		AsyncCallback<Long> callback = new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				check = LogonCheck.FAIL;
				setLogonPage();
			}

			@Override
			public void onSuccess(Long result) {
				if (result != null && result >= 0) {
					check = LogonCheck.ACCEPTED;
					Cookies.setCookie(COOKIE_TAG_ID, String.valueOf(result),
							null, null, "/", false);
					if (remember.getValue() && hadCookie) {
						// TODO
					}
					viewMainPage();
				} else {
					check = LogonCheck.DENIED;
					getResultInfo(object);
					setError(constants.Authorization() + " : " + result + ". "
							+ resultInfo);
					setLogonPage();
				}
			}
		};
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey(GWT_DES_KEY);
		try {
			String encryptPassword = cipher.encrypt(password.getText());
			String encryptUsername = cipher.encrypt(username.getText());
			check = LogonCheck.UNKNOWN;
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
	SuggestBox username;
	
	@UiField
	Label head;
	
	@UiField
	Label errorLabel;
	
	@UiField
	VerticalPanel panel;
	
	@UiField
	HorizontalPanel	checkPanel;
	
	@UiHandler("username")
	void onKeyUserName(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			checkCredentials(rpcObject);
		}
	}

	@UiField
	PasswordTextBox password;

	@UiHandler("password")
	void onKeyPassword(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			checkCredentials(rpcObject);
		}
	}

	@UiField
	Button submit;

	@UiHandler("submit")
	void onClickSubmit(ClickEvent e) {
		checkCredentials(rpcObject);
	}

	@UiField
	CheckBox remember;

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

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return errorLabel.getText();
	}

	@Override
	public void setText(String text) {
		errorLabel.setVisible(true);
		errorLabel.setText(text);
	}
	
	public void setError(String text) {
		setText(text);
		errorLabel.addStyleName("redText");
	}
	
	
}
