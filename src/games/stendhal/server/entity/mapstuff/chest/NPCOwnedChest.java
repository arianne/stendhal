package games.stendhal.server.entity.mapstuff.chest;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * A chest that is for decoration purpose only. The player cannot open it. If he
 * tries, the nearby NPC will tell him to get away.
 * 
 * @author hendrik
 */
public class NPCOwnedChest extends Chest {

	private final SpeakerNPC npc;

	/**
	 * Creates a new NPCOwnedChest.
	 * 
	 * @param npc
	 *            SpeakerNPC
	 */
	public NPCOwnedChest(final SpeakerNPC npc) {
		this.npc = npc;
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		final Player player = (Player) user;

		if (player.nextTo(this)) {
			npc.say("Hey " + user.getTitle() + ", that is my chest.");
			return true;
		}
		return false;
	}
}
