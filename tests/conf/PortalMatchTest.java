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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import marauroa.common.Log4J;

public class PortalMatchTest {
	private final transient List<PortalTestObject> portals = new LinkedList<PortalTestObject>();

	@Test
	public void testread() {
		Log4J.init();
		try {

			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			final File directory = new File("data/conf/zones/");
			final File[] files = directory.listFiles(new FileFilter() {

				@Override
				public boolean accept(final File file) {
					return file.getName().endsWith("xml");
				}
			});

			assertThat(files, notNullValue());
			assertThat("files should not be empty", files.length, not((is(0))));
			for (final File f : files) {
				final Document doc = docBuilder.parse(f);
				portals.addAll(proceedDocument(doc));
			}

		} catch (final SAXParseException err) {

			fail(err.toString());

		} catch (final SAXException e) {

			fail(e.toString());
		} catch (final Exception t) {

			fail(t.toString());
		}

		assertThat("All portals are valid", isValid(portals), equalTo(""));

	}

	List<PortalTestObject> proceedDocument(final Document xmldoc) {
		// normalize text representation
		final List<PortalTestObject> tempList = new LinkedList<PortalTestObject>();
		String zone = "";
		String destZone = "";
		String destName = "";
		String name = "";
		xmldoc.getDocumentElement().normalize();

		final NodeList listOfPortals = xmldoc.getElementsByTagName("portal");
		if (listOfPortals.getLength() > 0) {

			for (int s = 0; s < listOfPortals.getLength(); s++) {
				zone = listOfPortals.item(s).getParentNode().getAttributes().getNamedItem(
						"name").getNodeValue();
				name = listOfPortals.item(s).getAttributes().getNamedItem("ref").getNodeValue();

				final NodeList listofChildren = listOfPortals.item(s).getChildNodes();
				for (int i = 0; i < listofChildren.getLength(); i++) {
					if ("destination".equals(listofChildren.item(i).getNodeName())) {
						destName = listofChildren.item(i).getAttributes().getNamedItem(
								"ref").getNodeValue();
						destZone = listofChildren.item(i).getAttributes().getNamedItem(
								"zone").getNodeValue();

					}
				}
				tempList.add(new PortalTestObject(zone, name, destZone, destName));

			}
		} // end of for loop with s var
		return tempList;
	}

	public String isValid(final List<PortalTestObject> testList) {
		StringBuilder errors = new StringBuilder();

		for (final PortalTestObject x : testList) {
			if (x.hasDestination()) {
				boolean founddestination = false;
				for (final PortalTestObject y : testList) {
					if (y.isDestinationOf(x)) {
						founddestination = true;
					}

				}
				if (!founddestination) {
					errors.append(x.toString());

				}
			}
		}
		return errors.toString();
	}
}
