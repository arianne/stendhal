package games.stendhal.client.events;

import games.stendhal.client.GameObjects;
import games.stendhal.client.entity.RPEntity;
import marauroa.common.game.RPObject;


/**
 * stops an attack
 *
 * @author yoriy
 */
public class StopAttackEvent extends Event<RPEntity> {

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		entity.onStopAttack(GameObjects.getInstance().get(new RPObject.ID(
				event.getInt("target"), entity.getID().getZoneID())));
	}

}
