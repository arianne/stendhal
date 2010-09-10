package games.stendhal.client.gui;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.common.constants.SoundLayer;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * A window like panel component with a title bar and optional close and
 * minimize buttons.
 */
public class InternalWindow extends JPanel {
	private static final int TITLEBAR_HEIGHT = 13;
	/** Space between titlebar components and before the title */
	private static final int TITLEBAR_PADDING = 2;
	
	private static Icon closeIcon, minimizeIcon;

	// Draw the buttons over static background to avoid overhead from constantly
	// painting translucent
	static {
		createIcons();
	}
	
	final TitleBar titleBar;
	final JButton minimizeButton;
	final JButton closeButton;
	/** Title text label */
	final JLabel titleLabel;
	
	private JComponent content;
	private boolean hideOnClose = false;
	
	private String openSound = "click-10";
	private String minimizeSound = "click-8";
	private String closeSound = "click-6";
	
	/**
	 * Create a new InternalWindow.
	 * 
	 * @param title title text
	 */
	public InternalWindow(String title) {
		setLayout(new BorderLayout());
		
		titleBar = new TitleBar();
		add(titleBar, BorderLayout.NORTH);
		titleBar.setLayout(new SBoxLayout(SBoxLayout.HORIZONTAL, TITLEBAR_PADDING));
		titleLabel = new JLabel(title);
		// Squeeze the label if there's not enough space
		titleLabel.setMinimumSize(new Dimension(0, 0));
		titleBar.add(titleLabel);
		SBoxLayout.addSpring(titleBar);
		
		createIcons();
		
		// Add the close and minimize buttons
		minimizeButton = new JButton(minimizeIcon);
		minimizeButton.setMargin(new Insets(0, 0, 0, 0));
		minimizeButton.setBorder(BorderFactory.createEmptyBorder());
		minimizeButton.setFocusable(false);
		minimizeButton.addActionListener(new MinimizeListener());
		titleBar.add(minimizeButton);
		
		closeButton = new JButton(closeIcon);
		closeButton.setMargin(new Insets(0, 0, 0, 0));
		closeButton.setBorder(BorderFactory.createEmptyBorder());
		closeButton.setFocusable(false);
		closeButton.addActionListener(new CloseListener());
		titleBar.add(closeButton);
	}
	
	/**
	 * Set the content of the window.
	 * 
	 * @param content
	 */
	public void setContent(JComponent content) {
		if (this.content != null) {
			remove(this.content);
		}
		add(content, BorderLayout.CENTER);
		this.content = content;
		/*
		 * Keep the preferred width even if the content is minimized
		 */
		int width = content.getPreferredSize().width;
		Dimension preferred = titleBar.getPreferredSize();
		preferred.width = width;
		titleBar.setPreferredSize(preferred);
	}
	
	/**
	 * Make the window closeable by the user by showing or hiding the close
	 * button.
	 * 
	 * @param closeable
	 */
	public void setCloseable(boolean closeable) {
		closeButton.setVisible(closeable);
	}
	
	/**
	 * Set closing policy. Normally windows delete themselves on close, but
	 * setting the policy to hiding just makes it invisible.
	 * 
	 * @param hide if <code>true</code>, the window will hide itself when the
	 * 	user closes it, otherwise the window is deleted on close
	 */
	public void setHideOnClose(boolean hide) {
		hideOnClose = hide;
	}
	
	/**
	 * Make the window minimizable by the user by showing or hiding the
	 * minimize button.
	 * 
	 * @param minimizable
	 */
	public void setMinimizable(boolean minimizable) {
		minimizeButton.setVisible(minimizable);
	}
	
	/**
	 * Get the minimization status of the window.
	 * 
	 * @return <code>true</code> if the window is minimized, <code>false</code>
	 * otherwise
	 */
	public boolean isMinimized() {
		return !content.isVisible();
	}
	
	/**
	 * Set the minimization status of the window.
	 * 
	 * @param minimized
	 */
	public void setMinimized(boolean minimized) {
		// Cosmetics. Hide the borders of the title bar while the window is
		// minimized
		if (minimized) {
			titleBar.hideBorder();
		} else {
			titleBar.restoreBorder();
		}
		content.setVisible(!minimized);
	}
	
	/**
	 * Get the title bar component.
	 * @return title bar
	 */
	protected JComponent getTitlebar() {
		return titleBar;
	}
	
	/**
	 * Set the window title.
	 * 
	 * @param title
	 */
	protected void setTitle(String title) {
		titleLabel.setText(title);
	}
	
	/**
	 * Handle close button.
	 */
	private class CloseListener implements ActionListener {
		public void actionPerformed(final ActionEvent ev) {
			Container parent = InternalWindow.this.getParent();
			if (hideOnClose) {
				setVisible(false);
			} else {
				parent.remove(InternalWindow.this);
				parent.validate();
			}
			playSound(closeSound);
		}
	}

