package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QUEST: The Ring Maker
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Ognir, who works in the weapon shop in Fado
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>If you go to Ognir with a broken emerald ring he offers to fix it </li>
 * <li>Bring him the money he wants (a lot) and gold to fix the ring.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>Fixed Ring</li>
 * <li>500 XP</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>Anytime you need it</li>
 * </ul>
 * 
 * NOTE: This quest uses the same NPC as Marriage.java, we need to be careful
 * not to interfere with that mission.
 */
public class RingMaker extends AbstractQuest {

	private static final int REQUIRED_GOLD = 2;

	private static final int REQUIRED_MONEY = 80000;

	private static final int REQUIRED_GEM = 1;

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "fix_emerald_ring";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void FixRingStep() {
		final SpeakerNPC npc = npcs.get("Ognir");

		npc.add(ConversationStates.ATTENDING, Arrays.asList("emerald ring", "life", "emerald"),
			new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "forging;")),
			ConversationStates.QUEST_ITEM_BROUGHT, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					final Item emeraldRing = player.getFirstEquipped("emerald ring");
					if (emeraldRing != null) {
						if (emeraldRing.getInt("amount") > 0) {
							// ring is not broken so he just lets player know
							// where it can be fixed
							npc.say("I see you already have an emerald ring. If it gets broken, you can come to me to fix it.");
							npc.setCurrentState(ConversationStates.ATTENDING);
						} else {
							// notices ring is broken
							npc.say("What a pity, your emerald ring is broken. I can fix it, for a #price.");
						}
					} else {
						// they don't have a ring with them, tell about quest
						npc.say("It is difficult to get the ring of life. Do a favour for a powerful elf in Nal'wor and you may receive one as a reward.");
						npc.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});

		npc.add(ConversationStates.ATTENDING, Arrays.asList("emerald ring", "life", "emerald"),
			new QuestStateStartsWithCondition(QUEST_SLOT, "forging;"),
			ConversationStates.IDLE, null, new SpeakerNPC.ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
					// minutes -> milliseconds
					final long delay = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE; 
					final long timeRemaining = (Long.parseLong(tokens[1]) + delay)
							- System.currentTimeMillis();
					if (timeRemaining > 0L) {
						npc.say("I haven't finished fixing your ring of life. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
							+ ". Good bye for now.");
						return;
					}
					npc.say("I'm pleased to say, your ring of life is fixed! It's good as new now.");
					player.addXP(500);
					final Item emeraldRing = SingletonRepository.getEntityManager().getItem(
									"emerald ring");
					emeraldRing.setBoundTo(player.getName());
					player.equip(emeraldRing, true);
					player.setQuest(QUEST_SLOT, "done");
					player.notifyWorldAboutChanges();
					npc.setCurrentState(ConversationStates.ATTENDING);
				}
			});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT, "price", null,
			ConversationStates.QUEST_ITEM_BROUGHT,
			"The charge for my service is " + REQUIRED_MONEY
					+ " money, and I need " + REQUIRED_GOLD
					+ " gold bars and " + REQUIRED_GEM
					+ " emerald to fix the ring. Do you want to pay now?",
			null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.IDLE, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					if ((player.isEquipped("gold bar", REQUIRED_GOLD))
							&& (player.isEquipped("money", REQUIRED_MONEY))
							&& (player.isEquipped("emerald", REQUIRED_GEM))) {
						player.drop("gold bar", REQUIRED_GOLD);
						player.drop("emerald", REQUIRED_GEM);
						player.drop("money", REQUIRED_MONEY);
						player.drop("emerald ring");
						npc.say("Okay, that's all I need to fix the ring. Come back in "
							+ REQUIRED_MINUTES
							+ " minutes and it will be ready. Bye for now.");
						player.setQuest(QUEST_SLOT, "forging;"
								+ System.currentTimeMillis());
					} else {
						npc.say("Come back when you have the money, the gem and the gold. Goodbye.");
					}
				}
			});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING, 
			"No problem, just come back when you have the money, the emerald, and the gold.",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		FixRingStep();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.startsWith("forging;")) {
			res.add("FORGING");
		}
		return res;
	}
}
