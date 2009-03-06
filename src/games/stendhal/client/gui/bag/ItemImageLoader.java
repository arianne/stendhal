package games.stendhal.client.gui.bag;

import static javax.imageio.ImageIO.read;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import marauroa.common.game.RPObject;

public class ItemImageLoader {

	static final ConcurrentMap<String, BufferedImage> ImageMap = new ConcurrentHashMap<String, BufferedImage>();
	
	public  BufferedImage loadFromObject(final RPObject object)  {
			final String clazz = object.get("class");
			final String subClass = object.get("subclass");
			final String path = "/data/sprites/items/" + clazz + "/" + subClass + ".png";
			
				
			return loadFromPath(path);
	}
	
	
public  BufferedImage loadFromPath(final String path)  {
	BufferedImage img = ImageMap.get(path);
	if (img != null) {
		return img;
	} else {
	
	
		try {
			URL resource = getClass().getResource(path);
			if (resource != null) {
				img = read(resource);
				ImageMap.put(path, img);
			} else {
				System.out.println("file not found :" +  path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		return img;
	}
	
	public static void main(final String[] args) {
		RPObject object = new RPObject();
		new ItemImageLoader().loadFromObject(object);
	}
	
}
