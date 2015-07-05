package ru.novaris.novoscan.client.resources;


public interface ImplFilters {	
	
	public String getTableName();
	
	public void initDataProvider();
	
	public void refreshData();
	
	public void addHandler();
	
	public void initTable();
	
	public void initFilters();
	
	public void initAdd();
	
	public void initButtons();
	
}
