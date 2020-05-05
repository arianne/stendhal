/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.LogoutListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.game.IRPZone;

/**
 * Controls player access to the Wizard's Bank via an NPC.
 * <p>He takes a fee to enter. Players are allowed only 5 minutes access at once.
 *
 * @author kymara
 */

public class WizardBank extends AbstractQuest implements LoginListener,LogoutListener {

	// constants
	private static final String QUEST_SLOT = "wizard_bank";

	private static final String GRAFINDLE_QUEST_SLOT = "grafindle_gold";

	private static final String ZARA_QUEST_SLOT = "suntan_cream_zara";

	private static final String ZONE_NAME = "int_magic_bank";

	/** Time (in Seconds) allowed in the bank. */
	private static final int TIME = 60 * 5;

	// Cost to access chests
	private static final int COST = 1000;

	// "static" data
	private StendhalRPZone zone = null;

	private SpeakerNPC npc;

	/**
	 * Tells the player the remaining time and teleports him out when his time
	 * is up.
	 */
	class Timer implements TurnListener {
		private final WeakReference<Player> timerPlayer;
		/**
		 * Using unique playername for hashcode.
		 */
		private final String playername;
		/**
		 * Starts a teleport-out-timer.
		 *
		 * @param player
		 *            the player who started the timer
		 */
		protected Timer(final Player player) {
			timerPlayer = new WeakReference<Player>(player);
			playername = player.getName();
		}

		private int counter = TIME;

		// override equals

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;

			if (playername == null) {
				return prime * result;
			} else {
				return prime * result + playername.hashCode();
			}

		}

		@Override
		public boolean equals(final Object obj) {
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

			if (playername == null) {
				if (other.playername != null) {
					return false;
				}
			} else if (!playername.equals(other.playername)) {
				return false;
			}
			return true;
		}

		// override hash



		@Override
		public void onTurnReached(final int currentTurn) {
			// check that the player is still in game and stop the timer
			// in case the player is not playing anymore.
			// Note that "player" always refers to the current player
			// in order not to teleport the next player out too early,
			// we have to compare it to the player who started this timer

			final Player playerTemp = timerPlayer.get();

			if (playerTemp != null) {
				final IRPZone playerZone = playerTemp.getZone();

				if (playerZone.equals(zone)) {
					if (counter > 0) {
						npc.say(playerTemp.getTitle() + ", you have "
								+ TimeUtil.timeUntil(counter) + " left.");
						counter = counter - 10 * 6;
						SingletonRepository.getTurnNotifier().notifyInTurns(10 * 3 * 6, this);
					} else {
						// teleport the player out
						npc.say("Sorry, " + playerTemp.getTitle()
								+ ", your time here is up.");
						teleportAway(playerTemp);
					}
				}
			}
		}
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void createNPC() {

		npc = new SpeakerNPC("Javier X") {
			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {

				// has been here before
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new AndCondition(new GreetingMatchesNameCondition(super.getName()),
								new QuestCompletedCondition(GRAFINDLE_QUEST_SLOT),
								new QuestCompletedCondition(ZARA_QUEST_SLOT),
								new QuestCompletedCondition(QUEST_SLOT)),
					    ConversationStates.ATTENDING,
					    null,
					    new SayTextAction("Welcome to the Wizard's Bank, [name]. Do you wish to pay to access your chest again?"));

				// never started quest
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new AndCondition(new GreetingMatchesNameCondition(super.getName()),
								new QuestCompletedCondition(GRAFINDLE_QUEST_SLOT),
								new QuestCompletedCondition(ZARA_QUEST_SLOT),
								new QuestNotStartedCondition(QUEST_SLOT)),
					    ConversationStates.ATTENDING,
					    null,
					    new SayTextAction("Welcome to the Wizard's Bank, [name]."));

				// currently in bank
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new AndCondition(new GreetingMatchesNameCondition(super.getName()),
								new QuestCompletedCondition(GRAFINDLE_QUEST_SLOT),
								new QuestCompletedCondition(ZARA_QUEST_SLOT),
								new QuestActiveCondition(QUEST_SLOT)),
					    ConversationStates.ATTENDING,
					    null,
					    new SayTextAction("Welcome to the Wizard's Bank, [name]. You may #leave sooner, if required."));

