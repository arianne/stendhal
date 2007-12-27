package games.stendhal.server.core.engine;

import games.stendhal.server.entity.player.Player;

public interface Task {
	void execute(Player player);

}
