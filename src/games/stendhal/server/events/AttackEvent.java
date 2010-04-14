package games.stendhal.server.events;

import games.stendhal.common.constants.DamageType;
import games.stendhal.common.constants.Events;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.Type;

/**
 * An RPEntity attacks another
 */
public class AttackEvent extends RPEvent {
	private static final String HIT_ATTR = "hit";
	private static final String DAMAGE_ATTR = "damage";
	private static final String DAMAGE_TYPE_ATTR = "type";
	
	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.ATTACK);
		rpclass.addAttribute(HIT_ATTR, Type.FLAG);
		rpclass.addAttribute(DAMAGE_ATTR, Type.INT);
		rpclass.addAttribute(DAMAGE_TYPE_ATTR, Type.INT);
	}

	/**
	 * Construct a new <code>AttackEvent</code>
	 * 
	 * @param canHit <code>false</code> for missed hits, <code>true</code> for wounding or blocked hits
	 * @param damage damage done
	 * @param type damage type of the attack
	 */
	public AttackEvent(boolean canHit, int damage, DamageType type) {
		super(Events.ATTACK);
		if (canHit) {
			put(HIT_ATTR, "");
		}
		put(DAMAGE_ATTR, damage);
		put(DAMAGE_TYPE_ATTR, type.ordinal());
	}
}
