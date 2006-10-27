package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.item.Token;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.OnePlayerRoomDoor;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.EntityManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * A quest where the player has to invert an arrow build out of stones
 * by moving only up to 3 tokens.
 *
 * @author hendrik
 */
// TODO: split this class, it does to many different things
public class ReverseArrow extends AbstractQuest implements Token.TokenMoveListener {

	// constants
	private static final String QUEST_SLOT = "reverse_arrow";
	private static final String ZONE_NAME = "int_ados_reverse_arrow";
	private static final int TIME = 60;
	private static final int MAX_MOVES = 3;
	private static final int OFFSET_X = 15;
	private static final int OFFSET_Y = 10;

	// "static" data
	protected StendhalRPZone zone = null;
	protected SpeakerNPC npc = null;
	protected List<Token> tokens = null;
	private OnePlayerRoomDoor door = null;
	private StendhalRPZone entranceZone = null;

	// quest instance data
	private int moveCount = 0;
	protected Player player = null;
	private Timer timer = null;

	/**
	 * Checks the result
	 */
	protected class ReverseArrowCheck implements TurnListener {

		/**
		 * Is the task solved?
		 *
		 * @return true on success, false on failure
		 */
		private boolean checkBoard() {
			// We check the complete arrow (and not just the three moved
			// tokens) here for two reasons:

			// 1. there are 6 permutions so the code would quite messy
			// 2. there may be a solution i did not recognize

			// This aproach has the side effect that the code does not
			// tell the solution :-)

			// sort the tokens according to their position
			Collections.sort(tokens, new Comparator<Token>() {
				public int compare(Token t1, Token t2) {
					int d = t1.getY() - t2.getY();
					if (d == 0) {
						d = t1.getX() - t2.getX();
					}
					return d;
				}
			});
			//     0
			//   1 2 3 
			// 4 5 6 7 8

			// get the position of the topmost token
			int topX = tokens.get(0).getX();
			int topY = tokens.get(0).getY();

			// check first row
			for (int i = 1; i <= 3; i++) {
				Token token = tokens.get(i);
				if ((token.getX() != topX - 1 + (i - 1)) || (token.getY() != topY + 1)) {
					return false;
				}
			}

			// check second row
			for (int i = 4; i <= 8; i++) {
				Token token = tokens.get(i);
				if ((token.getX() != topX - 2 + (i - 4)) || (token.getY() != topY + 2)) {
					return false;
				}
			}

			return true;
		}

		/**
		 * invoked shortly after the player did his/her third move.
		 */
		public void onTurnReached(int currentTurn, String message) {
			if (checkBoard()) {
				if (!player.isQuestCompleted(QUEST_SLOT)) {
					npc.say("Congratulations you solved the quizz");
					// TODO: give reward
				} else {
					npc.say("Congratulations you solved the quizz again. I'm afraid there is only a reward on the first time.");
				}
				player.setQuest(QUEST_SLOT, "done");
			} else {
				player.setQuest(QUEST_SLOT, "failed");
				npc.say("I am sorry. This does not look like an arrow pointing upwards to me.");
			}

			// teleport the player out
			TurnNotifier.get().notifyInTurns(6, new FinishNotifier(true, player), null); // 2 seconds
		}
	}

	/**
	 * Teleports the player out
	 */
	protected class FinishNotifier implements TurnListener {
		private boolean reset; 
		private Player player;
		public FinishNotifier(boolean reset, Player player) {
			this.player = player;
			this.reset = reset;
		}
		/**
		 * invoked shortly after the player did his job.
		 */
		public void onTurnReached(int currentTurn, String message) {
			finish(reset, player);
		}
	}

	/**
	 * Tells the player the remaining time and teleports him out if the
	 * task is not completed in time.
	 */
	class Timer implements TurnListener {
		private Player timerPlayer;
		/**
		 * Starts a teleport-out-timer
		 *
		 * @param player the player who started the timer
		 */
		protected Timer(Player player) {
			timerPlayer = player;
		}
		private int counter = TIME;
		public void onTurnReached(int currentTurn, String message) {
			// check that the player is still in game and stop the timer
			// in case the player is not playing anymore.
			// Note that "player" always refers to the current player
			// in order not to teleport the next player out to early,
			// we have to compare it to the player who started this timer
			if ((player == timerPlayer) && (player != null)) {
				IRPZone playerZone = StendhalRPWorld.get().getRPZone(player.getID());
				if (playerZone.equals(zone)) {
					if (counter > 0) {
						npc.say("You have " + counter + " seconds left.");
						counter = counter - 10;
						TurnNotifier.get().notifyInTurns(10*3, this, null);
					} else {
						// teleport the player out
						npc.say("Sorry, your time is up.");
						TurnNotifier.get().notifyInTurns(1, new FinishNotifier(true, player), null); // need to do this on the next turn
					}
				}
			}
		}
	}

	/**
	 * Notifies this script on sucessful usage
	 */
	class NotifingDoor extends OnePlayerRoomDoor {
		NotifingDoor(String clazz, Direction dir) {
			super(clazz, dir);
		}

		@Override
		public void onUsed(RPEntity user) {
			super.onUsed(user);
			start((Player) user);
		}

