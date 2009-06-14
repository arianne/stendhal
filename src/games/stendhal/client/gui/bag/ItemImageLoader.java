package games.stendhal.client.gui.bag;

import static javax.imageio.ImageIO.read;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import marauroa.common.game.RPObject;

public class ItemImageLoader {

	static final ConcurrentMap<String, BufferedImage> ImageMap = new ConcurrentHashMap<String, BufferedImage>();

	public BufferedImage loadFromObject(final RPObject object) {
		final String path = extractPathFromObject(object);
		return loadFromPath(path);
	}

	private String extractPathFromObject(final RPObject object) {
		final String clazz = object.get("class");
		final String subClass = object.get("subclass");
		final String path = "/data/sprites/items/" + clazz + "/" + subClass
				+ ".png";
		return path;
	}

	public BufferedImage loadFromPath(final String path) {
		BufferedImage img = ImageMap.get(path);
		if (img != null) {
			return img;
		} else {
			try {
				final URL resource = getClass().getResource(path);
				if (resource != null) {
					img = read(resource);
					ImageMap.put(path, img);
				} else {
					Logger.getLogger(ItemImageLoader.class).error("file not found: " + path);
				}
			} catch (final IOException e) {
				Logger.getLogger(ItemImageLoader.class).error("io error while loading: " + path, e);
			}
		}
		return img;
	}

	public static void main(final String[] args) {
		final RPObject object = new RPObject();
		new ItemImageLoader().loadFromObject(object);
	}

	public ItemImage loadItemImageFromPath(final String path) {
		final ItemImage itemImage = new ItemImage();
		itemImage.init(loadFromPath(path));
		return itemImage;

	}

	public ItemImage loadItemImageFromObject(final RPObject object) {
		final ItemImage itemImage = new ItemImage();
		final String path = extractPathFromObject(object);
		itemImage.init(loadFromPath(path));
		return itemImage;

	}

}
