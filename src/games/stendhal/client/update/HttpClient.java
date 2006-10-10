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
	private String urlString = null;
	private HttpURLConnection connection = null;
	private InputStream is = null;
	
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
	 * fetches the first line of a file using http
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