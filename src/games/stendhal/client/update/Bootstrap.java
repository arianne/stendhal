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

	private String pathSep;

	private String jarFolder;

	private Properties bootProp;

	private Properties bootPropOrg;

	/**
	 * An URLClassLoader with does load its classes first and only delegates
	 * missing classes to the parent classloader. (default is the other way
	 * round)
	 */
	private static class ButtomUpOrderClassLoader extends URLClassLoader {

		/**
		 * Creates a buttom up order class loader.
		 * 
		 * @param urls
		 *            classpath
		 * @param parent
		 *            parent classloader
		 */
		ButtomUpOrderClassLoader(final URL[] urls, final ClassLoader parent) {
			super(urls, parent);
		}

		@Override
		protected synchronized Class< ? > loadClass(final String name, final boolean resolve)
				throws ClassNotFoundException {
			final ClassLoader parent = super.getParent();
			Class< ? > clazz = findLoadedClass(name);
			if (clazz == null) {
				try {
					clazz = findClass(name);
				} catch (final ClassNotFoundException e) {
					clazz = parent.loadClass(name);
				}
			}
			if (resolve) {
				resolveClass(clazz);
			}
			return clazz;
		}

		@Override
		public URL getResource(final String name) {
			final ClassLoader parent = super.getParent();
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
	 * saves modifed boot properties to disk.
	 * 
	 * @throws IOException
	 *             if an IO-error occurs
	 */
	public void saveBootProp() throws IOException {
		// only try to save it, if it was changed (so that we do not have to
		// care about all the things which could go wrong unless an update
		// was done this time.
		if (!bootProp.equals(bootPropOrg)) {
			final String propFile = jarFolder + "jar.properties";
			final OutputStream os = new FileOutputStream(propFile);
			bootProp.store(os, "Stendhal Boot Configuration");
			os.close();
		}
	}

	/**
	 * initializes the startup process.
	 */
	void init() {
		// discover folder for .jar-files
		pathSep = System.getProperty("file.separator");

		final String stendhal = ClientGameConfiguration.get("GAME_NAME").toLowerCase();
		System.out.println("GAME: " + stendhal);

		jarFolder = System.getProperty("user.home") + pathSep + stendhal
				+ pathSep + "jar" + pathSep;
		final File folder = new File(jarFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		System.setProperty("log4j.ignoreTCL", "true");
	}

	/**
	 * Sets a dynamic classpath up and returns a Class reference loaded from it.
	 * 
	 * @return ClassLoader object
	 * @throws Exception
	 *             if an unexpected error occurs
	 */
	ClassLoader createClassloader() throws Exception {
		// load jar.properties
		final String propFile = jarFolder + "jar.properties";
		bootProp = new Properties();
		final List<URL> jarFiles = new LinkedList<URL>();
		if (new File(propFile).canRead()) {
			final InputStream is = new FileInputStream(propFile);
			bootProp.load(is);
			bootPropOrg = (Properties) bootProp.clone();
			is.close();

			// get list of .jar-files
			final String jarNameString = bootProp.getProperty("load-0.69", "");
			final StringTokenizer st = new StringTokenizer(jarNameString, ",");
			while (st.hasMoreTokens()) {
				final String filename = st.nextToken();
				jarFiles.add(new File(jarFolder + filename).toURI().toURL());
			}
			System.out.println("our classpath: " + jarNameString);
		} else {
			System.out.println("no jar.properties");
		}

		// add boot classpath at the end so that those classes
		// are loaded by our classloader as well (otherwise the dependencies
		// would be loaded by the system classloader as well).
		final String vmClasspath = System.getProperty("java.class.path", "");
		System.out.println("vm  classpath: " + vmClasspath);
		final StringTokenizer st = new StringTokenizer(vmClasspath, ":;");
		while (st.hasMoreTokens()) {
			final String filename = st.nextToken();
			jarFiles.add(new File(filename).toURI().toURL());
		}

		// Create new class loader which the list of .jar-files as classpath
		final URL[] urlArray = jarFiles.toArray(new URL[jarFiles.size()]);
		final ClassLoader loader = new ButtomUpOrderClassLoader(urlArray,
				this.getClass().getClassLoader());
		return loader;
	}

	/**
	 * Do the whole start up process in a privileged block.
	 * @param <T> 
	 */
	private class PrivilegedBoot<T> implements PrivilegedAction<T> {

		private final String className;

		private final String[] args;

		/**
		 * Creates a PrivilagedBoot object.
		 * 
		 * @param className
		 *            className to boot
		 * @param args
		 *            arguments for the main-method
		 */
		public PrivilegedBoot(final String className, final String[] args) {
			this.className = className;
			this.args = args;
		}

		/**
		 * Handles the update procedure.
		 */
		private void handleUpdate() {
			// invoke update handling first
			try {
				final ClassLoader classLoader = createClassloader();
				// is this the initial download (or do we already have the
				// program downloaded)?
				boolean initialDownload = false;
				try {
					classLoader.loadClass(className);
					classLoader.loadClass("org.apache.Logger");
					classLoader.loadClass("marauroa.common.Logger");
					classLoader.loadClass("marauroa.client.ClientFramework");
					if (classLoader.getResource(ClientGameConfiguration.get("GAME_ICON")) == null) {
						throw new ClassNotFoundException(ClientGameConfiguration.get("GAME_ICON"));
					}
					if (classLoader.getResource("data/gui/offline.png") == null) {
						throw new ClassNotFoundException(ClientGameConfiguration.get("data/gui/offline.png"));
					}
					
				} catch (final ClassNotFoundException e) {
					initialDownload = true;
					System.out.println("Initial Download");
				}

				// start update handling
				final Class< ? > clazz = classLoader.loadClass("games.stendhal.client.update.UpdateManager");
				final Method method = clazz.getMethod("process", String.class, Properties.class, Boolean.class);
				method.invoke(clazz.newInstance(), jarFolder, bootProp,	initialDownload);
			} catch (final SecurityException e) {
				throw e;
			} catch (final Exception e) {
				unexpectedErrorHandling("State: UpdateManager\r\n", e);
			}
		}

		/**
		 * store boot prop (if they were altered during update).
		 */
		private void storeBootProp() {
			try {
				saveBootProp();
			} catch (final IOException e) {
				JOptionPane.showMessageDialog(
					null,
					"Sorry, an error occurred while downloading the update. Could not write bootProperties");
			}
		}

		/**
		 * load program.
		 */
		private void loadProgram() {
			// regenerate classloader stuff, because in handleUpdate additional
			// .jar-files may have been added

			try {
				final ClassLoader classLoader = createClassloader();
				final Class< ? > clazz = classLoader.loadClass(className);
				final Method method = clazz.getMethod("main", args.getClass());
				method.invoke(null, (Object) args);
			} catch (final Throwable e) {
				unexpectedErrorHandling("State: in game\r\n", e);
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
		final URL url = Bootstrap.class.getClassLoader().getResource(
				ClientGameConfiguration.get("UPDATE_SIGNER_FILE_NAME"));
		return url != null;
	}

	/**
	 * Starts the main-method of specified class after dynamically building the
	 * classpath.
	 * 
	 * @param className
	 *            name of class with "main"-method
	 * @param args
	 *            command line arguments
	 */
	public void boot(final String className, final String[] args) {
		try {
			System.setSecurityManager(null);
		} catch (final Throwable t) {
			t.printStackTrace(System.err);
		}

		boolean startSelfBuild = true;
		if (isSigned()) {
			startSelfBuild = false;
			// official client, look for updates and integrate additional .jar
			// files
			System.err.println("Integrating old updates and looking for new ones");
			try {
				AccessController.doPrivileged(new PrivilegedBoot<Object>(
						className, args));
			} catch (final SecurityException e) {
				// partly update
				System.err.println("Got SecurityException most likely because signed jars files from the official distribution have been included into a self build client."
						+ e);
				startSelfBuild = true;
			}
		}

		if (startSelfBuild) {
			// self build client, do not try to update it
			System.err.println("Self build client, starting without update .jar-files");
			try {
				final Class< ? > clazz = Class.forName(className);
				final Method method = clazz.getMethod("main", args.getClass());
				method.invoke(null, (Object) args);
			} catch (final Exception err) {
				err.printStackTrace(System.err);
				JOptionPane.showMessageDialog(null,
						"Something nasty happened while trying to start your self build client: "
								+ err);
			}
		}
	}

	/**
	 * Handles exceptions during program invocation.
	 * 
	 * @param t
	 *            exception
	 */
	void unexpectedErrorHandling(String message, final Throwable t) {
		// unwrap chained exceptions
		Throwable e = t;
		while (e.getCause() != null) {
			e = e.getCause();
		}

		e.printStackTrace();

		if (e instanceof OutOfMemoryError) {
			JOptionPane.showMessageDialog(null,
					"Sorry, an OutOfMemoryError occurred. Please restart Stendhal.");
		} else if (e instanceof LinkageError) {
			final int res = JOptionPane.showConfirmDialog(
					null,
					message + " Sorry an error occurred because of an inconsistent update state. Delete update files so that they are downloaded again after you restart Stendhal?",
					"Stendhal", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (res == JOptionPane.YES_OPTION) {
				bootProp.remove("load");
				bootProp.remove("load-0.69");
				try {
					saveBootProp();
				} catch (final IOException e1) {
					JOptionPane.showMessageDialog(null,	"Could not write jar.properties");
				}
			}
		} else {
			String errorMessage = stacktraceToString(e);
			JOptionPane.showMessageDialog(
					null,
					message + " An unexpected error occurred.\r\nPlease open a bug report at http://sf.net/projects/arianne with this error message:\r\n"
							+ errorMessage);
		}
		System.exit(1);
	}

	/**
	 * converts a Throwable into a string representation 
	 * @param e throwable
	 * @return string
	 */
	String stacktraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.getMessage());
		for (StackTraceElement frame : e.getStackTrace()) {
			sb.append("\r\n");
			sb.append(frame.toString());
		}
		return sb.toString();
	}
}
