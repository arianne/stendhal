package games.stendhal.server.events;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * A healed event.
 *
 * @author hendrik
 */
public class HealedEvent extends RPEvent {
	private static final String RPCLASS_NAME = "healed";
	private static final String AMOUNT = "amount";

	// TODO: add additional paramer "healer" onces it is possible to heal other people

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.add(DefinitionClass.ATTRIBUTE, AMOUNT, Type.INT);
	}

	/**
	 * Creates a new healed event.
	 *
	 * @param amount amount of hp healed
	 */
	public HealedEvent(final int amount) {
		super(RPCLASS_NAME);		
		put(AMOUNT, amount);
	}
}
