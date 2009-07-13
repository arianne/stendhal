package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;

import marauroa.common.game.RPAction;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;

import org.apache.log4j.Logger;

public class BanAction extends AdministrationAction {
	private static Logger logger = Logger.getLogger(BanAction.class);

	@Override
	protected void perform(final Player player, final RPAction action) {
		if (action.has("target")) {
			String bannedName = action.get("target");
			String reason = "";
			if (action.has("reason")) {
				reason = action.get("reason");
			}
			
			try {
				DAORegister.get().get(AccountDAO.class).setAccountStatus(bannedName, "banned");
				player.sendPrivateText("You have banned " + bannedName + " for: " + reason);

				// logging
				Logger.getLogger(BanAction.class).info(player.getName() + " has banned " + bannedName + " for: " + reason);
				new GameEvent(player.getName(), "ban",  bannedName, reason).raise();				
				
				SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper",
						player.getName() + " banned " + bannedName
						+ ". Reason: " + reason	+ ".");
			} catch (SQLException e) {
				logger.error("Error while trying to ban user", e);
			}
		}

	}
	public static void register() {
		CommandCenter.register("ban", new BanAction(), 1000);
	}
}
