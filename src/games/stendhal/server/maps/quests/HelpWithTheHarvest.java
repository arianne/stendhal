/**
 *
 */
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import games.stendhal.server.entity.npc.action.ResetBlockChatAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * In this quest the player can help Eheneumniranin by bringing
 * two carts with straw up to the barn near Karl.
 *
 * (proof of concept for pushable blocks)
 *
 * @author madmetzger
 *
 *
 * QUEST: Help with the Harvest
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Eheneumniranin (the half-elf on Ados farm) </li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Eheneumniranin asks you to push some carts full of straw to Karl's barn </li>
 * <li> Push 2 carts to the designated spots in front of the barn </li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 50 XP </li>
 * <li> some karma (5 + (2 | -2)) </li>
 * <li> between 10 and 20 <item>grain</item> </li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
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
		if (player.isQuestInState(QUEST_SLOT, "rejected")) {
		    result.add("Farm work is too hard for me at the moment.");
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
		return "Help with the Harvest";
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
				ConversationStates.QUEST_OFFERED,
				"Are you here to help me a bit with my harvest?",
				null);

		/*
		 * Player is interested in helping, so explain the quest.
		 */
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
                        "That is really nice. I was getting tired of bringing carts to Karl. Please #push two straw carts to Karl's #barn and tell me that you are #done afterwards.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start;2", 2.0));

		npc.addReply("push", "You can easily move the carts by pushing them in front of the barn entrance. Take care to not get them stuck anywhere around or you won't be able to move them away.");

		npc.addReply("barn", "You can find Karl's barn north of here. It is marked by a huge sign with his name.");

		/*
		 * Player refused to help - end the conversation.
		 */
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Oh, I was hoping to get a bit of help, but ok...",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -2.0));


		/*
		 * Player has not yet put the carts to the right spots
		 */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("done"),
				constructHayCartsNotYetCompletedCondition(),
				ConversationStates.ATTENDING,
				"You did not yet bring both straw carts next to the cart near the barn just north of here.",
				null);

		/*
		 * Player asks for a quest although he has it already open
		 */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I already ask you to bring both straw carts next to Karls barn. Tell me if you are already #done with that.",
				null);

		/*
		 * Player has put both carts at the right spots
		 */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("done"),
				createTaskFinishedCondition(),
				ConversationStates.ATTENDING,
				"Thank you for helping me with the harvest. Here is your reward. Maybe you can bring the grain to #Jenny who can mill #flour from it.",
				createReward());
		/*
		 * Player has finished the quest and can get additional information
		 */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("jenny"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"You can find #Jenny near Semos at the mill. She mills grain into #flour for you if you bring her a few sheaves.",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("flour"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"#Jenny will mill the grain I gave you as reward to flour which you could maybe use for #bread?",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("bread"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"#Erna hasn't baked for you yet? It is really worth it, because #Leander can use it to make #sandwiches for you.",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("sandwich"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"You haven't tried a #sandwich made by #Leander yet? They are so tasty.",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("leander"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"Leander runs the bakery in Semos City and can make #sandwiches for you if you bring him the ingredients. Why don't you give him a visit?",
				null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("erna"),
				createFinishedCondition(),
				ConversationStates.ATTENDING,
				"Erna is the assistant to #Leander in the bakery. If you bring her #flour, she will bake #bread for you.",
				null);

        /*
         * Add a reply on the trigger phrase "quest" after it is finished
         */
        npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, createFinishedCondition(), ConversationStates.ATTENDING, "We already brought in the complete harvest, thanks again for your help.", null);
	}


	/**
	 * Place the carts and targets into the zone
	 */
	private void placeCartsAndTargets() {
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_ados_forest_w2");

		ChatCondition c = constructHayCartsNotYetCompletedCondition();

		String cartDescription = "You see a straw cart. Can you manage to push it to Karl's barn?";

		Block cartOne = new Block(true, "hay_cart");
		cartOne.setPosition(87, 100);
		cartOne.setDescription(cartDescription);
		Block cartTwo = new Block(true, "hay_cart");
		cartTwo.setPosition(79, 106);
		cartTwo.setDescription(cartDescription);

        ChatAction a = new MultipleActions(new IncrementQuestAction(QUEST_SLOT, 1, -1), new ResetBlockChatAction(cartOne), new ResetBlockChatAction(cartTwo));

		zone.add(cartOne);
		zone.add(cartTwo);

		BlockTarget targetOne = new BlockTarget();
		targetOne.setPosition(64, 75);
		targetOne.setDescription("You see a plain point on the ground. Something heavy stood here before.");
		targetOne.setCondition(c);
		targetOne.setAction(a);

		BlockTarget targetTwo = new BlockTarget();
		targetTwo.setPosition(65, 75);
		targetTwo.setDescription("You see a plain point on the ground. Something heavy stood here before.");
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
