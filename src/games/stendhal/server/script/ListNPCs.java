/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

/**
 * lists all npcs and there position
 * 
 * @author hendrik
 */
public class ListNPCs extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);

		StringBuilder res = new StringBuilder();
		NPCList npcs = NPCList.get();
		for (String name : npcs.getNPCs()) {
			SpeakerNPC npc = npcs.get(name);
			res.append("\r\n" + name + "\t is in ");
			res.append(npc.getZone().getName() + " at (");
			res.append(npc.getX() + ", " + npc.getY() + ")");
		}
		admin.sendPrivateText(res.toString());
	}

}
