package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Checks if a player has killed (not only solo kills) an amount of one or more special creatures
 *  
 * @author madmetzger
 */
public class PlayerHasKilledNumberOfCreaturesCondition implements ChatCondition {
	
	private final Map<String, Integer> creatures;
	
	/**
	 * Constructor to use condition with only one creature
	 * 
	 * @param creature
	 * @param numberOfKills
	 */
	public PlayerHasKilledNumberOfCreaturesCondition (String creature, Integer numberOfKills) {
		creatures = new HashMap<String, Integer>();
		creatures.put(creature, numberOfKills);
	}

	/**
	 * creates a condition to kill each creature with the name specified in the map and the number as value
	 * 
	 * @param kills map of creature name to kill and number of that creature to kill
	 */
	public PlayerHasKilledNumberOfCreaturesCondition (Map<String, Integer> kills) {
		creatures = new HashMap<String, Integer>();
		creatures.putAll(kills);
	}
	
	/**
	 * Constructor to use when you want to let kill the same number of each specified creature
	 * 
	 * @param number the desired number
	 * @param creatureNames the names of the creatures to kill
	 */
	public PlayerHasKilledNumberOfCreaturesCondition (Integer number, String... creatureNames) {
		creatures = new HashMap<String, Integer>();
		List<String> names = Arrays.asList(creatureNames);
		for (String name : names) {
			creatures.put(name, number);
		}
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		for (Entry<String, Integer> entry : creatures.entrySet()) {
			int actualSharedKills = player.getSharedKill(entry.getKey());
			int actualSoloKills = player.getSoloKill(entry.getKey());
			int actualKills = actualSharedKills + actualSoloKills;
			if (entry.getValue().intValue() > actualKills) {
				return false;
			}
		}
		return true;
	}

}
