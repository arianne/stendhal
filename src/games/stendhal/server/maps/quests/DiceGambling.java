package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Dice;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;

public class DiceGambling extends AbstractQuest {
	
	private static final int STAKE = 100;
	// private static final int STAKE = 1; // for testing
	
	@Override
	public void addToWorld() {

		CroupierNPC ricardo = (CroupierNPC) NPCList.get().get("Ricardo");

		String[] prizes = new String[19];
		prizes[18] = "golden_legs";
		prizes[17] = "crown_shield";
		prizes[16] = "scale_armor_+2";
		prizes[15] = "greater_potion";
		prizes[14] = "plate_shield";
		prizes[13] = "chain_helmet";
		prizes[12] = "sandwich";
		prizes[11] = "antidote";
		prizes[10] = "dwarf_cloak";
		prizes[9] = "chain_legs";
		prizes[8] = "leather_boots";
		prizes[7] = "beer";
		ricardo.setPrizes(prizes);
		
		ricardo.add(ConversationStates.ATTENDING,
					"play",
					null,
					ConversationStates.QUESTION_1,
					"In order to playing, you have to stake " + STAKE + " gold. Do you want to pay?",
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
