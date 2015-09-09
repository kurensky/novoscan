package ru.novaris.novoscan.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

public class RotateImage {
	private final String imageBase = GWT.getHostPageBaseURL();
	private StringBuffer imageUrl;
	public RotateImage() {
    }
	public void setImageUrl(String imageFile, Integer rotateDegree) {
		imageUrl = new StringBuffer();
		imageUrl.append(imageBase);
		imageUrl.append("imageServlet?file=");
		imageUrl.append(imageFile + ".png");
		imageUrl.append("&degree=" + rotateDegree);
	}
	public Image getImage() {
		Image image = new Image(imageUrl.toString());
		return image;
	}
	public String getUrl() {
		return imageUrl.toString();
	}
}
