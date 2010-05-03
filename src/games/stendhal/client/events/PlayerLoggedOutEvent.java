package games.stendhal.client.events;

import org.apache.log4j.Logger;

import games.stendhal.client.World;
import games.stendhal.client.entity.Entity;

public class PlayerLoggedOutEvent extends Event<Entity> {
	
	private static final Logger logger = Logger.getLogger(PlayerLoggedOutEvent.class);

	@Override
	public void execute() {
		String playerName = event.get("name");
		logger.debug("Executing logout event for "+playerName);
		World.get().removePlayerLoggingOut(playerName);
	}

}
