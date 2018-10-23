package com.file.util.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It is the child of box . Each box will have n childs. Each job is considered
 * as a child of the box.
 *
 */
public class BoxChild {

	/**
	 * List of the parameters present in the job.
	 */
	private List<String> params = new ArrayList<>();;

	/**
	 * Name of the job . It is the child of a box.
	 */
	private String job;

	/**
	 * Type of job
	 */
	private String jobType;

	/**
	 * Command executed for the job.
	 */
	private String command;

	/**
	 * List of files which are parsed recursively and its associated parameters
	 * and sql commands.
	 */
	private List<KshChild> kshChilds = new ArrayList<>();

	/**
	 * Files to be parsed recursively. This list will be populated during the
	 * extraction of jobs.Then this list is recursively iterated to get the
	 * scripts which are to be parsed.
	 */
	private List<String> filesTobeScanned = new ArrayList<>();

	/**
	 * The parmaters which are part of the command being executed.
	 */
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
