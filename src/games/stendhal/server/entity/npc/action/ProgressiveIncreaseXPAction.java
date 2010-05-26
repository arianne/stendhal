package games.stendhal.server.entity.npc.action;

import games.stendhal.common.Level;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * Increases the xp of the current player.
 */
public class ProgressiveIncreaseXPAction implements ChatAction {
    // player will get 1/xpDiff part of his level's full xp amount. 
	private final double xpDiff;
	// player will get this bonus instead xp if have max level
	private final double karmabonus;

	/**
	 * Creates a new IncreaseKarmaAction.
	 * 
	 * @param xpDiff
	 *            amount of karma to add
	 */
	public ProgressiveIncreaseXPAction(final int xpDiff, final double karmabonus) {
		this.xpDiff = xpDiff;
		this.karmabonus = karmabonus;
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		final int start = Level.getXP(player.getLevel());
		final int next = Level.getXP(player.getLevel() + 1);
		int reward = (int) ((next - start) / xpDiff);
		if (player.getLevel() >= Level.maxLevel()) {
			reward = 0;
			// no reward so give a lot karma instead
			player.addKarma(karmabonus);
		}
		player.addXP(reward);
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "ProgressiveIncreaseXP <" + xpDiff + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int)(1/xpDiff);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ProgressiveIncreaseXPAction other = (ProgressiveIncreaseXPAction) obj;
		if (xpDiff != other.xpDiff) {
			return false;
		}
		return true;
	}

}
