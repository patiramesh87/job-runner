package com.workday.techtest.builder;

import com.workday.techtest.Job;

public class JobExecutionResult {
	private long customerId;
	private long uniqueId;
	private String executionStatus;
	
	public JobExecutionResult(Job job, ExecutionStatus executionStatus) {
		this.customerId = job.customerId();
		this.uniqueId = job.uniqueId();
		this.executionStatus = executionStatus.name();
	}
	
	public long getCustomerId() {
		return customerId;
	}
	public long getUniqueId() {
		return uniqueId;
	}
	public String getExecutionStatus() {
		return executionStatus;
	}

	@Override
	public String toString() {
		return "JobExecutionResult [customerId=" + customerId + ", uniqueId=" + uniqueId + ", executionStatus="
				+ executionStatus + "]";
	}
	
}
