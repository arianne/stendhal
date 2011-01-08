/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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

/**
 * Appendable text component to be used as the chat log.
 */
public class KTextEdit extends JComponent {
	/** Point size of the text */
	protected static final int TEXT_SIZE = 11;
	/** Color of the time stamp written before the lines */ 
	protected static final Color HEADER_COLOR = Color.gray;

	/** Location for saving chat log file. */
	private static final String GAME_LOG_FILE = System.getProperty("user.home") 
		+ stendhal.STENDHAL_FOLDER + "gamechat.log";
	
	private static final long serialVersionUID = -698232821850852452L;
	private static final Logger logger = Logger.getLogger(KTextEdit.class);
	
	/** The actual text component for showing the chat log */
	protected JTextPane textPane;
	/** Scroll pane containing the text component */ 
	private JScrollPane scrollPane;
	/**
	 * <code>true</code> if the chat log should automatically scroll to the
	 * last line when more lines are added, <code>false</code> otherwise.
	 */
	private boolean autoScrollEnabled;
	
	
	/** Listener for opening the popup menu when it's requested. */
	private final class TextPaneMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(final MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			maybeShowPopup(e);
		}

		/**
		 * Show the chat log popup menu at operating system dependent triggers.
		 * Do nothing if the mouse event is not a popup trigger.
		 *  
		 * @param e mouse event that could potentially show the popup
		 */
		private void maybeShowPopup(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				final JPopupMenu popup = new JPopupMenu("save");

				JMenuItem menuItem = new JMenuItem("Save");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						save();
					}
				});
				popup.add(menuItem);
				
				menuItem = new JMenuItem("Clear");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						clear();
					}
				});
				popup.add(menuItem);
				
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	

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
	 * Initializes the basic styles.
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
	 * Get the style corresponding to a description and color.
	 * 
	 * @param desiredColor
	 *            the color with which the text must be colored
	 * @param styleDescription
	 *            which style to use (may not be normal)
	 * @return the colored style
	 */
	private Style getStyle(final Color desiredColor, final String styleDescription) {
		final Style s = textPane.getStyle(styleDescription);
		StyleConstants.setForeground(s, desiredColor);
		return s;
	}

	/**
	 * Insert a header.
	 * 
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

	/**
	 * Insert time stamp.
	 * 
	 * @param header time stamp
	 */
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

	/**
	 * Add text using a style defined for a notification type.
	 * 
	 * @param text
	 * @param type
	 */
	protected void insertText(final String text, final NotificationType type) {
		final Color color = type.getColor();
		final String styleDescription = type.getStyleDescription();
		final Document doc = textPane.getDocument();

		try {
			final FormatTextParser parser =	new FormatTextParser() {
				@Override
				public void normalText(final String txt) throws BadLocationException {
					doc.insertString(doc.getLength(), txt, getStyle(color,styleDescription));
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

	/**
	 * Start a new line.
	 */
	protected void insertNewline() {
		final Document doc = textPane.getDocument();
		try {
			doc.insertString(doc.getLength(), "\r\n", getStyle(Color.black, "normal"));
		} catch (final BadLocationException e) {
			logger.error("Couldn't insert initial text.", e);
		}
	}

	/**
	 * Scroll to the last line.
	 */
	private void scrollToBottom() {	
		final JScrollBar vbar = scrollPane.getVerticalScrollBar();
		vbar.setValue(vbar.getMaximum());
	}

	/**
	 * Add a new line with a specified header and content. The style will be
	 * chosen according to the type of the message.
	 *
	 * @param header
	 *            a string with the header
	 * @param line
	 *            a string representing the line to be printed
	 * @param type
	 *            The logical format type.
	 */
	private synchronized void addLine(final String header, final String line,
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
	
	/**
	 * Add a new line with a specified header and content. The style will be
	 * chosen according to the type of the message. Keep the view at the last
	 * line unless the user has scrolled higher.
	 *
	 * @param header
	 *            a string with the header
	 * @param line
	 *            a string representing the line to be printed
	 * @param type
	 *            The logical format type.
	 */
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
			}
		});
	}

	/**
	 * Set the auto scroll flag.
	 * 
	 * @param autoScrollEnabled <code>true</code> if the chat log view keeps
	 * 	at the last line, <code>false</code> otherwise 
	 */
	private void setAutoScrollEnabled(final boolean autoScrollEnabled) {
		this.autoScrollEnabled = autoScrollEnabled;
	}

	/**
	 * Check if the view should keep following the last line.
	 * 
	 * @return <code>true</code> if the view should be kept at bottom
	 */
	private boolean isAutoScrollEnabled() {
		return autoScrollEnabled;
	}

	/**
	 * Append an event line.
	 * 
	 * @param line 
	 */
	public void addLine(final EventLine line) {
		this.addLine(line.getHeader(), line.getText(), line.getType());
	}
	
	/**
	 * Clear the context.
	 */
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
	
	/**
	 * Save the contents into the log file and inform the user about it.
	 */
	public void save() {
		FileWriter fo;
		try {
			fo = new FileWriter(GAME_LOG_FILE);
			try {
				textPane.write(fo);
			} finally {
				fo.close();
			}

			addLine("", "Chat log has been saved to " + GAME_LOG_FILE, NotificationType.CLIENT);
		} catch (final Exception ex) {
			logger.error(ex, ex);
		}
	}
}
