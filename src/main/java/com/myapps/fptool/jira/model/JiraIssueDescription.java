package com.myapps.fptool.jira.model;

public class JiraIssueDescription {
	private JiraIssueLocation[] issueLocations;
	private String text;

	public JiraIssueLocation[] getIssueLocations() {
		return issueLocations;
	}

	public void setIssueLocations(JiraIssueLocation[] issueLocations) {
		this.issueLocations = issueLocations;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
