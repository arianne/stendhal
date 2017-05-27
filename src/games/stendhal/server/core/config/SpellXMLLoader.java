/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config;

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

import games.stendhal.server.core.rule.defaultruleset.DefaultSpell;

public final class SpellXMLLoader extends DefaultHandler {

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

			final InputStream is = SpellXMLLoader.class.getResourceAsStream(uri.getPath());

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
			if(attributeValues.containsKey("amount")) {
				spell.setAmount(attributeValues.get("amount"));
			}
			if(attributeValues.containsKey("atk")) {
				spell.setAtk(attributeValues.get("atk"));
			}
			if(attributeValues.containsKey("cooldown")) {
				spell.setCooldown(attributeValues.get("cooldown"));
			}
			if(attributeValues.containsKey("def")) {
				spell.setDef(attributeValues.get("def"));
			}
			if(attributeValues.containsKey("lifesteal")) {
				spell.setLifesteal(attributeValues.get("lifesteal"));
			}
			if(attributeValues.containsKey("mana")) {
				spell.setMana(attributeValues.get("mana"));
			}
			if(attributeValues.containsKey("minimum-level")) {
				spell.setMinimumLevel(attributeValues.get("minimum-level"));
			}
			if(attributeValues.containsKey("range")) {
				spell.setRange(attributeValues.get("range"));
			}
			if(attributeValues.containsKey("rate")) {
				spell.setRate(attributeValues.get("rate"));
			}
			if(attributeValues.containsKey("regen")) {
				spell.setRegen(attributeValues.get("regen"));
			}
			if(attributeValues.containsKey("modifier")) {
				spell.setModifier(attributeValues.get("modifier"));
			}
			loadedSpells.add(spell);
		}
		if(qName.equals("attributes")) {
			attributeTagFound = false;
		}
	}

}
