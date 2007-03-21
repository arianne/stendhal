package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Date;

public class BailAction extends SpeakerNPC.ChatAction {
	public void fire(Player player, String text, SpeakerNPC engine) {
		String questInfo = player.getQuest("deathmatch");
		if (questInfo == null) {
				engine.say("Coward, you haven't even #started!");
				return;
		}
		String[] tokens = (questInfo+";0;0").split(";");
		String questState = tokens[0];
		String questLevel = tokens[1];
		if(!"start".equals(questState)) {
			engine.say("Coward, we haven't even #started!");
			return;
		}
		player.setQuest("deathmatch", "bail;"+ questLevel + ";" + (new Date()).getTime());
		// We assume that the player only carries one trophy helmet.
		Item helmet	= player.getFirstEquipped("trophy_helmet");
		if(helmet != null) {
			engine.say("Coward! I'm sorry to inform you, for this your helmet has been magically weakened.");
		}
		else {
			engine.say("Coward! You're not as experienced as you used to be.");
		}
		return;
	}
}
