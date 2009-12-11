package games.stendhal.server.entity.npc;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.fsm.Transition;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.npc.parser.ExpressionMatcher;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This is a finite state machine that implements a chat system. See:
 * http://en.wikipedia.org/wiki/Finite_state_machine In fact, it is a
 * transducer.
 * States are denoted by the enum ConversationStates. Input is the text
 * that the player says to the SpeakerNPC. Output is the text that the
 * SpeakerNPC answers.
 *
 * See examples to understand how it works.
 *
 * RULES:
 *
 * State IDLE is both the start state and the state that will end the
 * conversation between the player and the SpeakerNPC.
 *
 * State ATTENDING is the state where only one player can talk to NPC
 * and where the prior talk doesn't matter.
 *
 * State ANY is a wildcard and is used to jump from any state whenever
 * the trigger is active. There are states that are reserved for
 * special behaviours and quests.
 *
 * Example how it works: First we need to create a message to greet the player
 * and attend it. We add a hi event:
 *
 * add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
 * ConversationStates.ATTENDING, "Welcome, player!", null)
 *
 * Once the NPC is in the IDLE state and hears the word "hi", it will say
 * "Welcome player!" and move to ATTENDING.
 *
 * Now let's add some options when player is in ATTENDING_STATE, like job,
 * offer, buy, sell, etc.
 *
 * add(ConversationStates.ATTENDING, ConversationPhrases.JOB_MESSAGES,
 * ConversationStates.ATTENDING, "I work as a part time example showman", null)
 *
 * add(ConversationStates.ATTENDING_STATE, "offer",
 * ConversationStates.ATTENDING_STATE, "I sell best quality swords", null)
 *
 * Ok, two new events: job and offer, they go from ATTENDING state to ATTENDING
 * state, because after reacting to "job" or "offer", the NPC can directly react
 * to one of these again.
 *
 * <pre>
 * add(ConversationStates.ATTENDING, "buy", ConversationStates.BUY_PRICE_OFFERED, null, new ChatAction() {
 *    public void fire(Player player, String text, SpeakerNPC npc) {
 *        int i = text.indexOf(" ");
 *        String item = text.substring(i + 1);
 *        if (item.equals("sword")) {
 *            npc.say(item + "costs 10 coins. Do you want to buy?");
 *        } else {
 *            npc.say("Sorry, I don't sell " + item + ".");
 *            npc.setActualState(ConversationStates.ATTENDING);
 *        }
 *    }
 * });
 * </pre>
 *
 * Now the hard part. We listen to "buy", so we need to process the text, and
 * for that we use the ChatAction class, we create a new class that will handle
 * the event. Also see that we move to a new state, BUY_PRICE_OFFERED. The
 * player is then replying to a question, so we only expect two possible
 * replies: yes or no.
 *
 * add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.YES_MESSAGES,
 * ConversationStates.ATTENDING, "Sorry, I changed my mind. I won't sell
 * anything.", null); // See SellerBehaviour.java for a working example.
 *
 * Whatever the reply is, return to ATTENDING state so we can listen to new
 * things.
 *
 * Finally we want to finish the conversation, so whatever state we are, we want
 * to finish a conversation with "Bye!".
 *
 * add(ConversationStates.ANY, ConversationPhrases.GOODBYE_MESSAGES,
 * ConversationStates.IDLE, "Bye!", null);
 *
 * We use the state ANY as a wildcard, so if the input text is "bye" the
 * transition happens, no matter in which state the FSM really is, with the
 * exception of the IDLE state.
 */
public class SpeakerNPC extends NPC {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(SpeakerNPC.class);

	private final Engine engine = new Engine(this);

	/**
	 * Determines how long a conversation can be paused before it will
	 * terminated by the NPC. Defaults to 30 seconds at 300 ms / turn.
	 */
	private long playerChatTimeout = 90;

	// Default wait message when NPC is busy
	private String waitMessage;

	// Default wait action when NPC is busy
	private ChatAction waitAction;

	// Default bye message when NPC stops chatting with the player
	private String goodbyeMessage;

	private ChatCondition initChatCondition;

	// Default initChat action when NPC stop chatting with the player
	private ChatAction initChatAction;

	/**
	 * Stores which turn was the last one at which a player spoke to this NPC.
	 * This is important to determine conversation timeout.
	 */
	private long lastMessageTurn;

	/**
	 * The player who is currently talking to the NPC, or null if the NPC is
	 * currently not taking part in a conversation.
	 */
	private Player attending;

	/**
	 * Creates a new SpeakerNPC.
	 *
	 * @param name
	 *            The NPC's name. Please note that names should be unique.
	 */
	public SpeakerNPC(final String name) {
		baseSpeed = 0.2;
		createPath();

		lastMessageTurn = 0;

		setName(name);
		createDialog();
		put("title_type", "npc");

		setSize(1, 1);
	}

