package games.stendhal.client.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * a very simple http client
 *
 * @author hendrik
 */
public class HttpClient {
	private static Logger logger = Logger.getLogger(HttpClient.class);
	
	public InputStream openInputStream(String urlString) {
		try {
	        URL url = new URL(urlString);
	        HttpURLConnection.setFollowRedirects(false);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setConnectTimeout(1500);  // 1.5 secs
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	        	logger.warn("HttpServer returned an error code: " + connection.getResponseCode());
	        	return null;
	        }
	        return connection.getInputStream();
	    } catch (Exception e) {
	        logger.warn("Error connecting to http-Server: ", e);
	    }
	    return null;
	}

	/**
	 * fetches the first line of a file using http
	 *
	 * @param urlString The url of the file to fetch
	 * @return the first line
	 */
	public String fetchFirstLine(String urlString) {
		String line = null;
		try {
	        URL url = new URL(urlString);
	        HttpURLConnection.setFollowRedirects(false);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setConnectTimeout(1500);  // 1.5 secs
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            line = br.readLine();
	        } else {
	        	logger.warn("HttpServer returned an error code: " + connection.getResponseCode());
	        }
	        br.close();
	        connection.disconnect();
	    } catch (Exception e) {
	        logger.warn("Error connecting to http-Server: ", e);
	    }
	    return line;
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