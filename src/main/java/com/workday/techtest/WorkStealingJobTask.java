package com.workday.techtest;

import java.util.List;
import java.util.concurrent.RecursiveAction;

public class WorkStealingJobTask extends RecursiveAction {
	
	private static final long serialVersionUID = -4946522709560843668L;
	private List<Job> jobList;
	private static int SEQUENTIAL_THRESHOLD = 2;
	
	public WorkStealingJobTask(List<Job> jobList) {
		this.jobList = jobList;
	}

	@Override
	protected void compute() {
		int jobCount = jobList.size();
		
        if (jobCount <= SEQUENTIAL_THRESHOLD) {
        	for(Job job:jobList) {
        		job.execute();
        	}
        } else {
        	int middle = jobCount/2;
            List<Job> newList = jobList.subList(middle, jobCount);
            jobList = jobList.subList(0, middle);
            WorkStealingJobTask task = new WorkStealingJobTask(newList);
            task.fork();
            this.compute();
        }
	}

}
