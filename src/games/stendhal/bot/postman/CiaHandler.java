package games.stendhal.bot.postman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * listens for udp messages
 *
 * @author hendrik
 */
public class CiaHandler {

	/**
	 * processes an CIA email
	 *
	 * @param is input stream delivering the email
	 * @return IRC command
	 * @throws IOException in case of an input/output error
	 */
	public List<CiaMessage> read(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		try {
			skipHeader(br);
			Element root = DomHelper.readXml(new InputSource(br));

			// read header
			Element source = DomHelper.getChild(root, "source");
			String module = DomHelper.getChildText(source, "module");
			String branch = DomHelper.getChildText(source, "branch");

			// read commits
			List<CiaMessage> list = new LinkedList<CiaMessage>();
			NodeList nodes = DomHelper.getChild(root, "body").getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if ((node instanceof Element) && node.getNodeName().equals("commit")) {
					CiaMessage msg = new CiaMessage();
					msg.setModule(module);
					msg.setBranch(branch);
					msg.setAuthor(DomHelper.getChildText(node, "author"));
					msg.setRevision(DomHelper.getChildText(node, "revision"));

					NodeList fileNodes = DomHelper.getChild(node, "files").getChildNodes();
					for (int j = 0; j < fileNodes.getLength(); j++) {
						Node fileNode = fileNodes.item(j);
						if ((fileNode instanceof Element) && fileNode.getNodeName().equals("file")) {
							msg.addFile(fileNode.getTextContent().trim());
						}
					}
					msg.setMessage(DomHelper.getChildText(node, "log"));
					list.add(msg);
				}
			}

			return list;
		} finally {
			br.close();
		}
	}


	/**
	 * skips the mail header
	 *
	 * @param br BufferedReader
	 * @throws IOException in case of an input/output error
	 */
	private void skipHeader(BufferedReader br) throws IOException {
		String line = br.readLine();
		while ((line != null) && (!line.trim().isEmpty())) {
			line = br.readLine();
		}
	}
}
