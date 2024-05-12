package cn.watchdog.troneconomy.storage.implementation.custom;

/**
 * Hook to allow external code to provide a storage implementation
 */
public final class CustomStorageProviders {
	private static CustomStorageProvider provider = null;

	private CustomStorageProviders() {}

	public static void register(CustomStorageProvider provider) {
		CustomStorageProviders.provider = provider;
	}

	public static CustomStorageProvider getProvider() {
		if (provider == null) {
			throw new IllegalStateException("Provider not present.");
		}

		return provider;
	}

}