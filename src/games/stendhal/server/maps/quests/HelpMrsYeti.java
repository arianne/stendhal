package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Mrs Yeti Needs Help
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Mrs Yeti, who lives in a snowy dungeon</li>
 * <li>Salva Mattori, Healer at magic city</li>
 * <li>Hackim Easso, Blacksmith assistant semos</li>
 * </ul>
 *
 * STEPS:
Mrs. Yeti lifes in a cave somewhere in semos mountain. She is mournful, because Mr. Yeti turn away from her. Thats why she ask the player for help. She like to have a special potion and some other stuff as a present for her husband.

There is only one witch who, who can make the special potion. Mrs. Yeti tell the player where she lives. The player go for the witch. Once he found her, she tell the player, that she will help, but need some ingriedents.

When the player is bringing in the collected stuff, she has to tell him, that her magic knife is damaged and she need a new one and send the player to a blacksmith. He has to craft a new magic knife for the witch.

The blacksmith is willing to help. But need some stuff too, to craft the magic knife. He sends the player to collect it. The player brings in the needed items and the blacksmith could start make the knife, but he is too hungry to start it right now. Player has to bring him some food and he starts crafting the knife. But the player has to wait a bit until he is ready with it.

After bring the knife to the witch, he tell the player that she forgot an important item. The player has to get it and bring it to here. After a while the special potion is ready. And the player can bring it to Mrs. Yeti.

Mrs. Yeti is very happy about the special potion. But she needs some other things to make her husband happy. The player has to collect some items for her. After player bring the items to her, she is happy as never befor.

But there is a last thing to do, a monster has to be killed by player. After this favor, the player can go back to Mrs. Yeti and get the reward.

