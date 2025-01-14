package cn.watchdog.lib.asm.tree;

import cn.watchdog.lib.asm.MethodVisitor;

/**
 * A node that represents a local variable declaration.
 *
 * @author Eric Bruneton
 */
public class LocalVariableNode {

	/**
	 * The name of a local variable.
	 */
	public String name;

	/**
	 * The type descriptor of this local variable.
	 */
	public String desc;

	/**
	 * The signature of this local variable. May be {@literal null}.
	 */
	public String signature;

	/**
	 * The first instruction corresponding to the scope of this local variable (inclusive).
	 */
	public LabelNode start;

	/**
	 * The last instruction corresponding to the scope of this local variable (exclusive).
	 */
	public LabelNode end;

	/**
	 * The local variable's index.
	 */
	public int index;

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.LocalVariableNode}.
	 *
	 * @param name       the name of a local variable.
	 * @param descriptor the type descriptor of this local variable.
	 * @param signature  the signature of this local variable. May be {@literal null}.
	 * @param start      the first instruction corresponding to the scope of this local variable
	 *                   (inclusive).
	 * @param end        the last instruction corresponding to the scope of this local variable (exclusive).
	 * @param index      the local variable's index.
	 */
	public LocalVariableNode(
			final String name,
			final String descriptor,
			final String signature,
			final LabelNode start,
			final LabelNode end,
			final int index) {
		this.name = name;
		this.desc = descriptor;
		this.signature = signature;
		this.start = start;
		this.end = end;
		this.index = index;
	}

	/**
	 * Makes the given visitor visit this local variable declaration.
	 *
	 * @param methodVisitor a method visitor.
	 */
	public void accept(final MethodVisitor methodVisitor) {
		methodVisitor.visitLocalVariable(
				name, desc, signature, start.getLabel(), end.getLabel(), index);
	}
}
