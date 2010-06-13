package games.stendhal.server.actions;

import static games.stendhal.common.constants.Actions.LISTPRODUCERS;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class ListProducersAction implements ActionListener {
	

	public static void register() {
		CommandCenter.register(LISTPRODUCERS, new ListProducersAction());
	}

	public void onAction(final Player player, final RPAction action) {

		final StringBuilder st = new StringBuilder();
		st.append(SingletonRepository.getProducerRegister().listWorkingProducers(player));

		player.sendPrivateText(st.toString());
		player.notifyWorldAboutChanges();

	}

}
