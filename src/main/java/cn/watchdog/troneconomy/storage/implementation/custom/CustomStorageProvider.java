package cn.watchdog.troneconomy.storage.implementation.custom;

import cn.watchdog.troneconomy.TronEconomy;
import cn.watchdog.troneconomy.storage.implementation.StorageImplementation;

/**
 * A storage provider
 */
@FunctionalInterface
public interface CustomStorageProvider {

	StorageImplementation provide(TronEconomy plugin);

}