package games.stendhal.server.actions;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;

public class QuestListAction implements ActionListener {
	private static final String _LISTQUESTS = "listquests";

	public static void register() {
		CommandCenter.register(_LISTQUESTS, new QuestListAction());
	}

	public void onAction(final Player player, final RPAction action) {

		final StringBuilder st = new StringBuilder();
		if (action.has(TARGET)) {
			final String which = action.get(TARGET);
			st.append(SingletonRepository.getStendhalQuestSystem().listQuest(player, which));

		} else {
			st.append(SingletonRepository.getStendhalQuestSystem().listQuests(player));
		}
		player.sendPrivateText(st.toString());
		player.notifyWorldAboutChanges();

	}

}
