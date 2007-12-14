package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Checks the records of kills
 * 
 * @author hendrik
 */
public class KilledCondition extends ChatCondition {
	private Set<String> toKill;

	/**
	 * creates a new KilledCondition
	 * 
	 * @param toKill
	 *            list of creatures which should be killed by the player
	 */
	public KilledCondition(List<String> toKill) {
		this.toKill = new TreeSet<String>(toKill);
	}

	/**
	 * creates a new KilledCondition
	 * 
	 * @param toKill
	 *            creatures which should be killed by the player
	 */
	public KilledCondition(String... toKill) {
		this.toKill = new TreeSet<String>(Arrays.asList(toKill));
	}

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
		for (String creature : toKill) {
			if (!player.hasKilled(creature)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "KilledCondition <" + toKill + ">";
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				KilledCondition.class);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
