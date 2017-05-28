/* $Id$ */
package games.stendhal.server.script;

import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.core.scripting.ScriptingNPC;
import games.stendhal.server.core.scripting.ScriptingSandbox;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.JailAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NakedCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Creates a portable NPC which sell foods&drinks, or optionally items from any other shop,
 * at meetings.
 *
 * As admin use /script Maria.class to summon her right next to you. Please put
 * her back in int_admin_playground after use.
 */
public class Maria extends ScriptImpl {

	private static Logger logger = Logger.getLogger(Maria.class);
	private static final String QUEST_SLOT = "Ketteh";
	private static final int GRACE_PERIOD = 1;
	private static final int JAIL_TIME = 10;


//	private static final class MargaretCouponAction implements ChatAction {
//
//		private final ScriptingSandbox sandbox;
//
//		public MargaretCouponAction(final ScriptingSandbox sandbox) {
//			this.sandbox = sandbox;
//		}
//
//		public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
//			if (player.drop("coupon")) {
//				final Item beer = sandbox.getItem("beer");
//				player.equipOrPutOnGround(beer);
//				raiser.say("Here is your free beer.");
//				player.setQuest("MariaCoupon", "done");
//			} else {
//				raiser.say("Sorry, you don't have a coupon. You can get one from Maria.");
//			}
//		}
//	}

	@Override
	public void load(final Player admin, final List<String> args, final ScriptingSandbox sandbox) {

		// Create NPC
		final ScriptingNPC npc = new ScriptingNPC("Maria");
		npc.setEntityClass("tavernbarmaidnpc");

		// Place NPC in int_admin_playground on server start
		final String myZone = "int_admin_playground";
		sandbox.setZone(myZone);
		int x = 11;
		int y = 4;
		String shop = "food&drinks";
		final ShopList shops = SingletonRepository.getShopList();
		if (args.size() > 0 ) {
			if (shops.get(args.get(0))!= null) {
				shop = args.get(0);
			} else {
				admin.sendPrivateText(args.get(0)
						+ " not recognised as a shop name. Using default food&drinks");
			}
		}
		// If this script is executed by an admin, Maria will be placed next to him.
		if (admin != null) {
			sandbox.setZone(sandbox.getZone(admin));
			x = admin.getX() + 1;
			y = admin.getY();
		}

		// Set zone and position
		npc.setPosition(x, y);
		sandbox.add(npc);

		// Create Dialog
		npc.behave("greet", "Hi, how can I help you?");
		npc.behave(
				"job",
				"I am one of the bar maids at Semos' #tavern and doing outside services. We sell fine beers and food.");
		npc.behave("tavern",
//			"I have a #coupon for a free beer in Semos' tavern. "+
			"It is on the left side of the temple.");
		npc.behave("help",
				"You can see what I #offer and take a break to meet new people!");
		npc.behave("bye", "Bye bye!");
		try {
			npc.behave("sell", SingletonRepository.getShopList().get(shop));
		} catch (final NoSuchMethodException e) {
			logger.error(e, e);
		}

		// COPY AND PASTED CODE AHEAD from MeetKetteh

		// force Ketteh to notice naked players that she has already warned
			// but leave a 5 minute (or GRACE_PERIOD) gap if she only just warned them
			npc.addInitChatMessage(
					new AndCondition(
							new NakedCondition(),
							new OrCondition(
									new AndCondition(
											new QuestInStateCondition(QUEST_SLOT, 0,"seen_naked"),
											new TimePassedCondition(QUEST_SLOT,1,GRACE_PERIOD)),
							        new QuestInStateCondition(QUEST_SLOT,"seen"),
							        new QuestInStateCondition(QUEST_SLOT,"learnt_manners"),
							        // done was an old state that was used when naked but then clothed,
							        // but they should do learnt_manners too
							        new QuestInStateCondition(QUEST_SLOT,"done"))),
					new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						((SpeakerNPC) raiser.getEntity()).listenTo(player, "hi");
					}
			});

			// player is naked but may not have been warned recently, warn them and stamp the quest slot
			// this can be initiated by the npc as above
			npc.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
							new NakedCondition(),
							new QuestNotInStateCondition(QUEST_SLOT, 0,"seen_naked")),
					ConversationStates.ATTENDING,
					"Who are you? Aiiieeeee!!! You're naked! Quickly, right-click on yourself and choose SET OUTFIT! If you don't I'll call the guards!",
					new MultipleActions(
							new SetQuestAction(QUEST_SLOT,0, "seen_naked"),
							new SetQuestToTimeStampAction(QUEST_SLOT,1)));

			// player is naked and has been warned,
			// they started another conversation or the init chat message prompted this interaction as above
			npc.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
							new NakedCondition(),
							new QuestInStateCondition(QUEST_SLOT, 0, "seen_naked")),
					ConversationStates.ATTENDING,
					// this message doesn't get seen by the player himself as he gets sent to jail, but it would explain to bystanders why he is gone
					"Ugh, you STILL haven't put any clothes on. To jail for you!",
					// Jail the player
					new MultipleActions(
							new SetQuestAction(QUEST_SLOT,0, "seen_naked"),
							new SetQuestToTimeStampAction(QUEST_SLOT,1),
							new JailAction(JAIL_TIME, "Maria jailed you for being naked in public!")));
	}

}
