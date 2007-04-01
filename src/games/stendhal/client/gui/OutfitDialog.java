/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * SetOutfitGameDialog.java
 *
 * Created on 2 de Junho de 2005, 10:58
 */

package games.stendhal.client.gui;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalClient;

import java.awt.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import marauroa.common.game.RPAction;

public class OutfitDialog extends JDialog {

	/** the logger instance. */
	//  private static final Logger logger = Log4J.getLogger(OutfitDialog.class);
	private static final long serialVersionUID = 4628210176721975735L;

	// to keep the sprites to show
	private Sprite[] hairs = null;

	private Sprite[] heads = null;

	private Sprite[] bodies = null;

	private Sprite[] clothes = null;

	// current selected parts index
	private int hairs_index = 0;

	private int heads_index = 0;

	private int bodies_index = 0;

	private int clothes_index = 0;

	// to handle the draws update
	private Timer timer = null;

	// 0 for animation UP, 1 RIGHT, 2 DOWN and 3 LEFT
	private int animation = 2;

	private StendhalClient client;

	/** Creates new form SetOutfitGameDialog
	 *
	 * @param title a String with the title for the dialog
	 * @param outfit the current outfit
	 * @param total_hairs an integer with the total of sprites with hairs
	 * @param total_heads an integer with the total of sprites with heads
	 * @param total_bodies an integer with the total of sprites with bodies
	 * @param total_clothes an integer with the total of sprites with clothes
	 */
	public OutfitDialog(Frame parent, String title, int outfit, int total_hairs, int total_heads,
	        int total_bodies, int total_clothes) {
		super(parent, false);
		initComponents();
		setTitle(title);

		client = StendhalClient.get();

		// initializes the arrays
		hairs = new Sprite[total_hairs]; // Plus 1 to add the sprite_empty.png that is always at 0
		heads = new Sprite[total_heads];
		bodies = new Sprite[total_bodies];
		clothes = new Sprite[total_clothes]; // Plus 1 to add the sprite_empty.png that is always at 0
		// loads the sprites
		loadSprites();

		// updates the draws every 2500 milliseconds
		timer = new Timer();
		timer.schedule(new AnimationTask(), 1000, 2500);

		// analyse current outfit
		bodies_index = outfit % 100;
		outfit = outfit / 100;
		clothes_index = outfit % 100;
		outfit = outfit / 100;
		heads_index = outfit % 100;
		outfit = outfit / 100;
		hairs_index = outfit % 100;

		// reset special outfits
		if (hairs_index >= hairs.length) {
			hairs_index = 0;
		}
		if (heads_index >= heads.length) {
			heads_index = 0;
		}
		if (bodies_index >= bodies.length) {
			bodies_index = 0;
		}
		if (clothes_index >= clothes.length) {
			clothes_index = 0;
		}

	}

	/**
	 * @return a String with the name of the selected hair sprite file
	 */
	public String getSelectedHair() {
		return "hair_" + hairs_index + ".png";
	}

	/**
	 * @return a String with the name of the selected head sprite file
	 */
	public String getSelectedHead() {
		return "head_" + heads_index + ".png";
	}

	/**
	 * @return a String with the name of the selected body sprite file
	 */
	public String getSelectedBody() {
		return "player_base_" + bodies_index + ".png";
	}

	/**
	 * @return a String with the name of the selected clothes sprite file
	 */
	public String getSelectedClothes() {
		String filename = "dress_" + clothes_index + ".png";
		return filename;
	}

	/**
	 * Loads the sprites into the arrays
	 */
	private void loadSprites() {
		SpriteStore st = SpriteStore.get();

		// load the sprites
		for (int i = 1; i < hairs.length; i++) {
			hairs[i] = st.getSprite("data/sprites/outfit/hair_" + i + ".png");
		}
		for (int i = 0; i < heads.length; i++) {
			heads[i] = st.getSprite("data/sprites/outfit/head_" + i + ".png");
		}
		for (int i = 0; i < bodies.length; i++) {
			bodies[i] = st.getSprite("data/sprites/outfit/player_base_" + i + ".png");
		}
		for (int i = 1; i < clothes.length; i++) {
			clothes[i] = st.getSprite("data/sprites/outfit/dress_" + i + ".png");
		}

		// to allow choosing no hair and no clothes
		hairs[0] = st.getSprite("data/sprites/outfit/sprite_empty.png");
		clothes[0] = st.getSprite("data/sprites/outfit/sprite_empty.png");
	}

