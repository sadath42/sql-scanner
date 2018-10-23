package com.file.util.vo;

import java.util.List;

/**
 * Box for the vap.
 *
 */
public class Box {

	private String name;

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

}
