package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.item.Corpse;

public class QuestKanmararn {

	/**
	 * a non equipable corpse used in quests
	 */
	// TODO: fix equiping of normal corpse by implementing weight and degenrating in bag 
	public static class QuestCorpse extends Corpse {

		/**
		 * Creates a new non equipable corpse
		 *
		 * @param clazz Class
		 * @param x x-position
		 * @param y y-position
		 */
		public QuestCorpse(String clazz, int x, int y) {
			super(clazz, x, y);
		}

		@Override
		public boolean canBeEquippedIn(String slot) {
			return false;
		}
	}
}
