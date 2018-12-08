package com.myapps.fptool.product;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.myapps.fptool.FPRulesProcessor;
import com.myapps.fptool.FPToolException;
import com.myapps.fptool.model.RuleGroup;

public final class ProductFPRulesProcessor implements FPRulesProcessor {

	@Override
	public void process(String inputPath, String reportFile, RuleGroup ruleGroup) throws FPToolException {
		try {
			List<String> outputViolations = new ArrayList<>();
			for (String flagModule : ruleGroup.getFlagModules()) {
				if (inputPath.startsWith(flagModule)) {
					final String violation = "1,," + inputPath + ",0,0,"
							+ "Identify classes from flagged modules: Class from ignored module '" + flagModule
							+ "' found" + ",";
					outputViolations.add(violation);
				}
			}
			if (CollectionUtils.isNotEmpty(outputViolations)) {
				addViolations(outputViolations, reportFile);
			}
		} catch (IOException e) {
			throw new FPToolException("Error in process of ProductFPRulesProcessor!", e);
		}
	}

	@Override
	public void process(List<String> inputPaths, String reportFile, RuleGroup ruleGroup) throws FPToolException {
		try {
			List<String> outputViolations = new ArrayList<>();
			for (String inputPath : inputPaths) {
				for (String flagModule : ruleGroup.getFlagModules()) {
					if (inputPath.startsWith(flagModule)) {
						final String violation = "1,," + inputPath + ",0,0,"
								+ "Identify classes from flagged modules: Class from ignored module '" + flagModule
								+ "' found" + ",";
						outputViolations.add(violation);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(outputViolations)) {
				addViolations(outputViolations, reportFile);
			}
		} catch (IOException e) {
			throw new FPToolException("Error in process of ProductFPRulesProcessor!", e);
		}
	}

	private void addViolations(final List<String> violations, String reportFile) throws IOException {
		File file = new File(reportFile);

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
			if (bw != null) {
				for (String violation : violations) {
					bw.write(violation);
					bw.newLine();
				}
			}
		}
	}
}
