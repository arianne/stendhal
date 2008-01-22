/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * Lists all npcs and there position.
 * 
 * @author hendrik
 */
public class ListNPCs extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);

		StringBuilder res = new StringBuilder();
		NPCList npcs = SingletonRepository.getNPCList();
		for (String name : npcs.getNPCs()) {
			SpeakerNPC npc = npcs.get(name);
			res.append("\r\n" + name + "\t is in ");
			res.append(npc.getZone().getName() + " at (");
			res.append(npc.getX() + ", " + npc.getY() + ")");
		}
		admin.sendPrivateText(res.toString());
	}

}
