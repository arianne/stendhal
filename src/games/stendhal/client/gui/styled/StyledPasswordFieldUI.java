package games.stendhal.client.gui.styled;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;

public class StyledPasswordFieldUI extends BasicPasswordFieldUI {
	private final Style style;
	
	// Required by UIManager
	public static ComponentUI createUI(JComponent field) {
		// BasicTextFieldUI can not be shared
		return new StyledPasswordFieldUI(StyleUtil.getStyle());
	}
	
	/**
	 * Create a new StyledPasswordFieldUI.
	 * 
	 * @param style pixmap style
	 */
	public StyledPasswordFieldUI(Style style) {
		this.style = style;
	}
	
	@Override
	public void installUI(JComponent field) {
		super.installUI(field);
		field.setBorder(style.getBorderDown());
	}
}
