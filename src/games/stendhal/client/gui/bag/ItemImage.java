package games.stendhal.client.gui.bag;

import java.awt.image.BufferedImage;

public class ItemImage {

	private static final CircledCollection<BufferedImage> TEMPLATE = new CircledCollection<BufferedImage>();
	private CircledCollection<BufferedImage>[] bufImgCircledCollection;

	public CircledCollection<BufferedImage>[] getCircledCollectionArray() {
		return bufImgCircledCollection;
	}

	CircledCollection<BufferedImage>[] init(final BufferedImage image) {
		final int sequence = image.getWidth() / 32;
		final int states = image.getHeight() / 32;
		
		bufImgCircledCollection = TEMPLATE.newArray(states);
		for (int i = 0; i < states; i++) {
			bufImgCircledCollection[i] = new CircledCollection<BufferedImage>();
			for (int j = 0; j < sequence; j++) {
				bufImgCircledCollection[i].add(image.getSubimage(j*32, i, 32, 32));
			}
		}
		return bufImgCircledCollection;
	}
}
