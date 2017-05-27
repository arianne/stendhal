/* $Id$ */
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.TransactionPool;

/**
 * Dumps the currently open database transactiosn to the log file.
 *
 * @author hendrik
 */
public class DumpOpenDBTransactions extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() > 0) {
			admin.sendPrivateText("/script DumpOpenDBTransactions");
			return;
		}

		TransactionPool.get().dumpOpenTransactions();
		admin.sendPrivateText("Transactions have been dumped to the log file.");
	}

}
