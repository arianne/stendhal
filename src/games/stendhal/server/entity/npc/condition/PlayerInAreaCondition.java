package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player in the specified area?
 */
public class PlayerInAreaCondition implements ChatCondition {

	private final Area area;

	/**
	 * Creates a new PlayerInAreaCondition.
	 * 
	 * @param area
	 *            Area
	 */
	public PlayerInAreaCondition(final Area area) {
		this.area = area;
	}

	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
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
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerInAreaCondition.class);
	}
}
