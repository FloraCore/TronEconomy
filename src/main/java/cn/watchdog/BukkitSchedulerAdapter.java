package cn.watchdog;

import cn.watchdog.scheduler.AbstractJavaScheduler;
import cn.watchdog.scheduler.SchedulerAdapter;
import cn.watchdog.troneconomy.TronEconomy;

import java.util.concurrent.Executor;

public class BukkitSchedulerAdapter extends AbstractJavaScheduler implements SchedulerAdapter {
	private final Executor sync;

	public BukkitSchedulerAdapter(TronEconomy bootstrap) {
		super(bootstrap);
		this.sync = r -> bootstrap.getServer().getScheduler().scheduleSyncDelayedTask(bootstrap, r);
	}

	@Override
	public Executor sync() {
		return this.sync;
	}

}
