package games.stendhal.server.entity.npc;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.fsm.PostTransitionAction;
import games.stendhal.server.entity.npc.fsm.PreTransitionCondition;
import games.stendhal.server.entity.npc.fsm.Transition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.Path;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * This is a finite state machine that implements a chat system. See:
 * http://en.wikipedia.org/wiki/Finite_state_machine In fact, it is a
 * transducer.
 * * States are denoted by integers. Some constants are defined in
 *   ConversationStates for often-used states.
 * * Input is the text that the player says to the SpeakerNPC.
 * * Output is the text that the SpeakerNPC answers.
 * 
 * See examples to understand how it works. RULES:
 * * State 0 (IDLE_STATE) is both the start state and the state that
 *   will end the conversation between the player and the SpeakerNPC.
 * * State 1 (ATTENDING_STATE) is the state where only one player can talk to
 *   NPC and where the prior talk doesn't matter.
 * * State -1 (ANY_STATE) is a wildcard and is used to jump from any state
 *   whenever the trigger is active.
 * * States from 2 to 100 are reserved for special behaviours and quests.
 * 
 * Example how it works: First we need to create a message to greet the player
 * and attend it. We add a hi event:
 * 
 * add(ConversationStates.IDLE_STATE,
 *     Behaviours.GREETING_MESSAGES,
 *     ConversationStates.ATTENDING_STATE,
 *     "Welcome player!",
 *     null)
 * 
 * Once the NPC is in the IDLE_STATE and hears the word "hi", it will say
 * "Welcome player!" and move to ATTENDING_STATE.
 * 
 * Now let's add some options when player is in ATTENDING_STATE, like job,
 * offer, buy, sell, etc.
 * 
 * add(ConversationStates.ATTENDING_STATE,
 *     Behaviours.JOB_MESSAGES,
 *     ConversationStates.ATTENDING_STATE,
 *     "I work as a part time example showman",
 *     null)
 * 
 * add(ConversationStates.ATTENDING_STATE,
 *     "offer",
 *     ConversationStates.ATTENDING_STATE,
 *     "I sell best quality swords",
 *     null)
 * 
 * Ok, two new events: job and offer, they go from ATTENDING_STATE to
 * ATTENDING_STATE, because after reacting to "job" or "offer", the NPC can
 * directly react to one of these again.
 * 
 * add(ConversationStates.ATTENDING_STATE,
 *     "buy",
 *     ConversationStates.BUY_PRICE_OFFERED,
 *     null,
 *     new ChatAction() {
 *         public void fire(Player player, String text, SpeakerNPC engine) {
 *             int i=text.indexOf(" "); String item=text.substring(i+1);
 *             if(item.equals("sword")) {
 *             engine.say(item+" costs 10 coins. Do you want to buy?");
 *         } else {
 *             engine.say("Sorry, I don't sell " + item);
 *             engine.setActualState(ConversationStates.ATTENDING_STATE);
 *         }
 *    }
 * });
 * 
 * Now the hard part. We listen to "buy", so we need to process the text, and
 * for that we use the ChatAction class, we create a new class that will handle
 * the event. Also see that we move to a new state, BUY_PRICE_OFFERED (20).
 * The player is then replying to a question, so we only expect two possible
 * replies: yes or no.
 * 
 * add(ConversationStates.BUY_PRICE_OFFERED,
 *     SpeakerNPC.YES_MESSAGES,
 *     ConversationStates.ATTENDING_STATE,
 *     "Sorry, I changed my mind. I won't sell anything.",
 *     null);
 *      // See Behaviours.java for a working example.
 * 
 * Whatever the reply is, return to ATTENDING_STATE so we can listen to new
 * things.
 * 
 * Finally we want to finish the conversation, so whatever state we are, we want
 * to finish a conversation with "Bye!".
 * 
 * add(ConversationStates.ANY_STATE,
 *     Behaviours.GOODBYE_MESSAGES,
 *     ConversationStates.IDLE_STATE,
 *     "Bye!",
 *     null);
 * 
 * We use ANY_STATE (-1) as a wildcard, so if the input text is "bye" the
 * transition happens, no matter in which state the FSM really is.
 */
public abstract class SpeakerNPC extends NPC {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(SpeakerNPC.class);

	private Engine engine = new Engine(this);
	private BehaviourAdder behaviourAdder = new BehaviourAdder(this, engine);
	
