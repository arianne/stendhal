package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import marauroa.server.game.db.Accessor;
import marauroa.server.game.db.IDatabase;
import marauroa.server.game.db.Transaction;

/**
 * Lists all word list entries in the database with missing type information.
 * 
 * @author M. Fuchs
 */
public class ListUnknownWords extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		final IDatabase db = SingletonRepository.getPlayerDatabase();
		final Transaction trans = db.getTransaction();
		final Accessor acc = trans.getAccessor();

		final StringBuilder sb = new StringBuilder("Currently unknown words:\n");

		try {
	        final ResultSet res = acc.query(
        		"select normalized\n"
	        		+ "from	words w\n"
	        		+ "where type = ''\n"
	        		+ "order by normalized"
	        );

	        while (res.next()) {
	        	sb.append(res.getString(1));
	        	sb.append('\n');
	        }

			trans.commit();
        } catch (final SQLException e) {
        	sb.append("error while reading from DB table words: " + e.getMessage());

	        try {
	            trans.rollback();
            } catch (final SQLException e1) {
    	        sb.append("error while rolling back transaction: " + e.getMessage());
            }
        }

		admin.sendPrivateText(sb.toString());
	}
}
