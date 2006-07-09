/* $Id$
 * 
 */
package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.Player;

/**
 * This is a collection of standard actions and conditions.
 * Although most of them are very simply in normal Java-code,
 * they are annoying in Groovy because anon classes
 * are not supported.
 * 
 * @author hendrik
 */
public class StandardInteraction {

	/**
	 * Is the player an admin?
	 */
	public class AdminCondition extends SpeakerNPC.ChatCondition {
		int requiredAdminlevel;

		public AdminCondition() {
			requiredAdminlevel = 5000;
		}

		public AdminCondition(int requiredAdminlevel) {
			this.requiredAdminlevel = requiredAdminlevel;
		}

		public boolean fire(Player player, SpeakerNPC engine) {
			return (player.has("adminlevel") && (player.getInt("adminlevel") >= 5000));
		}
	}

}
