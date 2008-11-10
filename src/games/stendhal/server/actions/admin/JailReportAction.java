package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;

public class JailReportAction extends AdministrationAction {
	private static final String JAILREPORT = "jailreport";

	public static void register() {
		CommandCenter.register(JAILREPORT, new JailReportAction(), 50);

	}

	@Override
	protected void perform(final Player player, final RPAction action) {
		final Jail jail = Jail.get();
		final String playerName = action.get(TARGET);
		
		if (playerName != null) {
			final ArrestWarrant warrant = jail.getWarrant(playerName);
			
			if (warrant != null) {
				player.sendPrivateText(warrant.getCriminal() + ": " 
						+ warrant.getMinutes() + " Minutes because: "
						+ warrant.getReason());
			} else {
				player.sendPrivateText(playerName + " is not in jail");
			}
		} else {
			player.sendPrivateText(jail.listJailed());
		}
		
		player.notifyWorldAboutChanges();
	}

}
