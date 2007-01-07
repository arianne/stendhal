package games.stendhal.server.maps.quests;

import games.stendhal.common.Pair;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Blackboard;
import games.stendhal.server.entity.item.Dice;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

public class DiceGambling extends AbstractQuest {

	private static final int STAKE = 100;

	@Override
	@SuppressWarnings("unchecked")
	public void addToWorld() {

		CroupierNPC ricardo = (CroupierNPC) NPCList.get().get("Ricardo");

		List<Pair<String, String>> prizes = Arrays.asList(
				null, // 0 - can't happen
				null, // 1 - can't happen
				null, // 2 - can't happen
				// secret consolation prize for throwing three ones ;) 
				new Pair<String, String>("fire_sword", "Dude, you are one unlucky guy! I feel so sorry for you! Here, take this fire sword."), // 3
				null, // 4 - no prize
				null, // 5 - no prize
				null, // 6 - no prize
				new Pair<String, String>("beer", "That's enough for a consolation prize, a bottle of beer."), // 7
				//new Pair<String, String>("coupon", "That's enough for a consolation prize, a coupon for a bottle of beer. Margaret will accept it."), // 7
				new Pair<String, String>("leather_boots", "Take these simple shoes as a reward."), // 8
				new Pair<String, String>("chain_legs", "I hope you have a use for these chain legs."), // 9
				new Pair<String, String>("dwarf_cloak", "You have won this fashionable dwarf cloak!"), // 10
				new Pair<String, String>("antidote", "This antidote will serve you well when you fight against poisonous creatures."), // 11
				new Pair<String, String>("sandwich", "You have won a tasty sandwich!"), // 12
				new Pair<String, String>("chain_helmet", "Your prize is this robust chain helmet."), // 13
				new Pair<String, String>("hammer_+3", "Take this valuable golden hammer!"), // 14
				new Pair<String, String>("greater_potion", "You have won a greater potion, but with your luck you'll probably never have to use it!"), // 15
				new Pair<String, String>("scale_armor_+2", "You have won this very rare enhanced scale armor."), // 16
				new Pair<String, String>("crown_shield", "You're so lucky! Here's your prize: an invaluable crown shield!"), // 17
				new Pair<String, String>("golden_legs", "You have hit the JACKPOT! Golden legs!") // 18
				);

		ricardo.setPrizes(prizes);
		
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(ricardo.getID());
		
		Blackboard board = new Blackboard(false);
		zone.assignRPObjectID(board);
		board.set(25, 0);
		StringBuffer prizelistBuffer = new StringBuffer("PRIZES:\n");
		for (int i = 18; i >= 13; i--) {
			prizelistBuffer.append("\n" + i + ": " + prizes.get(i).first());
		}
		board.setText(prizelistBuffer.toString());
		zone.add(board);
		
		board = new Blackboard(false);
		zone.assignRPObjectID(board);
		board.set(26, 0);
		prizelistBuffer = new StringBuffer("PRIZES:\n");
		for (int i = 12; i >= 7; i--) {
			prizelistBuffer.append("\n" + i + ": " + prizes.get(i).first());
		}
		board.setText(prizelistBuffer.toString());
		zone.add(board);

		
		ricardo.add(ConversationStates.ATTENDING,
					"play",
					null,
					ConversationStates.QUESTION_1,
					"In order to play, you have to stake " + STAKE + " gold. Do you want to pay?",
					null);
		
		ricardo.add(ConversationStates.QUESTION_1,
					SpeakerNPC.YES_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new ChatAction() {
						public void fire(Player player, String text, SpeakerNPC npc) {
							if (player.drop("money", STAKE)) {
								Dice dice = (Dice) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("dice");
								dice.setCroupierNPC((CroupierNPC) npc);
								player.equip(dice, true);
								npc.say("OK, here are the dice. Just throw them when you're ready. Good luck!");
							} else {
								npc.say("Hey! You don't have enough money!");
							}
						}
					});
		
		ricardo.add(ConversationStates.QUESTION_1,
					"no",
					null,
					ConversationStates.ATTENDING,
					"Coward! How will you ever become a hero when you risk nothing?",
					null);

	}


}
