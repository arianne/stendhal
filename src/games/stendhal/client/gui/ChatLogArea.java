/***************************************************************************
 *                (C) Copyright 2003-2018 - Faiumoni e.V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import static games.stendhal.client.gui.settings.SettingsProperties.MSG_BLINK;
import static games.stendhal.client.gui.settings.SettingsProperties.MSG_SOUND;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.gui.wt.core.SettingChangeAdapter;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.facade.InfiniteAudibleArea;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.SoundLayer;


class ChatLogArea {
	/** Background color of the private chat tab. Light blue. */
	private static final String PRIVATE_TAB_COLOR = "0xdcdcff";

	private final NotificationChannelManager channelManager;
	private final JTabbedPane tabs = new JTabbedPane(SwingConstants.BOTTOM);
	private final Timer animator = new Timer(100, null);

	ChatLogArea(NotificationChannelManager channelManager) {
		this.channelManager = channelManager;
		createLogArea();
	}

	/**
	 * Create the chat log tabs.
	 *
	 * @return chat log area
	 */
	private JTabbedPane createLogArea() {
		tabs.setFocusable(false);
		List<JComponent> logs = createChannelComponents();
		BitSet changedChannels = new BitSet(logs.size());

		// Must be done before adding tabs
		setupTabChangeHandling(changedChannels);

		Iterator<NotificationChannel> it = channelManager.getChannels().iterator();
		for (JComponent tab : logs) {
			tabs.add(it.next().getName(), tab);
		}

		setupHiddenChannelMessageHandling(changedChannels);
		setupAnimation(changedChannels);

		return tabs;
	}

	/**
	 * Create chat channels.
	 *
	 * @return Chat log components of the notification channels
	 */
	private List<JComponent> createChannelComponents() {
		List<JComponent> list = new ArrayList<>();
		KTextEdit edit = new KTextEdit();
		list.add(edit);

		NotificationChannel mainChannel = setupMainChannel(edit);
		channelManager.addChannel(mainChannel);

		// ** Private channel **
		edit = new KTextEdit();
		list.add(edit);
		NotificationChannel personal = setupPersonalChannel(edit);
		channelManager.addChannel(personal);

		return list;
	}

	private NotificationChannel setupPersonalChannel(KTextEdit edit) {
		edit.setChannelName("Personal");
		/*
		 * Give it a different background color to make it different from the
		 * main chat log.
		 */
		edit.setDefaultBackground(Color.decode(PRIVATE_TAB_COLOR));
		/*
		 * Types shown by default in the private/group tab. Admin messages
		 * should occur everywhere, of course, and not be possible to be
		 * disabled in preferences.
		 */
		String personalDefault = NotificationType.PRIVMSG.toString() + ","
				+ NotificationType.CLIENT + "," + NotificationType.GROUP + ","
				+ NotificationType.TUTORIAL + "," + NotificationType.SUPPORT;

		return new NotificationChannel("Personal", edit, false, personalDefault);
	}

	private NotificationChannel setupMainChannel(KTextEdit edit) {
		NotificationChannel channel = new NotificationChannel("Main", edit, true, "");

		// Follow settings changes for the main channel
		WtWindowManager wm = WtWindowManager.getInstance();
		wm.registerSettingChangeListener("ui.healingmessage", new SettingChangeAdapter("ui.healingmessage", "false") {
			@Override
			public void changed(String newValue) {
				channel.setTypeFiltering(NotificationType.HEAL, Boolean.parseBoolean(newValue));
			}
		});
		wm.registerSettingChangeListener("ui.poisonmessage", new SettingChangeAdapter("ui.poisonmessage", "false") {
			@Override
			public void changed(String newValue) {
				channel.setTypeFiltering(NotificationType.POISON, Boolean.parseBoolean(newValue));
			}
		});
		return channel;
	}

	private void setupTabChangeHandling(BitSet changedChannels) {
		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int i = tabs.getSelectedIndex();
				NotificationChannel channel = channelManager.getChannels().get(i);
				channelManager.setVisibleChannel(channel);
				if (changedChannels.get(i)) {
					changedChannels.clear(i);
					// Remove modified marker
					tabs.setBackgroundAt(i, null);
					if (changedChannels.isEmpty()) {
						animator.stop();
					}
				}
			}
		});
	}

	private void setupHiddenChannelMessageHandling(BitSet changedChannels) {
		final WtWindowManager wm = WtWindowManager.getInstance();

		channelManager.addHiddenChannelListener(new NotificationChannelManager.HiddenChannelListener() {
			@Override
			public void channelModified(int index) {
				if (index == 1 && wm.getPropertyBoolean(MSG_SOUND, true)) {
					// play notification
					final String sndFile = "ui/notify_up.ogg";
					if (this.getClass().getResource("/data/sounds/" + sndFile) != null) {
						final SoundGroup group = ClientSingletonRepository.getSound()
							.getGroup(SoundLayer.USER_INTERFACE.groupName);
						group.loadSound(MSG_SOUND, sndFile, SoundFileType.OGG, false);
						group.play(MSG_SOUND, 0, new InfiniteAudibleArea(), null, false, true);
					}
				}

				// Mark the tab as modified so that the user can see there's
				// new text
				if (!changedChannels.get(index)) {
					changedChannels.set(index);
					if (!animator.isRunning() && wm.getPropertyBoolean(MSG_BLINK, true)) {
						animator.start();
					}
				}
			}
		});
	}

	private void setupAnimation(BitSet changedChannels) {
		animator.addActionListener(new AnimationActionListener(changedChannels));
	}

	JComponent getComponent() {
		return tabs;
	}

	private class AnimationActionListener implements ActionListener {
		private final BitSet changedChannels;
		private static final int STEPS = 10;
		private final Color[] colors;
		private int colorIndex;
		private int change = 1;

		private AnimationActionListener(BitSet changedChannels) {
			this.changedChannels = changedChannels;

			colors = new Color[STEPS];
			initColors();
		}

		private void initColors() {
			Color endColor;

			Style style = StyleUtil.getStyle();
			if (style != null) {
				colors[0] = style.getHighLightColor();
				endColor = style.getPlainColor();
			} else {
				colors[0] = Color.BLUE;
				endColor = Color.DARK_GRAY;
			}

			int r = colors[0].getRed();
			int g = colors[0].getGreen();
			int b = colors[0].getBlue();
			int alpha = 0xff;
			int rDelta = r - endColor.getRed();
			int gDelta = g - endColor.getGreen();
			int bDelta = b - endColor.getBlue();
			int alphaDelta;
			if (TransparencyMode.TRANSPARENCY == Transparency.TRANSLUCENT) {
				alphaDelta = 0xff / STEPS;
			} else {
				alphaDelta = 0;
			}
			for (int i = 1; i < STEPS; i++) {
				alpha -= alphaDelta;
				colors[i] = new Color(r - i * rDelta / STEPS, g - i * gDelta / STEPS, b - i * bDelta / STEPS, alpha);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			colorIndex += change;
			if (colorIndex >= colors.length || colorIndex < 0) {
				change = -change;
				colorIndex += change;
			}

			for (int i = changedChannels.nextSetBit(0); i >= 0; i = changedChannels.nextSetBit(i + 1)) {
				tabs.setBackgroundAt(i, colors[colorIndex]);
			}
		}
	}
}
