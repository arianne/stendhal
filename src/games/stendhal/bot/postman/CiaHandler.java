package games.stendhal.bot.postman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
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
			String project = DomHelper.getChildText(source, "project");
			String module = DomHelper.getChildText(source, "module");
			String branch = DomHelper.getChildText(source, "branch");

			// read commits
			List<CiaMessage> list = new LinkedList<CiaMessage>();
			for (Element node : DomHelper.getChildren(DomHelper.getChild(root, "body"), "commit")) {
				CiaMessage msg = new CiaMessage();
				msg.setProject(project);
				msg.setModule(module);
				msg.setBranch(branch);
				msg.setAuthor(DomHelper.getChildText(node, "author"));
				msg.setRevision(DomHelper.getChildText(node, "revision"));
				for (Element fileNode : DomHelper.getChildren(DomHelper.getChild(node, "files"), "file")) {
					msg.addFile(fileNode.getTextContent().trim());
				}
				msg.setMessage(DomHelper.getChildText(node, "log"));
				list.add(msg);
			}

			return list;
		} finally {
			br.close();
		}
	}
//	20:33 < CIA-18> arianne_rpg: madmetzger * r464dd9c4d5bb marauroa/ (18 files in 13 dirs): Merge branch 'master' of ssh://madmetzger@arianne.git.sourceforge.net/gitroot/arianne/marauroa
//	18:15 < CIA-27> arianne_rpg: kiheru * stendhal/src/games/stendhal/client/gui/ (6 files in 2 dirs): Autoinspect containers. Removed the old, more complicated, approach



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
