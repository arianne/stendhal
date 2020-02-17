/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasPetOrSheepCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Mrs. Yeti Needs Help
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Mrs. Yeti, who lives in a snowy dungeon</li>
 * <li>Salva Mattori, Healer at magic city</li>
 * <li>Hackim Easso, Blacksmith assistant semos</li>
 * </ul>
 *
 * STEPS:
 * Mrs. Yeti lifes in a cave somewhere in semos mountain. She is mournful, because Mr. Yeti turn away from her. Thats why she ask the player for help. She like to have a special potion and some other stuff as a present for her husband.
 *
 * There is only one witch who, who can make the special potion. Mrs. Yeti tell the player where she lives. The player go for the witch. Once he found her, she tell the player, that she will help, but need some ingriedents.
 *
 * When the player is bringing in the collected stuff, she has to tell him, that her magic knife is damaged and she need a new one and send the player to a blacksmith. He has to craft a new magic knife for the witch.
 *
 * The blacksmith is willing to help. But need some stuff too, to craft the magic knife. He sends the player to collect it. The player brings in the needed items and the blacksmith could start make the knife, but he is too hungry to start it right now. Player has to bring him some food and he starts crafting the knife. But the player has to wait a bit until he is ready with it.
 *
 * After bring the knife to the witch, he tell the player that she forgot an important item. The player has to get it and bring it to here. After a while the special potion is ready. And the player can bring it to Mrs. Yeti.
 *
 * Mrs. Yeti is very happy about the special potion. But she needs some other things to make her husband happy. The player has to collect a baby dragon for her. After player bring the baby dragon to her, she is happy as never befor.
 *
 * REWARD:
 * <ul>
 * <li> 1,000 XP </li>
 * <li> some karma (10 + (10 | -10)) </li>
 * <li> Can buy <item>roach</item> from Mrs. Yeti </li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Not repeatable.</li>
 * </ul>
 */

 public class HelpMrsYeti extends AbstractQuest {

 	private static final String QUEST_SLOT = "mrsyeti";
	private static final int DELAY_IN_MINUTES = 60*24;

	private static Logger logger = Logger.getLogger(HelpMrsYeti.class);

 	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void startQuest() {
		final SpeakerNPC npc = npcs.get("Mrs. Yeti");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"I am mournful, because Mr. Yeti turns away from me. I need a special potion to make him happy and some present to please him. Will you help?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thank you for your help! Now Mr. Yeti and I are very happy again.",
				null);


		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Thank you for your help! You need to go to Salva Mattori in the magic city for the #potion.",
				new SetQuestAction(QUEST_SLOT, "start"));

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
			    ConversationStates.ATTENDING, "I will help you make this potion, Mrs. Yeti is an old friend of mine. But the blade on "
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
				ConversationStates.ATTENDING, "Very good! Now I need the items to make the love #potion. I need 3 lilia flowers, 1 sprig of kokuda, 1 glass of wine and 1 black pearl. Please bring them all together at once and then ask me to make the #potion.",
				new MultipleActions(new SetQuestAction(QUEST_SLOT, "potion"), new DropItemAction("knife")));

	    npc.add(ConversationStates.ATTENDING,  Arrays.asList("salva","knife","potion"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "knife"),
				new NotCondition(new PlayerHasItemWithHimCondition("knife"))),
				ConversationStates.ATTENDING, "I see you have been to Hackim, but where is the magic knife?",
				null);

		final List<ChatAction> potionactions = new LinkedList<ChatAction>();
		potionactions.add(new DropItemAction("lilia",3));
		potionactions.add(new DropItemAction("kokuda"));
		potionactions.add(new DropItemAction("wine"));
		potionactions.add(new DropItemAction("black pearl"));
		potionactions.add(new EquipItemAction("love potion"));
		potionactions.add(new IncreaseXPAction(100));
		potionactions.add(new SetQuestAction(QUEST_SLOT, "gotpotion"));

		// don't make player wait for potion - could add this in later if wanted
		npc.add(ConversationStates.ATTENDING,  Arrays.asList("salva","potion"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "potion"),
								 new PlayerHasItemWithHimCondition("lilia",3),
								 new PlayerHasItemWithHimCondition("kokuda"),
								 new PlayerHasItemWithHimCondition("wine"),
								 new PlayerHasItemWithHimCondition("black pearl")),
				ConversationStates.ATTENDING, "I see you have all the items for the potion. *mutters magic words* And now, ta da! You have the love potion. Wish Mrs. Yeti good luck from me!",
				new MultipleActions(potionactions));

		npc.add(ConversationStates.ATTENDING,  Arrays.asList("salva","potion"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "potion"),
								 new NotCondition(
												  new AndCondition(new PlayerHasItemWithHimCondition("lilia",3),
																   new PlayerHasItemWithHimCondition("kokuda"),
																   new PlayerHasItemWithHimCondition("wine"),
																   new PlayerHasItemWithHimCondition("black pearl")))),
				ConversationStates.ATTENDING, "I need 3 lilia flowers, 1 sprig of kokuda, 1 glass of wine and 1 black pearl to make the love potion. Please bring them all together at once. Thanks!", null);


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

	    npc.add(ConversationStates.ATTENDING, Arrays.asList("salva", "pies"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "pies"),
				new PlayerHasItemWithHimCondition("pie",5)),
				ConversationStates.ATTENDING, "Ah, thank you very much! Now I will tell you a little secret of mine. I am not a blacksmith, "
				+ "only an assistant. I can't make knives at all! But I sell Salva a normal knife and is happy enough with that! So just take her "
				+ "a plain knife like you could buy from Xin Blanca in Semos Tavern. I'll tell her I made it! Oh and thanks for the pies!!!",
				new MultipleActions(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "knife", 1.0), new DropItemAction("pie",5)));

	    npc.add(ConversationStates.ATTENDING, Arrays.asList("salva", "pies"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "pies"),
				new NotCondition(new PlayerHasItemWithHimCondition("pie",5))),
				ConversationStates.ATTENDING, "Arlindo from Ados makes the best meat and vegetable pies. Please remember to bring me 5, I am hungry!",
				null);

	}

	private void bringPotion() {
	final SpeakerNPC npc = npcs.get("Mrs. Yeti");
		final String extraTrigger = "potion";
	    List<String> questTrigger;
	    questTrigger = new LinkedList<String>(ConversationPhrases.QUEST_MESSAGES);
		questTrigger.add(extraTrigger);

	    final List<ChatAction> tookpotionactions = new LinkedList<ChatAction>();
		tookpotionactions.add(new DropItemAction("love potion"));
		tookpotionactions.add(new IncreaseKarmaAction(10.0));
		tookpotionactions.add(new IncreaseXPAction(1000));
		tookpotionactions.add(new SetQuestAction(QUEST_SLOT, "dragon"));

		npc.add(ConversationStates.ATTENDING, questTrigger,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "gotpotion"),
				new PlayerHasItemWithHimCondition("love potion")),
				ConversationStates.ATTENDING, "Thank you! That looks so powerful I almost love you from smelling it! But don't worry I will save it for my husband. But he won't take it without some other temptation. I think he'd like a baby #dragon, if you'd be so kind as to bring one.",
				new MultipleActions(tookpotionactions));

		npc.add(
			ConversationStates.ATTENDING, questTrigger,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "gotpotion"), new NotCondition(new PlayerHasItemWithHimCondition("love potion"))),
			ConversationStates.ATTENDING,
			"What did you do with the love potion?",
			null);

		npc.add(ConversationStates.ATTENDING,
				questTrigger,
				new OrCondition(new QuestInStateCondition(QUEST_SLOT, "start"),
								new QuestInStateCondition(QUEST_SLOT, "pies"),
								new QuestInStateCondition(QUEST_SLOT, "knife")),
				ConversationStates.ATTENDING,
				"I am waiting for you to return with a love potion. Please ask Salva Mattori in the magic city about: #potion.",
				null);
	}

	private void bringDragon() {
	final SpeakerNPC npc = npcs.get("Mrs. Yeti");

	    final String extraTrigger = "dragon";
	    List<String> questTrigger;
	    questTrigger = new LinkedList<String>(ConversationPhrases.QUEST_MESSAGES);
		questTrigger.add(extraTrigger);

		// easy to check if they have a pet or sheep at all
	    npc.add(
			ConversationStates.ATTENDING, questTrigger,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "dragon"),
							 new NotCondition(new PlayerHasPetOrSheepCondition())),
			ConversationStates.ATTENDING,
			"You can get a baby dragon only if you have a mythical egg. Those, you must get from Morgrin at the wizard school. "
			+ "Then Terry in Semos caves will hatch it.",
			null);

		// if they have any pet or sheep, then check if it's a baby dragon
		npc.add(
			ConversationStates.ATTENDING, questTrigger,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "dragon"),
							 new PlayerHasPetOrSheepCondition()),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence,
								 final EventRaiser npc) {
					if(!player.hasPet()){
						npc.say("That's a cute sheep you have there, but I need a baby dragon for Mr. Yeti. Try Morgrin at the magic school.");
						return;
					}
					Pet pet = player.getPet();
					String petType = pet.get("type");
					if("baby_dragon".equals(petType)) {
						player.removePet(pet);
						npc.say("Ah you brought the baby dragon! It will make such a wonderful stew. Baby dragon stew is my speciality and Mr. Yeti loves it! You've made us both very happy! Come back in a day to see me for a #reward.");
						player.addKarma(5.0);
						player.addXP(500);
						pet.delayedDamage(pet.getHP(), "Mrs. Yeti");
						player.setQuest(QUEST_SLOT,"reward;"+System.currentTimeMillis());
						player.notifyWorldAboutChanges();
					} else {
						npc.say("That's a cute pet you have there, but I need a baby dragon for Mr. Yeti. Try Morgrin at the magic school.");
					}
				}
			});

	}

	private void getReward() {

	final SpeakerNPC npc = npcs.get("Mrs. Yeti");

	    final String extraTrigger = "reward";
	    List<String> questTrigger;
	    questTrigger = new LinkedList<String>(ConversationPhrases.QUEST_MESSAGES);
		questTrigger.add(extraTrigger);

	    npc.add(
			ConversationStates.ATTENDING, questTrigger,
			new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "reward"),
							 // delay is in minutes, last parameter is argument of timestamp
							 new NotCondition(new TimePassedCondition(QUEST_SLOT,1,DELAY_IN_MINUTES))),
			ConversationStates.ATTENDING,
			null,
			new SayTimeRemainingAction(QUEST_SLOT,1,DELAY_IN_MINUTES,"Hello I am still busy with that baby dragon stew for Mr. Yeti. You can get your reward in"));


		npc.add(
			ConversationStates.ATTENDING, questTrigger,
			new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "reward"),
							 // delay is in minutes, last parameter is argument of timestamp
							 new TimePassedCondition(QUEST_SLOT,1,DELAY_IN_MINUTES)),
			ConversationStates.ATTENDING,
			"Thank you! To say thank you, I'd like to offer you the chance to always #buy #roach from me cheaply. I have so much of it and perhaps you have a use for it.",
			new MultipleActions(new SetQuestAction(QUEST_SLOT,"done"), new IncreaseXPAction(1000)));

	}


	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Help Mrs. Yeti",
				"Mrs. Yeti is really unhappy with her current love life because her husband turned away from her. Now the couple is in deep trouble. Just a special love potion can help Mrs. Yeti to get her husband back.",
				true);
		startQuest();
		makePotion();
		makeMagicKnife();
		bringPotion();
		bringDragon();
		getReward();
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			final String questState = player.getQuest(QUEST_SLOT);
			res.add("I met Mrs. Yeti in icy caves below Semos Mountain.");
			res.add("Mrs. Yeti asked me to go to Salva Mattori for a special love potion for her husband.");
			if ("rejected".equals(questState)) {
				res.add("I don't want to help with soppy love stories..");
				return res;
			}
			if ("start".equals(questState)) {
				return res;
			}
			res.add("Salva Mattori needs a magic knife from Hackim Easso to make her potion.");
			if ("hackim".equals(questState)) {
				return res;
			}
			res.add("Hackim is hungry and wants 5 meat pies before he helps me.");
			if ("pies".equals(questState)) {
				return res;
			}
			res.add("Hackim said I should go buy a standard knife like from Xin Blanca!! Apparently he tricked Salva all these years into believing they are magic, I better not let on...");
			if ("knife".equals(questState)) {
				return res;
			}
			res.add("The love potion requires 3 lilia flowers, 1 sprig of kokuda, 1 glass of wine and 1 black pearl.");
			if ("potion".equals(questState)) {
				return res;
			}
			res.add("I must take the love potion in its heart shaped bottle, to Mrs. Yeti.");
			if ("gotpotion".equals(questState)) {
				return res;
			}
			res.add("Mrs. Yeti needs something else to tempt her husband with and has asked me to bring a baby dragon.");
			if ("dragon".equals(questState)) {
				return res;
			}
			res.add("Oh my! She killed my dragon to make stew! That wasn't the kind of treat I thought she had in mind!");
			if (questState.startsWith("reward")) {
				if (new TimePassedCondition(QUEST_SLOT,1,DELAY_IN_MINUTES).fire(player, null, null)) {
					res.add("Mrs. Yeti told me to come back in a day to collect my reward and it's already been long enough.");
				} else {
					res.add("Mrs. Yeti told me to come back in a day to collect my reward so I need to wait.");
				}
				return res;
			}
			res.add("Mrs. Yeti is really pleased with the outcome of my help and now she'll sell me roach very cheaply.");
			if (isCompleted(player)) {
				return res;
			}

			// if things have gone wrong and the quest state didn't match any of the above, debug a bit:
			final List<String> debug = new ArrayList<String>();
			debug.add("Quest state is: " + questState);
			logger.error("History doesn't have a matching quest state for " + questState);
			return debug;
	}

	@Override
	public String getName() {
		return "HelpMrsYeti";
	}

	@Override
	public int getMinLevel() {
		return 60;
	}

	@Override
	public String getNPCName() {
		return "Mrs. Yeti";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_YETI_CAVE;
	}
}
