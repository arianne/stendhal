package games.stendhal.server.maps.nalwor.hell;

import java.util.IdentityHashMap;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.config.annotations.ServerModeUtil;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.ZoneEnterExitListener;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.GlobalVisualEffectEvent;
import marauroa.common.game.RPObject;

/**
 * Handles moving the player from hell to the pit, at irrwegular intervals.
 * The slot has form "last_kickout_time;status", where the kickout time is used
 * to decide if the player should be kicked sooner than normally. The status
 * indicates if the player has been noticed, and can be used to kick the player
 * out immediately.
 */
public class HellTimer implements ZoneConfigurator, ZoneEnterExitListener {
	private static final String QUEST_SLOT = "hell_timer";
	/**
	 * The mean time player may normally stay in hell (excluding grace time).
	 * The actual time is random.
	 */
	private static final int MEAN_WAIT_TIME = MathHelper.SECONDS_IN_ONE_HOUR;
	/**
	 * The mean time player may stay in hell (excluding grace time) when they
	 * have been caught recently. The actual time is random.
	 */
	private static final int SHORT_WAIT_TIME = 3 * MathHelper.SECONDS_IN_ONE_MINUTE;
	/**
	 * The time in minutes that the player should stay away from hell until the
	 * guardian has forgotten about them.
	 */
	private static final int GUARDIAN_WARNED_TIME = 6 * MathHelper.MINUTES_IN_ONE_HOUR;
	/**
	 * The time how long a caught state is considered valid. The player can log
	 * out before being moved to the pit. Normally they'd be moved immediately
	 * to the pit on login, but server releases can move players out of the
	 * hell. So the state expires to avoid the situation where a player enters
	 * the hell through the chasm but is immediately moved to the Pit - possibly
	 * after not even having played for a long time.
	 */
	private static final int CAUGHT_EXPIRE_TIME = 5 * GUARDIAN_WARNED_TIME;
	/**
	 * The player can stay in hell during the grace time, so that they can pick
	 * up items left on the ground etc. During the grace time the player is
	 * sent messages about what is happening at specified intervals.
	 * The full grace time is 3 * GRACE_TIME_INTERVAL in seconds.
	 */
	private static final int GRACE_TIME_INTERVAL = 5;
	private static final int LAWYER_FEE = 10000;
	private static final String STD_MSG = "An irresistible force drags you to the Pit";
	private static final String LAWYER_MSG = "The lawyer sends you to the reapers.";
	private static final String[][] MESSAGES = {
		{ "Hell's guardian tells you: Hey, what's a live soul doing in hell?"
			+ " You'll need to answer to the reapers.", STD_MSG },
		{ "Fire brander tells you: Don't try to fool me, I won't brand live"
			+ " skin. The color would be all wrong, and rebranding can't be"
			+ " done correctly every day, like with proper, dead soul. You'll"
			+ " get no branding from me! And don't come back until you're"
			+ " dead!", STD_MSG },
		{ "Flagellator tells you: *slash*. Hmm, what a weird scream. I don't"
			+ " think you belong here. Try to explain that to the bosses!",
			STD_MSG },
		{ "Master skinner tells you: Ah, that will make a nice tanned hide..."
			+ " yikes, that thing underneath is alive! Get away! I have no use"
			+ " for fur animals that can't regrow their skin!", STD_MSG },
		{ "Apprentice nail puller tells you: *tug*. Huh, that was loose, and it"
			+ " seems to grow back awfully slow. Sorry, but something must be"
			+ " wrong with your nails, I'll send you to my superiors for"
			+ " checking.", STD_MSG },
		{ "Soul cooker tells you: Oh, you are not ripe enough for cooking. I'll"
			+ " need to report you for the reapers. But don't be sad. *pat* "
			+ "*pat*. In just a few years you'll be dead, and then we can cook"
			+ " you every day! I'll promise to make the oil extra hot for you"
			+ " then.", STD_MSG },
		// The contract also says: "In case you do not wish to agree to this
		// contract, fall back up immediately".
		{ "Lawyer tells you: As per the contract you agreed to by falling down"
			+ " the chasm, it's strictly forbidden to stay in hell while alive."
			+ " You can remedy that by forfeiting your live status. However, as"
			+ " your legal counsel, I advice that     it would be more advantageous"
			+ " for ... you take the matter to the reapers for arbitration."
			+ " Thank you for paying my fee of $FEE money! I recommend you to"
			+ " contact me again in case you visit the hell while alive again. "
			+ "Farewell, it's been pleasant to do business with you!",
			LAWYER_MSG },
		{ "Hell's janitor tells you: Get away from here! I have enough work with"
			+ " just demons and the dead making a mess.", STD_MSG },
		{ "Hell's accountant tells you: You are not properly registered as a"
			+ " resident here. Here, fill forms F42 and H6 to apply for the"
			+ " necessary paperwork for applying the permission to register as"
			+ " a candidate to request a permission for applying for"
			+ " residency.", "You volunteer to jump to the Pit instead." },
		{ "Hell's marketeer tells you: Hello friend! I have a wonderful offer"
			+ " only for you, $NAME! Here, look at these  brochures about"
			+ " spending your afterlife in Hell while I tell about the unique"
			+ " opportunity, $NAME, that we have reserved personally for you."
			+ " The masses of slaughtered innocents makes you eligible for our"
			+ " platinum program, and on top of that we have arranged"
			+ " personally for you...", "You jump to the pit to escape the"
			+ " salesman." },
		{ "Hell's teacher tells you: $NAME! I saw you! What do you think,"
		    + " you are doing out here? Go home immedietaly! And I want you to"
		    + " write 'I will complete my homework before playing online games'."
		    + " A hundred times. In your best handwriting. Until Monday."
		    + " Have it signed by your parents. When you are done, you will write"
		    + " a thousand word essay on 'The hell is no place for the living'.",
		    "The teacher turns around fetching a cane and you decide it's"
		    + " better to start running to the pit."
		}
	};
	private static final String PIT_ZONE_NAME = "int_hell_pit";

