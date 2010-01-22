package games.stendhal.server.events;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * An event for stopping attack.
 *
 */
public class StopAttackEvent extends RPEvent {
	private static final String RPCLASS_NAME = "stop_attack";
	private static final String TARGET = "target";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.add(DefinitionClass.ATTRIBUTE, TARGET, Type.INT);
	}

	/**
	 * Creates a new stop attack event.
	 *
	 * @param target - target for attack
	 */
	public StopAttackEvent(final int target) {
		super(RPCLASS_NAME);		
		put(TARGET, target);
	}
}
