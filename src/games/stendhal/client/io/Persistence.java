/**
 * 
 */
package games.stendhal.client.io;

import games.stendhal.common.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Allows transparent access to files. Subclasses implement Persistence
 * for normal and webstart environment.
 *
 * @author hendrik
 */
public abstract class Persistence {
	private static Persistence instance = null;

	/**
	 * Returns the Persistence manager for this environmen
	 *
	 * @return Persistence
	 */
	public static Persistence get() {
		if (instance == null) {
			if (Debug.WEB_START_SANDBOX) {
				// TODO: reflection
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
