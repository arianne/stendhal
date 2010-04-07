package games.stendhal.client.events;

import games.stendhal.client.World;
import games.stendhal.client.entity.Entity;

public class PlayerLoggedOutEvent extends Event<Entity> {

	@Override
	public void execute() {
		String playerName = event.get("name");
		World.get().removePlayerLoggingOut(playerName);
	}

}
