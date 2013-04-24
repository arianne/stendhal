package games.stendhal.server.core.rp.achievement.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Were this many quests completed?
 * 
 * @author kymara
 */
public class QuestCountCompletedCondition implements ChatCondition {

	private final int count;

	/**
	 * Creates a new QuestCountCompletedCondition.
	 * 
	 * @param count
	 *            number of quests to check
	 */
	public QuestCountCompletedCondition(final int count) {
		this.count = count;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		List<String> quests = SingletonRepository.getStendhalQuestSystem().getCompletedQuests(player);
		if (quests.size()>=count) {
			return true;
		}	
		return false;
	}

	@Override
	public String toString() {
		return "QuestCountCompletedCondition <" + count + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestCountCompletedCondition.class);
	}
}
