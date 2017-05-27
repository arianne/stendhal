package games.stendhal.server.entity.player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.rp.DaylightPhase;

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

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String get(Object obj) {
		if (obj == null || ! (obj instanceof String)) {
			return null;
		}
		String key = (String) obj;

		if (key.equals("name")) {
			return player.getName();
		} else if (key.startsWith("quest.")) {
			return getQuestValue(key);
		} else if (key.equals("daylightphase")) {
			return DaylightPhase.current().getGreetingName();
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

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String put(String arg0, String arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> values() {
		throw new UnsupportedOperationException();
	}

}
