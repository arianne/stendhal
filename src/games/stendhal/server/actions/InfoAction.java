package games.stendhal.server.actions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.DateFormatter;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class InfoAction implements ActionListener {

	private static final String DATE_FORMAT_NOW = "dd-MMMM-yyyy HH:mm:ss";

	public void onAction(Player player, RPAction action) {
		player.sendPrivateText("It is " + getGametime());

	}

	private String getGametime() {
		 SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		    return sdf.format(new Date(System.currentTimeMillis()));
		
	}

}
