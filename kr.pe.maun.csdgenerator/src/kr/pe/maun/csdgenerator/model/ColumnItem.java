package kr.pe.maun.csdgenerator.model;

import java.io.Serializable;

public class ColumnItem implements Serializable {

	private static final long serialVersionUID = 6875884070980161630L;

	String columnName;
	String dataType;
	String comments;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
