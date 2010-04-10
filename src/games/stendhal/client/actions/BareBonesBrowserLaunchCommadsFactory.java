package games.stendhal.client.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Factory to create all known {@link SlashAction}s that open a specified URL in the browser
 *  
 * @author madmetzger
 */
public class BareBonesBrowserLaunchCommadsFactory {
	
	private static Map<String, String> commandsAndUrls;
	
	private static void initialize() {
		commandsAndUrls = new HashMap<String, String>();
		commandsAndUrls.put("atlas", "http://stendhalgame.org/wiki/StendhalAtlas");
		commandsAndUrls.put("faq", "http://stendhalgame.org/wiki/StendhalFAQ");
		commandsAndUrls.put("manual", "http://stendhalgame.org/wiki/StendhalManual");
	}
	
	/**
	 * creates {@link SlashAction}s for all in initialize specified values 
	 * @return
	 */
	public static Map<String, SlashAction> createBrowserCommands() {
		initialize();
		Map<String, SlashAction> commandsMap = new HashMap<String, SlashAction>();
		for(Entry<String, String> entry : commandsAndUrls.entrySet()) {
			commandsMap.put(entry.getKey(), new BareBonesBrowserLaunchCommand(entry.getValue()));
		}
		return commandsMap;
	}

}
