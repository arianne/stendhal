package games.stendhal.client.gui.styled;

import javax.swing.UIDefaults;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class StyledLookAndFeel extends MetalLookAndFeel {
	private static final String pkg = "games.stendhal.client.gui.styled.";
	
	private final Style style;
	
	public StyledLookAndFeel(Style style) {
		super();
		this.style = style;
	}
	
	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);

		Object[] uiDefaults = {
			// Provide access to the style for the components
			"StendhalStyle", style,
			// The component UIs
			"ButtonUI", pkg + "StyledButtonUI",
			"MenuItemUI", pkg + "StyledMenuItemUI",
			"PanelUI", pkg + "StyledPanelUI",
			"PasswordFieldUI", pkg + "StyledPasswordFieldUI",
			"PopupMenuUI", pkg + "StyledPopupMenuUI",
			"ScrollBarUI", pkg + "StyledScrollBarUI",
			"ScrollPaneUI", pkg + "StyledScrollPaneUI",
			"SliderUI", pkg + "StyledSliderUI",
			"SplitPaneUI", pkg + "StyledSplitPaneUI",
			"TextFieldUI", pkg + "StyledTextFieldUI",
		};
		
		table.putDefaults(uiDefaults);
	}
	
	@Override
	public boolean isSupportedLookAndFeel() {
		// supported everywhere
		return true;
	}
	
	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}
	
	@Override
	public String getDescription() {
		return "Stendhal pixmap look and feel";
	}
	
	@Override
	public String getID() {
		return "Stendhal";
	}
	
	@Override
	public String getName() {
		return "Stendhal";
	}
}
