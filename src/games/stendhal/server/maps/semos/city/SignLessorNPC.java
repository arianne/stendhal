	package games.stendhal.server.maps.semos.city;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.entity.mapstuff.office.RentedSignList;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.TextHasParameterCondition;
import games.stendhal.server.entity.player.Player;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * A merchant (original name: XXX) who rents signs to players.
 *
 * The player has to have at least level 5 to prevent abuse by newly created characters.
 */
public class SignLessorNPC extends SpeakerNPCFactory {
	protected String text;
	private static final int MONEY = 100; 
	RentedSignList rentedSignList = null;

	public SignLessorNPC() {
		StendhalRPZone zone = StendhalRPWorld.get().getZone("0_semos_city");
		Shape shape = new Rectangle(21, 48, 17, 1);
		rentedSignList = new RentedSignList(zone, shape);
	}

	@Override
	public void createDialog(SpeakerNPC npc) {
		npc.addGreeting();
		npc.addJob("I #rent signs for a day.");
		npc.addHelp("If you want to #rent a sign, just tell me what i should write up on it.");

		npc.add(ConversationStates.ATTENDING, "rent", 
			new LevelLessThanCondition(6), 
			ConversationStates.ATTENDING, 
			"Oh sorry, i don't rent signs to people who have so little experience like you.",
			null);

		npc.add(ConversationStates.ATTENDING, "rent", 
			new AndCondition(new LevelGreaterThanCondition(5), new NotCondition(new TextHasParameterCondition())), 
			ConversationStates.ATTENDING, 
			"Just tell me #rent followed by the text i should write on it.",
			null);

		npc.add(ConversationStates.ATTENDING, "rent", 
			new AndCondition(new LevelGreaterThanCondition(5), new TextHasParameterCondition()), 
			ConversationStates.BUY_PRICE_OFFERED, 
			"A sign costs " + MONEY + " money for 24 hours. Do you want to rent one?",
			new SpeakerNPC.ChatAction() {

				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					String temp = sentence.getOriginalText().trim();
					text = temp.substring(5).trim();
				}
			
		});
		
		npc.add(ConversationStates.BUY_PRICE_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			new NotCondition(new PlayerHasItemWithHimCondition("money", MONEY)),
			ConversationStates.ATTENDING,
			"Sorry, you do not have enough money", null);

		npc.add(ConversationStates.BUY_PRICE_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("money", MONEY),
			ConversationStates.IDLE, null,
			new SpeakerNPC.ChatAction() {

				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					RentedSign sign = new RentedSign(player, text);
					boolean success = rentedSignList.add(sign);
					if (success) {
						player.drop("money", MONEY);
						npc.say("OK, let me put your sign up.");
					} else {
						npc.say("Sorry, there are too many signs at the moment. I do not have a free spot left.");
					}
				}
		});

		npc.add(ConversationStates.BUY_PRICE_OFFERED, 
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"If you change your mind, just talk to me again.", null);

		npc.addGoodbye();
	}
}
