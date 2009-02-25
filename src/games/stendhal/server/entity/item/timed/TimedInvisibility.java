package games.stendhal.server.entity.item.timed;

import games.stendhal.server.entity.player.Player;

import java.util.Map;

public class TimedInvisibility extends TimedStackableItem {

	public TimedInvisibility(final TimedStackableItem item) {
		super(item);
	}

	public TimedInvisibility(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	@Override
	public boolean useItem(final Player player) {
		if (player == null) {
			return false;
		}
		player.setInvisible(true);
		return true;
	}

	@Override
	public void itemFinished(final Player player) {
		if (player != null) {
			player.sendPrivateText("You don't feel so secure anymore.");
			player.setInvisible(false);
		}
	}
}
