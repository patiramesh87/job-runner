package com.workday.techtest.builder;

import java.util.ArrayList;
import java.util.List;

import com.workday.techtest.Report;

public class ReportBuilder {
	public Report buildJobRunnerReport(long jobCount, List<JobExecutionResult> executionResult) {
		if(executionResult == null)
			executionResult = new ArrayList<>();
		
		Report report = new Report();
		report.setExecutionResult(executionResult);
		report.setTotalJobsExecuted(executionResult.size());
		report.setTotalJobsInRequest(jobCount);
		
		return report;
		
	}
}
