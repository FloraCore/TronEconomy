package cn.watchdog.lib.asm.tree;

import cn.watchdog.lib.asm.MethodVisitor;
import cn.watchdog.lib.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A node that represents a stack map frame. These nodes are pseudo instruction nodes in order to be
 * inserted in an instruction list. In fact these nodes must(*) be inserted <i>just before</i> any
 * instruction node <b>i</b> that follows an unconditionnal branch instruction such as GOTO or
 * THROW, that is the target of a jump instruction, or that starts an exception handler block. The
 * stack map frame types must describe the values of the local variables and of the operand stack
 * elements <i>just before</i> <b>i</b> is executed. <br>
 * <br>
 * (*) this is mandatory only for classes whose version is greater than or equal to {@link
 * Opcodes#V1_6}.
 *
 * @author Eric Bruneton
 */
public class FrameNode extends AbstractInsnNode {

	/**
	 * The type of this frame. Must be {@link Opcodes#F_NEW} for expanded frames, or {@link
	 * Opcodes#F_FULL}, {@link Opcodes#F_APPEND}, {@link Opcodes#F_CHOP}, {@link Opcodes#F_SAME} or
	 * {@link Opcodes#F_APPEND}, {@link Opcodes#F_SAME1} for compressed frames.
	 */
	public int type;

	/**
	 * The types of the local variables of this stack map frame. Elements of this list can be Integer,
	 * String or LabelNode objects (for primitive, reference and uninitialized types respectively -
	 * see {@link MethodVisitor}).
	 */
	public List<Object> local;

	/**
	 * The types of the operand stack elements of this stack map frame. Elements of this list can be
	 * Integer, String or LabelNode objects (for primitive, reference and uninitialized types
	 * respectively - see {@link MethodVisitor}).
	 */
	public List<Object> stack;

	private FrameNode() {
		super(-1);
	}

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.FrameNode}.
	 *
	 * @param type     the type of this frame. Must be {@link Opcodes#F_NEW} for expanded frames, or
	 *                 {@link Opcodes#F_FULL}, {@link Opcodes#F_APPEND}, {@link Opcodes#F_CHOP}, {@link
	 *                 Opcodes#F_SAME} or {@link Opcodes#F_APPEND}, {@link Opcodes#F_SAME1} for compressed frames.
	 * @param numLocal number of local variables of this stack map frame. Long and double values count
	 *                 for one variable.
	 * @param local    the types of the local variables of this stack map frame. Elements of this list
	 *                 can be Integer, String or LabelNode objects (for primitive, reference and uninitialized
	 *                 types respectively - see {@link MethodVisitor}). Long and double values are represented by
	 *                 a single element.
	 * @param numStack number of operand stack elements of this stack map frame. Long and double
	 *                 values count for one stack element.
	 * @param stack    the types of the operand stack elements of this stack map frame. Elements of this
	 *                 list can be Integer, String or LabelNode objects (for primitive, reference and
	 *                 uninitialized types respectively - see {@link MethodVisitor}). Long and double values are
	 *                 represented by a single element.
	 */
	public FrameNode(
			final int type,
			final int numLocal,
			final Object[] local,
			final int numStack,
			final Object[] stack) {
		super(-1);
		this.type = type;
		switch (type) {
			case Opcodes.F_NEW:
			case Opcodes.F_FULL:
				this.local = Util.asArrayList(numLocal, local);
				this.stack = Util.asArrayList(numStack, stack);
				break;
			case Opcodes.F_APPEND:
				this.local = Util.asArrayList(numLocal, local);
				break;
			case Opcodes.F_CHOP:
				this.local = Util.asArrayList(numLocal);
				break;
			case Opcodes.F_SAME:
				break;
			case Opcodes.F_SAME1:
				this.stack = Util.asArrayList(1, stack);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	private static Object[] asArray(final List<Object> list) {
		Object[] array = new Object[list.size()];
		for (int i = 0, n = array.length; i < n; ++i) {
			Object o = list.get(i);
			if (o instanceof LabelNode) {
				o = ((LabelNode) o).getLabel();
			}
			array[i] = o;
		}
		return array;
	}

	@Override
	public int getType() {
		return FRAME;
	}

	@Override
	public void accept(final MethodVisitor methodVisitor) {
		switch (type) {
			case Opcodes.F_NEW:
			case Opcodes.F_FULL:
				methodVisitor.visitFrame(type, local.size(), asArray(local), stack.size(), asArray(stack));
				break;
			case Opcodes.F_APPEND:
				methodVisitor.visitFrame(type, local.size(), asArray(local), 0, null);
				break;
			case Opcodes.F_CHOP:
				methodVisitor.visitFrame(type, local.size(), null, 0, null);
				break;
			case Opcodes.F_SAME:
				methodVisitor.visitFrame(type, 0, null, 0, null);
				break;
			case Opcodes.F_SAME1:
				methodVisitor.visitFrame(type, 0, null, 1, asArray(stack));
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
		FrameNode clone = new FrameNode();
		clone.type = type;
		if (local != null) {
			clone.local = new ArrayList<>();
			for (int i = 0, n = local.size(); i < n; ++i) {
				Object localElement = local.get(i);
				if (localElement instanceof LabelNode) {
					localElement = clonedLabels.get(localElement);
				}
				clone.local.add(localElement);
			}
		}
		if (stack != null) {
			clone.stack = new ArrayList<>();
			for (int i = 0, n = stack.size(); i < n; ++i) {
				Object stackElement = stack.get(i);
				if (stackElement instanceof LabelNode) {
					stackElement = clonedLabels.get(stackElement);
				}
				clone.stack.add(stackElement);
			}
		}
		return clone;
	}
}
