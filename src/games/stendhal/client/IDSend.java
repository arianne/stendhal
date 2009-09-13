package games.stendhal.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.log4j.Logger;

import marauroa.common.game.RPAction;
import marauroa.common.io.Persistence;

public class IDSend {
	
	/** the logger instance. */
	private static final Logger logger = Logger
			.getLogger(IDSend.class);
	
	/** filename for the settings persistence. */
	private static final String FILE_NAME = "cid";
	
	private static String computerID = null;
	
	public static void send() {
		readID();
		if(!haveID()) {
			generateID();
			saveID();
		}
		
		if(!haveID()) {
			return;
		}
		
		final RPAction action = new RPAction();

		action.put("type", "cid");
		action.put("id", computerID);

		StendhalClient.get().send(action);
		
	}
	
	private static void generateID() {
		computerID = generateRandomString();
	}
	
	/**
	* generates a random string
	*
	* @return random string
	*/
	private static String generateRandomString() {
		String chars =
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$/()@";
		StringBuffer res = new StringBuffer();
		Random rnd = new SecureRandom();
		for (int i = 0; i < 32; i++) {
			int pos = (int) (rnd.nextFloat() * chars.length());
			res.append(chars.charAt(pos));
		}
	
		return res.toString();
	}
	
	private static boolean haveID() {
		if(computerID == null) {
			return false;
		}
		return true;
	}
	
	private static void readID() {
		try {
			final InputStream is = Persistence.get().getInputStream(true, "stendhal",
					FILE_NAME);

		    BufferedInputStream bis = new BufferedInputStream(is);
		    ByteArrayOutputStream buf = new ByteArrayOutputStream();
		    int result = bis.read();
		    while(result != -1) {
		      byte b = (byte)result;
		      buf.write(b);
		      result = bis.read();
		    }        
		    computerID = buf.toString().trim();
		    
			is.close();
		} catch (final IOException e) {
			// ignore exception
		}
	}
	private static void saveID() {
		try {
			final OutputStream os = Persistence.get().getOutputStream(true,
					"stendhal", FILE_NAME);
			final OutputStreamWriter writer = new OutputStreamWriter(os);
			writer.write(computerID);
			writer.close();
		} catch (final IOException e) {
			// ignore exception
			logger.error("Can't write " + stendhal.STENDHAL_FOLDER + FILE_NAME,
					e);
		}
		
	}
}
