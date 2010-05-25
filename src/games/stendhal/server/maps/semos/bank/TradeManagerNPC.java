package games.stendhal.server.maps.semos.bank;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

import java.awt.Rectangle;
//TODO: take NPC definition elements which are currently in XML and include here
public class TradeManagerNPC extends SpeakerNPCFactory {
	
	// these are the two players who are trading
	private Player alice;
	private Player bob;
	private SpeakerNPC npc;
	
	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Hello. Please tell me if I can be of any #help to you.");
		npc.addHelp("If you wish to #trade items with another player I can manage that for you. If you wish to learn about the magic chests in the bank, please ask #Dagobert.");
		npc.addReply("Dagobert", "Dagobert is the Customer Advisor for Semos Bank, you will find him behind the front desk. he may even have a #task for you.");
		npc.addQuest("I don't have any task for you, but #Dagobert may.");

		npc.add(ConversationStates.ATTENDING,
				"trade",
				null,
				ConversationStates.QUESTION_1,
				"Which player do you want to trade with?",
				null);
		
		npc.add(ConversationStates.QUESTION_1, 
				"", 
				null,
				ConversationStates.ATTENDING, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence,
									final SpeakerNPC npc) {
						// find out whom the player wants to trade with
						final String player2 = sentence.getSubjectName();
						startTrade(npc, player, player2);
					}
				});
		
		npc.add(ConversationStates.QUESTION_2,
				"", 
				null,
				ConversationStates.ATTENDING, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC npc) {
						//finishTrade();
					}
				});
		npc.addReply("exchange", "When you are both ready, swap places. The narrow corridors are designed so that no-one else can take the items you have placed. If someone gets in the way you can just go back and remove your items from the table until the area is clear again. If you don't understand anything, try asking another player for a demonstration. Oh, and by the way, we also have #security at the table.");
		npc.addJob("I manage trade between players.");
		npc.addGoodbye("Remember, always trade safely!");
	}
	
	// check that trading area contains both alice and bob
	private void startTrade(final SpeakerNPC npc, final Player player,
			final String player2) {
		final StendhalRPZone bankZone = npc.getZone();
		final Area tradingArea = new Area(bankZone, new Rectangle(32, 2, 12, 10));
		
		alice = player;
		bob = SingletonRepository.getRuleProcessor().getPlayer(player2);

		if (!tradingArea.contains(alice)) {
			npc.say("I can't hear " + alice.getTitle() + " from here.");
		} else if ((bob == null) || !tradingArea.contains(bob)) {
			npc.say("I can't hear " + player2 + " from here.");
		} else if (alice.getName().equals(bob.getName())) {
			npc.say("You can't trade with yourself!");
		} else {
			askAlice();
		}
	}
	private void askAlice() {
		npc.say(alice.getName() + " what item are you offering for trade? Just tell me one item for now, if you want to add more to the trade I will ask in a moment.");
		npc.setCurrentState(ConversationStates.QUESTION_2);
	}
	
	// A buyer (see entity/npc/Behaviour.java ) has a list of items names which is what he buys. we can't really list all items in the game (well maybe we could but i don't want to)
	// so lets try making the list of items he could be trading be what the player is equipped with
	
}
