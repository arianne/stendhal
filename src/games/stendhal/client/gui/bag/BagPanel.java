package games.stendhal.client.gui.bag;

import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.swing.StyledJPanel;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class BagPanel extends StyledJPanel {

	private static final int FRAMES_PER_SECOND = 10;
	private static Timer timer;
	
	final ActionListener timerTask = new ActionListener() {

		public void actionPerformed(final ActionEvent e) {
			BagPanel.this.repaint();
		}
	};
	


	
	public BagPanel(final Style instance, final Component[] panels) {
		super(instance);
		timer = new Timer(1000 / FRAMES_PER_SECOND, timerTask);	
		timer.start();

		setLayout(new GridLayout(4, 3));
		
		for (Component panel : panels) {
			add(panel);
		}
	}
}
