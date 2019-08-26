package com.workday.techtest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class WorkStealingJobRunner implements JobRunner {
	//private final ExecutorService pool = Executors.newWorkStealingPool();
	private final ForkJoinPool pool = ForkJoinPool.commonPool();
	
	@Override
	public void runner(JobQueue jobQueue, long jobCount) {
		List<Job> jobList = new ArrayList<>();
		while(jobCount>0) {
			jobList.add(jobQueue.pop());
		}
		pool.invoke(new WorkStealingJobTask(jobList));
	}

	@Override
	public String version() {
		return "536543A4-4077-4672-B501-3520A49549E6";
	}

}
