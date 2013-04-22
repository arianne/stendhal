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
import games.stendhal.client.gui.WindowUtils;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.update.ClientGameConfiguration;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import marauroa.client.BannedAddressException;
import marauroa.client.LoginFailedException;
import marauroa.client.TimeoutException;
import marauroa.common.io.Persistence;
import marauroa.common.net.InvalidVersionException;
import marauroa.common.net.message.MessageS2CLoginNACK;

import org.apache.log4j.Logger;

/**
 * Server login dialog.
 *
 */
public class LoginDialog extends JDialog {

	private static final long serialVersionUID = -1182930046629241075L;

	protected ProfileList profiles;

	private JComboBox profilesComboBox;

	private JCheckBox saveLoginBox;

	private JCheckBox savePasswordBox;

	private JTextField usernameField;

	private JPasswordField passwordField;

	private JTextField serverField;

	private JTextField serverPortField;

	private JButton loginButton;

	private JButton removeButton;

	private JPanel contentPane;

	// End of variables declaration
	private final StendhalClient client;

	private ProgressBar progressBar;

	public LoginDialog(final Frame owner, final StendhalClient client) {
		super(owner, true);
		this.client = client;
		initializeComponent();
		WindowUtils.closeOnEscape(this);
	}

	private void initializeComponent() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (getOwner() == null) {
					System.exit(0);
				}
				getOwner().setEnabled(true);
				dispose();
			}
		});

		JLabel l;

		this.setTitle("Login to Server");
		this.setResizable(false);

		//
		// contentPane
		//
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		final int pad = SBoxLayout.COMMON_PADDING;
		contentPane.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));

		final GridBagConstraints c = new GridBagConstraints();
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

		/*
		 * Remove profile button
		 */
		removeButton = createRemoveButton();

		// Container for the profiles list and the remove button
		JComponent box = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		profilesComboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		box.add(profilesComboBox);
		box.add(removeButton);

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(box, c);

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
			@Override
			public void actionPerformed(final ActionEvent e) {
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
		usernameField.requestFocusInWindow();
		if (getOwner() != null) {
			getOwner().setEnabled(false);
			this.setLocationRelativeTo(getOwner());
		}
	}

	/**
	 * Create the remove character button.
	 *
	 * @return JButton
	 */
	private JButton createRemoveButton() {
		final URL url = DataLoader.getResource("data/gui/trash.png");
		ImageIcon icon = new ImageIcon(url);
		JButton button = new JButton(icon);
		// Clear the margins that buttons normally add
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setToolTipText("Remove the selected account from the list");

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				removeButton_actionPerformed(e);
			}
		});

		return button;
	}

	private void loginButton_actionPerformed(final ActionEvent e) {
		// If this window isn't enabled, we shouldn't act.
		if (!isEnabled()) {
			return;
		}
		setEnabled(false);

		Profile profile;
		profile = new Profile();

		profile.setHost((serverField.getText()).trim());

		try {
			profile.setPort(Integer.parseInt(serverPortField.getText().trim()));

			// Support for saving port number. Only save when input is a number
			// intensifly@gmx.com

		} catch (final NumberFormatException ex) {
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
				final String pw = profile.getPassword();
				profile.setPassword("");
				saveProfiles(profiles);
				profile.setPassword(pw);
			}

		}

		/*
		 * Run the connection procces in separate thread. added by TheGeneral
		 */
		final Thread t = new Thread(new ConnectRunnable(profile), "Login");
		t.start();
	}

	private void removeButton_actionPerformed(final ActionEvent e) {
		// If this window isn't enabled, we shouldn't act.
		if (!isEnabled() || (profiles.profiles.size() == 0)) {
			return;
		}
		setEnabled(false);

		Profile profile;

		profile = (Profile) profilesComboBox.getSelectedItem();
		Object[] options = { "Remove", "Cancel" };

		Integer confirmRemoveProfile = JOptionPane.showOptionDialog(this,
			"This will permanently remove a user profile from your local list of accounts.\n"
			+ "It will not delete an account on any servers.\n"
			+ "Are you sure you want to remove \'" + profile.getUser() + "@" + profile.getHost() + "\' profile?",
			"Remove user profile from local list of accounts",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1]);

		if ( confirmRemoveProfile == 0 ) {
			profiles.remove(profile);
			saveProfiles(profiles);
			profiles = loadProfiles();
			populateProfiles(profiles);
		}

		setEnabled(true);
	}

	@Override
	public void setEnabled(final boolean b) {
		super.setEnabled(b);
		loginButton.setEnabled(b);
		removeButton.setEnabled(b);
	}
	/**
	 * Connect to a server using a given profile.
	 * @param profile
	 */
	public void connect(final Profile profile) {
		// We are not in EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar = new ProgressBar(LoginDialog.this);
				progressBar.start();
			}
		});

		try {
			client.connect(profile.getHost(), profile.getPort());

			// for each major connection milestone call step(). progressBar is
			// created in EDT, so it is not guaranteed non null in the main
			// thread.
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressBar.step();
				}
			});
		} catch (final Exception ex) {
			// if something goes horribly just cancel the progressbar
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressBar.cancel();
					setEnabled(true);
				}
			});
			String message = "unable to connect to server";

			if (profile != null) {
				message = message + " " + profile.getHost() + ":" + profile.getPort();
			} else {
				message = message + ", because profile was null";
			}
			Logger.getLogger(LoginDialog.class).error(message, ex);
			handleError("Unable to connect to server. Did you misspell the server name?", "Connection failed");
			return;
		}

		final JDialog me=this;
		try {
			client.setAccountUsername(profile.getUser());
			client.setCharacter(profile.getCharacter());
			client.login(profile.getUser(), profile.getPassword(), profile.getSeed());
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressBar.finish();
					// workaround near failures in AWT at openjdk (tested on openjdk-1.6.0.0)
					try {
						setVisible(false);
					} catch (NullPointerException npe) {
						Logger.getLogger(LoginDialog.class).error("Error probably related to bug in JRE occured", npe);
						me.dispose();
					}
				}
			});

		} catch (final InvalidVersionException e) {
			handleError("You are running an incompatible version of Stendhal. Please update",
					"Invalid version");
		} catch (final TimeoutException e) {
			handleError("Server is not available right now.\nThe server may be down or, if you are using a custom server,\nyou may have entered its name and port number incorrectly.",
					"Error Logging In");
		} catch (final LoginFailedException e) {
			handleError(e.getMessage(), "Login failed");
			if (e.getReason() == MessageS2CLoginNACK.Reasons.SEED_WRONG) {
				System.exit(1);
			}
		} catch (final BannedAddressException e) {
			handleError("Your IP is banned. If you think this is not right, please send a Support Request to http://sourceforge.net/tracker/?func=add&group_id=1111&atid=201111",
					"IP Banned");
		}
	}

	/**
	 * Displays the error message, removes the progress bar and
	 * either enabled the login dialog in interactive mode or exits
	 * the client in non interactive mode.
	 *
	 * @param errorMessage error message
	 * @param errorTitle   title of error dialog box
	 */
	private void handleError(final String errorMessage, final String errorTitle) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.cancel();
				JOptionPane.showMessageDialog(
						LoginDialog.this, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

				if (isVisible()) {
					setEnabled(true);
				} else {
					// Hack for non interactive login
					System.exit(1);
				}
			}
		});
	}

	/**
	 * Load saves profiles.
	 * @return ProfileList
	 */
	private ProfileList loadProfiles() {
		final ProfileList tmpProfiles = new ProfileList();

		try {
			final InputStream is = Persistence.get().getInputStream(false, stendhal.getGameFolder(),
					"user.dat");

			try {
				tmpProfiles.load(is);
			} finally {
				is.close();
			}
		} catch (final FileNotFoundException fnfe) {
			// Ignore
		} catch (final IOException ioex) {
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
	protected void populateProfiles(final ProfileList profiles) {
		profilesComboBox.removeAllItems();

		final Iterator< ? > iter = profiles.iterator();

		while (iter.hasNext()) {
			profilesComboBox.addItem(iter.next());
		}

		/*
		 * The last profile (if any) is the default.
		 */
		final int count = profilesComboBox.getItemCount();
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
	private void saveProfiles(final ProfileList profiles) {
		try {
			final OutputStream os = Persistence.get().getOutputStream(false,
					stendhal.getGameFolder(), "user.dat");

			try {
				profiles.save(os);
			} finally {
				os.close();
			}
		} catch (final IOException ioex) {
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

		public ConnectRunnable(final Profile profile) {
			this.profile = profile;
		}

		@Override
		public void run() {
			connect(profile);
		}
	}

	/**
	 * Profiles combobox selection change listener.
	 */
	protected class ProfilesCB implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			profilesCB();
		}
	}

	/**
	 * Save profile selection change.
	 */
	protected class SaveProfileStateCB implements ChangeListener {

		@Override
		public void stateChanged(final ChangeEvent ev) {
			saveProfileStateCB();
		}
	}
}
