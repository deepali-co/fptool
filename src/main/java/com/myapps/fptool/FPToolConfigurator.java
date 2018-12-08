package com.myapps.fptool;

import static com.myapps.fptool.utils.FPToolHelper.convertObjectListToListOfStrings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import com.myapps.fptool.jira.model.JiraSettings;
import com.myapps.fptool.model.Product;
import com.myapps.fptool.model.RuleGroup;

public class FPToolConfigurator {

	private final List<String> projects = new ArrayList<>();
	private final Set<String> softwareProductNames = new HashSet<>();
	private final List<String> queries = new ArrayList<>();
	private final List<String> sourcePaths = new ArrayList<>();
	private final List<String> sourcePathsFromInputFile = new ArrayList<>();
	private String inputSourceFilesPath;
	private final Set<String> productKeys = new HashSet<>();
	private JiraSettings jiraSettings;
	private RuleGroup appRuleGroup;
	private String finalReportPath;
	private String finalReportFile;
	public static final String FINAL_REPORT_FILE_EXT = "xls";
	public static final String VIOLATIONS_REPORT_FILE_EXT = "csv";

	public FPToolConfigurator() throws ConfigurationException {
		loadApplicationSettings();
		loadJiraSettings();
	}

	private void loadJiraSettings() throws ConfigurationException {
		PropertiesConfiguration config = new PropertiesConfiguration("jira-rest-client.properties");
		jiraSettings = new JiraSettings();
		jiraSettings.setUserName(config.getString("jira.user.id"));
		jiraSettings.setPassword(config.getString("jira.user.pwd"));
		jiraSettings.setJiraUrl(config.getString("jira.server.url"));

		jiraSettings.setQuery(queries.get(0));
		jiraSettings.setProject(projects.get(0));
		jiraSettings.setProductKeys(productKeys);

		final Map<String, Product> productKeyProductMap = new HashMap<>();
		jiraSettings.setProductKeyProductMap(productKeyProductMap);
		final Map<String, String> productGitBaseProductKeyMap = new HashMap<>();
		jiraSettings.setProductGitBaseProductKeyMap(productGitBaseProductKeyMap);
		for (String productKey : jiraSettings.getProductKeys()) {
			PropertiesConfiguration config1 = new PropertiesConfiguration(productKey + ".properties");
			Product product = new Product();
			product.setProductBasePath(config1.getString("basePath"));
			product.setSoftwareProductName(config1.getString("softwareProductName"));
			product.setScmUrl(config1.getString("scmUrl"));
			product.setProductKey(productKey);
			productGitBaseProductKeyMap.put(product.getScmUrl(), productKey);
			productKeyProductMap.put(productKey, product);

			softwareProductNames.add(product.getSoftwareProductName());
		}
	}

	private void loadApplicationSettings() throws ConfigurationException {
		PropertiesConfiguration config = new PropertiesConfiguration("FPToolSettings.properties");
		sourcePaths.addAll(config.getList("sourcePath", Collections.emptyList()).stream()
				.map(sourcePath -> sourcePath.toString()).collect(Collectors.toList()));
		inputSourceFilesPath = config.getString("inputSourceFilesPath");

		projects.add(config.getString("project"));
		queries.add(config.getString("query"));

		productKeys.addAll(convertObjectListToListOfStrings(config.getList("productKey")));
		appRuleGroup = new RuleGroup();
		appRuleGroup.setReportPath(config.getString("reportPath"));
		appRuleGroup.setReportFile(config.getString("violationsReportFile"));
		correctAppReportPath(appRuleGroup);
		correctAppReportFile(appRuleGroup);
		finalReportPath = appRuleGroup.getReportPath();
		finalReportFile = config.getString("finalReportFile");
		correctFinalReportFile();
		appRuleGroup.getApplyRuleSets().addAll(config.getList("ruleSet", Collections.emptyList()).stream()
				.map(ruleSet -> ruleSet.toString()).collect(Collectors.toList()));
		appRuleGroup.getIncludeModules().addAll(config.getList("includeModule", Collections.emptyList()).stream()
				.map(includeModule -> includeModule.toString()).collect(Collectors.toList()));
		appRuleGroup.getExcludeModules().addAll(config.getList("excludeModule", Collections.emptyList()).stream()
				.map(excludeModule -> excludeModule.toString()).collect(Collectors.toList()));
		appRuleGroup.getFlagModules().addAll(config.getList("flagModule", Collections.emptyList()).stream()
				.map(flagModule -> flagModule.toString()).collect(Collectors.toList()));
		appRuleGroup.getIncludePackages().addAll(config.getList("includePackage", Collections.emptyList()).stream()
				.map(includePackage -> includePackage.toString()).collect(Collectors.toList()));
		appRuleGroup.getExcludePackages().addAll(config.getList("excludePackage", Collections.emptyList()).stream()
				.map(excludePackage -> excludePackage.toString()).collect(Collectors.toList()));
		appRuleGroup.getFlagPackages().addAll(config.getList("flagPackage", Collections.emptyList()).stream()
				.map(flagPackage -> flagPackage.toString()).collect(Collectors.toList()));
		appRuleGroup.getFindWords().addAll(config.getList("findWord", Collections.emptyList()).stream()
				.map(findWord -> findWord.toString()).collect(Collectors.toList()));
		appRuleGroup.getAutoGeneratedTerms().addAll(config.getList("autoGeneratedTerm", Collections.emptyList())
				.stream().map(autoGeneratedTerm -> autoGeneratedTerm.toString()).collect(Collectors.toList()));
	}

