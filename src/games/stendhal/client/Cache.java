package games.stendhal.client;

import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.client.update.ClientGameConfiguration;
import games.stendhal.common.Debug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import marauroa.common.Configuration;
import marauroa.common.ConfigurationParams;
import marauroa.common.io.Persistence;
import marauroa.common.net.message.TransferContent;

import org.apache.log4j.Logger;

/**
 * <p>
 * Manages a two level cache which one or both levels are optional:
 * </p>
 * 
 * <p>
 * The first level is prefilled readonly cache in a .jar file on class path. At
 * the time of writing we use this for Webstart because we are unsure how large
 * the webstart PersistenceService may grow.
 * </p>
 * 
 * <p>
 * The second level is a normal cache on filesystem.
 * </p>
 */
public class Cache {
	private static Logger logger = Logger.getLogger(Cache.class);
	private Configuration cacheManager;
	private Properties prefilledCacheManager;
	private static final String VERSION_KEY = "_VERSION";

	/**
	 * Return the client configuration instance
	 * (content of "stendhal.cache").
	 * 
	 * @return
	 */
	public Configuration getConfiguration() {
		return cacheManager;
	}

	/**
	 * Inits the cache.
	 */
	public void init() {
		try {
			prefilledCacheManager = new Properties();
			URL url = SpriteStore.get().getResourceURL("cache/stendhal.cache");
			if (url != null) {
				InputStream is = url.openStream();
				prefilledCacheManager.load(is);
				is.close();
			}

			// init caching-directory
			if (!Debug.WEB_START_SANDBOX) {
				// Create file object
				File file = new File(System.getProperty("user.home") + "/"
						+ stendhal.STENDHAL_FOLDER);
				if (!file.exists() && !file.mkdir()) {
					logger.error("Can't create " + file.getAbsolutePath()
							+ " folder");
				} else if (file.exists() && file.isFile()) {
					if (!file.delete() || !file.mkdir()) {
						logger.error("Can't removing file "
								+ file.getAbsolutePath()
								+ " and creating a folder instead.");
					}
				}

				file = new File(System.getProperty("user.home")
						+ stendhal.STENDHAL_FOLDER + "cache/");
				if (!file.exists() && !file.mkdir()) {
					logger.error("Can't create " + file.getAbsolutePath()
							+ " folder");
				}
			}
			initCacheManager();
			cleanCacheOnUpdate();
			cacheManager.set(VERSION_KEY, stendhal.VERSION);

		} catch (Exception e) {
			logger.error("cannot create StendhalClient", e);
		}
	}

	/**
	 * Empty cache on update.
	 * 
	 * Stendhal is known to crash, if incompatible stuff is in cache.
	 * 
	 * @throws IOException
	 *             in case the cache folder is not writeable
	 */
	private void cleanCacheOnUpdate() throws IOException {
		if (!cacheManager.has(VERSION_KEY)
				|| !cacheManager.get(VERSION_KEY).equals(stendhal.VERSION)) {
			cleanCache();
			cacheManager.clear();
			initCacheManager();
		}
	}

	/**
	 * initializes the low level cache manager.
	 * 
	 * @throws IOException
	 *             in case the cache folder is not readable
	 */
	private void initCacheManager() throws IOException {
		String cacheFile = System.getProperty("user.home")
				+ stendhal.STENDHAL_FOLDER + "cache/stendhal.cache";

		// create a new cache file if doesn't exist already
		new File(cacheFile).createNewFile();

		cacheManager = new Configuration(new ConfigurationParams(
				true, stendhal.STENDHAL_FOLDER, "cache/stendhal.cache"));
	}

	/**
	 * Deletes the cache.
	 */
	private void cleanCache() {
		String homeDir = System.getProperty("user.home");
		String gameName = ClientGameConfiguration.get("GAME_NAME");
		String gameFolder = "/" + gameName.toLowerCase() + "/";
		String cache = "cache";
		File cacheDir = new File(homeDir + gameFolder + cache);
		if (cacheDir.isDirectory()) {
			File[] files = cacheDir.listFiles();
			for (File file : files) {
				file.delete();
			}
		}
	}

	private InputStream getItemFromPrefilledCache(TransferContent item) {
		String name = "cache/" + item.name;

		// note: timestamp may contain a checksum. So we have to do an
		// "equal"-compare.
		String timestamp = prefilledCacheManager.getProperty(item.name);
		if ((timestamp != null)
				&& (Integer.parseInt(timestamp) == item.timestamp)) {

			// get the stream
			URL url = SpriteStore.get().getResourceURL(name);
			if (url != null) {
				try {
					logger.debug("Content " + item.name
							+ " is in prefilled cache.");
					return url.openStream();
				} catch (IOException e) {
					logger.error(e, e);
				}
			}

		}
		return null;
	}

	private InputStream getItemFromCache(TransferContent item) {

		// check cache
		if (cacheManager.has(item.name)
				&& (Integer.parseInt(cacheManager.get(item.name)) == item.timestamp)) {
			logger.debug("Content " + item.name
					+ " is on cache. We save transfer");

			// get the stream
			try {
				return Persistence.get().getInputStream(true,
						stendhal.STENDHAL_FOLDER + "cache/", item.name);
			} catch (IOException e) {
				logger.error(e, e);
			}
		}
		return null;
	}

	/**
	 * Gets an item from cache.
	 * 
	 * @param item
	 *            key
	 * @return InputStream or null if not in cache
	 */
	public InputStream getItem(TransferContent item) {
		// 1. try to read it from stendhal-prefilled-cache.jar
		InputStream is = getItemFromPrefilledCache(item);

		// 2. try to read from our cache (if not in sandbox)
		if (is == null) {
			is = getItemFromCache(item);
		}
		return is;
	}

	/**
	 * Stores an item in cache.
	 * 
	 * @param item
	 *            key
	 * @param data
	 *            data
	 */
	public void store(TransferContent item, byte[] data) {
		try {
			OutputStream os = Persistence.get().getOutputStream(true,
					stendhal.STENDHAL_FOLDER + "cache/", item.name);
			os.write(data);
			os.close();

			logger.debug("Content " + item.name + " cached now. Timestamp: "
					+ Integer.toString(item.timestamp));

			cacheManager.set(item.name, Integer.toString(item.timestamp));
		} catch (java.io.IOException e) {
			logger.error("store", e);
		}
	}
}
