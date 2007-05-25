package games.stendhal.server.actions;

import games.stendhal.server.StendhalQuestSystem;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;


public class QuestListAction implements ActionListener {
	private static final Logger logger = Log4J.getLogger(QuestListAction.class);
	public static void register() {
	     StendhalRPRuleProcessor.register("listquests", new QuestListAction());
	}
	public void onAction(Player player, RPAction action) {
		StringBuilder st = new StringBuilder();
		if (action.has("target")){
			String which=action.get("target");
			st.append(StendhalQuestSystem.get().listQuest(player,which));
			
		}else{
			st.append(StendhalQuestSystem.get().listQuests(player));
		}
		player.sendPrivateText(st.toString());
		player.notifyWorldAboutChanges();
	}

}
