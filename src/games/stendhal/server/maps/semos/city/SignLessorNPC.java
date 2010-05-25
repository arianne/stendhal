package games.stendhal.server.maps.semos.city;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.entity.mapstuff.office.RentedSignList;
import games.stendhal.server.entity.npc.ChatAction;
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
//TODO: take NPC definition elements which are currently in XML and include here
public class SignLessorNPC extends SpeakerNPCFactory {
	protected String text;

	// 1 min at 300 ms/turn
	private static final int ONE_MINUTE = 180;
	private static final int MONEY = 100; 
	protected RentedSignList rentedSignList;

	public SignLessorNPC() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_city");
		final Shape shape = new Rectangle(21, 48, 17, 1);
		rentedSignList = new RentedSignList(zone, shape);
	}

	@Override
	public void createDialog(final SpeakerNPC npc) {
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
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					text = sentence.getOriginalText().trim().substring(5).trim();

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
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					if (text.length() > 1000) {
						text = text.substring(1000);
					}

					// do not accept all upper case
					if (StringUtils.countLowerCase(text) < StringUtils.countUpperCase(text) * 2) {
						text = text.toLowerCase();
					}

					// put the sign up
					rentedSignList.removeByName(player.getName());
					final RentedSign sign = new RentedSign(player, text);
					final boolean success = rentedSignList.add(sign);

					// confirm, log, tell postman
					if (success) {
						player.drop("money", MONEY);
						npc.say("OK, let me put your sign up.");

						// inform IRC using postman
						final Player postman = SingletonRepository.getRuleProcessor().getPlayer("postman");
						if (postman != null) {
							postman.sendPrivateText(player.getName() + " rented a sign saying \"" + text + "\"");
						}
						new GameEvent(player.getName(), "sign", "rent", text).raise();
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
			"You did not rent any sign, so I cannot remove one.", null);

		// admins may remove signs (even low level admins)
		npc.add(ConversationStates.ATTENDING, "delete", 
			new AdminCondition(100),
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					if (sentence.getExpressions().size() < 2) {
						npc.say("Syntax: delete <nameofplayer>");
						return;
					}
					final String playerName = sentence.getOriginalText().substring("delete ".length()).trim();
					if (rentedSignList.removeByName(playerName)) {
						final String message = player.getName() + " deleted sign from " + playerName;
						SingletonRepository.getRuleProcessor().sendMessageToSupporters("SignLessorNPC", message);
						new GameEvent(player.getName(), "sign", "deleted", playerName).raise();
					} else {
						player.sendPrivateText("I could not find a sign by " + playerName);
					}
				}

				@Override
				public String toString() {
					return "admin delete sign";
				}
		});

		npc.addGoodbye();
	}
}
