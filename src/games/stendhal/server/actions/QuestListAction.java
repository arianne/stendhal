package games.stendhal.server.actions;

import games.stendhal.server.StendhalQuestSystem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class QuestListAction implements ActionListener {
	public static void register() {
		CommandCentre.register("listquests", new QuestListAction());
	}

	public void onAction(Player player, RPAction action) {

		StringBuilder st = new StringBuilder();
		if (action.has("target")) {
			String which = action.get("target");
			st.append(StendhalQuestSystem.get().listQuest(player, which));

		} else {
			st.append(StendhalQuestSystem.get().listQuests(player));
		}
		player.sendPrivateText(st.toString());
		player.notifyWorldAboutChanges();

	}

}
