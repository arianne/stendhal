package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class JailReportAction extends AdministrationAction {
	private static final String JAILREPORT = "jailreport";

	public static void register() {
		CommandCenter.register(JAILREPORT, new JailReportAction(), 400);

	}

	@Override
	protected void perform(final Player player, final RPAction action) {
		player.sendPrivateText(Jail.get().listJailed());
		player.notifyWorldAboutChanges();

	}

}
