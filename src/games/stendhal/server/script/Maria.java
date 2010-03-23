/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.core.scripting.ScriptingNPC;
import games.stendhal.server.core.scripting.ScriptingSandbox;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Creates a portable NPC which sell foods&drinks, or optionally items from any other shop, 
 * at meetings.
 * 
 * As admin use /script Maria.class to summon her right next to you. Please put
 * her back in int_admin_playground after use.
 */
public class Maria extends ScriptImpl {

	private static Logger logger = Logger.getLogger(Maria.class);

	class MargaretCouponAction implements ChatAction {

		private final ScriptingSandbox sandbox;

		public MargaretCouponAction(final ScriptingSandbox sandbox) {
			this.sandbox = sandbox;
		}

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			if (player.drop("coupon")) {
				final Item beer = sandbox.getItem("beer");
				player.equipOrPutOnGround(beer);
				engine.say("Here is your free beer.");
				player.setQuest("MariaCoupon", "done");
			} else {
				engine.say("Sorry, you don't have a coupon. You can get one from Maria.");
			}
		}
	}

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

		// TODO Modify Margaret
		// game.getNPC("Margaret");
	}

}
