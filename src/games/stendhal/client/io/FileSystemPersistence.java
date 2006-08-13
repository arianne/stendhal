package games.stendhal.client.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Normal persistence using files
 *
 * @author hendrik
 */
public class FileSystemPersistence extends Persistence {
	private String basedir = System.getProperty("user.home") + "/"; 

	/**
	 * creates a "normal" FileSystemPersistence
	 */
	FileSystemPersistence() {
		// package visibile only
	}

	@Override
	public InputStream getInputStream(String filename) throws IOException {
		return new FileInputStream(basedir + filename);
	}

	@Override
	public OutputStream getOutputStream(String filename) throws IOException {
		return new FileOutputStream(basedir + filename);
	}

}
