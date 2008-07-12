package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.entity.item.ConsumableItem;

public final class FeederFactory {
	private static Stuffer stuffer = new Stuffer();
	private static Immunizer imunizer = new Immunizer();
	private static Poisoner poisoner = new Poisoner();
	private static Eater eater = new Eater();

	public static Feeder get(final ConsumableItem item) {
		if (item.getName().contains("potion")) {
			return stuffer;
		}
		if (item.getRegen() == 0) {
			return imunizer;
		} else if (item.getRegen() < 0) {
			return poisoner;
		} else {
			return eater;
		}

	}

}
