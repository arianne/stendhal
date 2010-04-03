package games.stendhal.tools.charas;

import java.awt.image.BufferedImage;

/**
 * A utility to perform the scale2x algorithm on an image.
 * 
 * @author Kevin Glass
 * @author johnnnny
 */
public class ImageScale2x {

	/*
	 * No need to get an instance of this class, therefore private
	 */
	private ImageScale2x() {
	}

	/**
	 * Retrieve the scaled image. Note this is the method that actually does
	 * the work so it may take some time to return
	 * @param srcImage the source image to be scaled
	 * @return The newly scaled image
	 */
	public static BufferedImage getScaledImage(BufferedImage srcImage) {
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();

		int[] srcData = new int[width * height];
		srcImage.getRGB(0, 0, width, height, srcData, 0, width);
		RawScale2x scaler = new RawScale2x(srcData, width, height);

		BufferedImage image = new BufferedImage(width * 2, height * 2,
				BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, width * 2, height * 2, scaler.getScaledData(), 0,
				width * 2);

		return image;
	}
}
