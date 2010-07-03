package games.stendhal.client.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Factory to create all known {@link SlashAction}s that open a specified URL in the browser
 *  
 * @author madmetzger
 */
public class BareBonesBrowserLaunchCommandsFactory {
	
	private static Map<String, String> commandsAndUrls;
	
	private static void initialize() {
		commandsAndUrls = new HashMap<String, String>();
		commandsAndUrls.put("atlas", "http://stendhalgame.org/wiki/StendhalAtlas");
		commandsAndUrls.put("faq", "http://stendhalgame.org/wiki/StendhalFAQ");
		commandsAndUrls.put("manual", "http://stendhalgame.org/wiki/Stendhal_Manual");
		commandsAndUrls.put("rules", "http://stendhalgame.org/wiki/Stendhal_Rules");
		commandsAndUrls.put("changepassword", "https://stendhalgame.org/account/change-password.html");
		commandsAndUrls.put("loginhistory", "https://stendhalgame.org/account/history.html");
		commandsAndUrls.put("merge", "https://stendhalgame.org/account/merge.html");
		commandsAndUrls.put("halloffame", "https://stendhalgame.org/world/hall-of-fame/active_overview.html");
	}

	/**
	 * creates {@link SlashAction}s for all in initialize specified values 
	 * @return map of the created actions
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
