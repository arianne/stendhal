//*****************************************************************************
//*****************************************************************************
//
//                               Important note
//
// Please note that this file is compiled using Java 1.2 in the build-script
// in order to display a dialogbox to the user in case an old version of java
// is used. As we compile it with Java 1.2 no new features may be used in this
// class.
// 
//*****************************************************************************
//*****************************************************************************
package games.stendhal.client.update;

import java.lang.reflect.Method;

/**
 * @author hendrik
 */
public class ClientRunner {

	/**
	 * Starts stendhal.
	 * 
	 * @param args
	 *            args
	 */
	public static void run(final String[] args) {


		try {
			// invoke real client with reflection in order to prevent
			// a class-load-time dependency.

			// get class and create an object of it
			final Class<?> clazz = Class.forName("games.stendhal.client.update.Bootstrap");
			final Object object = clazz.newInstance();

			// get param values of boot method
			final Object[] params = new Object[2];
			params[0] = "games.stendhal.client.stendhal";
			params[1] = args;

			// get types of params
			final Class<?>[] paramTypes = new Class[2];
			for (int i = 0; i < params.length; i++) {
				paramTypes[i] = params[i].getClass();
			}

			// get method and invoke it
			// IGNORE THIS WARNING BECAUSE THIS CODE NEEDS TO BE COMPILED FOR
			// OLDER JREs.
			final Method method = clazz.getMethod("boot", paramTypes);
			method.invoke(object, params);
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
