package edu.cmu.lti.util.system;

import java.util.LinkedList;

public class ThreadPool {

	public LinkedList<Job> queue_ = new LinkedList<Job>();

	public class Job {
		public int id_;
		public String key_ = null;
		public Object data_ = null;

		public Job(int id, String key) {
			this(id, key, null);
		}

		public Job(int id, String key, Object data) {
			this.id_ = id;
			this.key_ = key;
			this.data_ = data;
		}

		public String toString() {
			return id_ + ")" + key_;
		}
	}

	public void addJob(String line) {
		addJob(new Job(-1, line, null));
	}
	
	public void addJob(int id) {
		addJob(new Job(id, null, null));
	}
	
	public void clearData() {
		for (WorkThread thread:  threads_) thread.clearData();
	}

	
	public class WorkThread extends Thread {
		public int id_;

		public void clearData() {
			
		}
		public WorkThread(int id) {//, Net1 net){
			this.id_ = id;
			this.start();
		}
		public void workOnJob(Job job) {
			System.out.println("thread " + id_ + " works on data " + job.id_);
			System.out.flush();
		}

		public void run() {

			while (true) {
				Job job;
				synchronized (queue_) {
					while (queue_.isEmpty()) {
						try {
							queue_.wait();
						} catch (InterruptedException ignored) {
							return;
						}
					}
					job = queue_.removeFirst();
					++num_working_threads_;
					queue_.notifyAll();
				}

				try {		// If we don't catch RuntimeException, the pool could leak threads
					workOnJob(job);
				} catch (RuntimeException e) {
					System.err.println("exception at thread=" + this.id_);
					System.err.println(e);
					e.printStackTrace();
					System.exit(-1);
				}

				synchronized (queue_) { //am done
					--num_working_threads_;
					queue_.notifyAll();
				}

			}
		}
	}

	boolean done = false;

	public WorkThread[] threads_ = null;
	public Integer num_working_threads_ = 0;

	public void startThreads(int num_threads) {
		System.out.println("\n" + num_threads + " threads started");

		threads_ = new WorkThread[num_threads];
		for (int i = 0; i < threads_.length; ++i)
			threads_[i] = newWorkThread(i);
	}

	// stub for subclassing
	public WorkThread newWorkThread(int i) {
		return new WorkThread(i);
	}

	public void addJob(Job job) {
		synchronized (queue_) {
			queue_.add(job);
			queue_.notify();
		}
	}

	public void sendNullJobs(int num_jobs) {//for testing purpose
		for (int i = 0; i < num_jobs; ++i) addJob(new Job(i, null));
	}

	public void killThreads() {//let threads kill themselves
		for (int i = 0; i < threads_.length; ++i) {
			threads_[i].interrupt();
		}
	}

	public void waitJobs() {
		//System.out.println("waitJobs()");
		while (true) {
			synchronized (queue_) {
				if (num_working_threads_ == 0 && queue_.size() == 0) return;
				try {
					queue_.wait(1000);
				} catch (InterruptedException ignored) {
					System.err.println("InterruptedException");
					return;
				}
			}
		}
	}

	//wait until queue size is smaller than n
	public void waitQueue(int n) {
		while (true) {
			synchronized (queue_) {
				if (queue_.size() < n) return;
				try {
					queue_.wait(1000);
				} catch (InterruptedException ignored) {
					System.err.println("InterruptedException");
					return;
				}
			}
		}
	}

	public void waitWorker() {
		while (true) {
			synchronized (queue_) {
				if (num_working_threads_ < threads_.length) return;
				try {
					queue_.wait();
				} catch (InterruptedException ignored) {
					return;
				}
			}
		}
	}

	public void test() {
		startThreads(7);
		FSystem.sleep(1000);
		sendNullJobs(100);
		//FSystem.sleep(10000000);
		waitJobs();
		killThreads();
		System.out.println("done");
	}

	public static void main(String[] args) {
		new ThreadPool().test();
	}
}
