package games.stendhal.server.actions.admin;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.db.IDatabase;

public class BanAction extends AdministrationAction {

	@Override
	protected void perform(final Player player, final RPAction action) {
		if (action.has("target")) {
			String bannedName = action.get("target");
			
			IDatabase playerDatabase = SingletonRepository.getPlayerDatabase();
			
			try {
				playerDatabase.setAccountStatus(playerDatabase.getTransaction(), bannedName, "banned");
				player.sendPrivateText("you have banned " + bannedName);
				Logger.getLogger(BanAction.class).info(player.getName() + " has banned " + bannedName);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	public static void register() {
		CommandCenter.register("ban", new BanAction(), 5000);
	}
}
