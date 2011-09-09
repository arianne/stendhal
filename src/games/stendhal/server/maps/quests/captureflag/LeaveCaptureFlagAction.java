package games.stendhal.server.maps.quests.captureflag;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * leave a game of CTF (remove the tag uselistener
 * @author hendrik, sjtsp 
 *
 */
public class LeaveCaptureFlagAction implements ChatAction {

	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		player.removeUseListener();
	}
}
