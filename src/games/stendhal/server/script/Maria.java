/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.core.scripting.ScriptingNPC;
import games.stendhal.server.core.scripting.ScriptingSandbox;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Creates a portable NPC which sell foods&drinks at meetings.
 * 
 * As admin use /script Maria.class to sommon her right next to you. Please put
 * her back in int_admin_playground after use.
 */
public class Maria extends ScriptImpl {

	private static Logger logger = Logger.getLogger(Maria.class);

	class MargaretCouponAction extends SpeakerNPC.ChatAction {

		private ScriptingSandbox sandbox;

		public MargaretCouponAction(ScriptingSandbox sandbox) {
			this.sandbox = sandbox;
		}

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			if (player.drop("coupon")) {
				Item beer = sandbox.getItem("beer");
				player.equip(beer, true);
				engine.say("Here is your free beer.");
				player.setQuest("MariaCoupon", "done");
			} else {
				engine.say("Sorry, you don't have a coupon. You can get one from Maria.");
			}
		}
	}

	@Override
	public void load(Player admin, List<String> args, ScriptingSandbox sandbox) {

		// Create NPC
		ScriptingNPC npc = new ScriptingNPC("Maria");
		npc.setEntityClass("tavernbarmaidnpc");

		// Place NPC in int_admin_playground on server start
		String myZone = "int_admin_playground";
		sandbox.setZone(myZone);
		int x = 11;
		int y = 4;

		// if this script is executed by an admin, Maria will be placed next to
		// him.
		if (admin != null) {
			sandbox.setZone(sandbox.getZone(admin));
			x = admin.getX() + 1;
			y = admin.getY();
		}

		// Set zone and position
		npc.setPosition(x, y);
		sandbox.add(npc);

		// Create Dialog
		npc.behave("greet", "Hi, how can i help you?");
		npc.behave(
				"job",
				"I am one of the bar maids at Semos' #tavern and doing outside services. We sell fine beers and food.");
		npc.behave("tavern", /*
								 * "I have a #coupon for a free beer in Semos'
								 * tavern. "+
								 */
		"It is on the left side of the temple.");
		npc.behave("help",
				"You can get an #offer of drinks and take a break to meet new people!");
		try {
			npc.behave("sell", ShopList.get().get("food&drinks"));
		} catch (NoSuchMethodException e) {
			logger.error(e, e);
		}

		// TODO Modify Margaret
		// game.getNPC("Margaret");
	}

}
