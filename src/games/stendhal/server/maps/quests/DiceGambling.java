package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Dice;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import marauroa.common.Pair;

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
				new Pair<String, String>(
						"fire_sword",
						"Dude, you are one unlucky guy! I feel so sorry for you! Here, take this fire sword."), // 3
				null, // 4 - no prize
				null, // 5 - no prize
				null, // 6 - no prize
				new Pair<String, String>("beer",
						"That's enough for a consolation prize, a bottle of beer."), // 7
				// new Pair<String, String>("coupon", "That's enough for
				// a consolation prize, a coupon for a bottle of beer.
				// Margaret will accept it."), // 7
				new Pair<String, String>("wine",
						"You have won this delicious bottle of wine!"), // 8
				new Pair<String, String>("studded_shield",
						"Take this simple shield as a reward."), // 9
				new Pair<String, String>("chain_legs",
						"I hope you have a use for these chain legs."), // 10
				new Pair<String, String>("antidote",
						"This antidote will serve you well when you fight against poisonous creatures."), // 11
				new Pair<String, String>("sandwich",
						"You have won a tasty sandwich!"), // 12
				new Pair<String, String>("chain_helmet",
						"Your prize is this robust chain helmet."), // 13
				new Pair<String, String>("golden_hammer",
						"Take this valuable golden hammer!"), // 14
				new Pair<String, String>(
						"greater_potion",
						"You have won a greater potion, but with your luck you'll probably never have to use it!"), // 15
				new Pair<String, String>("pauldroned_iron_cuirass",
						"You have won this very rare iron cuirass with pauldrons!"), // 16
				new Pair<String, String>("crown_shield",
						"You're so lucky! Here's your prize: an invaluable crown shield!"), // 17
				new Pair<String, String>("golden_legs",
						"You have hit the JACKPOT! Golden legs!") // 18
		);

		ricardo.setPrizes(prizes);

		StendhalRPZone zone = ricardo.getZone();

		Sign blackboard = new Sign();
		blackboard.setPosition(25, 0);
		blackboard.setEntityClass("blackboard");
		StringBuffer prizelistBuffer = new StringBuffer("PRIZES:\n");
		for (int i = 18; i >= 13; i--) {
			prizelistBuffer.append("\n" + i + ": " + prizes.get(i).first());
		}
		blackboard.setText(prizelistBuffer.toString());
		zone.add(blackboard);

		blackboard = new Sign();
		blackboard.setPosition(26, 0);
		blackboard.setEntityClass("blackboard");
		prizelistBuffer = new StringBuffer("PRIZES:\n");
		for (int i = 12; i >= 7; i--) {
			prizelistBuffer.append("\n" + i + ": " + prizes.get(i).first());
		}
		blackboard.setText(prizelistBuffer.toString());
		zone.add(blackboard);

		ricardo.add(ConversationStates.ATTENDING, "play", null,
				ConversationStates.QUESTION_1,
				"In order to play, you have to stake " + STAKE
						+ " gold. Do you want to pay?", null);

		ricardo.add(
				ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new PlayerHasItemWithHimCondition("money", STAKE),
				ConversationStates.ATTENDING,
				"OK, here are the dice. Just throw them when you're ready. Good luck!",
				new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						player.drop("money", STAKE);
						Dice dice = (Dice) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
								"dice");
						dice.setCroupierNPC((CroupierNPC) npc);
						player.equip(dice, true);
					}
				});

		ricardo.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, new NotCondition(
						new PlayerHasItemWithHimCondition("money", STAKE)),
				ConversationStates.ATTENDING,
				"Hey! You don't have enough money!", null);

		ricardo.add(
				ConversationStates.QUESTION_1,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Coward! How will you ever become a hero when you risk nothing?",
				null);

	}

}
