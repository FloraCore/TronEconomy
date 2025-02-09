package cn.watchdog.lib.asm.tree;

import cn.watchdog.lib.asm.AnnotationVisitor;
import cn.watchdog.lib.asm.Attribute;
import cn.watchdog.lib.asm.ClassVisitor;
import cn.watchdog.lib.asm.Opcodes;
import cn.watchdog.lib.asm.RecordComponentVisitor;
import cn.watchdog.lib.asm.Type;
import cn.watchdog.lib.asm.TypePath;

import java.util.List;

/**
 * A node that represents a record component.
 *
 * @author Remi Forax
 */
public class RecordComponentNode extends RecordComponentVisitor {

	/**
	 * The record component name.
	 */
	public String name;

	/**
	 * The record component descriptor (see {@link Type}).
	 */
	public String descriptor;

	/**
	 * The record component signature. May be {@literal null}.
	 */
	public String signature;

	/**
	 * The runtime visible annotations of this record component. May be {@literal null}.
	 */
	public List<AnnotationNode> visibleAnnotations;

	/**
	 * The runtime invisible annotations of this record component. May be {@literal null}.
	 */
	public List<AnnotationNode> invisibleAnnotations;

	/**
	 * The runtime visible type annotations of this record component. May be {@literal null}.
	 */
	public List<TypeAnnotationNode> visibleTypeAnnotations;

	/**
	 * The runtime invisible type annotations of this record component. May be {@literal null}.
	 */
	public List<TypeAnnotationNode> invisibleTypeAnnotations;

	/**
	 * The non standard attributes of this record component. * May be {@literal null}.
	 */
	public List<Attribute> attrs;

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.RecordComponentNode}. <i>Subclasses must not use this constructor</i>.
	 * Instead, they must use the {@link #RecordComponentNode(int, String, String, String)} version.
	 *
	 * @param name       the record component name.
	 * @param descriptor the record component descriptor (see {@link Type}).
	 * @param signature  the record component signature.
	 * @throws IllegalStateException If a subclass calls this constructor.
	 */
	public RecordComponentNode(final String name, final String descriptor, final String signature) {
		this(/* latest api = */ Opcodes.ASM9, name, descriptor, signature);
		if (getClass() != RecordComponentNode.class) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.RecordComponentNode}.
	 *
	 * @param api        the ASM API version implemented by this visitor. Must be one of {@link Opcodes#ASM8}
	 *                   or {@link Opcodes#ASM9}.
	 * @param name       the record component name.
	 * @param descriptor the record component descriptor (see {@link Type}).
	 * @param signature  the record component signature.
	 */
	public RecordComponentNode(
			final int api, final String name, final String descriptor, final String signature) {
		super(api);
		this.name = name;
		this.descriptor = descriptor;
		this.signature = signature;
	}

	// -----------------------------------------------------------------------------------------------
	// Implementation of the FieldVisitor abstract class
	// -----------------------------------------------------------------------------------------------

	@Override
	public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
		AnnotationNode annotation = new AnnotationNode(descriptor);
		if (visible) {
			visibleAnnotations = Util.add(visibleAnnotations, annotation);
		} else {
			invisibleAnnotations = Util.add(invisibleAnnotations, annotation);
		}
		return annotation;
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(
			final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
		TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
		if (visible) {
			visibleTypeAnnotations = Util.add(visibleTypeAnnotations, typeAnnotation);
		} else {
			invisibleTypeAnnotations = Util.add(invisibleTypeAnnotations, typeAnnotation);
		}
		return typeAnnotation;
	}

	@Override
	public void visitAttribute(final Attribute attribute) {
		attrs = Util.add(attrs, attribute);
	}

	@Override
	public void visitEnd() {
		// Nothing to do.
	}

	// -----------------------------------------------------------------------------------------------
	// Accept methods
	// -----------------------------------------------------------------------------------------------

	/**
	 * Checks that this record component node is compatible with the given ASM API version. This
	 * method checks that this node, and all its children recursively, do not contain elements that
	 * were introduced in more recent versions of the ASM API than the given version.
	 *
	 * @param api an ASM API version. Must be one of {@link Opcodes#ASM8} or {@link Opcodes#ASM9}.
	 */
	public void check(final int api) {
		if (api < Opcodes.ASM8) {
			throw new UnsupportedClassVersionException();
		}
	}

	/**
	 * Makes the given class visitor visit this record component.
	 *
	 * @param classVisitor a class visitor.
	 */
	public void accept(final ClassVisitor classVisitor) {
		RecordComponentVisitor recordComponentVisitor =
				classVisitor.visitRecordComponent(name, descriptor, signature);
		if (recordComponentVisitor == null) {
			return;
		}
		// Visit the annotations.
		if (visibleAnnotations != null) {
			for (AnnotationNode annotation : visibleAnnotations) {
				annotation.accept(recordComponentVisitor.visitAnnotation(annotation.desc, true));
			}
		}
		if (invisibleAnnotations != null) {
			for (AnnotationNode annotation : invisibleAnnotations) {
				annotation.accept(recordComponentVisitor.visitAnnotation(annotation.desc, false));
			}
		}
		if (visibleTypeAnnotations != null) {
			for (TypeAnnotationNode typeAnnotation : visibleTypeAnnotations) {
				typeAnnotation.accept(
						recordComponentVisitor.visitTypeAnnotation(
								typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
			}
		}
		if (invisibleTypeAnnotations != null) {
			for (TypeAnnotationNode typeAnnotation : invisibleTypeAnnotations) {
				typeAnnotation.accept(
						recordComponentVisitor.visitTypeAnnotation(
								typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
			}
		}
		// Visit the non standard attributes.
		if (attrs != null) {
			for (Attribute attr : attrs) {
				recordComponentVisitor.visitAttribute(attr);
			}
		}
		recordComponentVisitor.visitEnd();
	}
}
