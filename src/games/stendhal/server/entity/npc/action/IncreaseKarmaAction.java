package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Increases the karma of the current player
 */
public class IncreaseKarmaAction extends SpeakerNPC.ChatAction {

	private double karmaDiff;

	/**
	 * Creates a new IncreaseKarmaAction
	 *
	 * @param karmaDiff amount of karma to add
	 */
	public IncreaseKarmaAction(double karmaDiff) {
		this.karmaDiff = karmaDiff;
	}

	@Override
	public void fire(Player player, String text, SpeakerNPC engine) {
		player.addKarma(karmaDiff);
	}
}