	protected void createPath() {
		// sub classes can implement this method
	}

	protected void createDialog() {
		// sub classes can implement this method
	}

	/**
	 * Is called when the NPC stops chatting with a player. Override it if
	 * needed.
	 * @param player who has been talked to.
	 */
	protected void onGoodbye(final Player player) {
		// do nothing
	}

	/**
	 * Gets all players that have recently (this turn?) talked and are standing
	 * nearby the NPC. Nearby means that they are standing less than <i>range</i>
	 * squares away horizontally and less than <i>range</i> squares away
	 * vertically.
	 *
	 * Why is range a double, not an int? Maybe someone wanted to implement a
	 * circle instead of the rectangle we're having now. -- mort
	 * (DHerding@gmx.de)
	 *
	 * @param npc
	 * @param range
	 * @return A list of nearby players who have recently talked.
	 */
	private List<Player> getNearbyPlayersThatHaveSpoken(final NPC npc, final double range) {
		final int x = npc.getX();
		final int y = npc.getY();

		final List<Player> players = new LinkedList<Player>();

		for (final Player player : getZone().getPlayers()) {
			final int px = player.getX();
			final int py = player.getY();

			if (player.has("text") && (Math.abs(px - x) < range)
					&& (Math.abs(py - y) < range)) {
				players.add(player);
			}
		}

		return players;
	}

	/**
	 * Gets the player who is standing nearest to the NPC. Returns null if no
	 * player is standing nearby. Nearby means that they are standing less than
	 * <i>range</i> squares away horizontally and less than <i>range</i>
	 * squares away vertically. Note, however, that the Euclidian distance is
	 * used to compare which player is standing closest.
	 *
	 * @param range
	 * @return The nearest player, or null if no player is standing on the same
	 *         map.
	 */
	private Player getNearestPlayer(final double range) {
		final int x = getX();
		final int y = getY();

		Player nearest = null;

		int squaredDistanceOfNearestPlayer = Integer.MAX_VALUE;

		for (final Player player : getZone().getPlayers()) {
			final int px = player.getX();
			final int py = player.getY();

			if ((Math.abs(px - x) < range) && (Math.abs(py - y) < range)) {
				final int squaredDistanceOfThisPlayer = (px - x) * (px - x)
						+ (py - y) * (py - y);
				if (squaredDistanceOfThisPlayer < squaredDistanceOfNearestPlayer) {
					squaredDistanceOfNearestPlayer = squaredDistanceOfThisPlayer;
					nearest = player;
				}
			}
		}
		return nearest;
	}

	/**
	 * The player who is currently talking to the NPC, or null if the NPC is
	 * currently not taking part in a conversation.
	 *
	 * @return Player
	 */
	public Player getAttending() {
		return attending;
	}

	/**
	 * Sets the player to whom the NPC is currently listening. Note: You don't
	 * need to use this for most NPCs.
	 *
	 * @param player
	 *            the player with whom the NPC should be talking.
	 */
	public void setAttending(final Player player) {
		attending = player;

		if (player != null) {
			stop();
		} else {
			if (hasPath()) {
				setSpeed(getBaseSpeed());
			}
		}
	}

	@Override
	public void onDead(final Entity killer, final boolean remove) {
		
		heal();
		notifyWorldAboutChanges();
	}

	@Override
	protected void dropItemsOn(final Corpse corpse) {
		//they cant die
		logger.error("SpeakerNpc " + getName());
	}

	/**
	 * Sets the time a conversation can be paused before it will be terminated
	 * by the NPC.
	 *
	 * @param playerChatTimeout
	 *            the time, in turns
	 */
	public void setPlayerChatTimeout(final long playerChatTimeout) {
		this.playerChatTimeout = playerChatTimeout;
	}

	@Override
	public void logic() {
		// do nothing, the logic is in preLogic because it needs to be
		// done at the beginning of the next turn. Otherwise the NPCs
		// respond to player in the chat log before the player says something.
	}

	public void preLogic() {
		if (has("text")) {
			remove("text");
		}

		// if no player is talking to the NPC, the NPC can move around.
		if (!isTalking()) {
			// TODO: Reset this on FSM engine state change
			if (getAttending() != null) {
				setAttending(null);
			}
			if (hasPath()) {
				setSpeed(baseSpeed);
			}
			applyMovement();
		} else if (attending != null) {
			// If the player is too far away
			if ((attending.squaredDistance(this) > 8 * 8)
					|| attending.isDisconnected()
			// or if the player fell asleep ;)
					|| (SingletonRepository.getRuleProcessor().getTurn()
							- lastMessageTurn > playerChatTimeout)) {
				// we force him to say bye to NPC :)
				if (goodbyeMessage != null) {
					say(goodbyeMessage);
				}
				onGoodbye(attending);
				engine.setCurrentState(ConversationStates.IDLE);
				setAttending(null);
			}
		}

		// now look for nearest player only if there's an initChatAction
		if (!isTalking() && (initChatAction != null)) {
			final Player nearest = getNearestPlayer(7);

			if (nearest != null) {
				if ((initChatCondition == null)
						|| initChatCondition.fire(nearest, null, this)) {
					// Note: The sentence parameter is left as null, so be
					// carefull not to use it in the fire() handler.
					initChatAction.fire(nearest, null, this);
				}
			}
		}

		// and finally react on anybody talking to us
		final List<Player> speakers = getNearbyPlayersThatHaveSpoken(this, 5);
		for (final Player speaker : speakers) {
			tell(speaker, speaker.get("text"));
		}

		notifyWorldAboutChanges();
	}

