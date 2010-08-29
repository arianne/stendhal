package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.item.RingOfLife;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

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

	private static final String FORGING = "forging";

	private static final int REQUIRED_GOLD = 2;

	private static final int REQUIRED_MONEY = 80000;

	private static final int REQUIRED_GEM = 1;

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "fix_emerald_ring";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	void fixRingStep(final SpeakerNPC npc) {

		npc.add(ConversationStates.ATTENDING, Arrays.asList("emerald ring", "life", "emerald"),
			new AndCondition(new PlayerHasItemWithHimCondition("emerald ring"), 
					         new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, FORGING))),
			ConversationStates.QUEST_ITEM_BROUGHT, 
			null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final RingOfLife emeraldRing = (RingOfLife) player.getFirstEquipped("emerald ring");
					
						if (emeraldRing.isBroken()) {
							npc.say("What a pity, your emerald ring is broken. I can fix it, for a #price.");
						} else {
							// ring is not broken so he just lets player know
							// where it can be fixed
							npc.say("I see you already have an emerald ring. If it gets broken, you can come to me to fix it.");
							npc.setCurrentState(ConversationStates.ATTENDING);
						}
					
				}
			});
		
		npc.add(ConversationStates.ATTENDING, 
				Arrays.asList("emerald ring", "life", "emerald"),
				new AndCondition(new NotCondition(new PlayerHasItemWithHimCondition("emerald ring")), 
				         new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, FORGING))),

				ConversationStates.ATTENDING, 
				"It is difficult to get the ring of life. Do a favour for a powerful elf in Nal'wor and you may receive one as a reward."
				, null);
	
		npc.add(ConversationStates.ATTENDING, 
				Arrays.asList("emerald ring", "life", "emerald"),
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, FORGING),
						new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, FORGING + "unbound")),
						new TimePassedCondition(QUEST_SLOT,1,REQUIRED_MINUTES)),
				ConversationStates.ATTENDING, 
				"I'm pleased to say, your ring of life is fixed! It's good as new now.",
				new MultipleActions(
						new IncreaseXPAction(500),
						new SetQuestAction(QUEST_SLOT, "done"),
						new EquipItemAction("emerald ring", 1, true)));
		
		npc.add(ConversationStates.ATTENDING, 
				Arrays.asList("emerald ring", "life", "emerald"),
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, FORGING),
						new QuestStateStartsWithCondition(QUEST_SLOT, FORGING + "unbound"),
						new TimePassedCondition(QUEST_SLOT,1,REQUIRED_MINUTES)),
				ConversationStates.ATTENDING, 
				"I'm pleased to say, your ring of life is fixed! It's good as new now.",
				new MultipleActions(
						new IncreaseXPAction(500),
						new SetQuestAction(QUEST_SLOT, "done"),
						new EquipItemAction("emerald ring", 1, false)));

		npc.add(ConversationStates.ATTENDING, 
				Arrays.asList("emerald ring", "life", "emerald"),
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, FORGING),
						new NotCondition(new TimePassedCondition(QUEST_SLOT,1,REQUIRED_MINUTES))),
				ConversationStates.IDLE, null,
				new SayTimeRemainingAction(QUEST_SLOT,1, REQUIRED_MINUTES,"I haven't finished fixing your ring of life. Please check back in"));
		
		
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT, 
				"price", 
				null,
				ConversationStates.QUEST_ITEM_BROUGHT,
				"The charge for my service is " + REQUIRED_MONEY
					+ " money, and I need " + REQUIRED_GOLD
					+ " gold bars and " + REQUIRED_GEM
					+ " emerald to fix the ring. Do you want to pay now?",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES, 
				new AndCondition(
						new PlayerHasItemWithHimCondition("gold bar", REQUIRED_GOLD),
						new PlayerHasItemWithHimCondition("money", REQUIRED_MONEY),
						new PlayerHasItemWithHimCondition("emerald", REQUIRED_GEM)), 
				ConversationStates.IDLE,
				"Okay, that's all I need to fix the ring. Come back in "
						+ REQUIRED_MINUTES
						+ " minutes and it will be ready. Bye for now.",
				new MultipleActions(
						new DropItemAction("gold bar", REQUIRED_GOLD),
						new DropItemAction("emerald", REQUIRED_GEM),
						new DropItemAction("money", REQUIRED_MONEY), 
						new ChatAction() {
							public void fire(final Player player,
									final Sentence sentence,
									final EventRaiser npc) {
								final RingOfLife emeraldRing = (RingOfLife) player.getFirstEquipped("emerald ring");
								if (player.isBoundTo(emeraldRing)) {
									player.setQuest(QUEST_SLOT, "forging;"
											+ System.currentTimeMillis());
								} else {
									player.setQuest(QUEST_SLOT, "forgingunbound;"
											+ System.currentTimeMillis());
								}

							}
						},
						new DropItemAction("emerald ring")));
		
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES, 
				new NotCondition(new  AndCondition(
						new PlayerHasItemWithHimCondition("gold bar", REQUIRED_GOLD),
						new PlayerHasItemWithHimCondition("money", REQUIRED_MONEY),
						new PlayerHasItemWithHimCondition("emerald", REQUIRED_GEM))),
				ConversationStates.IDLE, 
				"Come back when you have the money, the gem and the gold. Goodbye.",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES, 
			null,
			ConversationStates.ATTENDING, 
			"No problem, just come back when you have the money, the emerald, and the gold.",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		fixRingStep(npcs.get("Ognir"));
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("Oh no! My ring of life broke while I died. Ognir said he will forge me a new one after bringin him an emerald, 80000 money and 2 gold bars.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.startsWith(FORGING)) {
			res.add("Ognir forges me a new ring now which will be finished in 10 minutes. Phew!");
		}
		return res;
	}

	@Override
	public String getName() {
		return "RingMaker";
	}
}
