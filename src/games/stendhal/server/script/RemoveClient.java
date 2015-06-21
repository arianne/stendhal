package games.stendhal.server.script;

import java.util.List;

import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

public class RemoveClient extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		PlayerEntry playerEntry = PlayerEntryContainer.getContainer().get(Integer.parseInt(args.get(0)));
		admin.sendPrivateText("playerEntry: " + playerEntry);
		
		PlayerEntryContainer.getContainer().remove(Integer.parseInt(args.get(0)));
	}

}
