package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

/**
 * Makes client display a fake player name by changing the title attribute. 
 *If args[0] equals remove, the original name is reset. Can only be used to  *chage the name of the player running the script.
 *
 * @author timothyb89
 */
public class NameChange extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		if (args[0].equals("remove")) {
		admin.remove("title");
		admin.sendPrivateText("Your original name has been restored. Please change zones for the changes to take effect.");
		admin.update();
		admin.notifyWorldAboutChanges();
	} else {
		admin.put("title", args[0]);
		admin.sendPrivateText("Your name has been changed to " + admin.get("alternateTitle") + ".");
		admin.update();
		admin.notifyWorldAboutChanges();
	}
	}

}
