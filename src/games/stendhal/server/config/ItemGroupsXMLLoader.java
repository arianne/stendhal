package games.stendhal.server.config;

import games.stendhal.server.rule.defaultruleset.DefaultItem;

import java.io.FileNotFoundException;
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

	/** Logger */
	private static final Logger logger = Logger.getLogger(ItemGroupsXMLLoader.class);

	/** The main item configuration file. */
	protected URI uri;

	/**
	 * Create an xml based loader of item groups.
	 *
	 * @param	uri		The location of the configuration file.
	 */
	public ItemGroupsXMLLoader(URI uri) {
		this.uri = uri;
	}

	/**
	 * Load items
	 *
	 * @return list of items
	 * @throws	SAXException	If a SAX error occurred.
	 * @throws	IOException	If an I/O error occurred.
	 * @throws	FileNotFoundException
	 *				If the resource was not found.
	 */
	public List<DefaultItem> load() throws SAXException, IOException {
		GroupsXMLLoader groupsLoader = new GroupsXMLLoader(uri); 
		List<URI> groups = groupsLoader.load();

		ItemsXMLLoader loader = new ItemsXMLLoader(); 
		List<DefaultItem> list = new LinkedList<DefaultItem>();
		for (URI uri : groups) {
			logger.debug("Loading item group [" + uri + "]");
			list.addAll(loader.load(uri));

		}
		return list;
	}
}