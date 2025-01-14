package cn.watchdog.lib.asm.tree;

import cn.watchdog.lib.asm.AnnotationVisitor;
import cn.watchdog.lib.asm.Attribute;
import cn.watchdog.lib.asm.ClassVisitor;
import cn.watchdog.lib.asm.FieldVisitor;
import cn.watchdog.lib.asm.MethodVisitor;
import cn.watchdog.lib.asm.ModuleVisitor;
import cn.watchdog.lib.asm.Opcodes;
import cn.watchdog.lib.asm.RecordComponentVisitor;
import cn.watchdog.lib.asm.Type;
import cn.watchdog.lib.asm.TypePath;

import java.util.ArrayList;
import java.util.List;

/**
 * A node that represents a class.
 *
 * @author Eric Bruneton
 */
public class ClassNode extends ClassVisitor {

	/**
	 * The class version. The minor version is stored in the 16 most significant bits, and the major
	 * version in the 16 least significant bits.
	 */
	public int version;

	/**
	 * The class's access flags (see {@link Opcodes}). This field also indicates if
	 * the class is deprecated {@link Opcodes#ACC_DEPRECATED} or a record {@link Opcodes#ACC_RECORD}.
	 */
	public int access;

	/**
	 * The internal name of this class (see {@link Type#getInternalName()}).
	 */
	public String name;

	/**
	 * The signature of this class. May be {@literal null}.
	 */
	public String signature;

	/**
	 * The internal of name of the super class (see {@link Type#getInternalName()}).
	 * For interfaces, the super class is {@link Object}. May be {@literal null}, but only for the
	 * {@link Object} class.
	 */
	public String superName;

	/**
	 * The internal names of the interfaces directly implemented by this class (see {@link
	 * Type#getInternalName()}).
	 */
	public List<String> interfaces;

	/**
	 * The name of the source file from which this class was compiled. May be {@literal null}.
	 */
	public String sourceFile;

	/**
	 * The correspondence between source and compiled elements of this class. May be {@literal null}.
	 */
	public String sourceDebug;

	/**
	 * The module stored in this class. May be {@literal null}.
	 */
	public ModuleNode module;

	/**
	 * The internal name of the enclosing class of this class (see {@link
	 * Type#getInternalName()}). Must be {@literal null} if this class has no
	 * enclosing class, or if it is a local or anonymous class.
	 */
	public String outerClass;

	/**
	 * The name of the method that contains the class, or {@literal null} if the class has no
	 * enclosing class, or is not enclosed in a method or constructor of its enclosing class (e.g. if
	 * it is enclosed in an instance initializer, static initializer, instance variable initializer,
	 * or class variable initializer).
	 */
	public String outerMethod;

	/**
	 * The descriptor of the method that contains the class, or {@literal null} if the class has no
	 * enclosing class, or is not enclosed in a method or constructor of its enclosing class (e.g. if
	 * it is enclosed in an instance initializer, static initializer, instance variable initializer,
	 * or class variable initializer).
	 */
	public String outerMethodDesc;

	/**
	 * The runtime visible annotations of this class. May be {@literal null}.
	 */
	public List<AnnotationNode> visibleAnnotations;

	/**
	 * The runtime invisible annotations of this class. May be {@literal null}.
	 */
	public List<AnnotationNode> invisibleAnnotations;

	/**
	 * The runtime visible type annotations of this class. May be {@literal null}.
	 */
	public List<TypeAnnotationNode> visibleTypeAnnotations;

	/**
	 * The runtime invisible type annotations of this class. May be {@literal null}.
	 */
	public List<TypeAnnotationNode> invisibleTypeAnnotations;

	/**
	 * The non standard attributes of this class. May be {@literal null}.
	 */
	public List<Attribute> attrs;

	/**
	 * The inner classes of this class.
	 */
	public List<InnerClassNode> innerClasses;

	/**
	 * The internal name of the nest host class of this class (see {@link
	 * Type#getInternalName()}). May be {@literal null}.
	 */
	public String nestHostClass;

	/**
	 * The internal names of the nest members of this class (see {@link
	 * Type#getInternalName()}). May be {@literal null}.
	 */
	public List<String> nestMembers;

	/**
	 * The internal names of the permitted subclasses of this class (see {@link
	 * Type#getInternalName()}). May be {@literal null}.
	 */
	public List<String> permittedSubclasses;

	/**
	 * The record components of this class. May be {@literal null}.
	 */
	public List<RecordComponentNode> recordComponents;

