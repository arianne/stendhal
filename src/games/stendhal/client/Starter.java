package games.stendhal.client;

import java.lang.reflect.Method;

import javax.swing.JOptionPane;

/**
 * This class can be compiled with a lower version of Java
 * and will display an error message if the java version
 * is too old.
 *
 * @author hendrik
 */
public class Starter {

	/**
	 * Starts stendhal
	 *
	 * @param args args
	 */
	public static void main(String[] args) {
		try {
			String version = System.getProperty("java.specification.version");
			if (Float.parseFloat(version) < 1.5f) {
				JOptionPane.showMessageDialog(null, "You need at least Java 1.5.0 (also known as 5.0) but you only have " + version + ". You can download it at http://java.sun.com");
			}
		} catch (RuntimeException e) {
			// ignore
		}

		try {
			// invoke real client with reflection in order to prevent
			// a class-load-time dependency.

			// get class
			Class clazz = Class.forName("games.stendhal.client.stendhal");

			// get param types of main method
			Class[] paramTypes = new Class[1];
			paramTypes[0] = args.getClass();

			// get param values of main method
			Object[] params = new Object[1];
			params[0] = args;

			// get method and invoke it
			Method method = clazz.getMethod("main", paramTypes);
			method.invoke(null, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
