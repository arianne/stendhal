package games.stendhal.server.actions.admin;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class GhostModeAction extends AdministrationAction {
public static void register(){
	CommandCenter.register("ghostmode", new GhostModeAction(), 500);
	
}
	@Override
	public void perform(Player player, RPAction action) {
	
		if (player.isGhost()) {
			player.setGhost(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"ghostmode", "off");
	
			player.setInvisible(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"invisible", "off");
	
			for (Player p : StendhalRPRuleProcessor.get().getPlayers()) {
				p.notifyOnline(player.getName());
			}
		} else {
			/*
			 * When we enter ghostmode we want our player to be also invisible.
			 */
			player.setInvisible(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"invisible", "on");
	
			player.setGhost(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"ghostmode", "on");
	
			for (Player p : StendhalRPRuleProcessor.get().getPlayers()) {
				p.notifyOffline(player.getName());
			}
		}
	
		player.notifyWorldAboutChanges();
	}

}
