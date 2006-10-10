package games.stendhal.client.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
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

	private static Class getMainClass(String className) throws Exception {
		Class clazz = null;
		try {
			// discover folder for .jar-files
			String pathSep = System.getProperty("file.separator");
			String jarFolder = System.getProperty("user.home") + pathSep + "stendhal" + pathSep + "jar" + pathSep;

			// load jar.properties
			String propFile = jarFolder + "jar.properties";
			Properties prop = new Properties();
			InputStream is = new FileInputStream(propFile);
			prop.load(is);
			is.close();

			// get list of .jar-files
			String jarNameString = prop.getProperty("load", "jar.properties does not contain \"load=\" line");
			List jarNameList = new LinkedList();
			StringTokenizer st = new StringTokenizer(jarNameString, ",");
			while (st.hasMoreTokens()) {
				jarNameList.add(st.nextToken());
			}
			// convert .jar-filename-list to URL-array
		    URL[] jarFiles = new URL[jarNameList.size()];
		    Iterator itr = jarNameList.iterator();
		    int i = 0;
		    while (itr.hasNext()) {
		    	jarFiles[i] = new File(jarFolder + itr.next()).toURI().toURL();
		    	i++;
		    }
		    // Create new class loader which the list of .jar-files as classpath 
		    ClassLoader loader = new URLClassLoader(jarFiles);

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
