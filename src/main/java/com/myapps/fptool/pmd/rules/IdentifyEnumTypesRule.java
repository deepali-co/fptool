package com.myapps.fptool.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class IdentifyEnumTypesRule extends AbstractJavaRule {

	@Override
	public Object visit(ASTEnumDeclaration decl, Object data) {
		ASTPackageDeclaration packageDecl = decl.getFirstParentOfType(ASTTypeDeclaration.class)
				.getFirstParentOfType(ASTCompilationUnit.class).getPackageDeclaration();

		addViolationWithMessage(data, decl, reportMsgFor(packageDecl.getPackageNameImage() + "." + decl.getImage()),
				decl.getBeginLine(), decl.getEndLine());

		return super.visit(decl, data);
	}

	private String reportMsgFor(String className) {
		StringBuilder msg = new StringBuilder(getMessage()).append(": ");

		msg.append("Enum type found: " + className);

		return msg.toString();
	}
}
