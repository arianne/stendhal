package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Deep inspects a player and all his/her items.
 * 
 * @author hendrik
 */
public class DeepInspect extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		if (args.size() == 0) {
			admin.sendPrivateText("Need player name as parameter.");
			return;
		}
		Player player = SingletonRepository.getRuleProcessor().getPlayer(args.get(0));
		StringBuffer sb = new StringBuffer();
		sb.append("Inspecting " + player.getName() + "\n");

		// inspect slots
		for (RPSlot slot : player.slots()) {
			// don't return buddy-list for privacy reasons
			if (slot.getName().equals("!buddy")
					|| slot.getName().equals("!ignore")) {
				continue;
			}
			sb.append("\nSlot " + slot.getName() + ": \n");

			// list objects
			for (RPObject object : slot) {
				sb.append("   " + object + "\n");
			}
		}

		admin.sendPrivateText(sb.toString());
	}
}
