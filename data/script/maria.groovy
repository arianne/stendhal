/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;

/**
 * Creates a portable NPC which sell foods&drinks at meetings.
 *
 * As admin use /script maria.groovy to sommon her right next to you.
 * Please put her back in int_admin_playground after use.
 */

class MariaCouponAction extends SpeakerNPC.ChatAction {
  private StendhalGroovyScript game;
  public MariaCouponAction ( StendhalGroovyScript game) {
    this.game = game;
  }
  public void fire(Player player, String text, SpeakerNPC engine) {
      if (player.getQuest("MariaCoupon") == null) {
          Item item = game.getItem("coupon");
          item.setDescription("Coupon for one free beer at Semos' tavern.");
          player.equip(item, true);
          engine.say("Give this coupon to Margaret in Semos' Tavern to get a free beer.");
          player.setQuest("MariaCoupon", "coupon");
      } else {
          engine.say("Sorry, only one free beer per person.");
      }
  }
}

class MargaretCouponAction extends SpeakerNPC.ChatAction {
    private StendhalGroovyScript game;
    public MargaretCouponAction ( StendhalGroovyScript game) {
      this.game = game;
    }
    public void fire(Player player, String text, SpeakerNPC engine) {
        Item coupon = player.getEquipped("coupon");
        if (coupon != null) {
            Item beer = game.getItem("beer");
            player.drop(coupon);
            player.equip(beer, true);
            engine.say("Here is your free beer.");
            player.setQuest("MariaCoupon", "done");
        } else {
            engine.say("Sorry, you don't have a coupon. You can get one from Maria.");
        }
    }
  }

//public class Maria {



	// Create NPC
	npc=new ScriptingNPC("Maria");
	npc.setClass("tavernbarmaidnpc");

	// Place NPC in int_admin_playground on server start
	myZone = "int_admin_playground";
	x = 11;
	y = 4;

    // if this script is executed by an admin, Maria will be placed next to him.
	if (player != null) {
		myZone = game.getZone(player);
		x = player.getx() + 1;
		y = player.gety();
	}

    // Set zone and position
	game.setZone(myZone);
	npc.set(x, y);
	game.add(npc)

	// Create Dialog
	npc.behave("greet", "Hi, how can i help you?");
	npc.behave("job","I am one of the bar maids at Semos' #tavern and doing outside services. We sell fine beers and food.");
    npc.behave("tavern", "I have a #coupon for a free beer in Semos' tavern. It is on the left side of the temple.");
	npc.behave("help", "You can get an #offer of drinks and take a break to meet new people!");
	npc.behave("sell", ShopList.get().get("food&drinks"));
//    npc.add(1, "coupon", null, 1, null, new MariaCouponAction(game));

    // Modify Margaret
   // game.getNPC("Margaret");