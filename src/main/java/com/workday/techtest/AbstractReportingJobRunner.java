package com.workday.techtest;

public abstract class AbstractReportingJobRunner implements JobRunner {
	public abstract Report reportingRunner(JobQueue jobQueue, long jobCount);

	@Override
	public void runner(JobQueue jobQueue, long jobCount) {
		reportingRunner(jobQueue, jobCount);
	}

}
