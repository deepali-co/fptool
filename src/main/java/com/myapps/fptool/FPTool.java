package com.myapps.fptool;

import static com.myapps.fptool.utils.FPToolHelper.convertToAbsolutePath;
import static com.myapps.fptool.utils.FPToolHelper.createReportFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.myapps.fptool.jira.JiraClientManager;
import com.myapps.fptool.jira.model.JiraIssue;
import com.myapps.fptool.model.FPRulesProcessorTypes;
import com.myapps.fptool.model.RuleGroup;
import com.myapps.fptool.reports.GenerateReport;

public final class FPTool {

	private final FPToolConfigurator appConfigurator;
	private final JiraClientManager jiraClientManager;

	public static void main(String[] args) {
		try {
			FPTool fpTool = new FPTool();
			List<FPRulesProcessorTypes> processorTypes = Arrays.asList(FPRulesProcessorTypes.PMD,
					FPRulesProcessorTypes.PRODUCT);
			if (ArrayUtils.isNotEmpty(args) && "product".equalsIgnoreCase(args[0])) {
				fpTool.analyseProduct(processorTypes);
			} else {
				fpTool.analyseStandAlone(processorTypes);
			}
		} catch (FPToolException e) {
			System.err.println("Error in FPTool!");
			e.printStackTrace(System.err);
		}
	}

	public FPTool() throws FPToolException {
		try {
			appConfigurator = new FPToolConfigurator();
			jiraClientManager = new JiraClientManager(appConfigurator);
		} catch (ConfigurationException e) {
			throw new FPToolException("Error in loading FPTool!", e);
		}
	}

	public void analyseStandAlone(List<FPRulesProcessorTypes> applyProcessorTypes) throws FPToolException {
		try {
			if (StringUtils.isNotBlank(appConfigurator.getInputSourceFilesPath())) {
				appConfigurator.loadInputFilesFromLog();
			}
			List<String> outputResults = new ArrayList<>();
			List<String> inputFiles = new ArrayList<>();
			inputFiles.addAll(appConfigurator.getSourcePaths());
			inputFiles.addAll(appConfigurator.getSourcePathsFromInputFile());
			final RuleGroup ruleGroup = appConfigurator.getAppRuleGroup();
			final String reportFile = convertToAbsolutePath(ruleGroup.getReportPath(), ruleGroup.getReportFile());
			createReportFile(reportFile);
			for (FPRulesProcessorTypes processorType : applyProcessorTypes) {
				FPRulesProcessor processor = FPRulesProcessorFactory.initFPRulesProcessor(processorType, ruleGroup);
				processor.process(inputFiles, reportFile, ruleGroup);
				loadOutputResults(outputResults, reportFile);
			}
		} catch (ConfigurationException | IOException e) {
			throw new FPToolException("Error in analyseStandAlone of FPTool!", e);
		}
	}

	public void analyseProduct(List<FPRulesProcessorTypes> applyProcessorTypes) throws FPToolException {
		try {
			List<JiraIssue> jiraIssues = jiraClientManager.fetchJiraIssues();
			Map<String, Map<String, Set<JiraIssue>>> productJiraIssuesMap = jiraClientManager
					.makeProductSourcesJiraIssuesMap(jiraIssues);
			List<String> outputResults = new ArrayList<>();
			int idx = 0;
			for (Entry<String, Map<String, Set<JiraIssue>>> entry : productJiraIssuesMap.entrySet()) {
				final String productKey = entry.getKey();
				final List<String> inputFiles = new ArrayList<>(entry.getValue().keySet());
				System.out.println("inputFiles: " + inputFiles);
				Map<String, Set<JiraIssue>> sourceJiraIssueMap = entry.getValue();
				final RuleGroup ruleGroup = appConfigurator.loadProductRuleGroup(productKey);
				final String reportFile = convertToAbsolutePath(ruleGroup.getReportPath(), ruleGroup.getReportFile());
				createReportFile(reportFile);
				for (FPRulesProcessorTypes processorType : applyProcessorTypes) {
					FPRulesProcessor processor = FPRulesProcessorFactory.initFPRulesProcessor(processorType, ruleGroup);
					for (Entry<String, Set<JiraIssue>> entry1 : sourceJiraIssueMap.entrySet()) {
						final String inputFile = entry1.getKey();
						processor.process(inputFile, reportFile, ruleGroup);
						int bIdx = idx;
						loadOutputResults(outputResults, reportFile);
						idx = outputResults.size();
						final Set<JiraIssue> jis = entry1.getValue();
						for (int i = bIdx; i < idx; i++) {
							for (JiraIssue jiraIssue : jis) {
								jiraIssue.getViolations().add(outputResults.get(i));
							}
						}
					}
				}
				generateReport("FP_Report", jiraIssues);
				System.out.println("outputResults: " + outputResults);
			}
		} catch (ConfigurationException | IOException e) {
			throw new FPToolException("Error in analyseProduct of FPTool!", e);
		}
	}

	private void loadOutputResults(final List<String> outputResults, String reportFile) throws IOException {
		File file = new File(reportFile);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			if (br != null) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] tokens = line.split(",");
					outputResults.add(tokens[5]);
				}
			}
		}
	}

	private void generateReport(String reportFile, List<JiraIssue> jiraIssues) {
		GenerateReport generateReport = new GenerateReport(appConfigurator.getFinalReportPath(),
				appConfigurator.getFinalReportFile(), FPToolConfigurator.FINAL_REPORT_FILE_EXT);

		generateReport.writeExcelReportForFP(jiraIssues);
	}
}
