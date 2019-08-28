package com.workday.techtest;

import static org.junit.Assert.*;

import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Test;

import com.workday.techtest.builder.ExecutionStatus;

public class ReportingJobRunnerTest {
	
	private ReportingJobRunner reportingJobRunner = new ReportingJobRunner();
	
	@Test
	public void testRunnerDuringSuccessExceptionAndBlockingDuringExecute() {
		TestjobQueue queue = new TestjobQueue();
		try {
			TestJob job;
			queue.enque(new TestJob(9, 20));
			
			job = new TestJob(5, 200);
			job.setCanSleep(true);
			queue.enque(job);
			
			job = new TestJob(10, 25);
			job.setCanThrowExpn(true);
			queue.enque(job);
			
		} catch (InterruptedException e) {}
		Report report = reportingJobRunner.reportingRunner(queue, 4);
		assertEquals(4, report.getTotalJobsInRequest());
		assertEquals(3, report.getTotalJobsExecuted());
		assertEquals(ExecutionStatus.FAILED.name(), report.getExecutionResult().get(2).getExecutionStatus());
		assertEquals(ExecutionStatus.SUCCESS.name(), report.getExecutionResult().get(1).getExecutionStatus());
		assertEquals(ExecutionStatus.SUCCESS.name(), report.getExecutionResult().get(0).getExecutionStatus());
	}
	
	@Test
	public void testRunnerDuringSuccessAndSleepDuringExecute() {
		TestjobQueue queue = new TestjobQueue();
		try {
			TestJob job;
			queue.enque(new TestJob(9, 50));
			
			job = new TestJob(5, 200);
			job.setCanSleep(true);
			queue.enque(job);
			
			job = new TestJob(10, 600);
			job.setInfinite(true);
			queue.enque(job);
			
		} catch (InterruptedException e) {}
		Report report = reportingJobRunner.reportingRunner(queue, 3);
		assertEquals(3, report.getTotalJobsInRequest());
		assertEquals(3, report.getTotalJobsExecuted());
		assertEquals(ExecutionStatus.FAILED.name(), report.getExecutionResult().get(2).getExecutionStatus());
		assertEquals(ExecutionStatus.SUCCESS.name(), report.getExecutionResult().get(1).getExecutionStatus());
		assertEquals(ExecutionStatus.SUCCESS.name(), report.getExecutionResult().get(0).getExecutionStatus());
	}
	
	class TestjobQueue implements JobQueue{
		private ArrayBlockingQueue<Job> queue = new ArrayBlockingQueue<>(4);
		
		public void enque(Job job) throws InterruptedException {
			queue.put(job);
		}

		@Override
		public Job pop() {
			try {
				return queue.take();
			} catch (InterruptedException e) {
				return null;
			}
		}
		
	}
	class TestJob implements Job{
		private int number;
		private int duration;
		private boolean isInfinite;
		private boolean canSleep;
		private boolean canThrowExpn;
		
		public TestJob(int number, int duration) {
			this.number = number;
			this.duration = duration;
		}
		
		public void setCanThrowExpn(boolean canThrowExpn) {
			this.canThrowExpn = canThrowExpn;
		}

		public void setInfinite(boolean isInfinite) {
			this.isInfinite = isInfinite;
		}

		public void setCanSleep(boolean canSleep) {
			this.canSleep = canSleep;
		}

		@Override
		public long customerId() {
			return 111;
		}

		@Override
		public long uniqueId() {
			return 121;
		}

		@Override
		public int duration() {
			return duration;
		}

		@Override
		public void execute() {
			if(canThrowExpn) {
				throw new RuntimeException("thrown explicitly");
			}
			
			if(canSleep) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					
				}
			}
			
			if(isInfinite) {
				while(true) {
					System.out.println("infinitely executing");
				}
			}
			for(int i=0;i<number*number;i++);
				System.out.println("Finitely executing");
		}
		
	}
}