	public boolean isTalking() {
		return engine.getCurrentState() != ConversationStates.IDLE;
	}

	@Override
	// you can override this if you don't want your NPC to turn around
	// in certain situations.
	public void say(final String text) {
		// turn towards player if necessary, then say it.
		say(text, true);
	}

	protected void say(final String text, final boolean turnToPlayer) {
		// be polite and face the player we are talking to
		if (turnToPlayer && (attending != null)) {
			faceToward(attending);
		}
		super.say(text);

	}

	/** Message when NPC is attending another player.
	 * @param text to say to bothering player
	 * @param action to perform
	 */
	public void addWaitMessage(final String text, final ChatAction action) {
		waitMessage = text;
		waitAction = action;
	}

	public void addInitChatMessage(final ChatCondition condition, final ChatAction action) {
		initChatCondition = condition;
		initChatAction = action;
	}

	/**
	 * Adds a new transition to the FSM.
	 *
	 * @param state
	 *            the starting state of the FSM
	 * @param trigger
	 *            input for this transition
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple text reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 */
	public void add(final ConversationStates state, final String trigger, final ChatCondition condition,
			final ConversationStates nextState, final String reply, final ChatAction action) {
		engine.add(state, trigger, condition, nextState, reply, action);
	}

	/**
	 * Adds a new transition with explicit ExpressionMatcher to the FSM.
	 *
	 * @param state
	 * @param trigger
	 * @param matcher
	 * @param condition
	 * @param nextState
	 * @param reply
	 * @param action
	 */
	public void add(final ConversationStates state, final String trigger, final ExpressionMatcher matcher, final ChatCondition condition,
			final ConversationStates nextState, final String reply, final ChatAction action) {
		engine.add(state, trigger, matcher, condition, nextState, reply, action);
	}

	/**
	 * Adds a new set of transitions to the FSM.
	 *
	 * @param state
	 *            the starting state of the FSM
	 * @param triggers
	 *            a list of inputs for this transition
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple text reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 */
	public void add(final ConversationStates state, final List<String> triggers, final ChatCondition condition,
			final ConversationStates nextState, final String reply, final ChatAction action) {
		engine.add(state, triggers, condition, nextState, reply, action);
	}

	/**
	 * Adds a new set of transitions to the FSM.
	 *
	 * @param states
	 *            the starting states of the FSM
	 * @param trigger
	 *            input for this transition
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple text reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 */
	public void add(final ConversationStates[] states, final String trigger, final ChatCondition condition,
			final ConversationStates nextState, final String reply, final ChatAction action) {
		for (final ConversationStates state : states) {
			add(state, trigger, condition, nextState, reply, action);
		}
	}

	/**
	 * Adds a new set of transitions to the FSM.
	 *
	 * @param states
	 *            the starting states of the FSM
	 * @param triggers
	 *            a list of inputs for this transition
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple text reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 */
	public void add(final ConversationStates[] states, final List<String> triggers,
			final ChatCondition condition, final ConversationStates nextState, final String reply,
			final ChatAction action) {
		for (final ConversationStates state : states) {
			add(state, triggers, condition, nextState, reply, action);
		}
	}


	public void add(final ConversationStates state, final List<String> triggers, final ConversationStates nextState,
			final String reply, final ChatAction action) {
		for (final String trigger : triggers) {
			add(state, trigger, null, nextState, reply, action);
		}
	}

	public void listenTo(final Player player, final String text) {
		tell(player, text);
	}

	/**
	 * If the given player says something to this NPC, and the NPC is already
	 * speaking to another player, tells the given player to wait.
	 *
	 * @param player
	 *            The player who spoke to the player
	 * @param text
	 *            The text that the given player has said
	 * @return true iff the NPC had to get rid of the player
	 */
	private boolean getRidOfPlayerIfAlreadySpeaking(final Player player, final String text) {
		// If we are attending another player make this one wait.
		if (attending != null && !player.equals(attending)) {
			if (ConversationPhrases.GREETING_MESSAGES.contains(text)) {
				logger.debug("Already attending a player");

				if (waitMessage != null) {
					say(waitMessage);
				}

				if (waitAction != null) {
					final Sentence sentence = ConversationParser.parse(text);
					// Note: sentence is currently not yet used in
					// the called handler functions.
					waitAction.fire(player, sentence, this); 
				}
			}

			return true;
		}

		return false;
	}

