/* $Id$
 * $Log$
 */
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 *
 * @author hendrik
 */
public class TeleportNPC extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		if (args.size() != 4) {
			admin.sendPrivateText(NotificationType.ERROR, "/script TeleportNPC npc zone x y");
			return;
		}
		StendhalRPZone zone = StendhalRPWorld.get().getZone(args.get(1));
		if (zone == null) {
			admin.sendPrivateText(NotificationType.ERROR, "Zone " + args.get(1) + " does not exist.");
			return;
		}
		int x = MathHelper.parseInt(args.get(2));
		int y = MathHelper.parseInt(args.get(3));

		SpeakerNPC npc = NPCList.get().get(args.get(0));
		npc.getZone().remove(npc);
		zone.add(npc);
		npc.setPosition(x, y);
	}

}
