package com.workday.techtest;

public class ReportRunnerTask implements Runnable{
	private JobQueue jobQueue;
	private Job job;
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
