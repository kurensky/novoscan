package ru.novaris.novoscan.client.resources;


import java.util.Date;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.datepicker.client.DateBox;

public class DateTimeBox extends FlexTable implements ImplConstantsGWT {
	private ListBox hoursBox;
	private ListBox minutesBox;
	private ListBox secondsBox;
	
	private DateBox dateBox;
	private Date date;
	
	private FlexTable flexTable;

	@SuppressWarnings("deprecation")
	public DateTimeBox(Date date) {
		super();
		this.date = date;
		dateBox = new DateBox();
		dateBox.setFormat(new DateBox.DefaultFormat(DATE_FORMAT));
		hoursBox = new ListBox(); 
		minutesBox  = new ListBox(); 
		secondsBox  = new ListBox();
		flexTable = new FlexTable();
		listBox(hoursBox, 0, 23, date.getHours());
		listBox(minutesBox, 0, 59, date.getMinutes());
		listBox(secondsBox, 0, 59, date.getSeconds());
		dateBox.setValue(date);
		dateBox.setWidth("80");
	    flexTable.setCellSpacing(2);
	    flexTable.setCellPadding(0);
	    FlexCellFormatter cellFormatter = flexTable.getFlexCellFormatter();
	    cellFormatter.setHorizontalAlignment(1, 4, HasHorizontalAlignment.ALIGN_LEFT);
	    flexTable.setWidget(0, 0, dateBox);
	    flexTable.setWidget(0, 1, hoursBox);
	    flexTable.setWidget(0, 2, minutesBox);
	    flexTable.setWidget(0, 3, secondsBox);
	}
	
	public FlexTable getDateTimeBox() {
		return flexTable;
	}
	

	
	@SuppressWarnings("deprecation")
	public Date getDate() {
		if(dateBox.getValue() != null) {
			date = dateBox.getValue();		
			date.setHours(getHour());
			date.setMinutes(getMinute());
			date.setSeconds(getSecond());
	    }	
		return this.date;
	}
	public int getHour() {
		return Integer.parseInt(hoursBox.getItemText(hoursBox.getSelectedIndex()));
	}
	public int getMinute() {
		return Integer.parseInt(minutesBox.getItemText(minutesBox.getSelectedIndex()));
	}
	public int getSecond() {
		return Integer.parseInt(secondsBox.getItemText(secondsBox.getSelectedIndex()));
	}
	
	private void listBox(ListBox listBox, int i, int j, int l) {
		for (int k = i; k <= j; k++) {
			listBox.addItem(String.valueOf(k));
		}
		listBox.setSelectedIndex(l);
	}

	@SuppressWarnings("deprecation")
	public String getText() {
		if(dateBox.getValue() != null) {
			date = dateBox.getValue();		
			date.setHours(getHour());
			date.setMinutes(getMinute());
			date.setSeconds(getSecond());
	    }		
		return DATE_TIME_FORMAT.format(date);
	}

	@Override
	public String getText(int row, int col) {
		return getText();
	}

}
