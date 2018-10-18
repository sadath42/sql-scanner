package com.file.util.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxChild {

	private List<String> params = new ArrayList<>();;

	private String job;

	private String jobType;

	private String command;

	private List<KshChild> kshChilds = new ArrayList<>();

	private List<String> filesTobeScanned = new ArrayList<>();

	private Map<String, String> cmdParams = new HashMap<>();

	public Map<String, String> getCmdParams() {
		return cmdParams;
	}

	public void setCmdParams(Map<String, String> cmdParams) {
		this.cmdParams = cmdParams;
	}

	public List<KshChild> getKshChilds() {
		return kshChilds;
	}

	public void setKshChilds(List<KshChild> kshChilds) {
		this.kshChilds = kshChilds;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List<String> getFilesTobeScanned() {
		return filesTobeScanned;
	}

	public void setFilesTobeScanned(List<String> filesTobeScanned) {
		this.filesTobeScanned = filesTobeScanned;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "BoxChild [params=" + params + ", job=" + job + ", jobType=" + jobType + ", command=" + command
				+ ", kshChilds=" + kshChilds + ", filesTobeScanned=" + filesTobeScanned + "]";
	}

}
