/*
 * ColorSelector.java is public domain:
 * http://blog.noblemaster.com/2010/08/19/color-swatch-color-selector-for-java/
 */

package games.stendhal.tools.colorselector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

/**
 * Represents a color selector.
 *
 * @author noblemaster
 * @since August 16, 2010
 */
public class ColorSelector extends JButton {

  /****/
	private static final long serialVersionUID = 5574529692031984318L;

/** The default colors. */
  private static final int[][] DEFAULT_COLORS = new int[][] {
      { 0xFFFFFF, 0xEEEEEE, 0xDDDDDD, 0xCCCCCC
       , 0xBBBBBB, 0xAAAAAA, 0xFFCC00, 0xFF9900
       , 0xFF6600, 0xFF3300, 0x999999, 0x888888
       , 0x666666, 0x444444, 0x222222, 0x000000 },
      { 0x99CC00, 0x000000, 0x000000, 0x000000
       , 0x000000, 0xCC9900, 0xFFCC33, 0xFFCC66
       , 0xFF9966, 0xFF6633, 0xCC3300, 0x000000
       , 0x000000, 0x000000, 0x000000, 0xCC0033 },
      { 0xCCFF00, 0xCCFF33, 0x333300, 0x666600
       , 0x999900, 0xCCCC00, 0xFFFF00, 0xCC9933
       , 0xCC6633, 0x330000, 0x660000, 0x990000
       , 0xCC0000, 0xFF0000, 0xFF3366, 0xFF0033 },
      { 0x99FF00, 0xCCFF66, 0x99CC33, 0x666633
       , 0x999933, 0xCCCC33, 0xFFFF33, 0x996600
       , 0x993300, 0x663333, 0x993333, 0xCC3333
       , 0xFF3333, 0xCC3366, 0xFF6699, 0xFF0066 },
      { 0x66FF00, 0x99FF66, 0x66CC33, 0x669900
       , 0x999966, 0xCCCC66, 0xFFFF66, 0x996633
       , 0x663300, 0x996666, 0xCC6666, 0xFF6666
       , 0x990033, 0xCC3399, 0xFF66CC, 0xFF0099 },
      { 0x33FF00, 0x66FF33, 0x339900, 0x66CC00
       , 0x99FF33, 0xCCCC99, 0xFFFF99, 0xCC9966
       , 0xCC6600, 0xCC9999, 0xFF9999, 0xFF3399
       , 0xCC0066, 0x990066, 0xFF33CC, 0xFF00CC },
      { 0x00CC00, 0x33CC00, 0x336600, 0x669933
       , 0x99CC66, 0xCCFF99, 0xFFFFCC, 0xFFCC99
       , 0xFF9933, 0xFFCCCC, 0xFF99CC, 0xCC6699
       , 0x993366, 0x660033, 0xCC0099, 0x330033 },
      { 0x33CC33, 0x66CC66, 0x00FF00, 0x33FF33
       , 0x66FF66, 0x99FF99, 0xCCFFCC, 0xFFFFE3
       , 0xFFFFFF, 0xFFE3FF, 0xCC99CC, 0x996699
       , 0x993399, 0x990099, 0x663366, 0x660066 },
      { 0x006600, 0x336633, 0x009900, 0x339933
       , 0x669966, 0x99CC99, 0xE3FFFF, 0xFFFFFF
       , 0xFFFFFF, 0xFFCCFF, 0xFF99FF, 0xFF66FF
       , 0xFF33FF, 0xFF00FF, 0xCC66CC, 0xCC33CC },
      { 0x003300, 0x00CC33, 0x006633, 0x339966
       , 0x66CC99, 0x99FFCC, 0xCCFFFF, 0x3399FF
       , 0x99CCFF, 0xCCCCFF, 0xCC99FF, 0x9966CC
       , 0x663399, 0x330066, 0x9900CC, 0xCC00CC },
      { 0x00FF33, 0x33FF66, 0x009933, 0x00CC66
       , 0x33FF99, 0x99FFFF, 0x99CCCC, 0x0066CC
       , 0x6699CC, 0x9999FF, 0x9999CC, 0x9933FF
       , 0x6600CC, 0x660099, 0xCC33FF, 0xCC00FF },
      { 0x00FF66, 0x66FF99, 0x33CC66, 0x009966
       , 0x66FFFF, 0x66CCCC, 0x669999, 0x003366
       , 0x336699, 0x6666FF, 0x6666CC, 0x666699
       , 0x330099, 0x9933CC, 0xCC66FF, 0x9900FF },
      { 0x00FF99, 0x66FFCC, 0x33CC99, 0x33FFFF
       , 0x33CCCC, 0x339999, 0x336666, 0x006699
       , 0x003399, 0x3333FF, 0x3333CC, 0x333399
       , 0x333366, 0x6633CC, 0x9966FF, 0x6600FF },
      { 0x00FFCC, 0x33FFCC, 0x00FFFF, 0x00CCCC
       , 0x009999, 0x006666, 0x003333, 0x3399CC
       , 0x3366CC, 0x0000FF, 0x0000CC, 0x000099
       , 0x000066, 0x000033, 0x6633FF, 0x3300FF },
      { 0x00CC99, 0x000000, 0x000000, 0x000000
       , 0x000000, 0x0099CC, 0x33CCFF, 0x66CCFF
       , 0x6699FF, 0x3366FF, 0x0033CC, 0x000000
       , 0x000000, 0x000000, 0x000000, 0x3300CC },
      { 0x000000, 0x222222, 0x444444, 0x666666
       , 0x888888, 0x999999, 0x00CCFF, 0x0099FF
       , 0x0066FF, 0x0033FF, 0xAAAAAA, 0xBBBBBB
       , 0xCCCCCC, 0xDDDDDD, 0xEEEEEE, 0xFFFFFF },
  };

