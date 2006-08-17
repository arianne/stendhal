package games.stendhal.server.entity.npc;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.common.Rand;

import java.awt.geom.Rectangle2D;
import java.util.*;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;

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
 *     "yes",
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
 * transition happens.
 */
public abstract class SpeakerNPC extends NPC {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(SpeakerNPC.class);

	public static final String[] GREETING_MESSAGES = {"hi", "hello", "greetings", "hola"};
	
	public static final String[] JOB_MESSAGES = {"job", "work"};

	public static final String[] HELP_MESSAGES = {"help", "ayuda"};

	public static final String[] QUEST_MESSAGES = {"task", "quest", "favor", "favour"};

	public static final String[] GOODBYE_MESSAGES = {"bye", "farewell", "cya", "adios"};
	
	// FSM state table
	private List<StatePath> statesTable;

	// FSM actual state
	private int actualState;

	private int maxState;

	// Default wait message when NPC is busy
	private String waitMessage;

	// Default wait action when NPC is busy
	private ChatAction waitAction;

	// Default bye message when NPC stop chatting with the player
	private String byeMessage;

	// Default bye action when NPC stop chatting with the player
	private ChatAction byeAction;

	private ChatCondition initChatCondition;

	// Default initChat action when NPC stop chatting with the player
	private ChatAction initChatAction;

	// Timeout control value
	private long lastMessageTurn;

	private static long TIMEOUT_PLAYER_CHAT = 90; // 30 seconds at 300ms.

	// Attended players
	private Player attending;

	private Map<String, Object> behavioursData;