	/**
	 * Determines how long a conversation can be paused before it will
	 * terminated by the NPC.
	 * Defaults to 30 seconds at 300 ms / turn.
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
	 * Stores which turn was the last one at which a player spoke to this
	 * NPC. This is important to determine conversation timeout.
	 */
	private long lastMessageTurn;

	/**
	 * The player who is currently talking to the NPC, or null if the NPC
	 * is currently not taking part in a conversation. 
	 */  
	private Player attending;

	/**
	 * Creates a new SpeakerNPC.
	 *
	 * @param name The NPC's name. Please note that names should be unique.
	 */
	public SpeakerNPC(String name) {
		super();
		createPath();

		lastMessageTurn = 0;

		setName(name);
		createDialog();
		put("title_type", "npc");
	}

	abstract protected void createPath();

	abstract protected void createDialog();

	/**
	 * Is called when the NPC stops chatting with a player.
	 * Override it if needed.
	 */
	protected void onGoodbye(Player player) {
		// do nothing
	}
	
	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y + 1, 1, 1);
	}

	/**
	 * Gets all players that have recently (this turn?) talked and are
	 * standing nearby the NPC. Nearby means that they are standing
	 * less than <i>range</i> squares away horizontally and
	 * less than <i>range</i> squares away vertically.
	 * 
	 * Why is range a double, not an int? Maybe someone wanted to
	 * implement a circle instead of the rectangle we're having now.
	 * -- mort (DHerding@gmx.de)
	 * @param npc
	 * @param range
	 * @return A list of nearby players who have recently talked.
	 */
	private List<Player> getNearbyPlayersThatHaveSpoken(NPC npc, double range) {
		int x = npc.getX();
		int y = npc.getY();

		List<Player> players = new LinkedList<Player>();

		for (Player player : StendhalRPRuleProcessor.get().getPlayers()) {
			int px = player.getX();
			int py = player.getY();

			if (player.has("text")
					&& get("zoneid").equals(player.get("zoneid"))
					&& (Math.abs(px - x) < range) && (Math.abs(py - y) < range)) {
				players.add(player);
			}
		}
		return players;
	}

	/**
	 * Gets the player who is standing nearest to the NPC. Returns null if
	 * no player is standing nearby. Nearby means that they are standing
	 * less than <i>range</i> squares away horizontally and
	 * less than <i>range</i> squares away vertically.
	 * Note, however, that the Euclidian distance is used to compare which
	 * player is standing closest.
	 * @param range
	 * @return The nearest player, or null if no player is standing on the
	 *         same map.
	 */
	private Player getNearestPlayer(double range) {
		int x = getX();
		int y = getY();

		Player nearest = null;

		int squaredDistanceOfNearestPlayer = Integer.MAX_VALUE;

		for (Player player : StendhalRPRuleProcessor.get().getPlayers()) {
			int px = player.getX();
			int py = player.getY();

			if (get("zoneid").equals(player.get("zoneid"))
					&& (Math.abs(px - x) < range) && (Math.abs(py - y) < range)) {
				int squaredDistanceOfThisPlayer = (px - x) * (px - x) + (py - y) * (py - y);
				if (squaredDistanceOfThisPlayer < squaredDistanceOfNearestPlayer) {
					squaredDistanceOfNearestPlayer = squaredDistanceOfThisPlayer;
					nearest = player;
				}
			}
		}
		return nearest;
	}

	/**
	 * The player who is currently talking to the NPC, or null if the NPC
	 * is currently not taking part in a conversation.
	 *
	 * @return Player
	 */
	public Player getAttending() {
		return attending;
	}

	@Override
	public void onDead(Entity who) {
		setHP(getBaseHP());
		notifyWorldAboutChanges();
	}

	@Override
	protected void dropItemsOn(Corpse corpse) {
		// They can't die
	}

	/**
	 * Sets the time a conversation can be paused before it will be
	 * terminated by the NPC.
	 * @param playerChatTimeout the time, in turns
	 */
	public void setPlayerChatTimeout(long playerChatTimeout) {
		this.playerChatTimeout = playerChatTimeout;
	}
	
	@Override
	public void logic() {
		if (has("text")) {
			remove("text");
		}

		// if no player is talking to the NPC, the NPC can move around.
		if (!isTalking()) {
			if (hasPath()) {
				Path.followPath(this, 0.2);
				StendhalRPAction.move(this);
			}
		} else if(attending != null) {
		     // If the player is too far away
		    if ((attending.squaredDistance(this) > 8 * 8)                
             // or if the player fell asleep ;) 
                 || (StendhalRPRuleProcessor.get().getTurn() - lastMessageTurn > playerChatTimeout)) {
             // we force him to say bye to NPC :)  
				if (goodbyeMessage != null) {
					say(goodbyeMessage);
				}
				onGoodbye(attending);
				engine.setCurrentState(ConversationStates.IDLE);
				attending = null;
			}
			if (!stopped()) {
				stop();
			}
		}

         // now look for nearest player only if there's an initChatAction 
		if (!isTalking() && (initChatAction != null)) {
			Player nearest = getNearestPlayer(7);
			if (nearest != null) {
				if ((initChatCondition == null) 
					|| initChatCondition.fire(nearest, null, this)) {
					initChatAction.fire(nearest, null, this);
				}
			}
		}

        // and finally react on anybody talking to us
		List<Player> speakers = getNearbyPlayersThatHaveSpoken(this, 5);
		for (Player speaker : speakers) {
			tell(speaker, speaker.get("text"));
		}

		notifyWorldAboutChanges();
	}

	public boolean isTalking() {
		return engine.getCurrentState() != ConversationStates.IDLE;
	}

	abstract public static class ChatAction implements PostTransitionAction {
		abstract public void fire(Player player, String text, SpeakerNPC engine);
	}

	abstract public static class ChatCondition implements PreTransitionCondition {
		abstract public boolean fire(Player player, String text, SpeakerNPC engine);
	}

	@Override
	// you can override this if you don't want your NPC to turn around
	// in certain situations.
	public void say(String text) {
		// turn towards player if necessary, then say it.
		say(text, true);
	}
	
	protected void say(String text, boolean turnToPlayer) {
		// be polite and face the player we are talking to
		if (turnToPlayer && (attending != null) && (!facingTo(attending))) {
			faceTo(attending);
		}
		super.say(text);
		
	}

	/** Message when NPC is attending another player. */
	public void addWaitMessage(String text, ChatAction action) {
		waitMessage = text;
		waitAction = action;
	}

	/** Message when NPC is attending another player. */
	public void addInitChatMessage(ChatCondition condition, ChatAction action) {
		initChatCondition = condition;
		initChatAction = action;
	}

	/** Add a new transition to FSM */
	public void add(int state, String trigger, ChatCondition condition,
			int next_state, String reply, ChatAction action) {
		engine.add(state, trigger, condition, next_state, reply, action);
	}

	/**
	 * Adds a new set of transitions to the FSM
	 *
	 * @param state the starting state of the FSM
	 * @param triggers a list of inputs for this transition
	 * @param condition null or condition that has to return true for this transition to be considered
	 * @param nextState the new state of the FSM
	 * @param reply a simple text replay (may be null for no replay)
	 * @param action a special action to be taken (may be null)
	 */
	public void add(int state, List<String> triggers, ChatCondition condition,
			int nextState, String reply, ChatAction action) {
		engine.add(state, triggers, condition, nextState, reply, action);
	}

	/**
	 * Adds a new set of transitions to the FSM
	 *
	 * @param states the starting states of the FSM
	 * @param trigger input for this transition
	 * @param condition null or condition that has to return true for this transition to be considered
	 * @param nextState the new state of the FSM
	 * @param reply a simple text replay (may be null for no replay)
	 * @param action a special action to be taken (may be null)
	 */
	public void add(int[] states, String trigger, ChatCondition condition,
			int nextState, String reply, ChatAction action) {
		for (int state : states) {
			add(state, trigger, condition, nextState, reply, action);
		}
	}

	/**
	 * 
	 */
	public void add(int state, List<String> triggers, int nextState,
			String reply, ChatAction action) {
		for (String trigger : triggers) {
			add(state, trigger, null, nextState, reply, action);
		}
	}

	public void listenTo(Player player, String text) {
		tell(player, text);
	}
	
	/**
	 * If the given player says something to this NPC, and the NPC is already
	 * speaking to another player, tells the given player to wait.
	 * @param player The player who spoke to the player
	 * @param text The text that the given player has said
	 * @return true iff the NPC had to get rid of the player
	 */
	private boolean getRidOfPlayerIfAlreadySpeaking(Player player, String text) {
		// If we are attending another player make this one wait.
		// TODO: don't check if it equals the text, but if it starts
		// with it (case-insensitive)
		if (!player.equals(attending)) {
			if (ConversationPhrases.GREETING_MESSAGES.contains(text)) {
			
				logger.debug("Already attending a player");
				if (waitMessage != null) {
					say(waitMessage);
				}
	
				if (waitAction != null) {
					waitAction.fire(player, text, this);
				}
			}
			return true;
		}
		return false;
	}

	/** This function evolves the FSM */
	private boolean tell(Player player, String text) {
		// If we are no attending a player attend, this one.
		if (engine.getCurrentState() == ConversationStates.IDLE) {
			logger.debug("Attending player " + player.getName());
			attending = player;
		}

		if (getRidOfPlayerIfAlreadySpeaking(player, text)) {
			return true;
		}

		lastMessageTurn = StendhalRPRuleProcessor.get().getTurn();

		return engine.step(player, text);
	}

	public void setCurrentState(int state) {
		engine.setCurrentState(state);
	}

	public void addGreeting() {
		addGreeting("Greetings! How may I help you?", null);
	}

	public void addGreeting(String text) {
		addGreeting(text, null);
	}

	public void addGreeting(String text,
			SpeakerNPC.ChatAction action) {
		add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			ConversationStates.ATTENDING,
			text,
			action);

		addWaitMessage(null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("Please wait, " + player.getName() + "! I am still attending to "
								+ engine.getAttending().getName() + ".");
					}
				});
	}

	/**
	 * Makes this NPC say a text when it hears a certain trigger during
	 * a conversation.
	 * @param trigger The text that causes the NPC to answer
	 * @param text The answer
	 */
	public void addReply(String trigger, String text) {
		add(ConversationStates.ATTENDING,
				trigger,
				null,
				ConversationStates.ATTENDING,
				text,
				null);
	}

	/**
	 * @param triggers
	 * @param text
	 */
	public void addReply(List<String> triggers,
			String text) {
		add(ConversationStates.ATTENDING,
		triggers,
		ConversationStates.ATTENDING,
		text,
		null);
	}

	public void addQuest(String text) {
		add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				ConversationStates.ATTENDING,
				text,
				null);
	}

	public void addJob(String jobDescription) {
		addReply(ConversationPhrases.JOB_MESSAGES,
				jobDescription);
	}

	public void addHelp(String helpDescription) {
		addReply(ConversationPhrases.HELP_MESSAGES,
				 helpDescription);
	}

	public void addGoodbye() {
		addGoodbye("Bye.");
	}

	public void addGoodbye(String text) {
		goodbyeMessage = text;
		add(ConversationStates.ANY,
				ConversationPhrases.GOODBYE_MESSAGES,
				ConversationStates.IDLE,
				text,
				new ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						npc.onGoodbye(player);
					}
				});
	}

	public void addSeller(SellerBehaviour behaviour) {
		behaviourAdder.addSeller(behaviour, true);
	}

	public void addSeller(final SellerBehaviour behaviour,
					boolean offer) {
		behaviourAdder.addSeller(behaviour, offer);
	}

	public void addBuyer(BuyerBehaviour behaviour) {
		addBuyer(behaviour, true);
	}

	public void addBuyer(final BuyerBehaviour behaviour, boolean offer) {
		behaviourAdder.addBuyer(behaviour, offer);
	}

	public void addHealer(int cost) {
		behaviourAdder.addHealer(cost);
	}

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits.
	 * @param behaviour The behaviour (which includes a pricelist).
	 * @param command The action needed to get the outfit, e.g. "buy", "lend".
	 */
	public void addOutfitChanger(OutfitChangerBehaviour behaviour, String command) {
		behaviourAdder.addOutfitChanger(behaviour, command, true, true);
	}

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits. 
	 * @param behaviour The behaviour (which includes a pricelist).
	 * @param command The action needed to get the outfit, e.g. "buy", "lend".
	 * @param offer Defines if the NPC should react to the word "offer".
	 * @param canReturn If true, a player can say "return" to get his original
	 *                  outfit back.
	 */
	public void addOutfitChanger(final OutfitChangerBehaviour behaviour,
			final String command, boolean offer, final boolean canReturn) {
		behaviourAdder.addOutfitChanger(behaviour, command, offer, canReturn);
	}
	
	public void addProducer(final ProducerBehaviour behaviour, String welcomeMessage) {
		behaviourAdder.addProducer(behaviour, welcomeMessage);
	}


	/**
	 * Returns a copy of the transition table
	 *
	 * @return list of transitions
	 */
	public List<Transition> getTransitions() {
		return engine.getTransitions();
	}
}
