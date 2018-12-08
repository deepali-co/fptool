package com.myapps.fptool.reports;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.myapps.fptool.jira.model.JiraIssue;
import com.myapps.fptool.utils.FPToolHelper;

public class GenerateReport {

	private String fileName;
	private String fileType;
	private String filePath;

	public GenerateReport(String filePath, String fileName, String fileType) {
		super();
		System.out.println("Initializing Generate Report");
		this.fileName = fileName;
		this.fileType = fileType;
		this.filePath = filePath;
		System.out.println("Generate Report Initialized");
	}

	public void writeExcelReportForFP(List<JiraIssue> issues) {
		List<String> headers = new ArrayList<>();

		headers.add("Issue Id");
		headers.add("Issue Key");
		headers.add("Project");
		headers.add("Company");
		headers.add("Software Product Name");
		headers.add("Summary");
		headers.add("Assignee");
		headers.add("Status");
		headers.add("Scm Url");
		headers.add("Scm Branch");
		headers.add("Effected LoC");
		headers.add("FP Reason");

		try {
			System.out.println("Begining writeExcelReportForFP");
			String reportFile = filePath + "/" + fileName + "." + fileType;
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("FPReport");
			short rownum = 0;

			HSSFRow rowhead = sheet.createRow(rownum++);

			int idx = 0;
			System.out.println("Begining Headers Processing");
			for (String header : headers) {
				rowhead.createCell(idx++).setCellValue(header);
			}

			System.out.println("Begining JiraIssue Processing");
			for (JiraIssue jiraIssue : issues) {
				for (String violation : jiraIssue.getViolations()) {
					idx = 0;
					HSSFRow row = sheet.createRow(rownum++);
					row.createCell(idx++).setCellValue(jiraIssue.getIssueId());
					row.createCell(idx++).setCellValue(jiraIssue.getIssueKey());
					row.createCell(idx++).setCellValue(jiraIssue.getProject());
					row.createCell(idx++).setCellValue(jiraIssue.getCompany());
					row.createCell(idx++).setCellValue(jiraIssue.getSoftwareProductName());
					row.createCell(idx++).setCellValue(jiraIssue.getSummary());
					row.createCell(idx++).setCellValue(jiraIssue.getAssignee());
					row.createCell(idx++).setCellValue(jiraIssue.getStatus());
					row.createCell(idx++).setCellValue(jiraIssue.getScmUrl());
					row.createCell(idx++).setCellValue(jiraIssue.getScmBranch());
					row.createCell(idx++).setCellValue(jiraIssue.getEffectedLoc());
					row.createCell(idx++).setCellValue(violation);
				}
			}
			System.out.println("Begining Report Writing");
			FPToolHelper.createReportFile(reportFile);
			try (FileOutputStream fileOut = new FileOutputStream(reportFile)) {
				workbook.write(fileOut);
				workbook.close();
			}
			System.out.println("Report Generated!");
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}
}
