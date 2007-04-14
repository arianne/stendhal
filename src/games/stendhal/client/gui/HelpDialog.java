/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 * Summary description for CreateAccountDialog
 *
 */
public class HelpDialog extends JDialog {

	private static final long serialVersionUID = 4436228792112530975L;

	// Variables declaration
	private JLabel usernameLabel;

	private JLabel serverLabel;

	private JLabel serverPortLabel;

	private JLabel passwordLabel;
 

	private Frame owner;

	public HelpDialog(Frame owner) throws MalformedURLException, IOException {
		super(owner);
		this.owner = owner;
		initializeComponent();

		this.setVisible(true);
	}
	
	class Hyperactive implements HyperlinkListener {
		 
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                JEditorPane pane = (JEditorPane) e.getSource();
                if (e instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                    HTMLDocument doc = (HTMLDocument)pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                } else {
                    try {
                        pane.setPage(e.getURL());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
    }

	private void initializeComponent() throws MalformedURLException, IOException {

		JEditorPane editor = new JEditorPane(new URL("http://arianne.sourceforge.net/wiki/index.php?title=Stendhal_Manual/Gameplay"));
		editor.setEditable(false);
		editor.addHyperlinkListener(new Hyperactive());

		JScrollPane scroller = new JScrollPane(editor);
		add(scroller);

		// CreateAccountDialog
		this.setTitle("Stendhal Help");
		this.setResizable(true);
		this.setSize(new Dimension(350, 275));
		this.setLocationRelativeTo(owner);

	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		new HelpDialog(null);
	}
	
}
