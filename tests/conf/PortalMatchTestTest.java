package conf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;

public class PortalMatchTestTest {

	private PortalMatchTest pmt = new PortalMatchTest();

	@Test
	public void testvalidate() throws Exception {
		LinkedList<PortalTestObject> portals = new LinkedList<PortalTestObject>();
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document xmldoc = docBuilder.parse(new File("tests/conf/valid.xml"));

		portals.addAll(pmt.proceedDocument(xmldoc));
		assertTrue(pmt.isValid(portals));
		portals = new LinkedList<PortalTestObject>();
		xmldoc = docBuilder.parse(new File("tests/conf/invalid.xml"));
		portals.addAll(pmt.proceedDocument(xmldoc));
		assertFalse(pmt.isValid(portals));

	}

}
