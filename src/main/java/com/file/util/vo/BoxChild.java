package com.file.util.vo;

import java.util.ArrayList;
import java.util.List;

public class BoxChild {

	private List<String> params;

	private List<SqlComand> sqlComands = new ArrayList<>();

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public List<SqlComand> getSqlComands() {
		return sqlComands;
	}

	public void setSqlComands(List<SqlComand> sqlComands) {
		this.sqlComands = sqlComands;
	}

}
