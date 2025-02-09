package cn.watchdog.lib.asm.util;

import cn.watchdog.lib.asm.AnnotationVisitor;
import cn.watchdog.lib.asm.Attribute;
import cn.watchdog.lib.asm.ClassVisitor;
import cn.watchdog.lib.asm.FieldVisitor;
import cn.watchdog.lib.asm.MethodVisitor;
import cn.watchdog.lib.asm.ModuleVisitor;
import cn.watchdog.lib.asm.Opcodes;
import cn.watchdog.lib.asm.RecordComponentVisitor;
import cn.watchdog.lib.asm.TypePath;

import java.io.PrintWriter;

/**
 * A {@link ClassVisitor} that prints the classes it visits with a {@link Printer}. This class
 * visitor can be used in the middle of a class visitor chain to trace the class that is visited at
 * a given point in this chain. This may be useful for debugging purposes.
 *
 * <p>When used with a {@link Textifier}, the trace printed when visiting the {@code Hello} class is
 * the following:
 *
 * <pre>
 * // class version 49.0 (49) // access flags 0x21 public class Hello {
 *
 * // compiled from: Hello.java
 *
 * // access flags 0x1
 * public &lt;init&gt; ()V
 * ALOAD 0
 * INVOKESPECIAL java/lang/Object &lt;init&gt; ()V
 * RETURN
 * MAXSTACK = 1 MAXLOCALS = 1
 *
 * // access flags 0x9
 * public static main ([Ljava/lang/String;)V
 * GETSTATIC java/lang/System out Ljava/io/PrintStream;
 * LDC &quot;hello&quot;
 * INVOKEVIRTUAL java/io/PrintStream println (Ljava/lang/String;)V
 * RETURN
 * MAXSTACK = 2 MAXLOCALS = 1
 * }
 * </pre>
 *
 * <p>where {@code Hello} is defined by:
 *
 * <pre>
 * public class Hello {
 *
 *   public static void main(String[] args) {
 *     System.out.println(&quot;hello&quot;);
 *   }
 * }
 * </pre>
 *
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
public final class TraceClassVisitor extends ClassVisitor {

	/**
	 * The printer to convert the visited class into text.
	 */
	// DontCheck(MemberName): can't be renamed (for backward binary compatibility).
	public final Printer p;
	/**
	 * The print writer to be used to print the class. May be {@literal null}.
	 */
	private final PrintWriter printWriter;

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.util.TraceClassVisitor}.
	 *
	 * @param printWriter the print writer to be used to print the class. May be {@literal null}.
	 */
	public TraceClassVisitor(final PrintWriter printWriter) {
		this(null, printWriter);
	}

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.util.TraceClassVisitor}.
	 *
	 * @param classVisitor the class visitor to which to delegate calls. May be {@literal null}.
	 * @param printWriter  the print writer to be used to print the class. May be {@literal null}.
	 */
	public TraceClassVisitor(final ClassVisitor classVisitor, final PrintWriter printWriter) {
		this(classVisitor, new Textifier(), printWriter);
	}

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.util.TraceClassVisitor}.
	 *
	 * @param classVisitor the class visitor to which to delegate calls. May be {@literal null}.
	 * @param printer      the printer to convert the visited class into text.
	 * @param printWriter  the print writer to be used to print the class. May be {@literal null}.
	 */
	public TraceClassVisitor(
			final ClassVisitor classVisitor, final Printer printer, final PrintWriter printWriter) {
		super(/* latest api = */ Opcodes.ASM10_EXPERIMENTAL, classVisitor);
		this.printWriter = printWriter;
		this.p = printer;
	}

	@Override
	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		p.visit(version, access, name, signature, superName, interfaces);
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public void visitSource(final String file, final String debug) {
		p.visitSource(file, debug);
		super.visitSource(file, debug);
	}

	@Override
	public ModuleVisitor visitModule(final String name, final int flags, final String version) {
		Printer modulePrinter = p.visitModule(name, flags, version);
		return new TraceModuleVisitor(super.visitModule(name, flags, version), modulePrinter);
	}

	@Override
	public void visitNestHost(final String nestHost) {
		p.visitNestHost(nestHost);
		super.visitNestHost(nestHost);
	}

	@Override
	public void visitOuterClass(final String owner, final String name, final String descriptor) {
		p.visitOuterClass(owner, name, descriptor);
		super.visitOuterClass(owner, name, descriptor);
	}

	@Override
	public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
		Printer annotationPrinter = p.visitClassAnnotation(descriptor, visible);
		return new TraceAnnotationVisitor(
				super.visitAnnotation(descriptor, visible), annotationPrinter);
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(
			final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
		Printer annotationPrinter = p.visitClassTypeAnnotation(typeRef, typePath, descriptor, visible);
		return new TraceAnnotationVisitor(
				super.visitTypeAnnotation(typeRef, typePath, descriptor, visible), annotationPrinter);
	}

	@Override
	public void visitAttribute(final Attribute attribute) {
		p.visitClassAttribute(attribute);
		super.visitAttribute(attribute);
	}

	@Override
	public void visitNestMember(final String nestMember) {
		p.visitNestMember(nestMember);
		super.visitNestMember(nestMember);
	}

	@Override
	public void visitPermittedSubclass(final String permittedSubclass) {
		p.visitPermittedSubclass(permittedSubclass);
		super.visitPermittedSubclass(permittedSubclass);
	}

	@Override
	public void visitInnerClass(
			final String name, final String outerName, final String innerName, final int access) {
		p.visitInnerClass(name, outerName, innerName, access);
		super.visitInnerClass(name, outerName, innerName, access);
	}

	@Override
	public RecordComponentVisitor visitRecordComponent(
			final String name, final String descriptor, final String signature) {
		Printer recordComponentPrinter = p.visitRecordComponent(name, descriptor, signature);
		return new TraceRecordComponentVisitor(
				super.visitRecordComponent(name, descriptor, signature), recordComponentPrinter);
	}

	@Override
	public FieldVisitor visitField(
			final int access,
			final String name,
			final String descriptor,
			final String signature,
			final Object value) {
		Printer fieldPrinter = p.visitField(access, name, descriptor, signature, value);
		return new TraceFieldVisitor(
				super.visitField(access, name, descriptor, signature, value), fieldPrinter);
	}

	@Override
	public MethodVisitor visitMethod(
			final int access,
			final String name,
			final String descriptor,
			final String signature,
			final String[] exceptions) {
		Printer methodPrinter = p.visitMethod(access, name, descriptor, signature, exceptions);
		return new TraceMethodVisitor(
				super.visitMethod(access, name, descriptor, signature, exceptions), methodPrinter);
	}

	@Override
	public void visitEnd() {
		p.visitClassEnd();
		if (printWriter != null) {
			p.print(printWriter);
			printWriter.flush();
		}
		super.visitEnd();
	}
}
