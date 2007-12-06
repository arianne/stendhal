package games.stendhal.server.actions.admin;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class SupportAnswerAction extends AdministrationAction {

	public static void register(){
		CommandCenter.register("supportanswer", new SupportAnswerAction(), 50);

	}
	@Override
	public void perform(Player player, RPAction action) {
		if (action.has("target") && action.has("text")) {
			String message = player.getTitle() + " answers "
					+ Grammar.suffix_s(action.get("target"))
					+ " support question: " + action.get("text");
	
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"supportanswer", action.get("target"), action.get("text"));
	
			boolean found = false;
			for (Player p : StendhalRPRuleProcessor.get().getPlayers()) {
				if (p.getTitle().equals(action.get("target"))) {
					p.sendPrivateText("Support (" + player.getTitle()
							+ ") tells you: " + action.get("text"));
					p.notifyWorldAboutChanges();
					found = true;
				}
				if (p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT) {
					p.sendPrivateText(message);
					p.notifyWorldAboutChanges();
				}
			}
	
			if (!found) {
				player.sendPrivateText(action.get("target")
						+ " is not currently logged in.");
			}
		}
	}

}
