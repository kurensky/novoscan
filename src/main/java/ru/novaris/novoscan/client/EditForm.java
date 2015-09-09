package ru.novaris.novoscan.client;

import ru.novaris.novoscan.client.resources.ImplConstantsGWT;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditForm extends DialogBox implements ImplConstantsGWT {
	private final Button buttonOK = new Button(constants.Apply());;
	private final Button buttonCancel = new Button(constants.Cancel());
	private final Grid gridEdit = new Grid();
	private final Label infoLabel = new Label();
	private final TextArea extInfo = new TextArea();
	private final FlexTable flexTableEdit = new FlexTable();
	public VerticalPanel extendVerticalPanel = new VerticalPanel();
	private String action = "";

	public EditForm(Novoscan entryPoint) {
		VerticalPanel mainVerticalPanel = new VerticalPanel();
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		extendVerticalPanel.add(infoLabel);
		extendVerticalPanel.add(extInfo);
		infoLabel.setVisible(false);
		extInfo.setVisible(false);
		horizontalPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		buttonOK.setWidth("100px");
		buttonCancel.setWidth("100px");
		horizontalPanel.add(buttonOK);
		horizontalPanel.add(buttonCancel);
		flexTableEdit.setCellPadding(5);
		mainVerticalPanel.add(flexTableEdit);
		mainVerticalPanel.add(extendVerticalPanel);
		mainVerticalPanel.add(horizontalPanel);
		mainVerticalPanel.setCellHorizontalAlignment(horizontalPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		this.add(mainVerticalPanel);
		this.setGlassEnabled(true);
		this.setAnimationEnabled(false);
	}

	// -------------------------------------------------
	public String getAction() {
		return action;
	}

	// ****************************************************
	public void setAction(String action) {
		this.action = action;

	}

	// ***************************************************
	public void setExt(String label) {
		infoLabel.setVisible(true);
		extInfo.setVisible(true);
		infoLabel.setText(label);

	}

	// ****************************************************
	public void setVisible(int num, boolean Visible) {
		flexTableEdit.getRowFormatter().setVisible(num, Visible);
	}

	// *****************************************************
	public void addRow(String name, Widget source) {
		int row = flexTableEdit.getRowCount();
		flexTableEdit.setText(row, 0, name);
		flexTableEdit.setWidget(row, 1, source);
		flexTableEdit.getCellFormatter().setHorizontalAlignment(row, 0,
				HasHorizontalAlignment.ALIGN_RIGHT);
		flexTableEdit.getCellFormatter().setHorizontalAlignment(row, 1,
				HasHorizontalAlignment.ALIGN_LEFT);

	}


	public Grid getGridEdit() {
		return gridEdit;
	}

}
