/***************************************************************************
 *                 (C) Copyright 2003 - 2015 Faiumoni e.V.                 *
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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;

import org.apache.log4j.Logger;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.NumberDocumentFilter;
import games.stendhal.client.gui.ProgressBar;
import games.stendhal.client.gui.WindowUtils;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.update.ClientGameConfiguration;
import marauroa.client.BannedAddressException;
import marauroa.client.LoginFailedException;
import marauroa.client.TimeoutException;
import marauroa.common.io.Persistence;
import marauroa.common.net.InvalidVersionException;
import marauroa.common.net.message.MessageS2CLoginNACK;

/**
 * Server login dialog.
 */
public class LoginDialog extends JDialog {
	private ProfileList profiles;

	private JComboBox<Profile> profilesComboBox;

	private JCheckBox saveLoginBox;

	private JCheckBox savePasswordBox;

	private JTextField usernameField;

	private JPasswordField passwordField;

	private JTextField serverField;

	private JTextField serverPortField;

	private JButton loginButton;

	private JButton removeButton;

	private final StendhalClient client;

	private ProgressBar progressBar;
	/** Object checking that all required fields are filled */
	private DataValidator fieldValidator;

	/**
	 * Create a new LoginDialog.
	 *
	 * @param owner parent window
	 * @param client client
	 */
	public LoginDialog(final Frame owner, final StendhalClient client) {
		super(owner, true);
		this.client = client;
		initializeComponent();
		WindowUtils.closeOnEscape(this);
	}

