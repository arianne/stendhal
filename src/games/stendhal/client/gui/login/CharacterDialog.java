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
package games.stendhal.client.gui.login;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.gui.TransparencyMode;
import games.stendhal.client.gui.WindowUtils;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import marauroa.client.BannedAddressException;
import marauroa.client.TimeoutException;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPObject;
import marauroa.common.net.InvalidVersionException;

/**
 * A dialog for selecting from the available characters of a user account.
 */
public final class CharacterDialog extends JDialog implements Runnable {
	private static final long serialVersionUID = -8827654641088132946L;

	private static Logger logger = Logger.getLogger(CharacterDialog.class);

	/** Maximum dialog width in pixels */
	private static final int DIALOG_WIDTH = 640;
	/** Maximum dialog height in pixels */
	private static final int DIALOG_HEIGHT = 480;
	/** Maximum number of character buttons per row */
	private static final int MAX_COLUMNS = 4;
	/** Width of a player image in pixels */
	private static final int IMAGE_WIDTH = 48;
	/** Height of a player image in pixels */
	private static final int IMAGE_HEIGHT = 64;
	/**
	 * Maximum number of characters / account. A soft limit since this
	 * is client side.
	 */
	private static final int MAX_CHARACTERS = 16;


	/** Area containing buttons for each character */
	private final JComponent characterPanel;

