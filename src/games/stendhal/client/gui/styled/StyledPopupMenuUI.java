package games.stendhal.client.gui.styled;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.MenuElement;
import javax.swing.plaf.basic.BasicPopupMenuUI;

/**
 * PopupMenuUI implementation for drawing styled menus. 
 */
public class StyledPopupMenuUI extends BasicPopupMenuUI {
	private final Style style;
	
	/**
	 * Create a new StyledPopupMenuUI.
	 * 
	 * @param style The pixmap style for drawing the menu
	 */
	public StyledPopupMenuUI(Style style) {
		this.style = style;
	}
	
	@Override
	public void paint(Graphics g, JComponent menu) {
		StyleUtil.fillBackground(style, g, 0, 0, menu.getWidth(), menu.getHeight());
	}
	
	@Override
	public void installUI(JComponent component) {
		super.installUI(component);
		component.setBorder(style.getBorder());
		
		/*
		 * A hack to apply an approximation of the style to all the menu items.
		 * There's no really good way to do that until the theme is complete
		 * enough for the menu items to get their UI definitions from there.
		 */
		for (MenuElement elem : popupMenu.getSubElements()) {
			Component tmp = elem.getComponent();
			if (tmp instanceof JComponent) {
				JComponent item = (JComponent) tmp;
				//item.setBorder(style.getBorder());
				// Borders make the menu look heavy
				item.setBorder(null);
				item.setOpaque(false);
				item.setFont(style.getFont());
				item.setForeground(style.getForeground());
			}
		}
	}
}
