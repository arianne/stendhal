package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Blackjack extends AbstractQuest implements TurnListener {

	private static final int STAKE = 1; // TODO: increase after testing
	
	private boolean bankStands;

	private boolean playerStands;

	private Map<String, Integer> cardValues;
	
	private Stack<String> deck;

	private List<String> playerCards = new LinkedList<String>();

	private List<String> bankCards = new LinkedList<String>();
	
	private StackableItem playerCardsItem;

	private StackableItem bankCardsItem;

	private SpeakerNPC ramon;

	private StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get()
	.getRPZone("-1_athor_ship_w2");
	
	private void startNewGame(Player player) {
		cleanUpTable();
		playerCards.clear();
		bankCards.clear();
		
		playerCardsItem = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("cards");
		zone.assignRPObjectID(playerCardsItem);
		zone.add(playerCardsItem);
		playerCardsItem.set(25, 38);
		bankCardsItem = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("cards");
		zone.assignRPObjectID(bankCardsItem);
		zone.add(bankCardsItem);
		bankCardsItem.set(27, 38);
		
		playerStands = false;
		bankStands = false;
		// Before each game, we put all cards back on the deck and
		// shuffle it.
		// We could change that later so that the player can
		// try to remember what's still on the deck
		deck = new Stack<String>();
		for (String card: cardValues.keySet()) {
			deck.add(card);
		}
		Collections.shuffle(deck);
		
		dealCards(player, 2);
	}
	
	private void cleanUpTable() {
		if (playerCardsItem != null) {
			zone.remove(playerCardsItem);
			playerCardsItem = null;
		}
		if (bankCardsItem != null) {
			zone.remove(bankCardsItem);
			bankCardsItem = null;
		}
	}
	
	private int countAces(List<String> cards) {
		int count = 0;
		for (String card: cards) {
			if (card.startsWith("Ace ")) {
				count++;
			}
		}
		return count;
	}
	
	private int sumValues(List<String> cards) {
		int sum = 0;
		for (String card: cards) {
			sum += cardValues.get(card);
		}
		int numberOfAces = countAces(cards);
		while (sum > 21 && numberOfAces > 0) {
			sum -= 10;
			numberOfAces--;
		}
		return sum;
	}
	
	private boolean isBlackjack(List<String> cards) {
		return sumValues(cards) == 21 && cards.size() == 2;
	}
	
	/**
	 * Deals <i>number</i> cards to the player, if the player is
	 * not standing, and to the bank, if the bank is not standing.
	 * @param number The number of cards that each player should
	 *               draw.
	 */
	private void dealCards(Player player, int number) {
		String message = "\n";
		int playerSum = 0;
		int bankSum = 0;
		for (int i = 0; i < number; i++) {
			if (! playerStands) {
				String playerCard = deck.pop();
				playerCards.add(playerCard);
				message += "You got a " + playerCard + ".\n";
			}
	
			if (! bankStands) {
				String bankCard = deck.pop();
				bankCards.add(bankCard);
				message += "The bank got a " + bankCard + ".\n";
			}
			playerSum = sumValues(playerCards);
			bankSum = sumValues(bankCards);
		}
		playerCardsItem.setQuantity(playerSum);
		playerCardsItem.setDescription("You see the player's cards: " + SpeakerNPC.enumerateCollection(playerCards));
		playerCardsItem.notifyWorldAboutChanges();
		bankCardsItem.setQuantity(bankSum);
		bankCardsItem.setDescription("You see the bank's cards: " + SpeakerNPC.enumerateCollection(bankCards));
		bankCardsItem.notifyWorldAboutChanges();
		if (! playerStands) {
			message += "You have " + playerSum + ".\n";
		}
		if (! bankStands) {
			message += "The bank has " + bankSum + ".\n";
			if (bankSum >= 17 && bankSum <= 21 && bankSum >= playerSum) {
				bankStands = true;
				message += "The bank stands.\n";
			}
		}
		String message2 = analyze(player); 
		if (message2 != null) {
		    message += message2;
		}
		ramon.say(message);
	}

	/**
	 * @return The text that the dealer should say, or null if he shouldn't
	 *         say anything.
	 */
	private String analyze(Player player) {
		int playerSum = sumValues(playerCards);
		int bankSum = sumValues(bankCards);
		String message = null;
		if (isBlackjack (bankCards) && isBlackjack (playerCards)) {
			message = "You have a blackjack, but the bank has one too. It's a push.";
			payOff(player, 1);
		} else if (isBlackjack (bankCards)) {
			message = "The bank has a blackjack. Better luck next time!";				
		} else if (isBlackjack (playerCards)) {
			message = "You have a blackjack! Congratulations!";
			payOff(player, 3);
		} else if (playerSum > 21) {
			if (bankSum > 21 ) {
				message = "Both have busted! This is a draw.";
				payOff(player, 1);
			} else {
				message = "You have busted! Better luck next time!";
			}
		} else if (bankSum > 21 ) {
			message = "The bank has busted! Congratulations!";
			payOff(player, 2);
		} else {
			if (! playerStands && playerSum != 21) {
				message = "Do you want another card?";
				ramon.setCurrentState(ConversationStates.QUESTION_1);
			} else if (! bankStands) {
				letBankDrawAfterPause(ramon.getAttending().getName());
			} else if (bankSum > playerSum) {
				message = "The bank has won. Better luck next time!";
			} else if (bankSum == playerSum) {
				message = "This is a draw.";
				payOff(player, 1);
			} else {
				message = "You have won. Congratulations!";
				payOff(player, 2);
			}
		}
		return message;
	}
	
	private void letBankDrawAfterPause(String playerName) {
		TurnNotifier.get().notifyInTurns(3, this, playerName);
	}

	public void onTurnReached(int currentTurn, String message) {
		// Check whether the player is still there, or if he
		// has left/said goodbye/logged out.
		if (message.equals(ramon.getAttending().getName())) {
			dealCards(ramon.getAttending(), 1);
		}
	}

	private void payOff(Player player, int factor) {
		StackableItem money = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("money");
		money.setQuantity(factor * STAKE);
		player.equip(money, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addToWorld() {

		ramon = new SpeakerNPC("Ramon") {
			@Override
			protected void createPath() {
				// Ramon doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				
				addGreeting("Welcome to the #blackjack table! You can #play here to kill time until the ferry arrives.");
				addJob("I was a card dealer in the Semos tavern, but I lost my gambling license. But my brother Ricardo is still working in the tavern.");
				addReply("blackjack", "Blackjack is a simple card game. You can read the rules at the blackboard at the wall.");
				// TODO
				addHelp("...");
				addGoodbye("Goodbye!");
			}
			
			@Override
			protected void onGoodbye(Player player) {
				// remove the cards when the player stops playing.
				cleanUpTable();
			}
		};
		
		npcs.add(ramon);
		
		zone.assignRPObjectID(ramon);
		ramon.put("class", "naughtyteen2npc");
		ramon.setX(26);
		ramon.setY(35);
		ramon.setDirection(Direction.DOWN);
		ramon.setBaseHP(100);
		ramon.setHP(ramon.getBaseHP());
		zone.addNPC(ramon);		

		cardValues = new HashMap<String,Integer>();
		String[] colors = {"Clubs", "Diamonds", "Hearts", "Spades"};
		String[] pictures = {"Jack", "Queen", "King"};
		for (String color: colors) {
			for (int i = 2; i <= 10; i++) {
				cardValues.put(i + " of " + color, i);
			}
			for (String picture: pictures) {
				cardValues.put(picture + " of " + color, 10);
			}
			// ace values are calculated during the game
			cardValues.put("Ace of " + color, 11);
		}
		
		ramon.add(ConversationStates.ATTENDING,
					"play",
					null,
					ConversationStates.SERVICE_OFFERED,
					"In order to play, you have to stake " + STAKE + " gold. Do you want to pay?",
					null);
		
		ramon.add(ConversationStates.SERVICE_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Coward! How will you ever become a hero when you risk nothing?",
				null);

		ramon.add(ConversationStates.SERVICE_OFFERED,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						if (player.drop("money", STAKE)) {
							startNewGame(player);
						} else {
							npc.say("Hey! You don't have enough money!");
						}
					}
				});
	ramon.add(ConversationStates.QUESTION_1,
			SpeakerNPC.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC npc) {
					dealCards(player, 1);
				}
			});

	// The player says he doesn't want to have another card.
	// Let the dealer give cards to the bank.
	ramon.add(ConversationStates.QUESTION_1,
			SpeakerNPC.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC npc) {
					playerStands = true;
					if (bankStands) {
						// Both stand. Let the dealer tell the final resul
						String message = analyze(player);
						if (message != null) {
							ramon.say(message);
						}
					} else {
						letBankDrawAfterPause(player.getName());
					}
				}
			});
	}
}
