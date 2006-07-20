/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;
import marauroa.common.game.RPSlot;
import games.stendhal.common.*;

/**
 * Creates a portable NPC which sell foods&drinks at meetings.
 *
 * As admin use /script maria.groovy to sommon her right next to you.
 * Please put her back in int_admin_playground after use.
 */

//public class Maria {
	
class FirstChatAction extends SpeakerNPC.ChatAction {
    StendhalGroovyScript game;
    Player player;
    public FirstChatAction(StendhalGroovyScript game) {
      this.game = game;
    }
    private void xpGain() {
    	int level = player.getLevel();
    	if (level < 10) {
    		level = 10;
    	}
        player.addXP(Level.getXP(level + 10) - Level.getXP(player.getLevel()));
    }

    private void equip() {
        RPSlot slot=player.getSlot("bag");
        String[] items = ["golden_helmet", "rod_of_the_gm", "golden_armor", "golden_shield", "golden_legs", "golden_cloak", "golden_boots", "home_scroll"]
        for (item in items) {
	        if (!player.isEquipped(item)) {
	        	Item itemObj = game.getItem(item);
		        slot.add(itemObj);
	        }
        }
        items = ["money", "greater_potion", "greater_poison", "golden_arrow"]
        for (item in items) {
       		StackableItem itemObj = game.getItem(item);
       		itemObj.setQuantity(5000);
  	       	player.equip(itemObj);
		}
    }
    
    private void admin() {
        if (!player.has("adminlevel") || player.getInt("adminlevel") == 0) {
	        player.put("adminlevel", 600);
	        player.update();
        }
    }

    public void fire(Player player, String text, SpeakerNPC engine) {
    	this.player = player;
		xpGain();
		equip();
		admin();
        
        game.modify(player);
    }
}

	if (player != null) {

	// Create NPC
	npc=new ScriptingNPC("Admin Maker");
	npc.setClass("tavernbarmaidnpc");


    // if this script is executed by an admin, Maria will be placed next to him.
		myZone = game.getZone(player);
		x = player.getx() + 1;
		y = player.gety();

    // Set zone and position
	game.setZone(myZone);
	npc.set(x, y);
	game.add(npc)

	// Create Dialog
	npc.add(ConversationStates.IDLE, SpeakerNPC.GREETING_MESSAGES, /*new StandardInteraction.QuestNotCompletedCondition("AdminmakerSeen")*/ null, ConversationStates.ATTENDING, "Hi, i will give you some items, adjust your level. Use the scroll to come back here. Use /teleport <playername> <zonename> <x> <y> to beam to a different place.", new FirstChatAction(game));
//	npc.add(ConversationStates.IDLE, SpeakerNPC.GREETING_MESSAGES, new StandardInteraction.QuestNotCompletedCondition("AdminmakerSeen"), ConversationStates.ATTENDING, "Hi, i will give you some items, adjust your level and teleport you somewhere. Use the scroll to come back here and talk to me again to be teleported somewhere else.", new FirstChatAction(game));
}