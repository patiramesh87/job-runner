package com.workday.techtest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.workday.techtest.builder.JobExecutionResult;
import com.workday.techtest.builder.ReportBuilder;

public class ReportingJobRunner extends AbstractReportingJobRunner {
	private int PARALLELISM=15;
	private final ForkJoinPool pool = (ForkJoinPool) Executors.newWorkStealingPool(PARALLELISM);
	//private final ForkJoinPool pool = ForkJoinPool.commonPool();
	
	private final ReportBuilder reportBuilder = new ReportBuilder();
	private static final Logger LOGGER = Logger.getLogger(ReportingJobRunner.class.getName());
	
	@Override
	public String version() {
		return "536543A4-4077-4672-B501-3520A49549E6";
	}

	@Override
	public Report reportingRunner(JobQueue jobQueue, long jobCount) {
		LOGGER.log(Level.INFO, "started executing runner with " + jobCount);
		if(jobCount == 0)
			return reportBuilder.buildJobRunnerReport(jobCount, new ArrayList<>());
		try {
			List<JobExecutionResult> executionResult = pool.invoke(new ReportingJobRecursiveTask(jobQueue, jobCount));
			return reportBuilder.buildJobRunnerReport(jobCount, executionResult);
		}catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Error during excecution " + ex);
			throw new JobExecutionException(ex.getMessage());
		}
	}

}
