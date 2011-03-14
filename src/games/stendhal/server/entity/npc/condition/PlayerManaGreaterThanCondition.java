package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * Condition to check if a player's mana is greater than a number
 * 
 * @author madmetzger
 *
 */
public class PlayerManaGreaterThanCondition implements ChatCondition {
	
	private int mana;

	public PlayerManaGreaterThanCondition(int mana) {
		this.mana = mana;
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return player.getMana() > this.mana;
	}

}
