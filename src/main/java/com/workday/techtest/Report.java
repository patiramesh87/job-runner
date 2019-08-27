package com.workday.techtest;

import java.util.List;

import com.workday.techtest.builder.JobExecutionResult;

public class Report {
	private long totalJobsInRequest;
	private int totalJobsExecuted;
	private List<JobExecutionResult> executionResult;
	
	public long getTotalJobsInRequest() {
		return totalJobsInRequest;
	}


	public void setTotalJobsInRequest(long totalJobInRequest) {
		this.totalJobsInRequest = totalJobInRequest;
	}


	public int getTotalJobsExecuted() {
		return totalJobsExecuted;
	}


	public void setTotalJobsExecuted(int totalJobExecuted) {
		this.totalJobsExecuted = totalJobExecuted;
	}


	public List<JobExecutionResult> getExecutionResult() {
		return executionResult;
	}


	public void setExecutionResult(List<JobExecutionResult> executionResult) {
		this.executionResult = executionResult;
	}


	@Override
	public String toString() {
		return "Report [totalJobExecuted=" + totalJobsExecuted + ", executionResult=" + executionResult + "]";
	}
	
}
