package games.stendhal.server.events;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * An online event.
 *
 * @author hendrik
 */
public class BuddyLoginEvent extends RPEvent {
	private static final String RPCLASS_NAME = "buddy_login";
	private static final String NAME = "name";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.add(DefinitionClass.ATTRIBUTE, NAME, Type.STRING);
	}

	/**
	 * Creates a new online event.
	 *
	 * @param player Player who just logged in
	 */
	public BuddyLoginEvent(final Player player) {
		super(RPCLASS_NAME);		
		put(NAME, player.getName());
	}
}
