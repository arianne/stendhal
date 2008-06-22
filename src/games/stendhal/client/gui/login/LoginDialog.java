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
package games.stendhal.client.gui.login;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.ProgressBar;
import games.stendhal.client.update.ClientGameConfiguration;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import marauroa.client.BannedAddressException;
import marauroa.client.LoginFailedException;
import marauroa.client.TimeoutException;

import marauroa.common.io.Persistence;
import marauroa.common.net.InvalidVersionException;

/**
 * Server login dialog.
 * 
 */
public class LoginDialog extends JDialog {

	private static final long serialVersionUID = -1182930046629241075L;

	private JComboBox profilesComboBox;

	private JCheckBox saveLoginBox;

	private JCheckBox savePasswordBox;

	private JTextField usernameField;

	private JPasswordField passwordField;

	private JTextField serverField;

	private JTextField serverPortField;

	private JButton loginButton;

	private JPanel contentPane;

	// End of variables declaration
	private StendhalClient client;

	private Frame owner;

	protected ProfileList profiles;

	public LoginDialog(Frame owner, StendhalClient client) {
		super(owner, true);
		this.client = client;
		this.owner = owner;
		initializeComponent();

		this.setVisible(true);
	}

	private void initializeComponent() {
		JLabel l;

		this.setTitle("Login to Server");
		this.setResizable(false);

		//
		// contentPane
		//
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;

		/*
		 * Profiles
		 */
		l = new JLabel("Account profiles");

		c.insets = new Insets(4, 4, 15, 4);
		// column
		c.gridx = 0;
		 // row
		c.gridy = 0;
		contentPane.add(l, c);

		profilesComboBox = new JComboBox();
		profilesComboBox.addActionListener(new ProfilesCB());
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(profilesComboBox, c);

		/*
		 * Server Host
		 */
		l = new JLabel("Server name");
		c.insets = new Insets(4, 4, 4, 4);
		// column
		c.gridx = 0;
		 // row
		c.gridy = 1;
		contentPane.add(l, c);

		serverField = new JTextField(
				ClientGameConfiguration.get("DEFAULT_SERVER"));
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

		serverPortField = new JTextField(
				ClientGameConfiguration.get("DEFAULT_PORT"));
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(serverPortField, c);

		/*
		 * Username
		 */
		l = new JLabel("Type your username");
		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 3;
		contentPane.add(l, c);

		usernameField = new JTextField();
		// TODO: put the caret into the username field, does not work?!
		usernameField.requestFocusInWindow();
		c.gridx = 1;
		c.gridy = 3;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(usernameField, c);

		/*
		 * Password
		 */
		l = new JLabel("Type your password");

		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(l, c);

		passwordField = new JPasswordField();

		c.gridx = 1;
		c.gridy = 4;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(passwordField, c);

		/*
		 * Save Profile/Login
		 */
		saveLoginBox = new JCheckBox("Save login profile locally");
		saveLoginBox.setSelected(false);

		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(saveLoginBox, c);

		/*
		 * Save Profile Password
		 */
		savePasswordBox = new JCheckBox("Save password");
		savePasswordBox.setSelected(true);
		savePasswordBox.setEnabled(false);

		c.gridx = 0;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 20, 0, 0);
		contentPane.add(savePasswordBox, c);

		loginButton = new JButton();
		loginButton.setText("Login to Server");
		loginButton.setMnemonic(KeyEvent.VK_L);
		this.rootPane.setDefaultButton(loginButton);

		loginButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				loginButton_actionPerformed(e);
			}
		});

		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(15, 4, 4, 4);
		contentPane.add(loginButton, c);

		/*
		 * Load saved profiles
		 */
		profiles = loadProfiles();
		populateProfiles(profiles);

		/*
		 * Add this callback after everything is initialized
		 */
		saveLoginBox.addChangeListener(new SaveProfileStateCB());

		//
		// Dialog
		//

		this.pack();
		this.setLocationRelativeTo(owner);
	}

	private void loginButton_actionPerformed(ActionEvent e) {
		Profile profile;

		// If this window isn't enabled, we shouldn't act.
		if (!isEnabled()) {
			return;
		}

		profile = new Profile();

		profile.setHost((serverField.getText()).trim());

		try {
			profile.setPort(Integer.parseInt(serverPortField.getText().trim()));

			// Support for saving port number. Only save when input is a number
			// intensifly@gmx.com

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this,
					"That is not a valid port number. Please try again.",
					"Invalid port", JOptionPane.WARNING_MESSAGE);
			return;
		}

		profile.setUser(usernameField.getText().trim());
		profile.setPassword(new String(passwordField.getPassword()));

		/*
		 * Save profile?
		 */
		if (saveLoginBox.isSelected()) {
			profiles.add(profile);
			populateProfiles(profiles);

			if (savePasswordBox.isSelected()) {
				saveProfiles(profiles);
			} else {
				String pw = profile.getPassword();
				profile.setPassword("");
				saveProfiles(profiles);
				profile.setPassword(pw);
			}

		}

		/*
		 * Run the connection procces in separate thread. added by TheGeneral
		 */
		Thread t = new Thread(new ConnectRunnable(profile));
		t.start();
	}

	/**
	 * Connect to a server using a given profile.
	 * @param profile 
	 */
	protected void connect(Profile profile) {
		final ProgressBar progressBar = new ProgressBar(this);

		// intialize progress bar
		progressBar.start();

		// disable this screen when attempting to connect
		setEnabled(false);

		try {
			client.connect(profile.getHost(), profile.getPort());

			// for each major connection milestone call step()
			progressBar.step();
		} catch (Exception ex) {
			// if something goes horribly just cancel the progressbar
			progressBar.cancel();
			setEnabled(true);

			Logger.getLogger(LoginDialog.class).error(
					"unable to connect to server", ex);
			JOptionPane.showMessageDialog(this,
					"Unable to connect to server. Did you misspell the server name?");
			return;
		}

		try {
			client.setAccountUsername(profile.getUser());
			client.login(profile.getUser(), profile.getPassword());

			progressBar.step();
			progressBar.finish();

			setVisible(false);
			owner.setVisible(false);
			stendhal.doLogin = true;
		} catch (InvalidVersionException e) {
			progressBar.cancel();
			setEnabled(true);

			JOptionPane.showMessageDialog(
					this,
					"You are running an incompatible version of Stendhal. Please update",
					"Invalid version", JOptionPane.ERROR_MESSAGE);
		} catch (TimeoutException e) {
			progressBar.cancel();
			setEnabled(true);

			JOptionPane.showMessageDialog(
					this,
					"Server is not available right now. The server may be down or, if you are using a custom server, you may have entered its name and port number incorrectly.",
					"Error Logging In", JOptionPane.ERROR_MESSAGE);
		} catch (LoginFailedException e) {
			progressBar.cancel();
			setEnabled(true);

			JOptionPane.showMessageDialog(this, e.getMessage(), "Login failed",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (BannedAddressException e) {
			progressBar.cancel();
			setEnabled(true);

			JOptionPane.showMessageDialog(
					this,
					"You IP is banned. If you think this is not right. Please send a Support request to http://sourceforge.net/tracker/?func=add&group_id=1111&atid=201111",
					"IP Banned", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Load saves profiles.
	 * @return 
	 */
	private ProfileList loadProfiles() {
		ProfileList tmpProfiles = new ProfileList();

		try {
			InputStream is = Persistence.get().getInputStream(true, "stendhal",
					"user.dat");

			try {
				tmpProfiles.load(is);
			} finally {
				is.close();
			}
		} catch (FileNotFoundException fnfe) {
			// Ignore
		} catch (IOException ioex) {
			JOptionPane.showMessageDialog(this,
					"An error occurred while loading your login information",
					"Error Loading Login Information",
					JOptionPane.WARNING_MESSAGE);
		}

		return tmpProfiles;
	}

	/**
	 * Populate the profiles combobox and select the default.
	 * @param profiles 
	 */
	protected void populateProfiles(ProfileList profiles) {
		
		

		profilesComboBox.removeAllItems();

		Iterator< ? > iter = profiles.iterator();

		while (iter.hasNext()) {
			profilesComboBox.addItem(iter.next());
		}

		/*
		 * The last profile (if any) is the default.
		 */
		int count = profilesComboBox.getItemCount();
		if (count != 0) {
			profilesComboBox.setSelectedIndex(count - 1);
		}
	}

	/**
	 * Called when a profile selection is changed.
	 */
	protected void profilesCB() {
		Profile profile;
		String host;

		profile = (Profile) profilesComboBox.getSelectedItem();

		if (profile != null) {
			host = profile.getHost();
			serverField.setText(host);

			serverPortField.setText(String.valueOf(profile.getPort()));

			usernameField.setText(profile.getUser());
			passwordField.setText(profile.getPassword());
		} else {

			serverPortField.setText(String.valueOf(Profile.DEFAULT_SERVER_PORT));

			usernameField.setText("");
			passwordField.setText("");
		}
	}

	/*
	 * Author: Da_MusH Description: Methods for saving and loading login
	 * information to disk. These should probably make a separate class in the
	 * future, but it will work for now. comment: Thegeneral has added encoding
	 * for password and username. Changed for multiple profiles.
	 */
	private void saveProfiles(ProfileList profiles) {
		try {
			OutputStream os = Persistence.get().getOutputStream(true,
					"stendhal", "user.dat");

			try {
				profiles.save(os);
			} finally {
				os.close();
			}
		} catch (IOException ioex) {
			JOptionPane.showMessageDialog(this,
					"An error occurred while saving your login information",
					"Error Saving Login Information",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Called when save profile selection change.
	 */
	protected void saveProfileStateCB() {
		savePasswordBox.setEnabled(saveLoginBox.isSelected());
	}

	/**
	 * Server connect thread runnable.
	 */
	protected class ConnectRunnable implements Runnable {

		protected Profile profile;

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

	/**
	 * Save profile selection change.
	 */
	protected class SaveProfileStateCB implements ChangeListener {

		public void stateChanged(ChangeEvent ev) {
			saveProfileStateCB();
		}
	}
}
