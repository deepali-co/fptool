package com.myapps.fptool.pmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.myapps.fptool.FPRulesProcessor;
import com.myapps.fptool.FPToolException;
import com.myapps.fptool.model.RuleGroup;
import com.myapps.fptool.utils.FPToolHelper;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

public final class PMDFPRulesProcessor implements FPRulesProcessor {

	private final Set<Language> languages;
	private final PMDConfiguration configuration = new PMDConfiguration();
	private final RuleSetFactory ruleSetFactory = new RuleSetFactory();
	private final RuleSets ruleSets;
	private final List<RuleSet> ruleSetList = new ArrayList<>();
	private final RuleContext context = new RuleContext();

	private PMDFPRulesProcessor(final List<String> ruleSetNames) throws RuleSetNotFoundException {
		final String allRuleSet = String.join(",", ruleSetNames);
		configuration.setRuleSets(allRuleSet);
		configuration.setClassLoader(getClass().getClassLoader());
		configuration.setSourceEncoding(StandardCharsets.UTF_8.name());
		configuration.setThreads(1);
		ruleSetFactory.setMinimumPriority(RulePriority.MEDIUM);
		ruleSets = initRuleSets(allRuleSet);
		for (String rs : ruleSetNames) {
			ruleSetList.add(initRuleSet(rs));
		}
		languages = Collections.singleton(Language.JAVA);
	}

	public static PMDFPRulesProcessor init(RuleGroup ruleGroup) throws FPToolException {
		PMDFPRulesProcessor processor = null;
		try {
			processor = new PMDFPRulesProcessor(ruleGroup.getApplyRuleSets());
		} catch (RuleSetNotFoundException e) {
			throw new FPToolException("Error in init of PMDFPRulesProcessor!", e);
		}
		return processor;
	}

	@Override
	public void process(String inputPath, String reportFile, RuleGroup ruleGroup) throws FPToolException {
		try {
			String sourceFile = inputPath;
			String productBasePath = ruleGroup.getProductBasePath();
			if (StringUtils.isNotBlank(productBasePath)) {
				productBasePath = FPToolHelper.trimPathSeparatorFromEnd(productBasePath);
				sourceFile = FPToolHelper.convertToAbsolutePath(ruleGroup.getProductBasePath(), inputPath);
			}
			configuration.setInputPaths(String.join(",", sourceFile));
			configuration.setReportFile(reportFile);
			configuration.setReportFormat("net.sourceforge.pmd.renderers.CSVRenderer");
			configuration.setShowSuppressedViolations(true);
			context.setLanguageVersion(null);
			final Report report = new Report();
			context.setReport(report);
			doPMD(reportFile);
			System.out.println(context.getReport().getSummary());
		} catch (RuntimeException e) {
			throw new FPToolException("Error in process of PMDFPRulesProcessor!", e);
		}
	}

	@Override
	public void process(List<String> inputPaths, String reportFile, RuleGroup ruleGroup) throws FPToolException {
		try {
			List<String> sourceFiles = inputPaths;
			String productBasePath = ruleGroup.getProductBasePath();
			if (StringUtils.isNotBlank(productBasePath)) {
				productBasePath = FPToolHelper.trimPathSeparatorFromEnd(productBasePath);
				sourceFiles = FPToolHelper.convertToAbsolutePaths(ruleGroup.getProductBasePath(), inputPaths);
			}
			configuration.setInputPaths(String.join(",", sourceFiles));
			configuration.setReportFile(reportFile);
			configuration.setReportFormat("net.sourceforge.pmd.renderers.CSVRenderer");
			configuration.setShowSuppressedViolations(true);
			context.setLanguageVersion(null);
			final Report report = new Report();
			context.setReport(report);
			doPMD(reportFile);
			System.out.println(context.getReport().getSummary());
		} catch (RuntimeException e) {
			throw new FPToolException("Error in process of PMDFPRulesProcessor!", e);
		}
	}

	private RuleSets initRuleSets(String rulesets) throws RuleSetNotFoundException {
		RuleSets ruleSets = null;
		ruleSetFactory.setWarnDeprecated(true);
		ruleSets = ruleSetFactory.createRuleSets(rulesets);
		ruleSetFactory.setWarnDeprecated(false);
		return ruleSets;
	}

	private RuleSet initRuleSet(String ruleset) throws RuleSetNotFoundException {
		RuleSet ruleSet = null;
		ruleSetFactory.setWarnDeprecated(true);
		ruleSet = ruleSetFactory.createRuleSet(ruleset);
		ruleSetFactory.setWarnDeprecated(false);
		return ruleSet;
	}

	private void doPMD(String reportFile) {
		try {
			final List<DataSource> javaFilesUnderTest = PMD.getApplicableFiles(configuration, languages);
			final Renderer renderer = configuration.createRenderer(true);
			PMD.processFiles(configuration, ruleSetFactory, javaFilesUnderTest, context,
					Collections.singletonList(renderer));
		} catch (RuntimeException e) {
			System.err.println("Error in doPMB of PMDFPRulesProcessor!");
			e.printStackTrace(System.err);

			try {
				addDoPMDViolation("1,,,0,0," + e.getMessage() + ",", reportFile);
			} catch (IOException e1) {
				System.err.println("Error in doPMB of PMDFPRulesProcessor!");
				e.printStackTrace(System.err);
			}
		}
	}

	private void addDoPMDViolation(final String violation, String reportFile) throws IOException {
		File file = new File(reportFile);

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
			if (bw != null) {
				bw.write(violation);
				bw.newLine();
			}
		}
	}
}
