package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * Action to start a new deathmatch session for the player.
 *
 * @author hendrik
 */
public class StartAction extends SpeakerNPC.ChatAction {

	private final DeathmatchInfo deathmatchInfo;

	/**
	 * Creates a new StartAction for the specified deathmatch.
	 
	 * @param deathmatchInfo deathmatch to start
	 */
	public StartAction(final DeathmatchInfo deathmatchInfo) {
		this.deathmatchInfo = deathmatchInfo;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		engine.say("Have fun!");
		deathmatchInfo.startSession(player);
	}
}
