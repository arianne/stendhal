package games.stendhal.server.entity.npc.fsm;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

public interface PostTransitionAction {
	
	public void fire(Player player, String text, SpeakerNPC engine);
}