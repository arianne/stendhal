package games.stendhal.client.gui.styled;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 * MenuItemUI for drawing menu items with style.
 */
public class StyledMenuItemUI extends BasicMenuItemUI {
	private final Style style;
	
	// Required by UIManager
	public static ComponentUI createUI(JComponent menuItem) {
		// BasicMenuItemUI can not be shared
		return new StyledMenuItemUI(StyleUtil.getStyle());
	}
	
	/**
	 * Create a new StyledMenuItemUI.
	 * 
	 * @param style pixmap style
	 */
	public StyledMenuItemUI(Style style) {
		this.style = style;
		selectionBackground = style.getHighLightColor();
		selectionForeground = style.getShadowColor(); 
	}
	
	@Override
	public void installUI(JComponent component) {
		super.installUI(component);
		
		component.setBorder(style.getBorder());
		component.setOpaque(false);
		component.setFont(style.getFont());
		component.setForeground(style.getForeground());
	}
}
