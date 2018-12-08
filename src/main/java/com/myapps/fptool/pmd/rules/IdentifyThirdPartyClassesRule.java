package com.myapps.fptool.pmd.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;

public class IdentifyThirdPartyClassesRule extends AbstractJavaRule {
	private static final List<String> thirdPartyPackages = new ArrayList<>();
	private static final String[] THIRD_PARTY_PACKAGES = { "javax\\..*", "java\\..*", "org\\..*", "net\\..*",
			"io\\..*" };
	private String[] origThirdPartyPackages;
	private List<String> currThirdPartyPackages;

	static {
		try {
			PropertiesConfiguration config = new PropertiesConfiguration("PMDSettings.properties");
			thirdPartyPackages.addAll(config.getList("flagPackage", Collections.emptyList()).stream()
					.map(flagPackage -> flagPackage.toString()).collect(Collectors.toList()));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static final StringMultiProperty THIRD_PARTY_PACKAGES_DESCRIPTOR = new StringMultiProperty(
			"thirdPartyPackages", "Identify classes from third party packages", THIRD_PARTY_PACKAGES, 3.0f, '|');

	public static final StringMultiProperty THIRD_PARTY_PACKAGES_DESCRIPTOR_1 = new StringMultiProperty(
			"thirdPartyPackages", "Identify classes from third party packages",
			thirdPartyPackages.toArray(new String[0]), 3.0f, '|');

	public IdentifyThirdPartyClassesRule() {
		definePropertyDescriptor(THIRD_PARTY_PACKAGES_DESCRIPTOR_1);
	}

	@Override
	public Object visit(ASTPackageDeclaration decl, Object data) {
		if (currThirdPartyPackages == null) {
			start(null);
		}
		checkPackageMeetsRequirement(decl, data);
		return super.visit(decl, data);
	}

	private boolean checkPackageMeetsRequirement(ASTPackageDeclaration decl, Object data) {
		final String origPackageName = decl.getPackageNameImage();
		final String packageName = origPackageName.toUpperCase(Locale.ROOT);
		ASTClassOrInterfaceDeclaration classDecl = decl.getFirstParentOfType(ASTCompilationUnit.class)
				.getFirstChildOfType(ASTTypeDeclaration.class)
				.getFirstChildOfType(ASTClassOrInterfaceDeclaration.class);

		for (String thirdPartyPackage : currThirdPartyPackages) {
			if (Pattern.compile(thirdPartyPackage).matcher(packageName).matches()) {
				addViolationWithMessage(data, decl, reportMsgFor(origPackageName + "." + classDecl.getImage()),
						decl.getBeginLine(), decl.getEndLine());
				return true;
			}
		}
		return false;
	}

	private String reportMsgFor(String className) {
		StringBuilder msg = new StringBuilder(getMessage()).append(": ");

		msg.append("Class from a third party package found: " + className);

		return msg.toString();
	}

	@Override
	public void start(RuleContext ctx) {
		origThirdPartyPackages = getProperty(THIRD_PARTY_PACKAGES_DESCRIPTOR_1);
		currThirdPartyPackages = new ArrayList<>();
		for (String classPackage : origThirdPartyPackages) {
			currThirdPartyPackages.add(classPackage.toUpperCase(Locale.ROOT));
		}
	}

	@Override
	public void end(RuleContext ctx) {
		// can be implemented
	}
}
