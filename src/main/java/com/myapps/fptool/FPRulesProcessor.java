package com.myapps.fptool;

import java.util.List;

import com.myapps.fptool.model.RuleGroup;

public interface FPRulesProcessor {
	void process(String inputPath, String reportFile, RuleGroup ruleGroup) throws FPToolException;

	void process(List<String> inputPaths, String reportFile, RuleGroup ruleGroup) throws FPToolException;
}