	/**
	 * The fields of this class.
	 */
	public List<FieldNode> fields;

	/**
	 * The methods of this class.
	 */
	public List<MethodNode> methods;

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.ClassNode}. <i>Subclasses must not use this constructor</i>. Instead,
	 * they must use the {@link #ClassNode(int)} version.
	 *
	 * @throws IllegalStateException If a subclass calls this constructor.
	 */
	public ClassNode() {
		this(Opcodes.ASM9);
		if (getClass() != ClassNode.class) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.ClassNode}.
	 *
	 * @param api the ASM API version implemented by this visitor. Must be one of the {@code
	 *            ASM}<i>x</i> values in {@link Opcodes}.
	 */
	public ClassNode(final int api) {
		super(api);
		this.interfaces = new ArrayList<>();
		this.innerClasses = new ArrayList<>();
		this.fields = new ArrayList<>();
		this.methods = new ArrayList<>();
	}

	// -----------------------------------------------------------------------------------------------
	// Implementation of the ClassVisitor abstract class
	// -----------------------------------------------------------------------------------------------

	@Override
	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		this.version = version;
		this.access = access;
		this.name = name;
		this.signature = signature;
		this.superName = superName;
		this.interfaces = Util.asArrayList(interfaces);
	}

	@Override
	public void visitSource(final String file, final String debug) {
		sourceFile = file;
		sourceDebug = debug;
	}

	@Override
	public ModuleVisitor visitModule(final String name, final int access, final String version) {
		module = new ModuleNode(name, access, version);
		return module;
	}

	@Override
	public void visitNestHost(final String nestHost) {
		this.nestHostClass = nestHost;
	}

	@Override
	public void visitOuterClass(final String owner, final String name, final String descriptor) {
		outerClass = owner;
		outerMethod = name;
		outerMethodDesc = descriptor;
	}

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
	public void visitNestMember(final String nestMember) {
		nestMembers = Util.add(nestMembers, nestMember);
	}

	@Override
	public void visitPermittedSubclass(final String permittedSubclass) {
		permittedSubclasses = Util.add(permittedSubclasses, permittedSubclass);
	}

	@Override
	public void visitInnerClass(
			final String name, final String outerName, final String innerName, final int access) {
		InnerClassNode innerClass = new InnerClassNode(name, outerName, innerName, access);
		innerClasses.add(innerClass);
	}

	@Override
	public RecordComponentVisitor visitRecordComponent(
			final String name, final String descriptor, final String signature) {
		RecordComponentNode recordComponent = new RecordComponentNode(name, descriptor, signature);
		recordComponents = Util.add(recordComponents, recordComponent);
		return recordComponent;
	}

	@Override
	public FieldVisitor visitField(
			final int access,
			final String name,
			final String descriptor,
			final String signature,
			final Object value) {
		FieldNode field = new FieldNode(access, name, descriptor, signature, value);
		fields.add(field);
		return field;
	}

	@Override
	public MethodVisitor visitMethod(
			final int access,
			final String name,
			final String descriptor,
			final String signature,
			final String[] exceptions) {
		MethodNode method = new MethodNode(access, name, descriptor, signature, exceptions);
		methods.add(method);
		return method;
	}

	@Override
	public void visitEnd() {
		// Nothing to do.
	}

	// -----------------------------------------------------------------------------------------------
	// Accept method
	// -----------------------------------------------------------------------------------------------

	/**
	 * Checks that this class node is compatible with the given ASM API version. This method checks
	 * that this node, and all its children recursively, do not contain elements that were introduced
	 * in more recent versions of the ASM API than the given version.
	 *
	 * @param api an ASM API version. Must be one of the {@code ASM}<i>x</i> values in {@link
	 *            Opcodes}.
	 */
	public void check(final int api) {
		if (api < Opcodes.ASM9 && permittedSubclasses != null) {
			throw new UnsupportedClassVersionException();
		}
		if (api < Opcodes.ASM8 && ((access & Opcodes.ACC_RECORD) != 0 || recordComponents != null)) {
			throw new UnsupportedClassVersionException();
		}
		if (api < Opcodes.ASM7 && (nestHostClass != null || nestMembers != null)) {
			throw new UnsupportedClassVersionException();
		}
		if (api < Opcodes.ASM6 && module != null) {
			throw new UnsupportedClassVersionException();
		}
		if (api < Opcodes.ASM5) {
			if (visibleTypeAnnotations != null && !visibleTypeAnnotations.isEmpty()) {
				throw new UnsupportedClassVersionException();
			}
			if (invisibleTypeAnnotations != null && !invisibleTypeAnnotations.isEmpty()) {
				throw new UnsupportedClassVersionException();
			}
		}
		// Check the annotations.
		if (visibleAnnotations != null) {
			for (int i = visibleAnnotations.size() - 1; i >= 0; --i) {
				visibleAnnotations.get(i).check(api);
			}
		}
		if (invisibleAnnotations != null) {
			for (int i = invisibleAnnotations.size() - 1; i >= 0; --i) {
				invisibleAnnotations.get(i).check(api);
			}
		}
		if (visibleTypeAnnotations != null) {
			for (int i = visibleTypeAnnotations.size() - 1; i >= 0; --i) {
				visibleTypeAnnotations.get(i).check(api);
			}
		}
		if (invisibleTypeAnnotations != null) {
			for (int i = invisibleTypeAnnotations.size() - 1; i >= 0; --i) {
				invisibleTypeAnnotations.get(i).check(api);
			}
		}
		if (recordComponents != null) {
			for (int i = recordComponents.size() - 1; i >= 0; --i) {
				recordComponents.get(i).check(api);
			}
		}
		for (int i = fields.size() - 1; i >= 0; --i) {
			fields.get(i).check(api);
		}
		for (int i = methods.size() - 1; i >= 0; --i) {
			methods.get(i).check(api);
		}
	}

	/**
	 * Makes the given class visitor visit this class.
	 *
	 * @param classVisitor a class visitor.
	 */
	public void accept(final ClassVisitor classVisitor) {
		// Visit the header.
		String[] interfacesArray = new String[this.interfaces.size()];
		this.interfaces.toArray(interfacesArray);
		classVisitor.visit(version, access, name, signature, superName, interfacesArray);
		// Visit the source.
		if (sourceFile != null || sourceDebug != null) {
			classVisitor.visitSource(sourceFile, sourceDebug);
		}
		// Visit the module.
		if (module != null) {
			module.accept(classVisitor);
		}
		// Visit the nest host class.
		if (nestHostClass != null) {
			classVisitor.visitNestHost(nestHostClass);
		}
		// Visit the outer class.
		if (outerClass != null) {
			classVisitor.visitOuterClass(outerClass, outerMethod, outerMethodDesc);
		}
		// Visit the annotations.
		if (visibleAnnotations != null) {
			for (AnnotationNode annotation : visibleAnnotations) {
				annotation.accept(classVisitor.visitAnnotation(annotation.desc, true));
			}
		}
		if (invisibleAnnotations != null) {
			for (AnnotationNode annotation : invisibleAnnotations) {
				annotation.accept(classVisitor.visitAnnotation(annotation.desc, false));
			}
		}
		if (visibleTypeAnnotations != null) {
			for (TypeAnnotationNode typeAnnotation : visibleTypeAnnotations) {
				typeAnnotation.accept(
						classVisitor.visitTypeAnnotation(
								typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
			}
		}
		if (invisibleTypeAnnotations != null) {
			for (TypeAnnotationNode typeAnnotation : invisibleTypeAnnotations) {
				typeAnnotation.accept(
						classVisitor.visitTypeAnnotation(
								typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
			}
		}
		// Visit the non-standard attributes.
		if (attrs != null) {
			for (Attribute attr : attrs) {
				classVisitor.visitAttribute(attr);
			}
		}
		// Visit the nest members.
		if (nestMembers != null) {
			for (String nestMember : nestMembers) {
				classVisitor.visitNestMember(nestMember);
			}
		}
		// Visit the permitted subclasses.
		if (permittedSubclasses != null) {
			for (String permittedSubclass : permittedSubclasses) {
				classVisitor.visitPermittedSubclass(permittedSubclass);
			}
		}
		// Visit the inner classes.
		for (InnerClassNode innerClass : innerClasses) {
			innerClass.accept(classVisitor);
		}
		// Visit the record components.
		if (recordComponents != null) {
			for (RecordComponentNode recordComponent : recordComponents) {
				recordComponent.accept(classVisitor);
			}
		}
		// Visit the fields.
		for (FieldNode field : fields) {
			field.accept(classVisitor);
		}
		// Visit the methods.
		for (MethodNode method : methods) {
			method.accept(classVisitor);
		}
		classVisitor.visitEnd();
	}
}
