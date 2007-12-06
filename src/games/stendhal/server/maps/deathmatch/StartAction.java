package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Action to start a new deathmatch session for the player
 *
 * @author hendrik
 */
public class StartAction extends SpeakerNPC.ChatAction {

	private DeathmatchInfo deathmatchInfo;

	/**
	 * creates a new StartAction for the specified deathmatch
	 
	 * @param deathmatchInfo deathmatch to start
	 */
	public StartAction(DeathmatchInfo deathmatchInfo) {
		this.deathmatchInfo = deathmatchInfo;
	}

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
		engine.say("Have fun!");
		deathmatchInfo.startSession(player);
	}
}
