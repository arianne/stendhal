package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Dice;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Pair;

public class DiceGambling extends AbstractQuest {

	private static final int STAKE = 100;
	@Override
	public String getSlotName() {
		return "dice_gambling";
	}
	@Override
	
	public void addToWorld() {

		final CroupierNPC ricardo = (CroupierNPC) SingletonRepository.getNPCList().get("Ricardo");

		final Map<Integer, Pair<String, String>> prizes = initPrices();

		ricardo.setPrizes(prizes);

		final StendhalRPZone zone = ricardo.getZone();

		Sign blackboard = new Sign();
		blackboard.setPosition(25, 0);
		blackboard.setEntityClass("blackboard");
		StringBuilder prizelistBuffer = new StringBuilder("PRIZES:\n");
		for (int i = 18; i >= 13; i--) {
			prizelistBuffer.append("\n" + i + ": " + prizes.get(i).first());
		}
		blackboard.setText(prizelistBuffer.toString());
		zone.add(blackboard);

		blackboard = new Sign();
		blackboard.setPosition(26, 0);
		blackboard.setEntityClass("blackboard");
		prizelistBuffer = new StringBuilder("PRIZES:\n");
		for (int i = 12; i >= 7; i--) {
			prizelistBuffer.append("\n" + i + ": " + prizes.get(i).first());
		}
		blackboard.setText(prizelistBuffer.toString());
		zone.add(blackboard);

		ricardo.add(ConversationStates.ATTENDING, "play", null,
				ConversationStates.QUESTION_1,
				"In order to play, you have to stake " + STAKE
						+ " gold. Do you want to pay?", null);

		ricardo.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("money", STAKE),
			ConversationStates.ATTENDING,
			"OK, here are the dice. Just throw them when you're ready. Good luck!",
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					player.drop("money", STAKE);
					final Dice dice = (Dice) SingletonRepository.getEntityManager()
							.getItem("dice");
					dice.setCroupierNPC((CroupierNPC) npc);
					player.equipOrPutOnGround(dice);
				}
			});
		
		ricardo.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, 
			new NotCondition(new PlayerHasItemWithHimCondition("money", STAKE)),
			ConversationStates.ATTENDING,
			"Hey! You don't have enough money!", null);

		ricardo.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Coward! How will you ever become a hero when you risk nothing?",
			null);

	}
	private Map <Integer, Pair<String, String>> initPrices() {
		Map<Integer, Pair<String, String>> map = new HashMap<Integer, Pair<String, String>>();
		map.put(3, new Pair<String, String>("fire sword",
				"Dude, you are one unlucky guy! I feel so sorry for you! Here, take this fire sword."));
		map.put(7, new Pair<String, String>("beer",
				"That's enough for a consolation prize, a bottle of beer."));
		map.put(8, new Pair<String, String>("wine",
				"You have won this delicious glass of wine!"));
		map.put(9, new Pair<String, String>("studded shield",
				"Take this simple shield as a reward."));
		map.put(10, new Pair<String, String>("chain legs",
				"I hope you have a use for these chain legs."));
		map.put(11,	new Pair<String, String>("antidote",
			   "This antidote will serve you well when you fight against poisonous creatures."));
		map.put(12, new Pair<String, String>("sandwich",
				"You have won a tasty sandwich!"));
		map.put(13, new Pair<String, String>("chain helmet",
				"Your prize is this robust chain helmet."));
		map.put(14, new Pair<String, String>("golden hammer",
				"Take this valuable golden hammer!"));
		map.put(15,	new Pair<String, String>("greater potion",
				"You have won a greater potion, but with your luck you'll probably never have to use it!"));
		map.put(16, new Pair<String, String>("pauldroned iron cuirass",
				"You have won this very rare iron cuirass with pauldrons!"));
		map.put(17,	new Pair<String, String>("crown shield",
				"You're so lucky! Here's your prize: an invaluable crown shield!"));
		map.put(18, new Pair<String, String>("golden legs",
				"You have hit the JACKPOT! Golden legs!"));
		
		return map;
	}
	@Override
	public String getName() {
		return "DiceGambling";
	}

}
