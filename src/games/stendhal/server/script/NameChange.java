package games.stendhal.server.script;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

import marauroa.common.Configuration;

/**
 * Makes client display a fake player name by changing the title attribute. If
 * args[0] equals remove, the original name is reset. Can only be used to *chage
 * the name of the player running the script.
 * 
 * @author timothyb89
 */
public class NameChange extends ScriptImpl {
	private static final String CONFIG_KEY = "stendhal.scripts.namechange.enabled";

	@Override
	public void execute(Player admin, List<String> args) {
		// check configuration
		// TODO: Do this on load (allowing success/fail return value)?
		try {
			if (!Configuration.getConfiguration().has(CONFIG_KEY)
					|| !Boolean.parseBoolean(Configuration.getConfiguration().get(
							CONFIG_KEY))) {
				admin.sendPrivateText("This script is disabled in the server configuration file key "
						+ CONFIG_KEY);
				return;
			}
		} catch (Exception e) {
			admin.sendPrivateText(e.toString());
			return;
		}

		// do title change
		if (args.get(0).equals("remove")) {
			admin.setTitle(null);
			admin.sendPrivateText("Your original name has been restored. Please change zones for the changes to take effect.");
		} else {
			String title = args.get(0);

			admin.setTitle(title);
			admin.sendPrivateText("Your name has been changed to " + title
					+ ".");
		}

		admin.notifyWorldAboutChanges();
	}
}
