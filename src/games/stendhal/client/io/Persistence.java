package games.stendhal.client.io;

import games.stendhal.common.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * Allows transparent access to files. Subclasses implement Persistence
 * for normal and webstart environment.
 *
 * @author hendrik
 */
public abstract class Persistence {
	private static Logger logger = Logger.getLogger(Persistence.class);
	private static Persistence instance = null;

	/**
	 * Returns the Persistence manager for this environmen
	 *
	 * @return Persistence
	 */
	public static Persistence get() {
		if (instance == null) {
			if (Debug.WEB_START_SANDBOX) {
				try {
					// we use reflection to prevent any runtime dependency on jnlp.jar
					// outside webstart. So we do not have to distribute jnlp.jar
					Class clazz = Class.forName("games.stendhal.client.io.WebstartPersistence");
					instance = (Persistence) clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace(System.err);
					logger.error(e, e);
				}
			} else {
				instance = new FileSystemPersistence();
			}
		}
		return instance;
	}
	
	/**
	 * Gets an input stream to this "virtual" file
	 *
	 * @param filename filename (without path)
	 * @return InputStream
	 * @throws IOException on io error
	 */
	public abstract InputStream getInputStream(String filename) throws IOException;

	/**
	 * Gets an output stream to this "virtual" file
	 *
	 * @param filename filename (without path)
	 * @return InputStream
	 * @throws IOException on io error
	 */
	public abstract OutputStream getOutputStream(String filename) throws IOException;

}
