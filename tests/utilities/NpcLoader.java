package utilities;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import games.stendhal.server.core.config.XMLUtil;
import games.stendhal.server.core.config.zone.ConfiguratorDescriptor;

public class NpcLoader {

	public Collection<ConfiguratorDescriptor> loadNpcZoneConfiguratorDescriptors(
			String zoneFilePath) {
		Collection<ConfiguratorDescriptor> zoneConfigurators = new LinkedList<ConfiguratorDescriptor>();
		Collection<String> classNames = loadConfiguratorClassNamesFromZoneFile(zoneFilePath);
		for (String className : classNames) {
			if (className.endsWith("NPC")) {
				ConfiguratorDescriptor descriptor = new ConfiguratorDescriptor(
						className);
				zoneConfigurators.add(descriptor);
			}
		}
		return zoneConfigurators;
	}

	private Collection<String> loadConfiguratorClassNamesFromZoneFile(
			String zoneFilePath) {
		Collection<String> classNames = new LinkedList<String>();
		InputStream in = this.getClass().getResourceAsStream(zoneFilePath);
		try {
			Document doc = XMLUtil.parse(in);
			List<Element> zoneElements = XMLUtil.getElements(
					doc.getDocumentElement(), "zone");
			for (Element element : zoneElements) {
				for (Element child : XMLUtil.getElements(element)) {
					String tag = child.getTagName();
					if (tag.equals("configurator")) {
						String className = child.getAttribute("class-name");
						classNames.add(className);
					}
				}
			}
		} catch (Exception e) {
			close(in);
		}
		close(in);
		return classNames;
	}

	private void close(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
