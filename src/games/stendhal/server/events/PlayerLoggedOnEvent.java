package games.stendhal.server.events;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Events;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

public class PlayerLoggedOnEvent extends RPEvent {
	
	private static final String NAME_ATTRIBUTE = "name";
	private static final Logger logger = Logger.getLogger(PlayerLoggedOnEvent.class);
	
	public static void generateRPClass() {
		try {
			RPClass clazz = new RPClass(Events.PLAYER_LOGGED_ON);
			clazz.add(DefinitionClass.ATTRIBUTE, NAME_ATTRIBUTE, Type.STRING);
		} catch (Exception e) {
			logger.error("cannot generate RPClass", e);
		}
	}
	
	public PlayerLoggedOnEvent(String name) {
		super(Events.PLAYER_LOGGED_ON);
		put(NAME_ATTRIBUTE, name);
	}

}
