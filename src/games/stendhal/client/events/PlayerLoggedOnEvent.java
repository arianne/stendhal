package games.stendhal.client.events;

import org.apache.log4j.Logger;

import games.stendhal.client.World;
import games.stendhal.client.entity.Entity;

public class PlayerLoggedOnEvent extends Event<Entity> {
	
	private static final Logger logger = Logger.getLogger(PlayerLoggedOnEvent.class);

	@Override
	public void execute() {
		String playerName = event.get("name");
		logger.debug("Executing logon event for "+playerName);
		World.get().addPlayerLoggingOn(playerName);
	}

}
