/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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
import games.stendhal.client.gui.chatlog.ChatTextSink;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.textformat.StringFormatter;
import games.stendhal.client.gui.textformat.StyleSet;
import games.stendhal.common.NotificationType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.log4j.Logger;

/**
 * Appendable text component to be used as the chat log.
 */
class KTextEdit extends JComponent {
	/** Color of the time stamp written before the lines. */
	protected static final Color HEADER_COLOR = Color.gray;

	private static final long serialVersionUID = -698232821850852452L;
	private static final Logger logger = Logger.getLogger(KTextEdit.class);

	/** The actual text component for showing the chat log. */
	JTextPane textPane;
	/** Scroll pane containing the text component. */
	private JScrollPane scrollPane;
	/** Name of the log. */
	private String name = "";
	/** Background color when not highlighting unread messages. */
	private Color defaultBackground = Color.white;
	/** Formatting class for text containing stendhal markup. */
	private final StringFormatter<Style, StyleSet> formatter = new StringFormatter<Style, StyleSet>();
	private StyleSet defaultAttributes;

	/** Listener for opening the popup menu when it's requested. */
	private final class TextPaneMouseListener extends MousePopupAdapter {
		@Override
		protected void showPopup(final MouseEvent e) {
			final JPopupMenu popup = new JPopupMenu("save");

			JMenuItem menuItem = new JMenuItem("Save");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					save();
				}
			});
			popup.add(menuItem);

			menuItem = new JMenuItem("Clear");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					clear();
				}
			});
			popup.add(menuItem);

			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void setFont(Font font) {
		/*
		 * Dynamic font size changing tries to set the default font for
		 * KTextEdit. We don't use it, but we can signal the change of font
		 * sizes to textPane. Traditionally the chat log has used one point
		 * smaller font than the rest of the UI, so we keep that practice.
		 */
		initStylesForTextPane(textPane, font.getSize() - 1);
	}

	/**
	 * Basic Constructor.
	 */
	KTextEdit() {
		buildGUI();
	}

	/**
	 * This method builds the Gui.
	 */
	protected void buildGUI() {
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setAutoscrolls(true);
		// Turn off caret following. VerticalScrollBarModel takes care of
		// automatic scrolling
		Caret caret = textPane.getCaret();
		if (caret instanceof DefaultCaret) {
			((DefaultCaret) caret).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		} else {
			logger.warn("Failed to turn off caret following");
		}

		textPane.addMouseListener(new TextPaneMouseListener());

		initStylesForTextPane(textPane, textPane.getFont().getSize());
		setLayout(new BorderLayout());

		scrollPane = new JScrollPane(textPane) {
			@Override
			public JScrollBar createVerticalScrollBar() {
				JScrollBar bar = super.createVerticalScrollBar();
				bar.setModel(new VerticalScollbarModel());
				return bar;
			}
		};

		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(final AdjustmentEvent ev) {
				JScrollBar bar = (JScrollBar) ev.getAdjustable();
				// Try to avoid turning the new message indicator off
				// while the player keeps adjusting the scroll bar to
				// avoid missleading results
				if (!bar.getValueIsAdjusting() && isAtMaximum(bar)) {
					setUnreadLinesWarning(false);
				}
			}
		});

		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Initializes the basic styles.
	 *
	 * @param textPane
	 *            the active text component
	 * @param mainTextSize size of regular text
	 */
	protected void initStylesForTextPane(final JTextPane textPane, int mainTextSize) {
		// ****** General style definitions for the text pane ******
		Style regular = textPane.getStyle("regular");
		if (regular == null) {
			final Style def = StyleContext.getDefaultStyleContext().getStyle(
					StyleContext.DEFAULT_STYLE);
			regular = textPane.addStyle("regular", def);
			StyleConstants.setFontFamily(def, "Dialog");
		}
		StyleConstants.setFontSize(regular, mainTextSize);

		Style s = textPane.getStyle("normal");
		if (s == null) {
			s = textPane.addStyle("normal", regular);
			StyleConstants.setBold(s, true);
			StyleConstants.setForeground(s, HEADER_COLOR);
		}

		s = textPane.getStyle("bold");
		if (s == null) {
			s = textPane.addStyle("bold", regular);
			StyleConstants.setItalic(s, true);
			StyleConstants.setBold(s, true);
			StyleConstants.setForeground(s, Color.blue);
		}
		StyleConstants.setFontSize(regular, mainTextSize + 1);

		s = textPane.getStyle("header");
		if (s == null) {
			s = textPane.addStyle("header", regular);
			StyleConstants.setItalic(s, true);
			StyleConstants.setForeground(s, HEADER_COLOR);
		}
		StyleConstants.setFontSize(s, mainTextSize);

		s = textPane.getStyle("timestamp");
		if (s == null) {
			s = textPane.addStyle("timestamp", regular);
			StyleConstants.setItalic(s, true);
			StyleConstants.setForeground(s, HEADER_COLOR);
		}
		StyleConstants.setFontSize(s, mainTextSize - 1);
		
		//****** Styles used by the string formatter ******
		defaultAttributes = new StyleSet(StyleContext.getDefaultStyleContext(), regular);
		
		StyleSet attributes = defaultAttributes.copy();
		attributes.setAttribute(StyleConstants.Italic, true);
		attributes.setAttribute(StyleConstants.Foreground, Color.blue);
		formatter.addStyle('#', attributes);

		attributes = defaultAttributes.copy();
		attributes.setAttribute(StyleConstants.Underline, true);
		formatter.addStyle('§', attributes);
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
	 * @param header header string
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
	 * @param text text contents
	 * @param type type for formatting
	 */
	protected void insertText(final String text, final NotificationType type) {
		ChatTextSink dest = new ChatTextSink(textPane.getDocument());
		StyleSet set = new StyleSet(StyleContext.getDefaultStyleContext(), getStyle(type.getColor(), type.getStyleDescription()));
		set.setAttribute(StyleConstants.Foreground, type.getColor());

		formatter.format(text, set, dest);
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
	private void addLine(final String header, final String line,
			final NotificationType type) {
		// do the whole thing in the event dispatch thread to ensure the generated
		// events get handled in the correct order
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				handleAddLine(header, line, type);
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						handleAddLine(header, line, type);
					}
				});
			}
		} catch (final RuntimeException e) {
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
		insertNewline();

		final java.text.Format formatter = new java.text.SimpleDateFormat("[HH:mm] ");
		final String dateString = formatter.format(new Date());
		insertTimestamp(dateString);

		insertHeader(header);
		insertText(line, type);
	}

	/**
	 * Check if a scroll bar is at its maximum value.
	 *
	 * @param bar scroll bar
	 * @return <code>true</code> if the scrollbar is at its maximum value
	 * 	location, <code>false</code>otherwise
	 */
	private boolean isAtMaximum(JScrollBar bar) {
		return (bar.getValue() + bar.getVisibleAmount() >= bar.getMaximum());
	}

	/**
	 * Append an event line.
	 *
	 * @param line event line
	 */
	void addLine(final EventLine line) {
		this.addLine(line.getHeader(), line.getText(), line.getType());
	}

	/**
	 * Clear the context.
	 */
	void clear() {
		textPane.setText("");
	}

	/**
	 * Set the background color to be used normally, when not highlighting
	 * unread messages.
	 *
	 * @param color background color
	 */
	void setDefaultBackground(Color color) {
		defaultBackground = color;
	}

	/**
	 * Set the name of the logged channel.
	 *
	 * @param name channel name
	 */
	void setChannelName(String name) {
		this.name = name;
	}

	/**
	 * Set a clear warning for the user that there are new, unread lines.
	 * @param warn true if the warning indicator should be shown, false otherwise
	 */
	private void setUnreadLinesWarning(final boolean warn) {
		if (warn) {
			textPane.setBackground(Color.pink);
		} else {
			textPane.setBackground(defaultBackground);
		}
	}

	/**
	 * Get name of the file where logs should be saved on request.
	 *
	 * @return file name
	 */
	private String getSaveFileName() {
		if ("".equals(name)) {
			return stendhal.getGameFolder() + "gamechat.log";
		} else {
			return stendhal.getGameFolder() + "gamechat-" + name + ".log";
		}
	}

	/**
	 * Save the contents into the log file and inform the user about it.
	 */
	private void save() {
		String fname = getSaveFileName();
		Writer fo;
		try {
			fo = new OutputStreamWriter(new FileOutputStream(fname), "UTF-8");
			try {
				textPane.write(fo);
			} finally {
				fo.close();
			}

			addLine("", "Chat log has been saved to " + fname, NotificationType.CLIENT);
		} catch (final IOException ex) {
			logger.error(ex, ex);
		}
	}

	/**
	 * A custom range model that implements the automatically scrolling pane.
	 * Keeps the scrollbar at bottom, if it it was there before.
	 */
	private class VerticalScollbarModel extends DefaultBoundedRangeModel {
		@Override
		public void setRangeProperties(int value, int extent, int min, int max,
				boolean adjusting) {
			boolean atBottom = getValue() + getExtent() >= getMaximum();
			if (atBottom && (value == getValue())) {
				// We are at bottom, use adjusted values to ensure we stay
				// at bottom
				value = Math.min(Math.max(max - extent, min), max);
			} else if (max > getMaximum()) {
				// Not at bottom. Keep the old location.
				value = Math.min(Math.max(getValue(), min), max);
				setUnreadLinesWarning(true);
			}
			super.setRangeProperties(value, extent, min, max, adjusting);
		}
	}
}
