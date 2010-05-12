package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player's age less than the specified age?
 */
public class AgeLessThanCondition implements ChatCondition {

	private final int age;

	/**
	 * Creates a new AgeLessThanCondition.
	 * 
	 * @param age
	 *            age 
	 */
	public AgeLessThanCondition(final int age) {
		this.age = age;
	}

	/**
	 * @return true if players age less than age in condition
	 */
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (player.getAge() < age);
	}

	@Override
	public String toString() {
		return "age < " + age + " ";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				AgeLessThanCondition.class);
	}

}
