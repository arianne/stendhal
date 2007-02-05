package games.stendhal.client.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
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
	private Properties bootPropOrg = null;

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
		    		clazz = parent.loadClass(name);
			    }
			}
			if (resolve) {
			    resolveClass(clazz);
			}
			return clazz;
	    }

	    @Override
		public URL getResource(String name) {
			ClassLoader parent = super.getParent();
			URL url = findResource(name);
			if (url == null) {
				if (parent != null) {
					url = parent.getResource(name);
				}
			}
			return url;
		}
	}


	/**
	 * saves modifed boot properties to disk
	 *
	 * @throws IOException if an IO-error occurs
	 */
	public void saveBootProp() throws IOException {
		// only try to save it, if it was changed (so that we do not have to
		// care about all the things which could go wrong unless an update
		// was done this time.
		if (!bootProp.equals(bootPropOrg)) {
			String propFile = jarFolder + "jar.properties";
			OutputStream os = new FileOutputStream(propFile);
			bootProp.store(os, "Stendhal Boot Configuration");
			os.close();
		}
	}

	private void init() {
		// discover folder for .jar-files
		pathSep = System.getProperty("file.separator");
		jarFolder = System.getProperty("user.home") + pathSep + "stendhal" + pathSep + "jar" + pathSep;
		File folder = new File(jarFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		System.setProperty("log4j.ignoreTCL", "true");
	}
	
	/**
	 * Sets a dynamic classpath up and returns a Class reference loaded from it
	 *
	 * @param className  name of class to load form the dynamic path
	 * @return Class-object
	 * @throws Exception if an unexpected error occurs
	 */
	private ClassLoader createClassloader() throws Exception {
		// load jar.properties
		String propFile = jarFolder + "jar.properties";
		bootProp = new Properties();
		List<URL> jarFiles = new LinkedList<URL>();
		if (new File(propFile).canRead()) {
			InputStream is = new FileInputStream(propFile);
			bootProp.load(is);
			bootPropOrg = (Properties) bootProp.clone();
			is.close();

			// get list of .jar-files
			String jarNameString = bootProp.getProperty("load", "");
			StringTokenizer st = new StringTokenizer(jarNameString, ",");
			while (st.hasMoreTokens()) {
				String filename = st.nextToken();
				jarFiles.add(new File(jarFolder + filename).toURI().toURL());
			}
			System.out.println("our classpath: " + jarNameString);
		} else {
			System.out.println("no jar.properties");
		}
		
		// add boot classpath at the end so that those classes
		// are loaded by our classloader as well (otherwise the dependencies
		// would be loaded by the system classloader as well).
		String vmClasspath = System.getProperty("java.class.path", "");
		System.out.println("vm  classpath: " + vmClasspath);
		StringTokenizer st = new StringTokenizer(vmClasspath, ":;");
		while (st.hasMoreTokens()) {
			String filename = st.nextToken();
			jarFiles.add(new File(filename).toURI().toURL());
		}

	    // Create new class loader which the list of .jar-files as classpath
		URL[] urlArray = jarFiles.toArray(new URL[jarFiles.size()]);
	    ClassLoader loader = new ButtomUpOrderClassLoader(urlArray, this.getClass().getClassLoader());
	    return loader;
	}

	/**
	 * Do the whole start up process in a privilaged block
	 */
	private class PrivilagedBoot<T> implements PrivilegedAction<T> {
		private String className = null;
		private String[] args = null;

		/**
		 * Creates a PrivilagedBoot object
		 *
		 * @param className className to boot
		 * @param args arguments for the main-method
		 */
		public PrivilagedBoot(String className, String[] args) {
			this.className = className;
			this.args = args;
		}

		/**
		 * Handles the update procedure
		 */
		private void handleUpdate() {
			// invoke update handling first
			try {
				ClassLoader classLoader = createClassloader();
				// is this the initial download (or do we already have the program downloaded)?
				boolean initialDownload = false;
				try {
					classLoader.loadClass(className);
				} catch (ClassNotFoundException e) {
					initialDownload = true;
				}
				
				// start update handling
				Class clazz = classLoader.loadClass("games.stendhal.client.update.UpdateManager");
				Method method = clazz.getMethod("process", String.class, Properties.class, Boolean.class);
				method.invoke(clazz.newInstance(), jarFolder, bootProp, initialDownload);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Something nasty happend while trying to build classpath for UpdateManager.\r\nPlease open a bug report at http://sf.net/projects/arianne with this error message:\r\n" + e);
				e.printStackTrace(System.err);
			}
		}

		/**
		 * store boot prop (if they ware altered during update)
		 */
		private void storeBootProp() {
			try {
				saveBootProp();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Sorry, an error occured while downloading the update. Could not write bootProperties");
			}
		}

		/**
		 * load program
		 */
		private void loadProgram() {
			// regenerate classloader stuff, because in handleUpdate additional
			// .jar-files may have been added
			
			try {
				ClassLoader classLoader = createClassloader();
				Class clazz = classLoader.loadClass(className);
				Method method = clazz.getMethod("main", args.getClass());
				method.invoke(null, (Object) args);
			} catch (Throwable e) {
				if (e instanceof InvocationTargetException) {
					unexspectedErrorHandling(e);
				} else {
					JOptionPane.showMessageDialog(null, "Something nasty happend while trying to build classpath.\r\nPlease open a bug report at http://sf.net/projects/arianne with this error message:\r\n" + e);
					e.printStackTrace(System.err);
					try {
						Class clazz = Class.forName(className);
						Method method = clazz.getMethod("main", args.getClass());
						method.invoke(null, (Object) args);
					} catch (Exception err) {
						err.printStackTrace(System.err);
					}
				}
			}

		}
		
		public T run() {
			init();
			handleUpdate();
			storeBootProp();
			loadProgram();
			return null;
		}
	}

	/**
	 * Is this package signed? Note it does not validate the signature, just
	 * looks for the presents of one.
	 *
	 * @return true, if there is some kind of signature; false otherwise
	 */
	private boolean isSigned() {
		URL url = Bootstrap.class.getClassLoader().getResource("META-INF/MIGUELAN.SF");
		return url != null;
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
			System.setSecurityManager(null);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}

		if (isSigned()) {
			// official client, look for updates and integrate additinal .jar files
			System.err.println("Integrating old updates and looking for new ones");
			AccessController.doPrivileged(new PrivilagedBoot<Object>(className, args));
		} else {
			// self build client, do not try to update it
			System.err.println("Self build client, starting without update .jar-files");
			try {
				Class clazz = Class.forName(className);
				Method method = clazz.getMethod("main", args.getClass());
				method.invoke(null, (Object) args);
			} catch (Exception err) {
				JOptionPane.showMessageDialog(null, "Something nasty happend while trying to start your self build client: " + err);
				err.printStackTrace(System.err);
			}
		}
	}

	private void unexspectedErrorHandling(Throwable t) {
		// unwrap chained expections
		Throwable e = t;
		while (e.getCause() != null) {
			e = e.getCause();
		}

		e.printStackTrace();
		
		if (e instanceof OutOfMemoryError) {
			JOptionPane.showMessageDialog(null, "Sorry, an OutOfMemoryError occured. Please restart Stendhal.");
		} else {
			JOptionPane.showMessageDialog(null, "An unexspected error occured.\r\nPlease open a bug report at http://sf.net/projects/arianne with this error message:\r\n" + e);
		}
		System.exit(1);
	}
}
