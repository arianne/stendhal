/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.nalwor.forest;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.LogoutListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.BreakableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.area.FlyOverArea;
import games.stendhal.server.entity.mapstuff.portal.ConditionAndActionPortal;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.NPCEmoteAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.AreaIsFullCondition;
import games.stendhal.server.entity.npc.condition.ComparisonOperator;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerStatLevelCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShowItemListEvent;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.maps.quests.AbstractQuest;
import games.stendhal.server.util.Area;
import games.stendhal.server.util.TimeUtil;

/**
 * TODO: create JUnit test
 * FIXME: should bows wear & break even if hit not successful?
 * FIXME: (client) no sound for training targets when hit
 */
public class ArcheryRange implements ZoneConfigurator,LoginListener,LogoutListener {

	/** logger instance */
	private static Logger logger = Logger.getLogger(ArcheryRange.class);

	/** quest/activity identifier */
	private static final String QUEST_SLOT = "archery_range";

	/** cost to use archery range */
	private static final int COST = 10000;

	/** capped range attack level */
	private static final int RATK_LIMIT = 80;

	/** time (in seconds) allowed for training session */
	private static final int TRAIN_TIME = 15 * MathHelper.SECONDS_IN_ONE_MINUTE;

	/** time player must wait to train again */
	private static final int COOLDOWN = 6 * MathHelper.MINUTES_IN_ONE_HOUR;

	/** max number of players allowed in training area at a time */
	private static final int MAX_OCCUPANTS = 10;

	/** zone info */
	private StendhalRPZone archeryZone;
	private String archeryZoneID;

	/** archery range area */
	private final Rectangle2D archeryArea = new Rectangle(97, 97, 19, 10);

	/** NPC that manages archery area */
	private static final String npcName = "Chester";
	private SpeakerNPC npc;

	private static final int bowPrice = 4500;

	/** phrases used in conversations */
	private static final List<String> TRAIN_PHRASES = Arrays.asList("train", "training");
	private static final List<String> FEE_PHRASES = Arrays.asList("fee", "cost", "charge");

	/** quest states */
	private static final String STATE_ACTIVE = "training";
	private static final String STATE_DONE = "done";

	private static final String FULL_MESSAGE = "The archery range is full. Come back later.";

	/** position of portal that manages access to training area */
	private static final Point PORTAL_POS = new Point(116, 104);

	/** misc objects for JUnit test */
	private static AbstractQuest quest;
	private static ShopSign blackboard;


	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		// set up the login/logout notifiers
		SingletonRepository.getLoginNotifier().addListener(this);
		SingletonRepository.getLogoutNotifier().addListener(this);

		archeryZone = zone;
		archeryZoneID = zone.getName();