	/**
	 * Create a new <code>CharacterDialog</code>.
	 *
	 * @param characters map of available characters, and <code>RPObjects</code>
	 * 	representing them
	 * @param owner the parent window
	 */
	public CharacterDialog(final Map<String, RPObject> characters, JFrame owner) {
		super(owner);
		setTitle("Choose character");

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onClose();
			}
		});

		int pad = SBoxLayout.COMMON_PADDING;
		setLayout(new SBoxLayout(SBoxLayout.VERTICAL, pad));
		Container content = getContentPane();
		if (content instanceof JComponent) {
			((JComponent) content).setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		}

		// Create the character area
		characterPanel = new JPanel();
		characterPanel.setBorder(null);
		// Do not add empty places if the first row is not full
		int columns = Math.min(MAX_COLUMNS, characters.keySet().size());
		GridLayout grid = new GridLayout(0, columns);
		characterPanel.setLayout(grid);
		JScrollPane scroll = new JScrollPane(characterPanel);
		add(scroll, SLayout.EXPAND_X);

		addCharacters(characters);

		// Create area for additional buttons
		JComponent buttonBar = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
		add(buttonBar, SLayout.EXPAND_X);

		// Align the buttons right
		SBoxLayout.addSpring(buttonBar);

		// Action buttons. Should these be of uniform size?
		JButton newCharButton = new JButton("New Character");
		newCharButton.setMnemonic(KeyEvent.VK_N);
		newCharButton.addActionListener(new CreateCharacterAction(this));
		// Disable creating unreasonable amounts of characters. Enough is enough.
		if (characters.keySet().size() >= MAX_CHARACTERS) {
			newCharButton.setEnabled(false);
		}
		buttonBar.add(newCharButton);

		JButton exitButton = new JButton("Cancel");
		exitButton.setMnemonic(KeyEvent.VK_C);
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				onClose();
			}
		});
		buttonBar.add(exitButton);

		setSize(Math.min(getWidth(), DIALOG_WIDTH), Math.min(getHeight(), DIALOG_HEIGHT));
		pack();
		if (owner != null) {
			owner.setEnabled(false);
			this.setLocationRelativeTo(owner);
		}

		Thread thread = new Thread(this, "KeepAlive on character dialog");
		thread.setDaemon(true);
		thread.start();

		WindowUtils.closeOnEscape(this);
		setResizable(false);
		setVisible(true);
	}

	/**
	 * Add the available characters.
	 *
	 * @param characters
	 */
	private void addCharacters(final Map<String, RPObject> characters) {
		for (Entry<String, RPObject> character : characters.entrySet()) {
			JButton button = createCharacterButton(character.getKey(), character.getValue());
			characterPanel.add(button);
		}
	}

	/**
	 * Create a button for a character.
	 *
	 * @param name Name of the character
	 * @param character Object representing the character
	 *
	 * @return a button for the character
	 */
	private JButton createCharacterButton(final String name, final RPObject character) {
		// Abusing EntityView code to get the image of the player.
		// Avoid doing sound stuff. That is not available at this stage of
		// running the client.
		IEntity player = new Player() {
			@Override
			protected void onPosition(double x, double y) {
			}
			@Override
			protected void addSounds(String groupName, String categoryName, String... soundNames) {
			}
		};
		// Zero the coordinates, otherwise the EntityView will try to draw
		// itself to some weird place
		character.put("x", 0);
		character.put("y", 0);
		// ignore ghostmode for showing the image
		character.remove("ghostmode");
		// ignore player killer skull
		character.remove("last_player_kill_time");
		player.initialize(character);

		EntityView<?> view = EntityViewFactory.create(player);
		// this if-block is there to be compatible with Stendhal 0.84 that is missing information
		Icon icon = null;
		if (view != null) {
			Image image = createCharacterImage(view);
			icon = new ImageIcon(image);
		}

		// Construct a label for the player button with the information we
		// want to show. Needs to be html as that's the only way JButton
		// can handle multi line labels.
		StringBuilder label = new StringBuilder("<html>");
		label.append(name);

		// this if-block is here for compatibility with stendhal server 0.84
		if (character.has("name")) {
			label.append("<br>Level: ");
			String level = "0";
			if (character.has("level")) {
				level = character.get("level");
			}
			label.append(level);
		}
		label.append("</html>");

		JButton playerButton = new JButton(label.toString(), icon);
		// Reduce the margins. The buttons are large enough as they are.
		playerButton.setMargin(new Insets(1, 1, 1, 1));

		playerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				chooseCharacter(name);
			}
		});

		return playerButton;
	}

	/**
	 * Create a character image from the view.
	 *
	 * @param view view of the player
	 * @return A front view image of the player
	 */
	private Image createCharacterImage(EntityView<?> view) {
		Image image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(IMAGE_WIDTH, IMAGE_HEIGHT, TransparencyMode.TRANSPARENCY);
		Graphics2D g2d = (Graphics2D) image.getGraphics();

		// Adjust the coordinates so that the actual player image gets
		// drawn to the image area
		g2d.translate(0, IMAGE_HEIGHT % 36);
		view.draw(g2d);
		g2d.dispose();

		return image;
	}

	/**
	 * Called when the window is closed or the exit button is pressed.
	 */
	private void onClose() {
		if (getOwner() == null) {
			System.exit(0);
		}
		getOwner().setEnabled(true);
		this.setVisible(false);
		dispose();
	}

	/**
	 * Called when a character is selected.
	 *
	 * @param character player selected by the user
	 */
	private void chooseCharacter(final String character) {
		try {
			StendhalClient.get().chooseCharacter(character);
			setVisible(false);
			stendhal.setDoLogin();
			dispose();
		} catch (TimeoutException e) {
			logger.error(e, e);
			handleError("Your connection timed out, please login again.", "Choose Character");
		} catch (InvalidVersionException e) {
			logger.error(e, e);
			handleError("Your version of Stendhal is incompatible with the server.", "Choose Character");
		} catch (BannedAddressException e) {
			logger.error(e, e);
			handleError("Please login again.", "Choose Character");
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
	private void handleError(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(
				this, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

		if (getOwner() != null) {
			setVisible(false);
			getOwner().setEnabled(true);
			dispose();
		} else {
			// Hack for non interactive login
			System.exit(1);
		}
	}

	static class CreateCharacterAction implements ActionListener {
		private CharacterDialog parent;
		private CreateCharacterAction(CharacterDialog parent) {
			this.parent = parent;
		}

		@Override
		public void actionPerformed(final ActionEvent evt) {
			String name = JOptionPane.showInputDialog(parent,
					"Please enter the name of your character (only letters allowed):",
					"Create Character",
					JOptionPane.QUESTION_MESSAGE);

			if (name == null) {
				return;
			}

			try {
				// TODO: error handling, exceptions and return of false
				CharacterResult result = StendhalClient.get().createCharacter(name.toLowerCase(Locale.ENGLISH), new RPObject());
				if (result.getResult().failed()) {
					JOptionPane.showMessageDialog(parent, result.getResult().getText());
				} else {
					parent.setVisible(false);
				}
			} catch (TimeoutException e) {
				logger.error(e, e);
				parent.handleError("Your connection timed out, please login again.", "Choose Character");
			} catch (InvalidVersionException e) {
				logger.error(e, e);
				parent.handleError("Your version of Stendhal is incompatible with the server.", "Choose Character");
			} catch (BannedAddressException e) {
				logger.error(e, e);
				parent.handleError("Please login again.", "Choose Character");
			}
		}
	}

	@Override
	public void run() {
		while (isVisible()) {
			StendhalClient.get().sendKeepAlive();
			try {
				Thread.sleep(5*60*1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
