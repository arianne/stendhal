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
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.OutputQuestSlotAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestSmallerThanCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

import java.util.ArrayList;
import java.util.Arrays;
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
		List<String> result = new ArrayList<String>();
		if(new QuestStartedCondition(QUEST_SLOT).fire(player, null, null)) {
			result.add("I want to help Eheneumniranin with his harvest.");
		}
		if(constructHayCartsNotYetCompletedCondition().fire(player, null, null)) {
			result.add("I need to bring two hay carts to the barn just north of Eheneumniranin.");
		}
		if(createFinishedCondition().fire(player, null, null)) {
			result.add("I have brought enough hay carts to the barn. I can tell Eheneumniranin now that I am done.");
		}
		return result;
	}

	@Override
	public String getName() {
		return "Help with the harvest";
	}
	
	@Override
	public int getMinLevel() {
		return 5;
	}

	@Override
	public void addToWorld() {
		placeCartsAndTargets();
		configureNPC();
		fillQuestInfo(getName(), "Eheneumniranin needs help with the harvest.", false);
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
				new MultipleActions(
						new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start;2", 2.0), 
						new OutputQuestSlotAction(QUEST_SLOT)));

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
		 * Player has not yet put the carts to the right spots
		 */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				constructHayCartsNotYetCompletedCondition(),
				ConversationStates.ATTENDING,
				"You did not bring yet both hay carts next to the hay cart near the barn just north of here.",
				null);
		
		/*
		 * Player has put both carts at the right spots
		 */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("done"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"Thank you for helping me with the harvest. Here is your reward.",
				createReward());
	}

	/**
	 * Place the carts and targets into the zone
	 */
	private void placeCartsAndTargets() {
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_ados_forest_w2");
		
		ChatAction a = new MultipleActions(
				new IncrementQuestAction(QUEST_SLOT, 1, -1),
				new OutputQuestSlotAction(QUEST_SLOT));
		ChatCondition c = constructHayCartsNotYetCompletedCondition();
		
		String cartDescription = "You see a hay cart. Can you manage to push it to Karl's barn?";
		
		Block cartOne = new Block(87, 100, true, "hay_cart");
		cartOne.setDescription(cartDescription);
		Block cartTwo = new Block(79, 106, true, "hay_cart");
		cartTwo.setDescription(cartDescription);
		
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

	/**
	 * Create condition determining if hay carts have been moved completely to the barn
	 * 
	 * @return the condition
	 */
	private ChatCondition constructHayCartsNotYetCompletedCondition() {
		ChatCondition c = new AndCondition(
								new QuestStartedCondition(QUEST_SLOT), 
								new QuestInStateCondition(QUEST_SLOT, 0, "start"), 
								new QuestStateGreaterThanCondition(QUEST_SLOT, 1, 0));
		return c;
	}
	
	/**
	 * Create the reward action
	 * 
	 * @return the action for rewarding finished quest
	 */
	private ChatAction createReward() {
		return new MultipleActions(
					new IncreaseKarmaAction(5),
					new IncreaseXPAction(50),
					new SetQuestAction(QUEST_SLOT, "done"));	
	}
	
	/**
	 * Create the condition determining if quest is finished
	 * 
	 * @return the condition
	 */
	private ChatCondition createFinishedCondition() {
		return new AndCondition(new QuestStartedCondition(QUEST_SLOT), 
					new QuestInStateCondition(QUEST_SLOT, 0, "start"),
					new QuestSmallerThanCondition(QUEST_SLOT, 1, 1));
	}

	@Override
	public String getRegion() {
		return Region.ADOS_SURROUNDS;
	}

	@Override
	public String getNPCName() {
		return "Eheneumniranin";
	}

}
