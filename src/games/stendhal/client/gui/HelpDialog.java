package games.stendhal.client.gui;

import games.stendhal.client.SpriteStore;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * A help system that displays the manual
 *
 * @author hendrik (mostly based on "How to Use Trees" http://java.sun.com/docs/books/tutorial/uiswing/components/tree.html)
 */
public class HelpDialog extends JPanel implements TreeSelectionListener {
	private static final long serialVersionUID = 41013220176906825L;

	private JEditorPane htmlPane;
    private JTree tree;
    private URL helpURL;

    /**
     * Creates a new help dialog
     */
    public HelpDialog() {
        super(new GridLayout(1,0));

        //Create the nodes.
        DefaultMutableTreeNode top =
            new DefaultMutableTreeNode("The Java Series");
        createNodes(top);

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);

        //Create the HTML viewing pane.
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        initHelp();
        JScrollPane htmlView = new JScrollPane(htmlPane);

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(htmlView);

        Dimension minimumSize = new Dimension(200, 50);
        htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(200);
        splitPane.setPreferredSize(new Dimension(500, 300));

        //Add the split pane to this panel.
        add(splitPane);
    }

    /** 
     * Update the browser window on selection change in the tree 
     */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            BookInfo book = (BookInfo)nodeInfo;
            displayURL(book.bookURL);
        } else {
            displayURL(helpURL); 
        }
    }

    private class BookInfo {
        public String bookName;
        public URL bookURL;

        public BookInfo(String book, String filename) {
            bookName = book;
            bookURL = SpriteStore.get().getResourceURL("data/docu/" + filename);
            if (bookURL == null) {
                System.err.println("Couldn't find file: "
                                   + filename);
            }
        }

        public String toString() {
            return bookName;
        }
    }

    private void initHelp() {
        String s = "TreeDemoHelp.html";
        helpURL = HelpDialog.class.getResource(s);
        if (helpURL == null) {
            System.err.println("Couldn't open help file: " + s);
        }
        displayURL(helpURL);
    }

    private void displayURL(URL url) {
        try {
            if (url != null) {
                htmlPane.setPage(url);
            } else { //null url
		htmlPane.setText("File Not Found");
            }
        } catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url);
        }
    }

    private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;

        category = new DefaultMutableTreeNode("Books for Java Programmers");
        top.add(category);

        //original Tutorial
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Tutorial: A Short Course on the Basics",
            "gameplay.html"));
        category.add(book);

        //Tutorial Continued
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Tutorial Continued: The Rest of the JDK",
            "tutorialcont.html"));
        category.add(book);

        //JFC Swing Tutorial
        book = new DefaultMutableTreeNode(new BookInfo
            ("The JFC Swing Tutorial: A Guide to Constructing GUIs",
            "swingtutorial.html"));
        category.add(book);

        //Bloch
        book = new DefaultMutableTreeNode(new BookInfo
            ("Effective Java Programming Language Guide",
	     "bloch.html"));
        category.add(book);

        //Arnold/Gosling
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Programming Language", "arnold.html"));
        category.add(book);

        //Chan
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Developers Almanac",
             "chan.html"));
        category.add(book);

        category = new DefaultMutableTreeNode("Books for Java Implementers");
        top.add(category);

        //VM
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Virtual Machine Specification",
             "vm.html"));
        category.add(book);

        //Language Spec
        book = new DefaultMutableTreeNode(new BookInfo
            ("The Java Language Specification",
             "jls.html"));
        category.add(book);
    }
        
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TreeDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        HelpDialog newContentPane = new HelpDialog();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    
    /**
     * debug method
     *
     * @param args ignores
     */
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
 
}
