package games.stendhal.client.gui.styled;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class StyledComboBoxUI extends BasicComboBoxUI {
	private final Style style;
	
	// Required by UIManager
	public static ComponentUI createUI(JComponent menuItem) {
		// BasicComboBoxUI can not be shared
		return new StyledComboBoxUI(StyleUtil.getStyle());
	}
	
	/**
	 * Create a new SytledComboBoxUI.
	 * 
	 * @param style pixmap style
	 */
	public StyledComboBoxUI(Style style) {
		this.style = style;
	}
	
	@Override
	protected JButton createArrowButton() {
		return new StyledArrowButton(SwingConstants.SOUTH, style);
	}
	
	@Override
	public void installUI(JComponent component) {
		super.installUI(component);
		component.setBorder(style.getBorderDown());
		listBox.setSelectionBackground(style.getShadowColor());
		listBox.setSelectionForeground(style.getForeground());
	}
}
