/*
 * ColorSwatch.java is public domain:
 * http://blog.noblemaster.com/2010/08/19/color-swatch-color-selector-for-java/
 */

package games.stendhal.tools.colorselector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**
 * Displays a color swatch.
 *
 * @author noblemaster
 * @since August 16, 2010
 */
public class ColorSwatch extends JPanel {
  /**
	 *
	 */
	private static final long serialVersionUID = -5148427974120905902L;

/** The rollover color. */
  private static final Color ROLLOVER_COLOR = new Color(0x80ffffff, true);

  /** The listener. */
  public static interface ColorListener {

    /**
     * Called if a color has been activated.
     *
     * @param color  The color.
     */
    void handleColor(Color color);
  }

  /** The listeners. */
  private transient List<ColorListener>
 listeners = new ArrayList<ColorListener>();

  /** The colors. */
  private Color[][] colors;

  /** The rollover color. */
  private Color rolloverColor = null;

  /**
   * The constructor.
   */
  public ColorSwatch() {
    this(new Color[0][0]);
  }

  /**
   * The constructor.
   *
   * @param colors  The colors to select from.
   */
  public ColorSwatch(Color[][] colors) {
    this.colors = colors;

    // set look
    setOpaque(true);
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

    // add listener
    MouseInputListener mouseListener = new MouseInputListener() {
      @Override
	public void mousePressed(MouseEvent event) {
        Color color = getColor(event.getX(), event.getY());

        // notify about selection
        for (int i = 0; i < listeners.size(); i++) {
          listeners.get(i).handleColor(color);
        }
      }
      @Override
	public void mouseMoved(MouseEvent event) {
        rolloverColor = getColor(event.getX(), event.getY());
        repaint();
      }
      @Override
	public void mouseDragged(MouseEvent event) {
        // not used
      }
      @Override
	public void mouseReleased(MouseEvent event) {
        // not used
      }
      @Override
	public void mouseClicked(MouseEvent arg0) {
        // not used
      }
      @Override
	public void mouseEntered(MouseEvent arg0) {
        // not used
      }
      @Override
	public void mouseExited(MouseEvent arg0) {
        rolloverColor = null;
        repaint();
      }
    };
    addMouseListener(mouseListener);
    addMouseMotionListener(mouseListener);
  }

  /**
   * Returns the colors.
   *
   * @return  The colors.
   */
  public Color[][] getColors() {
    return colors;
  }

  /**
   * Sets the colors.
   *
   * @param colors  The colors.
   */
  public void setColors(Color[][] colors) {
    this.colors = colors;
  }

  /**
   * Returns the preferred size.
   *
   * @return  The preferred size.
   */
  @Override
  public Dimension getPreferredSize() {
    int colorSize = getColorSize();
    int colorSpacing = getColorSpacing();
    int margin = getMargin();
    return new Dimension((colorSize + colorSpacing) * colors[0].length
                              + (2 * margin) - colorSpacing
                       , (colorSize + colorSpacing) * colors.length
                              + (2 * margin) - colorSpacing);
  }

  /**
   * Returns the margin/insets from where the colors are painted. Override this method to paint the colors
   * further inside.
   *
   * @return  The margin.
   */
  public int getMargin() {
    return 2;
  }

  /**
   * Returns the color for the given coordinate.
   *
   * @param x  The x coordinate.
   * @param y  The y coordinate.
   * @return  The color or null if not found.
   */
  private Color getColor(int x, int y) {
    int margin = getMargin();
    int colorSize = getColorSize();
    int colorSpacing = getColorSpacing();
    int marginedX = x - margin;
    int marginedY = y - margin;
    int col = marginedX / (colorSize + colorSpacing);
    int row = marginedY / (colorSize + colorSpacing);
    if ((col < 0) || (col >= colors[0].length) || (row < 0)
                       || (row >= colors.length)) {
      return null;
    }
    else {
      return colors[row][col];
    }
  }

  /**
   * Returns the spacing.
   *
   * @return  The spacing.
   */
  private int getColorSpacing() {
    return getColorSize() > 2 ? 1 : 0;
  }

  /**
   * Returns the size of a color thingy.
   *
   * @return  The color size in pixels.
   */
  private int getColorSize() {
    if ((colors[0].length <= 12) && (colors.length <= 8)) {
      return 15;
    }
    else if ((colors[0].length <= 24) && (colors.length <= 16)) {
      return 7;
    }
    else if ((colors[0].length <= 48) && (colors.length <= 32)) {
      return 5;
    }
    else if ((colors[0].length <= 96) && (colors.length <= 64)) {
      return 3;
    }
    else {
      return 1;
    }
  }

  /**
   * Draws this component.
   *
   * @param g  Where to draw to.
   */
  @Override
  protected void paintComponent(Graphics g) {
    // use antialiasing
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
    int width = getWidth();
    int height = getHeight();
    int margin = getMargin();
    int colorSize = getColorSize();
    int colorSpacing = getColorSpacing();

    // background fill
    g.setColor(getBackground());
    g.fillRect(0, 0, width, height);

    // draw the colors
    for (int y = 0; y < colors.length; y++) {
      for (int x = 0; x < colors[0].length; x++) {         Color color = colors[y][x];         g.setColor(color);         g.fillRect(x * (colorSize + colorSpacing) + margin                  , y * (colorSize + colorSpacing) + margin                  , colorSize                  , colorSize);                  // draw rollover?         if (colorSpacing > 0) {
          if (color == rolloverColor) {
            g.setColor(ROLLOVER_COLOR);
            g.fillRect(x * (colorSize + colorSpacing) + margin
                , y * (colorSize + colorSpacing) + margin
                , colorSize
                , colorSize);
          }
        }
      }
    }

  /**
   * Adds a listener.
   *
   * @param listener  The listener.
   */
  public void addColorListener(ColorListener listener) {
    listeners.add(listener);
  }

  /**
   * Removes a listener.
   *
   * @param listener  The listener.
   */
  public void removeColorListener(ColorListener listener) {
    listeners.remove(listener);
  }
}
