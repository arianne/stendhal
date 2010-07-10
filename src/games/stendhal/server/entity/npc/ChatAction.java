package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.npc.fsm.PostTransitionAction;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

public interface ChatAction extends PostTransitionAction {

	void fire(final Player player, final Sentence sentence, final EventRaiser npc);

}
