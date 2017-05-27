package games.stendhal.server.script;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import games.stendhal.common.MathHelper;
import games.stendhal.common.NotificationType;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * sets the path of an NPC
 *
 * @author hendrik
 */
public class SetNPCPath extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		if (args.size() != 2) {
			admin.sendPrivateText(NotificationType.ERROR, "/script SetPathNPC npc \"x1 y1 x2 y2 x3 y3\"");
			return;
		}
		SpeakerNPC npc = NPCList.get().get(args.get(0));
		List<Node> nodes = parsePath(args.get(1));
		if (!nodes.isEmpty()) {
			npc.setPath(new FixedPath(nodes, true));
		} else {
			npc.clearPath();
			npc.stop();
		}
	}

	/**
	 * parses the path
	 *
	 * @param pathString path as string
	 * @return Path
	 */
	private List<Node> parsePath(String pathString) {
		List<Node> nodes = new LinkedList<Node>();
		StringTokenizer st = new StringTokenizer(pathString);
		while (st.hasMoreElements()) {
			String x = st.nextToken();
			if (!st.hasMoreElements()) {
				break;
			}
			String y = st.nextToken();
			nodes.add(new Node(MathHelper.parseInt(x), MathHelper.parseInt(y)));
		}
		return nodes;
	}

}