	/**
	 * Helper function to nicely formulate an enumeration of a collection.
	 * For example, for a collection containing the 3 elements x, y, z,
	 * returns the string "x, y, and z".
	 * @param collection The collection whose elements should be enumerated 
	 * @return A nice String representation of the collection
	 */
	public static String enumerateCollection(Collection<String> collection) {
		String[] elements = collection.toArray(new String[collection.size()]);
		if (elements.length == 0) {
			return "";
		} else if (elements.length == 1) {
			return elements[0];
		} else if (elements.length == 2) {
			return elements[0] + " and " + elements[1];
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < elements.length - 1; i++) {
				sb.append(elements[i] + ", ");
			}
			sb.append("and " + elements[elements.length - 1]);
			return sb.toString();
		}
	}
	
	public SpeakerNPC(String name) throws AttributeNotFoundException {
		super();
		createPath();

		statesTable = new LinkedList<StatePath>();
		actualState = ConversationStates.IDLE;
		maxState = 0;
		lastMessageTurn = 0;

		behavioursData = new HashMap<String, Object>();
		setName(name);
		createDialog();
		put("title_type", "npc");
	}

	abstract protected void createPath();

	abstract protected void createDialog();

	public void setBehaviourData(String behaviour, Object data) {
		behavioursData.put(behaviour, data);
	}

	public Object getBehaviourData(String behaviour) {
		return behavioursData.get(behaviour);
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y + 1, 1, 1);
	}

	private List<Player> getNearestPlayersThatHasSpoken(NPC npc, double range) {
		int x = npc.getx();
		int y = npc.gety();

		List<Player> players = new LinkedList<Player>();

		for (Player player : rp.getPlayers()) {
			int px = player.getx();
			int py = player.gety();

			if (player.has("text")
					&& get("zoneid").equals(player.get("zoneid"))
					&& Math.abs(px - x) < range && Math.abs(py - y) < range) {
				players.add(player);
			}
		}

		return players;
	}

	private Player getNearestPlayer(double range) {
		int x = getx();
		int y = gety();

		Player nearest = null;

		int dist = Integer.MAX_VALUE;

		for (Player player : rp.getPlayers()) {
			int px = player.getx();
			int py = player.gety();

			if (get("zoneid").equals(player.get("zoneid"))
					&& Math.abs(px - x) < range && Math.abs(py - y) < range) {
				int actual = (px - x) * (px - x) + (py - y) * (py - y);
				if (actual < dist) {
					dist = actual;
					nearest = player;
				}
			}
		}

		return nearest;
	}

	public Player getAttending() {
		return attending;
	}

	@Override
	public void onDead(RPEntity who) {
		setHP(getBaseHP());
		world.modify(this);
	}

	@Override
	protected void dropItemsOn(Corpse corpse) {
		// They can't die
	}

	@Override
	public void logic() {
		if (has("text"))
			remove("text");

		// if no player is talking to the NPC, the NPC can move around.
		if (!talking()) {
			if (hasPath()) {
				Path.followPath(this, 0.2);
				StendhalRPAction.move(this);
			}
		} else if(attending != null) {
		     // If the player is too far away
		    if ((attending.distance(this) > 8 * 8)                
             // or if the player fell asleep ;) 
                 || (rp.getTurn() - lastMessageTurn > TIMEOUT_PLAYER_CHAT)) {
             // we force him to say bye to NPC :)  
				if (byeMessage != null) {
					say(byeMessage);
				}
				if (byeAction != null) {
					byeAction.fire(attending, null, this);
				}
				actualState = ConversationStates.IDLE;
				attending = null;
			}
			if (!stopped()) {
				stop();
			}
		}

         // now look for nearest player only if there's an initChatAction 
		if (!talking() && initChatAction != null) {
			Player nearest = getNearestPlayer(7);
			if (nearest != null) {
				if (initChatCondition == null || initChatCondition
								.fire(nearest, this)) {
					initChatAction.fire(nearest, null, this);
				}
			}
		}

        // and finally react on anybody talking to us
		List<Player> speakers = getNearestPlayersThatHasSpoken(this, 5);
		for (Player speaker : speakers) {
			tell(speaker, speaker.get("text"));
		}


		world.modify(this);
	}

	public boolean talking() {
		return actualState != ConversationStates.IDLE;
	}

	abstract public static class ChatAction {
		abstract public void fire(Player player, String text, SpeakerNPC engine);
	}

	abstract public static class ChatCondition {
		abstract public boolean fire(Player player, SpeakerNPC engine);
	}

	// TODO: docu please. What is a StatePath?
	private class StatePath {
		public int state;

		public int nextState;

		public String trigger;

		public ChatCondition condition;

		public String reply;

		public ChatAction action;

		StatePath(int state, String trigger, ChatCondition condition,
				int nextState, String reply, ChatAction action) {
			this.state = state;
			this.condition = condition;
			this.nextState = nextState;
			this.trigger = trigger.toLowerCase();
			this.reply = reply;
			this.action = action;
		}

		// TODO: docu please
		public boolean absoluteJump(int state, String text) {
			if (this.state == ConversationStates.ANY && trigger.equalsIgnoreCase(text)) {
				return true;
			}
			return false;
		}

		public boolean equals(int state, String text) {
			if (state == this.state && trigger.equalsIgnoreCase(text)) {
				return true;
			}
			return false;
		}

		public boolean contains(int state, String text) {
			text = text.toLowerCase();
			if (state == this.state && text.startsWith(trigger)) {
				return true;
			}

			return false;
		}

		public boolean executeCondition(Player player, SpeakerNPC npc) {
			if (condition != null) {
				return condition.fire(player, npc);
			} else {
				return true;
			}
		}

        @Override
		public String toString() {
			return "[" + state + "," + trigger + "," + nextState + ","
					+ condition + "]";
		}
	}

	@Override
	public void say(String text) {
		// be polite and face the player we are talking to
		if (!facingto(attending)) {
			faceto(attending);
		}
		super.say(text);
	}

	/** Message when NPC is attending another player. */
	public void addWaitMessage(String text, ChatAction action) {
		waitMessage = text;
		waitAction = action;
	}

	/** Message when NPC is attending another player. */
	public void addByeMessage(String text, ChatAction action) {
		byeMessage = text;
		byeAction = action;
	}

	/** Message when NPC is attending another player. */
	public void addInitChatMessage(ChatCondition condition, ChatAction action) {
		initChatCondition = condition;
		initChatAction = action;
	}

	private StatePath get(int state, String trigger, ChatCondition condition) {
		for (StatePath i : statesTable) {
			if (i.equals(state, trigger)) {
				if (i.condition == condition) {
					return i;
				}
			}
		}
		return null;
	}

	public int getFreeState() {
		maxState++;
		return maxState;
	}

	/** Add a new state to FSM */
	public void add(int state, String trigger, ChatCondition condition,
			int next_state, String reply, ChatAction action) {
		if (state > maxState) {
			maxState = state;
		}

		StatePath existing = get(state, trigger, condition);
		if (existing != null) {
			// A previous state, trigger combination exist.
			logger.warn("Adding to " + existing + " the state [" + state + ","
					+ trigger + "," + next_state + "]");
			existing.reply = existing.reply + " " + reply;
		}

		StatePath item = new StatePath(state, trigger, condition, next_state,
				reply, action);
		statesTable.add(item);
	}

	/**
	 * Adds a new set of states to FSM
	 */
	public void add(int state, String[] triggers, ChatCondition condition,
			int nextState, String reply, ChatAction action) {
		for (String trigger : triggers) {
			add(state, trigger, condition, nextState, reply, action);
		}
	}

	/**
	 * 
	 */
	public void add(int state, List<String> triggers, ChatCondition condition,
			int nextState, String reply, ChatAction action) {
		add(state, triggers.toArray(new String[2]), condition, nextState,
				reply, action);
	}

	public void add(int[] states, String trigger, ChatCondition condition,
			int nextState, String reply, ChatAction action) {
		for (int state : states) {
			add(state, trigger, condition, nextState, reply, action);
		}
	}

	public void add(int state, String[] triggers, int nextState, String reply,
			ChatAction action) {
		for (String trigger : triggers) {
			add(state, trigger, null, nextState, reply, action);
		}
	}

	/**
	 * 
	 */
	public void add(int state, List<String> triggers, int nextState,
			String reply, ChatAction action) {
		add(state, triggers.toArray(new String[2]), null, nextState, reply,
				action);
	}

	public void add(int state, String[] triggers, int nextState,
			String[] replies, ChatAction action) {
		for (String trigger : triggers) {
			for (String reply : replies) {
				add(state, trigger, null, nextState, reply, action);
			}
		}
	}

	/**
	 * 
	 */
	public void add(int state, List<String> triggers, int nextState,
			List<String> replies, ChatAction action) {
		add(state, triggers.toArray(new String[2]), nextState, replies
				.toArray(new String[2]), action);
	}

	// TODO: docu please. What is type?
	private boolean matchState(int type, Player player, String text) {
		List<StatePath> listCondition = new LinkedList<StatePath>();
		List<StatePath> listConditionLess = new LinkedList<StatePath>();

		// First we try to match with stateless states.
		for (StatePath state : statesTable) {
			if ((type == 0 && actualState != ConversationStates.IDLE
					&& state.absoluteJump(actualState, text))
					|| (type == 1 && state.equals(actualState, text))
					|| (type == 2 && state.contains(actualState, text))) {
				if (state.executeCondition(player, this)) {
					if (state.condition == null) {
						listConditionLess.add(state);
					} else {
						listCondition.add(state);
					}
				}
			}
		}

		if (listCondition.size() > 0) {
			int i = Rand.rand(listCondition.size());
			executeState(player, text, listCondition.get(i));
			return true;
		}

		if (listConditionLess.size() > 0) {
			int i = Rand.rand(listConditionLess.size());
			executeState(player, text, listConditionLess.get(i));
			return true;
		}

		return false;
	}

	// these constants don't seem to be used anymore; commented out.
	// - DHerding@gmx.de
	// final private static int ABSOLUTE_JUMP = 0;

	// final private static int EXACT_MATCH = 1;

	// final private static int SIMILAR_MATCH = 2;

	public void listenTo(Player player, String text) {
		tell(player, text);
	}

	/** This function evolves the FSM */
	private boolean tell(Player player, String text) {
		// If we are no attening a player attend, this one.
		if (actualState == ConversationStates.IDLE) {
			logger.debug("Attending player " + player.getName());
			attending = player;
		}

/* this code should be useless as this is already treated in logic()
 * intensifly
        // If the attended player with state != 0 got idle, attend this one.
        if (rp.getTurn() - lastMessageTurn > TIMEOUT_PLAYER_CHAT
				&& actualState != ConversationStates.IDLE) {
			if (byeMessage != null) {
				say(byeMessage);
			}

			if (byeAction != null) {
				byeAction.fire(attending, null, this);
			}

			logger.debug("Attended player " + attending + " went timeout");

			attending = player;
			actualState = ConversationStates.IDLE;
		}
*/
		// If we are attending another player make this one waits.
		if (!attending.equals(player)) {
			logger.debug("Already attending a player");
			if (waitMessage != null) {
				say(waitMessage);
			}

			if (waitAction != null) {
				waitAction.fire(player, text, this);
			}

			return true;
		}

		lastMessageTurn = rp.getTurn();

		if (matchState(0, player, text)) {
			return true;
		} else if (matchState(1, player, text)) {
			return true;
		} else if (matchState(2, player, text)) {
			return true;
		} else {
			// Couldn't match the text with the current FSM state
			logger.debug("Couldn't match any state: " + actualState + ":"
					+ text);
			return false;
		}
	}

	public void setActualState(int state) {
		actualState = state;
	}

	private void executeState(Player player, String text, StatePath state) {
		int nextState = state.nextState;
		if (state.reply != null) {
			say(state.reply);
		}

		actualState = nextState;

		if (state.action != null) {
			state.action.fire(player, text, this);
		}
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
			GREETING_MESSAGES,
			ConversationStates.ATTENDING,
			text,
			action);

		addWaitMessage(null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("Please wait! I am attending "
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
	 * Makes this NPC say a text when it hears one of the given triggers
	 * during a conversation.
	 * @param npc The NPC that should reply
	 * @param triggers The texts that cause the NPC to answer
	 * @param text The answer
	 */
	public void addReply(String[] triggers, String text) {
		add(ConversationStates.ATTENDING,
				triggers,
				ConversationStates.ATTENDING,
				text,
				null);
	}

	/**
	 * @param npc
	 * @param triggers
	 * @param text
	 */
	public void addReply(List<String> triggers,
			String text) {
		addReply(triggers.toArray(new String[2]),
				 text);
	}

	public void addQuest(String text) {
		add(ConversationStates.ATTENDING,
				QUEST_MESSAGES,
				ConversationStates.ATTENDING,
				text,
				null);
	}

	public static void addQuest(SpeakerNPC npc, String[] texts) {
		npc.add(ConversationStates.ATTENDING,
				QUEST_MESSAGES,
				ConversationStates.ATTENDING,
				texts,
				null);
	}

	public void addJob(String jobDescription) {
		addReply(JOB_MESSAGES,
				jobDescription);
	}

	public void addHelp(String helpDescription) {
		addReply(HELP_MESSAGES,
				 helpDescription);
	}

	public void addGoodbye() {
		addGoodbye("Bye.");
	}

	public void addGoodbye(String text) {
		addByeMessage(text, null);
		add(ConversationStates.ANY,
				GOODBYE_MESSAGES,
				ConversationStates.IDLE,
				text,
				null);
	}

	public void addSeller(SellerBehaviour behaviour) {
		addSeller(behaviour, true);
	}

	public void addSeller(SellerBehaviour behaviour,
			boolean offer) {
		setBehaviourData("seller", behaviour);

		if (offer) {
			add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"I sell " + enumerateCollection(behaviour.dealtItems()) + ".",
					null);
		}

		add(ConversationStates.ATTENDING,
				"buy",
				null,
				ConversationStates.BUY_PRICE_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						SellerBehaviour sellerBehaviour = (SellerBehaviour) engine
								.getBehaviourData("seller");
		
						// find out what the player wants to buy, and how
						// much of it
						String[] words = text.split(" ");
		
						String amount = "1";
						String item = null;
						if (words.length > 2) {
							amount = words[1].trim();
							item = words[2].trim();
						} else if (words.length > 1) {
							item = words[1].trim();
						}
		
						// find out if the NPC sells this item, and if so,
						// how much it costs.
						if (sellerBehaviour.hasItem(item)) {
							sellerBehaviour.chosenItem = item;
							sellerBehaviour.setAmount(amount);
		
							int price = sellerBehaviour.getUnitPrice(item)
									* sellerBehaviour.amount;
		
							engine.say(amount + " " + item + " costs " + price
									+ ". Do you want to buy?");
						} else {
							engine.say("Sorry, I don't sell " + item);
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		add(ConversationStates.BUY_PRICE_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Thanks.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						SellerBehaviour sellerBehaviour = (SellerBehaviour) engine
								.getBehaviourData("seller");
		
						String itemName = sellerBehaviour.chosenItem;
						logger.debug("Selling a " + itemName + " to player "
								+ player.getName());
		
						sellerBehaviour.transactAgreedDeal(engine, player);
					}
				});

		add(ConversationStates.BUY_PRICE_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING, "Ok, how may I help you?",
				null);
	}

	public void addBuyer(BuyerBehaviour behaviour) {
		addBuyer(behaviour, true);
	}

	public void addBuyer(BuyerBehaviour behaviour, boolean offer) {
		setBehaviourData("buyer", behaviour);

		if (offer) {
			add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"I buy " + enumerateCollection(behaviour.dealtItems()) + ".",
					null);
		}

		add(ConversationStates.ATTENDING,
				"sell",
				null,
				ConversationStates.SELL_PRICE_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						BuyerBehaviour buyerBehaviour = (BuyerBehaviour) engine
								.getBehaviourData("buyer");
		
						String[] words = text.split(" ");
		
						String amount = "1";
						String item = null;
						if (words.length > 2) {
							amount = words[1].trim();
							item = words[2].trim();
						} else if (words.length > 1) {
							item = words[1].trim();
						}
		
						if (buyerBehaviour.hasItem(item)) {
							buyerBehaviour.chosenItem = item;
							buyerBehaviour.setAmount(amount);
							int price = buyerBehaviour.getCharge(player);
		
							engine.say(amount + " " + item + " is worth " + price
									+ ". Do you want to sell?");
						} else {
							engine.say("Sorry, I don't buy " + item);
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		add(ConversationStates.SELL_PRICE_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Thanks.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						BuyerBehaviour buyerBehaviour = (BuyerBehaviour) engine
								.getBehaviourData("buyer");
		
						logger.debug("Buying something from player "
								+ player.getName());
		
						buyerBehaviour.transactAgreedDeal(engine, player);
					}
				});

		add(ConversationStates.SELL_PRICE_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Ok, how may I help you?",
				null);
	}

	public void addHealer(int cost) {
		setBehaviourData("healer", new HealerBehaviour(world, cost));

		add(ConversationStates.ATTENDING,
				"offer",
				null,
				ConversationStates.ATTENDING,
				"I can #heal you.",
				null);
		
		add(ConversationStates.ATTENDING,
				"heal",
				null,
				ConversationStates.HEAL_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						HealerBehaviour healer = (HealerBehaviour) engine
								.getBehaviourData("healer");
						healer.chosenItem = "heal";
						healer.amount = 1;
						int cost = healer.getCharge(player);
		
						if (cost > 0) {
							engine.say("Healing costs " + cost
									+ ". Do you want to pay?");
						} else {
							engine.say("You are healed. How may I help you?");
							healer.heal(player);
		
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		add(ConversationStates.HEAL_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						HealerBehaviour healerBehaviour = (HealerBehaviour) engine
								.getBehaviourData("healer");
						
						if (player.drop("money", healerBehaviour.getCharge(player))) {
							healerBehaviour.heal(player);
							engine.say("You are healed. How may I help you?");
						} else {
							engine.say("A real pity! You don't have enough money!");
						}
					}
				});

		add(ConversationStates.HEAL_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"OK, how may I help you?",
				null);
		}
}
