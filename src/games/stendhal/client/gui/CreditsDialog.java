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

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.common.Debug;

import games.stendhal.client.gui.ScrollerPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.net.URL;
import org.apache.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;

public class CreditsDialog extends JDialog {
//	private static final Logger logger = Log4J.getLogger(CreditsDialog.class);
	private static final Logger logger = Logger.getLogger(CreditsDialog.class);
	private static String[] credits = { "Thanks everyone! Credits goes to: ",
																			"",
																			"Hendrik Brummermann",
																			"For the most active developer around, and stating logged into IRC to keep a record",
																			"",
																			"Miguel Angel Blanch Lardin",
																			"For without him, we'd never even have this wonderous game!",
																			"",
																			"The asorted developers out there, who make the above two's lives living hell! ;)",
																			"",
																			"And finally, you, for choosing to download this game and (hopefully) spread the word about it"};
	private ScrollerPanel sp;
	private JPanel buttonPane = new JPanel();
	private JButton closeButton = new JButton("Close");
	
	private Color backgroundColor = Color.white;
	
	private Font textFont = new Font("SansSerif", Font.BOLD, 12);
	private Color textColor = new Color(85, 85, 85);
	
	/**
	 * Summary goes here
	 * 
	 */
	public CreditsDialog(Frame owner) throws HeadlessException {
		super(owner, true);
		initGUI(owner);
		logger.debug("about dialog initialized");
		eventHandling();
		logger.debug("about dialog event handling ready");

		this.setTitle("Stendhal Credits");
//		this.setResizable(false);
//		this.pack();
		this.setLocationRelativeTo(owner);
		this.setSize(owner.getSize());
		this.setVisible(true);
	}
	
	private void initGUI(Frame owner) {
		this.getContentPane().setLayout( new BorderLayout());
		this.getContentPane().setBackground( backgroundColor );
		sp = new ScrollerPanel( credits, textFont, 0, textColor, backgroundColor, 20 );		
		
		buttonPane.setOpaque(false);
		buttonPane.add( closeButton );

		this.getContentPane().add( sp, BorderLayout.CENTER );
		this.getContentPane().add( buttonPane, BorderLayout.SOUTH);
	}
	
	/**
	 * setting up the listeners an event handling
	 */
	private void eventHandling() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
	}
	
	/**
	 * exit Credits Dialog
	 */
	private void exit() {
		sp.stop();
		this.setVisible( false );
		this.dispose();
		logger.debug("about dialog closed");
	}
}
