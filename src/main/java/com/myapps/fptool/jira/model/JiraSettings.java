package com.myapps.fptool.jira.model;

import java.util.Map;
import java.util.Set;

import com.myapps.fptool.model.Product;

public class JiraSettings {
	private String userName;
	private String password;
	private String jiraUrl;
	private String project;
	private String query;
	private Set<String> productKeys;
	private Map<String, Product> productKeyProductMap;
	private Map<String, String> productGitBaseProductKeyMap;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJiraUrl() {
		return jiraUrl;
	}

	public void setJiraUrl(String jiraUrl) {
		this.jiraUrl = jiraUrl;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Set<String> getProductKeys() {
		return productKeys;
	}

	public void setProductKeys(Set<String> productKeys) {
		this.productKeys = productKeys;
	}

	public Map<String, Product> getProductKeyProductMap() {
		return productKeyProductMap;
	}

	public void setProductKeyProductMap(Map<String, Product> productKeyProductMap) {
		this.productKeyProductMap = productKeyProductMap;
	}

	public Map<String, String> getProductGitBaseProductKeyMap() {
		return productGitBaseProductKeyMap;
	}

	public void setProductGitBaseProductKeyMap(Map<String, String> productGitBaseProductKeyMap) {
		this.productGitBaseProductKeyMap = productGitBaseProductKeyMap;
	}
}
