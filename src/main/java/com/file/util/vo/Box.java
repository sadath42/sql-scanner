package com.file.util.vo;

import java.util.List;
import java.util.Map;

public class Box {

	private String name;

	private Map<String, BoxChild> scriptAndSqlList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, BoxChild> getScriptAndSqlList() {
		return scriptAndSqlList;
	}

	public void setScriptAndSqlList(Map<String, BoxChild> scriptAndSqlList) {
		this.scriptAndSqlList = scriptAndSqlList;
	}
	
	
	
}
