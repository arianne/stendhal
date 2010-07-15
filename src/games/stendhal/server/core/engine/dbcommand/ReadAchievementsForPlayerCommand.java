package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.entity.player.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

public class ReadAchievementsForPlayerCommand extends AbstractDBCommand {
	
	private Set<String> identifiers = new HashSet<String>();
	private final Player player;

	/**
	 * @param player the player whose achievements should be read
	 */
	public ReadAchievementsForPlayerCommand(Player player) {
		this.player = player;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		identifiers = DAORegister.get().get(AchievementDAO.class).loadAllReachedAchievementsOfPlayer(getPlayer().getName(), transaction);
	}

	public Set<String> getIdentifiers() {
		return identifiers;
	}

	public Player getPlayer() {
		return player;
	}

}
