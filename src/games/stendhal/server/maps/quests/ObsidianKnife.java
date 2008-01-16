package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: The Obsidian Knife.
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Alrak, a dwarf who abandoned the mountain dwarves to live in Kobold City</li>
 * <li>Ceryl, the librarian in Semos</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Alrak is hungry and asks for 100 pieces of ham, cheese or meat</li>
 * <li>Then, Alrak is bored and asks for a book</li>
 * <li>Get the book from Ceryl, and remember the name of who it is for</li>
 * <li>Bring the book to Alrak - he reads it for 3 days</li>
 * <li>After 3 days Alrak has learned how to make a knife from obsidian</li>
 * <li>Get obsidian for the blade and a fish for the fish bone handle</li>
 * <li>Alrak makes the knife for you</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>Obsidian Knife</li>
 * <li>11500 XP</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class ObsidianKnife extends AbstractQuest {

	private static final int REQUIRED_FOOD = 100;

	private static final List<String> FOOD_LIST = Arrays.asList("ham", "meat", "cheese");

	private static final int REQUIRED_DAYS = 3;

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "obsidian_knife";

	private static final String FISH = "cod";

	private static final String NAME = "Alrak";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void prepareQuestOfferingStep() {
		SpeakerNPC npc = npcs.get("Alrak");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					if (!player.hasQuest(QUEST_SLOT)
							|| player.getQuest(QUEST_SLOT).equals("rejected")) {
						npc.say("You know, it's hard to get food round here. I don't have any #supplies for next year.");
						npc.setCurrentState(ConversationStates.QUEST_ITEM_QUESTION);
					} else if (player.isQuestCompleted(QUEST_SLOT)) {
						npc.say("I'm inspired to work again! I'm making things for Wrvil now. Thanks for getting me interested in forging again.");
					} else if (player.hasQuest(QUEST_SLOT)
							&& player.getQuest(QUEST_SLOT).equals("food_brought")) {
						npc.say("Now I'm less worried about food I've realised I'm bored. There's a #book I'd love to read.");
						npc.setCurrentState(ConversationStates.QUEST_ITEM_BROUGHT);
					} else {
						npc.say("I'm sure I asked you to do something for me, already.");
					}
				}
			});

		/*
		 * Player agrees to collect the food asked for. his quest slot gets set
		 * with it so that later when he returns and says the food name, Alrak
		 * can check if that was the food type he was asked to bring.
		 */
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					String food = player.getQuest(QUEST_SLOT);
					npc.say("Thank you! I hope it doesn't take too long to collect. Don't forget to say '"
						+ food + "' when you have it.");
					// player.setQuest(QUEST_SLOT, food);
					player.addKarma(5.0);
					// set food to null?
				}
			});

		// Player says no. they might get asked to bring a different food next
		// time but they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"I'm not sure how I'll survive next year now. Good bye, cruel soul.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// Player asks what supplies he needs, and a random choice of what he
		// wants is made.
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, 
				"supplies", 
				null,
				ConversationStates.QUEST_OFFERED, 
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						String food = Rand.rand(FOOD_LIST);
						player.setQuest(QUEST_SLOT, food);
						npc.say("If you could get me " + REQUIRED_FOOD
								+ " pieces of " + food
								+ ", I'd be in your debt. Will you help me?");
					}
				});
	}

	private void bringFoodStep() {
		SpeakerNPC npc = npcs.get("Alrak");

		List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					String item = sentence.getTriggerExpression().getNormalized();
					if (player.drop(item, REQUIRED_FOOD)) {
						npc.say("Great! You brought the " + item + "!");
					}
				} });
		reward.add(new IncreaseXPAction(1000));
		reward.add(new IncreaseKarmaAction(35.0));
		reward.add(new SetQuestAction(QUEST_SLOT, "food_brought"));

		/** If player has quest and has brought the food, and says so, take it */
		npc.add(ConversationStates.ATTENDING, 
				FOOD_LIST,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						String item = sentence.getTriggerExpression().getNormalized();
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals(item)
								&& player.isEquipped(item, REQUIRED_FOOD);
					}
				}, 
				ConversationStates.ATTENDING, 
				null,
				new MultipleActions(reward));
	}

	private void requestBookStep() {
		SpeakerNPC npc = npcs.get("Alrak");

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"book",
				null,
				ConversationStates.QUEST_ITEM_BROUGHT,
				"It's about gems and minerals. I doubt you'd be interested ... but do you think you could get it somehow?",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Shame, I would really like to learn more about precious stones. Ah well, good bye.",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				"Thanks. Try asking at a library for a 'gem book'.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "seeking_book", 10.0));
	}

	private void getBookStep() {
		SpeakerNPC npc = npcs.get("Ceryl");

		npc.add(ConversationStates.ATTENDING,
				"gem book", 
				new QuestInStateCondition(QUEST_SLOT, "seeking_book"),
				ConversationStates.QUESTION_1,
				"You're in luck! Ognir brought it back just last week. Now, who is it for?",
				null);

		npc.add(ConversationStates.QUESTION_1, 
				NAME, 
				null,
				ConversationStates.ATTENDING, 
				"Ah, the mountain dwarf! Hope he enjoys the gem book.",
				new MultipleActions(new EquipItemAction("blue book", 1, true), 
				new SetQuestAction(QUEST_SLOT, "got_book")));

		// player says something which isn't the dwarf's name.
		npc.add(ConversationStates.QUESTION_1, 
				"",
				new NotCondition(new TriggerInListCondition(NAME.toLowerCase())),
				ConversationStates.QUESTION_1,
				"Hm, you better check who it's really for.", 
				null);
	}

	private void bringBookStep() {
		SpeakerNPC npc = npcs.get("Alrak");
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "got_book"), new PlayerHasItemWithHimCondition("blue book")),
				ConversationStates.IDLE, 
				"Great! I think I'll read this for a while. Bye!",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						player.drop("blue book");
						player.addXP(500);
						player.setQuest(QUEST_SLOT, "reading;" + System.currentTimeMillis());
					}
				});

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT) 
						&& (player.getQuest(QUEST_SLOT).equals("seeking_book") || player.getQuest(QUEST_SLOT).equals("got_book")) 
						&& !player.isEquipped("blue book");
					}
				},
				ConversationStates.ATTENDING,
				"Hello again. I hope you haven't forgotten about the gem book I wanted.",
				null);
	}

	private void offerKnifeStep() {

		SpeakerNPC npc = npcs.get("Alrak");
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).startsWith("reading;");
					}
				}, 
				ConversationStates.IDLE, 
				null, 
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						String[] tokens = player.getQuest(QUEST_SLOT)
								.split(";");
						long delay = REQUIRED_DAYS * 60 * 60 * 24 * 1000; // milliseconds in REQUIRED_DAYS days
						long timeRemaining = (Long.parseLong(tokens[1]) + delay)
								- System.currentTimeMillis();
						if (timeRemaining > 0L) {
							npc.say("I haven't finished reading that book. Maybe I'll be done in "
									+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
									+ ".");
							return;
						}
						npc.say("I've finished reading! That was really interesting. I learned how to make a special #knife from #obsidian.");
						player.setQuest(QUEST_SLOT, "knife_offered");
						player.notifyWorldAboutChanges();
						npc.setCurrentState(ConversationStates.QUEST_2_OFFERED);
					}
			});

		npc.add(ConversationStates.QUEST_2_OFFERED,
				"obsidian",
				null,
				ConversationStates.QUEST_2_OFFERED,
				"That book says that the black gem, obsidian, can be used to make a very sharp cutting edge. Fascinating! If you slay a black dragon to bring it, I'll make a #knife for you.",
				null);

		npc.add(ConversationStates.QUEST_2_OFFERED,
				"knife",
				null,
				ConversationStates.QUEST_2_OFFERED,
				"I'll make an obsidian knife if you can get the gem which makes the blade. Bring a "
						+ FISH
						+ " so that I can make the bone handle, too.",
				null);

		// player says hi to NPC when equipped with the fish and the gem and
		// he's killed a black dragon
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
							&& player.getQuest(QUEST_SLOT).equals("knife_offered")
							&& player.hasKilled("black dragon")
							&& player.isEquipped("obsidian")
							&& player.isEquipped(FISH);
					}
				}, 
				ConversationStates.IDLE, 
				null, 
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						player.drop("obsidian");
						player.drop(FISH);
						npc.say("You found the gem for the blade and the fish bone to make the handle! I'll start work right away. Come back in "
								+ REQUIRED_MINUTES + " minutes.");
						player.setQuest(QUEST_SLOT, "forging;" + System.currentTimeMillis());
					}
				});

		// player says hi to NPC when equipped with the fish and the gem and
		// he's not killed a black dragon
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
							&& player.getQuest(QUEST_SLOT).equals("knife_offered")
							&& !player.hasKilled("black dragon")
							&& player.isEquipped("obsidian")
							&& player.isEquipped(FISH);
					}
				},
				ConversationStates.ATTENDING,
				"Didn't you hear me properly? I told you to go slay a black dragon for the obsidian, not buy it! How do I know this isn't a fake gem? *grumble* I'm not making a special knife for someone who is scared to face a dragon.",
				null);

		// player says hi to NPC when not equipped with the fish and the gem
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("knife_offered")
								&& !(player.isEquipped("obsidian") && player.isEquipped(FISH));
					}
				},
				ConversationStates.ATTENDING,
				"Hello again. Don't forget I offered to make that obsidian knife, if you bring me a "
					+ FISH
					+ " and a piece of obsidian from a black dragon you killed. In the meantime if I can #help you, just say the word.",
				null);

		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).startsWith("forging;");
					}
				}, 
				ConversationStates.IDLE, 
				null, 
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						String[] tokens = player.getQuest(QUEST_SLOT)
								.split(";");
						long delay = REQUIRED_MINUTES * 60 * 1000; // minutes -> milliseconds
						long timeRemaining = (Long.parseLong(tokens[1]) + delay)
								- System.currentTimeMillis();
						if (timeRemaining > 0L) {
							npc.say("I haven't finished making the knife. Please check back in "
								+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
							return;
						}
						npc.say("The knife is ready! You know, that was enjoyable. I think I'll start making things again. Thanks!");
						player.addXP(10000);
						Item knife = StendhalRPWorld.get().getRuleManager()
								.getEntityManager().getItem("obsidian knife");
						knife.setBoundTo(player.getName());
						player.equip(knife, true);
						player.setQuest(QUEST_SLOT, "done");
						player.notifyWorldAboutChanges();
					}
				});

		// Here because of the random response from different actions bug, we
		// define the greeting message for all cases not covered so far:
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						final List<String> NOT_COVERED_LIST = Arrays.asList(
								"food_brought", "start", "meat", "ham",
								"cheese", "rejected");
						return !player.hasQuest(QUEST_SLOT)
								|| player.isQuestCompleted(QUEST_SLOT)
								|| (player.hasQuest(QUEST_SLOT) 
								&& NOT_COVERED_LIST.contains(player.getQuest(QUEST_SLOT)));
					}
				}, ConversationStates.ATTENDING,
				"How did you get down here? I usually only see #kobolds.", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		prepareQuestOfferingStep();
		bringFoodStep();
		requestBookStep();
		getBookStep();
		bringBookStep();
		offerKnifeStep();
	}

}
