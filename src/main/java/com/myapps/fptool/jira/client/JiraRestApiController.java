package com.myapps.fptool.jira.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lesstif.jira.issue.Attachment;
import com.lesstif.jira.issue.Issue;
import com.lesstif.jira.issue.IssueFields;
import com.lesstif.jira.issue.IssueSearchResult;
import com.lesstif.jira.project.Project;
import com.lesstif.jira.services.IssueService;
import com.lesstif.jira.services.ProjectService;
import com.lesstif.jira.util.HttpConnectionUtil;
import com.myapps.fptool.FPToolConfigurator;

public class JiraRestApiController {

	private static JiraRestApiController jiraRestApiController;
	private static final Logger logger = LoggerFactory.getLogger(JiraRestApiController.class);

	private static String PROJECT_KEY;
	private static Set<String> SOFTWARE_PRODUCT_NAME;

	public static JiraRestApiController getInstance() {
		if (jiraRestApiController == null) {
			jiraRestApiController = new JiraRestApiController();
		}

		return jiraRestApiController;
	}

	private JiraRestApiController() {
		HttpConnectionUtil.disableSslVerification();
	}

	public void initJiraRestApiController(FPToolConfigurator appConfigurator) {
		PROJECT_KEY = appConfigurator.getProjects().get(0);
		SOFTWARE_PRODUCT_NAME = appConfigurator.getSoftwareProductNames();
	}

	public Project getProject(String projectKey) throws IOException, ConfigurationException {
		ProjectService prjService = new ProjectService();

		projectKey = (projectKey == null) ? PROJECT_KEY : projectKey;

		Project prj = prjService.getProjectDetail(projectKey);
		return prj;
	}

	public void showProject(Project prj) throws IOException, ConfigurationException {
		logger.debug("Project Key=" + prj.getKey());
		logger.debug("Project Name=" + prj.getName());
		logger.debug("Project Description=" + prj.getDescription());
	}

	public Issue getIssue(String issueKey) throws IOException, ConfigurationException {
		final IssueService issueService = new IssueService();

		return (issueKey == null || issueKey.equals("")) ? null : issueService.getIssue(issueKey);
	}

	public void showIssue(Issue issue) throws IOException, ConfigurationException {

		logger.debug("Issue Key:" + issue.getKey());

		IssueFields fields = issue.getFields();

		// Project key
		logger.debug("Project Key:" + fields.getProject().getKey());

		// issue type
		logger.debug("IssueType:" + fields.getIssuetype().toPrettyJsonString());

		// issue description
		logger.debug("Issue Description:" + fields.getDescription());

		// attachment info
		List<Attachment> attachs = issue.getFields().getAttachment();
		for (Attachment a : attachs) {
			logger.debug("Attachment:" + a.getFilename());
		}
	}

	public Map<String, Object> getCustomFields(String issueKey) throws IOException, ConfigurationException {
		Issue issue = getIssue(issueKey);

		Map<String, Object> fields = issue.getFields().getCustomfield();
		return fields;
	}

	public Map<String, Object> getCustomFields(Issue issue) {
		Map<String, Object> fields = issue.getFields().getCustomfield();
		return fields;
	}

	public void showCustomFields(Map<String, Object> fields) {
		for (String key : fields.keySet()) {
			logger.debug("Field Name: " + key + ",value:" + fields.get(key));
		}
	}

	public List<Issue> getIssuesFromQuery(String query) throws IOException, ConfigurationException {
		List<Issue> issues = null;
		IssueSearchResult issueSearchResult = getSearchResultFromQuery(query);

		if (issueSearchResult.getTotal() != 0) {
			issues = issueSearchResult.getIssues();
		}
		return issues;
	}

	public IssueSearchResult getSearchResultFromQuery(String query) throws IOException, ConfigurationException {
		IssueService issueService = new IssueService();
		final String spnQuery = createQueryForSPN();
		if (StringUtils.isBlank(query)) {
			query = "(project = " + PROJECT_KEY + " AND " + spnQuery + ")";
		} else {
			query = query + " AND (project = " + PROJECT_KEY + " AND " + spnQuery + ")";
		}

		logger.debug("Search query : " + query);

		return issueService.getIssuesFromQuery(query);
	}

	public void showIssueSearchResult(IssueSearchResult issues) throws IOException, ConfigurationException {
		if (issues != null) {
			logger.debug("Issues: " + issues.toString());
		}
	}

	public String mapToPrettyJsonString(Map<String, Object> map) {
		ObjectMapper mapper = new ObjectMapper();

		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(map);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonStr;
	}

	private String createQueryForSPN() {
		final StringBuilder sb = new StringBuilder();
		sb.append("('software product name' ~ ");
		sb.append(String.join(" OR 'software product name' ~ ", SOFTWARE_PRODUCT_NAME));
		sb.append(")");
		return sb.toString();
	}
}
