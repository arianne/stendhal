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
	public void execute(Player admin, List<String> args) {
		IDatabase db = SingletonRepository.getPlayerDatabase();
		Transaction trans = db.getTransaction();
		Accessor acc = trans.getAccessor();

		StringBuffer sb = new StringBuffer("Currently unknown words:\n");

		try {
	        ResultSet res = acc.query(
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
        } catch (SQLException e) {
        	sb.append("error while reading from DB table words: " + e.getMessage());

	        try {
	            trans.rollback();
            } catch (SQLException e1) {
    	        sb.append("error while rolling back transaction: " + e.getMessage());
            }
        }

		admin.sendPrivateText(sb.toString());
	}
}
