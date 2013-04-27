/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.settings;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.gui.styled.StyledLookAndFeel;
import games.stendhal.client.gui.styled.styles.StyleFactory;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.MathHelper;
import games.stendhal.common.NotificationType;

import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 * Page for style settings.
 */
class VisualSettings {
	private static final String STYLE_PROPERTY = "ui.style";
	private static final String DEFAULT_STYLE = "Wood (default)";

	private static final String GAMESCREEN_CREATURESPEECH = "gamescreen.creaturespeech";
	
	/** Containers that have components to be toggled */
	//private JPanel colorsPanel;
	
	/** Buttons for selecting either defined styles or custom styles */
	/*private JRadioButton definedStyleSelector;
	private JRadioButton customStyleSelector;*/
	
	/** Default decorative font. */
	private static final String DEFAULT_FONT = "BlackChancery";
	/** Default font size. */
	private static final int DEFAULT_FONT_SIZE = 12;
	/** Smallest size in the font size selector. */
	private static final int FONT_MIN_SIZE = 8;
	/** Largest size in the font size selector. */
	private static final int FONT_MAX_SIZE = 20;
	/** Property used for the decorative font. */
	private static final String FONT_PROPERTY = "ui.logfont";
	/** Property used for the decorative font. */
	private static final String FONT_SIZE_PROPERTY = "ui.font_size";
	
	private static final String GAMESCREEN_BLOOD = "gamescreen.blood";
	
	private static final String SCALE_SCREEN_PROPERTY = "ui.scale_screen";
	/** Property used for toggling map coloring on. */
	private static final String MAP_COLOR_PROPERTY = "ui.colormaps";
	
	/** Container for the setting components. */
	private final JComponent page;
	
