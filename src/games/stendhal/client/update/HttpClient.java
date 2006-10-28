package games.stendhal.client.update;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * a very simple http client
 *
 * @author hendrik
 */
public class HttpClient {
	private static Logger logger = Logger.getLogger(HttpClient.class);
	private String urlString = null;
	private HttpURLConnection connection = null;
	private InputStream is = null;

	/**
	 * Creates a HTTP-Client which will connect to the specified URL
	 *
	 * @param url URL to connect to
	 */
	public HttpClient(String url) {
		this.urlString = url;
	}

	private void openInputStream() {
		try {
	        URL url = new URL(urlString);
	        HttpURLConnection.setFollowRedirects(true);
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setConnectTimeout(1500);  // 1.5 secs
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	        	logger.warn("HttpServer returned an error code: " + connection.getResponseCode());
	        	return;
	        }
	        is = connection.getInputStream();
	    } catch (Exception e) {
	        logger.warn("Error connecting to http-Server: ", e);
	    }
	    return;
	}

	/**
	 * Return an InputStream to read the requested file from.
	 * You have to close it using @see close().
	 *
	 * @return InputStream or null on error.
	 */
	public InputStream getInputStream() {
		openInputStream();
		return is;
	}

	/**
	 * Closes the connection and associated streams.
	 */
	public void close() {
		try {
			is.close();
		} catch (IOException e) {
			logger.warn(e, e);
		}
		connection.disconnect();
	}

	/**
	 * fetches the first line of a file using http and closes the
	 * connection automatically.
	 *
	 * @return the first line
	 */
	public String fetchFirstLine() {
		String line = null;
		try {
			openInputStream();
			if (is == null) {
				return null;
			}
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
            line = br.readLine();
	        br.close();
	        connection.disconnect();
	    } catch (Exception e) {
	        logger.warn("Error connecting to http-Server: ", e);
	    }
	    return line;
	}

	/**
	 * fetches a file using http as Properties object and closes the
	 * connection automatically.
	 *
	 * @return the first line
	 */
	public Properties fetchProperties() {
		Properties prop = null;
		openInputStream();
		if (is == null) {
			return prop;
		}
		try {
			prop = new Properties();
			prop.load(is);
		} catch (IOException e) {
			logger.warn(e, e);
		}
		this.close();
		return prop;
	}

	/**
	 * Fetches a file from the HTTP-Server and stores it on disk
	 *
	 * @param filename name of the file to write
	 * @return true on success, false otherwise
	 */
	public boolean fetchFile(String filename) {
		boolean res = false;
		
		openInputStream();
		if (is == null) {
			return res;
		}

		try {
			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(filename));
			copyStream(is, os);
			connection.disconnect();
			res = true;
		} catch (Exception e) {
			res = true;
			logger.warn(e, e);
		}
		return res;
	}

	/**
	 * Copies data from an inputStream to and outputStream and closes both
	 * steams after work
	 *
	 * @param inputStream  stream to read from
	 * @param outputStream stream to write to
	 * @throws IOException on an input/output error
	 */
	private void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[10240];
		int length = inputStream.read(buffer);
		while (length > -1) {
			outputStream.write(buffer, 0, length);
			length = inputStream.read(buffer);
		}
		inputStream.close();
		outputStream.close();
	}
}