package games.stendhal.server.script;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * sets the welcome text players see on login.
 *
 * @author hendrik
 */
public class SetWelcomeText extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		if (args.isEmpty()) {
			admin.sendPrivateText(NotificationType.ERROR, "Argument missing.");
			return;
		}

		if (args.size() > 1) {
			admin.sendPrivateText(NotificationType.ERROR, "Too many arguments. Please use quotes.");
			return;
		}

		StendhalRPRuleProcessor.get().setWelcomeMessage(args.get(0));
		admin.sendPrivateText("Set welcome text to: " + args.get(0));
	}

	
}
