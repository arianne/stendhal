package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.lang.ref.WeakReference;

import marauroa.common.game.IRPZone;

/**
 * Controls player access to the Wizard's Bank via an NPC. 
 * <p>He takes a fee to enter. Players are allowed only 5 minutes access at once.
 * 
 * @author kymara
 */

public class WizardBank extends AbstractQuest implements LoginListener {

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
	protected StendhalRPZone zone;

	protected SpeakerNPC npc;

	// quest instance data
	protected Player player;

	/**
	 * Tells the player the remaining time and teleports him out when his time
	 * is up.
	 */
	class Timer implements TurnListener {
		private WeakReference<Player> timerPlayer;

		/**
		 * Starts a teleport-out-timer.
		 * 
		 * @param player
		 *            the player who started the timer
		 */
		protected Timer(Player player) {
			timerPlayer = new WeakReference<Player>(player);
		}

		private int counter = TIME;

		// override equals

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Timer) {
				Timer newTim = (Timer) obj;
				return timerPlayer.get() == newTim.timerPlayer.get();
			} else {
				return false;
			}
		}

		// override hash

		@Override
		public int hashCode() {
			// TODO: This is broken. If player gets GC'd, then
			// hash code changes. (not good)
			Player playerTemp = timerPlayer.get();

			if (playerTemp != null) {
				return playerTemp.hashCode();
			} else {
				return 0;
			}
		}

		public void onTurnReached(int currentTurn) {
			// check that the player is still in game and stop the timer
			// in case the player is not playing anymore.
			// Note that "player" always refers to the current player
			// in order not to teleport the next player out too early,
			// we have to compare it to the player who started this timer

			Player playerTemp = timerPlayer.get();

			if (playerTemp != null) {
				IRPZone playerZone = playerTemp.getZone();

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
	public void init(String name) {
		super.init(name, QUEST_SLOT);
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
				addGreeting(null, new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC engine) {
						if (player.isQuestCompleted(GRAFINDLE_QUEST_SLOT)
								&& player.isQuestCompleted(ZARA_QUEST_SLOT)) {
							String reply;
							if (player.isQuestCompleted(QUEST_SLOT)) {
								reply = " Do you wish to pay to access your chest again?";
							} else if (!player.hasQuest(QUEST_SLOT)) {
								reply = "";
							} else {
								reply = " You may #leave sooner, if required.";
							}
							engine.say("Welcome to the Wizard's Bank, "
									+ player.getTitle() + "." + reply);
						} else {
							engine.say("You may not use this bank if you have not gained the right to use the chests at Nalwor, nor if you have not earned the trust of a certain young woman. Goodbye!");
							engine.setCurrentState(ConversationStates.IDLE);
						}
					}
				});

				addReply("fee", null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						if (player.isQuestCompleted(QUEST_SLOT)
								|| !player.hasQuest(QUEST_SLOT)) {
							engine.say("The fee is " + COST
									+ " money. Do you want to pay?");
						} else {
							engine.say("As you already know, the fee is "
									+ COST + " money.");
						}
					}
				});

				addReply("yes", null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						if (player.isQuestCompleted(QUEST_SLOT)
								|| !player.hasQuest(QUEST_SLOT)) {
							if (player.drop("money", COST)) {
								engine.say("Semos, Nalwor and Fado bank chests are to my right. The chests owned by Ados Bank Merchants and your friend Zara are to my left. If you are finished before your time here is done, please say #leave.");

								player.teleport(zone, 10, 10, Direction.DOWN, player);

								SingletonRepository.getTurnNotifier().notifyInTurns(0, new Timer(player));

								player.setQuest(QUEST_SLOT, "start");
							} else {
								engine.say("You do not have enough money!");
							}
						} else {
							engine.say("Hm, I do not understand you. If you wish to #leave, just say");
						}
					}
				});

				addReply("no", null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						if (player.isQuestCompleted(QUEST_SLOT)
								|| !player.hasQuest(QUEST_SLOT)) {
							engine.say("Very well.");
						} else {
							engine.say("Hm, I do not understand you. If you wish to #leave, just say");
						}
					}
				});

				addReply("leave", null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						if (!player.isQuestCompleted(QUEST_SLOT)) {
							teleportAway(player);
							// remove the players Timer
							SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));
							engine.say("Thank you for using the Wizard's Bank");
						} else {
							engine.say("Leave where?");
						}
					}
				});

				addJob("I control access to the bank. My spells ensure people cannot simply come and go as they please. We charge a #fee.");

				addReply("magic",
						"Have you not heard of magic? It is what makes the grass grow here. Perhaps in time your kind will learn how to use this fine art.");

				addOffer("I would have thought that the offer of these #fiscal services is enough for you.");

				addReply("fiscal",
						"You do not understand the meaning of the word? You should spend more time in libraries, I hear the one in Ados is excellent.");

				addHelp("This bank is suffused with #magic, and as such you may access any vault you own. There will be a #fee to pay for this privilege, as we are not a charity.");

				addQuest("You may only use this bank if you have gained the right to use the chests at Nalwor, and if you have earned the trust of a young woman.");

				addGoodbye("Goodbye.");
			}
		};

		npc.setDescription("You see a wizard who you should be afraid to mess with.");
		npc.setEntityClass("brownwizardnpc");
		npc.setPosition(15, 10);
		npc.initHP(100);
		zone.add(npc);
	}

	// kym says: this was in reverse arrow. don't know what it's for.
	public void onLoggedIn(Player player) {
		teleportAway(player);
	}

	/**
	 * Finishes the time and teleports the player out.
	 * 
	 * @param player
	 *            the player to teleport out
	 */
	void teleportAway(Player player) {
		if (player != null) {
			IRPZone playerZone = player.getZone();
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
		super.addToWorld();

		SingletonRepository.getLoginNotifier().addListener(this);

		zone = SingletonRepository.getRPWorld().getZone(ZONE_NAME);
		createNPC();
	}
}
