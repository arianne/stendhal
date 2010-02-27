package games.stendhal.client;

import games.stendhal.client.update.ClientGameConfiguration;

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
	private static final String VERSION_KEY = "_VERSION";
	private static Logger logger = Logger.getLogger(Cache.class);
	private Configuration cacheManager;
	private Properties prefilledCacheManager;

	/**
	 * Return the client configuration instance
	 * (content of "stendhal.cache").
	 * 
	 * @return the singleton instance
	 */
	public Configuration getConfiguration() {
		return cacheManager;
	}

	/**
	 * Inits the cache.
	 */
	protected void init() {
		try {
			prefilledCacheManager = new Properties();
			final URL url = this.getClass().getClassLoader().getResource("cache/stendhal.cache");
			if (url != null) {
				final InputStream is = url.openStream();
				prefilledCacheManager.load(is);
				is.close();
			}

			// init caching-directory
			if (!stendhal.WEB_START_SANDBOX) {
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

		} catch (final Exception e) {
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
				|| !stendhal.VERSION.equals(cacheManager.get(VERSION_KEY))) {
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
		final String cacheFile = System.getProperty("user.home")
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
		final String homeDir = System.getProperty("user.home");
		final String gameName = ClientGameConfiguration.get("GAME_NAME");
		final String gameFolder = "/" + gameName.toLowerCase() + "/";
		final String cache = "cache";
		final File cacheDir = new File(homeDir + gameFolder + cache);
		if (cacheDir.isDirectory()) {
			final File[] files = cacheDir.listFiles();
			for (final File file : files) {
				file.delete();
			}
		}
	}

	private InputStream getItemFromPrefilledCache(final TransferContent item) {
		final String name = "cache/" + item.name;

		// note: timestamp may contain a checksum. So we have to do an
		// "equal"-compare.
		final String timestamp = prefilledCacheManager.getProperty(item.name);
		if ((timestamp != null)
				&& (Integer.parseInt(timestamp) == item.timestamp)) {

			// get the stream
			final URL url = this.getClass().getClassLoader().getResource(name);
			if (url != null) {
				try {
					logger.debug("Content " + item.name
							+ " is in prefilled cache.");
					return url.openStream();
				} catch (final IOException e) {
					logger.error(e, e);
				}
			}

		}
		return null;
	}

	private InputStream getItemFromCache(final TransferContent item) {
		try {
			// check cache
			if (cacheManager.has(item.name)
					&& (Integer.parseInt(cacheManager.get(item.name)) == item.timestamp)) {
				logger.debug("Content " + item.name
						+ " is on cache. We save transfer");

				// get the stream
				try {
					return Persistence.get().getInputStream(true, stendhal.STENDHAL_FOLDER + "cache/", item.name);
				} catch (final IOException e) {
					logger.warn("Cannot read cache file: " + item.name);
				}
			}
		} catch (final NumberFormatException e) {
			logger.error("Broken cache entry: " + item.name, e);
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
	protected InputStream getItem(final TransferContent item) {
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
	protected void store(final TransferContent item, final byte[] data) {
		try {
			final OutputStream os = Persistence.get().getOutputStream(true,
					stendhal.STENDHAL_FOLDER + "cache/", item.name);
			os.write(data);
			os.close();

			logger.debug("Content " + item.name + " cached now. Timestamp: "
					+ Integer.toString(item.timestamp));

			cacheManager.set(item.name, Integer.toString(item.timestamp));
		} catch (final java.io.IOException e) {
			logger.error("store", e);
		}
	}
}
