package games.stendhal.server.core.config;

import games.stendhal.server.core.rule.defaultruleset.DefaultItem;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Load and configure items via an XML configuration file.
 */
public class ItemGroupsXMLLoader extends DefaultHandler {

	
	private static final Logger logger = Logger.getLogger(ItemGroupsXMLLoader.class);

	/** The main item configuration file. */
	protected URI uri;

	/**
	 * Create an xml based loader of item groups.
	 * 
	 * @param uri
	 *            The location of the configuration file.
	 */
	public ItemGroupsXMLLoader(final URI uri) {
		this.uri = uri;
	}

	/**
	 * Load items.
	 * 
	 * @return list of items
	 * @throws SAXException
	 *             If a SAX error occurred.
	 * @throws IOException
	 *             If an I/O error occurred.
	 */
	public List<DefaultItem> load() throws SAXException, IOException {
		final GroupsXMLLoader groupsLoader = new GroupsXMLLoader(uri);
		final List<URI> groups = groupsLoader.load();

		final ItemsXMLLoader loader = new ItemsXMLLoader();
		final List<DefaultItem> list = new LinkedList<DefaultItem>();
		for (final URI groupUri : groups) {
			logger.debug("Loading item group [" + groupUri + "]");
			list.addAll(loader.load(groupUri));
		}

		return list;
	}
}
