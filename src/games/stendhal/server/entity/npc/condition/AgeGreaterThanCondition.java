package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player's age greater than the specified age?
 */
public class AgeGreaterThanCondition implements ChatCondition {

	private final int age;

	/**
	 * Creates a new AgeGreaterThanCondition.
	 * 
	 * @param age
	 *            age 
	 */
	public AgeGreaterThanCondition(final int age) {
		this.age = age;
	}

	/**
	 * @return true if players age greater than age in condition
	 */
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (player.getAge() > age);
	}

	@Override
	public String toString() {
		return "age > " + age + " ";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				AgeGreaterThanCondition.class);
	}

}
