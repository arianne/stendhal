package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * decreases the karma of the current player
 */
public class DecreaseKarmaAction extends SpeakerNPC.ChatAction {

	private double karmaDiff;

	/**
	 * Creates a new DecreaseKarmaAction
	 *
	 * @param karmaDiff amount of karma to subtract
	 */
	public DecreaseKarmaAction(double karmaDiff) {
		this.karmaDiff = karmaDiff;
	}

	@Override
	public void fire(Player player, String text, SpeakerNPC engine) {
		player.addKarma(-1 * karmaDiff);
	}

	@Override
	public String toString() {
		return "DecreaseKarma<" + karmaDiff +">";
	}
}