				// hasn't got access to all banks yet
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new AndCondition(new GreetingMatchesNameCondition(super.getName()),
							new OrCondition(
									new QuestNotCompletedCondition(GRAFINDLE_QUEST_SLOT),
									new QuestNotCompletedCondition(ZARA_QUEST_SLOT))),
						ConversationStates.IDLE,
						"You may not use this bank if you have not gained the right to use the chests at Nalwor, nor if you have not earned the trust of a certain young woman. Goodbye!",
						null);

				add(ConversationStates.ATTENDING,
						Arrays.asList("fee", "enter"),
						new QuestNotActiveCondition(QUEST_SLOT),
						ConversationStates.ATTENDING,
						"The fee is " + COST
						+ " money. Do you want to pay?",
						null);

				add(ConversationStates.ATTENDING,
						Arrays.asList("fee", "enter"),
						new QuestActiveCondition(QUEST_SLOT),
						ConversationStates.ATTENDING,
						"As you already know, the fee is "
						+ COST + " money.",
						null);

				add(ConversationStates.ATTENDING,
						ConversationPhrases.YES_MESSAGES,
						new AndCondition(
								new PlayerHasItemWithHimCondition("money", COST),
								new QuestNotActiveCondition(QUEST_SLOT)),
								ConversationStates.ATTENDING,
								"Semos, Nalwor and Fado bank chests are to my right. The chests owned by Ados Bank Merchants and your friend Zara are to my left. If you are finished before your time here is done, please say #leave.",
								new MultipleActions(
										new DropItemAction("money", COST),
										new TeleportAction(ZONE_NAME, 10, 10, Direction.DOWN),
										new SetQuestAction(QUEST_SLOT, "start"),
										new ChatAction() {
											@Override
											public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
												SingletonRepository.getTurnNotifier().notifyInTurns(0, new Timer(player));
											}}));

				add(ConversationStates.ATTENDING,
						ConversationPhrases.YES_MESSAGES,
						new AndCondition(
								new NotCondition(new PlayerHasItemWithHimCondition("money", COST)),
								new QuestNotActiveCondition(QUEST_SLOT)),
						ConversationStates.ATTENDING,
						"You do not have enough money!",
						null);

				add(ConversationStates.ATTENDING,
						ConversationPhrases.YES_MESSAGES,
						new QuestActiveCondition(QUEST_SLOT),
						ConversationStates.ATTENDING,
						"Hm, I do not understand you. If you wish to #leave, just say",
						null);

				add(ConversationStates.ATTENDING,
						ConversationPhrases.NO_MESSAGES,
						new QuestNotActiveCondition(QUEST_SLOT),
						ConversationStates.ATTENDING,
						"Very well.",
						null);

				add(ConversationStates.ATTENDING,
						ConversationPhrases.NO_MESSAGES,
						new QuestActiveCondition(QUEST_SLOT),
						ConversationStates.ATTENDING,
						"Hm, I do not understand you. If you wish to #leave, just say",
						null);

				add(ConversationStates.ATTENDING,
						"leave",
						new QuestNotActiveCondition(QUEST_SLOT),
						ConversationStates.ATTENDING,
						"Leave where?",
						null);


				add(ConversationStates.ATTENDING,
						"leave",
						new QuestActiveCondition(QUEST_SLOT),
						ConversationStates.ATTENDING,
						"Thank you for using the Wizard's Bank",
						// we used to use teleportAway() here
						new MultipleActions(
								new TeleportAction(ZONE_NAME, 15, 16, Direction.DOWN),
								new SetQuestAction(QUEST_SLOT, "done"),
								new ChatAction() {
									@Override
									public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
										SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));
									}}));


				addJob("I control access to the bank. My spells ensure people cannot simply come and go as they please. We charge a #fee to #enter.");

				addReply("magic",
						"Have you not heard of magic? It is what makes the grass grow here. Perhaps in time your kind will learn how to use this fine art.");

				addOffer("I would have thought that the offer of these #fiscal services is enough for you.");

				addReply("fiscal",
						"You do not understand the meaning of the word? You should spend more time in libraries, I hear the one in Ados is excellent. Anyhow, to #enter the bank just ask.");

				addHelp("This bank is suffused with #magic, and as such you may access any vault you own. There will be a #fee to pay for this privilege, as we are not a charity.");

				addQuest("To #enter this bank you need only ask.");

				addGoodbye("Goodbye.");
			}
		};

		npc.setDescription("You see a wizard who you should be afraid to mess with.");
		npc.setEntityClass("brownwizardnpc");
		npc.setPosition(15, 10);
		npc.initHP(100);
		zone.add(npc);
	}

	@Override
	public void onLoggedIn(final Player player) {
		/*
		 *  Stop any possible running notifiers that might be left after the player
		 *  logged out while in the bank. Otherwise the player could be thrown out
		 *  too early if he goes back.
		 */
		SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));
		teleportAway(player);
	}

	@Override
	public void onLoggedOut(final Player player) {
		// FIXME: this only works if the player logs out correctly & not if player is disconnected
		SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));
	}

	/**
	 * Finishes the time and teleports the player out.
	 *
	 * @param player
	 *            the player to teleport out
	 */
	private void teleportAway(final Player player) {
		if (player != null) {
			final IRPZone playerZone = player.getZone();
			if (playerZone.equals(zone)) {
				player.teleport(zone, 15, 16, Direction.DOWN, player);

				// complete the quest if it already started
				if (player.hasQuest(QUEST_SLOT)) {
					player.setQuest(QUEST_SLOT, "done");
				}
			}
		}
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"The Wizard Bank",
				"At the Wizard Bank, one can access many magical chests at once.",
				false);

		SingletonRepository.getLoginNotifier().addListener(this);
		SingletonRepository.getLogoutNotifier().addListener(this);

		zone = SingletonRepository.getRPWorld().getZone(ZONE_NAME);
		createNPC();
	}

	@Override
	public String getName() {
		return "WizardBank";
	}

	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}

	@Override
	public List<String> getHistory(final Player player) {
		return new ArrayList<String>();
	}

	@Override
	public String getNPCName() {
		return "Javier X";
	}

	@Override
	public String getRegion() {
		return Region.FADO_CAVES;
	}
}
