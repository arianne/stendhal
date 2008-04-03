package games.stendhal.client.gui;

import java.util.Properties;

/** encapsulates the configuration of a window. */
class WindowConfiguration {

	/** name of the window. */
	public String name;

	/** minimized state of the window. */
	public boolean minimized;

	/** is the window visible? */
	public boolean visible;

	/** x-pos. */
	public int x;

	/** y-pos. */
	public int y;

	public WindowConfiguration(String name) {
		this.name = name;
	}

	/** returns to config as a property string. */
	public String writeToPropertyString() {
		return "window." + name + ".minimized=" + minimized + "\n"
				+ "window." + name + ".visible=" + visible + "\n"
				+ "window." + name + ".x=" + x + "\n" + "window." + name
				+ ".y=" + y + "\n";
	}

	/** returns to config as a property string. */
	@Override
	public String toString() {
		return writeToPropertyString();
	}

	/** adds all props to the property. */
	public void writeToProperties(Properties props) {
		props.put("window." + name + ".minimized", minimized);
		props.put("window." + name + ".visible", visible);
		props.put("window." + name + ".x", x);
		props.put("window." + name + ".y", y);
	}

	/** reads the config from the properties. */
	public void readFromProperties(Properties props, boolean defaultMinimized,
	                                int defaultX, int defaultY, boolean defaultVisible) {
		minimized = Boolean.parseBoolean(props.getProperty("window." + name
				+ ".minimized", Boolean.toString(defaultMinimized)));
		visible = Boolean.parseBoolean(props.getProperty("window." + name
				+ ".visible", Boolean.toString(defaultVisible)));
		x = Integer.parseInt(props.getProperty("window." + name + ".x",
				Integer.toString(defaultX)));
		y = Integer.parseInt(props.getProperty("window." + name + ".y",
				Integer.toString(defaultY)));
	}

	/** reads the config from the properties. */
	public void readFromProperties(Properties props, ClientPanel defaults) {
		readFromProperties(props, defaults.isIcon(), defaults.getX(), defaults.getY(), defaults.isVisible());
	}

}