		buildNPC();
		initShop();
		initRepairShop();
		initTraining();
		initEntrance();
		initBlockers();
		addToQuestSystem();
	}

	private void buildNPC() {
		npc = new SpeakerNPC(npcName) {
			@Override
			protected void createDialog() {
				addGreeting("This is the assassins' archery range. Watch yer tongue if ya don't want to get hurt.");
				addGoodbye("Come back when ya got some cash. Courtesy aint currency 'round here.");
				addJob("I manage this here archery range. It belongs to the assassins, so don't go pokin' yer nose where it doesn't belong.");
				addQuest("Do I look like I need any help!? If yer not here to #train then get outta my sight!");
				addReply("training bow", "Training bows are weak but easy to use, so you can fire from them"
						+ " much faster than with a normal bow. But because of their poor quality, they don't"
						+ " last long.");
				addHelp("This is the assassins' archery range. I can let you #train here for a #fee"
						+ " if you're in good with HQ. If you haven't quite got the range, try the targets"
						+ " on the end. The ninjas seem to like those. I recommend using a #'training bow'.");
				addReply(FEE_PHRASES, "The fee to #train is " + Integer.toString(COST) + " money.");
			}

			@Override
			protected void onGoodbye(final RPEntity player) {
				setDirection(Direction.DOWN);
			}

			@Override
			public void say(final String text) {
				// don't turn toward player
				say(text, false);
			}
		};

		npc.setDescription("You see a man who appears to be a skilled assassin.");
		npc.setPosition(120, 100);
		npc.setEntityClass("rangernpc");
		archeryZone.add(npc);
	}

	/**
	 * Adds bow & arrows for sale from NPC.
	 */
	private void initShop() {
		final String rejectedMessage = "I'm not selling you anything without proof that you can be trusted.";

		// override the default offer message
		npc.add(ConversationStates.ANY,
				ConversationPhrases.OFFER_MESSAGES,
				new PlayerHasItemWithHimCondition("assassins id"),
				ConversationStates.ATTENDING,
				"Don't look at me all dumbfounded! Check my bow and arrow prices on"
						+ " that blackboard over there. But if yer lookin' for a"
						+ " bargain, then scram and find some other witless sucker.",
				null);

		// player wants to buy items but does not have assassins id
		npc.add(ConversationStates.ANY,
				ConversationPhrases.OFFER_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.ATTENDING,
				rejectedMessage,
				null);

		// prices are higher than those of other shops
		final Map<String, Integer> shop = new LinkedHashMap<>();
		shop.put("wooden arrow", 4);
		shop.put("wooden spear", 125);
		shop.put("wooden bow", 600);
		shop.put("longbow", 1200);
		shop.put("training bow", bowPrice);

		// override seller bahaviour so that player must have assassins id
		final SellerBehaviour seller = new SellerBehaviour(shop) {
			@Override
			public ChatCondition getTransactionCondition() {
				return new PlayerHasItemWithHimCondition("assassins id");
			}

			@Override
			public ChatAction getRejectedTransactionAction() {
				return new SayTextAction(rejectedMessage);
			}
		};
		new SellerAdder().addSeller(npc, seller, false);

		// a sign showing prices of items
		blackboard = new ShopSign("sellarcheryrange", "Assassins' Archery Shop", "Bows and arrows sold here:", true) {
			@Override
			public boolean onUsed(final RPEntity user) {
				// Chester is protective, even of his blackboard if player doesn't have assassins ID
				if (user.isEquipped("assassins id")) {
					List<Item> itemList = generateItemList(shop);
					ShowItemListEvent event = new ShowItemListEvent(this.title, this.caption, itemList);
					user.addEvent(event);
					user.notifyWorldAboutChanges();
				} else {
					npc.say("Get away from my blackboard you mongrel!");
				}

				return true;
			}
		};
		blackboard.setEntityClass("blackboard");
		blackboard.setPosition(117, 101);
		archeryZone.add(blackboard);
	}

	/**
	 * If players bring their worn training swords they can get them repaired for half the
	 * price of buying a new one.
	 */
	private void initRepairShop() {
		final Sign repairSign = new Sign();
		repairSign.setEntityClass("notice");
		repairSign.setPosition(118, 101);
		repairSign.setText("Training bows #repaired here for half the price of new ones.");
		archeryZone.add(repairSign);

		final List<String> repairPhrases = Arrays.asList("repair", "fix");

		final ChatCondition needsRepairCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return getUsedBowsCount(player) > 0;
			}
		};

		final ChatCondition canAffordRepairsCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return player.isEquipped("money", getRepairPrice(getUsedBowsCount(player)));
			}
		};

		final ChatAction sayRepairPriceAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final int usedBows = getUsedBowsCount(player);
				final boolean multiple = usedBows > 1;

				final StringBuilder sb = new StringBuilder("You have " + Integer.toString(usedBows) + " used training bow");
				if (multiple) {
					sb.append("s");
				}
				sb.append(". I can repair ");
				if (multiple) {
					sb.append("them all");
				} else {
					sb.append("it");
				}
				sb.append(" for " + Integer.toString(getRepairPrice(usedBows)) + " money. Would you like me to do so?");

				npc.say(sb.toString());
			}
		};

		final ChatAction repairAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final int usedBows = getUsedBowsCount(player);
				player.drop("money", getRepairPrice(usedBows));

				for (final Item bow: player.getAllEquipped("training bow")) {
					final BreakableItem breakable = (BreakableItem) bow;
					if (breakable.isUsed()) {
						breakable.repair();
					}
				}

				if (usedBows > 1) {
					npc.say("Done! Your training bows are as good as new.");
				} else {
					npc.say("Done! Your training bow is as good as new.");
				}

				npc.addEvent(new SoundEvent("coins-01", SoundLayer.CREATURE_NOISE));
			}
		};


		npc.add(ConversationStates.ATTENDING,
				repairPhrases,
				new NotCondition(new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.ATTENDING,
				"Only members of the assassins guild can have their #'training bows' repaired.",
				null);

		npc.add(ConversationStates.ATTENDING,
				repairPhrases,
				new AndCondition(
						new PlayerHasItemWithHimCondition("assassins id"),
						new NotCondition(needsRepairCondition)),
				ConversationStates.ATTENDING,
				"You don't have any #'training bows' that need repaired.",
				null);

		npc.add(ConversationStates.ATTENDING,
				repairPhrases,
				new AndCondition(
						new PlayerHasItemWithHimCondition("assassins id"),
						needsRepairCondition),
				ConversationStates.QUESTION_2,
				null,
				sayRepairPriceAction);

		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Good luck then. Remember, once they break, they can't be repaired.",
				null);

		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(needsRepairCondition),
				ConversationStates.ATTENDING,
				"Did you drop your bow?",
				null);

		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition,
						new NotCondition(canAffordRepairsCondition)),
				ConversationStates.ATTENDING,
				"You don't have enough money. Get outta here!",
				null);

		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition,
						canAffordRepairsCondition),
				ConversationStates.ATTENDING,
				null,
				repairAction);
	}

	private int getUsedBowsCount(final Player player) {
		int count = 0;
		for (final Item bow: player.getAllEquipped("training bow")) {
			if (((BreakableItem) bow).isUsed()) {
				count++;
			}
		}

		return count;
	}

	private int getRepairPrice(final int count) {
		return count * (bowPrice / 2);
	}

	/**
	 * Initializes conversation & actions for archery training.
	 */
	private void initTraining() {

		// player has never trained before
		npc.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new QuestNotStartedCondition(QUEST_SLOT),
						new PlayerStatLevelCondition("ratk", ComparisonOperator.LESS_THAN, RATK_LIMIT),
						new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.QUESTION_1,
				null,
				new MultipleActions(
						new NPCEmoteAction(npcName + " looks over your assassins id.", false),
						new SayTextAction("Hmmm, I haven't seen you around here before."
								+ " But you have the proper credentials. Do you want me to"
								+ " open up the archery range? It'll cost ya " + Integer.toString(COST)
								+ " money.")));

		// player returns after cooldown period is up
		npc.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_DONE),
						new TimePassedCondition(QUEST_SLOT, 1, COOLDOWN),
						new PlayerStatLevelCondition("ratk", ComparisonOperator.LESS_THAN, RATK_LIMIT)),
				ConversationStates.QUESTION_1,
				"It's " + Integer.toString(COST) + " money to train. So, you good for it?",
				null);

		// player returns before cooldown period is up
		npc.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, COOLDOWN)),
						new PlayerStatLevelCondition("ratk", ComparisonOperator.LESS_THAN, RATK_LIMIT)),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, COOLDOWN, "You can't train again yet. Come back in"));

		// player's RATK level is too high
		npc.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new PlayerStatLevelCondition("ratk", ComparisonOperator.GREATER_OR_EQUALS, RATK_LIMIT),
				ConversationStates.ATTENDING,
				"You are already too skilled to train here. Now get off yer lazy butt and fight some monsters!",
				null);

		// player does not have an assassins id
		npc.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new PlayerStatLevelCondition("ratk", ComparisonOperator.LESS_THAN, RATK_LIMIT),
						new NotCondition(new PlayerHasItemWithHimCondition("assassins id"))),
				ConversationStates.ATTENDING,
				"You can't train here without permission from the assassins' HQ. Now git, before I sic the dogs on you!",
				null);

		// player training state is active
		npc.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new QuestInStateCondition(QUEST_SLOT, 0, STATE_ACTIVE),
				ConversationStates.ATTENDING,
				"Get out of here with that! You already paid for a training session.",
				null);

		// player meets requirements but training area is full
		Area area = new Area(SingletonRepository.getRPWorld().getZone(archeryZoneID), archeryArea);
		npc.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new PlayerStatLevelCondition("ratk", ComparisonOperator.LESS_THAN, RATK_LIMIT),
						new PlayerHasItemWithHimCondition("assassins id"),
						new AreaIsFullCondition(area, MAX_OCCUPANTS)),
				ConversationStates.ATTENDING,
				FULL_MESSAGE,
				null);

		/* player has enough money to begin training
		 *
		 * XXX: If admin alters player's quest slot, timer/notifier is not removed. Which
		 *      could potentially lead to strange behavior. But this should likely never
		 *      happen on live server. In an attempt to prevent such issues, the old
		 *      timer/notifier will be removed if the player begins a new training session.
		 *      Else the timer will simply be removed once it has run its lifespan.
		 */
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new PlayerHasItemWithHimCondition("money", COST),
				ConversationStates.IDLE,
				"You can train for up to " + Integer.toString(TRAIN_TIME / MathHelper.SECONDS_IN_ONE_MINUTE) + " minutes. So make good use of yer time.",
				new MultipleActions(
						new DropItemAction("money", COST),
						new SetQuestAction(QUEST_SLOT, STATE_ACTIVE + ";" + Integer.toString(TRAIN_TIME)),
						new ArcheryRangeTimerAction()));

		// player does not have enough money to begin training
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition("money", COST)),
				ConversationStates.ATTENDING,
				"What's this? You don't even have enough money for the #fee. Be gone with ya!",
				null);

		// player does not want to train
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Then quit wasting my time!",
				null);

		/* FIXME: How to get updated remaining time?
		// player asks how much time is left in training session
		npc.add(ConversationStates.ATTENDING,
				"time",
				new QuestInStateCondition(QUEST_SLOT, 0, STATE_ACTIVE),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, TRAIN_TIME, "Your training will end in about"));
		*/
	}

	/**
	 * Initializes portal entity that manages access to the training area.
	 */
	private void initEntrance() {
		archeryZone.add(new ArcheryRangeConditionAndActionPortal());
	}

	private void initBlockers() {
		final int[] xLocations = {
				97, 99, 101, 103, 105, 107, 109, 111, 113, 115
		};

		for (final int x: xLocations) {
			final FlyOverArea blocker = new FlyOverArea();
			blocker.setPosition(x, 102);

			archeryZone.add(blocker);
		}
	}

	/**
	 * Makes visible in inspect command.
	 */
	private void addToQuestSystem() {
		quest = new AbstractQuest() {

			@Override
			public List<String> getHistory(Player player) {
				return null;
			}

			@Override
			public String getSlotName() {
				return QUEST_SLOT;
			}

			@Override
			public void addToWorld() {
			}

			@Override
			public String getName() {
				return "ArcheryRange";
			}
		};

		SingletonRepository.getStendhalQuestSystem().loadQuest(quest);
	}

	/**
	 * Allows time remaining to be altered by changing quest slot.
	 */
	private Integer updateTimeRemaining(final Player player) {
		try {
			final int timeRemaining = Integer.parseInt(player.getQuest(QUEST_SLOT, 1)) - 1;
			player.setQuest(QUEST_SLOT, 1, Integer.toString(timeRemaining));
			return timeRemaining;
		} catch (NumberFormatException e) {
			// couldn't get time remaining from quest state
			SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));

			e.printStackTrace();
		}

		return null;
	}


	/**
	 * Checks if entity is within bounds of an area.
	 *
	 * @param area
	 * 		Area dimensions to check.
	 * @return
	 * 		<code>true</code> if entity is within area.
	 */
	public boolean isPlayerInArea(final Player player, final String zoneid, final Rectangle2D area) {
		// TODO: Use standard collision check, which can handle entities larger than 1x1
		if (!player.get("zoneid").equals(zoneid)) {
			return false;
		}
		return area.contains(player.getInt("x"), player.getInt("y"));
	}

	/**
	 * Actions to take when player logs in.
	 */
	@Override
	public void onLoggedIn(final Player player) {
		// don't allow players to login within archery range area boundaries
		if (isPlayerInArea(player, archeryZoneID, archeryArea) || (player.getX() == PORTAL_POS.x && player.getY() == PORTAL_POS.y)) {
			player.teleport(archeryZoneID, 118, 104, null, null);
		}

		final String sessionState = player.getQuest(QUEST_SLOT, 0);
		if (sessionState != null && sessionState.equals(STATE_ACTIVE)) {
			final String sessionTimeString = player.getQuest(QUEST_SLOT, 1);
			if (sessionTimeString != null) {
				// re-initialize turn notifier if player still has active training session
				new ArcheryRangeTimerAction().fire(player, null, null);
			}
		}
	}

	/**
	 * Actions to take when player logs out.
	 */
	@Override
	public void onLoggedOut(Player player) {
		// disable timer/notifier
		SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));
	}

	/**
	 * Teleports player out of archery range training area.
	 */
	private void endTrainingSession(final Player player) {
		if (player.get("zoneid").equals(archeryZoneID)) {
			npc.say("Your training time is up " + player.getName() + ".");
		}
		if (isPlayerInArea(player, archeryZoneID, archeryArea)) {
			player.teleport(archeryZoneID, 118, 104, null, null);
		}

		player.setQuest(QUEST_SLOT, STATE_DONE + ";" + Long.toString(System.currentTimeMillis()));
	}

	/**
	 * Retrieves objects used for archery range functions.
	 */
	public List<Object> getJunitObjects() {
		return Arrays.asList(
				quest,
				blackboard,
				COST,
				TRAIN_TIME);
	}


	/**
	 * Notifies player of time remaining for training & ends training session.
	 */
	private class Timer implements TurnListener {

		private final WeakReference<Player> timedPlayer;

		private Integer timeRemaining = 0;

		protected Timer(final Player player) {
			timedPlayer = new WeakReference<Player>(player);

			try {
				final String questState = timedPlayer.get().getQuest(QUEST_SLOT, 0);
				if (questState != null && questState.equals(STATE_ACTIVE)) {
					// set player's time remaining from quest slot value
					timeRemaining = Integer.parseInt(timedPlayer.get().getQuest(QUEST_SLOT, 1));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onTurnReached(int currentTurn) {
			final Player playerTemp = timedPlayer.get();

			if (playerTemp != null) {
				if (timeRemaining != null && timeRemaining > 0) {
					// notify players at 10 minute mark & every minute after 5 minute mark
					if (timeRemaining == 10 * MathHelper.SECONDS_IN_ONE_MINUTE ||
							(timeRemaining <= 5 * MathHelper.SECONDS_IN_ONE_MINUTE && timeRemaining % 60 == 0)) {
						npc.say(playerTemp.getName() + ", you have " + TimeUtil.timeUntil(timeRemaining) + " left.");
					}
					// remaining time needs to be updated every second in order to be saved if player logs out
					timeRemaining = updateTimeRemaining(playerTemp);
					SingletonRepository.getTurnNotifier().notifyInSeconds(1, this);
				} else {
					endTrainingSession(playerTemp);
				}
			}
		}

		@Override
		public int hashCode() {
			final Player player = timedPlayer.get();

			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((player == null) ? 0 : player.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			final Player player = timedPlayer.get();

			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Timer other = (Timer) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (player == null) {
				if (other.timedPlayer.get() != null) {
					return false;
				}
			} else if (!player.equals(other.timedPlayer.get())) {
				return false;
			}
			return true;
		}

		private ArcheryRange getOuterType() {
			return ArcheryRange.this;
		}
	}


	/**
	 * Action that notifies
	 */
	private class ArcheryRangeTimerAction implements ChatAction {

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			// remove any existing notifiers
			SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));

			// create the new notifier
			SingletonRepository.getTurnNotifier().notifyInTurns(0, new Timer(player));
		}
	}


	/**
	 * Special portal for checking multiple conditions.
	 */
	private class ArcheryRangeConditionAndActionPortal extends ConditionAndActionPortal {

		/** messages for different rejection reasons */
		private final Map<ChatCondition, List<String>> rejections;

		/** message for when player is pushed into training area by another player */
		private final String pushMessage = "Hey %s! Don't push!";

		/** determines if entity was pushed onto portal */
		private boolean wasPushed = false;
		private RPEntity pusher = null;


		public ArcheryRangeConditionAndActionPortal() {
			super(null, null);
			Area area = new Area(SingletonRepository.getRPWorld().getZone(archeryZoneID), archeryArea);

			rejections = new LinkedHashMap<>();
			rejections.put(
					new QuestInStateCondition(QUEST_SLOT, 0, STATE_ACTIVE),
					Arrays.asList(
							"Hey %s! You can't just walk into the archery range for free.",
							pushMessage));
			rejections.put(
					new NotCondition(new AreaIsFullCondition(area, MAX_OCCUPANTS)),
					Arrays.asList(
							FULL_MESSAGE,
							pushMessage));

			setPosition(PORTAL_POS.x, PORTAL_POS.y);
			setIgnoreNoDestination(true);
			setResistance(0);
			setForceStop(true);
		}

		private String formatMessage(String message, final RPEntity user) {
			return String.format(message, user.getName());
		}

		/**
		 * Checks the list of conditions & sets the rejection message text.
		 */
		@Override
		protected boolean isAllowed(final RPEntity user) {
			int msgIndex = 0;
			RPEntity msgTarget = user;
			if (wasPushed && pusher != null) {
				msgIndex = 1;
				msgTarget = pusher;
			}

			final Sentence sentence = ConversationParser.parse(user.get("text"));
			for (final ChatCondition cond : rejections.keySet()) {
				if (!cond.fire((Player) user, sentence, this)) {
					setRejectedAction(new MultipleActions(
							new TeleportAction(archeryZoneID, 117, 104, null),
							new SayTextAction(formatMessage(rejections.get(cond).get(msgIndex), msgTarget))));
					return false;
				}
			}

			return true;
		}

		@Override
		public boolean onUsed(final RPEntity user) {
			boolean res = false;

			// don't worry about players trying to leave
			final Direction dir = user.getDirectionToward(this);
			if (dir == Direction.LEFT) {
				res = super.onUsed(user);
			}

			return res;
		}

		/**
		 * Check access for players pushed onto portal.
		 */
		@Override
		public void onPushedOntoFrom(final RPEntity pushed, final RPEntity pusher, final Point prevPos) {
			wasPushed = true;
			if (pusher != null) {
				this.pusher = pusher;
			}

			// check if entity is being pushed from the right
			if (prevPos.x == getX() + 1) {
				super.onUsed(pushed);
			}

			// reset pushed status
			wasPushed = false;
			this.pusher = null;
		}

		/**
		 * Override to avoid java.lang.NullPointerException.
		 */
		@Override
		protected void rejected(final RPEntity user) {
			if (user instanceof Player) {
				final Player player = (Player) user;

				if (rejectedAction != null) {
					rejectedAction.fire(player, ConversationParser.parse(user.get("text")), new EventRaiser(npc));
				}

				if (forceStop) {
					player.forceStop();
					return;
				}
			}

			user.stop();
		}
	}
}
