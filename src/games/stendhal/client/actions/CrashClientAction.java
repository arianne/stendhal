package games.stendhal.client.actions;

import games.stendhal.client.entity.User;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

public class CrashClientAction implements SlashAction {

	public boolean execute(String[] params, String remainder) {
		if (!User.isAdmin()) {
			j2DClient.get().addEventLine(new HeaderLessEventLine("Ouch. That hurts!", NotificationType.ERROR));
			return true;
		}

		if (params[0].equalsIgnoreCase("oom") || params[0].equalsIgnoreCase("OutOfMemoryError")) {
			throw new OutOfMemoryError();
		} else if (params[0].equalsIgnoreCase("ThreadDeath")) {
			throw new ThreadDeath();
		} else if (params[0].equalsIgnoreCase("IllegalAccessError")) {
			throw new IllegalAccessError();
		} else if (params[0].equalsIgnoreCase("NoSuchMethodError")) {
			throw new NoSuchMethodError();
		} else if (params[0].equalsIgnoreCase("npe") || params[0].equalsIgnoreCase("NullPointerException")) {
			throw new NullPointerException();
		} else if (params[0].equalsIgnoreCase("sioobe") || params[0].equalsIgnoreCase("StringIndexOutOfBoundsException")) {
			throw new StringIndexOutOfBoundsException();
		}

		return true;
	}

	public int getMaximumParameters() {
		return 1;
	}

	public int getMinimumParameters() {
		return 1;
	}

}