	public void loadInputFilesFromLog() throws IOException, ConfigurationException {
		final Set<String> inputFiles = new HashSet<>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(inputSourceFilesPath)), StandardCharsets.UTF_8))) {
			if (br != null) {
				String line;
				while ((line = br.readLine()) != null) {
					if (StringUtils.isNotBlank(line)) {
						inputFiles.add(line.trim());
					}
				}
			}
		}
		sourcePathsFromInputFile.addAll(inputFiles);
	}

	public RuleGroup loadProductRuleGroup(String productName) throws ConfigurationException {
		PropertiesConfiguration config = new PropertiesConfiguration(productName + ".properties");
		final RuleGroup ruleGroup = new RuleGroup();
		ruleGroup.setProductBasePath(config.getString("productBasePath"));
		ruleGroup.setScmUrl(config.getString("scmUrl"));
		ruleGroup.setSoftwareProductName(config.getString("softwareProductName"));
		ruleGroup.setReportPath(config.getString("reportPath"));
		ruleGroup.setReportFile(config.getString("violationsReportFile"));
		correctReportPath(ruleGroup);
		correctReportFile(ruleGroup);
		finalReportPath = ruleGroup.getReportPath();
		finalReportFile = config.getString("finalReportFile");
		correctFinalReportFile();
		ruleGroup.getApplyRuleSets().addAll(config.getList("ruleSet", Collections.emptyList()).stream()
				.map(ruleSet -> ruleSet.toString()).collect(Collectors.toList()));
		correctApplyRuleSets(ruleGroup);
		ruleGroup.getIncludeModules().addAll(config.getList("includeModule", Collections.emptyList()).stream()
				.map(includeModule -> includeModule.toString()).collect(Collectors.toList()));
		ruleGroup.getExcludeModules().addAll(config.getList("excludeModule", Collections.emptyList()).stream()
				.map(excludeModule -> excludeModule.toString()).collect(Collectors.toList()));
		ruleGroup.getFlagModules().addAll(config.getList("flagModule", Collections.emptyList()).stream()
				.map(flagModule -> flagModule.toString()).collect(Collectors.toList()));
		ruleGroup.getIncludePackages().addAll(config.getList("includePackage", Collections.emptyList()).stream()
				.map(includePackage -> includePackage.toString()).collect(Collectors.toList()));
		ruleGroup.getExcludePackages().addAll(config.getList("excludePackage", Collections.emptyList()).stream()
				.map(excludePackage -> excludePackage.toString()).collect(Collectors.toList()));
		ruleGroup.getFlagPackages().addAll(config.getList("flagPackage", Collections.emptyList()).stream()
				.map(flagPackage -> flagPackage.toString()).collect(Collectors.toList()));
		ruleGroup.getFlagPackages().addAll(appRuleGroup.getFlagPackages());
		ruleGroup.getFindWords().addAll(config.getList("findWord", Collections.emptyList()).stream()
				.map(findWord -> findWord.toString()).collect(Collectors.toList()));
		ruleGroup.getAutoGeneratedTerms().addAll(config.getList("autoGeneratedTerm", Collections.emptyList()).stream()
				.map(autoGeneratedTerm -> autoGeneratedTerm.toString()).collect(Collectors.toList()));
		return ruleGroup;
	}

	public List<String> getProjects() {
		return projects;
	}

	public Set<String> getSoftwareProductNames() {
		return softwareProductNames;
	}

	public List<String> getQueries() {
		return queries;
	}

	public JiraSettings getJiraSettings() {
		return jiraSettings;
	}

	public List<String> getSourcePaths() {
		return sourcePaths;
	}

	public List<String> getSourcePathsFromInputFile() {
		return sourcePathsFromInputFile;
	}

	public String getInputSourceFilesPath() {
		return inputSourceFilesPath;
	}

	public Set<String> getProductKeys() {
		return productKeys;
	}

	public RuleGroup getAppRuleGroup() {
		return appRuleGroup;
	}

	public String getFinalReportPath() {
		return finalReportPath;
	}

	public String getFinalReportFile() {
		return finalReportFile;
	}

	private void correctApplyRuleSets(RuleGroup rGroup) {
		if (CollectionUtils.isEmpty(rGroup.getApplyRuleSets())) {
			rGroup.getApplyRuleSets().addAll(appRuleGroup.getApplyRuleSets());
		}
	}

	private void correctReportPath(RuleGroup rGroup) {
		if (StringUtils.isBlank(rGroup.getReportPath())) {
			rGroup.setReportPath(appRuleGroup.getReportPath());
		}
		correctAppReportPath(rGroup);
	}

	private void correctReportFile(RuleGroup rGroup) {
		if (StringUtils.isBlank(rGroup.getReportFile())) {
			rGroup.setReportFile(appRuleGroup.getReportFile());
		}
		correctAppReportFile(rGroup);
	}

	private void correctAppReportPath(RuleGroup rGroup) {
		if (StringUtils.isBlank(rGroup.getReportPath())) {
			rGroup.setReportPath("/");
		}
	}

	private void correctAppReportFile(RuleGroup rGroup) {
		if (StringUtils.isBlank(rGroup.getReportFile())) {
			rGroup.setReportFile("ReportFPClasses.csv");
		}
	}

	private void correctFinalReportFile() {
		if (StringUtils.isBlank(finalReportFile)) {
			finalReportFile = "FP_Report";
		}
	}
}
