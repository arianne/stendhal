package games.stendhal.client.gui.bag;

import static pagelayout.EasyCell.eol;
import static pagelayout.EasyCell.grid;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.swing.StyledJPanel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import pagelayout.CellGrid;

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

		final CellGrid baggrid = grid(panels[0], panels[1], panels[2], eol(),
				panels[3], panels[4], panels[5], eol(), panels[6], panels[7],
				panels[8], eol(), panels[9], panels[10], panels[11], eol());
		baggrid.setFixedWidth(panels, true);
		baggrid.setFixedHeight(panels, true);
		baggrid.setComponentGaps(1, 1);
		baggrid.createLayout(this).setContainerGaps(0, 0);
	}
}
