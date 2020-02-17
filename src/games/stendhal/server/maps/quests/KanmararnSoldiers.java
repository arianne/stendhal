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
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropInfostringItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasInfostringItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerOwnsItemIncludingBankCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import marauroa.common.game.RPObject;
import marauroa.common.game.SlotIsFullException;

/**
 * QUEST:
 * <p>
 * Soldiers in Kanmararn.
 *
 * NOTE:
 * <p>
 * It also starts a quest that needs NPC McPegleg that is created. It doesn't
 * harm if that script is missing, just that the IOU cannot be delivered and
 * hence the player can't get cash
 *
 * PARTICIPANTS:
 * <li> Henry
 * <li> Sergeant James
 * <li> corpse of Tom
 * <li> corpse of Charles
 * <li> corpse of Peter
 *
 * STEPS:
 * <li> optional: speak to Sergeant James to get the task to find the map
 * <li> talk to Henry to get the task to find some proof that the other 3
 * soldiers are dead.
 * <li> collect the item in each of the corpses of the three other soldiers
 * <li> bring them back to Henry to get the map - bring the map to Sergeant
 * James
 *
 * REWARD:
 * <p>
 * from Henry:
 * <li> you can keep the IOU paper (for quest MCPeglegIOU)
 * <li> 2,500 XP
 * <li> some karma (15)
 * <p>
 * from Sergeant James
 * <li> mainio boots
 * <li> some karma (15)
 *
 * REPETITIONS:
 * <li> None.
 *
 * @see McPeglegIOU
 */
public class KanmararnSoldiers extends AbstractQuest {

	private static final Logger logger = Logger.getLogger(KanmararnSoldiers.class);

	private static final String QUEST_SLOT = "soldier_henry";

	/**
	 * The maximum time (in seconds) until plundered corpses will be filled
	 * again, so that other players can do the quest as well.
	 */
	private static final int CORPSE_REFILL_SECONDS = 60;

	/* Soldier names used in quest */
	private static final String SLD_HENRY = "Henry";
	private static final String SLD_CHARLES = "Charles";
	private static final String SLD_TOM = "Tom";
	private static final String SLD_PETER = "Peter";
	private static final String SRG_JAMES = "Sergeant James";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	/**
	 * A CorpseRefiller checks, in regular intervals, if the given corpse.
	 *
	 * @author daniel
	 *
	 */
	static class CorpseRefiller implements TurnListener {
		private final Corpse corpse;

		private final String itemName;

		private final String description;

		public CorpseRefiller(final Corpse corpse, final String itemName, final String description) {
			this.corpse = corpse;
			this.itemName = itemName;
			this.description = description;
		}

		public void start() {
			SingletonRepository.getTurnNotifier().notifyInTurns(1, this);
		}

		private boolean equalsExpectedItem(final Item item) {
			if (!item.getName().equals(itemName)) {
				return false;
			}

			if (!item.getDescription().equals(description)) {
				return false;
			}

			return corpse.getName().equals(item.getInfoString());
		}

		@Override
		public void onTurnReached(final int currentTurn) {
			boolean isStillFilled = false;
			// Check if the item is still in the corpse. Note that somebody
			// might have put other stuff into the corpse.
			for (final RPObject object : corpse.getSlot("content")) {
				if (object instanceof Item) {
					final Item item = (Item) object;
					if (equalsExpectedItem(item)) {
						isStillFilled = true;
					}
				}
			}
			try {
				if (!isStillFilled) {
					// recreate the item and fill the corpse
					final Item item = SingletonRepository.getEntityManager().getItem(
							itemName);
					item.setInfoString(corpse.getName());
					item.setDescription(description);
					corpse.add(item);
					corpse.notifyWorldAboutChanges();
				}
			} catch (final SlotIsFullException e) {
				// ignore, just don't refill the corpse until someone removes
				// the other items from the corpse
				logger.warn("Quest corpse is full: " + corpse.getName());
			}
			// continue the checking cycle
			SingletonRepository.getTurnNotifier().notifyInSeconds(CORPSE_REFILL_SECONDS, this);
		}
	}