	/** This function evolves the FSM. 
	 * @param player 
	 * @param text 
	 * @return true if step was successfully executed*/
	private boolean tell(final Player player, final String text) {
		//TODO mf - The logic to pass spoken text to NPCs should be moved out of the NPC.
		// This way we can extend the rules to understand prepended NPC/Player names to confine the listening NPCs.
		// And we can present the Player a list of possible phrases in the client.
		if (getRidOfPlayerIfAlreadySpeaking(player, text)) {
			return true;
		}

		// If we are not attending a player, attend this one.
		if (engine.getCurrentState() == ConversationStates.IDLE) {
			logger.debug("Attending player " + player.getName());
			setAttending(player);
		}

		lastMessageTurn = SingletonRepository.getRuleProcessor().getTurn();

		return engine.step(player, text);
	}

	public void setCurrentState(final ConversationStates state) {
		engine.setCurrentState(state);
	}

	public void addGreeting() {
		addGreeting("Greetings! How may I help you?", null);
	}

	public void addGreeting(final String text) {
		addGreeting(text, null);
	}

	public void addGreeting(final String text, final ChatAction action) {
		add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				ConversationStates.ATTENDING, text, action);

		addWaitMessage(null, new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				npc.say("Please wait, " + player.getTitle()
						+ "! I am still attending to "
						+ npc.getAttending().getTitle() + ".");
			}
		});
	}

	/**
	 * Makes this NPC say a text when it hears a certain trigger during a
	 * conversation.
	 *
	 * @param trigger
	 *            The text that causes the NPC to answer
	 * @param text
	 *            The answer
	 */
	public void addReply(final String trigger, final String text) {
		add(ConversationStates.ATTENDING, trigger, null,
				ConversationStates.ATTENDING, text, null);
	}

	/**
	 * @param triggers
	 * @param text
	 */
	public void addReply(final List<String> triggers, final String text) {
		add(ConversationStates.ATTENDING, triggers,
				ConversationStates.ATTENDING, text, null);
	}

	/**
	 * Makes NPC say a text and/or do an action when a trigger is said.
	 *
	 * @param trigger
	 * @param text
	 * @param action
	 */
	public void addReply(final String trigger, final String text, final ChatAction action) {
		add(ConversationStates.ATTENDING, trigger, null,
				ConversationStates.ATTENDING, text, action);

		addWaitMessage(null, new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				npc.say("Please wait, " + player.getTitle()
						+ "! I am still attending to "
						+ npc.getAttending().getTitle() + ".");
			}
		});
	}

	/**
	 * Makes NPC say a text and/or do an action when a trigger is said.
	 *
	 * @param triggers
	 * @param text
	 * @param action
	 */
	public void addReply(final List<String> triggers, final String text, final ChatAction action) {
		add(ConversationStates.ATTENDING, triggers, null,
				ConversationStates.ATTENDING, text, action);

		addWaitMessage(null, new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				npc.say("Please wait, " + player.getTitle()
						+ "! I am still attending to "
						+ npc.getAttending().getTitle() + ".");
			}
		});
	}

	public void addQuest(final String text) {
		add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				ConversationStates.ATTENDING, text, null);
	}

	public void addJob(final String jobDescription) {
		addReply(ConversationPhrases.JOB_MESSAGES, jobDescription);
	}

	public void addHelp(final String helpDescription) {
		addReply(ConversationPhrases.HELP_MESSAGES, helpDescription);
	}

	public void addOffer(final String offerDescription) {
		addReply(ConversationPhrases.OFFER_MESSAGES, offerDescription);
	}

	public void addGoodbye() {
		addGoodbye("Bye.");
	}

	public void addGoodbye(final String text) {
		goodbyeMessage = text;
		add(ConversationStates.ANY, ConversationPhrases.GOODBYE_MESSAGES,
				ConversationStates.IDLE, text, new ChatAction() {

					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC npc) {
						npc.onGoodbye(player);
					}

					@Override
					public String toString() {
						return "SpeakerNPC.onGoodbye";
					}
				});
	}

	/**
	 * Returns a copy of the transition table.
	 *
	 * @return list of transitions
	 */
	public List<Transition> getTransitions() {
		return engine.getTransitions();
	}

	public Engine getEngine() {
		return engine;
	}

	@Override
	protected void handleObjectCollision() {
		stop();
	}

	@Override
	protected void handleSimpleCollision(final int nx, final int ny) {
		stop();
	}
}
