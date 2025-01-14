package cn.watchdog.lib.asm.commons;

import cn.watchdog.lib.asm.Type;

import java.util.Collections;
import java.util.Map;

/**
 * A {@link Remapper} using a {@link java.util.Map} to define its mapping.
 *
 * @author Eugene Kuleshov
 */
public class SimpleRemapper extends Remapper {

	private final Map<String, String> mapping;

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.commons.SimpleRemapper} with the given mapping.
	 *
	 * @param mapping a map specifying a remapping as follows:
	 *                <ul>
	 *                  <li>for method names, the key is the owner, name and descriptor of the method (in the
	 *                      form &lt;owner&gt;.&lt;name&gt;&lt;descriptor&gt;), and the value is the new method
	 *                      name.
	 *                  <li>for invokedynamic method names, the key is the name and descriptor of the method (in
	 *                      the form .&lt;name&gt;&lt;descriptor&gt;), and the value is the new method name.
	 *                  <li>for field and attribute names, the key is the owner and name of the field or
	 *                      attribute (in the form &lt;owner&gt;.&lt;name&gt;), and the value is the new field
	 *                      name.
	 *                  <li>for internal names, the key is the old internal name, and the value is the new
	 *                      internal name (see {@link Type#getInternalName()}).
	 *                </ul>
	 */
	public SimpleRemapper(final Map<String, String> mapping) {
		this.mapping = mapping;
	}

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.commons.SimpleRemapper} with the given mapping.
	 *
	 * @param oldName the key corresponding to a method, field or internal name (see {@link
	 *                #SimpleRemapper(java.util.Map)} for the format of these keys).
	 * @param newName the new method, field or internal name (see {@link
	 *                Type#getInternalName()}).
	 */
	public SimpleRemapper(final String oldName, final String newName) {
		this.mapping = Collections.singletonMap(oldName, newName);
	}

	@Override
	public String mapMethodName(final String owner, final String name, final String descriptor) {
		String remappedName = map(owner + '.' + name + descriptor);
		return remappedName == null ? name : remappedName;
	}

	@Override
	public String mapInvokeDynamicMethodName(final String name, final String descriptor) {
		String remappedName = map('.' + name + descriptor);
		return remappedName == null ? name : remappedName;
	}

	@Override
	public String mapAnnotationAttributeName(final String descriptor, final String name) {
		String remappedName = map(descriptor + '.' + name);
		return remappedName == null ? name : remappedName;
	}

	@Override
	public String mapFieldName(final String owner, final String name, final String descriptor) {
		String remappedName = map(owner + '.' + name);
		return remappedName == null ? name : remappedName;
	}

	@Override
	public String map(final String key) {
		return mapping.get(key);
	}
}