  /** The rollover color. */
  private static final Color ROLLOVER_COLOR = new Color(0x20ffffff, true);

  /** The current color. */
  private Color color;
  /** The available colors. */
  private Color[][] colors;

  /**
   * The constructor.
   */
  public ColorSelector() {
    this(Color.WHITE, getDefaultColors());
  }

  /**
   * The constructor.
   *
   * @param color  The active color.
   * @param colors  The colors to select from.
   */
  public ColorSelector(Color color, Color[][] colors) {
    this.color = color;
    this.colors = colors;

    // set background
    setOpaque(false);

    // listen to clicks and display popup as needed
    addActionListener(new ActionListener() {
      @Override
	public void actionPerformed(ActionEvent arg0) {
        // create and show swatch
        final JPopupMenu popup = new JPopupMenu();
        popup.setOpaque(false);
        ColorSwatch swatch = new ColorSwatch(ColorSelector.this.colors);
        swatch.addColorListener(new ColorSwatch.ColorListener() {
          @Override
		public void handleColor(Color color) {
            // set the new color
            if (color != null) {
              ColorSelector.this.setColor(color);
            }

            // hide the popup
            popup.setVisible(false);
          }
        });
        popup.add(swatch);
        popup.show(ColorSelector.this, getWidth() / 2, getHeight() / 2);
      }
    });
  }

  /**
   * Returns the color.
   *
   * @return  The color.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Sets the color.
   *
   * @param color  The color.
   */
  public void setColor(Color color) {
    this.color = color;
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
   * Returns the base colors.
   *
   * @return  The base colors.
   */
  public static Color[][] getDefaultColors() {
    Color[][] colors = new Color[DEFAULT_COLORS.length][DEFAULT_COLORS[0].length];
    for (int y = 0; y < colors.length; y++) {
      for (int x = 0; x < colors[0].length; x++) {
        colors[y][x] = new Color(DEFAULT_COLORS[y][x]);
      }
    }
    return colors;
  }

  /**
   * Returns the preferred size.
   *
   * @return  The preferred size.
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(60, 30);
  }

  /**
   * Returns the margin.
   *
   * @return  The margin.
   */
  private int margin() {
    return 3;
  }

  /**
   * Draws this component.
   *
   * @param g  Where to draw to.
   */
  @Override
  public void paint(Graphics g) {
    // use antialiasing
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
    int width = getWidth();
    int height = getHeight();
    int x = 0;
    int y = 0;
    int margin = margin();

    // draw border
    getBorder().paintBorder(this, g, 0, 0, width - 1, height - 1);

    // draw the color
    g.setColor(color);
    g.fillRoundRect(x + margin, y + margin
                 , width - (2 * margin), height - (2 * margin), 5, 5);

    // draw effect as need
    ButtonModel model = getModel();
    if (model.isPressed()) {
      g.setColor(ROLLOVER_COLOR);
      g.fillRoundRect(x + margin, y + margin
                 , width - (2 * margin), height - (2 * margin), 5, 5);
      g.fillRoundRect(x + margin, y + margin
                , width - (2 * margin), height - (2 * margin), 5, 5);
    }
    else if (model.isRollover()) {
      g.setColor(ROLLOVER_COLOR);
      g.fillRoundRect(x + margin, y + margin
               , width - (2 * margin), height - (2 * margin), 5, 5);
    }
  }
}
