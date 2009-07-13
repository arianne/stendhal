package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

/**
 * Lists all word list entries in the database with missing type information.
 * 
 * @author M. Fuchs
 */
public class ListUnknownWords extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		final DBTransaction transaction = TransactionPool.get().beginWork();

		final StringBuilder sb = new StringBuilder("Currently unknown words:\n");

		try {
	        final ResultSet res = transaction.query(
        		"SELECT normalized FROM words w"
	        		+ " WHERE type = '' ORDER BY normalized", null);

	        while (res.next()) {
	        	sb.append(res.getString(1));
	        	sb.append('\n');
	        }

			TransactionPool.get().commit(transaction);
        } catch (final SQLException e) {
        	sb.append("error while reading from DB table words: " + e.getMessage());
			TransactionPool.get().rollback(transaction);
        }

		admin.sendPrivateText(sb.toString());
	}
}
