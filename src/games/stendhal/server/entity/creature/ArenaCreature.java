package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.RPEntity;

import java.awt.Shape;
import java.util.LinkedList;
import java.util.List;

/**
 * An creature that will only target enemies which are within a specified area.
 * 
 * @author hendrik
 */
public class ArenaCreature extends Creature {

	private Shape arena;

	/**
	 * ArenaCreature.
	 * 
	 * @param copy
	 *            creature to wrap
	 * @param arena
	 *            arena
	 */
	public ArenaCreature(Creature copy, Shape arena) {
		super(copy);
		this.arena = arena;
	}

	@Override
	protected List<RPEntity> getEnemyList() {
		// only return those enemies which are in the arena
		List<RPEntity> standardEnemyList = super.getEnemyList();
		List<RPEntity> resultList = new LinkedList<RPEntity>();

		for (RPEntity enemy : standardEnemyList) {
			if (arena.contains(enemy.getX(), enemy.getY())) {
				resultList.add(enemy);
			}
		}
		return resultList;
	}

	@Override
	public Creature getInstance() {
		return new ArenaCreature(this, arena);
	}

}
