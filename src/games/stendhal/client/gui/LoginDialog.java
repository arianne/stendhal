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
import games.stendhal.client.gui.login.Profile;
import games.stendhal.client.gui.login.ProfileList;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

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
import marauroa.common.io.Persistence;

/**
 * Server login dialog.
 * 
 */
public class LoginDialog extends JDialog {
//	private static final long serialVersionUID = 4436228792112530975L;

	private static final String TCPIP_TEXT = "TCP/IP (default)";
		
	private static final String UDP_TEXT = "UDP";
		
	// Variables declaration

	private JComboBox profilesComboBox;

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

	protected ProfileList	profiles;


	public LoginDialog(Frame owner, StendhalClient client) {
		super(owner, true);
		this.client = client;
		this.owner = owner;
		initializeComponent();

		this.setVisible(true);
	}

	private void initializeComponent() {
		JLabel	l;


		this.setTitle("Login to Server");
		this.setResizable(false);

		//
		// contentPane
		//
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;


		/*
		 * Profiles
		 */
		l = new JLabel("Account profiles");

		c.insets = new Insets(4, 4, 15, 4);
		c.gridx = 0;// column
		c.gridy = 0;// row
		contentPane.add(l, c);

		profilesComboBox = new JComboBox();
		profilesComboBox.addActionListener(new ProfilesCB());

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(profilesComboBox, c);


		/*
		 *  Server Host
		 */
		l = new JLabel("Stendhal server");

		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;// column
		c.gridy = 1;// row
		contentPane.add(l, c);

		serverField = new JComboBox();
		serverField.setEditable(true);

		//
		// Standard Servers
		//
		for (String server : stendhal.SERVERS_LIST) {
			serverField.addItem(server);
		}

		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(serverField, c);


		/*
		 * Server Port
		 */
		l = new JLabel("Server port");

		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 2;
		contentPane.add(l, c);

		serverPortField = new JTextField("32160");

		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(serverPortField, c);


		/*
		 * Network Protocol
		 */
		l = new JLabel("Network protocol");

		c.gridx = 0;
		c.gridy = 3;
		contentPane.add(l, c);

		protocolComboBox = new JComboBox(
			new String[] {TCPIP_TEXT, UDP_TEXT} );

		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(protocolComboBox, c);

		/*
		 * What if we want to "save" a profile in TCP mode, and just
		 * use UDP when forced to?
		 */
		if (Debug.WEB_START_SANDBOX) {
			// UDP is not supported in sandbox mode.
			l.setEnabled(false);
//			l.setVisible(false);

			protocolComboBox.setSelectedItem(TCPIP_TEXT);
			protocolComboBox.setEnabled(false);
//			protocolComboBox.setVisible(false);
		}


		/*
		 * Username
		 */
		l = new JLabel("Type your username");

		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 4;
		contentPane.add(l, c);

		usernameField = new JTextField();

		c.gridx = 1;
		c.gridy = 4;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(usernameField, c);


		/*
		 * Password
		 */
		l = new JLabel("Type your password");

		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(l, c);

		passwordField = new JPasswordField();

		c.gridx = 1;
		c.gridy = 5;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(passwordField, c);


		/*
		 * Save Profile/Login
		 */
		saveLoginBox = new JCheckBox("Save login profile (on this machine)");

		c.gridx = 0;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(saveLoginBox, c);

		loginButton = new JButton();
		loginButton.setText("Login to Server");
		loginButton.setMnemonic(KeyEvent.VK_L);

		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginButton_actionPerformed(e);
			}
		});

		c.gridx = 1;
		c.gridy = 6;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(15, 4, 4, 4);
		contentPane.add(loginButton, c);


		/*
		 * Load saved profiles
		 */
		profiles = loadProfiles();
		populateProfiles(profiles);


		//
		// Dialog
		//

		this.pack();
		this.setLocationRelativeTo(owner);
	}


	private void loginButton_actionPerformed(ActionEvent e) {
		Profile	profile;


		profile = new Profile();

		profile.setHost(
			((String) serverField.getSelectedItem()).trim());

		try {
			profile.setPort(
				Integer.parseInt(
					serverPortField.getText().trim()));

			// Support for saving port number. Only save when input is a number
			// intensifly@gmx.com

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this,
				"That is not a valid port number. Please try again.", "Invalid port",
				JOptionPane.WARNING_MESSAGE);
			return;
		}

		profile.setUser(usernameField.getText().trim());
		profile.setPassword(new String(passwordField.getPassword()));

		profile.setTCP(
			protocolComboBox.getSelectedItem() == TCPIP_TEXT);


		/*
		 * Save profile?
		 */
		if(saveLoginBox.isSelected()) {
			profiles.add(profile);
			populateProfiles(profiles);
			saveProfiles(profiles);
		}


		/*
		 * Run the connection procces in separate thread.
		 * added by TheGeneral
		 */
		Thread t = new Thread(new ConnectRunnable(profile));
		t.start();
	}


	/**
	 * Add an item to a combobox if it doesn't already exist.
	 *
	 *
	 */
	protected void augmentComboBox(JComboBox combobox, Object item) {
		int	i;


		i = combobox.getItemCount();

		while(i-- != 0) {
			if(combobox.getItemAt(i).equals(item))
				break;
		}

		if(i == -1)
			combobox.addItem(item);
	}


	/**
	 * Connect to a server using a given profile.
	 *
	 *
	 */
	protected void connect(Profile profile) {
		final ProgressBar progressBar = new ProgressBar(this);
		boolean	useTCP;


		// intialize progress bar
		progressBar.start();

		// disable this screen when attempting to connect
		setEnabled(false);

		// Force TCP for Web Start Sandbox
		useTCP = profile.isTCP() || Debug.WEB_START_SANDBOX;
		
		try {
			client.connect(
				profile.getHost(), profile.getPort(), useTCP);

			// for each major connection milestone call step()
			progressBar.step();
		} catch (Exception ex) {
			// if something goes horribly just cancel the progressbar
			progressBar.cancel();
			setEnabled(true);

			JOptionPane.showMessageDialog(this,
				"Unable to connect to server. Did you misspell the server name?");

			ex.printStackTrace();

			return;
		}

		try {
			if (client.login(profile.getUser(), profile.getPassword()) == false) {
				String result = client.getEvent();

				if (result == null) {
					result = "Server is not available right now. The server " +
						"may be down or, if you are using a custom server, " +
						"you may have entered its name and port number incorrectly.";
				}

				progressBar.cancel();
				setEnabled(true);

				JOptionPane.showMessageDialog(
					this, result,
					"Error Logging In", JOptionPane.ERROR_MESSAGE);
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
				"Server does not respond. The server may be down or, " +
				"if you are using a custom server, you may have entered " +
				"its name and port number incorrectly.",
				"Error Logging In", JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			progressBar.cancel();
			setEnabled(true);

			JOptionPane.showMessageDialog(this,
				"Stendhal cannot connect. Please check that your connection " +
				"is set up and active, then try again.", "Error Logging In",
				JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Load saves profiles.
	 */
	private ProfileList loadProfiles() {
		ProfileList profiles;


		profiles = new ProfileList();

		try {
			InputStream is = Persistence.get().getInputStream(
				true, "stendhal", "user.dat");

			try {
				profiles.load(is);
			} finally {
				is.close();
			}
		} catch (FileNotFoundException fnfe) {
			// Ignore
		} catch (IOException ioex) {
			JOptionPane.showMessageDialog(
				this,
				"An error occurred while loading your login information",
				"Error Loading Login Information",
				JOptionPane.WARNING_MESSAGE);
		}

		return profiles;
	}


	/**
	 * Populate the profiles combobox and select the default.
	 */
	protected void populateProfiles(ProfileList profiles) {
		Iterator	iter;
		int		count;


		profilesComboBox.removeAllItems();

		iter = profiles.iterator();

		while(iter.hasNext())
			profilesComboBox.addItem(iter.next());

		/*
		 * The last profile (if any) is the default.
		 */
		if((count = profilesComboBox.getItemCount()) != 0)
			profilesComboBox.setSelectedIndex(count - 1);
	}


	/**
	 * Called when a profile selection is changed.
	 */
	protected void profilesCB() {
		Profile	profile;
		String	host;


		profile = (Profile) profilesComboBox.getSelectedItem();

		if(profile != null) {
			host = profile.getHost();

			augmentComboBox(serverField, host);
			serverField.setSelectedItem(host);

			serverPortField.setText(
				String.valueOf(profile.getPort()));

			if(profile.isTCP())
				protocolComboBox.setSelectedItem(TCPIP_TEXT);
			else
				protocolComboBox.setSelectedItem(UDP_TEXT);

			usernameField.setText(profile.getUser());
			passwordField.setText(profile.getPassword());
		} else {
			serverField.setSelectedIndex(0);

			serverPortField.setText(
				String.valueOf(Profile.DEFAULT_SERVER_PORT));

			protocolComboBox.setSelectedItem(TCPIP_TEXT);

			usernameField.setText("");
			passwordField.setText("");
		}
	}


	/*
	 * Author: Da_MusH Description: Methods for saving and loading login
	 * information to disk. These should probably make a separate class in the
	 * future, but it will work for now. comment: Thegeneral has added encoding
	 * for password and username.
	 * Changed for multiple profiles.
	 */
	private void saveProfiles(ProfileList profiles) {
		try {
			OutputStream os = Persistence.get().getOutputStream(
				true, "stendhal", "user.dat");

			try {
				profiles.save(os);
			}
			finally {
				os.close();
			}
		} catch (IOException ioex) {
			JOptionPane.showMessageDialog(
				this,
				"An error occurred while saving your login information",
				"Error Saving Login Information",
				JOptionPane.WARNING_MESSAGE);
		}
	}


	public static void main(String args[]) {
		new LoginDialog(null, null).setVisible(true);
	}

	//
	//

	/**
	 * Server connect thread runnable.
	 */
	protected class ConnectRunnable implements Runnable {
		protected Profile	profile;


		public ConnectRunnable(Profile profile) {
			this.profile = profile;
		}


		public void run() {
			connect(profile);
		}
	}


	/**
	 * Profiles combobox selection change listener.
	 */
	protected class ProfilesCB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			profilesCB();
		}
	}
}
