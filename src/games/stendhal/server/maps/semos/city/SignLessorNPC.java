package games.stendhal.server.maps.semos.city;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.entity.mapstuff.office.RentedSignList;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.action.RemoveStoreableEntityAction;
import games.stendhal.server.entity.npc.condition.AdminCondition;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasStoreableEntityCondition;
import games.stendhal.server.entity.npc.condition.TextHasParameterCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.StringUtils;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * A merchant (original name: Gordon) who rents signs to players.
 *
 * The player has to have at least level 5 to prevent abuse by newly created characters.
 */
public class SignLessorNPC extends SpeakerNPCFactory {
	protected String text;
	// 1 min at 300 ms/turn
	private static final int ONE_MINUTE = 180;
	private static final int MONEY = 100; 
	protected RentedSignList rentedSignList;

	public SignLessorNPC() {
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_city");
		Shape shape = new Rectangle(21, 48, 17, 1);
		rentedSignList = new RentedSignList(zone, shape);
	}

	@Override
	public void createDialog(SpeakerNPC npc) {
		npc.addGreeting("Hi, I #rent signs and #remove outdated ones.");
		npc.addJob("I #rent signs for a day.");
		npc.addHelp("If you want to #rent a sign, just tell me what I should write on it.");
		npc.setPlayerChatTimeout(ONE_MINUTE);
		
		npc.add(ConversationStates.ATTENDING, "rent", 
			new LevelLessThanCondition(6), 
			ConversationStates.ATTENDING, 
			"Oh sorry, I don't rent signs to people who have so little experience as you.",
			null);

		npc.add(ConversationStates.ATTENDING, "rent", 
			new AndCondition(new LevelGreaterThanCondition(5), new NotCondition(new TextHasParameterCondition())), 
			ConversationStates.ATTENDING, 
			"Just tell me #rent followed by the text I should write on it.",
			null);

		npc.add(ConversationStates.ATTENDING, "rent", 
			new AndCondition(new LevelGreaterThanCondition(5), new TextHasParameterCondition()), 
			ConversationStates.BUY_PRICE_OFFERED, 
			null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					String temp = sentence.getOriginalText().trim();
					text = temp.substring(5).trim();
					
					String reply = "A sign costs " + MONEY + " money for 24 hours. Do you want to rent one?";
					if (rentedSignList.getByName(player.getName()) != null) {
						reply = reply + " Please note that I will replace the sign you already rented.";
					}

					npc.say(reply);
				}

				@Override
				public String toString() {
					return "remember text";
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

					if (text.length() > 1000) {
						text = text.substring(1000);
					}
					
					// do not accept all uper case
					if (StringUtils.countLowerCase(text) < StringUtils.countUpperCase(text) * 2) {
						text = text.toLowerCase();
					}

					// put the sign up
					rentedSignList.removeByName(player.getName());
					RentedSign sign = new RentedSign(player, text);
					boolean success = rentedSignList.add(sign);

					// confirm, log, tell postman
					if (success) {
						player.drop("money", MONEY);
						npc.say("OK, let me put your sign up.");

						// inform irc using postman
						Player postman = SingletonRepository.getRuleProcessor().getPlayer("postman");
						if (postman != null) {
							postman.sendPrivateText(player.getName() + " rented a sign saying \"" + text + "\"");
						}
						SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "sign", "rent", text);
					} else {
						npc.say("Sorry, there are too many signs at the moment. I do not have a free spot left.");
					}
				}

				@Override
				public String toString() {
					return "put up sign";
				}
		});

		npc.add(ConversationStates.BUY_PRICE_OFFERED, 
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"If you change your mind, just talk to me again.", null);

		npc.add(ConversationStates.ATTENDING, "remove", 
			new PlayerHasStoreableEntityCondition(rentedSignList),
			ConversationStates.ATTENDING,
			"Ok, I am going to remove your sign.",
			new RemoveStoreableEntityAction(rentedSignList));

		npc.add(ConversationStates.ATTENDING, "remove", 
			new NotCondition(new PlayerHasStoreableEntityCondition(rentedSignList)),
			ConversationStates.ATTENDING,
			"You did not rent any sign, so i cannot remove one.", null);

		// admins may remove signs (even low level admins)
		npc.add(ConversationStates.ATTENDING, "delete", 
			new AdminCondition(100),
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {

				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					String original = sentence.getOriginalText().trim();
					int pos = original.indexOf(" ");
					if (pos < 0) {
						npc.say("Syntax: delete <nameofplayer>");
						return;
					}
					String playerName = original.substring(pos + 1).trim();
					rentedSignList.removeByName(playerName);
					String message = player.getName() + " deleted sign from " + playerName;
					StendhalRPRuleProcessor.sendMessageToSupporters("SignLessorNPC", message);
					SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "sign", "deleted", playerName);
				}

				@Override
				public String toString() {
					return "admin delete sign";
				}
		});

		npc.addGoodbye();
	}
}
