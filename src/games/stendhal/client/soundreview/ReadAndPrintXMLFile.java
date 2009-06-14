package games.stendhal.client.soundreview;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReadAndPrintXMLFile {
	private static Logger logger = Logger.getLogger(ReadAndPrintXMLFile.class);

	public static void main(final String[] argv) {
		try {

			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			final Document doc = docBuilder.parse(new File("data/sounds/sounds.xml"));

			// normalize text representation
			doc.getDocumentElement().normalize();
			System.out.println("Root element of the doc is "
					+ doc.getDocumentElement().getNodeName());

			final NodeList listOfPersons = doc.getElementsByTagName("entry");
			listOfPersons.item(0).getAttributes().item(0).toString();
			final int totalPersons = listOfPersons.getLength();
			System.out.println("Total no of people : " + totalPersons);

			for (int s = 0; s < listOfPersons.getLength(); s++) {
				System.out.println(listOfPersons.item(s).getAttributes().item(0).getNodeValue());
				System.out.println(listOfPersons.item(s).getTextContent());
			} // end of for loop with s var

		} catch (final SAXParseException err) {
			logger.error("** Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			logger.error(" " + err.getMessage());

		} catch (final SAXException e) {
			final Exception x = e.getException();
			if (x == null) {
				logger.error(e, e);
			} else {
				logger.error(x, x);
			}

		} catch (final Throwable t) {
			logger.error(t, t);
		}
		// System.exit (0);

	} // end of main

}
