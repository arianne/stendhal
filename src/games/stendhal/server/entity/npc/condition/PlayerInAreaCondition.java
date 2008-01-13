package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player in the specified area?
 */
public class PlayerInAreaCondition extends SpeakerNPC.ChatCondition {

	private Area area;

	/**
	 * Creates a new PlayerInAreaCondition.
	 * 
	 * @param area
	 *            Area
	 */
	public PlayerInAreaCondition(Area area) {
		this.area = area;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
		return area.contains(player);
	}

	@Override
	public String toString() {
		return "player in <" + area + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestStartedCondition.class);
	}
}