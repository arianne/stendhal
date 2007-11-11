package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Is the player an admin?
 */
public class AdminCondition extends SpeakerNPC.ChatCondition {

	private int requiredAdminlevel;

	/**
	 * Creates a new AdminCondition for high level admins.
'	 */
	public AdminCondition() {
		requiredAdminlevel = 5000;
	}

	/**
	 * Creates a new AdminCondition
	 *
	 * @param requiredAdminlevel minimum admin level
'	 */
	public AdminCondition(int requiredAdminlevel) {
		this.requiredAdminlevel = requiredAdminlevel;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (player.getAdminLevel() >= requiredAdminlevel);
	}

	@Override
	public String toString() {
		return "admin<" + requiredAdminlevel + ">"; 
	}
}
