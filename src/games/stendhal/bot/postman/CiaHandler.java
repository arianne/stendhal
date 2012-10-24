package games.stendhal.bot.postman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * listens for udp messages
 *
 * @author hendrik
 */
public class CiaHandler {

	/**
	 * processes an CIA email
	 *
	 * @param is input stream delivering the email
	 * @return IRC command
	 * @throws IOException in case of an input/output error
	 */
	public String process(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		try {
			skipHeader(br);

			// TODO: parse xml
			// TODO: format message

			return null;
		} finally {
			br.close();
		}
	}

	/**
	 * skips the mail header
	 *
	 * @param br BufferedReader
	 * @throws IOException in case of an input/output error
	 */
	private void skipHeader(BufferedReader br) throws IOException {
		String line = br.readLine();
		while ((line != null) && (!line.trim().isEmpty())) {
			line = br.readLine();
		}
	}
}
