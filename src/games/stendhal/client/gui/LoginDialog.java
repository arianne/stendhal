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

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.common.Debug;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import marauroa.client.ariannexpTimeoutException;
import marauroa.client.io.Persistence;

/**
 * Summary description for LoginDialog
 * 
 */
public class LoginDialog extends JDialog implements Runnable {
	private static final long serialVersionUID = 4436228792112530975L;

	private static final String TCPIP_TEXT = "TCP/IP (default)";
		
	private static final String UDP_TEXT = "UDP";
		
	// Variables declaration
	private JLabel usernameLabel;

	private JLabel serverLabel;

	private JLabel serverPortLabel;

	private JLabel protocolLabel;

	private JLabel passwordLabel;

	private JCheckBox saveLoginBox;
	
	private JComboBox protocolComboBox;

	private JTextField usernameField;

	private JPasswordField passwordField;

	private JComboBox serverField;

	private JTextField serverPortField;

	private JButton loginButton;

	private JPanel contentPane;

	// End of variables declaration
	private StendhalClient client;

	private Frame owner;

	public LoginDialog(Frame owner, StendhalClient client) {
		super(owner, true);
		this.client = client;
		this.owner = owner;
		initializeComponent();

		this.setVisible(true);
	}

	private void initializeComponent() {
		serverLabel = new JLabel("Choose your Stendhal server");
		serverPortLabel = new JLabel("Enter the server port");
		protocolLabel = new JLabel("Choose the protocol");
		usernameLabel = new JLabel("Type your username");
		passwordLabel = new JLabel("Type your password");
		saveLoginBox = new JCheckBox("Remember login info");
		protocolComboBox = new JComboBox( new String[] {TCPIP_TEXT, UDP_TEXT} );
		usernameField = new JTextField();
		passwordField = new JPasswordField();
		serverField = new JComboBox();
		serverField.setEditable(true);
		serverPortField = new JTextField("32160");
		loginButton = new JButton();
		contentPane = (JPanel) this.getContentPane();

		String loginInfoString = getLoginInfo();
		StringTokenizer loginInfo = null;
		if (loginInfoString.indexOf("\n") > -1) {
			loginInfo = new StringTokenizer(loginInfoString, "\n");
		} else {
			loginInfo = new StringTokenizer(loginInfoString);
		}

		//
		// serverField
		//
		for (String server : stendhal.SERVERS_LIST) {
			serverField.addItem(server);
		}

		int tokens = loginInfo.countTokens();
		if (tokens >= 3) {
			saveLoginBox.setSelected(true);
			//
			// serverField
			//
			serverField.setSelectedItem(loginInfo.nextToken());
			//
			// usernameField
			//
			usernameField.setText(loginInfo.nextToken());
			//
			// passwordField
			//
			passwordField.setText(loginInfo.nextToken());
		}
		if (tokens >= 4) {
			//
			// serverPortField
			//
			serverPortField.setText(loginInfo.nextToken());
		}
		if (tokens >= 5) {
			//
			// protocolFiled
			//
			// NOTE: This used to be a checkbox, that's why it's still
			// stored as a boolean. True stands for TCP/IP, false for
			// UDP. We might want to change this someday later.
			if (Boolean.parseBoolean(loginInfo.nextToken())) {
				protocolComboBox.setSelectedItem(TCPIP_TEXT);
			} else {
				protocolComboBox.setSelectedItem(UDP_TEXT);
			}
		}
		// loginButton
		loginButton.setText("Login to Server");
		loginButton.setMnemonic(KeyEvent.VK_L);
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginButton_actionPerformed(e);
			}
		});
		//
		// contentPane
		//
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5,
				5)));
		GridBagConstraints c = new GridBagConstraints();

		// row 0
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(4, 4, 15, 4);
		c.gridx = 0;// column
		c.gridy = 0;// row
		contentPane.add(serverLabel, c);
		c.gridx = 1;
		c.gridy = 0;
		contentPane.add(serverField, c);
		// row 1
		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 1;
		contentPane.add(serverPortLabel, c);
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(serverPortField, c);
		c.gridx = 0;
		c.gridy = 2;
		contentPane.add(protocolLabel, c);
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(protocolComboBox, c);
		// row 2
		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 3;
		contentPane.add(usernameLabel, c);
		c.gridx = 1;
		c.gridy = 3;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(usernameField, c);
		// row 3
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(passwordLabel, c);
		c.gridx = 1;
		c.gridy = 4;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(passwordField, c);
		// row 4
		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(saveLoginBox, c);
		c.gridx = 1;
		c.gridy = 5;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(15, 4, 4, 4);
		contentPane.add(loginButton, c);

		if (Debug.WEB_START_SANDBOX) {
			// UDP is not supported in sandbox mode.
			protocolLabel.setVisible(false);
			protocolComboBox.setSelectedItem(TCPIP_TEXT);
			protocolComboBox.setEnabled(false);
			protocolComboBox.setVisible(false);
		}

		//
		// LoginDialog
		//
		this.setTitle("Login to Server");
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(owner);
	}

	private void loginButton_actionPerformed(ActionEvent e) {
		try {
			Integer.parseInt(serverPortField.getText().trim());
			// Support for saving port number. Only save when input is a number
			// intensifly@gmx.com

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"You typed in a invalid port, try again", "Invalid port",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		/* seprate thread for connection proccess added by TheGeneral */
		// run the connection procces in separate thread
		Thread m_connectionThread = new Thread(this);
		m_connectionThread.start();
	}

	public void run() {
		final String username = usernameField.getText().trim();
		final String password = new String(passwordField.getPassword());
		final String server = ((String) serverField.getSelectedItem()).trim();
		final int port = Integer.parseInt(serverPortField.getText().trim());
		final ProgressBar progressBar = new ProgressBar(this);
		final boolean useTCP = protocolComboBox.getSelectedItem() == TCPIP_TEXT;
		final boolean saveLoginBoxStatus = saveLoginBox.isSelected();

		progressBar.start();// intialize progress bar
		setEnabled(false);  // disable this screen when attempting to
							// connect
		
		if (saveLoginBoxStatus) {
			saveLoginInfo(server, username, password, serverPortField.getText(), useTCP);
		}

		try {
			client.connect(server, port, useTCP);
			progressBar.step();// for each major connection milestone
								// call step()
		} catch (Exception ex) {
			progressBar.cancel();// if something goes horribly just
									// cancel the progressbar
			setEnabled(true);
			JOptionPane
					.showMessageDialog(this,
							"Stendhal can't connect to server. Did you misspell the server name?");

			ex.printStackTrace();

			return;
		}

		try {
			if (client.login(username, password) == false) {
				String result = client.getEvent();
				if (result == null) {
					result = "Server is not available right now. Check it is online";
				}
				progressBar.cancel();
				setVisible(true);
				JOptionPane.showMessageDialog(this, result,
						"Login status", JOptionPane.ERROR_MESSAGE);
			} else {
				progressBar.step();
				progressBar.finish();

				setVisible(false);
				owner.setVisible(false);
				stendhal.doLogin = true;
			}
		} catch (ariannexpTimeoutException ex) {
			progressBar.cancel();
			setEnabled(true);
			JOptionPane.showMessageDialog(this,
					"Can't connect to server. Server down?",
					"Login status", JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			progressBar.cancel();
			setEnabled(true);
			JOptionPane.showMessageDialog(this,
					"Connection error. Online?", "Login status",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	
	/*
	 * Author: Da_MusH Description: Methods for saving and loading login
	 * information to disk. These should probably make a separate class in the
	 * future, but it will work for now. comment: Thegeneral has added encoding
	 * for password and username
	 */

	private void saveLoginInfo(String server, String usrName, String pwd,
			String port, boolean useTCP) {
		Encoder encode = new Encoder();
		
			try {
				OutputStream os = Persistence.get().getOutputStream("user.dat");
				PrintStream ps = new PrintStream(os);
	
				ps.print(encode.encode(server + "\n" + usrName + "\n" + pwd + "\n"
						+ port + "\n" + Boolean.valueOf(useTCP).toString()));
				ps.close();
			} catch (IOException ioex) {
				JOptionPane
						.showMessageDialog(
								this,
								"Something went wrong when saving login information, nothing saved",
								"Login information save problem",
								JOptionPane.WARNING_MESSAGE);
			}
	}

	private String getLoginInfo() {
		Encoder decode = new Encoder();
		String loginLine = "";

		try {
			InputStream is = Persistence.get().getInputStream("user.dat");
			BufferedReader fin = new BufferedReader(new InputStreamReader(is));

			loginLine = decode.decode(fin.readLine());
			if (loginLine == null)
				loginLine = "no_data";
			fin.close();
		} catch (FileNotFoundException fnfe) {
			loginLine = "no_file";
		} catch (IOException ioex) {
			JOptionPane
					.showMessageDialog(
							this,
							"Something went wrong when loading login information, nothing loaded",
							"Login information load problem",
							JOptionPane.WARNING_MESSAGE);
		}

		return loginLine;
	}

	public static void main(String args[]) {
		new LoginDialog(null, null).setVisible(true);
	}
}
