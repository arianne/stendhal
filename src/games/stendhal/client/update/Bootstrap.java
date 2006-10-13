package games.stendhal.client.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

/**
 * Starts a program after doing some classpath magic.
 *
 * @author hendrik
 */
public class Bootstrap {
	// discover folder for .jar-files
	private static String pathSep = System.getProperty("file.separator");
	private static String jarFolder = System.getProperty("user.home") + pathSep + "stendhal" + pathSep + "jar" + pathSep;

	/**
	 * Sets a dynamic classpath up and returns a Class reference loaded from it
	 *
	 * @param className  name of class to load form the dynamic path
	 * @return Class-object
	 * @throws Exception if an unexpected error occurs
	 */
	private static Class getMainClass(String className) throws Exception {
		Class clazz = null;
		try {

			// load jar.properties
			String propFile = jarFolder + "jar.properties";
			Properties prop = new Properties();
			InputStream is = new FileInputStream(propFile);
			prop.load(is);
			is.close();

			// get list of .jar-files
			String jarNameString = prop.getProperty("load", "jar.properties does not contain \"load=\" line");
			List<URL> jarFiles = new LinkedList<URL>();
			StringTokenizer st = new StringTokenizer(jarNameString, ",");
			while (st.hasMoreTokens()) {
				String filename = st.nextToken();
				jarFiles.add(new File(jarFolder + filename).toURI().toURL());
			}

		    // Create new class loader which the list of .jar-files as classpath
			URL[] urlArray = jarFiles.toArray(new URL[jarFiles.size()]);
		    ClassLoader loader = new URLClassLoader(urlArray);

		    // load class through new loader
		    clazz = loader.loadClass(className);
            		
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Something nasty happend while trying to build classpath.\r\nPlease open a bug report at http://sf.net/projects/arianne with this error message:\r\n" + e);
		}

		if (clazz == null) {
			// fallback to normal classloading
			clazz = Class.forName(className);
		}
		return clazz;
	}

	/**
	 * Starts the main-method of specified class after
	 * dynamically building the classpath
	 *
	 * @param className name of class with "main"-method
	 * @param args command line arguments
	 */
	public void boot(String className, String[] args) {
		try {

			// build classpath
			// switch comment-markers on the next to lines, to use new code
			// Class clazz = getMainClass(className);
			Class clazz = Class.forName(className);

			// get method and invoke it
			Method method = clazz.getMethod("main", args.getClass());
			method.invoke(null, (Object) args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
