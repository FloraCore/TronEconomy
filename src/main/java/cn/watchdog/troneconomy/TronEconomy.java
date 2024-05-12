package cn.watchdog.troneconomy;

import cn.watchdog.BukkitSchedulerAdapter;
import cn.watchdog.classpath.ClassPathAppender;
import cn.watchdog.classpath.ReflectionClassPathAppender;
import cn.watchdog.dependencies.Dependency;
import cn.watchdog.dependencies.DependencyManager;
import cn.watchdog.dependencies.DependencyManagerImpl;
import lombok.Getter;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class TronEconomy extends JavaPlugin {
	/**
	 * A scheduler adapter for the platform
	 */
	private final BukkitSchedulerAdapter schedulerAdapter;
	/**
	 * The plugin class path appender
	 */
	@Getter
	private final ClassPathAppender classPathAppender;
	// init during load
	@Getter
	private DependencyManager dependencyManager;
	private OkHttpClient httpClient;

	public TronEconomy() {
		this.schedulerAdapter = new BukkitSchedulerAdapter(this);
		this.classPathAppender = new ReflectionClassPathAppender(this);
	}

	@Override
	public void onLoad() {
		// load dependencies
		this.dependencyManager = createDependencyManager();
		this.dependencyManager.loadDependencies(getGlobalDependencies());
	}

	@Override
	public void onEnable() {
		// set up a byte bin instance
		this.httpClient = new OkHttpClient.Builder().callTimeout(15, TimeUnit.SECONDS).build();
	}

	@Override
	public void onDisable() {
		// shutdown okhttp
		this.httpClient.dispatcher().executorService().shutdown();
		this.httpClient.connectionPool().evictAll();

		// close isolated loaders for non-relocated dependencies
		getDependencyManager().close();
	}

	private DependencyManager createDependencyManager() {
		return new DependencyManagerImpl(this);
	}

	private Set<Dependency> getGlobalDependencies() {
		Set<Dependency> ret = EnumSet.of(
				Dependency.CAFFEINE,
				Dependency.OKIO,
				Dependency.OKHTTP
		);
		return ret;
	}

	private Path resolveConfig(String fileName) {
		Path configFile = getDataDirectory().toAbsolutePath().resolve(fileName);

		// if the config doesn't exist, create it based on the template in the resources' dir
		if (!Files.exists(configFile)) {
			try {
				Files.createDirectories(configFile.getParent());
			} catch (IOException e) {
				// ignore
			}

			try (InputStream is = getResourceStream(fileName)) {
				Files.copy(is, configFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return configFile;
	}

	private InputStream getResourceStream(String path) {
		return getClass().getClassLoader().getResourceAsStream(path);
	}

	public Path getDataDirectory() {
		return this.getDataFolder().toPath().toAbsolutePath();
	}

	public BukkitSchedulerAdapter getScheduler() {
		return this.schedulerAdapter;
	}

}
