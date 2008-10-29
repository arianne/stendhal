package games.stendhal.client.gui;

import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.common.NotificationType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Date;

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

import org.apache.log4j.Logger;



public class KTextEdit extends JPanel {

	private static final long serialVersionUID = -698232821850852452L;
	private static final Logger logger = Logger.getLogger(KTextEdit.class);

	protected static final int TEXT_SIZE = 11;

	protected static final Color HEADER_COLOR = Color.gray;

	protected JTextPane textPane;

	
	
	private JScrollPane scrollPane;
	private boolean autoScrollEnabled;

	/**
	 * Basic Constructor.
	 */
	public KTextEdit() {
		buildGUI();
	}

	/**
	 * This method builds the Gui.
	 */
	protected void buildGUI() {
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setAutoscrolls(true);
		
		initStylesForTextPane(textPane);
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane(textPane);
		add(scrollPane, BorderLayout.CENTER);
		
	}

	/**
	 * Intializes the basic styles.
	 * @param textPane
	 *            the active text component
	 */
	protected void initStylesForTextPane(final JTextPane textPane) {

		final Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);

		final Style regular = textPane.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "Dialog");
		StyleConstants.setFontSize(regular, TEXT_SIZE);

		Style s = textPane.addStyle("normal", regular);
		StyleConstants.setBold(s, true);
		StyleConstants.setForeground(s, HEADER_COLOR);

		s = textPane.addStyle("bold", regular);
		StyleConstants.setFontSize(regular, TEXT_SIZE + 1);
		StyleConstants.setItalic(s, true);
		StyleConstants.setBold(s, true);
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
	 *            the color with which the text must be colored
	 * @return the colored style
	 */
	public Style getColor(final Color desiredColor) {
		final Style s = textPane.getStyle("normal");
		StyleConstants.setForeground(s, desiredColor);
		return s;
	}

	/**
	 * insert a header.
	 * @param header 
	 */
	protected void insertHeader(final String header) {
		final Document doc = textPane.getDocument();
		try {
			if (header.length() > 0) {
				doc.insertString(doc.getLength(), "<" + header + "> ",
						textPane.getStyle("header"));
			}
		} catch (final BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}

	protected void insertTimestamp(final String header) {
		final Document doc = textPane.getDocument();
		try {
			if (header.length() > 0) {
				doc.insertString(doc.getLength(), header,
						textPane.getStyle("timestamp"));
			}
		} catch (final BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}

	protected void insertText(final String text, final NotificationType type) {
		final Color color = type.getColor();
		final Document doc = textPane.getDocument();

		try {
			final FormatTextParser parser =	new FormatTextParser() {
				@Override
				public void normalText(final String txt) throws BadLocationException {
					doc.insertString(doc.getLength(), txt, getColor(color));
				}

				@Override
				public void colorText(final String txt) throws BadLocationException {
					doc.insertString(doc.getLength(), txt, textPane.getStyle("bold"));
				}
			};
			parser.format(text);
		} catch (final Exception ble) { 
			// BadLocationException
			System.err.println("Couldn't insert initial text.");
		}
	}

	protected void insertNewline() {
		final Document doc = textPane.getDocument();
		try {
			doc.insertString(doc.getLength(), "\r\n", getColor(Color.black));
		} catch (final BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}

	public void addLine(final String line) {
		addLine("", line);
	}

	public void addLine(final String header, final String line) {
		addLine(header, line, NotificationType.NORMAL);
	}

	private void scrollToBottom() {
		
		final JScrollBar vbar = scrollPane.getVerticalScrollBar();

		try {
			// We need to wait because we must not print further lines
			// before we have scrolled down.
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					vbar.setValue(vbar.getMaximum());
				}
			});
		} catch (final Exception e) {
			logger.error(e, e);
		}
	}

	/**
	 * The implemented method.
	 *
	 * @param header
	 *            a string with the header name
	 * @param line
	 *            a string representing the line to be printed
	 * @param type
	 *            The logical format type.
	 */
	public synchronized void addLine(final String header, final String line,
			final NotificationType type) {
	
		final JScrollBar vbar = scrollPane.getVerticalScrollBar();

		setAutoScrollEnabled((vbar.getValue() + vbar.getVisibleAmount() == vbar.getMaximum()));

		insertNewline();

		final java.text.Format formatter = new java.text.SimpleDateFormat("[HH:mm] ");
		final String dateString = formatter.format(new Date());
		insertTimestamp(dateString);

		insertHeader(header);
		insertText(line, type);

		
		if (isAutoScrollEnabled()) {
				scrollToBottom();
				textPane.setBackground(Color.white);
		} else {
				textPane.setBackground(Color.pink);
		}

	}

	public void setAutoScrollEnabled(final boolean autoScrollEnabled) {
	
		this.autoScrollEnabled = autoScrollEnabled;
	}

	public boolean isAutoScrollEnabled() {
		return autoScrollEnabled;
	}

	public void addLine(final EventLine line) {
		this.addLine(line.getHeader(), line.getText(), line.getType());
		
	}

	

}
