package games.stendhal.server.core.config;

import games.stendhal.server.maps.quests.QuestInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class QuestsXMLLoader extends DefaultHandler {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(QuestsXMLLoader.class);

	private Map<String, QuestInfo> questInfos;

	// used while parsing the XML structure
	//private String name;

	private QuestInfo currentQuestInfo;

	private Map<String, String> currentList;

	private StringBuilder text;

	private String entryName;

	public static void main(final String[] argv) {
		if (argv.length != 1) {
			System.err.println("Usage: cmd filename");
			System.exit(1);
		}

		try {
			System.out.println(new QuestsXMLLoader().load(argv[0]).size());
		} catch (final Throwable e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public QuestInfo get(final String name) {
		QuestInfo questInfo = questInfos.get(name);
		if (questInfo == null) {
			questInfo = new QuestInfo();
			questInfo.setName(name);
			questInfo.setTitle(name + " (unknown)");
			questInfo.setDescription(name + " (unkown)");
		}
		return questInfo;
	}

	private Map<String, QuestInfo> load(final String ref) throws SAXException {
		questInfos = new HashMap<String, QuestInfo>();
		// Use the default (non-validating) parser
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			final SAXParser saxParser = factory.newSAXParser();

			final InputStream is = getClass().getClassLoader().getResourceAsStream(
					ref);
			if (is == null) {
				throw new FileNotFoundException("cannot find resource '" + ref
						+ "' in classpath");
			}
			saxParser.parse(is, this);
		} catch (final ParserConfigurationException t) {
			logger.error(t);
		} catch (final IOException e) {
			logger.error(e);
			throw new SAXException(e);
		}
		return questInfos;
	}

	@Override
	public void startDocument() {
		// do nothing
	}

	@Override
	public void endDocument() {
		// do nothing
	}

	@Override
	public void startElement(final String namespaceURI, final String lName, final String qName,
			final Attributes attrs) {
		text = new StringBuilder();

		if (qName.equals("quest")) {
			currentQuestInfo = new QuestInfo();
			currentQuestInfo.setName(attrs.getValue("name"));

		} else if (qName.equals("repeatable")) {

			// TODO handle name.equals("repeatable")

		} else if (qName.equals("history") || qName.equals("hints")) {
			currentList = new HashMap<String, String>();
		} else if (qName.equals("entry")) {
			entryName = attrs.getValue("name");
		}
	}

	@Override
	public void endElement(final String namespaceURI, final String sName, final String qName) {

		if (qName.equals("quest")) {
			questInfos.put(currentQuestInfo.getName(), currentQuestInfo);
		} else if (qName.equals("title")) {
			currentQuestInfo.setTitle(text.toString());
		} else if (qName.equals("description")) {
			currentQuestInfo.setDescription(text.toString());
		} else if (qName.equals("gm-description")) {
			currentQuestInfo.setDescriptionGM(text.toString());
		} else if (qName.equals("history")) {
			currentQuestInfo.setHistory(currentList);
		} else if (qName.equals("hints")) {
			currentQuestInfo.setHints(currentList);
		} else if (qName.equals("entry")) {
			currentList.put(entryName, text.toString());
		}
	}

	@Override
	public void characters(final char[] buf, final int offset, final int len) {
		text.append((new String(buf, offset, len)).trim() + " ");
	}

	public void load() throws SAXException {
		load("data/conf/quests.xml");
		
	}
}
