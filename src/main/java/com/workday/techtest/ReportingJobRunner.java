package com.workday.techtest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.workday.techtest.builder.ExecutionStatus;
import com.workday.techtest.builder.JobExecutionResult;
import com.workday.techtest.builder.ReportBuilder;

public class ReportingJobRunner extends AbstractReportingJobRunner {
	private int PARALLELISM=10;
	private int MIN_ALLOCATION_TIME=30;
	private final ExecutorService executorService = Executors.newWorkStealingPool(PARALLELISM);
	private final ReportBuilder reportBuilder = new ReportBuilder();
	private static final Logger LOGGER = Logger.getLogger(ReportingJobRunner.class.getName());
	
	@Override
	public String version() {
		return "536543A4-4077-4672-B501-3520A49549E6";
	}

	@Override
	public Report reportingRunner(JobQueue jobQueue, long jobCount) {
		List<ReportingTask> taskList = buildTaskList(jobQueue, jobCount);
		List<JobExecutionResult> executionResult=null;
		try {
			List<Future<Void>> futures = executorService.invokeAll(taskList);
			executionResult = extractResult(futures, taskList);
		} catch (InterruptedException ex
				) {
			LOGGER.log(Level.SEVERE, "Exception occur", ex);
		}
		return reportBuilder.buildJobRunnerReport(jobCount, executionResult);
	}

	private List<ReportingTask> buildTaskList(JobQueue jobQueue, long jobCount) {
		List<ReportingTask> taskList = new ArrayList<>();
		while(jobCount>0) {
			taskList.add(new ReportingTask(jobQueue.pop()));
			jobCount--;
		}
		return taskList;
	}
	
	private List<JobExecutionResult> extractResult(List<Future<Void>> futures, List<ReportingTask> taskList) {
		Instant firstInstant = Instant.now();
		List<JobExecutionResult> executionResult = new ArrayList<>();
		int i=0;
		ReportingTask reportTask=null;
		long timeElapsed;
		ExecutionStatus executionStatus=ExecutionStatus.FAILED;
		
		for(Future<Void> future : futures) {
			try {
				reportTask = taskList.get(i++);
				timeElapsed = Duration.between(firstInstant, Instant.now()).toMillis();
				
				if((reportTask.task.duration()+MIN_ALLOCATION_TIME)>timeElapsed) {
					future.get(reportTask.task.duration()-timeElapsed+MIN_ALLOCATION_TIME, TimeUnit.MILLISECONDS);
					executionStatus=ExecutionStatus.SUCCESS;
				} else if(future.isDone()){
					future.get();
					executionStatus=ExecutionStatus.SUCCESS;
				} else {
					executionStatus=ExecutionStatus.FAILED;
					future.cancel(true);
				}
			}catch (Exception ex) {
				executionStatus=ExecutionStatus.FAILED;
				future.cancel(true);
			} finally {
				executionResult.add(new JobExecutionResult(reportTask.task, executionStatus));
			}
			
		}
		
		return executionResult;
	}
	
	private class ReportingTask implements Callable<Void> {
		private Job task;
		private final Logger LOGGER = Logger.getLogger(ReportingTask.class.getName());
		
		public ReportingTask(Job task) {
			this.task = task;
		}

		@Override
		public Void call() throws Exception {
			LOGGER.info("Running job" + task.customerId() + "  " + task.uniqueId());
			task.execute();
			
			return null;
		}
		
	}
}
