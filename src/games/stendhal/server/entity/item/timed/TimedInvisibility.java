package games.stendhal.server.entity.item.timed;

import java.util.Map;

import games.stendhal.server.entity.player.Player;

public class TimedInvisibility extends TimedStackableItem {

	public TimedInvisibility(TimedStackableItem item) {
		super(item);
	}

	public TimedInvisibility(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	@Override
	public boolean useItem(Player player) {
		if (player == null) {
			return false;
		}
		player.setInvisible(true);
		return true;
	}

	@Override
	public void itemFinished(Player player) {
		if (player != null) {
			player.sendPrivateText("You don't feel so secure anymore.");
			player.setInvisible(false);
		}
	}
}
