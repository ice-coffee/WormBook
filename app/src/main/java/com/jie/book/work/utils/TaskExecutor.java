package com.jie.book.work.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// an asynchronous task executor(thread pool)
public class TaskExecutor {
	public static final int THREAD_COUNT = 15;
	private static TaskExecutor instance;
	private ExecutorService service = null;

	private TaskExecutor() {
		service = Executors.newFixedThreadPool(THREAD_COUNT);
	}

	public static TaskExecutor getInstance() {
		if (instance == null) {
			instance = new TaskExecutor();
		}
		return instance;
	}

	public void executeTask(Runnable task) {
		service.execute(task);
	}

	public <T> Future<T> submitTask(Callable<T> task) {
		return service.submit(task);
	}

	public void shutdown() {
		if (service != null) {
			service.shutdown();
			service = null;
		}
	}

}