	static class HenryQuestNotCompletedCondition implements ChatCondition {
		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			return !player.hasQuest(QUEST_SLOT) || player.getQuest(QUEST_SLOT).equals("start");
		}
	}

	static class HenryQuestCompletedCondition implements ChatCondition {
		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			return player.hasQuest(QUEST_SLOT) && !player.getQuest(QUEST_SLOT).equals("start");
		}
	}

	static class GiveMapAction implements ChatAction {
		private boolean bind = false;

		public GiveMapAction(boolean bind) {
			this.bind = bind;
		}

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			final Item map = SingletonRepository.getEntityManager().getItem("map");
			map.setInfoString(npc.getName());
			map.setDescription("You see a hand drawn map, but no matter how you look at it, nothing on it looks familiar.");
			if (bind) {
				map.setBoundTo(player.getName());
			}
			player.equipOrPutOnGround(map);
			player.setQuest(QUEST_SLOT, "map");
		}
	}


	/**
	 * We add text for NPC Henry who will get us on the quest.
	 */
	private void prepareCowardSoldier() {
		final SpeakerNPC henry = npcs.get(SLD_HENRY);

		henry.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(new QuestNotStartedCondition(QUEST_SLOT),
							 new QuestNotInStateCondition(QUEST_SLOT,"map")),
			ConversationStates.QUEST_OFFERED,
			"Find my #group, Peter, Tom, and Charles, prove it and I will reward you. Will you do it?",
			null);

		henry.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new OrCondition(new QuestCompletedCondition(QUEST_SLOT),
								 new QuestInStateCondition(QUEST_SLOT,"map")),
				ConversationStates.ATTENDING,
				"I'm so sad that most of my friends are dead.",
				null);

		henry.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Thank you! I'll be waiting for your return.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 3));

		// player tries to ask for quest again after starting
		henry.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new QuestNotInStateCondition(QUEST_SLOT, "map")),
				ConversationStates.ATTENDING,
				"I have already asked you to find my friends Peter, Tom, and Charles.",
				null);

		henry.add(
			ConversationStates.QUEST_OFFERED,
			"group",
			null,
			ConversationStates.QUEST_OFFERED,
			"The General sent five of us to explore this area in search for #treasure. So, will you help me find them?",
			null);

        henry.add(
				ConversationStates.QUEST_OFFERED,
				"treasure",
				null,
				ConversationStates.QUEST_OFFERED,
				"A big treasure is rumored to be #somewhere in this dungeon. Will you help me find my group?",
				null);

		henry.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"OK. I understand. I'm scared of the #dwarves myself.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new IncreaseXPAction(2500));
		actions.add(new DropInfostringItemAction("leather legs", SLD_TOM));
		actions.add(new DropInfostringItemAction("scale armor", SLD_PETER));
		actions.add(new IncreaseKarmaAction(15.0));
		actions.add(new GiveMapAction(false));

		henry.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(henry.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new PlayerHasInfostringItemWithHimCondition("leather legs", SLD_TOM),
						new PlayerHasInfostringItemWithHimCondition("note", SLD_CHARLES),
						new PlayerHasInfostringItemWithHimCondition("scale armor", SLD_PETER)),
				ConversationStates.ATTENDING,
				"Oh my! Peter, Tom, and Charles are all dead? *cries*. Anyway, here is your reward. And keep the IOU.",
				new MultipleActions(actions));

		henry.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(henry.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new NotCondition(
								new AndCondition(
										new PlayerHasInfostringItemWithHimCondition("leather legs", SLD_TOM),
										new PlayerHasInfostringItemWithHimCondition("note", SLD_CHARLES),
										new PlayerHasInfostringItemWithHimCondition("scale armor", SLD_PETER)))),
				ConversationStates.ATTENDING,
				"You didn't prove that you have found them all!",
				null);

		henry.add(ConversationStates.ATTENDING, Arrays.asList("map", "group", "help"),
				new OrCondition(
					new	QuestCompletedCondition(QUEST_SLOT),
					new AndCondition(new HenryQuestCompletedCondition(),
					new PlayerOwnsItemIncludingBankCondition("map"))),
				ConversationStates.ATTENDING,
				"I'm so sad that most of my friends are dead.", null);

		henry.add(ConversationStates.ATTENDING, Arrays.asList("map"),
				new AndCondition(
					new	QuestNotCompletedCondition(QUEST_SLOT),
					new HenryQuestCompletedCondition(),
					new NotCondition(new PlayerOwnsItemIncludingBankCondition("map"))),
				ConversationStates.ATTENDING,
				"Luckily I drew a copy of the map, but please don't lose this one.",
				new GiveMapAction(true));

		henry.add(ConversationStates.ATTENDING, Arrays.asList("map"),
				new HenryQuestNotCompletedCondition(),
				ConversationStates.ATTENDING,
				"If you find my friends, I will give you the map.", null);
	}

	/**
	 * add corpses of ex-NPCs.
	 */
	private void prepareCorpses() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("-6_kanmararn_city");

		// Now we create the corpse of the second NPC
		final Corpse tom = new Corpse("youngsoldiernpc", 5, 47);
		// he died first
		tom.setStage(4);
		tom.setName(SLD_TOM);
		tom.setKiller("a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		zone.add(tom);

		// Add a refiller to automatically fill the corpse of unlucky Tom
		final CorpseRefiller tomRefiller = new CorpseRefiller(tom, "leather legs",
				"You see torn leather legs that are heavily covered with blood.");
		tomRefiller.start();

		// Now we create the corpse of the third NPC
		final Corpse charles = new Corpse("youngsoldiernpc", 94, 5);
		// he died second
		charles.setStage(3);
		charles.setName(SLD_CHARLES);
		charles.setKiller("a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		zone.add(charles);
		// Add a refiller to automatically fill the corpse of unlucky Charles
		final CorpseRefiller charlesRefiller = new CorpseRefiller(charles, "note",
				"You read: \"IOU 250 money. (signed) McPegleg\"");
		charlesRefiller.start();

		// Now we create the corpse of the fourth NPC
		final Corpse peter = new Corpse("youngsoldiernpc", 11, 63);
		// he died recently
		peter.setStage(2);
		peter.setName(SLD_PETER);
		peter.setKiller("a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		zone.add(peter);
		// Add a refiller to automatically fill the corpse of unlucky Peter
		final CorpseRefiller peterRefiller = new CorpseRefiller(
				peter,
				"scale armor",
				"You see a slightly rusty scale armor. It is heavily deformed by several strong hammer blows.");
		peterRefiller.start();
	}

	/**
	 * add James.
	 */
	private void prepareSergeant() {
		final SpeakerNPC james = npcs.get(SRG_JAMES);

		// quest related stuff
		james.addHelp("Think I need a little help myself. My #group got killed and #one of my men ran away. Too bad he had the #map.");
		james.addQuest("Find my fugitive soldier and bring him to me ... or at least the #map he's carrying.");
		james.addReply("group",
			"We were five, three of us died. You probably passed their corpses.");
		james.addReply(Arrays.asList("one", "henry"),
			"Yes, my youngest soldier. He ran away.");
		james.addReply("map",
			"The #treasure map that leads into the heart of the #dwarven #kingdom.");
		james.addReply("treasure",
			"A big treasure is rumored to be somewhere in this dungeon.");
		james.addReply(Arrays.asList("dwarf", "dwarves", "dwarven", "dwarven kingdom"),
			"They are strong enemies! We're in their #kingdom.");
		james.addReply(Arrays.asList("peter", "tom", "charles"),
			"He was a good soldier and fought bravely.");
		james.addReply(Arrays.asList("kingdom", "kanmararn"),
			"Kanmararn, the legendary kingdom of the #dwarves.");
		james.addReply("dreamscape",
			"There's a man east of town. He knows the way.");

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new IncreaseXPAction(5000));
		actions.add(new DropInfostringItemAction("map", SLD_HENRY));
		actions.add(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "done", 15.0));
		actions.add(new EquipItemAction("mainio boots", 1, true));

		james.add(ConversationStates.ATTENDING,
				Arrays.asList("map", "henry"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "map"),
								new PlayerHasInfostringItemWithHimCondition("map", SLD_HENRY)),
				ConversationStates.ATTENDING,
				"The map! Wonderful! Thank you. And here is your reward. I got these boots while on the #dreamscape.",
				new MultipleActions(actions));

		james.add(ConversationStates.ATTENDING,
				Arrays.asList("map", "henry"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "map"),
								new NotCondition(new PlayerHasInfostringItemWithHimCondition("map", SLD_HENRY))),
				ConversationStates.ATTENDING,
				"Well, where is the map?",
				null);

		james.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thanks again for bringing me the map!", null);

		james.add(ConversationStates.ATTENDING, ConversationPhrases.HELP_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thanks again for bringing me the map!", null);

		james.add(ConversationStates.ATTENDING, Arrays.asList("map", "henry",
			 "group", "one"),
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Thanks again for bringing me the map!", null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Kanmararn Soldiers",
				"Some time ago, Sergeant James started with his crew of four brave soldiers on their adventure of finding a treasure in Kanmararn, the city of dwarves. They didn't return yet.",
				true);
		prepareCowardSoldier();
		prepareCorpses();
		prepareSergeant();
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			final String questState = player.getQuest(QUEST_SLOT);
			res.add("I met a scared soldier in Kanmararn City. He asked me to find his friends, Peter, Charles, and Tom.");
			if ("rejected".equals(questState)) {
				res.add("I don't want to help Henry.");
				return res;
			}
			if ("start".equals(questState)) {
				return res;
			}
			res.add("Sadly I only found corpses of Peter, Charles, and Tom. Henry was aghast. He gave me a map and an IOU, but didn't say what I should do with them now.");
			if ("map".equals(questState)) {
				return res;
			}
			res.add("I met Sergeant James and gave him the treasure map. He gave me an excellent pair of mainio boots in return.");
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
		return "KanmararnSoldiers";
	}

	@Override
	public int getMinLevel() {
		return 40;
	}

	@Override
	public String getNPCName() {
		return SLD_HENRY;
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_DUNGEONS;
	}
}
