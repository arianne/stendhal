package games.stendhal.server.script;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * drop the specified amount of items from the player.
 * 
 * @author hendrik
 */
public class DropPlayerItems extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		if (args.size() < 2) {
			admin.sendPrivateText("<player> [<amount>] '<item>'");
			return;
		}

		final Player player = SingletonRepository.getRuleProcessor().getPlayer(args.get(0));
		String itemName = null;
		int amount = 1;

		if (args.size() == 3) {
			amount = Integer.parseInt(args.get(1));
			itemName = args.get(2);
		} else {
			itemName = args.get(1);
		}

		final String singularItemName = Grammar.singular(itemName);

		boolean res = player.drop(itemName, amount);

		if (!res && !itemName.equals(singularItemName)) {
			res = player.drop(singularItemName, amount);
		}

		final String msg = "Admin " + admin.getTitle() + " removed " + amount + " "
				+ Grammar.plnoun(amount, singularItemName) + " from player "
				+ player.getTitle() + ": " + res;

		admin.sendPrivateText(msg);

		if (res) {
			player.sendPrivateText(msg);
			SingletonRepository.getRuleProcessor().addGameEvent(admin.getName(),
					"admindrop", player.getName(), Integer.toString(amount),
					itemName);
		}
	}
}
