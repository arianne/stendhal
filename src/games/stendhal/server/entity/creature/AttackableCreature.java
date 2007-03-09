package games.stendhal.server.entity.creature;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * An creature that will be attacked by normal Creatures
 *
 * @author hendrik
 */
public class AttackableCreature extends Creature {
	private RPEntity master = null;

	/**
	 * AttackableCreature
	 *
	 * @param copy
	 */
	public AttackableCreature(Creature copy) {
		super(copy);
	}

	@Override
	public void init() {
		super.init();
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(this.getID());
		zone.addPlayerAndFriends(this);
	}

	@Override
	public void onDead(Entity killer) {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(this.getID());
		zone.removePlayerAndFriends(this);
		super.onDead(killer);
	}

	@Override
	protected List<RPEntity> getEnemyList() {
		List<RPEntity> res = this.getAttackingRPEntities();

		if (master != null) {
			if (res.isEmpty()) {
				res = master.getAttackingRPEntities();
			} else {
				res = new ArrayList<RPEntity>();
				res.addAll(this.getAttackingRPEntities());
				res.addAll(master.getAttackingRPEntities());
			}
		}
		return res;
	}

	/**
	 * sets the master of this creature
	 *
	 * @param master master
	 */
	public void setMaster(RPEntity master) {
		this.master = master;
	}

	@Override
	public Creature getInstance() {
		return new AttackableCreature(this);
	}

}
