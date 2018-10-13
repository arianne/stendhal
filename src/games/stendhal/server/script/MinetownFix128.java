package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

public class MinetownFix128 extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		StendhalRPZone int_magic_clothing_boutique = StendhalRPWorld.get().getZone("int_magic_clothing_boutique");
		StendhalRPZone semos = StendhalRPWorld.get().getZone("0_semos_city");
		
		SpeakerNPC speakerNPC1 = NPCList.get().get("Liliana");
		SpeakerNPC speakerNPC2 = NPCList.get().get("Saskia");
		
		int_magic_clothing_boutique.remove(speakerNPC1);
		int_magic_clothing_boutique.remove(speakerNPC2);
		
		semos.add(speakerNPC1);
		semos.add(speakerNPC2);
		
	}

	
}
