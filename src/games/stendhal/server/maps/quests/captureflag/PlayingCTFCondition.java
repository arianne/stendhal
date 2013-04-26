package games.stendhal.server.maps.quests.captureflag;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

public class PlayingCTFCondition implements ChatCondition {

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {

		// TODO: probably better to use an rpentity slot
		return player.hasUseListener();
	}

}
