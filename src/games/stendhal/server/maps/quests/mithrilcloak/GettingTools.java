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
package games.stendhal.server.maps.quests.mithrilcloak;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.ExpressionType;
import games.stendhal.common.parser.JokerExprMatcher;
import games.stendhal.common.parser.Sentence;
import games.stendhal.common.parser.SimilarExprMatcher;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TextHasNumberCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

/**
 * @author kymara
*/

class GettingTools {

	private MithrilCloakQuestInfo mithrilcloak;

	private final NPCList npcs = SingletonRepository.getNPCList();

	public GettingTools(final MithrilCloakQuestInfo mithrilcloak) {
		this.mithrilcloak = mithrilcloak;
	}


	private static final int REQUIRED_MINUTES_SCISSORS = 10;

	private static final int REQUIRED_HOURS_SEWING = 24;

	private void getScissorsStep() {

		// Careful not to overlap with any states from VampireSword quest

		final SpeakerNPC npc = npcs.get("Hogart");

		// player asks about scissors. they will need a random number of eggshells plus the metal
		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("scissors", "magical", "magical scissors", "ida", "mithril", "cloak", "mithril cloak"),
			new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_scissors"),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final int neededEggshells = Rand.randUniform(2, 4);
					raiser.say("Ah yes, Ida sent me a message about some magical scissors. I need one each of an iron bar and a mithril bar, and also " + Integer.toString(neededEggshells) + " magical #eggshells. Ask me about #scissors again when you return with those items.");
					// store the number of needed eggshells in the quest slot so he remembers how many he asked for
					player.setQuest(mithrilcloak.getQuestSlot(), "need_eggshells;" + Integer.toString(neededEggshells));
				}
			});

		// player needs eggshells
		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("scissors", "magical", "magical scissors", "ida", "mithril", "cloak", "mithril cloak"),
			new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "need_eggshells"),
			ConversationStates.SERVICE_OFFERED,
			"So, did you bring the items I need for the magical scissors?", null);

		// player asks about eggshells, hint to find terry
		npc.add(
			ConversationStates.ATTENDING,
			"eggshells",
			null,
			ConversationStates.ATTENDING,
			"They must be from dragon eggs. I guess you better find someone who dares to hatch dragons!",
			null);

		// player says yes they brought the items needed
		// we can't use the nice ChatActions here because the needed number is stored in the quest slot i.e. we need a fire
		npc.add(
			ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "need_eggshells"),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String[] questslot = player.getQuest(mithrilcloak.getQuestSlot()).split(";");
					final int neededEggshells = Integer.valueOf(questslot[1]);
					if (player.isEquipped("iron")
						&& player.isEquipped("mithril bar")
						&& player.isEquipped("magical eggshells", neededEggshells)) {
							player.drop("iron");
							player.drop("mithril bar");
							player.drop("magical eggshells", neededEggshells);
							npc.say("Good. It will take me some time to make these, come back in "
									   + REQUIRED_MINUTES_SCISSORS + " minutes to get your scissors.");
							player.addXP(100);
							player.setQuest(mithrilcloak.getQuestSlot(), "makingscissors;" + System.currentTimeMillis());
							player.notifyWorldAboutChanges();
						} else {
							npc.say("Liar, you don't have everything I need. Ask me about #scissors again when you have an iron bar, a mithril bar, and "
									+ questslot[1] + " magical eggshells. And don't be wasting my time!");
						}
				}
			});

		// player says they didn't bring the stuff yet
		npc.add(
			ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"What are you still here for then? Go get them!",
			null);

		// player returns while hogart is making scissors or has made them
		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("scissors", "magical", "magical scissors", "ida", "mithril", "cloak", "mithril cloak"),
			new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "makingscissors;"),
			ConversationStates.ATTENDING, null, new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String[] tokens = player.getQuest(mithrilcloak.getQuestSlot()).split(";");
					// minutes -> milliseconds
					final long delay = REQUIRED_MINUTES_SCISSORS * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
					final long timeRemaining = (Long.parseLong(tokens[1]) + delay)
							- System.currentTimeMillis();
					if (timeRemaining > 0L) {
						npc.say("Pff you're impatient aren't you? I haven't finished making the scissors yet, come back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
						return;
					}
					npc.say("Ah, thanks for reminding me. Here, Ida's scissors are ready. You better take them to her next as I don't know what she wanted them for.");
					player.addXP(100);
					player.addKarma(15);
					final Item scissors = SingletonRepository.getEntityManager().getItem(
									"magical scissors");
					scissors.setBoundTo(player.getName());
					player.equipOrPutOnGround(scissors);
					player.setQuest(mithrilcloak.getQuestSlot(), "got_scissors");
					player.notifyWorldAboutChanges();
				}
			});

	}

	private void getEggshellsStep() {

		final int REQUIRED_POISONS = 6;

		final SpeakerNPC npc = npcs.get("Terry");

		// offer eggshells when prompted
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("eggshells", "magical", "magical eggshells", "scissors", "hogart", "ida", "cloak", "mithril cloak", "specials"),
				new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "need_eggshells"),
				ConversationStates.QUEST_ITEM_QUESTION,
				"Sure, I sell eggshells. They're not worth much to me. I'll swap you one eggshell for every " + Integer.toString(REQUIRED_POISONS) + " disease poisons you bring me. I need it to kill the rats you see. Anyway, how many eggshells was you wanting?",
				null);

		// respond to question of how many eggshells are desired. terry expects a number or some kind
		npc.addMatching(ConversationStates.QUEST_ITEM_QUESTION,
				// match for all numbers as trigger expression
				ExpressionType.NUMERAL, new JokerExprMatcher(),
				new TextHasNumberCondition(1, 5000),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {

                        final int required = (sentence.getNumeral().getAmount());
						if (player.drop("disease poison", required * REQUIRED_POISONS)) {
							npc.say("Ok, here's your " + Integer.toString(required) + " eggshells. Enjoy!");
							new EquipItemAction("magical eggshells", required, true).fire(player, sentence, npc);
						} else {
							npc.say("Ok, ask me again when you have " + Integer.toString(required * REQUIRED_POISONS) + " disease poisons with you.");
						}
					}
				});

		// didn't want eggshells yet
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				Arrays.asList("no", "none", "nothing"),
				null,
				ConversationStates.ATTENDING,
				"No problem. Anything else I can help with, just say.",
				null);

 	}

	private void giveScissorsStep() {

		final SpeakerNPC npc = npcs.get("Ida");

		// take scissors and ask for needle now
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("scissors", "magical", "magical scissors", "mithril", "cloak", "mithril cloak", "task", "quest"),
				new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_scissors"), new PlayerHasItemWithHimCondition("magical scissors")),
				ConversationStates.ATTENDING,
				"You brought those magical scissors! Excellent! Now that I can cut the fabric I need a magical needle. You can buy one from a trader in the abandoned keep of Ados mountains, #Ritati Dragon something or other. Just go to him and ask for his 'specials'.",
				new MultipleActions(
									 new DropItemAction("magical scissors"),
									 new SetQuestAndModifyKarmaAction(mithrilcloak.getQuestSlot(), "need_needle;", 10.0),
									 new IncreaseXPAction(100)
									 )
				);

		// remind about scissors
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("scissors", "magical", "magical scissors", "mithril", "cloak", "mithril cloak", "task", "quest"),
				new OrCondition(
								new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_scissors"),
								new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "need_eggshells;"),
								new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "makingscissors;"),
								new AndCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "got_scissors"),
												 new NotCondition(new PlayerHasItemWithHimCondition("magical scissors")))
								),
				ConversationStates.ATTENDING,
				"Ask #Hogart about #scissors, I'm sure he will remember the messages I've sent him!",
				null);

		npc.addReply("Ritati", "He's somewhere in the abandoned keep in the mountains north east from here.");
	}



	private void getNeedleStep() {

		final int NEEDLE_COST = 1500;

		final Map<Integer, String> jokes = new HashMap<Integer, String>();

			jokes.put(1, "If a man stands in the middle of the forest speaking and there is no woman around to hear him, is he still wrong?");
			jokes.put(2, "Everyone has a photographic memory, some just don't have film.");
			jokes.put(3, "Eagles may soar, free and proud, but weasels never get sucked into jet engines.");
			jokes.put(4, "There's no future in time travel.");
			jokes.put(5, "Artificial intelligence is no match for natural stupidity.");
			jokes.put(6, "Honk if you love peace and quiet.");
			jokes.put(7, "Always remember you're unique, just like everyone else.");
			jokes.put(8, "Half the people in the world are below average.");


		final SpeakerNPC npc = npcs.get("Ritati Dragontracker");

		// ask for joke when prompted for needle
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("needle", "magical", "magical needle", "ida", "cloak", "mithril cloak", "specials"),
				new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "need_needle"),
				ConversationStates.ATTENDING,
				"Ok, but I have a little rule: never do important business with someone unless"
				+ "they can make you laugh. So, come back to tell me a #joke and I will sell you a needle.",
				null);

		// ask for joke when player says joke
		npc.add(ConversationStates.ATTENDING,
				"joke",
				new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "need_needle"),
				ConversationStates.QUESTION_1,
				"Ok, lets hear your joke then. and I hope it's from the book in Nalwor Library, that's my favourite. What joke did you choose?",
				null);

		npc.add(ConversationStates.QUESTION_1, "", null,
				ConversationStates.QUEST_ITEM_QUESTION, null,
					new ChatAction() {
						@Override
						public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
							for (int i = 1; i < 9; i++) {
								String joke = jokes.get(i);

								final Sentence answer = sentence.parseAsMatchingSource();
								final Sentence expected = ConversationParser.parse(joke, new SimilarExprMatcher());

								if (answer.matchesFull(expected)) {
									final String[] questslot = player.getQuest(mithrilcloak.getQuestSlot()).split(";");
									if (questslot.length > 2) {
										// if the split worked, we had stored a needle number before and we need to store it again
										int needles = Integer.parseInt(questslot[1]);
										int saidjoke = Integer.parseInt(questslot[2]);
										if (i == saidjoke) {
											npc.say("You told me that joke last time, come back with a new one! Bye.");
											npc.setCurrentState(ConversationStates.IDLE);
//											// stop looking through the joke list
											return;
										} else {
											player.setQuest(mithrilcloak.getQuestSlot(), "told_joke;" + Integer.toString(needles) + ";" + Integer.toString(i));
										}
									} else {
										player.setQuest(mithrilcloak.getQuestSlot(), "told_joke;" + Integer.toString(i));
									}
									// this might have been his favourite joke, which is determined randomly
									if (Rand.randUniform(1, 8) == i) {
										npc.say("That's the funniest joke I ever heard! I think it's my favourite of the moment. Here, have your needle for free... and then get out of here, You've been here far too long already.");
										new EquipItemAction("magical needle", 1, true).fire(player, sentence, npc);
										npc.setCurrentState(ConversationStates.IDLE);
//										// stop looking through the joke list
										return;
									} else {
										npc.say("*guffaws* Alright, lets get on with business. A magical needle costs "
											+ Integer.toString(NEEDLE_COST) + " pieces of money. Do you want to buy one now?");
										// stop looking through the joke list
										npc.setCurrentState(ConversationStates.QUEST_ITEM_QUESTION);
										return;
									 }
								}
							}
							if (ConversationPhrases.GOODBYE_MESSAGES.contains(sentence.getTriggerExpression().getNormalized())) {
									npc.say("Ok, bye then.");
									npc.setCurrentState(ConversationStates.IDLE);
							} else if (sentence.getTriggerExpression().getNormalized().equals("none")) {
								npc.say("Ok, bye.");
								npc.setCurrentState(ConversationStates.IDLE);
							} else {
								npc.say("Sorry, that joke just isn't funny. go back to Nalwor library and get another.");
								npc.setCurrentState(ConversationStates.IDLE);
							}
			}
		});


		// offer needle when prompted if they already told a joke
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("needle", "magical", "magical needle", "ida", "cloak", "mithril cloak", "specials"),
				new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "told_joke"),
				ConversationStates.QUEST_ITEM_QUESTION,
				"I have some magical needles but they cost a pretty penny, "
				+ Integer.toString(NEEDLE_COST) + " pieces of money to be precise. Do you want to buy one?",
				null);

		// agrees to buy 1 needle
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES,
				new PlayerHasItemWithHimCondition("money", NEEDLE_COST),
				ConversationStates.IDLE,
				"Ok, here you are. Be careful with them, they break easy. "+
				"And if you break it, all other needles you have already bought from me, will lose their magic. "+
				"Now, get lost, you have hung around here far too long already.",
				new MultipleActions(
					new DropItemAction("money", NEEDLE_COST),
					new EquipItemAction("magical needle", 1, true)
					));

		// said he had money but he didn't
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition("money", NEEDLE_COST)),
				ConversationStates.ATTENDING,
				"What the ... you don't have enough money! Get outta here!",
				null);

		// doesn't want to buy needle
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Ok, no pressure, no pressure. Maybe you'll like some of my other #offers.",
				null);

		// specials response for if the queststate condition is not met
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("needle", "magical", "magical needle", "ida", "cloak", "mithril cloak", "specials"),
				null,
				ConversationStates.ATTENDING,
				"The time will come when you will need my specials, but that time is not now.",
				null);

	}

	private void giveNeedleStep() {

		final SpeakerNPC npc = npcs.get("Ida");

		// player brings needle for first or subsequent time
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("needle", "magical needle", "magical", "mithril", "cloak", "mithril cloak", "task", "quest"),
				new AndCondition(new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "told_joke"), new PlayerHasItemWithHimCondition("magical needle")),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
					new ChatAction() {
						@Override
						public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
							final String[] questslot = player.getQuest(mithrilcloak.getQuestSlot()).split(";");
							int needles = 1;
							int saidjoke = 1;
							if (questslot.length > 2) {
								// if the split works, we had stored a needle number before
								needles = Integer.parseInt(questslot[1]);
								saidjoke = Integer.parseInt(questslot[2]);
								npc.say("I'm really sorry about the previous needle breaking. I'll start work again on your cloak,"
										+ " please return in another " + REQUIRED_HOURS_SEWING + " hours.");
							 } else if (questslot.length > 1) {
								// it wasn't split with a needle number, only joke
								// so this is the first time we brought a needle
								saidjoke = Integer.parseInt(questslot[1]);
								npc.say("Looks like you found Ritatty then, good. I'll start on the cloak now!"
										+ " A seamstress needs to take her time, so return in " + REQUIRED_HOURS_SEWING + " hours.");
								// ida breaks needles - she will need 1 - 3
								needles = Rand.randUniform(1, 3);
							}
							player.setQuest(mithrilcloak.getQuestSlot(), "sewing;" + System.currentTimeMillis() + ";" + needles + ";" + saidjoke);
						}
					},
					new DropItemAction("magical needle")
					)
				);

		// remind about needle
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("needle", "magical needle", "magical", "mithril", "cloak", "mithril cloak", "task", "quest"),
				new OrCondition(new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "need_needle"),
								new AndCondition(
									new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "told_joke"),
									new NotCondition(new PlayerHasItemWithHimCondition("magical needle"))
								)
						),
				ConversationStates.ATTENDING,
				"Please ask Ritati thingummy for his 'specials', or just ask about a #needle.",
				null);
	}

	private void sewingStep() {

		final SpeakerNPC npc = npcs.get("Ida");

		// the quest slot that starts with sewing is the form "sewing;number;number" where the first number is the time she started sewing
		// the second number is the number of needles that she's still going to use - player doesn't know number

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("magical", "mithril", "cloak", "mithril cloak", "task", "quest"),
				new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "sewing;"),
				ConversationStates.ATTENDING, null, new ChatAction() {
						@Override
						public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
							final String[] tokens = player.getQuest(mithrilcloak.getQuestSlot()).split(";");
							// hours -> milliseconds
							final long delay = REQUIRED_HOURS_SEWING * MathHelper.MILLISECONDS_IN_ONE_HOUR;
							final long timeRemaining = (Long.parseLong(tokens[1]) + delay)
								- System.currentTimeMillis();
							if (timeRemaining > 0L) {
								npc.say("I'm still sewing your cloak, come back in "
										+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + " - and don't rush me, or I'm more likely to break the needle.");
								return;
							}
							// ida breaks needles, but if it is the last one,
							// she pricks her finger on that needle
							if (Integer.valueOf(tokens[2]) == 1) {
								npc.say("Ouch! I pricked my finger on that needle! I feel woozy ...");
								player.setQuest(mithrilcloak.getQuestSlot(), "twilight_zone");
							} else {
								npc.say("These magical needles are so fragile, I'm sorry but you're going to have to get me another, the last one broke. Hopefully Ritati still has plenty.");
								final int needles = Integer.parseInt(tokens[2]) - 1;
								int saidjoke = Integer.parseInt(tokens[3]);
								player.setQuest(mithrilcloak.getQuestSlot(), "need_needle;" + needles + ";" + saidjoke);
							}
				}
			});
	}

	public void addToWorld() {
		getScissorsStep();
		getEggshellsStep();
		giveScissorsStep();
		getNeedleStep();
		giveNeedleStep();
		sewingStep();
	}

}
