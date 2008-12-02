package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.npc.fsm.PreTransitionCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

public interface ChatCondition extends PreTransitionCondition {

	public abstract boolean fire(Player player, Sentence sentence, SpeakerNPC npc);
}
