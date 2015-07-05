package ru.novaris.novoscan.domain;

import java.util.List;

public class FilterTable implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String filterTable;
	private int fieldId;
	private String fieldKey;
	private String fieldName;
	private String fieldType;
	private List<String> fieldExpression;
	private String fieldValue1;
	private String fieldValue2;
	private int nextId = 0;
	private String expression = "=";
	private String fieldLogical;
	
	public FilterTable() {
	}
	
	public FilterTable(String filterTable) {
		this.filterTable  = filterTable;
		getNext();
	}

	public FilterTable (String filterTable, int nextId) {
		this.nextId = nextId;
		this.filterTable  = filterTable;
	}
	
	public void setFilterTable (String filterTable) {
		this.filterTable  = filterTable;
	}
	


	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public List<String> getFieldExpression() {
		return fieldExpression;
	}
	public void setFieldExpression(List<String> fieldExpression) {
		this.fieldExpression = fieldExpression;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getFieldValue1() {
		return fieldValue1;
	}
	public void setFieldValue1(String fieldValue1) {
		this.fieldValue1 = fieldValue1;
	}
	public String getFieldValue2() {
		return fieldValue2;
	}
	public void setFieldValue2(String fieldValue2) {
		this.fieldValue2 = fieldValue2;
	}
	public int getFieldId() {
		return fieldId;
	}
	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldKey() {
		return fieldKey;
	}
	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}


	public void getNext() {
		this.fieldId = nextId;
		nextId++;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFilterTable() {
		return filterTable;
	}
	
	public String getFieldLogical() {
		return fieldLogical;
	}
	public void setFieldLogical(String fieldLogical) {
		this.fieldLogical = fieldLogical;
	}
	

}
