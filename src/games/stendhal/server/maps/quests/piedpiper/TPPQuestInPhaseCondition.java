package games.stendhal.server.maps.quests.piedpiper;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.ThePiedPiper;
import games.stendhal.server.maps.quests.piedpiper.ITPPQuestConstants.TPP_Phase;

public class TPPQuestInPhaseCondition implements ChatCondition {
	
	private TPP_Phase phase;
	
	public TPPQuestInPhaseCondition(TPP_Phase ph) {
		phase = ph;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		if(ThePiedPiper.getPhase().compareTo(phase)==0) {
			return true;
		}
		return false;
	}
}
