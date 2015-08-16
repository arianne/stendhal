/***************************************************************************
 *                   (C) Copyright 2003-2015 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package conf;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import marauroa.common.Log4J;

/**
 * Tests for PortalMatcherTest
 */
public class PortalMatchTestTest {

	/**
	 * Test the validation method
	 *
	 * @throws ParserConfigurationException in case of an invalid zone file
	 * @throws SAXException in case of an invalid zone file
	 * @throws IOException in case of an input/output error
	 */
	@Test
	public void testvalidate() throws ParserConfigurationException, SAXException, IOException {
		Log4J.init();
		final PortalMatchTest pmt = new PortalMatchTest();
		LinkedList<PortalTestObject> portals = new LinkedList<PortalTestObject>();
		final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document xmldoc = docBuilder.parse(new File("tests/conf/valid.xml"));

		portals.addAll(pmt.proceedDocument(xmldoc));
		assertThat("all portals in this test file are valid", pmt.isValid(portals), equalTo(""));
		portals = new LinkedList<PortalTestObject>();
		xmldoc = docBuilder.parse(new File("tests/conf/invalid.xml"));
		portals.addAll(pmt.proceedDocument(xmldoc));
		assertThat("there is a known bad in it", pmt.isValid(portals), not(equalTo("")));

	}

}
