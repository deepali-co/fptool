package com.myapps.fptool.jira.model;

import java.util.ArrayList;
import java.util.List;

public class JiraIssue implements Comparable<JiraIssue> {
	private String issueId;
	private String issueKey;
	private String issueType;
	private String summary;
	private JiraIssueDescription description;
	private String status;
	private String assignee;
	private String createdOn;
	private String project;
	private String company;
	private String softwareProductName;
	private String programmingTechnology;
	private String scmUrl;
	private String scmBranch;
	private String effectedLoc;
	private String[] labels;
	private List<String> violations = new ArrayList<>();

	public String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}

	public String getIssueKey() {
		return issueKey;
	}

	public void setIssueKey(String issueKey) {
		this.issueKey = issueKey;
	}

	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public JiraIssueDescription getDescription() {
		return description;
	}

	public void setDescription(JiraIssueDescription description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getSoftwareProductName() {
		return softwareProductName;
	}

	public void setSoftwareProductName(String softwareProductName) {
		this.softwareProductName = softwareProductName;
	}

	public String getProgrammingTechnology() {
		return programmingTechnology;
	}

	public void setProgrammingTechnology(String programmingTechnology) {
		this.programmingTechnology = programmingTechnology;
	}

	public String getScmUrl() {
		return scmUrl;
	}

	public void setScmUrl(String scmUrl) {
		this.scmUrl = scmUrl;
	}

	public String getScmBranch() {
		return scmBranch;
	}

	public void setScmBranch(String scmBranch) {
		this.scmBranch = scmBranch;
	}

	public String getEffectedLoc() {
		return effectedLoc;
	}

	public void setEffectedLoc(String effectedLoc) {
		this.effectedLoc = effectedLoc;
	}

	public String[] getLabels() {
		return labels;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public List<String> getViolations() {
		return violations;
	}

	public void setViolations(List<String> violations) {
		this.violations = violations;
	}

	@Override
	public String toString() {
		return "issueId : [" + issueId + "]" + "issueKey : [" + issueKey + "]" + "issueType : [" + issueType + "]"
				+ "summary : [" + summary + "]" + "description : [" + description + "]" + "status : [" + status + "]"
				+ "assignee : [" + assignee + "]" + "createdOn : [" + createdOn + "]" + "project : [" + project + "]"
				+ "company : [" + company + "]" + "softwareProductName : [" + softwareProductName + "]"
				+ "programmingTechnology : [" + programmingTechnology + "]" + "scmUrl : [" + scmUrl + "]"
				+ "scmBranch : [" + scmBranch + "]" + "effectedLoc : [" + effectedLoc + "]" + "[] labels : [" + labels
				+ "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JiraIssue) {
			return issueId == ((JiraIssue) obj).issueId;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public int compareTo(JiraIssue obj) {
		return issueId.compareTo(obj.issueId);
	}
}
