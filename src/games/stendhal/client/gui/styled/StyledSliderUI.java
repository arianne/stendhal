package games.stendhal.client.gui.styled;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalSliderUI;

/**
 * A SliderUI implementation using {@link Style} settings.
 * <br>
 * <b>IMPORTANT:</b> Only drawing horizontal sliders is implemented, and
 * trying to use this for vertical sliders will most likely fail spectacularly. 
 */
public class StyledSliderUI extends MetalSliderUI {
	private static final int TRACK_HEIGHT = 6;
	private static final int SLIDER_WIDTH = 8;
	private final Style style;

	public StyledSliderUI(Style style) {
		this.style = style;
	}
	
	@Override
	public void paintTrack(Graphics g) {
		int x = trackRect.x;
		int y = trackRect.y + (trackRect.height - TRACK_HEIGHT) / 2;
		
		// Right side of the slider
		int adjust = xPositionForValue(slider.getValue());
		/*
		 * If the user is adjusting the slider, getValue() is not accurate
		 * enough.
		 */
		if (isDragging()) {
			/*
			 * Needs to be done like this, because getMousePosition() does
			 * not necessarily return a non null value in the next call.
			 */
			Point point = slider.getMousePosition();
			if (point != null) {
				adjust = point.x;
			}
		}
		// Center of the slider
		adjust -= thumbRect.width / 2;
		adjust = Math.max(0, adjust);
		
		g.setColor(slider.getForeground());
		g.fillRect(x + adjust, y, trackRect.width - adjust, TRACK_HEIGHT);
		// Who knows why painBorder has Component as the first parameter?
		// Anyway, passing it null does not seem to crash it.
		style.getBorderDown().paintBorder(null, g, x, y, trackRect.width, TRACK_HEIGHT);
	}
	
	@Override
	public void paintThumb(Graphics g) {
		Rectangle r = getThumbShape();
		StyleUtil.fillBackground(style, g, r.x, r.y, r.width, r.height);
		style.getBorder().paintBorder(null, g, r.x, r.y, r.width, r.height);
	}
	
	@Override
	public void paintFocus(Graphics g) {
		g.setColor(Color.GRAY);
		Rectangle r = getThumbShape();
		g.drawRect(r.x - 1, r.y, r.width + 1, r.height - 1);
	}
	
	private Rectangle getThumbShape() {
		int x = thumbRect.x - (SLIDER_WIDTH - thumbRect.width) / 2;
		
		return new Rectangle(x, thumbRect.y, SLIDER_WIDTH, thumbRect.height);
	}
	
	@Override
	public void installUI(JComponent slider) {
		super.installUI(slider);
		// A pixmap style will not tile right if we try to draw the background 
		// here
		slider.setOpaque(false);
		slider.setForeground(style.getForeground());
	}
}
