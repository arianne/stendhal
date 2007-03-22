package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * handle the players request to leave the deathmatch
 * (if it is allowed in the current state).
 *
 * @author hendrik
 */
public class LeaveAction extends SpeakerNPC.ChatAction {

	@Override
	public void fire(Player player, String text, SpeakerNPC engine) {	 
		if("done".equals(player.getQuest("deathmatch"))) {
			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_semos_plains_n");
			player.teleport(zone, 100, 115, null, player);				
		} else {
			engine.say("I don't think you claimed your #victory yet.");
		}
		return;
	}
}