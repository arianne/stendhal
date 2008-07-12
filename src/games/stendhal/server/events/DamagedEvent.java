package games.stendhal.server.events;

import games.stendhal.client.entity.Entity;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * A damaged event.
 *
 * @author hendrik
 */
public class DamagedEvent extends RPEvent {
	private static final String RPCLASS_NAME = "damaged";
	private static final String AMOUNT = "amount";
	private static final String BY = "by";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.add(DefinitionClass.ATTRIBUTE, AMOUNT, Type.INT);
		// TODO: check whether we should use an rplink instead
		rpclass.add(DefinitionClass.ATTRIBUTE, BY, Type.INT);
	}

	/**
	 * Creates a new damaged event.
	 *
	 * @param amount amount of hp healed
	 */
	public DamagedEvent(final int amount) {
		super(RPCLASS_NAME);		
		put(AMOUNT, amount);
	}

	/**
	 * Creates a new damaged event.
	 *
	 * @param amount amount of hp healed
	 * @param damager The entity which caused the damager
	 */
	public DamagedEvent(final int amount, final Entity damager) {
		super(RPCLASS_NAME);		
		put(AMOUNT, amount);
		put(BY, damager.getID().getObjectID());
	}
}
