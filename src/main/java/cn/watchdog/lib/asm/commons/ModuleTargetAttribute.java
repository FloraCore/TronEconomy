package cn.watchdog.lib.asm.commons;

import cn.watchdog.lib.asm.Attribute;
import cn.watchdog.lib.asm.ByteVector;
import cn.watchdog.lib.asm.ClassReader;
import cn.watchdog.lib.asm.ClassVisitor;
import cn.watchdog.lib.asm.ClassWriter;
import cn.watchdog.lib.asm.Label;

/**
 * A ModuleTarget attribute. This attribute is specific to the OpenJDK and may change in the future.
 *
 * @author Remi Forax
 */
public final class ModuleTargetAttribute extends Attribute {

	/**
	 * The name of the platform on which the module can run.
	 */
	public String platform;

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.commons.ModuleTargetAttribute}.
	 *
	 * @param platform the name of the platform on which the module can run.
	 */
	public ModuleTargetAttribute(final String platform) {
		super("ModuleTarget");
		this.platform = platform;
	}

	/**
	 * Constructs an empty {@link cn.watchdog.lib.asm.commons.ModuleTargetAttribute}. This object can be passed as a prototype to
	 * the {@link ClassReader#accept(ClassVisitor, Attribute[], int)} method.
	 */
	public ModuleTargetAttribute() {
		this(null);
	}

	@Override
	protected Attribute read(
			final ClassReader classReader,
			final int offset,
			final int length,
			final char[] charBuffer,
			final int codeOffset,
			final Label[] labels) {
		return new ModuleTargetAttribute(classReader.readUTF8(offset, charBuffer));
	}

	@Override
	protected ByteVector write(
			final ClassWriter classWriter,
			final byte[] code,
			final int codeLength,
			final int maxStack,
			final int maxLocals) {
		ByteVector byteVector = new ByteVector();
		byteVector.putShort(platform == null ? 0 : classWriter.newUTF8(platform));
		return byteVector;
	}
}