	/**
	 * Cleans the previous draw
	 *
	 * @param g the Graphics where to clean
	 */
	private void clean(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(2, 2, 48, 64);
	}

	/**
	 * draws selected single part
	 */
	private void drawSinglePart(Sprite sprite, Graphics g) {
		clean(g);
		SpriteStore.get().getAnimatedSprite(sprite, animation, 3, 1.5, 2)[1].draw(g, 2, 2);
	}

	/**
	 * draws final player
	 */
	private void drawFinalPlayer(Graphics g) {
		clean(g);
		SpriteStore.get().getAnimatedSprite(bodies[bodies_index], animation, 3, 1.5, 2)[1].draw(g, 2, 2);
		SpriteStore.get().getAnimatedSprite(clothes[clothes_index], animation, 3, 1.5, 2)[1].draw(g, 2, 2);
		SpriteStore.get().getAnimatedSprite(heads[heads_index], animation, 3, 1.5, 2)[1].draw(g, 2, 2);
		SpriteStore.get().getAnimatedSprite(hairs[hairs_index], animation, 3, 1.5, 2)[1].draw(g, 2, 2);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		jpanel = new JPanel();
		jbtOK = new JButton();
		jbtLeftHairs = new JButton();
		jbtRightHairs = new JButton();
		jbtLeftHeads = new JButton();
		jbtRightHeads = new JButton();
		jbtLeftBodies = new JButton();
		jbtRightBodies = new JButton();
		jbtLeftClothes = new JButton();
		jbtRightClothes = new JButton();
		jlblHairs = new JLabel();
		jlblHeads = new JLabel();
		jlblBodies = new JLabel();
		jlblClothes = new JLabel();
		jlblFinalResult = new JLabel();
		jsliderAnimation = new JSlider();

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setBackground(new Color(200, 200, 200));
		setResizable(false);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		jpanel.setLayout(null);

		jpanel.setBorder(new LineBorder(new Color(100, 100, 100), 2, true));
		jbtOK.setText("OK");
		jbtOK.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jbtOKActionPerformed(evt);
			}
		});

		jpanel.add(jbtOK);
		jbtOK.setBounds(190, 220, 80, 30);

		jbtLeftHairs.setFont(new Font("Dialog", 1, 14));
		jbtLeftHairs.setText("<");
		jbtLeftHairs.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jbtLeftHairsActionPerformed(evt);
			}
		});

		jpanel.add(jbtLeftHairs);
		jbtLeftHairs.setBounds(10, 20, 45, 30);

		jbtRightHairs.setFont(new Font("Dialog", 1, 14));
		jbtRightHairs.setText(">");
		jbtRightHairs.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jbtRightHairsActionPerformed(evt);
			}
		});

		jpanel.add(jbtRightHairs);
		jbtRightHairs.setBounds(120, 20, 45, 30);

		jbtLeftHeads.setFont(new Font("Dialog", 1, 14));
		jbtLeftHeads.setText("<");
		jbtLeftHeads.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jbtLeftHeadsActionPerformed(evt);
			}
		});

		jpanel.add(jbtLeftHeads);
		jbtLeftHeads.setBounds(10, 100, 45, 30);

		jbtRightHeads.setFont(new Font("Dialog", 1, 14));
		jbtRightHeads.setText(">");
		jbtRightHeads.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jbtRightHeadsActionPerformed(evt);
			}
		});

		jpanel.add(jbtRightHeads);
		jbtRightHeads.setBounds(120, 100, 45, 30);

		jbtLeftBodies.setFont(new Font("Dialog", 1, 14));
		jbtLeftBodies.setText("<");
		jbtLeftBodies.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jbtLeftBodiesActionPerformed(evt);
			}
		});

		jpanel.add(jbtLeftBodies);
		jbtLeftBodies.setBounds(10, 180, 45, 30);

		jbtRightBodies.setFont(new Font("Dialog", 1, 14));
		jbtRightBodies.setText(">");
		jbtRightBodies.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jbtRightBodiesActionPerformed(evt);
			}
		});

		jpanel.add(jbtRightBodies);
		jbtRightBodies.setBounds(120, 180, 45, 30);

		jbtLeftClothes.setFont(new Font("Dialog", 1, 14));
		jbtLeftClothes.setText("<");
		jbtLeftClothes.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jbtLeftClothesActionPerformed(evt);
			}
		});

		jpanel.add(jbtLeftClothes);
		jbtLeftClothes.setBounds(10, 260, 45, 30);

		jbtRightClothes.setFont(new Font("Dialog", 1, 14));
		jbtRightClothes.setText(">");
		jbtRightClothes.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jbtRightClothesActionPerformed(evt);
			}
		});

		jpanel.add(jbtRightClothes);
		jbtRightClothes.setBounds(120, 260, 45, 30);

		jlblHairs.setBackground(new Color(255, 255, 255));
		jlblHairs.setFont(new Font("Dialog", 0, 10));
		jlblHairs.setHorizontalAlignment(SwingConstants.CENTER);
		jlblHairs.setText("loading...");
		jlblHairs.setBorder(new LineBorder(new Color(100, 100, 100), 1, true));
		jlblHairs.setOpaque(true);
		jpanel.add(jlblHairs);
		jlblHairs.setBounds(60, 10, 52, 68);

		jlblHeads.setBackground(new Color(255, 255, 255));
		jlblHeads.setFont(new Font("Dialog", 0, 10));
		jlblHeads.setHorizontalAlignment(SwingConstants.CENTER);
		jlblHeads.setText("loading...");
		jlblHeads.setBorder(new LineBorder(new Color(100, 100, 100), 1, true));
		jlblHeads.setOpaque(true);
		jpanel.add(jlblHeads);
		jlblHeads.setBounds(60, 90, 52, 68);

		jlblBodies.setBackground(new Color(255, 255, 255));
		jlblBodies.setFont(new Font("Dialog", 0, 10));
		jlblBodies.setHorizontalAlignment(SwingConstants.CENTER);
		jlblBodies.setText("loading...");
		jlblBodies.setBorder(new LineBorder(new Color(100, 100, 100), 1, true));
		jlblBodies.setOpaque(true);
		jpanel.add(jlblBodies);
		jlblBodies.setBounds(60, 170, 52, 68);

		jlblClothes.setBackground(new Color(255, 255, 255));
		jlblClothes.setFont(new Font("Dialog", 0, 10));
		jlblClothes.setHorizontalAlignment(SwingConstants.CENTER);
		jlblClothes.setText("loading...");
		jlblClothes.setBorder(new LineBorder(new Color(100, 100, 100), 1, true));
		jlblClothes.setOpaque(true);
		jpanel.add(jlblClothes);
		jlblClothes.setBounds(60, 250, 52, 68);

		jlblFinalResult.setBackground(new Color(255, 255, 255));
		jlblFinalResult.setFont(new Font("Dialog", 0, 10));
		jlblFinalResult.setHorizontalAlignment(SwingConstants.CENTER);
		jlblFinalResult.setText("loading...");
		jlblFinalResult.setBorder(new LineBorder(new Color(100, 100, 100), 1, true));
		jlblFinalResult.setOpaque(true);
		jpanel.add(jlblFinalResult);
		jlblFinalResult.setBounds(205, 90, 52, 68);

		jsliderAnimation.setMaximum(3);
		jsliderAnimation.setSnapToTicks(true);
		jsliderAnimation.setValue(2);
		jsliderAnimation.setInverted(true);
		jsliderAnimation.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent evt) {
				jsliderAnimationStateChanged(evt);
			}
		});

		jpanel.add(jsliderAnimation);
		jsliderAnimation.setBounds(190, 170, 80, 27);

		getContentPane().add(jpanel, BorderLayout.CENTER);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - 288) / 2, (screenSize.height - 361) / 2, 288, 361);
	}

	// </editor-fold>//GEN-END:initComponents

	/** this is called everytime the user moves the slider */
	private void jsliderAnimationStateChanged(ChangeEvent evt) {//GEN-FIRST:event_jsliderAnimationStateChanged
		animation = jsliderAnimation.getValue();
		drawFinalPlayer(jlblFinalResult.getGraphics());
		drawSinglePart(hairs[hairs_index], jlblHairs.getGraphics());
		drawSinglePart(heads[heads_index], jlblHeads.getGraphics());
		drawSinglePart(bodies[bodies_index], jlblBodies.getGraphics());
		drawSinglePart(clothes[clothes_index], jlblClothes.getGraphics());
	}//GEN-LAST:event_jsliderAnimationStateChanged

	/** when user closes this window */
	private void formWindowClosing(WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		timer.cancel();
		this.dispose();
	}//GEN-LAST:event_formWindowClosing

	/** Clothes Right button */
	private void jbtRightClothesActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jbtRightClothesActionPerformed
		if (clothes_index < clothes.length - 1) {
			clothes_index++;
		} else {
			clothes_index = 0;
		}
		drawSinglePart(clothes[clothes_index], jlblClothes.getGraphics());
		drawFinalPlayer(jlblFinalResult.getGraphics());
	}//GEN-LAST:event_jbtRightClothesActionPerformed

	/** Clothes Left button */
	private void jbtLeftClothesActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jbtLeftClothesActionPerformed
		if (clothes_index > 0) {
			clothes_index--;
		} else {
			clothes_index = clothes.length - 1;
		}
		drawSinglePart(clothes[clothes_index], jlblClothes.getGraphics());
		drawFinalPlayer(jlblFinalResult.getGraphics());
	}//GEN-LAST:event_jbtLeftClothesActionPerformed

	/** Bodies Right button */
	private void jbtRightBodiesActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jbtRightBodiesActionPerformed
		if (bodies_index < bodies.length - 1) {
			bodies_index++;
		} else {
			bodies_index = 0;
		}
		drawSinglePart(bodies[bodies_index], jlblBodies.getGraphics());
		drawFinalPlayer(jlblFinalResult.getGraphics());
	}//GEN-LAST:event_jbtRightBodiesActionPerformed

	/** Bodies Left button */
	private void jbtLeftBodiesActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jbtLeftBodiesActionPerformed
		if (bodies_index > 0) {
			bodies_index--;
		} else {
			bodies_index = bodies.length - 1;
		}
		drawSinglePart(bodies[bodies_index], jlblBodies.getGraphics());
		drawFinalPlayer(jlblFinalResult.getGraphics());
	}//GEN-LAST:event_jbtLeftBodiesActionPerformed

	/** Heads Right button */
	private void jbtRightHeadsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jbtRightHeadsActionPerformed
		if (heads_index < heads.length - 1) {
			heads_index++;
		} else {
			heads_index = 0;
		}
		drawSinglePart(heads[heads_index], jlblHeads.getGraphics());
		drawFinalPlayer(jlblFinalResult.getGraphics());
	}//GEN-LAST:event_jbtRightHeadsActionPerformed

	/** Heads Left button */
	private void jbtLeftHeadsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jbtLeftHeadsActionPerformed
		if (heads_index > 0) {
			heads_index--;
		} else {
			heads_index = heads.length - 1;
		}
		drawSinglePart(heads[heads_index], jlblHeads.getGraphics());
		drawFinalPlayer(jlblFinalResult.getGraphics());
	}//GEN-LAST:event_jbtLeftHeadsActionPerformed

	/** Hairs Right button */
	private void jbtRightHairsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jbtRightHairsActionPerformed
		if (hairs_index < hairs.length - 1) {
			hairs_index++;
		} else {
			hairs_index = 0;
		}
		drawSinglePart(hairs[hairs_index], jlblHairs.getGraphics());
		drawFinalPlayer(jlblFinalResult.getGraphics());
	}//GEN-LAST:event_jbtRightHairsActionPerformed

	/** Hairs Left button */
	private void jbtLeftHairsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jbtLeftHairsActionPerformed
		if (hairs_index > 0) {
			hairs_index--;
		} else {
			hairs_index = hairs.length - 1;
		}
		drawSinglePart(hairs[hairs_index], jlblHairs.getGraphics());
		drawFinalPlayer(jlblFinalResult.getGraphics());
	}//GEN-LAST:event_jbtLeftHairsActionPerformed

	/** Button OK action */
	private void jbtOKActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jbtOKActionPerformed
		sendAction();

		timer.cancel();
		this.dispose();
	}//GEN-LAST:event_jbtOKActionPerformed

	private void sendAction() {
		if (client == null) {
			/** If running standalone, just print the outfit */
			System.out.println("OUTFIT is: "
			        + (bodies_index + clothes_index * 100 + heads_index * 100 * 100 + hairs_index * 100 * 100 * 100));
			return;
		}

		RPAction rpaction = new RPAction();
		rpaction.put("type", "outfit");
		rpaction.put("value", bodies_index + clothes_index * 100 + heads_index * 100 * 100 + hairs_index * 100 * 100
		        * 100);
		client.send(rpaction);
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
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

	private JPanel jpanel;

	private JSlider jsliderAnimation;

	// End of variables declaration//GEN-END:variables

	/**
	 * TODO: maybe this could be improved, to reduce the flicker
	 *
	 * Private class that handles the update (repaint) of jLabels
	 */
	private class AnimationTask extends TimerTask {

		@Override
		public void run() {
			// draws single parts
			drawSinglePart(hairs[hairs_index], jlblHairs.getGraphics());
			drawSinglePart(heads[heads_index], jlblHeads.getGraphics());
			drawSinglePart(bodies[bodies_index], jlblBodies.getGraphics());
			drawSinglePart(clothes[clothes_index], jlblClothes.getGraphics());
			drawFinalPlayer(jlblFinalResult.getGraphics());
		}

	}

	static class HeadFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name.startsWith("head")) {
				return true;
			}

			return false;
		}
	}

	static class HairFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name.startsWith("hair")) {
				return true;
			}

			return false;
		}
	}

	static class BodyFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name.startsWith("player_base")) {
				return true;
			}

			return false;
		}
	}

	static class ClothesFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name.startsWith("dress")) {
				return true;
			}

			return false;
		}
	}

	private void generateAllOutfits() {
		/** TEST METHOD: DON'T NO USE */
		for (bodies_index = 0; bodies_index < bodies.length; bodies_index++) {
			for (clothes_index = 0; clothes_index < clothes.length; clothes_index++) {
				for (heads_index = 0; heads_index < heads.length; heads_index++) {
					for (hairs_index = 0; hairs_index < hairs.length; hairs_index++) {
						String name = Integer.toString(bodies_index + clothes_index * 100 + heads_index * 100 * 100
						        + hairs_index * 100 * 100 * 100);
						System.out.println("Creating " + name + ".png");
						Image image = new BufferedImage(48, 64,
						        BufferedImage.TYPE_INT_ARGB);
						drawFinalPlayer(getGraphics());
						try {
							ImageIO.write((RenderedImage) image, "png", new File(
							        "outfits\\" + name + ".png"));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	//  public OutfitDialog(Frame parent, String title, int outfit, int total_hairs, int total_heads, int total_bodies, int total_clothes) {
	public static void main(String args[]) {
		//TODO: the outfit is hardcoded because I was not able to find a way of iterating the jar resources to find the existing outfits. :(
		new OutfitDialog(null, "Stendhal - Choose outfit", 0, 23, 16, 11, 24).generateAllOutfits();
	}
}
