package games.stendhal.server.actions;

import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class MarkAction implements ActionListener {

	public void onAction(Player player, RPAction action) {
		if(action.has(TARGET)) {
			if(player.drop("empty scroll", 1)) {
				String ds=action.get(TARGET)+" "+action.get("args");
				String is=player.getZone().getName()+" "+player.getX()+" "+player.getY();

				Item scroll = SingletonRepository.getEntityManager().getItem("marked scroll");				
				scroll.setInfoString(is);
				scroll.setDescription("You see the marked by "+player.getName()+" scroll. It says: \""+ds+"\". ");
				player.equipOrPutOnGround(scroll);
				player.sendPrivateText("You marked scroll (\""+ds+"\")");
			} else {
				player.sendPrivateText("You dont have scrolls to mark it!");
			}			
		} else {
			player.sendPrivateText("you need enter description.");
		}

	}

}
