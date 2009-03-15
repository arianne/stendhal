package games.stendhal.server.core.engine;

import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.RPObject;

public class ItemLogEntry {
	public RPObject item;
	public RPEntity player;
	public String event;
	public String param1;
	public String param2;
	public String param3;
	public String param4;

	public ItemLogEntry(final RPObject item, final RPEntity player, final String event,
			final String param1, final String param2, final String param3, final String param4) {
		this.item = item;
		this.player = player;
		this.event = event;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
	}
}
