import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Implement a thread pool executor

public class MyThreadPoolExecutor {
	
	private static abstract class WorkItem<T> {
		protected T result_;
		abstract public T doWork();
		abstract public T getResult(T t);
	}
	
	private Queue<WorkItem<? extends Object>> jobQueue_;
	private List<Thread> threadPool_;
	private volatile boolean isShutdown_;
	
	public MyThreadPoolExecutor(int poolSize) {
		jobQueue_ = new LinkedList<WorkItem<? extends Object>>();
		threadPool_ = new LinkedList<Thread>();
		isShutdown_ = false;
		
		for (int i = 0; i < poolSize; i++) {
			Thread threadObj = initThread();
			threadPool_.add(threadObj);
			threadObj.start();
		}
	}
	
	private Thread initThread() {
		Thread thread = new Thread(() -> {
			WorkItem<? extends Object> workItem;
			while (true) {
				synchronized (jobQueue_) {
					while (!isShutdown_ && jobQueue_.isEmpty()) {
						try {
							//System.out.println("** Thread " + Thread.currentThread().getId() + " waiting for work");
							jobQueue_.wait(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if (isShutdown_) {
						System.out.println("** Thread " + Thread.currentThread().getId() + " shutting down.");
						break;
					}
					
					workItem = jobQueue_.poll();
				}
				
				if (workItem != null) {
					workItem.doWork();
					workItem = null;
				}
			}
		});
		
		return thread;
	}
	
	public void submit(WorkItem<? extends Object> workItem) {
		synchronized (jobQueue_) {
			jobQueue_.offer(workItem);
			jobQueue_.notifyAll();
		}
	}
	
	public void shutdown() {
		this.isShutdown_ = true;
	}
	
	public static WorkItem<? extends Object> createWorkItem() {
		WorkItem<Integer> item = new WorkItem<Integer>() {
			@Override
			public Integer doWork() {
				System.out.println("** Executed by :" + Thread.currentThread().getId());
				int x = 1 + 2;
				result_ = x;
				return x;
			}

			@Override
			public Integer getResult(Integer t) {
				return result_;
			}
		};
		
		return item;
	}

	public static void main(String[] args) {
		try {
			MyThreadPoolExecutor executor = new MyThreadPoolExecutor(2);
			
			// wait before submitting tasks
			Thread.currentThread().sleep(5000);
			
			List<WorkItem<? extends Object>> items = new ArrayList<WorkItem<? extends Object>>();
			for (int i = 0; i < 10; i++) {
				items.add(createWorkItem());
			}
			
			for (WorkItem<? extends Object> item: items) {
				executor.submit(item);
			}
			
			Thread.currentThread().sleep(5000);
			executor.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
