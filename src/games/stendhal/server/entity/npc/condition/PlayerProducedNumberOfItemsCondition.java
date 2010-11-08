/**
 * 
 */
package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Checks if a player has produced a given number of items
 * 
 * @author madmetzger
 */
public class PlayerProducedNumberOfItemsCondition implements ChatCondition {
	
	private final PlayerLootedNumberOfItemsCondition condition;
	
	public PlayerProducedNumberOfItemsCondition(int number, String... items) {
		List<String> itemProducedList = new ArrayList<String>();
		if(items != null) {
			for (String string : items) {
				itemProducedList.add("produced."+string);
			}
		}
		condition = new PlayerLootedNumberOfItemsCondition(number, itemProducedList.toArray(new String[0]));
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		return condition.fire(player, sentence, npc);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerProducedNumberOfItemsCondition.class);
	}

	@Override
	public String toString() {
		return condition.toString().replace("produced.","").replace("looted", "produced");
	}
	
}
