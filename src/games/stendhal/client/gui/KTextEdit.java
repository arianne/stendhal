package games.stendhal.client.gui;

import games.stendhal.client.stendhal;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.common.NotificationType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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


public class KTextEdit extends JComponent {
	protected static final int TEXT_SIZE = 11;

	protected static final Color HEADER_COLOR = Color.gray;

	private static final String GAME_LOG_FILE = System.getProperty("user.home") 
	+ stendhal.STENDHAL_FOLDER + "gamechat.log";
	
	
	private final class TextPaneMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(final MouseEvent e) {
	        maybeShowPopup(e);
	    }

		@Override
	    public void mouseReleased(final MouseEvent e) {
	        maybeShowPopup(e);
	    }

		private void maybeShowPopup(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				final JPopupMenu popup = new JPopupMenu("save");

				JMenuItem menuItem = new JMenuItem("save");
				menuItem.addActionListener(new ActionListener() {

					public void actionPerformed(final ActionEvent e) {
						save();

					}
				});
				popup.add(menuItem);
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private static final long serialVersionUID = -698232821850852452L;
	private static final Logger logger = Logger.getLogger(KTextEdit.class);

	
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
		
		textPane.addMouseListener(new TextPaneMouseListener());
		
		initStylesForTextPane(textPane);
		setLayout(new BorderLayout());
		
		scrollPane = new JScrollPane(textPane);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(final AdjustmentEvent ev) {
				JScrollBar bar = (JScrollBar) ev.getAdjustable();
				// Try to avoid turning the new message indicator off
				// while the player keeps adjusting the scroll bar to 
				// avoid missleading results
				if (!bar.getValueIsAdjusting() && (bar.getValue() + bar.getVisibleAmount() == bar.getMaximum())) {
						setUnreadLinesWarning(false);
						setAutoScrollEnabled(true);
				}
			}
		});
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
		} catch (final BadLocationException e) {
			logger.error("Couldn't insert initial text.", e);
		}
	}

	protected void insertTimestamp(final String header) {
		final Document doc = textPane.getDocument();
		try {
			if (header.length() > 0) {
				doc.insertString(doc.getLength(), header,
						textPane.getStyle("timestamp"));
			}
		} catch (final BadLocationException e) {
			logger.error("Couldn't insert initial text.", e);
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
		} catch (final Exception e) { 
			// BadLocationException
			logger.error("Couldn't insert initial text.", e);
		}
	}

	protected void insertNewline() {
		final Document doc = textPane.getDocument();
		try {
			doc.insertString(doc.getLength(), "\r\n", getColor(Color.black));
		} catch (final BadLocationException e) {
			logger.error("Couldn't insert initial text.", e);
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
		vbar.setValue(vbar.getMaximum());
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
		// do the whole thing in the event dispatch thread to ensure the generated 
		// events get handled in the correct order
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				handleAddLine(header, line, type);
			} else {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						handleAddLine(header, line, type);
					}
				});
			}
		} catch (final Exception e) {
			logger.error(e, e);
		}
	}
	
	private void handleAddLine(final String header, final String line, final NotificationType type) {
		final JScrollBar vbar = scrollPane.getVerticalScrollBar();
		final int currentLocation = vbar.getValue();
		
		setAutoScrollEnabled((vbar.getValue() + vbar.getVisibleAmount() == vbar.getMaximum()));
		insertNewline();

		final java.text.Format formatter = new java.text.SimpleDateFormat("[HH:mm] ");
		final String dateString = formatter.format(new Date());
		insertTimestamp(dateString);

		insertHeader(header);
		insertText(line, type);
		
		// wait a bit so that the scroll bar knows where it should scroll 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (isAutoScrollEnabled()) {
					scrollToBottom();
				} else {
					// the scroll bar insists changing its value, so jump back.
					// in a sane toolkit it would be possible to defer drawing
					// until this
					vbar.setValue(currentLocation);
					setUnreadLinesWarning(true);
				}
				// A workaround for swing being otherwise too stupid to do that
				// in certain conditions.
				// revalidate() etc do _not_ help
				textPane.paintImmediately(textPane.getVisibleRect());
			}
		});
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
	
	public void clear() {
		textPane.setText("");
	}
	
	/**
	 * Set a clear warning for the user that there are new, unread lines.
	 * @param warn true if the warning indicator should be shown, false otherwise
	 */
	private void setUnreadLinesWarning(final boolean warn) {
		if (warn) {
			textPane.setBackground(Color.pink);
		} else {
			textPane.setBackground(Color.white);
		}
	}
	
	
	public void save() {
		FileWriter fo;
		try {
			fo = new FileWriter(GAME_LOG_FILE);
			textPane.write(fo);
			fo.close();
			
			addLine("", "Chat log has been saved to " + GAME_LOG_FILE, NotificationType.CLIENT);
		} catch (final Exception ex) {
			logger.error(ex, ex);
		}
	}

}
