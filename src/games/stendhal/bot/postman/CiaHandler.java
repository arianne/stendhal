package games.stendhal.bot.postman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jibble.pircbot.Colors;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * listens for udp messages
 *
 * @author hendrik
 */
public class CiaHandler {
	/** logger */
	protected static Logger logger = Logger.getLogger(CiaHandler.class);
	private PostmanIRC postmanIRC;

	/**
	 * Cia Handler
	 *
	 * @param postmanIRC postman irc bot
	 */
	public CiaHandler(PostmanIRC postmanIRC) {
		this.postmanIRC = postmanIRC;
	}

	/**
	 * listens for cia messages
	 */
	public void listen() {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					ServerSocket server = new ServerSocket();
					server.bind(new InetSocketAddress("localhost", 7838));
					while (true) {
						Socket client = server.accept();
						Thread clientThread = new Thread(new CiaClientThread(client));
						clientThread.setDaemon(true);
						clientThread.start();
					}
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	/**
	 * handles Cia client messages
	 */
	class CiaClientThread implements Runnable {
		private Socket client = null;

		/**
		 * CiaClientThread
		 *
		 * @param client client
		 */
		public CiaClientThread(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				InputStream is = client.getInputStream();
				List<CiaMessage> msgs = read(is);
				is.close();
				client.close();
				for (CiaMessage msg : msgs) {
					String line = format(msg);
					postmanIRC.sendMessageToDevChannels(line);
				}
			} catch (IOException e) {
				logger.error(e, e);
			}
		}
		
	}

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
	 * formats a CiaMessage for IRC
	 *
	 * @param msg CiaMessage
	 * @return formatted irc line
	 */
	public String format(CiaMessage msg) {
		StringBuilder res = new StringBuilder();
		addFormattedToken(res, Colors.BOLD, msg.getProject() + ": ");
		addFormattedToken(res, Colors.DARK_GREEN, msg.getAuthor());
		if ((msg.getBranch() != null) && (!msg.getBranch().trim().isEmpty())) {
			res.append(" ");
			addFormattedToken(res, Colors.BROWN, msg.getBranch());
		}
		res.append(" *");
		if ((msg.getRevision() != null) && (!msg.getRevision().trim().isEmpty())) {
			res.append(" r");
			addFormattedToken(res, Colors.BOLD, msg.getRevision());
		}
		res.append(" ");
		addFormattedToken(res, Colors.TEAL, msg.getModule());
		res.append("/");
		res.append(msg.getFiles());
		addFormattedToken(res, Colors.BOLD, ":");
		res.append(" ");
		res.append(msg.getMessage());
		return res.toString();
	}

	/**
	 * formats a token
	 *
	 * @param out    output buffer
	 * @param format color or format to use
	 * @param token  message
	 */
	private void addFormattedToken(StringBuilder out, String format, String token) {
		out.append(format);
		out.append(Colors.removeFormattingAndColors(token));
		out.append(Colors.NORMAL);
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