	/**
	 * Create new StyleSettings.
	 */
	VisualSettings() {
		int pad = SBoxLayout.COMMON_PADDING;
		page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		
		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		
		page.add(createStyleTypeSelector(), SBoxLayout.constraint(SLayout.EXPAND_X));
		
		// Disable widgets not in use
		toggleComponents(page);
		
		// Lighting effects
		JCheckBox mapColoring = SettingsComponentFactory.createSettingsToggle(MAP_COLOR_PROPERTY, "true",
				"Light effects", "Show night time lighting, and other coloring effects");
		page.add(mapColoring);
		// Coloring setting needs a map change to take an effect, so we need to
		// inform the player about the delayed effect.
		mapColoring.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
				String tmp = enabled ? "enabled" : "disabled";
				String msg = "Lighting effects are now " + tmp
						+ ". You may need to change map or relogin for it to take effect.";
				ClientSingletonRepository.getUserInterface().addEventLine(new EventLine("", msg, NotificationType.CLIENT));
			}
		});

		// blood
		JCheckBox showBloodToggle = SettingsComponentFactory.createSettingsToggle(GAMESCREEN_BLOOD, "true",
				"Show blood and corpses", "Show blood spots on hits during fighting, and corpses.");
		page.add(showBloodToggle);
		
		// show creature speech bubbles
		JCheckBox showCreatureSpeechToggle = SettingsComponentFactory.createSettingsToggle(GAMESCREEN_CREATURESPEECH, "true",
										"Show creature speech bubbles", "Show creature speech bubbles in the client display");
		page.add(showCreatureSpeechToggle);
		
		final JCheckBox scaleScreenToggle = SettingsComponentFactory.createSettingsToggle(SCALE_SCREEN_PROPERTY,
				"true", "Scale view to fit window", "<html>If selected, the game view will scale to fit the available space,<br>otherwise the default sized graphics are used.</html>");
		page.add(scaleScreenToggle);
		page.add(Box.createHorizontalStrut(SBoxLayout.COMMON_PADDING));
		
		// Font stuff
		page.add(createFontSizeSelector());
		page.add(createFontSelector(), SBoxLayout.constraint(SLayout.EXPAND_X));
	}
	
	/**
	 * Get the component containing the style settings.
	 * 
	 * @return style settings page
	 */
	JComponent getComponent() {
		return page;
	}
	
	/**
	 * Create a selector for styles.
	 * 
	 * @return combo box with style options
	 */
	private JComponent createStyleSelector() {
		final JComboBox selector = new JComboBox();
		
		// Fill with available styles
		for (String s : StyleFactory.getAvailableStyles()) {
			selector.addItem(s);
		}
		
		final WtWindowManager wm = WtWindowManager.getInstance();
		String currentStyle = wm.getProperty(STYLE_PROPERTY, DEFAULT_STYLE);
		selector.setSelectedItem(currentStyle);
		 
		selector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selected = selector.getSelectedItem();
				wm.setProperty(STYLE_PROPERTY, selected.toString());
				ClientSingletonRepository.getUserInterface().addEventLine(new EventLine("",
						"The new style will be used the next time you start the game client.",
						NotificationType.CLIENT));
			}
		});
		
		return selector;
	}
	
	/**
	 * Disables widgets not being used.
	 * 
	 * @param container the container to look within
	 */
	private void toggleComponents(Container container) {
		/*
		boolean custom = false;
		if (this.customStyleSelector.isSelected()) {
			custom = true;
		}
		
		Component[] components = container.getComponents();
		
		for (Component c : components) {
			if (c.getName() == "defined") {
				c.setEnabled(!custom);
			}
			else if (c.getName() == "custom") {
				c.setEnabled(custom);
			}
			if (c instanceof Container) {
				toggleComponents((Container) c);
			}
		}*/
	}
	
	/**
	 * Create selector for choosing between defined and custom styles
	 * 
	 * @return layout for styles widgets
	 */
	private JComponent createStyleTypeSelector() {
		int pad = SBoxLayout.COMMON_PADDING;
		
		// Styles
		JComponent styleBox = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		/*
		styleBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(pad, pad, pad, pad)));
		// Button group for selecting between defined and custom styles
		definedStyleSelector = new JRadioButton("Use a pre-defined style", true);
		customStyleSelector = new JRadioButton("Use a custom style");
		ButtonGroup styleTypeSelection = new ButtonGroup();
		
		// Defined style selector
		styleTypeSelection.add(definedStyleSelector);
		*/
		JComponent definedStylesHBox = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		JLabel selectorLabel = new JLabel("Client style:");
		selectorLabel.setName("defined");
		definedStylesHBox.add(selectorLabel);
		JComponent selector = createStyleSelector();
		selector.setName("defined");
		definedStylesHBox.add(selector);
		definedStylesHBox.setToolTipText("<html>The style used to draw the controls in the game client."
				+ "<p>This affects the look only, and will not change the behavior of the game.</html>");
		/*
		styleBox.add(definedStyleSelector);*/
		styleBox.add(definedStylesHBox);
		/*
		// Custom style options
		styleTypeSelection.add(customStyleSelector);
		styleBox.add(customStyleSelector);
		
		// Text and border colors
		final JPanel colorsPanel = new JPanel();
		colorsPanel.setName("custom");
		colorsPanel.setLayout(new GridLayout(2, 1));
		List<JLabel> colorLabels = Arrays.asList(
				new JLabel("Text"), new JLabel("Hightlight"), new JLabel("Shadow"),
				new JLabel("Border Color 1"), new JLabel("Border Color 2"),
				new JLabel("Border Color 3"), new JLabel("Border Color 4")
				);
		int ccount;
		List<ColorSelector> colorButtons = Arrays.asList(
				new ColorSelector(), new ColorSelector(), new ColorSelector(),
				new ColorSelector(), new ColorSelector(), new ColorSelector(),
				new ColorSelector()
				);
		for (ccount = 0; ccount < colorLabels.size(); ccount++) {
			colorsPanel.add(colorLabels.get(ccount));
		}
		for (ccount = 0; ccount < colorButtons.size(); ccount++) {
			colorsPanel.add(colorButtons.get(ccount));
		}
		
		styleBox.add(colorsPanel);
		
		// Background image
		JLabel bgSelectorText = new JLabel("Background image");
		bgSelectorText.setName("custom");
		JButton bgSelectorButton = new JButton("...");
		bgSelectorButton.setName("custom");
		JTextField bgSelectorInput = new JTextField();
		bgSelectorInput.setName("custom");
		JComponent bgSelectorHBox = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		bgSelectorHBox.add(bgSelectorButton);
		bgSelectorHBox.add(bgSelectorInput);
		
		styleBox.add(bgSelectorText);
		styleBox.add(bgSelectorHBox);
		
		bgSelectorButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						selectBGImage();
					}
				});
		
		styleBox.add(Box.createHorizontalStrut(SBoxLayout.COMMON_PADDING));
		
		// Add event handlers for the style selector radio buttons
		definedStyleSelector.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						toggleComponents(page);
						toggleComponents(colorsPanel);
					}
				});
		customStyleSelector.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						toggleComponents(page);
						toggleComponents(colorsPanel);
					}
				});
		*/
		return styleBox;
	}
	
	/**
	 * Create selector for the default font size.
	 * 
	 * @return component containing the selector
	 */
	private JComponent createFontSizeSelector() {
		JComponent container = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
		container.add(new JLabel("Text size:"));
		
		final JComboBox selector = new JComboBox();
		
		// Fill the selector, and set current size as the selection
		int current = WtWindowManager.getInstance().getPropertyInt(FONT_SIZE_PROPERTY, DEFAULT_FONT_SIZE);
		selector.addItem("default (12)");
		for (int size = FONT_MIN_SIZE; size <= FONT_MAX_SIZE; size += 2) {
			Integer obj = size;
			selector.addItem(obj);
			if ((size == current) && (size != DEFAULT_FONT_SIZE)) {
				selector.setSelectedItem(obj);
			}
		}
		
		selector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selected = selector.getSelectedItem();
				if ("default (12)".equals(selected)) {
					selected = "12";
				}
				WtWindowManager.getInstance().setProperty(FONT_SIZE_PROPERTY, selected.toString());
				
				LookAndFeel look = UIManager.getLookAndFeel();
				if (look instanceof StyledLookAndFeel) {
					int size = MathHelper.parseIntDefault(selected.toString(), DEFAULT_FONT_SIZE);
					((StyledLookAndFeel) look).setDefaultFontSize(size);
				}
			}
		});
		container.add(selector);
		container.setToolTipText("Common text size");
		return container;
	}
	
	/**
	 * Create selector for the font used in the quest log and achievements.
	 * 
	 * @return component for specifying a font
	 */
	private JComponent createFontSelector() {
		int pad = SBoxLayout.COMMON_PADDING;
		JComponent fontBox = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		fontBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(pad, pad, pad, pad)));
		
		// There seems to be no good way to change the default background color
		// of all components. The color is needed for making the etched border.
		Style style = StyleUtil.getStyle();
		if (style != null) {
			fontBox.setBackground(style.getPlainColor());
		}
		
		JCheckBox fontToggle = new JCheckBox("Custom Decorative Font");
		fontToggle.setToolTipText("Set a custom font for the travel log and achievements");
		fontBox.add(fontToggle);
		
		JComponent fontRow = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		SBoxLayout.addSpring(fontRow);
		fontBox.add(fontRow, SBoxLayout.constraint(SLayout.EXPAND_X));
		final JLabel label = new JLabel("Font:");
		fontRow.add(label);
		final JComboBox fontList = new JComboBox();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (String font : ge.getAvailableFontFamilyNames()) {
			fontList.addItem(font);
		}
		// Show the user what's in use at the moment
		String font = WtWindowManager.getInstance().getProperty(FONT_PROPERTY, DEFAULT_FONT);
		fontList.setSelectedItem(font);
		fontRow.add(fontList);
		
		// Detect if the font property had been changed from the default.
		boolean changed = fontChanged(); 
		fontToggle.setSelected(changed);
		fontList.setEnabled(changed);
		label.setEnabled(changed);
		
		// Bind the toggle button to enabling and disabling the selector
		fontToggle.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
				if (enabled) {
					String selected = fontList.getSelectedItem().toString();
					WtWindowManager.getInstance().setProperty(FONT_PROPERTY, selected);
				} else {
					WtWindowManager.getInstance().setProperty(FONT_PROPERTY, DEFAULT_FONT);
				}
				fontList.setEnabled(enabled);
				label.setEnabled(enabled);
			}
		});
		
		// Bind changing the selection to changing the font. The selector is
		// enabled only when font changing is enabled, so this should be safe
		fontList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selected = fontList.getSelectedItem().toString();
				WtWindowManager.getInstance().setProperty(FONT_PROPERTY, selected);
			}
		});
		
		return fontBox;
	}
	
	/**
	 * Check if a custom font is in use.
	 * 
	 * @return <code>true</code> if the user has changed the font from the
	 * 	default, <code>false</code> otherwise 
	 */
	private boolean fontChanged() {
		String currentSetting = WtWindowManager.getInstance().getProperty(FONT_PROPERTY, DEFAULT_FONT);
		return !currentSetting.equals(DEFAULT_FONT);
	}

	/*
	private String selectBGImage() {
		JFileChooser bgSelector = new JFileChooser();
		bgSelector.setDialogType(JFileChooser.OPEN_DIALOG | JFileChooser.FILES_ONLY);
		bgSelector.setDialogTitle("Select an image to use for the client background");
		//bgSelector.createDialog(this.page);
		
		// Returning null until I figure out how to use JFileChooser
		return null;
	}
	*/
}
