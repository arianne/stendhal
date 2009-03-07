package games.stendhal.client.gui.bag;

import java.awt.image.BufferedImage;

public class ItemImage {

	private static final CircledCollection<BufferedImage> TEMPLATE = new CircledCollection<BufferedImage>();
	private CircledCollection<BufferedImage>[] niceNames;

	public CircledCollection<BufferedImage>[] getNiceNames() {
		return niceNames;
	}

	CircledCollection<BufferedImage>[] init(final BufferedImage image) {
		int sequence = image.getWidth() / 32;
		int states = image.getHeight() / 32;
		
		niceNames = TEMPLATE.newArray(states);
		for (int i = 0; i < states; i++) {
			niceNames[i] = new CircledCollection<BufferedImage>();
			for (int j = 0; j < sequence; j++) {
				niceNames[i].add(image.getSubimage(j, i, 32, 32));
				
			}
		}
		return niceNames;
	}
	
	
	
	
	

}
