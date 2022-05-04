package games.stendhal.server.entity.npc.quest;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.action.DropFirstOwnedItemAction;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import marauroa.common.Pair;

public class BringItemTask extends QuestTaskBuilder {
	private Pair<String, Integer> requestItem;
	private List<Pair<String, Integer>> alternativeItems = new LinkedList<>();

	public BringItemTask requestItem(int count, String name) {
		requestItem = new Pair<String, Integer>(name, count);
		return this;
	}

	public BringItemTask alternativeItem(int count, String name) {
		alternativeItems.add(new Pair<String, Integer>(name, count));
		return this;
	}

	@Override
	void simulate(QuestSimulator simulator) {
		simulator.info("Player obtained " + requestItem.second() + " " + requestItem.first() + ".");
		simulator.info("");
	}

	@Override
	ChatAction buildStartQuestAction(String questSlot) {
		return null;
	}

	@Override
	public ChatCondition buildQuestCompletedCondition(String questSlot) {
		List<ChatCondition> conditions = new LinkedList<>();
		conditions.add(new PlayerHasItemWithHimCondition(requestItem.first(), requestItem.second()));
		for (Pair<String, Integer> item : alternativeItems) {
			conditions.add(new PlayerHasItemWithHimCondition(item.first(), item.second()));
		}
		return new OrCondition(conditions);
	}

	@Override
	public ChatAction buildQuestCompleteAction(String questSlot) {
		LinkedList<Pair<String, Integer>> temp = new LinkedList<>(alternativeItems);
		temp.add(0, requestItem);
		return new DropFirstOwnedItemAction(temp);
	}

}
