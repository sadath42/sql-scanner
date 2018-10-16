package com.file.util.vo;

import java.util.ArrayList;
import java.util.List;

public class KshChild {
	String name;

	private List<SqlComand> sqlComands = new ArrayList<>();
	private List<String> params = new ArrayList<>();

	public KshChild(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
