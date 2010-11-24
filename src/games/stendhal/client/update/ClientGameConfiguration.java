package games.stendhal.client.update;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * read the configuration file for the client.
 * 
 * @author hendrik
 */
public final class ClientGameConfiguration {

	private static ClientGameConfiguration instance;

	private final Properties gameConfig = getGameProperties();

	private ClientGameConfiguration() {
	}

	private Properties getGameProperties() {
		Properties ret = null;

		try {
			InputStream is =  ClientGameConfiguration.class.getResourceAsStream("game.properties");
			if (is == null) {
				is = ClientGameConfiguration.class.getResourceAsStream("game-default.properties");
			}
			if (is == null) {
				throw new FileNotFoundException("Cannot read either game.properties or game-default.properties from classpath.");
			}

			final Properties config = new Properties();
			config.load(is);
			is.close();

			ret = config;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private static void init() {
		synchronized(ClientGameConfiguration.class) {
			if (instance == null) {
				instance = new ClientGameConfiguration();
			}
		}
	}

	/**
	 * gets a configuration value, in case it is undefined, the default of
	 * game-default.properties is returned. If this is undefined, too, the
	 * return value is null
	 * 
	 * @param key
	 *            key
	 * @return configured value
	 */
	public static String get(final String key) {
		init();
		return instance.gameConfig.getProperty(key);
	}
}
