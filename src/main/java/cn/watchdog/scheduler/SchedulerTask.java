package cn.watchdog.scheduler;

/**
 * Represents a scheduled task
 */
public interface SchedulerTask {

	/**
	 * Cancels the task.
	 */
	void cancel();

}
