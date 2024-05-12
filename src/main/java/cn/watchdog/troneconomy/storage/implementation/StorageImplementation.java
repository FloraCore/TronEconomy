package cn.watchdog.troneconomy.storage.implementation;

import cn.watchdog.troneconomy.TronEconomy;
import cn.watchdog.troneconomy.storage.StorageMetadata;

public interface StorageImplementation {
	TronEconomy getPlugin();

	String getImplementationName();

	void init() throws Exception;

	void shutdown();

	StorageMetadata getMeta();
}
