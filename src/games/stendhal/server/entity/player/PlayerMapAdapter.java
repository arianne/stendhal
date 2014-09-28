package games.stendhal.server.entity.player;

import games.stendhal.common.MathHelper;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * makes player properties available using the Map interface
 *
 * @author hendrik
 */
public class PlayerMapAdapter implements Map<String, String> {
	private static Logger logger = Logger.getLogger(PlayerMapAdapter.class);
	private Player player;
	
	/**
	 * a player object to make available using the map interface
	 *
	 * @param player Player
	 */
	public PlayerMapAdapter(Player player) {
		this.player = player;
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean containsKey(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean containsValue(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public Set<java.util.Map.Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException();
	}

	public String get(Object obj) {
		if (obj == null || ! (obj instanceof String)) {
			return null;
		}
		String key = (String) obj;

		if (key.equals("name")) {
			return player.getName();
		} else if (key.startsWith("quest.")) {
			return getQuestValue(key);
		} else {
			logger.warn("Unknown key: " + key, new Throwable());
			// Extend here
		}
		return null;
	}

	/**
	 * extracts the quest value, supporting both plain questslots and questslot:index
	 *
	 * @param key questslot or questslot:index
	 * @return value
	 */
	private String getQuestValue(String key) {
		String questslot = key.substring(6);
		int pos = questslot.indexOf(":");
		if (pos < 0) {
			return player.getQuest(questslot);
		} else {
			return player.getQuest(questslot.substring(0, pos), MathHelper.parseInt(questslot.substring(pos + 1)));
		}
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	public String put(String arg0, String arg1) {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends String, ? extends String> arg0) {
		throw new UnsupportedOperationException();
	}

	public String remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		throw new UnsupportedOperationException();
	}

	public Collection<String> values() {
		throw new UnsupportedOperationException();
	}

}
