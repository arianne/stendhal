package games.stendhal.server.script;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

/**
 * drop the specified amount of items from the player.
 * 
 * @author hendrik
 */
public class DropPlayerItems extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		if (args.size() < 2) {
			admin.sendPrivateText("<player> [<amount>] <item>");
			return;
		}
		Player player = StendhalRPRuleProcessor.get().getPlayer(args.get(0));
		String itemName = null;
		int amount = 1;
		if (args.size() == 3) {
			amount = Integer.parseInt(args.get(1));
			itemName = args.get(2);
		} else {
			itemName = args.get(1);
		}

		boolean res = player.drop(itemName, amount);
		String msg = "Admin " + admin.getTitle() + " removed " + amount + " "
				+ Grammar.plnoun(amount, itemName) + " from player "
				+ player.getTitle() + ": " + res;
		admin.sendPrivateText(msg);
		if (res) {
			player.sendPrivateText(msg);
			StendhalRPRuleProcessor.get().addGameEvent(admin.getName(),
					"admindrop", player.getName(), Integer.toString(amount),
					itemName);
		}
	}
}