	/**
	 * Create the dialog contents.
	 */
	private void initializeComponent() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if (getOwner() != null) {
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					getOwner().setEnabled(true);
				}
			});
		}

		JLabel l;

		this.setTitle("登陆");
		this.setResizable(false);

		//
		// contentPane
		//
		JComponent contentPane = (JComponent) getContentPane();
		contentPane.setLayout(new GridBagLayout());
		final int pad = SBoxLayout.COMMON_PADDING;
		contentPane.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;

		/*
		 * Profiles
		 */
		l = new JLabel("用户详情");

		c.insets = new Insets(4, 4, 15, 4);
		// column
		c.gridx = 0;
		 // row
		c.gridy = 0;
		contentPane.add(l, c);

		profilesComboBox = new JComboBox<Profile>();
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
		l = new JLabel("服务器名称");
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
		l = new JLabel("服务器端口");
		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 2;
		contentPane.add(l, c);

		serverPortField = new JTextField(
				ClientGameConfiguration.get("DEFAULT_PORT"));
		((AbstractDocument) serverPortField.getDocument()).setDocumentFilter(new NumberDocumentFilter(serverPortField, false));
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(serverPortField, c);

		/*
		 * Username
		 */
		l = new JLabel("用户名");
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
		l = new JLabel("用户密码");

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
		saveLoginBox = new JCheckBox("保存登陆信息");
		saveLoginBox.setSelected(false);

		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(saveLoginBox, c);

		/*
		 * Save Profile Password
		 */
		savePasswordBox = new JCheckBox("保存密码");
		savePasswordBox.setSelected(true);
		savePasswordBox.setEnabled(false);

		c.gridx = 0;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 20, 0, 0);
		contentPane.add(savePasswordBox, c);

		loginButton = new JButton();
		loginButton.setText("登陆");
		loginButton.setMnemonic(KeyEvent.VK_L);
		this.rootPane.setDefaultButton(loginButton);

		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				loginButtonActionPerformed();
			}
		});

		JComponent buttonBox = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
		JButton cancelButton = new JButton("取消");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchEvent(new WindowEvent(LoginDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		buttonBox.add(cancelButton);
		buttonBox.add(loginButton);

		c.gridx = 1;
		c.gridy = 5;
		c.gridheight = 2;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.insets = new Insets(0, 0, SBoxLayout.COMMON_PADDING, SBoxLayout.COMMON_PADDING);
		contentPane.add(buttonBox, c);

		// Before loading profiles so that we can catch the data filled from
		// there
		bindEditListener();

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
	 * Prepare the field validator and bind it to the relevant text fields.
	 */
	private void bindEditListener() {
		fieldValidator = new DataValidator(loginButton,
				serverField.getDocument(), serverPortField.getDocument(),
				usernameField.getDocument(), passwordField.getDocument());
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
		button.setToolTipText("删除此帐户");

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				removeButtonActionPerformed();
			}
		});

		return button;
	}

	/**
	 * Called when the login button is activated.
	 */
	private void loginButtonActionPerformed() {
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
					"端口不可用，请重新输入",
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

	/**
	 * Called when the remove profile button is activated.
	 */
	private void removeButtonActionPerformed() {
		// If this window isn't enabled, we shouldn't act.
		if (!isEnabled() || (profiles.profiles.size() == 0)) {
			return;
		}
		setEnabled(false);

		Profile profile;

		profile = (Profile) profilesComboBox.getSelectedItem();
		Object[] options = { "Remove", "Cancel" };

		Integer confirmRemoveProfile = JOptionPane.showOptionDialog(this,
			"此操作会永久删除本地存储的用户信息\n"
			+ "但不会删除服务器中的帐户\n"
			+ "确定要删除 \'" + profile.getUser() + "@" + profile.getHost() + "\' 帐户信息?",
			"删除列表中的此帐户",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1]);

		if (confirmRemoveProfile == 0) {
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
		// Enabling login button is conditional
		fieldValidator.revalidate();
		removeButton.setEnabled(b);
	}

	/**
	 * Connect to a server using a given profile.
	 *
	 * @param profile profile used for login
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
			String message = "连接服务器失败";

			if (profile != null) {
				message = message + " " + profile.getHost() + ":" + profile.getPort();
			} else {
				message = message + ", 用户信息为空";
			}
			Logger.getLogger(LoginDialog.class).error(message, ex);
			handleError("连接服务器失败，请核对服务器名称", "Connection failed");
			return;
		}

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
						Logger.getLogger(LoginDialog.class).error("此错误可能是JRE产生", npe);
						LoginDialog.this.dispose();
					}
				}
			});

		} catch (final InvalidVersionException e) {
			handleError("You are running an incompatible version of Stendhal. Please update",
					"Invalid version");
		} catch (final TimeoutException e) {
			handleError("服务器目前不可用\n服务器可能停机\n 或者核对服务器名称和端口名称",
					"Error Logging In");
		} catch (final LoginFailedException e) {
			handleError(e.getMessage(), "Login failed");
			if (e.getReason() == MessageS2CLoginNACK.Reasons.SEED_WRONG) {
				System.exit(1);
			}
		} catch (final BannedAddressException e) {
			handleError("你的IP被墙",
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
					"载入用户信息时发生错误",
					"Error Loading Login Information",
					JOptionPane.WARNING_MESSAGE);
		}

		return tmpProfiles;
	}

	/**
	 * Populate the profiles combobox and select the default.
	 *
	 * @param profiles profile data
	 */
	private void populateProfiles(final ProfileList profiles) {
		profilesComboBox.removeAllItems();

		for (Profile p : profiles) {
			profilesComboBox.addItem(p);
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
	private void profilesCB() {
		Profile profile;
		String host;

		// This *should* be generic in swing, but it is not
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

	/**
	 * Checks that a group of Documents (text fields) is not empty, and enables
	 * or disables a JComponent on that condition.
	 */
	private static class DataValidator implements DocumentListener {
		private final Document[] documents;
		private final JComponent component;

		/**
		 * Create a new DataValidator.
		 *
		 * @param component component to be enabled depending on the state of
		 *  documents
		 * @param docs documents
		 */
		DataValidator(JComponent component, Document... docs) {
			this.component = component;
			documents = docs;
			for (Document doc : docs) {
				doc.addDocumentListener(this);
			}
			revalidate();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			revalidate();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			if (e.getDocument().getLength() == 0) {
				component.setEnabled(false);
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// Attribute change - ignore
		}

		/**
		 * Do a full document state check and set the component status according
		 * to the result.
		 */
		final void revalidate() {
			for (Document doc : documents) {
				if (doc.getLength() == 0) {
					component.setEnabled(false);
					return;
				}
			}
			component.setEnabled(true);
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
					"保存登陆信息发生错误",
					"Error Saving Login Information",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Called when save profile selection change.
	 */
	private void saveProfileStateCB() {
		savePasswordBox.setEnabled(saveLoginBox.isSelected());
	}

	/**
	 * Server connect thread runnable.
	 */
	private final class ConnectRunnable implements Runnable {
		private final Profile profile;

		/**
		 * Create a new ConnectRunnable.
		 *
		 * @param profile profile used for connection
		 */
		private ConnectRunnable(final Profile profile) {
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
	private class ProfilesCB implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			profilesCB();
		}
	}

	/**
	 * Save profile selection change.
	 */
	private class SaveProfileStateCB implements ChangeListener {
		@Override
		public void stateChanged(final ChangeEvent ev) {
			saveProfileStateCB();
		}
	}
}
