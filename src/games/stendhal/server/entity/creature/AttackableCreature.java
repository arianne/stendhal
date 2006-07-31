package games.stendhal.server.entity.creature;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;

/**
 * An creature that will be attacked by normal Creatures
 *
 * @author hendrik
 */
public class AttackableCreature extends Creature {

	/**
	 * @param copy
	 */
	public AttackableCreature(Creature copy) {
		super(copy);
	}

	@Override
	public void init() {
		super.init();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(this.getID());
		zone.addPlayerAndFriends(this);
	}

	@Override
	public void onDead(RPEntity who) {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(this.getID());
		zone.removePlayerAndFriends(this);
		super.onDead(who);
	}

	@Override
	protected RPEntity getNearestPlayer(double range) {
		// do not attack each other
		return null;
	}

	@Override
	public Creature getInstance() {
		return new AttackableCreature(this);
	}

}