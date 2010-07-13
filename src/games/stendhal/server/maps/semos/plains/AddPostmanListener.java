package games.stendhal.server.maps.semos.plains;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.ReadAchievementsOnLogin;
import games.stendhal.server.entity.player.ReadMessagesOnLogin;

import java.util.Map;

/**
 * Adds the listener for postman messages
 */
public class AddPostmanListener implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		SingletonRepository.getLoginNotifier().addListener(new ReadMessagesOnLogin());
		SingletonRepository.getLoginNotifier().addListener(new ReadAchievementsOnLogin());
	}



}
