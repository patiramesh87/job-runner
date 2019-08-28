package com.workday.techtest;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

import com.workday.techtest.builder.JobExecutionResult;
import com.workday.techtest.builder.ReportBuilder;

public class ReportingJobRunner extends AbstractReportingJobRunner {
	private int PARALLELISM=15;
	//private final ForkJoinPool pool = (ForkJoinPool) Executors.newWorkStealingPool(PARALLELISM);
	private final ForkJoinPool pool = new ForkJoinPool(PARALLELISM);//ForkJoinPool.commonPool();
	
	private final ReportBuilder reportBuilder = new ReportBuilder();
	private static final Logger LOGGER = Logger.getLogger(ReportingJobRunner.class.getName());
	
	@Override
	public String version() {
		return "536543A4-4077-4672-B501-3520A49549E6";
	}

	@Override
	public Report reportingRunner(JobQueue jobQueue, long jobCount) {
		List<JobExecutionResult> executionResult = pool.invoke(new ReportingJobRecursiveTask(jobQueue, jobCount));
		return reportBuilder.buildJobRunnerReport(jobCount, executionResult);
	}

}
