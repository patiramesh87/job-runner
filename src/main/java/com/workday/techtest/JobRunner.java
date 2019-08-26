package com.workday.techtest;

public interface JobRunner {
	void runner(JobQueue jobQueue, long jobCount);
	String version();
}
