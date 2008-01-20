package conf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import marauroa.common.Log4J;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PortalMatchTestTest {

	

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
		assertTrue("all portals in this test file are valid", pmt.isValid(portals));
		portals = new LinkedList<PortalTestObject>();
		xmldoc = docBuilder.parse(new File("tests/conf/invalid.xml"));
		portals.addAll(pmt.proceedDocument(xmldoc));
		assertFalse("there is a known bad in it", pmt.isValid(portals));

	}

}
