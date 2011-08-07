package games.stendhal.client.gui.settings;

import games.stendhal.client.gui.wt.core.WtWindowManager;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

public class SettingsComponentFactory {
	
	public static JCheckBox createSettingsToggle(final String parameter, String defaultValue, String label, String tooltip) {
		boolean selected = false;
		JCheckBox toggle = new JCheckBox(label);
		toggle.setToolTipText(tooltip);
		selected = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty(parameter, defaultValue));
		toggle.setSelected(selected);
		
		toggle.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
				WtWindowManager.getInstance().setProperty(parameter, Boolean.toString(enabled));
			}
		});
		return toggle;
	}

}
