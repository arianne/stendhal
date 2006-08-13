package games.stendhal.client.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

/**
 * Persitence with webstart
 *
 * @author hendrik
 */
public class WebstartPersistence {
	private PersistenceService ps = null;
	private BasicService bs = null;
	private URL codebase = null;

	/**
	 * Creates a instance of class
	 */
	public WebstartPersistence() {
		try {
			ps = (PersistenceService) ServiceManager
					.lookup("javax.jnlp.PersistenceService");
			bs = (BasicService) ServiceManager
					.lookup("javax.jnlp.BasicService");

			if (ps != null && bs != null) {
				codebase = bs.getCodeBase();
			}

		} catch (UnavailableServiceException e) {
			e.printStackTrace(System.err);
			ps = null;
			bs = null;
		}
	}

	/**
	 * Gets an input stream to this "virtual" file
	 *
	 * @param filename filename (without path)
	 * @return InputStream
	 * @throws IOException on io error
	 */
	public InputStream getInputStream(String filename) throws IOException {
		URL muffinURL = new URL(codebase.toString() + filename);
		FileContents fc = ps.get(muffinURL);
		InputStream is = fc.getInputStream();
		return is;
	}

	/**
	 * Gets an output stream to this "virtual" file
	 *
	 * @param filename filename (without path)
	 * @return InputStream
	 * @throws IOException on io error
	 */
	public OutputStream getOutputStream(String filename) throws IOException {
		URL muffinURL = new URL(codebase.toString() + filename);
		try {
			ps.delete(muffinURL);
		} catch (Exception e) {
			// ignore
		}
		ps.create(muffinURL, 5000);
		FileContents fc = ps.get(muffinURL);
		OutputStream os = fc.getOutputStream(false);
		return os;
	}

}
