package games.stendhal.server.actions.admin;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class InvisibleAction extends AdministrationAction {
private static final String _INVISIBLE = "invisible";
public static void register(){
	CommandCenter.register(_INVISIBLE, new InvisibleAction(), 500);
	
}
	@Override
	public void perform(Player player, RPAction action) {
	
		if (player.isInvisible()) {
			player.setInvisible(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					_INVISIBLE, "off");
		} else {
			player.setInvisible(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					_INVISIBLE, "on");
		}
	}

}
