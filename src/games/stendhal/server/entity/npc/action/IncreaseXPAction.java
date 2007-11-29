package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Increases the xp of the current player
 */
public class IncreaseXPAction extends SpeakerNPC.ChatAction {

	private int xpDiff;

	/**
	 * Creates a new IncreaseKarmaAction
	 *
	 * @param xpDiff amount of karma to add
	 */
	public IncreaseXPAction(int xpDiff) {
		this.xpDiff = xpDiff;
	}

	@Override
	public void fire(Player player, String text, SpeakerNPC engine) {
		player.setXP(player.getXP() + xpDiff);
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "IncreaseXP <" + xpDiff +">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + xpDiff;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		final IncreaseXPAction other = (IncreaseXPAction) obj;
		if (xpDiff != other.xpDiff) return false;
		return true;
	}

}