	/**
	 * Handle minimization button.
	 */
	private class MinimizeListener implements ActionListener {
		public void actionPerformed(final ActionEvent ev) {
			setMinimized(!isMinimized());
			if (isMinimized()) {
				playSound(openSound);
			} else {
				playSound(minimizeSound);
			}
		}
	}
	
	/**
	 * Play a user interface sound.
	 * 
	 * @param sound the sound to be played
	 */
	private void playSound(String sound) {
		ClientSingletonRepository.getSound().getGroup(SoundLayer.USER_INTERFACE.groupName).play(sound, 0, null, null, false, true);
	}
	
	/**
	 * Create the close and minimize icons
	 */
	private static void createIcons() {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image bg = createIconBackground(gc);
		
		// copy bg for drawing
		Image image = gc.createCompatibleImage(TITLEBAR_HEIGHT, TITLEBAR_HEIGHT, Transparency.OPAQUE);
		Graphics g = image.getGraphics();
		g.drawImage(bg, 0, 0, null);
		closeIcon = createCloseIcon(image);
		
		// now we can draw over the background image
		minimizeIcon = createMinimizeIcon(bg);
	}
	
	/**
	 * Create image background for the title bar buttons. Tries using the
	 * style of the theme if available.
	 * 
	 * @param gc
	 * @return image background image
	 */
	private static Image createIconBackground(GraphicsConfiguration gc) {
		BufferedImage image = gc.createCompatibleImage(TITLEBAR_HEIGHT, TITLEBAR_HEIGHT, Transparency.OPAQUE);
		Graphics2D g = image.createGraphics();
		/*
		 * Use proper style if defined. If someone's using a different theme we
		 * don't have a nice background, but the client should not crash anyway.
		 * Those get just a white background.
		 */
		Style style = StyleUtil.getStyle();
		if (style != null) {
			style.getBackground().draw(g, 0, 0);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		}
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, TITLEBAR_HEIGHT, TITLEBAR_HEIGHT);
		g.dispose();
		
		return image;
	}
	
	/**
	 * Draw the close button icon.
	 * 
	 * @param image background image. The button image is drawn over this,
	 * 	without making a local copy first
	 * @return icon for close button
	 */
	private static Icon createCloseIcon(Image image) {
		Graphics g = image.getGraphics();

		g.setColor(Color.BLACK);
		/* \\\ */
		g.drawLine(1, 2, TITLEBAR_HEIGHT - 3, TITLEBAR_HEIGHT - 2);
		g.drawLine(1, 1, TITLEBAR_HEIGHT - 2, TITLEBAR_HEIGHT - 2);
		g.drawLine(2, 1, TITLEBAR_HEIGHT - 2, TITLEBAR_HEIGHT - 3);
		
		/* /// */
		g.drawLine(TITLEBAR_HEIGHT - 3, 1, 1, TITLEBAR_HEIGHT - 3);
		g.drawLine(TITLEBAR_HEIGHT - 2, 1, 1, TITLEBAR_HEIGHT - 2);
		g.drawLine(TITLEBAR_HEIGHT - 2, 2, 2, TITLEBAR_HEIGHT - 2);

		g.dispose();
		return new ImageIcon(image);
	}
	
	/**
	 * Draw the minimize button icon.
	 * 
	 * @param image background image. The button image is drawn over this,
	 * 	without making a local copy first
	 * @return icon for minimize button
	 */
	private static Icon createMinimizeIcon(Image image) {
		Graphics g = image.getGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(1, TITLEBAR_HEIGHT - 3, TITLEBAR_HEIGHT - 2, 2);
		
		return new ImageIcon(image);
	}
	
	/**
	 * A JPanel that draws only the lower part of the border
	 */
	private static class TitleBar extends JPanel {
		/** Original, unmodified insets */
		private Insets insets;
		private final Border border;
		
		public TitleBar() {
			/*
			 * Compensate with negative empty border the borders that are not
			 * drawn anyway. Left and right borders are useful as padding so 
			 * they are kept.
			 */
			insets = getInsets();
			border = BorderFactory.createCompoundBorder(getBorder(), 
					BorderFactory.createEmptyBorder(-insets.top, 0, 0, 0));
			setBorder(border);
		}
		
		/**
		 * Hide the special border of the title bar.
		 */
		public void hideBorder() {
			/*
			 * Create an empty border that corresponds exactly to the normal
			 * borders.
			 */
			Insets insets = getInsets();
			setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
		}
		
		/**
		 * Restore the special border of the title bar.
		 */
		public void restoreBorder() {
			setBorder(border);
		}
		
		@Override
		public void paintBorder(Graphics g) {
			Graphics graphics = g.create();
			graphics.clipRect(0, getHeight() - insets.bottom, getWidth(), 100);
			/*
			 * Adjust the width, so that the drawn border does not get corners
			 * that may look ugly
			 */
			getBorder().paintBorder(this, graphics, -insets.left, 0, 
					getWidth() + insets.left + insets.right, getHeight());
			graphics.dispose();
		}
	}
}
