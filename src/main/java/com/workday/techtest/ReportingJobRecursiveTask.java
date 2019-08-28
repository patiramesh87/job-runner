package com.workday.techtest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import com.workday.techtest.builder.ExecutionStatus;
import com.workday.techtest.builder.JobExecutionResult;

public class ReportingJobRecursiveTask extends RecursiveTask<List<JobExecutionResult>> {
	
	private static final long serialVersionUID = -4946522709560843668L;
	private static final int MIN_ALLOCATION_TIME = 30;
	private static int SEQUENTIAL_THRESHOLD = 5;
	private JobQueue jobQueue; 
	private long jobCount;
	
	public ReportingJobRecursiveTask(JobQueue jobQueue, long jobCount) {
		this.jobQueue = jobQueue;
		this.jobCount = jobCount;
	}

	@Override
	protected List<JobExecutionResult> compute() {
		List<JobExecutionResult> executionResult = new LinkedList<>();
		if (jobCount <= SEQUENTIAL_THRESHOLD) {
			executionResult.addAll(executeTasks());
		} else {
			System.out.println("Splitting workLoad : " + this.jobCount);

			ReportingJobRecursiveTask subtask = createSubtask();
             subtask.fork();
             
             executionResult.addAll(this.compute());
             executionResult.addAll(subtask.join());
		}
		
		return executionResult;
	}
	
	private List<JobExecutionResult> executeTasks() {
		List<CompletableFuture<Void>> futureList = new ArrayList<>((int)jobCount);
		List<ReportRunnerTask> taskList = new ArrayList<>((int)jobCount);;
		
		CompletableFuture<Void> future;
		ReportRunnerTask task;
		
		for(int i=0;i<jobCount;i++) {
			task = new ReportRunnerTask(jobQueue);
			future = CompletableFuture.runAsync(task);
			futureList.add(future);
			taskList.add(task);
		}
		
		return extractResult(futureList, taskList);
	}
	
	private List<JobExecutionResult> extractResult(List<CompletableFuture<Void>> futureList, List<ReportRunnerTask> taskList) {
		Instant firstInstant = Instant.now();
		List<JobExecutionResult> executionResult = new ArrayList<>();
		int i=0;
		ReportRunnerTask reportTask=null;
		long timeElapsed;
		ExecutionStatus executionStatus=ExecutionStatus.FAILED;
		Job job = null;
		
		for(CompletableFuture<Void> future : futureList) {
			try {
				reportTask = taskList.get(i++);
				timeElapsed = Duration.between(firstInstant, Instant.now()).toMillis();
				job = reportTask.getJob();
				executionStatus=ExecutionStatus.FAILED;
				
				if(job != null && (job.duration()+MIN_ALLOCATION_TIME)>timeElapsed) {
					future.get(job.duration()-timeElapsed+MIN_ALLOCATION_TIME, TimeUnit.MILLISECONDS);
					executionStatus=ExecutionStatus.SUCCESS;
				} else if(future.isDone()){
					future.get();
					executionStatus=ExecutionStatus.SUCCESS;
				} else {
					while(!reportTask.executionStarted) {}
					job = reportTask.getJob();
					int waitingTime = (job==null) ? MIN_ALLOCATION_TIME : MIN_ALLOCATION_TIME+job.duration();
					future.get(waitingTime, TimeUnit.MILLISECONDS);
					executionStatus=ExecutionStatus.SUCCESS;
				}
			}catch (Exception ex) {
				executionStatus=ExecutionStatus.FAILED;
				future.completeExceptionally(new RuntimeException());
				future.cancel(true);
			} finally {
				if(job !=null)
					executionResult.add(new JobExecutionResult(job, executionStatus));
			}
			
		}
		
		return executionResult;
	}

	private ReportingJobRecursiveTask createSubtask() {
		long mid = jobCount/2;
		this.jobCount = jobCount - mid;
        return new ReportingJobRecursiveTask(jobQueue, mid);
    }
	
	private class ReportRunnerTask implements Runnable{
		private JobQueue jobQueue;
		private Job job=null;
		private boolean executionStarted;
		
		public ReportRunnerTask(JobQueue jobQueue) {
			this.jobQueue = jobQueue;
		}
		
		public Job getJob() {
			return job;
		}

		public boolean hasExecutionStarted() {
			return executionStarted;
		}

		@Override
		public void run() {
			executionStarted=true;
			job = jobQueue.pop();
			if(job != null)
				job.execute();
		}
	}

}
