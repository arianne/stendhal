package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.RPEntity;

import java.awt.Shape;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An creature that will only target enemies which are within a specified area. 
 *
 * @author hendrik
 */
public class ArenaCreature extends Creature {
	private Shape arena = null;

	/**
	 * ArenaCreature
	 *
	 * @param copy creature to wrap
	 * @param arena arena
	 */
	public ArenaCreature(Creature copy, Shape arena) {
		super(copy);
		this.arena = arena;
	}

	@Override
	protected List<RPEntity> getEnemyList() {
		// get the normal enemy list and clone it
		List<RPEntity> standardEnemyList = super.getEnemyList();
		List<RPEntity> res = new LinkedList<RPEntity>(standardEnemyList);

		// then remove all enemies which are outside the arena
		if (arena != null) {
			Iterator<RPEntity> itr = res.iterator();
			while (itr.hasNext()) {
				RPEntity possibleTarget = itr.next();
				if (!arena.contains(possibleTarget.getX(), possibleTarget.getY())) {
					itr.remove();
				}
			}
		}
		return res;
	}

	@Override
	public Creature getInstance() {
		return new ArenaCreature(this, arena);
	}

}