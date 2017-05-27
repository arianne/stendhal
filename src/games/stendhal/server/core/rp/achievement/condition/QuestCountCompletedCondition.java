package games.stendhal.server.core.rp.achievement.condition;

import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

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

		return (quests.size() >= count);
	}

	@Override
	public String toString() {
		return "QuestCountCompletedCondition <" + count + ">";
	}

	@Override
	public int hashCode() {
		return 47 * count;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof QuestCountCompletedCondition)) {
			return false;
		}
		return count == ((QuestCountCompletedCondition) obj).count;
	}

}
