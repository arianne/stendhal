package games.stendhal.server.core.config;

import games.stendhal.server.core.rule.defaultruleset.DefaultSpell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SpellXMLLoader extends DefaultHandler {
	
	private static final Logger logger = Logger.getLogger(SpellXMLLoader.class);
	
	private String name;
	
	private String implementation;

	private Map<String, String> attributeValues;
	
	private List<DefaultSpell> loadedSpells;

	private boolean attributeTagFound;

	private String nature;

	public Collection<DefaultSpell> load(URI uri) throws SAXException {
		loadedSpells = new LinkedList<DefaultSpell>();
		// Use the default (non-validating) parser
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			final SAXParser saxParser = factory.newSAXParser();

			final InputStream is = getClass().getResourceAsStream(uri.getPath());

			if (is == null) {
				throw new FileNotFoundException("cannot find resource '" + uri
						+ "' in classpath");
			}
			try {
				saxParser.parse(is, this);
			} finally {
				is.close();
			}
		} catch (final ParserConfigurationException t) {
			logger.error(t);
		} catch (final IOException e) {
			logger.error(e);
			throw new SAXException(e);
		}
		return loadedSpells;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals("spell")) {
			name = attributes.getValue("name");
		}
		if(qName.equals("implementation")) {
			implementation = attributes.getValue("class-name");
		}
		if(qName.equals("nature")) {
			nature = attributes.getValue("value");
		}
		if(qName.equals("attributes")) {
			attributeTagFound = true;
			attributeValues = new HashMap<String, String>();
		}
		if(attributeTagFound) {
			attributeValues.put(qName, attributes.getValue("value"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("spell")) {
			DefaultSpell spell = new DefaultSpell(name, implementation);
			spell.setNature(nature);
			spell.setAmount(attributeValues.get("amount"));
			spell.setAtk(attributeValues.get("atk"));
			spell.setCooldown(attributeValues.get("cooldown"));
			spell.setDef(attributeValues.get("def"));
			spell.setLifesteal(attributeValues.get("lifesteal"));
			spell.setMana(attributeValues.get("mana"));
			spell.setMinimumLevel(attributeValues.get("minimum-level"));
			spell.setRange(attributeValues.get("range"));
			spell.setRate(attributeValues.get("rate"));
			spell.setRegen(attributeValues.get("regen"));
			loadedSpells.add(spell);
		}
		if(qName.equals("attributes")) {
			attributeTagFound = false;
		}
	}
	
}
