package cn.watchdog.lib.asm.tree;

import cn.watchdog.lib.asm.MethodVisitor;
import cn.watchdog.lib.asm.Type;

import java.util.List;

/**
 * A node that represents a try catch block.
 *
 * @author Eric Bruneton
 */
public class TryCatchBlockNode {

	/**
	 * The beginning of the exception handler's scope (inclusive).
	 */
	public LabelNode start;

	/**
	 * The end of the exception handler's scope (exclusive).
	 */
	public LabelNode end;

	/**
	 * The beginning of the exception handler's code.
	 */
	public LabelNode handler;

	/**
	 * The internal name of the type of exceptions handled by the handler. May be {@literal null} to
	 * catch any exceptions (for "finally" blocks).
	 */
	public String type;

	/**
	 * The runtime visible type annotations on the exception handler type. May be {@literal null}.
	 */
	public List<TypeAnnotationNode> visibleTypeAnnotations;

	/**
	 * The runtime invisible type annotations on the exception handler type. May be {@literal null}.
	 */
	public List<TypeAnnotationNode> invisibleTypeAnnotations;

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.TryCatchBlockNode}.
	 *
	 * @param start   the beginning of the exception handler's scope (inclusive).
	 * @param end     the end of the exception handler's scope (exclusive).
	 * @param handler the beginning of the exception handler's code.
	 * @param type    the internal name of the type of exceptions handled by the handler (see {@link
	 *                Type#getInternalName()}), or {@literal null} to catch any exceptions (for
	 *                "finally" blocks).
	 */
	public TryCatchBlockNode(
			final LabelNode start, final LabelNode end, final LabelNode handler, final String type) {
		this.start = start;
		this.end = end;
		this.handler = handler;
		this.type = type;
	}

	/**
	 * Updates the index of this try catch block in the method's list of try catch block nodes. This
	 * index maybe stored in the 'target' field of the type annotations of this block.
	 *
	 * @param index the new index of this try catch block in the method's list of try catch block
	 *              nodes.
	 */
	public void updateIndex(final int index) {
		int newTypeRef = 0x42000000 | (index << 8);
		if (visibleTypeAnnotations != null) {
			for (TypeAnnotationNode visibleTypeAnnotation : visibleTypeAnnotations) {
				visibleTypeAnnotation.typeRef = newTypeRef;
			}
		}
		if (invisibleTypeAnnotations != null) {
			for (TypeAnnotationNode invisibleTypeAnnotation : invisibleTypeAnnotations) {
				invisibleTypeAnnotation.typeRef = newTypeRef;
			}
		}
	}

	/**
	 * Makes the given visitor visit this try catch block.
	 *
	 * @param methodVisitor a method visitor.
	 */
	public void accept(final MethodVisitor methodVisitor) {
		methodVisitor.visitTryCatchBlock(
				start.getLabel(), end.getLabel(), handler == null ? null : handler.getLabel(), type);
		if (visibleTypeAnnotations != null) {
			for (TypeAnnotationNode typeAnnotation : visibleTypeAnnotations) {
				typeAnnotation.accept(
						methodVisitor.visitTryCatchAnnotation(
								typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
			}
		}
		if (invisibleTypeAnnotations != null) {
			for (TypeAnnotationNode typeAnnotation : invisibleTypeAnnotations) {
				typeAnnotation.accept(
						methodVisitor.visitTryCatchAnnotation(
								typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
			}
		}
	}
}
