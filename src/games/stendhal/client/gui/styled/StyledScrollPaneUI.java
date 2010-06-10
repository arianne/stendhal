package games.stendhal.client.gui.styled;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollPaneUI;

/**
 * A style for a scroll pane. Just changes the default border.
 */
public class StyledScrollPaneUI extends BasicScrollPaneUI {
	private final Style style;
	
	/**
	 * Create a new pixmap style.
	 * 
	 * @param style {@link Style} to be used for drawing the panel
	 */
	public StyledScrollPaneUI(Style style) {
		this.style = style;
	}
	
	@Override
	public void installUI(JComponent pane) {
		JScrollPane scrollPane = (JScrollPane) pane;
		super.installUI(scrollPane);
		scrollPane.setBorder(style.getBorderDown());
		// ScrollBarUI instances can not be shared
		scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI(style));
		scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI(style));
	}
}
