package ru.novaris.novoscan.client.resources;
import com.google.gwt.user.cellview.client.DataGrid;

public 	interface ObjInfoResource extends DataGrid.Resources {
	 @Source({DataGrid.Style.DEFAULT_CSS,"CwObjInfoTable.css"})
	 DataGrid.Style dataGrid();
}
