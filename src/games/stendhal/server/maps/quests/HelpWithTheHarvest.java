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
import games.stendhal.server.entity.npc.action.EquipRandomAmountOfItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * In this quest the player can help Eheneumniranin by bringing
 * two carts with straw up to the barn near Karl.
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
		if(new QuestStartedCondition(QUEST_SLOT).fire(player, null, null) && !createFinishedCondition().fire(player, null, null)) {
			result.add("I want to help Eheneumniranin with his harvest.");
		}
		if(constructHayCartsNotYetCompletedCondition().fire(player, null, null)) {
			result.add("I need to bring two straw carts to the barn just north of Eheneumniranin.");
		}
		if(createTaskFinishedCondition().fire(player, null, null)) {
			result.add("I have brought enough straw carts to the barn. I can tell Eheneumniranin now that I am done.");
		}
		if(createFinishedCondition().fire(player, null, null)) {
			result.add("I have helped " + getNPCName() + " and got my reward.");
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
				"This is really nice. I was getting tired of bringing the two carts with straw to Karl.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start;2", 2.0));

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
				"You did not bring yet both straw carts next to the straw cart near the barn just north of here.",
				null);
		
		/*
		 * Player has put both carts at the right spots
		 */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("done"),
				createTaskFinishedCondition(),
				ConversationStates.ATTENDING,
				"Thank you for helping me with the harvest. Here is your reward. Maybe you can bring the grain to #Jenny who can mill #flour from the grain.",
				createReward());
		/*
		 * Player has finished the quest and can get additional information
		 */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("jenny"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"You can find #Jenny near Semos at the mill. She mills grain to #flour for you if you bring her a few sheaves grain.",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("flour"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"#Jenny will mill the grain I gave you as reward to flour which you maybe could use for #bread?",
				null);
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("bread"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"#Erna did never bake a bread for you yet? It is really worth it, because you can use it to let #Leander make #sandwiches for you.",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("sandwich"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"You did not try a #sandwich made by #Leander yet? They are so tasty.",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("leander"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"Leander runs the bakery in semos city and can make #sandwiches for you if you bring him the ingredients. Why don't you spend him a visit?",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("erna"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"Erna is the assistant of #Leander in the bakery. If you bring her #flour, she will bake #bread for you.",
				null);
	}


	/**
	 * Place the carts and targets into the zone
	 */
	private void placeCartsAndTargets() {
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_ados_forest_w2");
		
		ChatAction a = new IncrementQuestAction(QUEST_SLOT, 1, -1);
		ChatCondition c = constructHayCartsNotYetCompletedCondition();
		
		String cartDescription = "You see a straw cart. Can you manage to push it to Karl's barn?";
		
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
	 * Create condition determining if straw carts have not been moved completely to the barn
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
	 * Create condition determining when straw carts were move to the barn
	 * 
	 * @return the condition
	 */
	private ChatCondition createTaskFinishedCondition() {
		ChatCondition c = new AndCondition(
				new QuestStartedCondition(QUEST_SLOT), 
				new QuestInStateCondition(QUEST_SLOT, 0, "start"), 
				new QuestInStateCondition(QUEST_SLOT, 1, "0"));
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
					new EquipRandomAmountOfItemAction("grain", 10, 20),
					new SetQuestAction(QUEST_SLOT, "done"));	
	}
	
	/**
	 * Create the condition determining if quest is finished
	 * 
	 * @return the condition
	 */
	private ChatCondition createFinishedCondition() {
		return new QuestCompletedCondition(QUEST_SLOT);
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
