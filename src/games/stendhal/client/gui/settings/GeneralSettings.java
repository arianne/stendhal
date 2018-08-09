/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import static games.stendhal.client.gui.settings.SettingsProperties.DOUBLE_TAP_AUTOWALK_PROPERTY;
import static games.stendhal.client.gui.settings.SettingsProperties.MOVE_CONTINUOUS_PROPERTY;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.border.TitledBorder;

import games.stendhal.client.actions.MoveContinuousAction;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.gui.wt.core.SettingChangeListener;
import games.stendhal.client.gui.wt.core.WtWindowManager;

/**
 * Page for general settings.
 */
class GeneralSettings {
	private static final String GAMESCREEN_AUTORAISECORPSE = "gamescreen.autoraisecorpse";

	/** Property used for the double click setting. */
	private static final String DOUBLE_CLICK_PROPERTY = "ui.doubleclick";

	private static final String HEALING_MESSAGE_PROPERTY = "ui.healingmessage";

	private static final String POISON_MESSAGE_PROPERTY = "ui.poisonmessage";

	private static final String DIMENSIONS_PROPERTY = "ui.dimensions";

	/** Container for the setting components. */
	private final JComponent page;

	/**
	 * Create new GeneralSettings.
	 */
	GeneralSettings() {
		int pad = SBoxLayout.COMMON_PADDING;
		page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);

		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));

		// click mode
		JCheckBox clickModeToggle = SettingsComponentFactory.createSettingsToggle(DOUBLE_CLICK_PROPERTY, "false",
				"双击模式", "使用双击鼠标行走或攻击");
		page.add(clickModeToggle);

		// raising corpses
		JCheckBox autoRaiseToggle = SettingsComponentFactory.createSettingsToggle(GAMESCREEN_AUTORAISECORPSE, "true",
				"自动搜尸", "当尸体里发现物品时，自动打开物品清单");
		page.add(autoRaiseToggle);

		// show healing messages
		JCheckBox showHealingToggle = SettingsComponentFactory.createSettingsToggle(HEALING_MESSAGE_PROPERTY, "false",
				"显示健康状态", "在对话框记录中显示健康信息");
		page.add(showHealingToggle);

		// show poison messages
		JCheckBox showPoisonToggle = SettingsComponentFactory.createSettingsToggle(POISON_MESSAGE_PROPERTY, "false",
										"显示中毒状态", "在对话记录中显示中毒信息");
		page.add(showPoisonToggle);

		// Double-tap direction for auto-walk
		JCheckBox doubleTapAutowalkToggle = SettingsComponentFactory.createSettingsToggle(DOUBLE_TAP_AUTOWALK_PROPERTY, "false",
										"自动行走",
										"按两次方向键实现自动行走");
		page.add(doubleTapAutowalkToggle);

		// Continuous movement
		final JCheckBox moveContinuousToggle = SettingsComponentFactory.createSettingsToggle(MOVE_CONTINUOUS_PROPERTY, "false",
										"保持行走", "当转换地图或传送时，行走状态保持不变");
		moveContinuousToggle.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				new MoveContinuousAction().sendAction(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		WtWindowManager.getInstance().registerSettingChangeListener(MOVE_CONTINUOUS_PROPERTY,
				new SettingChangeListener() {
			@Override
			public void changed(String newValue) {
				moveContinuousToggle.setSelected(Boolean.parseBoolean(newValue));
			}
		});
		page.add(moveContinuousToggle);

		// Client dimensions
		JComponent clientSizeBox = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		TitledBorder titleB = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"窗口状态设置");

		// There seems to be no good way to change the default background color
		// of all components. The color is needed for making the etched border.
		Style style = StyleUtil.getStyle();
		if (style != null) {
			clientSizeBox.setBackground(style.getPlainColor());
			titleB.setTitleColor(style.getForeground());
		}
		clientSizeBox.setBorder(BorderFactory.createCompoundBorder(titleB,
				BorderFactory.createEmptyBorder(pad, pad, pad, pad)));

		// Save client dimensions
		JCheckBox saveDimensionsToggle =
				SettingsComponentFactory.createSettingsToggle(
						DIMENSIONS_PROPERTY, "true", "保存窗口大小",
						"保留客户端窗口高度、宽度和最大化等一些状态");
		clientSizeBox.add(saveDimensionsToggle);

		// Reset client window to default dimensions
		JButton resetDimensions = new JButton("初始值");
		resetDimensions.setToolTipText(
				"重置窗口到初始状态");
		resetDimensions.setActionCommand("reset_dimensions");
		resetDimensions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetClientDimensions();
			}
		});
		resetDimensions.setAlignmentX(Component.RIGHT_ALIGNMENT);
		clientSizeBox.add(resetDimensions);

		page.add(clientSizeBox, SLayout.EXPAND_X);
	}

	/**
	 * Get the component containing the general settings.
	 *
	 * @return general settings page
	 */
	JComponent getComponent() {
		return page;
	}

	/**
	 * Resets the clients width and height to their default values.
	 */
	private void resetClientDimensions() {
		j2DClient clientFrame = j2DClient.get();
		Frame mainFrame = clientFrame.getMainFrame();
		int frameState = mainFrame.getExtendedState();

		/*
		 *  Do not attempt to reset client dimensions if window is maximized.
		 *  Prevents resizing errors for child components.
		 */
		if (frameState != Frame.MAXIMIZED_BOTH) {
			mainFrame.setSize(clientFrame.getFrameDefaultSize());
		}
	}
}
