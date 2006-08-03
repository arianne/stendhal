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

			// invoke real client with reflection in order to prevent
			// a class-load-time dependency.
			Class clazz = Class.forName("games.stendhal.client.stendhal");
			Method method = clazz.getMethod("main", String[].class);
			method.invoke(null, (Object) args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
