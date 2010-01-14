package games.stendhal.bot.textclient;

import games.stendhal.client.scripting.ChatLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * Reads and handles the input
 *
 * @author hendrik
 */
public class InputReader implements Runnable {
	private static Logger logger = Logger.getLogger(InputReader.class);

	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				ChatLineParser.parseAndHandle(line);
				
			} catch (IOException e) {
				logger.error(e, e);
				System.exit(0);
			}
		}
	}
}