What is needed:
- special potion
- magic knife (not for attacking)
- an idea for reward

 *
 * REWARD:
 * <ul>

 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Not repeatable.</li>
 * </ul>
 */
 
 public class HelpMrsYeti extends AbstractQuest {
 
 	private static final String QUEST_SLOT = "mrsyeti";
 	
 
 	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void startQuest() {
		final SpeakerNPC npc = npcs.get("Mrs Yeti");	
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"I am mournful, because Mr Yeti turns away from me. I need a special potion to make him happy and some present to please him. Will you help?",
				null);
							
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thank you for your help! Now Mr Yeti and I are very happy again.",
				null);
		

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Thank you for your help! You need to go to Salva Mattori in the magic city for the #potion.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 10.0));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"Oh, you are so heartless.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
	}

	private void makePotion() {
	// player needs to bring some items to make the potion:
	// a 'magic' knife from a blacksmith
	// 3 lilia flowers
	// sclaria
	// wine
	// black pearl
	final SpeakerNPC npc = npcs.get("Salva Mattori");
		npc.add(ConversationStates.ATTENDING, "potion",
				new QuestInStateCondition(QUEST_SLOT, "start"),
			    ConversationStates.ATTENDING, "I will help you make this potion, Mrs Yeti is an old friend of mine. But the blade on "
				+ "my magic knife has snapped yet again. I need another. I get mine from Hackim Easso of Semos City, will you go to him and "
				+ "ask him to make another knife? Just say my name: #salva",
				new SetQuestAction(QUEST_SLOT, "hackim"));

		npc.add(
			ConversationStates.ATTENDING, Arrays.asList("salva","knife"),
			new NotCondition(new QuestInStateCondition(QUEST_SLOT, "knife")),
			ConversationStates.ATTENDING,
			"You need to go to Hackim Easso and ask him about a magic knife for #salva before I can help you.",
			null);

	    npc.add(ConversationStates.ATTENDING,  Arrays.asList("salva","knife","potion"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "knife"),
				new PlayerHasItemWithHimCondition("knife")),
				ConversationStates.ATTENDING, "Very good! Now I need the items to make the love #potion. I need 3 lilia flowers, 8 sprigs of sclaria, 1 glass of wine and 1 black pearl. Please bring them all together at once.",
				new MultipleActions(new SetQuestAction(QUEST_SLOT, "potion"), new DropItemAction("knife")));

	    npc.add(ConversationStates.ATTENDING,  Arrays.asList("salva","knife","potion"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "knife"),
				new NotCondition(new PlayerHasItemWithHimCondition("knife"))),
				ConversationStates.ATTENDING, "I see you have been to Hackim, but where is the magic knife?",
				null);

		final List<ChatAction> potionactions = new LinkedList<ChatAction>();
		potionactions.add(new DropItemAction("lilia",3));
		potionactions.add(new DropItemAction("sclaria",8));
		potionactions.add(new DropItemAction("wine"));
		potionactions.add(new DropItemAction("black pearl"));
		potionactions.add(new EquipItemAction("love potion"));
		potionactions.add(new IncreaseXPAction(100));
		potionactions.add(new SetQuestAction(QUEST_SLOT, "gotpotion"));

		// don't make player wait for potion - could add this in later if wanted
		npc.add(ConversationStates.ATTENDING,  Arrays.asList("salva","knife","potion"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "knife"),
								 new PlayerHasItemWithHimCondition("lilia",3),
								 new PlayerHasItemWithHimCondition("sclaria",8),
								 new PlayerHasItemWithHimCondition("wine"),
								 new PlayerHasItemWithHimCondition("black pearl")),
				ConversationStates.ATTENDING, "I see you have all the items for the potion. *mutters magic words* And now you have the love #potion. Wish Mrs Yeti good luck from me!",
				new MultipleActions(potionactions));

		npc.add(ConversationStates.ATTENDING,  Arrays.asList("salva","knife","potion"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "knife"),
								 new NotCondition(
												  new AndCondition(new PlayerHasItemWithHimCondition("lilia",3),
																   new PlayerHasItemWithHimCondition("sclaria",8),
																   new PlayerHasItemWithHimCondition("wine"),
																   new PlayerHasItemWithHimCondition("black pearl")))),
				ConversationStates.ATTENDING, "I need 3 lilia flowers, 8 sprigs of sclaria, 1 glass of wine and 1 black pearl to make the love potion. Please bring them all together at once. Thanks!", null);
				

	}

	private void makeMagicKnife() {
		// although the player does end up just taking an ordinary knife to salva, this step must be completed 
		// (must be in quest state 'knife' when they take the knife)
	final SpeakerNPC npc = npcs.get("Hackim Easso");
		npc.add(ConversationStates.ATTENDING, "salva",
				new QuestInStateCondition(QUEST_SLOT, "hackim"),
			    ConversationStates.ATTENDING, "Salva needs another magic knife does she? Ok, I can help you but not while I am so hungry. "
				+ "I need food! Bring me 5 #pies and I will help you!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "pies", 1.0));

	    npc.add(ConversationStates.ATTENDING, "pies",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "pies"),
				new PlayerHasItemWithHimCondition("pie",5)),
				ConversationStates.ATTENDING, "Ah, thank you very much! Now I will tell you a little secret of mine. I am not a blacksmith, "
				+ "only an assistant. I can't make knives at all! But I sell Salva a normal knife and is happy enough with that! So just take her "
				+ "a plain knife like you could buy from Xin Blanca in Semos Tavern. I'll tell her I made it! Oh and thanks for the pies!!!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "knife", 1.0));

	}

	private void bringPotion() {
	final SpeakerNPC npc = npcs.get("Mrs Yeti");	
	
	    final List<ChatAction> tookpotionactions = new LinkedList<ChatAction>();
		tookpotionactions.add(new DropItemAction("love potion"));
		tookpotionactions.add(new IncreaseKarmaAction(10.0));
		tookpotionactions.add(new IncreaseXPAction(1000));
		tookpotionactions.add(new SetQuestAction(QUEST_SLOT, "presents"));

		npc.add(ConversationStates.ATTENDING, "potion",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"),
				new PlayerHasItemWithHimCondition("love potion")),
				ConversationStates.ATTENDING, "Thank you! That looks so powerful I almost love you from smelling it! But don't worry I will save it for my husband. Need other stuff.", 
				new MultipleActions(tookpotionactions));

		npc.add(
			ConversationStates.ATTENDING, "potion",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("love potion"))),
			ConversationStates.ATTENDING,
			"Please go to Salva Mattori in the magic city and ask her to make a love potion for you. Just tell her: #potion and she will understand.",
			null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"I am waiting for you to return with a love #potion.",
				null);
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();

		startQuest();
		makePotion();
		makeMagicKnife();
		bringPotion();
	}

	@Override
	public String getName() {
		return "HelpMrsYeti";
	}

 
}
 
