package games.stendhal.client.gui.dialog;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import marauroa.common.game.RPObject;

/**
 * A dialog for selecting from the available characters of a user account.
 */
public class CharacterDialog extends JDialog {
	/** Maximum dialog width in pixels */
	private static final int DIALOG_WIDTH = 320;
	/** Width of a player image in pixels */
	private static final int IMAGE_WIDTH = 32;
	/** Height of a player image in pixels */
	private static final int IMAGE_HEIGHT = 48;
	
	
	/** Area containing buttons for each character */
	private final JPanel characterPanel;
	
	/**
	 * Create a new <code>CharacterDialog</code>.
	 * 
	 * @param characters map of available characters, and <code>RPObjects</code>
	 * 	representing them
	 */
	public CharacterDialog(final Map<String, RPObject> characters) {
		setTitle("Choose character");
		this.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		
		// Create the character area
		characterPanel = new JPanel();
		characterPanel.setLayout(new BoxLayout(characterPanel, BoxLayout.X_AXIS));
		JScrollPane scroll = new JScrollPane(characterPanel);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scroll, SBoxLayout.constraint(SLayout.EXPAND_X));
		
		addCharacters(characters);
		
		// Create area for additional buttons
		JPanel buttonBar = new JPanel();
		buttonBar.setLayout(new SBoxLayout(SBoxLayout.HORIZONTAL));
		add(buttonBar, SBoxLayout.constraint(SLayout.EXPAND_X));
		
		// A way to get right alignment (really "end alignment") with
		// SBoxLayout. Used only here for now, so there's little point of
		// adding a convenience class to the layout package yet.
		JComponent spring = new JComponent() {
		};
		buttonBar.add(spring, SBoxLayout.constraint(SLayout.EXPAND_X));
		
		// Test buttons. Not used for anything for now.
		JButton newCharButton = new JButton("New Character");
		buttonBar.add(newCharButton);
		
		JButton logoutButton = new JButton("Logout");
		buttonBar.add(logoutButton);
		
		pack();
		setSize(Math.min(getWidth(), DIALOG_WIDTH), getHeight());
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
		player.initialize(character);
		
		EntityView view = EntityViewFactory.create(player);
		Image image = createCharacterImage(view);
		Icon icon = new ImageIcon(image);
		
		// Construct a label for the player button with the information we
		// want to show. Needs to be html as that's the only way JButton
		// can handle multi line labels.
		StringBuilder label = new StringBuilder("<html>");
		label.append(name);
		label.append("<br>Level: ");
		label.append(character.get("level"));
		label.append("</html>");
		
		JButton playerButton = new JButton(label.toString(), icon);
		
		playerButton.addActionListener(new ActionListener() {
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
	private Image createCharacterImage(EntityView view) {
		Image image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(IMAGE_WIDTH, IMAGE_HEIGHT);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		
		// Adjust the coordinates so that the actual player image gets
		// drawn to the image area
		g2d.translate(0, IMAGE_HEIGHT % 32);
		view.draw(g2d);
		g2d.dispose();
		
		return image;
	}
	
	/**
	 * Called when a character is selected.
	 * 
	 * @param character player selected by the user
	 */
	private void chooseCharacter(final String character) {
		System.err.println(character);
	}
}
