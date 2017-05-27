package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;

/**
 * removes an entry from the PlayerContainer with the specified clientid
 *
 * @author hendrik
 */
public class RemoveClient extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		if (args.size() != 1) {
			admin.sendPrivateText("Usage: /script RemoveClient.class <clientid>");
		}

		PlayerEntry playerEntry = PlayerEntryContainer.getContainer().get(Integer.parseInt(args.get(0)));
		admin.sendPrivateText("playerEntry: " + playerEntry);

		if (playerEntry != null) {
			PlayerEntryContainer.getContainer().remove(Integer.parseInt(args.get(0)));
		}
	}

}
