package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
/**
 * Check if a player is a good boy
 * 
 * @author madmetzger
 */
public class PlayerIsAGoodBoy implements ChatCondition {

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return !player.isBadBoy();
	}
	
}
