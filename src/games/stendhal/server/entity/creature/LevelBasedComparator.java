package games.stendhal.server.entity.creature;

import java.util.Comparator;


public class LevelBasedComparator implements Comparator<Creature> {

	public int compare(Creature o1, Creature o2) {
		return o1.getLevel() - o2.getLevel();
	}

}
