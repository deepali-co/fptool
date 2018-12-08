package com.myapps.fptool;

import com.myapps.fptool.model.FPRulesProcessorTypes;
import com.myapps.fptool.model.RuleGroup;
import com.myapps.fptool.pmd.PMDFPRulesProcessor;
import com.myapps.fptool.product.ProductFPRulesProcessor;

public final class FPRulesProcessorFactory {

	private FPRulesProcessorFactory() {
		throw new UnsupportedOperationException("This class cannot be instantiated!");
	}

	public static FPRulesProcessor initFPRulesProcessor(FPRulesProcessorTypes processorType, RuleGroup ruleGroup)
			throws FPToolException {
		if (FPRulesProcessorTypes.PMD == processorType) {
			return PMDFPRulesProcessor.init(ruleGroup);
		} else if (FPRulesProcessorTypes.PRODUCT == processorType) {
			return new ProductFPRulesProcessor();
		}
		return null;
	}
}
