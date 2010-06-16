package games.stendhal.client.gui.styled;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

/**
 * A style for a scroll pane. Just changes the default border.
 */
public class StyledScrollPaneUI extends BasicScrollPaneUI {
	private final Style style;
	
	// Required by UIManager
	public static ComponentUI createUI(JComponent pane) {
		// BasicScrollPaneUI instances can not be shared
		return new StyledScrollPaneUI(StyleUtil.getStyle());
	}
	
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
		super.installUI(pane);
		pane.setBorder(style.getBorderDown());
	}
}
