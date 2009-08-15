package games.stendhal.server.script;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.GameEvent;
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

		if (args.size() > 3) {
			admin.sendPrivateText("<player> [<amount>] '<item>' - and don't forget those quotes if the item name has spaces");
			return;
		}

		final Player player = SingletonRepository.getRuleProcessor().getPlayer(args.get(0));
		String itemName = null;
		int amount = 1;

		if (args.size() == 3) {
			try {
				amount = Integer.parseInt(args.get(1));
				itemName = args.get(2);
			} catch (final NumberFormatException e) {
				// admin did something like "playername black shield" (i.e. an
				// item with spaces, but didnt use quotes, or a number)
				// catch the exception and see if we can help them anyway
				// amount = 1; is default
				itemName = args.get(1) + " " + args.get(2);
			}
		} else {
			itemName = args.get(1);
		}

		final String singularItemName = Grammar.singular(itemName);

		boolean result = player.drop(itemName, amount);

		if (!result && !itemName.equals(singularItemName)) {
			result = player.drop(singularItemName, amount);
		}

		final String msg = "Admin " + admin.getName() + " removed " + amount
				+ " " + Grammar.plnoun(amount, singularItemName)
				+ " from player " + player.getName() + ": #" + result;

		admin.sendPrivateText(msg);

		if (result) {
			player.sendPrivateText(msg);
			SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper", msg);
			new GameEvent(admin.getName(), "admindrop", player.getName(),
					Integer.toString(amount), itemName).raise();
		}
	}
}
