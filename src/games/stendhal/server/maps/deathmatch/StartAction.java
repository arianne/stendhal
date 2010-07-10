package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * Action to start a new deathmatch session for the player.
 *
 * @author hendrik
 */
public class StartAction implements ChatAction {

	private final DeathmatchInfo deathmatchInfo;

	/**
	 * Creates a new StartAction for the specified deathmatch.
	 
	 * @param deathmatchInfo deathmatch to start
	 */
	public StartAction(final DeathmatchInfo deathmatchInfo) {
		this.deathmatchInfo = deathmatchInfo;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		raiser.say("Have fun!");
		deathmatchInfo.startSession(player);
	}
}
