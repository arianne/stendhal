package games.stendhal.client.gui;

import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.client.update.ClientGameConfiguration;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

/**
 * A help system that displays the manual
 * 
 * @author hendrik (based on "How to Use Trees"
 *         http://java.sun.com/docs/books/tutorial/uiswing/components/tree.html)
 */
public class HelpDialog extends JFrame {
	private static final long serialVersionUID = 41013220176906825L;
	private static Logger logger = Logger.getLogger(HelpDialog.class);

	private HelpDialogPanel panel;

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public HelpDialog() {
		// Create and set up the window.
		super(ClientGameConfiguration.get("GAME_NAME") + " - Help");
		URL url = SpriteStore.get().getResourceURL(
				ClientGameConfiguration.get("GAME_ICON"));
		this.setIconImage(new ImageIcon(url).getImage());

		super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// Create and set up the content pane.
		panel = new HelpDialogPanel();
		panel.setOpaque(true); // content panes must be opaque
		super.setContentPane(panel);

	}

	/**
	 * displays the help system
	 */
	public void display() {
		display(HelpDocument.Introduction);
	}

	/**
	 * displays the help system
	 * 
	 * @param bookInfo
	 *            page to display
	 */
	public void display(HelpDocument bookInfo) {
		panel.displayNode(bookInfo);
		super.pack();
		super.setVisible(true);
	}

	/**
	 * Creates a new help dialog
	 */
	private static class HelpDialogPanel extends JPanel implements
			TreeSelectionListener {
		private static final long serialVersionUID = -290672385299793246L;
		private JTree tree;
		private JEditorPane htmlPane;

		/**
		 * Creates a new HelpDialogPanel
		 */
		public HelpDialogPanel() {
			super(new GridLayout(1, 0));

			// Create the nodes.
			DefaultMutableTreeNode top = new DefaultMutableTreeNode(
					"Stendhal Manual");
			createNodes(top);

			// Create a tree that allows one selection at a time.
			tree = new JTree(top);
			tree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);

			// Expand tree
			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}

			// Listen for when the selection changes.
			tree.addTreeSelectionListener(this);

			// Create the scroll pane and add the tree to it.
			JScrollPane treeView = new JScrollPane(tree);

			// Create the HTML viewing pane.
			htmlPane = new JEditorPane();
			htmlPane.setEditable(false);
			htmlPane.setContentType("text/html");
			StyleSheet css = ((HTMLEditorKit) htmlPane.getEditorKit()).getStyleSheet();
			css.addRule("body { font-family: arial, helvetica, sans-serif; }");
			JScrollPane htmlView = new JScrollPane(htmlPane);

			// Add the scroll panes to a split pane.
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setTopComponent(treeView);
			splitPane.setBottomComponent(htmlView);

			Dimension minimumSize = new Dimension(50, 50);
			htmlView.setMinimumSize(minimumSize);
			treeView.setMinimumSize(minimumSize);
			splitPane.setDividerLocation(250);
			splitPane.setPreferredSize(new Dimension(790, 580));

			// Add the split pane to this panel.
			add(splitPane);
		}

		/**
		 * Update the browser window on selection change in the tree
		 */
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

			if (node == null) {
				return;
			}

			Object nodeInfo = node.getUserObject();
			if (node.isLeaf()) {
				HelpDocument book = (HelpDocument) nodeInfo;
				displayNode(book);
			}
		}

		private void displayNode(HelpDocument bookInfo) {
			URL url = null;
			try {
				if (bookInfo != null) {
					url = bookInfo.bookURL;
				}
				if (url != null) {
					htmlPane.setPage(url);
				} else { // null url
					htmlPane.setText("File Not Found");
				}
			} catch (IOException e) {
				logger.error("Attempted to read a bad URL: " + url, e);
			}
		}

		private void createNodes(DefaultMutableTreeNode top) {
			top.add(new DefaultMutableTreeNode(HelpDocument.Introduction));
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(
					HelpDocument.Setting);
			node.add(new DefaultMutableTreeNode(HelpDocument.Download));
			node.add(new DefaultMutableTreeNode(HelpDocument.AccountCreation));
			node.add(new DefaultMutableTreeNode(HelpDocument.Startup));
			top.add(node);

			node = new DefaultMutableTreeNode(HelpDocument.Controls);
			node.add(new DefaultMutableTreeNode(HelpDocument.GameScreen));
			node.add(new DefaultMutableTreeNode(HelpDocument.BasicControls));
			node.add(new DefaultMutableTreeNode(HelpDocument.Communication));
			top.add(node);

			top.add(new DefaultMutableTreeNode(HelpDocument.Gameplay));
		}
	}

	/**
	 * a help document
	 */
	public static enum HelpDocument {
		Introduction("Introduction", "introduction.html"),

		Setting("Setting up the game", "setting.html"),
		Download("Downloading Stendhal", "setting-download.html"),
		AccountCreation("Creating an account", "setting-accountcreation.html"),
		Startup("Starting the Game", "setting-start.html"),

		Controls("Controls and Game settings", "controls.html"),
		GameScreen("Game Screen", "controls-gamescreen.html"),
		BasicControls("Basic Controls", "controls-basic.html"),
		Communication("Communication", "controls-communication.html"),

		Gameplay("Gameplay", "gameplay.html");

		private String bookName;
		private URL bookURL;

		private HelpDocument(String title, String filename) {
			bookName = title;
			bookURL = SpriteStore.get().getResourceURL("data/docu/" + filename);
			if (bookURL == null) {
				logger.error("Couldn't find file: " + filename);
			}
		}

		@Override
		public String toString() {
			return bookName;
		}
	}

}
