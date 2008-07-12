package games.stendhal.server.entity.creature;

import games.stendhal.server.entity.RPEntity;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares to RPEntities based on their level.
 * 
 * @author hendrik
 */
public class LevelBasedComparator implements Comparator<RPEntity>, Serializable {
	private static final long serialVersionUID = -8923421203006949207L;

	public int compare(final RPEntity o1, final RPEntity o2) {
		return o1.getLevel() - o2.getLevel();
	}

}
