package cn.watchdog.lib.asm.tree.analysis;

import cn.watchdog.lib.asm.Opcodes;
import cn.watchdog.lib.asm.Type;

import java.util.List;

/**
 * An extended {@link BasicVerifier} that performs more precise verifications. This verifier
 * computes exact class types, instead of using a single "object reference" type (as done in {@link
 * BasicVerifier}).
 *
 * @author Eric Bruneton
 * @author Bing Ran
 */
public class SimpleVerifier extends BasicVerifier {

	/**
	 * The type of the class that is verified.
	 */
	private final Type currentClass;

	/**
	 * The type of the super class of the class that is verified.
	 */
	private final Type currentSuperClass;

	/**
	 * The types of the interfaces directly implemented by the class that is verified.
	 */
	private final List<Type> currentClassInterfaces;

	/**
	 * Whether the class that is verified is an interface.
	 */
	private final boolean isInterface;

	/**
	 * The loader to use to load the referenced classes.
	 */
	private ClassLoader loader = getClass().getClassLoader();

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.analysis.SimpleVerifier}. <i>Subclasses must not use this constructor</i>.
	 * Instead, they must use the {@link #SimpleVerifier(int, Type, Type, java.util.List, boolean)} version.
	 */
	public SimpleVerifier() {
		this(null, null, false);
	}

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.analysis.SimpleVerifier} to verify a specific class. This class will not be
	 * loaded into the JVM since it may be incorrect. <i>Subclasses must not use this constructor</i>.
	 * Instead, they must use the {@link #SimpleVerifier(int, Type, Type, java.util.List, boolean)} version.
	 *
	 * @param currentClass      the type of the class to be verified.
	 * @param currentSuperClass the type of the super class of the class to be verified.
	 * @param isInterface       whether the class to be verifier is an interface.
	 */
	public SimpleVerifier(
			final Type currentClass, final Type currentSuperClass, final boolean isInterface) {
		this(currentClass, currentSuperClass, null, isInterface);
	}

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.analysis.SimpleVerifier} to verify a specific class. This class will not be
	 * loaded into the JVM since it may be incorrect. <i>Subclasses must not use this constructor</i>.
	 * Instead, they must use the {@link #SimpleVerifier(int, Type, Type, java.util.List, boolean)} version.
	 *
	 * @param currentClass           the type of the class to be verified.
	 * @param currentSuperClass      the type of the super class of the class to be verified.
	 * @param currentClassInterfaces the types of the interfaces directly implemented by the class to
	 *                               be verified.
	 * @param isInterface            whether the class to be verifier is an interface.
	 */
	public SimpleVerifier(
			final Type currentClass,
			final Type currentSuperClass,
			final List<Type> currentClassInterfaces,
			final boolean isInterface) {
		this(
				/* latest api = */ ASM9,
				currentClass,
				currentSuperClass,
				currentClassInterfaces,
				isInterface);
		if (getClass() != SimpleVerifier.class) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Constructs a new {@link cn.watchdog.lib.asm.tree.analysis.SimpleVerifier} to verify a specific class. This class will not be
	 * loaded into the JVM since it may be incorrect.
	 *
	 * @param api                    the ASM API version supported by this verifier. Must be one of the {@code
	 *                               ASM}<i>x</i> values in {@link Opcodes}.
	 * @param currentClass           the type of the class to be verified.
	 * @param currentSuperClass      the type of the super class of the class to be verified.
	 * @param currentClassInterfaces the types of the interfaces directly implemented by the class to
	 *                               be verified.
	 * @param isInterface            whether the class to be verifier is an interface.
	 */
	protected SimpleVerifier(
			final int api,
			final Type currentClass,
			final Type currentSuperClass,
			final List<Type> currentClassInterfaces,
			final boolean isInterface) {
		super(api);
		this.currentClass = currentClass;
		this.currentSuperClass = currentSuperClass;
		this.currentClassInterfaces = currentClassInterfaces;
		this.isInterface = isInterface;
	}

	/**
	 * Sets the <code>ClassLoader</code> to be used in {@link #getClass}.
	 *
	 * @param loader the <code>ClassLoader</code> to use.
	 */
	public void setClassLoader(final ClassLoader loader) {
		this.loader = loader;
	}

	@Override
	public BasicValue newValue(final Type type) {
		if (type == null) {
			return BasicValue.UNINITIALIZED_VALUE;
		}

		boolean isArray = type.getSort() == Type.ARRAY;
		if (isArray) {
			switch (type.getElementType().getSort()) {
				case Type.BOOLEAN:
				case Type.CHAR:
				case Type.BYTE:
				case Type.SHORT:
					return new BasicValue(type);
				default:
					break;
			}
		}

		BasicValue value = super.newValue(type);
		if (BasicValue.REFERENCE_VALUE.equals(value)) {
			if (isArray) {
				value = newValue(type.getElementType());
				StringBuilder descriptor = new StringBuilder();
				for (int i = 0; i < type.getDimensions(); ++i) {
					descriptor.append('[');
				}
				descriptor.append(value.getType().getDescriptor());
				value = new BasicValue(Type.getType(descriptor.toString()));
			} else {
				value = new BasicValue(type);
			}
		}
		return value;
	}

	@Override
	protected boolean isArrayValue(final BasicValue value) {
		Type type = value.getType();
		return type != null && (type.getSort() == Type.ARRAY || type.equals(NULL_TYPE));
	}

	@Override
	protected BasicValue getElementValue(final BasicValue objectArrayValue) throws AnalyzerException {
		Type arrayType = objectArrayValue.getType();
		if (arrayType != null) {
			if (arrayType.getSort() == Type.ARRAY) {
				return newValue(Type.getType(arrayType.getDescriptor().substring(1)));
			} else if (arrayType.equals(NULL_TYPE)) {
				return objectArrayValue;
			}
		}
		throw new AssertionError();
	}

	@Override
	protected boolean isSubTypeOf(final BasicValue value, final BasicValue expected) {
		Type expectedType = expected.getType();
		Type type = value.getType();
		switch (expectedType.getSort()) {
			case Type.INT:
			case Type.FLOAT:
			case Type.LONG:
			case Type.DOUBLE:
				return type.equals(expectedType);
			case Type.ARRAY:
			case Type.OBJECT:
				if (type.equals(NULL_TYPE)) {
					return true;
				} else if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
					if (isAssignableFrom(expectedType, type)) {
						return true;
					} else if (getClass(expectedType).isInterface()) {
						// The merge of class or interface types can only yield class types (because it is not
						// possible in general to find an unambiguous common super interface, due to multiple
						// inheritance). Because of this limitation, we need to relax the subtyping check here
						// if 'value' is an interface.
						return Object.class.isAssignableFrom(getClass(type));
					} else {
						return false;
					}
				} else {
					return false;
				}
			default:
				throw new AssertionError();
		}
	}

	@Override
	public BasicValue merge(final BasicValue value1, final BasicValue value2) {
		if (!value1.equals(value2)) {
			Type type1 = value1.getType();
			Type type2 = value2.getType();
			if (type1 != null
					&& (type1.getSort() == Type.OBJECT || type1.getSort() == Type.ARRAY)
					&& type2 != null
					&& (type2.getSort() == Type.OBJECT || type2.getSort() == Type.ARRAY)) {
				if (type1.equals(NULL_TYPE)) {
					return value2;
				}
				if (type2.equals(NULL_TYPE)) {
					return value1;
				}
				if (isAssignableFrom(type1, type2)) {
					return value1;
				}
				if (isAssignableFrom(type2, type1)) {
					return value2;
				}
				int numDimensions = 0;
				if (type1.getSort() == Type.ARRAY
						&& type2.getSort() == Type.ARRAY
						&& type1.getDimensions() == type2.getDimensions()
						&& type1.getElementType().getSort() == Type.OBJECT
						&& type2.getElementType().getSort() == Type.OBJECT) {
					numDimensions = type1.getDimensions();
					type1 = type1.getElementType();
					type2 = type2.getElementType();
				}
				while (true) {
					if (type1 == null || isInterface(type1)) {
						return newArrayValue(Type.getObjectType("java/lang/Object"), numDimensions);
					}
					type1 = getSuperClass(type1);
					if (isAssignableFrom(type1, type2)) {
						return newArrayValue(type1, numDimensions);
					}
				}
			}
			return BasicValue.UNINITIALIZED_VALUE;
		}
		return value1;
	}

	private BasicValue newArrayValue(final Type type, final int dimensions) {
		if (dimensions == 0) {
			return newValue(type);
		} else {
			StringBuilder descriptor = new StringBuilder();
			for (int i = 0; i < dimensions; ++i) {
				descriptor.append('[');
			}
			descriptor.append(type.getDescriptor());
			return newValue(Type.getType(descriptor.toString()));
		}
	}

	/**
	 * Returns whether the given type corresponds to the type of an interface. The default
	 * implementation of this method loads the class and uses the reflection API to return its result
	 * (unless the given type corresponds to the class being verified).
	 *
	 * @param type a type.
	 * @return whether 'type' corresponds to an interface.
	 */
	protected boolean isInterface(final Type type) {
		if (currentClass != null && currentClass.equals(type)) {
			return isInterface;
		}
		return getClass(type).isInterface();
	}

	/**
	 * Returns the type corresponding to the super class of the given type. The default implementation
	 * of this method loads the class and uses the reflection API to return its result (unless the
	 * given type corresponds to the class being verified).
	 *
	 * @param type a type.
	 * @return the type corresponding to the super class of 'type'.
	 */
	protected Type getSuperClass(final Type type) {
		if (currentClass != null && currentClass.equals(type)) {
			return currentSuperClass;
		}
		Class<?> superClass = getClass(type).getSuperclass();
		return superClass == null ? null : Type.getType(superClass);
	}

	/**
	 * Returns whether the class corresponding to the first argument is either the same as, or is a
	 * superclass or superinterface of the class corresponding to the second argument. The default
	 * implementation of this method loads the classes and uses the reflection API to return its
	 * result (unless the result can be computed from the class being verified, and the types of its
	 * super classes and implemented interfaces).
	 *
	 * @param type1 a type.
	 * @param type2 another type.
	 * @return whether the class corresponding to 'type1' is either the same as, or is a superclass or
	 * superinterface of the class corresponding to 'type2'.
	 */
	protected boolean isAssignableFrom(final Type type1, final Type type2) {
		if (type1.equals(type2)) {
			return true;
		}
		if (currentClass != null && currentClass.equals(type1)) {
			if (getSuperClass(type2) == null) {
				return false;
			} else {
				if (isInterface) {
					return type2.getSort() == Type.OBJECT || type2.getSort() == Type.ARRAY;
				}
				return isAssignableFrom(type1, getSuperClass(type2));
			}
		}
		if (currentClass != null && currentClass.equals(type2)) {
			if (isAssignableFrom(type1, currentSuperClass)) {
				return true;
			}
			if (currentClassInterfaces != null) {
				for (Type currentClassInterface : currentClassInterfaces) {
					if (isAssignableFrom(type1, currentClassInterface)) {
						return true;
					}
				}
			}
			return false;
		}
		return getClass(type1).isAssignableFrom(getClass(type2));
	}

	/**
	 * Loads the class corresponding to the given type. The class is loaded with the class loader
	 * specified with {@link #setClassLoader}, or with the class loader of this class if no class
	 * loader was specified.
	 *
	 * @param type a type.
	 * @return the class corresponding to 'type'.
	 */
	protected Class<?> getClass(final Type type) {
		try {
			if (type.getSort() == Type.ARRAY) {
				return Class.forName(type.getDescriptor().replace('/', '.'), false, loader);
			}
			return Class.forName(type.getClassName(), false, loader);
		} catch (ClassNotFoundException e) {
			throw new TypeNotPresentException(e.toString(), e);
		}
	}
}
