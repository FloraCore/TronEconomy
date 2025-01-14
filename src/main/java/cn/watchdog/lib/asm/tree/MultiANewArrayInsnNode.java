package cn.watchdog.lib.asm.tree;

import cn.watchdog.lib.asm.MethodVisitor;
import cn.watchdog.lib.asm.Opcodes;
import cn.watchdog.lib.asm.Type;

import java.util.Map;

/**
 * A node that represents a MULTIANEWARRAY instruction.
 *
 * @author Eric Bruneton
 */
public class MultiANewArrayInsnNode extends AbstractInsnNode {

	/**
	 * An array type descriptor (see {@link Type}).
	 */
	public String desc;

	/**
	 * Number of dimensions of the array to allocate.
	 */
	public int dims;

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.MultiANewArrayInsnNode}.
	 *
	 * @param descriptor    an array type descriptor (see {@link Type}).
	 * @param numDimensions the number of dimensions of the array to allocate.
	 */
	public MultiANewArrayInsnNode(final String descriptor, final int numDimensions) {
		super(Opcodes.MULTIANEWARRAY);
		this.desc = descriptor;
		this.dims = numDimensions;
	}

	@Override
	public int getType() {
		return MULTIANEWARRAY_INSN;
	}

	@Override
	public void accept(final MethodVisitor methodVisitor) {
		methodVisitor.visitMultiANewArrayInsn(desc, dims);
		acceptAnnotations(methodVisitor);
	}

	@Override
	public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
		return new MultiANewArrayInsnNode(desc, dims).cloneAnnotations(this);
	}
}
