package ru.novaris.novoscan.domain;

import java.util.ArrayList;
import java.util.List;

import ru.novaris.novoscan.client.resources.RefRecord;

public class AddRecord implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String saveTable;
	private int fieldId;
	private String fieldKey;
	private String fieldName;
	private String fieldType;
	private String fieldValue;
	private boolean mandatory = false;
	private boolean editable = true;
	private List<RefRecord> refRecords = new ArrayList<RefRecord>();
	
	public AddRecord() {
	}

	public void setSaveTable (String saveTable) {
		this.saveTable  = saveTable;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
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

	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getSaveTable() {
		return saveTable;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}	
	public boolean isMandatory() {
		return this.mandatory;
	}
	public boolean isEditable() {
		return this.editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public List<RefRecord> getListRefRecord() {
		return refRecords ;
	}
	public void setListRefRecord(List<RefRecord> refRecords) {
		this.refRecords = refRecords;
	}	
}
