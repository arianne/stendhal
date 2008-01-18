package conf;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PortalMatchTest {
	private LinkedList<PortalTestObject> portals = new LinkedList<PortalTestObject>();

	@Test
	public void testread() throws Exception {

		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			// todo:make this read directory content
			String[] zonesxml = { "ados.xml", "amazon.xml", "athor.xml",
					"fado.xml", "kalavan.xml", "kikareukin.xml", "kirdneh.xml",
					"misc.xml", "nalwor.xml", "orril.xml", "semos.xml" };

			for (int zone = 0; zone < zonesxml.length; zone++) {
				Document doc = docBuilder.parse(new File("data/conf/zones/"
						+ zonesxml[zone]));

				portals.addAll(proceedDocument(doc));
			}

		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		assertTrue("All portals are valid", isValid(portals));

	}

	LinkedList<PortalTestObject> proceedDocument(Document xmldoc) {
		// normalize text representation
		LinkedList<PortalTestObject> tempList = new LinkedList<PortalTestObject>();
		String zone = "";
		String destZone = "";
		String destName = "";
		String name = "";
		xmldoc.getDocumentElement().normalize();

		NodeList listOfPortals = xmldoc.getElementsByTagName("portal");
		if (listOfPortals.getLength() > 0) {
			listOfPortals.item(0).getAttributes().item(0).toString();
			for (int s = 0; s < listOfPortals.getLength(); s++) {
				zone = listOfPortals.item(s).getParentNode().getAttributes().getNamedItem(
						"name").getNodeValue();
				name = listOfPortals.item(s).getAttributes().getNamedItem("ref").getNodeValue();
				listOfPortals.item(s).getNodeName();
				NodeList listofChildren = listOfPortals.item(s).getChildNodes();
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

	public boolean isValid(LinkedList<PortalTestObject> testList) {
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
					System.out.println(x.toString());

				}
				result = result && founddestination;
			}
		}
		return result;
	}
}
