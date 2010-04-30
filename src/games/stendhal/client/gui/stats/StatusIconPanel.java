package games.stendhal.client.gui.stats;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class StatusIconPanel extends JComponent {
	private static ImageIcon eatingIcon = new ImageIcon(StatusIconPanel.class.getClassLoader().getResource("data/sprites/ideas/eat.png"));
	private static ImageIcon chokingIcon = new ImageIcon(StatusIconPanel.class.getClassLoader().getResource("data/sprites/ideas/choking.png"));
	private static ImageIcon poisonIcon = new ImageIcon(StatusIconPanel.class.getClassLoader().getResource("data/sprites/ideas/poisoned.png"));

	final JLabel eating, choking, poison;
	protected StatusIconPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
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
	}
	
	protected void setEating(boolean isEating) {
		if (eating.isVisible() != isEating) {
			eating.setVisible(isEating);
		}
	}
	
	protected void setChoking(boolean isChoking) {
		if (choking.isVisible() != isChoking) {
			choking.setVisible(isChoking);
		}
	}
	
	protected void setPoisoned(boolean poisoned) {
		if (poison.isVisible() != poisoned) {
			poison.setVisible(poisoned);
		}
	}
}
