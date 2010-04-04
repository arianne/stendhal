package games.stendhal.client.update;

import java.util.Properties;

public class DefaultGameProperties extends Properties {

	public DefaultGameProperties() {
		super();

		this.put("GAME_NAME", "Stendhal");

		this.put("GAME_ICON", "data/gui/StendhalIcon.png");

		this.put("GAME_SPLASH_BACKGROUND", "data/gui/StendhalSplash.jpg");

		this.put("DEFAULT_SERVER", "stendhalgame.org");
		this.put("DEFAULT_PORT", "32160");

		this.put("UPDATE_ENABLE_AUTO_UPDATE", "true");
		this.put("UPDATE_SERVER_FOLDER", "http://arianne.sourceforge.net/stendhal/updates");
		this.put("UPDATE_VERSION_CHECK", "http://arianne.sourceforge.net/stendhal.version");
		this.put("UPDATE_SIGNER_FILE_NAME", "META-INF/MIGUELAN.SF");
	}

}