	Map<Player, TurnListener> runningTimers = new IdentityHashMap<>();

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		zone.addZoneEnterExitListener(this);
	}

	@Override
	public void onEntered(RPObject object, StendhalRPZone zone) {
		if (object instanceof Player) {
			Player player = (Player) object;
			// Disable moving admins out, except on the test server
			if (player.getAdminLevel() >= 1000 && !ServerModeUtil.isTestServer()) {
				return;
			}

			TurnListener timer;
			int seconds;
			if (new QuestInStateCondition(QUEST_SLOT, 1, "caught").fire(player, null, null)
					&& !new TimePassedCondition(QUEST_SLOT, 0, CAUGHT_EXPIRE_TIME).fire(player, null, null)) {
				// They are still considered caught.
				timer = new FinalTimer(player);
				seconds = 0;
			} else if (new TimePassedCondition(QUEST_SLOT, 0, GUARDIAN_WARNED_TIME).fire(player, null, null)) {
				timer = new TimerStage1(player, false);
				seconds = Rand.randExponential(MEAN_WAIT_TIME);
			} else {
				timer = new TimerStage1(player, true);
				player.sendPrivateText("Since you were recently caught without "
						+ "permission in hell, the guardians may be prepared "
						+ "for your return");
				seconds = Rand.randExponential(SHORT_WAIT_TIME);
			}
			runningTimers.put(player, timer);
			SingletonRepository.getTurnNotifier().notifyInSeconds(seconds, timer);
		}
	}

	@Override
	public void onExited(RPObject object, StendhalRPZone zone) {
		if (object instanceof Player) {
			TurnListener listener = runningTimers.get(object);
			if (listener != null) {
				SingletonRepository.getTurnNotifier().dontNotify(listener);
				runningTimers.remove(object);
			}
		}
	}

	private class TimerStage1 implements TurnListener {
		private final Player player;
		private final boolean recaught;

		TimerStage1(Player player, boolean recaught) {
			this.player = player;
			this.recaught = recaught;
		}

		@Override
		public void onTurnReached(int currentTurn) {
			player.sendPrivateText(NotificationType.SCENE_SETTING, "An infernal official has noticed you and approaches.");
			// The state is set immediately so that the player can't avoid
			// being kicked out by logging out when they get the message
			new SetQuestToTimeStampAction(QUEST_SLOT, 0).fire(player, null, null);
			new SetQuestAction(QUEST_SLOT, 1, "caught").fire(player, null, null);
			TurnListener timer = new TimerStage2(player, recaught);
			runningTimers.put(player, timer);
			SingletonRepository.getTurnNotifier().notifyInSeconds(GRACE_TIME_INTERVAL, timer);
		}
	}

	/**
	 * Timer that sends the player the message from the official, and does any
	 * extra action.
	 */
	private class TimerStage2 implements TurnListener {
		private final Player player;
		private final boolean recaught;

		TimerStage2(Player player, boolean recaught) {
			this.player = player;
			this.recaught = recaught;
		}

		@Override
		public void onTurnReached(int currentTurn) {
			String[] msg;
			if (recaught) {
				msg = new String[]{"Hell's guardian tells you: Ha, caught you."
						+ " The reapers warned me you might try to sneak back here.",
						STD_MSG };
			} else {
				msg = Rand.rand(MESSAGES);
			}
			String message = msg[0].replaceAll("\\$NAME", player.getName());
			if (LAWYER_MSG.equals(msg[1])) {
				// In principle a player can logout before being deducted the
				// money. That's a feature, not a bug - that can be interpreted
				// as the player jumping to the pit as soon as noticing the
				// official, and not giving them chance to talk.
				int sum = 0;
				for (Item item : player.getAllEquipped("money")) {
					sum += ((StackableItem) item).getQuantity();
				}
				int fee = Math.min(sum, LAWYER_FEE);
				new DropItemAction("money", fee).fire(player, null, null);
				message = msg[0].replaceAll("\\$FEE", Integer.toString(fee));
			}
			player.sendPrivateText(message);
			TurnListener timer = new TimerStage3(player, msg[1]);
			runningTimers.put(player, timer);
			SingletonRepository.getTurnNotifier().notifyInSeconds(GRACE_TIME_INTERVAL, timer);
		}
	}

	/**
	 * Timer that sends the player the leaving message and starts blanking the
	 * screen.
	 */
	private class TimerStage3 implements TurnListener {
		private final Player player;
		private final String message;

		TimerStage3(Player player, String message) {
			this.player = player;
			this.message = message;
		}

		@Override
		public void onTurnReached(int currentTurn) {
			player.sendPrivateText(NotificationType.SCENE_SETTING, message);
			TurnListener timer = new FinalTimer(player);
			runningTimers.put(player, timer);
			SingletonRepository.getTurnNotifier().notifyInSeconds(GRACE_TIME_INTERVAL, timer);
			player.addEvent(new GlobalVisualEffectEvent("blacken", 1000 * GRACE_TIME_INTERVAL));
		}
	}

	/**
	 * The timer that finally sends the player to the pit.
	 */
	private class FinalTimer implements TurnListener {
		private final Player player;

		FinalTimer(Player player) {
			this.player = player;
		}

		@Override
		public void onTurnReached(int currentTurn) {
			StendhalRPZone pit = SingletonRepository.getRPWorld().getZone(PIT_ZONE_NAME);
			if (!player.teleport(pit, 7, 10, Direction.UP, null)) {
				// Failing is extremely unlikely, but schedule a retry anyway
				// if that happens.
				SingletonRepository.getTurnNotifier().notifyInSeconds(GRACE_TIME_INTERVAL, this);
			} else {
				// once properly moved, clear the caught state
				new SetQuestAction(QUEST_SLOT, 1, "").fire(player, null, null);
				// ...but renew the time stamp, so that players can't easily
				// avoid any ill effects from the moving by just logging out and
				// waiting a bit. Anyway, it's about guardians remembering the
				// person so the time being in the pit matters. On the other
				// hand a player that spends a long time *in* the pit does not
				// look like someone who'd get immediately back, so the reapers
				// don't warn the guardians. (So the time stamp is not set at
				// *leaving*).
				new SetQuestToTimeStampAction(QUEST_SLOT, 0).fire(player, null, null);
			}
		}
	}
}
