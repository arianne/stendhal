package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.RPEntity;

import java.awt.Shape;
import java.util.Iterator;
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
		List<RPEntity> res = super.getEnemyList();
		Iterator<RPEntity> itr = res.iterator();
		if (arena != null) {
			while (itr.hasNext()) {
				// TODO: Koordinate von this.getY() ueberpruefen
				if (!arena.contains(this.getX(), this.getY())) {
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