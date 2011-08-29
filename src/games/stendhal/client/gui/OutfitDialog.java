/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - 2011 Stendhal                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.client.gui;

import games.stendhal.client.OutfitStore;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Outfits;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class OutfitDialog extends JDialog {

	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(OutfitDialog.class);

	private static final long serialVersionUID = 4628210176721975735L;

	private static final int PLAYER_WIDTH = 48;

	private static final int PLAYER_HEIGHT = 64;

	// to keep the sprites to show
	private final Sprite[] hairs;

	private final Sprite[] heads;

	private final Sprite[] bodies;

	private final Sprite[] clothes;

	// current selected parts index
	private int hairsIndex = 1;

	private int headsIndex;

	private int bodiesIndex;

	private int clothesIndex;

	// 0 for direction UP, 1 RIGHT, 2 DOWN and 3 LEFT
	private int direction = 2;

	private final StendhalClient client;

	private final SpriteStore store = SpriteStore.get();

	private final OutfitStore ostore = OutfitStore.get();
	
	private JButton jbtLeftBodies;

	private JButton jbtLeftClothes;

	private JButton jbtLeftHairs;

	private JButton jbtLeftHeads;

	private JButton jbtOK;

	private JButton jbtRightBodies;

	private JButton jbtRightClothes;

	private JButton jbtRightHairs;

	private JButton jbtRightHeads;

	private JLabel jlblBodies;

	private JLabel jlblClothes;

	private JLabel jlblFinalResult;

	private JLabel jlblHairs;

	private JLabel jlblHeads;

	private JSlider jsliderDirection;

	public OutfitDialog(final Frame parent, final String title, final int outfit) {
		this(parent, title, outfit, Outfits.HAIR_OUTFITS, Outfits.HEAD_OUTFITS, Outfits.BODY_OUTFITS,
				Outfits.CLOTHES_OUTFITS);
	}

	/**
	 * Creates new form SetOutfitGameDialog.
	 * @param parent
	 *
	 * @param title
	 *            a String with the title for the dialog
	 * @param outfit
	 *            the current outfit
	 * @param total_hairs
	 *            an integer with the total of sprites with hairs
	 * @param total_heads
	 *            an integer with the total of sprites with heads
	 * @param total_bodies
	 *            an integer with the total of sprites with bodies
	 * @param total_clothes
	 *            an integer with the total of sprites with clothes
	 */
	private OutfitDialog(final Frame parent, final String title, int outfit,
			final int total_hairs, final int total_heads, final int total_bodies,
			final int total_clothes) {
		super(parent, false);
		initComponents();
		applyStyle();
		setTitle(title);

		client = StendhalClient.get();

		// initializes the arrays
		// Plus 1 to add the sprite_empty.png that is always at 0
		hairs = new Sprite[total_hairs];
		heads = new Sprite[total_heads];
		bodies = new Sprite[total_bodies];
		// Plus 1 to add the sprite_empty.png that is always at 0
		clothes = new Sprite[total_clothes];

		// analyse current outfit
		bodiesIndex = outfit % 100;
		outfit = outfit / 100;
		clothesIndex = outfit % 100;
		outfit = outfit / 100;
		headsIndex = outfit % 100;
		outfit = outfit / 100;
		hairsIndex = outfit % 100;

		// reset special outfits
		if (hairsIndex >= hairs.length) {
			hairsIndex = 0;
		}
		if (headsIndex >= heads.length) {
			headsIndex = 0;
		}
		if (bodiesIndex >= bodies.length) {
			bodiesIndex = 0;
		}
		if (clothesIndex >= clothes.length) {
			clothesIndex = 0;
		}
		updateHairImage();
		updateHeadImage();
		updateBodyImage();
		updateDressImage();
		updateWholeOutfit();
		WindowUtils.closeOnEscape(this);
	}

	private void initComponents() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);

		JComponent content = (JComponent) getContentPane();
		content.setLayout(null);

		jbtOK = new JButton("OK");
		jbtOK.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent evt) {
				jbtOKActionPerformed(evt);
			}
		});

		content.add(jbtOK);
		jbtOK.setBounds(190, 220, 80, 30);

		
		jbtLeftHairs = new JButton("<");
		jbtLeftHairs.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent evt) {
				jbtLeftHairsActionPerformed(evt);
			}
		});

		content.add(jbtLeftHairs);
		jbtLeftHairs.setBounds(10, 20, 45, 30);

		jbtRightHairs = new JButton(">");
		jbtRightHairs.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent evt) {
				jbtRightHairsActionPerformed(evt);
			}
		});

		content.add(jbtRightHairs);
		jbtRightHairs.setBounds(120, 20, 45, 30);

		jbtLeftHeads = new JButton("<");
		jbtLeftHeads.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent evt) {
				jbtLeftHeadsActionPerformed(evt);
			}
		});

		content.add(jbtLeftHeads);
		jbtLeftHeads.setBounds(10, 100, 45, 30);

		jbtRightHeads = new JButton(">");
		jbtRightHeads.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent evt) {
				jbtRightHeadsActionPerformed(evt);
			}
		});

		content.add(jbtRightHeads);
		jbtRightHeads.setBounds(120, 100, 45, 30);

		jbtLeftBodies = new JButton("<");
		jbtLeftBodies.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent evt) {
				jbtLeftBodiesActionPerformed(evt);
			}
		});

		content.add(jbtLeftBodies);
		jbtLeftBodies.setBounds(10, 180, 45, 30);

		jbtRightBodies = new JButton(">");
		jbtRightBodies.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent evt) {
				jbtRightBodiesActionPerformed(evt);
			}
		});

		content.add(jbtRightBodies);
		jbtRightBodies.setBounds(120, 180, 45, 30);

		jbtLeftClothes = new JButton("<");
		jbtLeftClothes.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent evt) {
				jbtLeftClothesActionPerformed(evt);
			}
		});

		content.add(jbtLeftClothes);
		jbtLeftClothes.setBounds(10, 260, 45, 30);

		jbtRightClothes = new JButton(">");
		jbtRightClothes.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent evt) {
				jbtRightClothesActionPerformed(evt);
			}
		});

		content.add(jbtRightClothes);
		jbtRightClothes.setBounds(120, 260, 45, 30);

		jlblHairs = new JLabel();
		jlblHairs.setFont(new Font("Dialog", 0, 10));
		jlblHairs.setHorizontalAlignment(SwingConstants.CENTER);
		jlblHairs.setOpaque(true);
		content.add(jlblHairs);
		jlblHairs.setBounds(60, 10, 52, 68);

		jlblHeads = new JLabel();
		jlblHeads.setFont(new Font("Dialog", 0, 10));
		jlblHeads.setHorizontalAlignment(SwingConstants.CENTER);
		jlblHeads.setOpaque(true);
		content.add(jlblHeads);
		jlblHeads.setBounds(60, 90, 52, 68);

		jlblBodies = new JLabel();
		jlblBodies.setFont(new Font("Dialog", 0, 10));
		jlblBodies.setHorizontalAlignment(SwingConstants.CENTER);
		jlblBodies.setOpaque(true);
		content.add(jlblBodies);
		jlblBodies.setBounds(60, 170, 52, 68);

		jlblClothes = new JLabel();
		jlblClothes.setFont(new Font("Dialog", 0, 10));
		jlblClothes.setHorizontalAlignment(SwingConstants.CENTER);
		jlblClothes.setOpaque(true);
		content.add(jlblClothes);
		jlblClothes.setBounds(60, 250, 52, 68);

		jlblFinalResult = new JLabel();
		jlblFinalResult.setFont(new Font("Dialog", 0, 10));
		jlblFinalResult.setHorizontalAlignment(SwingConstants.CENTER);
		jlblFinalResult.setOpaque(true);
		content.add(jlblFinalResult);
		jlblFinalResult.setBounds(205, 90, 52, 68);

		jsliderDirection = new JSlider();
		jsliderDirection.setMaximum(3);
		jsliderDirection.setSnapToTicks(true);
		jsliderDirection.setValue(2);
		jsliderDirection.setInverted(true);
		jsliderDirection.addChangeListener(new ChangeListener() {

			public void stateChanged(final ChangeEvent evt) {
				jsliderDirectionStateChanged(evt);
			}
		});

		content.add(jsliderDirection);
		jsliderDirection.setBounds(190, 170, 80, 27);

		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 288) / 2, (screenSize.height - 361) / 2,
				288, 361);
	}



	/** this is called every time the user moves the slider.
	 * @param evt */
	private void jsliderDirectionStateChanged(final ChangeEvent evt) {
		direction = jsliderDirection.getValue();

		updateWholeOutfit();
		updateHairImage();
		updateHeadImage();
		updateBodyImage();
		updateDressImage();
	}
	
	/**
	 * Get the hair sprite.
	 * 
	 * @return hair sprite
	 */
	private Sprite getHairSprite() {
		return store.getTile(ostore.getHairSprite(hairsIndex), PLAYER_WIDTH,
				direction * PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
	}
	
	/**
	 * Get the head sprite.
	 * 
	 * @return head sprite
	 */
	private Sprite getHeadSprite() {
		return store.getTile(ostore.getHeadSprite(headsIndex), PLAYER_WIDTH,
				direction * PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
	}
	
	/**
	 * Get the body sprite.
	 * 
	 * @return body sprite
	 */
	private Sprite getBodySprite() {
		return store.getTile(ostore.getBaseSprite(bodiesIndex), PLAYER_WIDTH,
				direction * PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
	}
	
	/**
	 * Get the dress sprite.
	 * 
	 * @return dress sprite
	 */
	private Sprite getDressSprite() {
		return store.getTile(ostore.getDressSprite(clothesIndex), PLAYER_WIDTH,
				direction * PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
	}
	
	/**
	 * Update the hair image.
	 */
	private void updateHairImage() {
		updateLabel(jlblHairs, getHairSprite());
	}
	
	/**
	 * Update the head image.
	 */
	private void updateHeadImage() {
		updateLabel(jlblHeads, getHeadSprite());
	}
	
	/**
	 * Update the base image.
	 */
	private void updateBodyImage() {
		updateLabel(jlblBodies, getBodySprite());
	}
	
	/**
	 * Update the base image.
	 */
	private void updateDressImage() {
		updateLabel(jlblClothes, getDressSprite());
	}
	

	/**
	 * Updates the final outfit image.
	 */
	private void updateWholeOutfit() {
		updateLabel(jlblFinalResult, getBodySprite(), getDressSprite(),
				getHeadSprite(), getHairSprite());
	}
	
	/**
	 * Update the image of a label using a set of sprites.
	 * 
	 * @param label label to be updated
	 * @param sprites
	 */
	private void updateLabel(JLabel label, Sprite ... sprites) {
		BufferedImage img = label.getGraphicsConfiguration().createCompatibleImage(PLAYER_WIDTH, PLAYER_HEIGHT);
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, PLAYER_WIDTH, PLAYER_HEIGHT);
		for (Sprite sprite : sprites) {
			sprite.draw(g, 0, 0);
		}
		g.dispose();
		ImageIcon icon = new ImageIcon(img);
		label.setIcon(icon);
	}

	/** Clothes Right button.
	 * @param evt */
	private void jbtRightClothesActionPerformed(final ActionEvent evt) {
		if (clothesIndex < clothes.length - 1) {
			clothesIndex++;
		} else {
			clothesIndex = 0;
		}

		updateDressImage();
		updateWholeOutfit();
	}

	/** Clothes Left button.
	 * @param evt */
	private void jbtLeftClothesActionPerformed(final ActionEvent evt) {
		if (clothesIndex > 0) {
			clothesIndex--;
		} else {
			clothesIndex = clothes.length - 1;
		}

		updateDressImage();
		updateWholeOutfit();
	}

	/** Bodies Right button.
	 * @param evt */
	private void jbtRightBodiesActionPerformed(final ActionEvent evt) {
		if (bodiesIndex < bodies.length - 1) {
			bodiesIndex++;
		} else {
			bodiesIndex = 0;
		}

		updateBodyImage();
		updateWholeOutfit();
	}

	/** Bodies Left button.
	 * @param evt */
	private void jbtLeftBodiesActionPerformed(final ActionEvent evt) { 
		if (bodiesIndex > 0) {
			bodiesIndex--;
		} else {
			bodiesIndex = bodies.length - 1;
		}

		updateBodyImage();
		updateWholeOutfit();
	}

	/** Heads Right button.
	 * @param evt */
	private void jbtRightHeadsActionPerformed(final ActionEvent evt) { 
		if (headsIndex < heads.length - 1) {
			headsIndex++;
		} else {
			headsIndex = 0;
		}

		updateHeadImage();
		updateWholeOutfit();
	}

	/** Heads Left button.
	 * @param evt */
	private void jbtLeftHeadsActionPerformed(final ActionEvent evt) { 
		if (headsIndex > 0) {
			headsIndex--;
		} else {
			headsIndex = heads.length - 1;
		}

		updateHeadImage();
		updateWholeOutfit();
	} 

	/** Hairs Right button.
	 * @param evt */
	private void jbtRightHairsActionPerformed(final ActionEvent evt) { 
		if (hairsIndex < hairs.length - 1) {
			hairsIndex++;
		} else {
			hairsIndex = 0;
		}

		updateHairImage();
		updateWholeOutfit();
	} 

	/** Hairs Left button.
	 * @param evt */
	private void jbtLeftHairsActionPerformed(final ActionEvent evt) { 
		if (hairsIndex > 0) {
			hairsIndex--;
		} else {
			hairsIndex = hairs.length - 1;
		}

		updateHairImage();
		updateWholeOutfit();
	}

	/** Button OK action.
	 * @param evt */
	private void jbtOKActionPerformed(final ActionEvent evt) { 
		sendAction();

		this.dispose();
	} 

	private void sendAction() {
		if (client == null) {
			/** If running standalone, just print the outfit */
			System.out.println("OUTFIT is: "
					+ (bodiesIndex + clothesIndex * 100 + headsIndex * 100
							* 100 + hairsIndex * 100 * 100 * 100));
			return;
		}

		final RPAction rpaction = new RPAction();
		rpaction.put("type", "outfit");
		rpaction.put("value", bodiesIndex + clothesIndex * 100 + headsIndex
				* 100 * 100 + hairsIndex * 100 * 100 * 100);
		client.send(rpaction);
	}

	private void generateAllOutfits(final String baseDir) {
		/** TEST METHOD: DON'T NO USE */
		for (bodiesIndex = 0; bodiesIndex < bodies.length; bodiesIndex++) {
			for (clothesIndex = 0; clothesIndex < clothes.length; clothesIndex++) {
				for (headsIndex = 0; headsIndex < heads.length; headsIndex++) {
					for (hairsIndex = 0; hairsIndex < hairs.length; hairsIndex++) {
						final String name = Integer.toString(bodiesIndex
								+ clothesIndex * 100 + headsIndex * 100 * 100
								+ hairsIndex * 100 * 100 * 100);
						final File file = new File(baseDir + "outfits/" + name
								+ ".png");

						// for performance reasons only write new files.
						if (!file.exists()) {
							System.out.println("Creating " + name + ".png");
							final Image image = new BufferedImage(PLAYER_WIDTH,
									PLAYER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
							Graphics g = image.getGraphics();
							getBodySprite().draw(g, 0, 0);
							getDressSprite().draw(g, 0, 0);
							getHeadSprite().draw(g, 0, 0);
							getHairSprite().draw(g, 0, 0);
							g.dispose();
							try {
								ImageIO.write((RenderedImage) image, "png",
										file);
							} catch (final Exception e) {
								LOGGER.error(e, e);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Apply Stendhal style to all components.
	 */
	private void applyStyle() {
		Style style = StyleUtil.getStyle();
		if (style != null) {
			// Labels (Images). Making all JLabels bordered would be undesired
			jlblBodies.setBorder(style.getBorderDown());
			jlblClothes.setBorder(style.getBorderDown());
			jlblFinalResult.setBorder(style.getBorderDown());
			jlblHairs.setBorder(style.getBorderDown());
			jlblHeads.setBorder(style.getBorderDown());
		}
	}

	public static void main(final String[] args) {
		String baseDir = "";
		if (args.length > 0) {
			baseDir = args[0] + "/";
		}

		final OutfitDialog f = new OutfitDialog(null, "Stendhal - Choose outfit", 0);
		// show is required now, because getGraphics() returns null otherwise
		f.setVisible(true);
		f.generateAllOutfits(baseDir);
	}
}
