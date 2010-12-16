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
package games.stendhal.client.gui.progress;

import games.stendhal.client.gui.InternalManagedWindow;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Progress status window. For displaying quest information.
 */
public class ProgressLog implements HyperlinkListener {
	/** Width of the window content */ 
	private static final int PAGE_WIDTH = 300;
	/** Height of the window content */
	private static final int PAGE_HEIGHT = 300;
	
	/** The enclosing window */
	private final InternalManagedWindow window;
	private final JButton header;
	/** The html area */
	private final JEditorPane contentArea;
	/** Scrolling component of the html area */
	private final JScrollPane scrollPane;
	
	/** Progress query action information for clicking the html links */
	private ProgressStatusQuery contentQuery;
	private ProgressStatusQuery headerQuery;
	
	/** Page caption. Shown above the content items in the html area */
	private String caption;
	
	/**
	 * Create a new ProgressLog.
	 * 
	 * @param handle window identifier for the window manager
	 * @param name name of the window
	 */
	ProgressLog(String handle, String name) {
		window = new InternalManagedWindow(handle, name);
		final JComponent page = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		
		header = new JButton();
		page.add(header);
		
		contentArea = new JEditorPane();
		contentArea.setContentType("text/html");
		contentArea.setEditable(false);
		contentArea.addHyperlinkListener(this);
		
		scrollPane = new JScrollPane(contentArea);
		page.add(scrollPane, SBoxLayout.constraint(SLayout.EXPAND_X,
				SLayout.EXPAND_Y));
		page.setPreferredSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));
		
		header.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (headerQuery != null) {
					headerQuery.fire(header.getText());
				}
			}
		});
		
		window.setContent(page);
	}
	
	/**
	 * Get the window component.
	 * 
	 * @return travel log window
	 */
	InternalManagedWindow getWindow() {
		return window;
	}
	
	void setContentsAvailable(boolean available) {
		header.setVisible(available);
	}
	
	void setPageHeader(String text, ProgressStatusQuery onClick) {
		headerQuery = onClick;
		header.setText(text);
	}
	
	/**
	 * Set the page caption. This must be called <b>before</b> setPageContent to
	 * have effect.
	 * 
	 * @param caption page caption
	 */
	void setPageCaption(String caption) {
		this.caption = caption;
	}
	
	/**
	 * Set the page contents. Each of the content strings is shown as its own
	 * paragraph.
	 * 
	 * @param contents content paragraphs
	 * @param onClick query information if a paragraph is clicked, or
	 *	<code>null</code> if the paragraphs should not be clickable.
	 */
	void setPageContent(List<String> contents, ProgressStatusQuery onClick) {
		StringBuilder text = new StringBuilder("<html>");
		if (caption != null) {
			text.append("<h2>");
			text.append(caption);
			text.append("</h2>");
		}
		for (String elem : contents) {
			// Make the elements clickable only if we have a handler for the
			// clicks
			if (onClick != null) {
				text.append("<a href=\"");
				text.append(elem);
				text.append("\">");
				text.append(elem);
				text.append("</a>");
			} else {
				text.append(elem);
			}
			text.append("<p>");
		}
		text.append("</html>");
		contentArea.setText(text.toString());
		contentQuery = onClick;
		
		/*
		 * Scroll to top. This needs to be pushed to the even queue, because
		 * otherwise the scroll event triggered by changing the text would run
		 * after this.
		 */
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});	
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if (contentQuery != null) {
				/*
				 * It would be more correct to read the parameter from the link
				 * target, but swing does not give access to that when it fails
				 * to parse it as an URL.
				 */ 
				contentQuery.fire(event.getDescription());
			}
		}
	}
}
