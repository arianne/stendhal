/*
 * @(#) src/games/stendhal/common/ConfigurableFactoryHelper.java
 *
 * $Id$
 */

package games.stendhal.server.core.config.factory;

//
//

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A utility class for creating objects using ConfigurableFactory.
 */
public class ConfigurableFactoryHelper {

	/**
	 * A class type safe wrapper for a ConfigurableFactory that takes a desired
	 * target class and returns <code>null</code> if the factory returns an
	 * incompatible object. This allows the called to cast the return values
	 * without worrying about ClassCastExceptions.
	 *
	 * @param factory
	 *            Object factory.
	 * @param ctx
	 *            Configuration context.
	 * @param clazz
	 *            The target class.
	 *
	 * @return A new object, or <code>null</code> if allowed by the factory
	 *         type, or of the wrong class.
	 *
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 */
	public static Object create(final ConfigurableFactory factory,
			final ConfigurableFactoryContext ctx, final Class< ? > clazz) {
		Object obj;

		obj = factory.create(ctx);

		if (clazz.isInstance(obj)) {
			return obj;
		} else {
			return null;
		}
	}

	/**
	 * Create an object factory using a [logical] class name.
	 * <p>
	 *
	 * <p>
	 * This will attempt to create a factory for the class in the following
	 * order:
	 * <ul>
	 * <li>If a class named <em>&lt;class-name&gt</em><code>Factory</code>
	 * exists and implements <code>ConfigurableFactory</code>, return an
	 * instance of it.
	 *
	 * <li>If a class named <em>&lt;class-name&gt</em> exists and implements
	 * <code>ConfigurableFactory</code>, return an instance of it.
	 *
	 * <li>If a class named <em>&lt;class-name&gt</em> exists and accepts a
	 * constructor with ConfigurableFactoryContext, return a factory that
	 * creates an instance with that constructor when used.
	 *
	 * <li>If a class named <em>&lt;class-name&gt</em> exists and has a
	 * default constructor, return a factory that creates an instance with that
	 * constructor when used.
	 *
	 * <li>Returns <code>null</code>,
	 * </ul>
	 *
	 * @param className
	 *            A base class name to load.
	 *
	 * @return A factory, or <code>null</code> if no valid class was found.
	 */
	public static ConfigurableFactory getFactory(final String className) {
		Class< ? > clazz;
		/*
		 * First the <class>Factory form
		 */
		try {
			clazz = Class.forName(className + "Factory");

			/*
			 * Is it a ConfigurableFactory?
			 */
			if (ConfigurableFactory.class.isAssignableFrom(clazz)) {
				try {
					return (ConfigurableFactory) clazz.getDeclaredConstructor().newInstance();
				} catch (final InstantiationException ex) {
					throw new IllegalArgumentException(
							"Class is not instantiatable: " + clazz.getName(),
							ex);
				} catch (final IllegalAccessException ex) {
					throw new IllegalArgumentException(
							"Unable to access class: " + clazz.getName(), ex);
				} catch (final NoSuchMethodException ex) {
					throw new IllegalArgumentException("Method not found: "
							+ clazz.getName(), ex);
				} catch (final InvocationTargetException ex) {
					throw new IllegalArgumentException("Invocation failure: "
							+ clazz.getName(), ex);
				}
			}
		} catch (final ClassNotFoundException ex) {
			// Fall through
		}

		/*
		 * Now <class> directly
		 */
		try {
			clazz = Class.forName(className);

			/*
			 * Is it a ConfigurableFactory?
			 */
			if (ConfigurableFactory.class.isAssignableFrom(clazz)) {
				try {
					return (ConfigurableFactory) clazz.getDeclaredConstructor().newInstance();
				} catch (final InstantiationException ex) {
					throw new IllegalArgumentException(
							"Class is not instantiatable: " + className, ex);
				} catch (final IllegalAccessException ex) {
					throw new IllegalArgumentException(
							"Unable to access class: " + className, ex);
				} catch (final NoSuchMethodException ex) {
					throw new IllegalArgumentException("Method not found: "
							+ clazz.getName(), ex);
				} catch (final InvocationTargetException ex) {
					throw new IllegalArgumentException("Invocation failure: "
							+ clazz.getName(), ex);
				}
			}

			/*
			 * Look for <Class>(ConfigurableFactoryContext) constructor.
			 */
			try {
				return new ACFactory(
						clazz.getConstructor(new Class< ? >[] { ConfigurableFactoryContext.class }));
			} catch (final NoSuchMethodException ex) {
				// Fall through
			}

			/*
			 * Look for <Class>() constructor.
			 */
			try {
				/*
				 * Trigger's NoSuchMethodException if missing.
				 */
				clazz.getConstructor(new Class[] {});

				return new DCFactory(clazz);
			} catch (final NoSuchMethodException ex) {
				// Fall through
			}
		} catch (final ClassNotFoundException ex) {
			// Fall through
		}

		return null;
	}

	//
	//

	/**
	 * A wrapper factory for a <code>ConfigurableFactoryContext</code>
	 * parameter constructor.
	 */
	protected static class ACFactory implements ConfigurableFactory {

		protected Constructor< ? > cnstr;

		public ACFactory(final Constructor< ? > cnstr) {
			this.cnstr = cnstr;
		}

		//
		// ConfigurableFactory
		//

		@Override
		public Object create(final ConfigurableFactoryContext ctx) {
			try {
				return cnstr.newInstance(new Object[] { ctx });
			} catch (final InstantiationException ex) {
				throw new IllegalArgumentException(
						"Class is not instantiatable: "
								+ cnstr.getDeclaringClass().getName(), ex);
			} catch (final IllegalAccessException ex) {
				throw new IllegalArgumentException("Unable to access class: "
						+ cnstr.getDeclaringClass().getName(), ex);
			} catch (final InvocationTargetException ex) {
				throw new IllegalArgumentException("Error creating class: "
						+ cnstr.getDeclaringClass().getName(), ex);
			}
		}
	}

	/**
	 * A wrapper factory that uses the default constructor of a class.
	 */
	protected static class DCFactory implements ConfigurableFactory {

		protected Class< ? > clazz;

		public DCFactory(final Class< ? > clazz) {
			this.clazz = clazz;
		}

		//
		// ConfigurableFactory
		//

		@Override
		public Object create(final ConfigurableFactoryContext ctx) {
			try {
				return clazz.getDeclaredConstructor().newInstance();
			} catch (final InstantiationException ex) {
				throw new IllegalArgumentException(
						"Class is not instantiatable: " + clazz.getName(), ex);
			} catch (final IllegalAccessException ex) {
				throw new IllegalArgumentException("Unable to access class: "
						+ clazz.getName(), ex);
			} catch (final NoSuchMethodException ex) {
				throw new IllegalArgumentException("Method not found: "
						+ clazz.getName(), ex);
			} catch (final InvocationTargetException ex) {
				throw new IllegalArgumentException("Invocation failure: "
						+ clazz.getName(), ex);
			}
		}
	}
}
