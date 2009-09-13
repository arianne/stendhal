package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.CIDLIST;

import java.util.Map;

import games.stendhal.server.actions.CIDSubmitAction;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

class CIDListAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(CIDLIST, new CIDListAction(), 50);
	}

	@Override
	public void perform(final Player player, final RPAction action) {
		
		final Map<String, String> nameList = CIDSubmitAction.nameList;
		final Map<String, String> idList = CIDSubmitAction.idList;
		
		player.sendPrivateText("One line per Online Player");
		
		SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(
				
			new Task<Player>() {

			public void execute(final Player iPlayer) {
				
				String iName = iPlayer.getName();
				if (nameList.containsKey(iName)) {
					String tid = nameList.get(iName);
					if (idList.containsKey(tid)) {
						String group = idList.get(tid);
						player.sendPrivateText("One Computer(" + iName + "): " + group);
						//player.sendPrivateText(tid);
					}
				}
			}
			
		});
		
	}

}
