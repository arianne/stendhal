package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.log4j.Logger;

public class OfflineAdminlevel extends ScriptImpl {
	private static Logger logger = Logger.getLogger(OfflineAdminlevel.class);

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);
		
		// This code leaves the player as ghost in the world
		
		/*

		// validate and read parameters
		if (args.size() != 2) {
			admin.sendPrivateText("/script OfflineAdminlevel.class <playername> <newlevel>");
			return;
		}
		String playerName = args.get(0);
		String newLevel = args.get(1);

		// check that player is offline
		if (StendhalRPRuleProcessor.get().getPlayer(playerName) != null) {
			admin.sendPrivateText("This player is currently online. Please use the normal /adminlevel command");
			return;
		}

		// start a transaction
		CharacterDAO characterDAO = DAORegister.get().get(CharacterDAO.class);
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {

			// check that the player exists
			if (!characterDAO.hasCharacter(playerName, playerName)) {
				admin.sendPrivateText("No player with that name.");
				TransactionPool.get().commit(transaction);
				return;
			}

			RPObject object = characterDAO.loadCharacter(transaction, playerName, playerName);


			// do the modifications here
			object.put("adminlevel", Integer.parseInt(newLevel));


			// safe it back
			characterDAO.storeCharacter(transaction, playerName, playerName, object);
			TransactionPool.get().commit(transaction);

			// log game event
			new GameEvent(admin.getName(), ADMINLEVEL, playerName, ADMINLEVEL, newLevel).raise();
		} catch (Exception e) {
			logger.error(e, e);
			admin.sendPrivateText(e.toString());
			TransactionPool.get().rollback(transaction);
		}*/
	}
}
