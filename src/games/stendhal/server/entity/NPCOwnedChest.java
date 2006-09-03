package games.stendhal.server.entity;

import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A chest that is for decoration purpose only. The player cannot open
 * it. If he tries, the nearby NPC will tell him to get away.
 *
 * @author hendrik
 */
public class NPCOwnedChest extends Chest {
	private SpeakerNPC npc = null;

	/**
	 * Creates a new NPCOwnedChest
	 *
	 * @param npc SpeakerNPC
	 */
	public NPCOwnedChest(SpeakerNPC npc) {
		this.npc = npc;
	}

	@Override
	public void onUsed(RPEntity user) {
		Player player = (Player) user;

		if (player.nextTo(this, 0.25)) {
			npc.say("Hey " + user.getName() + ", that is my chest.");
		}
	}
}
