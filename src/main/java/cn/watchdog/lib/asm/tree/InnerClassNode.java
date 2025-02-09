package cn.watchdog.lib.asm.tree;

import cn.watchdog.lib.asm.ClassVisitor;
import cn.watchdog.lib.asm.Type;

/**
 * A node that represents an inner class. This inner class is not necessarily a member of the {@link
 * ClassNode} containing this object. More precisely, every class or interface C which is referenced
 * by a {@link ClassNode} and which is not a package member must be represented with an {@link
 * cn.watchdog.lib.asm.tree.InnerClassNode}. The {@link ClassNode} must reference its nested class or interface members, and
 * its enclosing class, if any. See the JVMS 4.7.6 section for more details.
 *
 * @author Eric Bruneton
 */
public class InnerClassNode {

	/**
	 * The internal name of an inner class (see {@link Type#getInternalName()}).
	 */
	public String name;

	/**
	 * The internal name of the class to which the inner class belongs (see {@link
	 * Type#getInternalName()}). May be {@literal null}.
	 */
	public String outerName;

	/**
	 * The (simple) name of the inner class inside its enclosing class. Must be {@literal null} if the
	 * inner class is not the member of a class or interface (e.g. for local or anonymous classes).
	 */
	public String innerName;

	/**
	 * The access flags of the inner class as originally declared in the source code from which the
	 * class was compiled.
	 */
	public int access;

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.InnerClassNode} for an inner class C.
	 *
	 * @param name      the internal name of C (see {@link Type#getInternalName()}).
	 * @param outerName the internal name of the class or interface C is a member of (see {@link
	 *                  Type#getInternalName()}). Must be {@literal null} if C is not the member
	 *                  of a class or interface (e.g. for local or anonymous classes).
	 * @param innerName the (simple) name of C. Must be {@literal null} for anonymous inner classes.
	 * @param access    the access flags of C originally declared in the source code from which this
	 *                  class was compiled.
	 */
	public InnerClassNode(
			final String name, final String outerName, final String innerName, final int access) {
		this.name = name;
		this.outerName = outerName;
		this.innerName = innerName;
		this.access = access;
	}

	/**
	 * Makes the given class visitor visit this inner class.
	 *
	 * @param classVisitor a class visitor.
	 */
	public void accept(final ClassVisitor classVisitor) {
		classVisitor.visitInnerClass(name, outerName, innerName, access);
	}
}
