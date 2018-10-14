package com.file.util.vo;

import java.util.List;
import java.util.Map;

public class Box {

	private String name;

	//private Map<String, List<BoxChild>> scriptAndSqlList;

	private List<BoxChild> boxChilds;

	public List<BoxChild> getBoxChilds() {
		return boxChilds;
	}

	public void setBoxChilds(List<BoxChild> boxChilds) {
		this.boxChilds = boxChilds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

/*	public Map<String, List<BoxChild>> getScriptAndSqlList() {
		return scriptAndSqlList;
	}

	public void setScriptAndSqlList(Map<String, List<BoxChild>> scriptAndSqlList) {
		this.scriptAndSqlList = scriptAndSqlList;
	}*/

}
