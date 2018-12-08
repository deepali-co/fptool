package com.myapps.fptool.pmd.rules;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class IdentifyPojoClassesRule extends AbstractJavaRule {

	@Override
	public Object visit(ASTPackageDeclaration decl, Object data) {
		ASTClassOrInterfaceDeclaration classDecl = decl.findChildrenOfType(ASTClassOrInterfaceDeclaration.class).get(0);

		List<ASTMethodDeclaration> mthdDecls = decl.findDescendantsOfType(ASTMethodDeclaration.class);

		boolean isPojoClass = true;

		for (ASTMethodDeclaration mthdDecl : mthdDecls) {
			final String methodName = mthdDecl.getMethodName();
			final boolean isGet = methodName.startsWith("get");
			final boolean isSet = methodName.startsWith("set");
			final boolean isIs = methodName.startsWith("is");

			final boolean isObjectMethod = "toString".equals(methodName) || "hashCode".equals(methodName)
					|| "equals".equals(methodName) || "clone".equals(methodName) || "compare".equals(methodName)
					|| "compareTo".equals(methodName);

			if (isObjectMethod) {
				continue;
			}

			List<ASTResultType> resDecl = mthdDecl.findChildrenOfType(ASTResultType.class);
			String returnType = null;
			List<ASTFormalParameter> params = null;
			if (CollectionUtils.isNotEmpty(resDecl) && resDecl.size() == 1) {
				ASTClassOrInterfaceType returnDecl = resDecl.get(0)
						.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
				if (returnDecl != null) {
					returnType = returnDecl.getImage();
				}
			}
			List<ASTMethodDeclarator> mthdDDecl = mthdDecl.findChildrenOfType(ASTMethodDeclarator.class);
			if (CollectionUtils.isNotEmpty(mthdDDecl) && mthdDDecl.size() == 1) {
				ASTFormalParameters paramsDecl = mthdDDecl.get(0).getFirstDescendantOfType(ASTFormalParameters.class);
				if (paramsDecl != null) {
					params = paramsDecl.findChildrenOfType(ASTFormalParameter.class);
				}
			}
			if ((isGet || isIs) && (CollectionUtils.isNotEmpty(params) || "void".equalsIgnoreCase(returnType))) {
				isPojoClass = false;
				break;
			} else if (isSet && (CollectionUtils.isEmpty(params) || params.size() > 1 || returnType != null)) {
				isPojoClass = false;
				break;
			} else {
				isPojoClass = false;
				break;
			}
		}

		if (isPojoClass) {
			addViolationWithMessage(data, decl, reportMsgFor(decl.getPackageNameImage() + "." + classDecl.getImage()),
					decl.getBeginLine(), decl.getEndLine());
		}

		return super.visit(decl, data);
	}

	/*@Override
	public Object visit(ASTClassOrInterfaceBody decl, Object data) {
		ASTClassOrInterfaceDeclaration classDecl = decl.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
		ASTCompilationUnit cuDecl = decl.getFirstParentOfType(ASTTypeDeclaration.class)
				.getFirstParentOfType(ASTCompilationUnit.class);

		List<ASTMethodDeclaration> mthdDecls = decl.findDescendantsOfType(ASTMethodDeclaration.class);

		boolean isPojoClass = true;

		for (ASTMethodDeclaration mthdDecl : mthdDecls) {
			final String methodName = mthdDecl.getMethodName();
			final boolean isGet = methodName.startsWith("get");
			final boolean isSet = methodName.startsWith("set");
			final boolean isIs = methodName.startsWith("is");

			final boolean isObjectMethod = "toString".equals(methodName) || "hashCode".equals(methodName)
					|| "equals".equals(methodName) || "clone".equals(methodName) || "compare".equals(methodName)
					|| "compareTo".equals(methodName);

			if (isObjectMethod) {
				continue;
			}

			List<ASTResultType> resDecl = mthdDecl.findChildrenOfType(ASTResultType.class);
			String returnType = null;
			List<ASTFormalParameter> params = null;
			if (CollectionUtils.isNotEmpty(resDecl) && resDecl.size() == 1) {
				ASTClassOrInterfaceType returnDecl = resDecl.get(0)
						.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
				if (returnDecl != null) {
					returnType = returnDecl.getImage();
				}
			}
			List<ASTMethodDeclarator> mthdDDecl = mthdDecl.findChildrenOfType(ASTMethodDeclarator.class);
			if (CollectionUtils.isNotEmpty(mthdDDecl) && mthdDDecl.size() == 1) {
				ASTFormalParameters paramsDecl = mthdDDecl.get(0).getFirstDescendantOfType(ASTFormalParameters.class);
				if (paramsDecl != null) {
					params = paramsDecl.findChildrenOfType(ASTFormalParameter.class);
				}
			}
			if ((isGet || isIs) && (CollectionUtils.isNotEmpty(params) || "void".equalsIgnoreCase(returnType))) {
				isPojoClass = false;
				break;
			} else if (isSet && (CollectionUtils.isEmpty(params) || params.size() > 1 || returnType != null)) {
				isPojoClass = false;
				break;
			} else {
				isPojoClass = false;
				break;
			}
		}

		if (isPojoClass) {
			addViolationWithMessage(data, decl,
					reportMsgFor(cuDecl.getPackageDeclaration().getPackageNameImage() + "." + classDecl.getImage()),
					decl.getBeginLine(), decl.getEndLine());
		}

		return super.visit(decl, data);
	}*/
	
	private String reportMsgFor(String className) {
		StringBuilder msg = new StringBuilder(getMessage()).append(": ");

		msg.append("POJO class found: " + className);

		return msg.toString();
	}
}