		@Override
		public void onUsedBackwards(RPEntity user) {
			super.onUsedBackwards(user);
			finish(true, (Player) user);
		}
	}
	
	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	/**
	 * creates a token and adds it to the world
	 *
	 * @param x x-position
	 * @param y y-position
	 */
	private void addTokenToWorld(int x, int y) {
		EntityManager entityManager = StendhalRPWorld.get().getRuleManager().getEntityManager();
		Token token = (Token) entityManager.getItem("token");
		zone.assignRPObjectID(token);
		token.set(x, y);
		token.put("persistent", 1);
		token.setTokenMoveListener(this);
		zone.add(token);
		tokens.add(token);
	}

	/**
	 * adds the tokens to the game field
	 */
	private void addAllTokens() {
		// 0 1 2 3 4
		//   5 6 7
		//     8
		tokens = new LinkedList<Token>();
		for (int i = 0; i < 5; i++) {
			addTokenToWorld(OFFSET_X + i, OFFSET_Y);
		}
		for (int i = 1; i < 4; i++) {
			addTokenToWorld(OFFSET_X + i, OFFSET_Y + 1);
		}
		addTokenToWorld(OFFSET_X + 2, OFFSET_Y + 2);
	}

	/**
	 * removes all tokens (called after the player messed them up)
	 */
	private void removeAllTokens() {
		if (tokens != null) {
			for (Token token : tokens) {
				if (token != null) {
					zone.remove(token.getID());
				}
			}
		}
		tokens = null;
	}

	private void step_1() {
		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(ZONE_NAME));
		step1CreateNPC();
		step1CreateDoors();
	}

	private void step1CreateNPC() {
		npc = new SpeakerNPC("Gamblos") {
			@Override
			protected void createPath() {
				// NPC doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("Hi, welcome to our small game. Your task is to let this arrow point upwards by moving up to three tokens.");
						} else {
							engine.say("Hi again " + player.getName() + ". I rembemer that you solved this problem already. You can do it again, of course.");
						}
					}
				});
				addHelp("You have to stand next to a token in order to move it.");
				addJob("I am the supervisor for this task.");
				addGoodbye("It was nice to meet you.");
				addQuest("Your task in this game is to revert the direction of this arrow moving only 3 tokens within " + TIME + " seconds.");
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldwizardnpc"); // TODO change outfit
		npc.set(20, 7);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc); 
	}
	
	private void step1CreateDoors() {
		// 0_semos_mountain_n2 at (95,101)
		String entranceZoneName = "0_semos_mountain_n2"; 
		entranceZone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(entranceZoneName));
		door = new NotifingDoor("housedoor", Direction.DOWN);
		entranceZone.assignRPObjectID(door);
		door.setX(95);
		door.setY(101);
		door.setNumber(0);
		door.setDestination(ZONE_NAME, 0);
		door.open();
		entranceZone.addPortal(door);

		Portal exit = new Portal();
		zone.assignRPObjectID(exit);
		exit.setX(17);
		exit.setY(20);
		exit.setNumber(0);
		exit.setDestination(entranceZoneName, 0);
		zone.addPortal(exit);

		Sign sign = new Sign();
		entranceZone.assignRPObjectID(sign);
		sign.setX(96);
		sign.setY(102);
		sign.setText("If the door is closed, you will have to wait a short time until the last player finishes his task.");
		entranceZone.add(sign);		
	}

	@Override
	public void convertOnUpdate(Player player) {
		super.convertOnUpdate(player);
		// need to do this on the next turn
		TurnNotifier.get().notifyInTurns(1, new FinishNotifier(false, player), null);
	}

	/**
	 * The player moved a token
	 *
	 * @param player Player
	 */
	public void onTokenMoved(Player player) {
		moveCount++;
		if (moveCount < MAX_MOVES) {
			npc.say("This was your " + Grammar.ordered(moveCount) + " move.");
		} else {
			npc.say("This was your " + Grammar.ordered(moveCount) + " and final move. Let me check your work.");
			TurnNotifier.get().notifyInTurns(6, new ReverseArrowCheck(), null); // 2 seconds
			if (timer != null) {
				TurnNotifier.get().dontNotify(timer, null);
			}
		}
	}

	/**
	 * A player entered the zone
	 *
	 * @param player Player
	 */
	public void start(Player player) {
		IRPZone playerZone = StendhalRPWorld.get().getRPZone(player.getID());
		if (playerZone.equals(zone)) {
			this.player = player;
			removeAllTokens();
			addAllTokens();
			timer = new Timer(player);
			TurnNotifier.get().notifyInTurns(0, timer, null);
			moveCount = 0;
		}
	}

	/**
	 * Finishes the quest and teleports the player out.
	 *
	 * @param reset  reset it for the next player (set to false on login)
	 * @param player the player to teleport out
	 */
	protected void finish(boolean reset, Player player) {
		if (player != null) {
			IRPZone playerZone = StendhalRPWorld.get().getRPZone(player.getID());
			if (playerZone.equals(zone)) {
				player.teleport(entranceZone, door.getX(), door.getY() + 1, Direction.DOWN, player);
			}
		}
		if (reset) {
			removeAllTokens();
			player = null;
			moveCount = 0;
			if (timer != null) {
				TurnNotifier.get().dontNotify(timer, null);
			}
			door.open();
		}
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
	}
}
