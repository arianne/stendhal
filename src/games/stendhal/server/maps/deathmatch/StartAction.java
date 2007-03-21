package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnNotifier;

import java.util.Date;

public class StartAction extends SpeakerNPC.ChatAction {
	private DeathmatchInfo deathmatchInfo;
	
	public StartAction(DeathmatchInfo deathmatchInfo) {
		this.deathmatchInfo = deathmatchInfo;
	}

	public void fire(Player player, String text, SpeakerNPC engine) {
		engine.say("Have fun!");
		int level = player.getLevel() - 2;
		if(level < 1) {
			level = 1;
		}
		player.setQuest("deathmatch", "start;"+ level + ";" + (new Date()).getTime());
		DeathmatchEngine scriptingAction = new DeathmatchEngine(player, deathmatchInfo);
		TurnNotifier.get().notifyInTurns(0, scriptingAction, null);
	}
}
