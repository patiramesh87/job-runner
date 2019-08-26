package com.workday.techtest;

public abstract class ReportingJobRunner implements JobRunner {
	public abstract Report reportingRunner(JobQueue jobQueue, long jobCount);

	@Override
	public void runner(JobQueue jobQueue, long jobCount) {
		reportingRunner(jobQueue, jobCount);
	}

	@Override
	public String version() {
		return "536543A4-4077-4672-B501-3520A49549E6";
	}
}
