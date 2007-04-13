package games.stendhal.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * User: lsoubrev122203 Date: May 9, 2005 Time: 10:02:40 AM
 */

public class KTextEdit extends JPanel {

	private static final long serialVersionUID = -698232821850852452L;

	private static final int TEXT_SIZE = 11;

	private static final Color HEADER_COLOR = Color.gray;

	private JTextPane textPane;

	private JScrollPane scrollPane;

	/**
	 * Basic Constructor
	 */
	public KTextEdit() {
		buildGUI();
	}

	/**
	 * This method builds the Gui
	 */
	private void buildGUI() {
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setAutoscrolls(true);
		initStylesForTextPane(textPane);
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane(textPane);
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * @param textPane
	 *            the active text component
	 */
	private void initStylesForTextPane(JTextPane textPane) {
		// Initialize the basics styles.
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

		Style regular = textPane.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "Dialog");
		StyleConstants.setFontSize(regular, TEXT_SIZE);

		Style s = textPane.addStyle("normal", regular);
		StyleConstants.setBold(s, true);
		StyleConstants.setForeground(s, HEADER_COLOR);

		s = textPane.addStyle("bold", regular);
		StyleConstants.setFontSize(regular, TEXT_SIZE + 1);
		StyleConstants.setItalic(s, true);
		StyleConstants.setBold(s, false);
		StyleConstants.setForeground(s, Color.blue);

		s = textPane.addStyle("header", regular);
		StyleConstants.setItalic(s, true);
		StyleConstants.setFontSize(s, TEXT_SIZE);
		StyleConstants.setForeground(s, HEADER_COLOR);

		s = textPane.addStyle("timestamp", regular);
		StyleConstants.setItalic(s, true);
		StyleConstants.setFontSize(s, TEXT_SIZE - 1);
		StyleConstants.setForeground(s, HEADER_COLOR);
	}

	/**
	 * @param desiredColor
	 *            the color with wich the text must be colored
	 * @return the colored style
	 */
	public Style getColor(Color desiredColor) {
		Style s = textPane.getStyle("normal");
		StyleConstants.setForeground(s, desiredColor);
		return s;
	}

	/**
	 * clear the text
	 */
	// Not needed, consider deletion
//	public void clearText() {
//		textPane.setText("");
//	}

	/**
	 * insert a header
	 */
	private void insertHeader(String header) {
		Document doc = textPane.getDocument();
		try {
			if (header.length() > 0) {
				doc.insertString(doc.getLength(), "<" + header + "> ", textPane.getStyle("header"));
			}
		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}

	private void insertTimestamp(String header) {
		Document doc = textPane.getDocument();
		try {
			if (header.length() > 0) {
				doc.insertString(doc.getLength(), header, textPane.getStyle("timestamp"));
			}
		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}

	private void insertText(String text, Color color) {
		Document doc = textPane.getDocument();
		try {
			String[] parts = text.split("#");

			int i = 0;
			for (String pieces : parts) {
				if (i > 0) {
					int index = pieces.indexOf(" ");
					if (index == -1) {
						index = pieces.length();
					}

					doc.insertString(doc.getLength(), pieces.substring(0, index), textPane.getStyle("bold"));
					pieces = pieces.substring(index);
				}

				doc.insertString(doc.getLength(), pieces, getColor(color));
				i++;
			}
		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}
	
	private void insertNewline() {
		Document doc = textPane.getDocument();
		try {
			doc.insertString(doc.getLength(), "\r\n", getColor(Color.black));
		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}


	public void addLine(String header, String line) {
		addLine(header, line, Color.black);
	}


	public void addLine(String line) {
		addLine(line, Color.black);
	}
	
	private void scrollToBottom() {
		// This didn't scroll all the way down. :(
		// textPane.setCaretPosition(textPane.getDocument().getLength());
		
		final JScrollBar vbar = scrollPane.getVerticalScrollBar();

		try {
			// We need to wait because we must not print further lines
			// before we have scrolled down.
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					vbar.setValue(vbar.getMaximum());					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();					
		}
	}


	/**
	 * The implemented method
	 * 
	 * @param header
	 *            a string with the header name
	 * @param line
	 *            a string representing the line to be printed
	 * @param color
	 *            the desired color
	 */
	public synchronized void addLine(String header, String line, Color color) {

		// Goal of the new code is making it easier to read older messages:
		// The client should only scroll down automatically if the scrollbar
		// was at the bottom before.
		// There were some bugs, so it is disabled until there is time to fix it.
		boolean useNewCode = false;
		
		// Determine whether the scrollbar is currently at the very bottom
		// position. We will only auto-scroll down if the user is not currently
		// reading old texts (like IRC clients do).
		final JScrollBar vbar = scrollPane.getVerticalScrollBar();
		
		boolean autoScroll = (vbar.getValue() + vbar.getVisibleAmount() == vbar.getMaximum());
//		System.out.println();
//		System.out.println(line);
//		System.out.println("value:      " + vbar.getValue());
//		System.out.println("visible:    " + vbar.getVisibleAmount());
//		System.out.println("maximum:    " + vbar.getMaximum());
//		System.out.println("autoscroll: " + autoScroll);

		insertNewline();

		java.text.Format formatter = new java.text.SimpleDateFormat("[HH:mm] ");
		String dateString = formatter.format(new Date());
		insertTimestamp(dateString);

		insertHeader(header);
		insertText(line, color);

		if (useNewCode) {
			if (autoScroll) {
				if (SwingUtilities.isEventDispatchThread()) {
					// you can't call invokeAndWait from the event dispatch thread.
					new Thread() {
						public void run() {
							scrollToBottom();
						}
					}.start();
				} else {
					scrollToBottom();
				}
			}
		} else {
			textPane.setCaretPosition(textPane.getDocument().getLength());
		}

	}

	/**
	 * @param line
	 *            a string representing the line to be printed
	 * @param color
	 *            the desired color
	 */
	public void addLine(String line, Color color) {
		addLine("", line, color);
	}

	/**
	 * Da main to make unit tests
	 */
	public static void main(String args[]) {
		/*
		 * BUG: This short example doesn't work. It doesn't throw any exception
		 * or anything. It just doesn't work. If you remove all teh edit.addLine
		 * but one it works. Can anyone with Swing/AWT abilities have a look to
		 * it?
		 */
		JFrame frame = new JFrame("KTextEdit Test-Frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		KTextEdit edit = new KTextEdit();
		frame.getContentPane().add(edit);
		edit.addLine("Well, there is really not #much to tell #about. !", Color.red);
		// edit.addLine("Well, there is really not much to tell about. !",
		// Color.blue);
		// edit.addLine("Well, there is really not much to tell about. !",
		// Color.green);
		// edit.addLine("Well, there is really not much to tell about. !", new
		// Color(240, 123, 56));
		// edit.addLine("Well, there is really not much to tell about. !",
		// Color.yellow);
		//
		// edit.addLine("Shaku", "Well, there is really not much to tell about.
		// !", Color.red);
		// edit.addLine("Keanu", "Well, there is really not much to tell about.
		// !", Color.blue);
		// edit.addLine("Bob", "Well, there is really not much to tell about.
		// !", Color.green);
		// edit.addLine("Dragon", "Well, there is really not much to tell about.
		// !", new Color(240, 123, 56));
		// edit.addLine("Little ant", "Well, there is really not much to tell
		// about. !", Color.yellow);
		//
		frame.setPreferredSize(new Dimension(400, 200));
		frame.pack();
		frame.setVisible(true);
	}
}
