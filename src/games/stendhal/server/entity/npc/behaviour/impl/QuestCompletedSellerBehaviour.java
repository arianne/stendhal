package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;

import java.util.HashMap;
import java.util.Map;

public class QuestCompletedSellerBehaviour extends SellerBehaviour {
	private final String questSlot;
	private final String message;

	public QuestCompletedSellerBehaviour(String questSlot, String message, final Map<String, Integer> priceList) {
		super(priceList);
		this.questSlot=questSlot;
		this.message=message;
	}

	@Override 
	public ChatCondition getTransactionCondition() {
		return new QuestCompletedCondition(questSlot);
	}
	
	@Override 
	public ChatAction getRejectedTransactionAction() {
		return new SayTextAction(message);
	}
}
