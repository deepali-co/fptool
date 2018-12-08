package com.myapps.fptool.jira.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.lesstif.jira.issue.Issue;
import com.lesstif.jira.issue.IssueFields;
import com.lesstif.jira.issue.IssueType;
import com.lesstif.jira.issue.Reporter;
import com.lesstif.jira.issue.Status;
import com.lesstif.jira.project.Project;
import com.myapps.fptool.jira.model.JiraIssue;
import com.myapps.fptool.jira.model.JiraIssueDescription;
import com.myapps.fptool.jira.model.JiraIssueLocation;

public final class JiraRestApiHelper {

	private JiraRestApiHelper() {
		throw new UnsupportedOperationException("This class cannot be instantiated!");
	}

	public static List<JiraIssue> getJiraIssues(List<Issue> issues) throws IOException {
		List<JiraIssue> jiraIssues = new ArrayList<>();
		for (Issue issue : issues) {
			IssueFields issueFields = issue.getFields();
			JiraIssue jiraIssue = new JiraIssue();
			Map<String, Object> customFields = issueFields.getCustomfield();

			jiraIssue.setIssueId(issue.getId());
			jiraIssue.setIssueKey(issue.getKey());
			jiraIssue.setIssueType(parseIssueType(issueFields.getIssuetype()));
			jiraIssue.setSummary(issueFields.getSummary());
			jiraIssue.setDescription(parseDescription(issueFields.getDescription()));
			jiraIssue.setStatus(parseStatus(issueFields.getStatus()));
			jiraIssue.setAssignee(parseAssignee(issueFields.getAssignee()));
			jiraIssue.setCreatedOn(convertObjectToString(issueFields.getCreated()));
			jiraIssue.setProject(parseProject(issueFields.getProject()));
			jiraIssue.setCompany(parseCompany(customFields.get("customfield_11707")));// Company
			// Software Product Name
			jiraIssue.setSoftwareProductName(convertObjectToString(customFields.get("customfield_11602")));
			// Programming Technology
			jiraIssue.setProgrammingTechnology(convertObjectToString(customFields.get("customfield_12400")));
			jiraIssue.setScmUrl(convertObjectToString(customFields.get("customfield_11908")));// scm_url
			jiraIssue.setScmBranch(convertObjectToString(customFields.get("customfield_11909")));// scm_branch
			jiraIssue.setEffectedLoc(convertObjectToString(customFields.get("customfield_15600")));// Effected LoC
			jiraIssue.setLabels(issueFields.getLabels());

			jiraIssues.add(jiraIssue);
		}
		return jiraIssues;
	}

	private static JiraIssueDescription parseDescription(String descriptionText) throws IOException {
		final JiraIssueDescription description = new JiraIssueDescription();
		description.setText(descriptionText);

		final String locations = extractString(descriptionText, "{noformat}", "{noformat}").trim();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		TypeReference<JiraIssueLocation[]> ref = new TypeReference<JiraIssueLocation[]>() {
		};
		final JiraIssueLocation[] issueLocations = mapper.readValue(locations, ref);
		description.setIssueLocations(issueLocations);

		return description;
	}

	private static String convertObjectToString(Object obj) {
		return (obj == null) ? "" : obj.toString();
	}

	private static String parseIssueType(Object issueType) throws IOException {
		if (issueType instanceof IssueType) {
			return ((IssueType) issueType).getName();
		}
		return convertObjectToString(issueType);
	}

	private static String parseStatus(Object status) throws IOException {
		if (status instanceof Status) {
			return ((Status) status).getName();
		}
		return convertObjectToString(status);
	}

	private static String parseAssignee(Object assignee) throws IOException {
		if (assignee instanceof Reporter) {
			return ((Reporter) assignee).getKey();
		}
		return convertObjectToString(assignee);
	}

	private static String parseProject(Object project) throws IOException {
		if (project instanceof Project) {
			return ((Project) project).getKey();
		}
		return convertObjectToString(project);
	}

	private static String parseCompany(Object company) throws IOException {
		if (company != null) {
			return extractString(company.toString(), "value=", ",");
		}
		return convertObjectToString(company);
	}

	private static String extractString(String orig, String startDelim, String endDelim) {
		int idx = orig.indexOf(startDelim);
		final int lenDelim = startDelim.length();

		return orig.substring(idx + lenDelim, orig.indexOf(endDelim, idx + lenDelim));
	}
}
