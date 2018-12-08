package com.myapps.fptool.jira.model;

public class JiraIssueLocation {
	private String pkg;
	private String fileName;
	private int endLine;
	private int startLine;
	private int startColumn;
	private String commitHashLong;
	private int countLineCode;

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRelFile() {
		return fileName;
	}

	public void setRelFile(String fileName) {
		this.fileName = fileName;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getBeginLine() {
		return startLine;
	}

	public void setBeginLine(int startLine) {
		this.startLine = startLine;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getCommitHashLong() {
		return commitHashLong;
	}

	public void setCommitHashLong(String commitHashLong) {
		this.commitHashLong = commitHashLong;
	}

	public int getCountLineCode() {
		return countLineCode;
	}

	public void setCountLineCode(int countLineCode) {
		this.countLineCode = countLineCode;
	}
}