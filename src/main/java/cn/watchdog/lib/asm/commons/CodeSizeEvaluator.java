package cn.watchdog.lib.asm.commons;

import cn.watchdog.lib.asm.ConstantDynamic;
import cn.watchdog.lib.asm.Handle;
import cn.watchdog.lib.asm.Label;
import cn.watchdog.lib.asm.MethodVisitor;
import cn.watchdog.lib.asm.Opcodes;
import lombok.Getter;

/**
 * A {@link MethodVisitor} that approximates the size of the methods it visits.
 *
 * @author Eugene Kuleshov
 */
@Getter
public class CodeSizeEvaluator extends MethodVisitor implements Opcodes {

	/**
	 * The minimum size in bytes of the visited method.
	 */
	private int minSize;

	/**
	 * The maximum size in bytes of the visited method.
	 */
	private int maxSize;

	public CodeSizeEvaluator(final MethodVisitor methodVisitor) {
		this(/* latest api = */ Opcodes.ASM9, methodVisitor);
	}

	protected CodeSizeEvaluator(final int api, final MethodVisitor methodVisitor) {
		super(api, methodVisitor);
	}

	@Override
	public void visitInsn(final int opcode) {
		minSize += 1;
		maxSize += 1;
		super.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(final int opcode, final int operand) {
		if (opcode == SIPUSH) {
			minSize += 3;
			maxSize += 3;
		} else {
			minSize += 2;
			maxSize += 2;
		}
		super.visitIntInsn(opcode, operand);
	}

	@Override
	public void visitVarInsn(final int opcode, final int varIndex) {
		if (varIndex < 4 && opcode != RET) {
			minSize += 1;
			maxSize += 1;
		} else if (varIndex >= 256) {
			minSize += 4;
			maxSize += 4;
		} else {
			minSize += 2;
			maxSize += 2;
		}
		super.visitVarInsn(opcode, varIndex);
	}

	@Override
	public void visitTypeInsn(final int opcode, final String type) {
		minSize += 3;
		maxSize += 3;
		super.visitTypeInsn(opcode, type);
	}

	@Override
	public void visitFieldInsn(
			final int opcode, final String owner, final String name, final String descriptor) {
		minSize += 3;
		maxSize += 3;
		super.visitFieldInsn(opcode, owner, name, descriptor);
	}

	@Override
	public void visitMethodInsn(
			final int opcodeAndSource,
			final String owner,
			final String name,
			final String descriptor,
			final boolean isInterface) {
		if (api < Opcodes.ASM5 && (opcodeAndSource & Opcodes.SOURCE_DEPRECATED) == 0) {
			// Redirect the call to the deprecated version of this method.
			super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
			return;
		}
		int opcode = opcodeAndSource & ~Opcodes.SOURCE_MASK;

		if (opcode == INVOKEINTERFACE) {
			minSize += 5;
			maxSize += 5;
		} else {
			minSize += 3;
			maxSize += 3;
		}
		super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
	}

	@Override
	public void visitInvokeDynamicInsn(
			final String name,
			final String descriptor,
			final Handle bootstrapMethodHandle,
			final Object... bootstrapMethodArguments) {
		minSize += 5;
		maxSize += 5;
		super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
	}

	@Override
	public void visitJumpInsn(final int opcode, final Label label) {
		minSize += 3;
		if (opcode == GOTO || opcode == JSR) {
			maxSize += 5;
		} else {
			maxSize += 8;
		}
		super.visitJumpInsn(opcode, label);
	}

	@Override
	public void visitLdcInsn(final Object value) {
		if (value instanceof Long
				|| value instanceof Double
				|| (value instanceof ConstantDynamic && ((ConstantDynamic) value).getSize() == 2)) {
			minSize += 3;
		} else {
			minSize += 2;
		}
		maxSize += 3;
		super.visitLdcInsn(value);
	}

	@Override
	public void visitIincInsn(final int varIndex, final int increment) {
		if (varIndex > 255 || increment > 127 || increment < -128) {
			minSize += 6;
			maxSize += 6;
		} else {
			minSize += 3;
			maxSize += 3;
		}
		super.visitIincInsn(varIndex, increment);
	}

	@Override
	public void visitTableSwitchInsn(
			final int min, final int max, final Label dflt, final Label... labels) {
		minSize += 13 + labels.length * 4;
		maxSize += 16 + labels.length * 4;
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}

	@Override
	public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
		minSize += 9 + keys.length * 8;
		maxSize += 12 + keys.length * 8;
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

	@Override
	public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
		minSize += 4;
		maxSize += 4;
		super.visitMultiANewArrayInsn(descriptor, numDimensions);
	}
}
