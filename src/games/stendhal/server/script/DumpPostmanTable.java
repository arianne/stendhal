package games.stendhal.server.script;

import java.io.FileInputStream;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.Iterator;
import java.util.Properties;
import java.util.List;


/**
 * For dumping postman's table straight to the server log in 'csv' format
 *  
 */
public class DumpPostmanTable extends ScriptImpl {
	
	/** Where is the postman file and what is it called?*/
	private static final String STENDHAL_POSTMAN_XML = ".stendhal-postman.xml";
	
	private final Properties messages = new Properties();
	
	/**
	 * Executes the script to dump the table if it can find the file
	 * 
	 * @param admin
	 *            The player (admin) executing script.
	 * @param args
	 *            The arguments (should be none for this).
	 */
	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);
		try {
			this.messages.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_XML));
		} catch (final Exception e) {
			admin.sendPrivateText("Could not find postman file");
		}
		
		final Iterator< ? > itr = messages.keySet().iterator();
		while (itr.hasNext()) {
			final String key = itr.next().toString();
			String target = key.substring(0,key.indexOf("!"));
			String source = key.substring(key.indexOf("!") + 1);
			String message = messages.getProperty(key);
			
			// messages got grouped together, but the line breaks will confuse the csv file: replace new lines with spaces.
			message = message.replace("\n", " ");
			// replace any double quotes in there with two single quotes so we don't confuse mysql on import
			message =  message.replace("\"", "''");
			// replace any escape characters 
			message =  message.replace("\\", "\\\\");
			// well, why not clean it now.
			message = message.trim();
			System.out.println("\"" + source + "\",\"" + target  + "\",\"" + message + "\"");

			itr.remove();

		}
	}

}
