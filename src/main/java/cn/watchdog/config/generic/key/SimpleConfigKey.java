package cn.watchdog.config.generic.key;

import cn.watchdog.config.generic.adapter.ConfigurationAdapter;
import lombok.Setter;

import java.util.function.Function;

/**
 * Basic {@link ConfigKey} implementation.
 *
 * @param <T> the value type
 */
public class SimpleConfigKey<T> implements ConfigKey<T> {
	private final Function<? super ConfigurationAdapter, ? extends T> function;

	@Setter
	private int ordinal = -1;
	@Setter
	private boolean reloadable = true;

	SimpleConfigKey(Function<? super ConfigurationAdapter, ? extends T> function) {
		this.function = function;
	}

	@Override
	public int ordinal() {
		return this.ordinal;
	}

	@Override
	public boolean reloadable() {
		return this.reloadable;
	}

	@Override
	public T get(ConfigurationAdapter adapter) {
		return this.function.apply(adapter);
	}

}
