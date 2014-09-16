package cn.joy.framework.task;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskQueue {
	private static final int CORE_POOL_SIZE = 5;
	private static final int MAXIMUM_POOL_SIZE = 128;
	private static final int KEEP_ALIVE = 1;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "JoyTaskQueue #" + mCount.getAndIncrement());
		}
	};

	private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(10);

	private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
			KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

	private final Executor SERIAL_EXECUTOR = new SerialExecutor();

	private class SerialExecutor implements Executor {
		final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
		Runnable mActive;

		public synchronized void execute(final Runnable r) {
			mTasks.offer(new Runnable() {
				public void run() {
					try {
						r.run();
					} finally {
						scheduleNext();
					}
				}
			});
			if (mActive == null) {
				scheduleNext();
			}
		}

		protected synchronized void scheduleNext() {
			if ((mActive = mTasks.poll()) != null) {
				THREAD_POOL_EXECUTOR.execute(mActive);
			}
		}
	}

	public void execute(JoyTask task) {
		SERIAL_EXECUTOR.execute(task);
	}

}
