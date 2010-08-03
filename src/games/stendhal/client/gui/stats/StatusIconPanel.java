package games.stendhal.client.gui.stats;

import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class StatusIconPanel extends JComponent {
	private static final ImageIcon eatingIcon = new ImageIcon(StatusIconPanel.class.getClassLoader().getResource("data/sprites/ideas/eat.png"));
	private static final ImageIcon chokingIcon = new ImageIcon(StatusIconPanel.class.getClassLoader().getResource("data/sprites/ideas/choking.png"));
	private static final ImageIcon poisonIcon = new ImageIcon(StatusIconPanel.class.getClassLoader().getResource("data/sprites/ideas/poisoned.png"));
	
	private final static Sprite awaySprite, grumpySprite;
	static {
		final SpriteStore store = SpriteStore.get();
		awaySprite = store.getSprite("data/sprites/ideas/away.png");
		grumpySprite = store.getSprite("data/sprites/ideas/grumpy.png");
	}

	final JLabel eating, choking, poison;
	final AnimatedIcon away, grumpy;
	
	protected StatusIconPanel() {
		setLayout(new SBoxLayout(SBoxLayout.HORIZONTAL));
		setOpaque(false);
		
		eating = new JLabel(eatingIcon);
		add(eating);
		eating.setVisible(false);
		
		choking = new JLabel(chokingIcon);
		add(choking);
		choking.setVisible(false);
		
		poison = new JLabel(poisonIcon);
		add(poison);
		poison.setVisible(false);
		
		away = new AnimatedIcon(awaySprite, 13, 19, 4, 2000);
		add(away);
		away.setVisible(false);
		
		grumpy = new AnimatedIcon(grumpySprite, 12, 20, 4, 2000);
		add(grumpy);
		grumpy.setVisible(false);
	}
	
	/**
	 * Display or hide eating icon
	 * 
	 * @param isEating
	 */
	protected void setEating(boolean isEating) {
		if (eating.isVisible() != isEating) {
			eating.setVisible(isEating);
		}
	}
	
	/**
	 * Display or hide choking icon
	 * 
	 * @param isChoking
	 */
	protected void setChoking(boolean isChoking) {
		if (choking.isVisible() != isChoking) {
			choking.setVisible(isChoking);
		}
		// A hack to prevent eating and choking icons appearing 
		// at the same time
		if (isChoking) {
			eating.setVisible(false);
		}
	}
	
	/**
	 * Display or hide poisoned icon
	 * 
	 * @param poisoned
	 */
	protected void setPoisoned(boolean poisoned) {
		if (poison.isVisible() != poisoned) {
			poison.setVisible(poisoned);
		}
	}
	
	/**
	 * Display or hide the away icon
	 * 
	 * @param isAway
	 */
	protected void setAway(boolean isAway) {
		if (away.isVisible() != isAway) {
			away.setVisible(isAway);
		}
	}
	
	/**
	 * Display or hide the grumpy icon
	 * 
	 * @param isGrumpy
	 */
	protected void setGrumpy(boolean isGrumpy) {
		if (grumpy.isVisible() != isGrumpy) {
			grumpy.setVisible(isGrumpy);
		}
	}
}
