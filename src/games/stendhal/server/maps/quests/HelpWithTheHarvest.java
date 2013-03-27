/**
 * 
 */
package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.block.Block;
import games.stendhal.server.entity.mapstuff.block.BlockTarget;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.OutputQuestSlotAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * In this quest the player can help Eheneumniranin by bringing
 * two carts with stray up to the barn near Karl.
 * 
 * (proof of concept for pushable blocks)
 * 
 * @author madmetzger
 */
public class HelpWithTheHarvest extends AbstractQuest {
	
	private static final String QUEST_SLOT = "helpwiththeharvest";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Help with the harvest";
	}

	@Override
	public void addToWorld() {
		placeCartsAndTargets();
		configureNPC();
	}

	private void configureNPC() {
		SpeakerNPC npc = npcs.get("Eheneumniranin");
		
		/*
		 * Add a reply on the trigger phrase "quest"
		 */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				"Are you here to help me a bit with my harvest?",
				null);
		
		/*
		 * Player is interested in helping, so explain the quest.
		 */
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"This is really nice. I was getting tired of bringing the two carts with stray to Karl.",
				new MultipleActions(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "carts;2", 2.0), new OutputQuestSlotAction(QUEST_SLOT)));

		/*
		 * Player refused to help - end the conversation.
		 */
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				"Oh, I was hoping to get a bit of help, but ok. Bye.",
				null);
		
		/*
		 * Player has put the carts to the right spots 
		 */
	}

	protected void placeCartsAndTargets() {
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_ados_forest_w2");
		
		ChatAction a = new MultipleActions(
				new IncrementQuestAction(QUEST_SLOT, 1, -1),
				new OutputQuestSlotAction(QUEST_SLOT));
		ChatCondition c = new AndCondition(
				new QuestStartedCondition(QUEST_SLOT), 
				new QuestInStateCondition(QUEST_SLOT, 0, "carts"), 
				new QuestStateGreaterThanCondition(QUEST_SLOT, 1, 0));
		
		Block cartOne = new Block(87, 100, true, "hay_cart");
		Block cartTwo = new Block(79, 106, true, "hay_cart");
		zone.add(cartOne);
		zone.add(cartTwo);
		zone.addMovementListener(cartOne);
		zone.addMovementListener(cartTwo);
		zone.addZoneEnterExitListener(cartOne);
		zone.addZoneEnterExitListener(cartTwo);
		
		BlockTarget targetOne = new BlockTarget(64, 75);
		targetOne.setCondition(c);
		targetOne.setAction(a);
		
		BlockTarget targetTwo = new BlockTarget(63, 75);
		targetTwo.setAction(a);
		targetTwo.setCondition(c);
		
		zone.add(targetOne);
		zone.add(targetTwo);
	}

}
