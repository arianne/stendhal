package conf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PortalMatchTest {
	private static final Logger logger = Logger.getLogger(PortalMatchTest.class); 
	private final transient List<PortalTestObject> portals = new LinkedList<PortalTestObject>();

	@Test
	public void testread() {
		Log4J.init();
		try {

			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			final File directory = new File("data/conf/zones/");
			final File[] files = directory.listFiles(new FileFilter() {

				public boolean accept(File file) {
					return file.getName().endsWith("xml");
				}
			});

			assertFalse("files should not be empty", files.length == 0);
			for (File f : files) {
				final Document doc = docBuilder.parse(f);
				portals.addAll(proceedDocument(doc));
			}

		} catch (SAXParseException err) {
		
			fail(err.toString());

		} catch (SAXException e) {
			
			fail(e.toString());
		} catch (Exception t) {
			
			fail(t.toString());
		}

		assertTrue("All portals are valid", isValid(portals));

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

	public boolean isValid(final List<PortalTestObject> testList) {
		boolean result = true;

		for (PortalTestObject x : testList) {
			if (x.hasDestination()) {
				boolean founddestination = false;
				for (PortalTestObject y : testList) {
					if (y.isDestinationOf(x)) {
						founddestination = true;
					}

				}
				if (!founddestination) {
					logger.warn(x.toString());

				}
				result = result && founddestination;
			}
		}
		return result;
	}
}
