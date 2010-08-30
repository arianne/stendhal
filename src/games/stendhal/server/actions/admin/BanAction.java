package games.stendhal.server.actions.admin;

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import marauroa.common.game.RPAction;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.CharacterDAO;
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
			int hours = 1;
			
			try {
				hours = Integer.parseInt(action.get("hours"));
			} catch (final NumberFormatException e) {
				player.sendPrivateText(NotificationType.ERROR, "Please ban for a whole number of hours, or -1 hours for a permanent ban. Shorter times than 1 hour can use /jail.");
				return; 
			}
			
			try {

				// look up username
				String username = DAORegister.get().get(CharacterDAO.class).getAccountName(bannedName);
				if (username == null) {
					player.sendPrivateText(NotificationType.ERROR, "No such character");
					return;
				}

				// parse expire
				Timestamp expire = null;
				String expireStr = "end of time";
				if (hours > 0) {
					Calendar date = new GregorianCalendar();
					date.add(Calendar.HOUR, hours);
					expire = new Timestamp(date.getTimeInMillis());
					expireStr = expire.toString();
				}

				DAORegister.get().get(AccountDAO.class).addBan(username, reason, expire);
				player.sendPrivateText("You have banned account " + username + " (character: " + bannedName + ") until " + expireStr + " for: " + reason);

				// logging
				logger.info(player.getName() + " has banned  account " + username + " (character: " + bannedName + ") until " + expireStr + " for: " + reason);
				new GameEvent(player.getName(), "ban",  bannedName, expireStr, reason).raise();
				
				SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper",
						player.getName() + " banned account " + username + " (character: " + bannedName + ") until " + expireStr
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
