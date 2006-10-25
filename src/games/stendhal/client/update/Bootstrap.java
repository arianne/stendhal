package games.stendhal.client.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	private String pathSep = null;
	private String jarFolder = null;
	private Properties bootProp = null;

	/**
	 * An URLClassLoader with does load its classes first and only delegates
	 * missing classes to the parent classloader (default is the other way round)
	 */
	private static class ButtomUpOrderClassLoader extends URLClassLoader {
	    private ButtomUpOrderClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
	    }

	    @Override
		protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException  {
			ClassLoader parent = super.getParent();
			Class clazz = findLoadedClass(name);
			if (clazz == null) {
			    try {
			    	clazz = findClass(name);
			    } catch (ClassNotFoundException e) {
			    	try { 
			    		clazz = parent.loadClass(name);
				    } catch (ClassNotFoundException e2) {
				    	e.printStackTrace();
				    }
			    }
			}
			if (resolve) {
			    resolveClass(clazz);
			}
			return clazz;
	    }
	}

	/**
	 * the folder where the .jar files should be stored
	 *
	 * @return path to folder
	 */
	public String getJarFolder() {
		return jarFolder;
	}

	/**
	 * Boot configuration properties
	 *
	 * @return bootProp
	 */
	public Properties getBootProp() {
		return bootProp;
	}

	/**
	 * saves modifed boot properties to disk
	 *
	 * @throws IOException if an IO-error occurs
	 */
	public void saveBootProp() throws IOException {
		String propFile = jarFolder + "jar.properties";
		bootProp = new Properties();
		OutputStream os = new FileOutputStream(propFile);
		bootProp.store(os, "Stendhal Boot Configuration");
		os.close();
	}

	private void init() {
		// discover folder for .jar-files
		pathSep = System.getProperty("file.separator");
		jarFolder = System.getProperty("user.home") + pathSep + "stendhal" + pathSep + "jar" + pathSep;
		File folder = new File(jarFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}
	
	/**
	 * Sets a dynamic classpath up and returns a Class reference loaded from it
	 *
	 * @param className  name of class to load form the dynamic path
	 * @return Class-object
	 * @throws Exception if an unexpected error occurs
	 */
	private Class getMainClass(String className) throws Exception {
		Class clazz = null;
		try {

			// load jar.properties
			String propFile = jarFolder + "jar.properties";
			bootProp = new Properties();
			if (new File(propFile).canRead()) {
				InputStream is = new FileInputStream(propFile);
				bootProp.load(is);
				is.close();
	
				// get list of .jar-files
				String jarNameString = bootProp.getProperty("load", "");
				List<URL> jarFiles = new LinkedList<URL>();
				StringTokenizer st = new StringTokenizer(jarNameString, ",");
				while (st.hasMoreTokens()) {
					String filename = st.nextToken();
					jarFiles.add(new File(jarFolder + filename).toURI().toURL());
				}

				// add boot classpath at the end so that those classes
				// are loaded by our classloader as well (otherwise the dependencies
				// would be loaded by the system classloader as well).
				String vmClasspath = System.getProperty("java.class.path", "");
				st = new StringTokenizer(vmClasspath, ":;");
				while (st.hasMoreTokens()) {
					String filename = st.nextToken();
					jarFiles.add(new File(filename).toURI().toURL());
				}
	
			    // Create new class loader which the list of .jar-files as classpath
				URL[] urlArray = jarFiles.toArray(new URL[jarFiles.size()]);
			    ClassLoader loader = new ButtomUpOrderClassLoader(urlArray, ClassLoader.getSystemClassLoader());
	
			    // load class through new loader
			    clazz = loader.loadClass(className);
			}
            		
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
		init();
		try {
			Class clazz;
			Method method;

			// invoke update first
/*			clazz = getMainClass("games.stendhal.client.update.UpdateManager");
			method = clazz.getMethod("process", String.class, Properties.class);
			method.invoke(clazz.newInstance(), jarFolder, bootProp);
			try {
				saveBootProp();
			} catch (IOException e) {
				UpdateGUI.messageBox("Sorry, an error occured while downloading the update. Could not write bootProperties");
			}
*/

			// load program (regenerate classloader stuff)
			clazz = getMainClass(className);
			method = clazz.getMethod("main", args.getClass());
			method.invoke(null, (Object) args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
