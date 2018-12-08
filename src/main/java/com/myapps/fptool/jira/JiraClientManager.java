package com.myapps.fptool.jira;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.myapps.fptool.FPToolConfigurator;
import com.myapps.fptool.jira.client.JiraRestApiController;
import com.myapps.fptool.jira.client.JiraRestApiHelper;
import com.myapps.fptool.jira.model.JiraIssue;
import com.myapps.fptool.jira.model.JiraIssueLocation;

public final class JiraClientManager {

	private final FPToolConfigurator appConfigurator;
	private final JiraRestApiController jiraRestApiController;

	public JiraClientManager(FPToolConfigurator appConfigurator) {
		this.appConfigurator = appConfigurator;
		jiraRestApiController = JiraRestApiController.getInstance();
		jiraRestApiController.initJiraRestApiController(appConfigurator);
	}

	/**
	 * Fetch issues from JIRA
	 */
	public List<JiraIssue> fetchJiraIssues() throws IOException, ConfigurationException {
		return JiraRestApiHelper
				.getJiraIssues(jiraRestApiController.getIssuesFromQuery(appConfigurator.getQueries().get(0)));
	}

	/**
	 * Convert JiraIssues into Product -> Source Files -> JiraIssues map
	 */
	public Map<String, Map<String, Set<JiraIssue>>> makeProductSourcesJiraIssuesMap(List<JiraIssue> jiraIssues) {
		final Map<String, Map<String, Set<JiraIssue>>> productSourcesJiraIssuesMap = new HashMap<>();

		final Map<String, String> productGitBaseProductKeyMap = appConfigurator.getJiraSettings()
				.getProductGitBaseProductKeyMap();

		for (JiraIssue jiraIssue : jiraIssues) {
			String productKey1 = jiraIssue.getSoftwareProductName();
			String productKey2 = productGitBaseProductKeyMap.get(jiraIssue.getScmUrl());
			final String productKey = productKey2 == null ? productKey1 : productKey2;
			if (StringUtils.isNotEmpty(productKey)) {
				if (jiraIssue.getDescription() != null
						&& ArrayUtils.isNotEmpty(jiraIssue.getDescription().getIssueLocations())) {
					for (JiraIssueLocation issueLocation : jiraIssue.getDescription().getIssueLocations()) {
						productSourcesJiraIssuesMap.computeIfAbsent(productKey, key -> new HashMap<>())
								.computeIfAbsent(issueLocation.getFileName(), loc -> new HashSet<>()).add(jiraIssue);
					}
				}
			}
		}

		return productSourcesJiraIssuesMap;
	}

	public Map<String, Set<JiraIssue>> getSourceIssuesMap(final List<JiraIssue> issues) {
		final Map<String, Set<JiraIssue>> sourceIssuesMap = new HashMap<>();

		for (JiraIssue issue : issues) {
			if (issue.getDescription() != null && ArrayUtils.isNotEmpty(issue.getDescription().getIssueLocations())) {
				for (JiraIssueLocation issueLocation : issue.getDescription().getIssueLocations()) {
					sourceIssuesMap.computeIfAbsent(issueLocation.getFileName(), loc -> new HashSet<>()).add(issue);
				}
			}
		}

		return sourceIssuesMap;
	}
}
