package games.stendhal.server.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Loads a list of files from an xml file
 */
class GroupsXMLLoader extends DefaultHandler {

	/** Logger */
	private static final Logger logger = Logger.getLogger(GroupsXMLLoader.class);

	/** The main configuration file. */
	protected URI uri;

	/**
	 * A list of files.
	 */
	protected LinkedList<URI> groups;

	/**
	 * Create an xml based loader of groups.
	 * 
	 * @param uri
	 *            The location of the configuration file.
	 */
	public GroupsXMLLoader(URI uri) {
		this.uri = uri;
	}

	/**
	 * Loads and returns the list
	 * 
	 * @return list of group entries
	 * @throws SAXException
	 *             If a SAX error occurred.
	 * @throws IOException
	 *             If an I/O error occurred.
	 * @throws FileNotFoundException
	 *             If the resource was not found.
	 */
	public List<URI> load() throws SAXException, IOException {
		InputStream in = getClass().getResourceAsStream(uri.getPath());

		if (in == null) {
			throw new FileNotFoundException("Cannot find resource: "
					+ uri.getPath());
		}

		try {
			return load(in);
		} finally {
			in.close();
		}
	}

	/**
	 * Load and returns the list of files
	 * 
	 * @param world
	 *            The world to load into.
	 * @param in
	 *            The config file stream.
	 * @return list of group entries
	 * 
	 * @throws SAXException
	 *             If a SAX error occurred.
	 * @throws IOException
	 *             If an I/O error occurred.
	 */
	protected List<URI> load(InputStream in) throws SAXException, IOException {
		SAXParser saxParser;

		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException ex) {
			throw new SAXException(ex);
		}

		// Parse the XML
		groups = new LinkedList<URI>();
		saxParser.parse(in, this);
		return groups;
	}

	@Override
	public void startElement(String namespaceURI, String lName, String qName,
			Attributes attrs) {
		if (qName.equals("groups")) {
			// Ignore
		} else if (qName.equals("group")) {
			String s = attrs.getValue("uri");
			if (s == null) {
				logger.warn("group without 'uri'");
			} else {
				try {
					groups.add(uri.resolve(s));
				} catch (IllegalArgumentException ex) {
					logger.error("Invalid group reference: " + s + " ["
							+ ex.getMessage() + "]");
				}

			}
		} else {
			logger.warn("Unknown XML element: " + qName);
		}
	}

}