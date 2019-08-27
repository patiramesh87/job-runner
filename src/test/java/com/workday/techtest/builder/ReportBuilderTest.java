package com.workday.techtest.builder;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.workday.techtest.Job;
import com.workday.techtest.Report;

public class ReportBuilderTest {
	private ReportBuilder reportBuilder = new ReportBuilder();
	
	@Test
	public void testBuildJobRunnerReport() {
		List<JobExecutionResult> executionResult = new ArrayList<>();
		executionResult.add(new JobExecutionResult(new TestJob(), ExecutionStatus.SUCCESS));
		executionResult.add(new JobExecutionResult(new TestJob(), ExecutionStatus.FAILED));
		executionResult.add(new JobExecutionResult(new TestJob(), ExecutionStatus.SUCCESS));
		Report report = reportBuilder.buildJobRunnerReport(5, executionResult);
		
		assertEquals(5, report.getTotalJobsInRequest());
		assertEquals(3, report.getTotalJobsExecuted());
		assertEquals(ExecutionStatus.FAILED.name(), report.getExecutionResult().get(1).getExecutionStatus());
		assertEquals(ExecutionStatus.SUCCESS.name(), report.getExecutionResult().get(0).getExecutionStatus());
	}
	
	@Test
	public void testBuildJobRunnerReportWhenNoJobsInQueue() {
		List<JobExecutionResult> executionResult = new ArrayList<>();
		
		Report report = reportBuilder.buildJobRunnerReport(5, executionResult);
		
		assertEquals(5, report.getTotalJobsInRequest());
		assertEquals(0, report.getTotalJobsExecuted());
	}
	
	class TestJob implements Job{

		@Override
		public long customerId() {
			return 0;
		}

		@Override
		public long uniqueId() {
			return 0;
		}

		@Override
		public int duration() {
			return 0;
		}

		@Override
		public void execute() {
			
		}
		
	}
	
}
