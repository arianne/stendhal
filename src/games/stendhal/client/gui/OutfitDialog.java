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

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Timer;
import java.util.TimerTask;

import marauroa.common.game.RPAction;

public class OutfitDialog extends javax.swing.JDialog {
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
    public OutfitDialog(java.awt.Frame parent, String title, int outfit, int total_hairs, int total_heads, int total_bodies, int total_clothes) {
        super(parent, false);
        initComponents();
        setTitle(title);
        
        client=StendhalClient.get();

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
        SpriteStore st=SpriteStore.get();        
        
        // load the sprites
        for (int i = 1; i < hairs.length; i++)
            hairs[i] = st.getSprite("data/sprites/outfit/hair_" + i + ".png");
        for (int i = 0; i < heads.length; i++)
            heads[i] = st.getSprite("data/sprites/outfit/head_" + i + ".png");
        for (int i = 0; i < bodies.length; i++)
            bodies[i] = st.getSprite("data/sprites/outfit/player_base_" + i + ".png");
        for (int i = 1; i < clothes.length; i++)
            clothes[i] = st.getSprite("data/sprites/outfit/dress_" + i + ".png");
            
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
        g.setColor(java.awt.Color.WHITE);
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
        jpanel = new javax.swing.JPanel();
        jbtOK = new javax.swing.JButton();
        jbtLeftHairs = new javax.swing.JButton();
        jbtRightHairs = new javax.swing.JButton();
        jbtLeftHeads = new javax.swing.JButton();
        jbtRightHeads = new javax.swing.JButton();
        jbtLeftBodies = new javax.swing.JButton();
        jbtRightBodies = new javax.swing.JButton();
        jbtLeftClothes = new javax.swing.JButton();
        jbtRightClothes = new javax.swing.JButton();
        jlblHairs = new javax.swing.JLabel();
        jlblHeads = new javax.swing.JLabel();
        jlblBodies = new javax.swing.JLabel();
        jlblClothes = new javax.swing.JLabel();
        jlblFinalResult = new javax.swing.JLabel();
        jsliderAnimation = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(200, 200, 200));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jpanel.setLayout(null);

        jpanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(100, 100, 100), 2, true));
        jbtOK.setText("OK");
        jbtOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtOKActionPerformed(evt);
            }
        });

        jpanel.add(jbtOK);
        jbtOK.setBounds(190, 220, 80, 30);

        jbtLeftHairs.setFont(new java.awt.Font("Dialog", 1, 14));
        jbtLeftHairs.setText("<");
        jbtLeftHairs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtLeftHairsActionPerformed(evt);
            }
        });

        jpanel.add(jbtLeftHairs);
        jbtLeftHairs.setBounds(10, 20, 45, 30);

        jbtRightHairs.setFont(new java.awt.Font("Dialog", 1, 14));
        jbtRightHairs.setText(">");
        jbtRightHairs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRightHairsActionPerformed(evt);
            }
        });

        jpanel.add(jbtRightHairs);
        jbtRightHairs.setBounds(120, 20, 45, 30);

        jbtLeftHeads.setFont(new java.awt.Font("Dialog", 1, 14));
        jbtLeftHeads.setText("<");
        jbtLeftHeads.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtLeftHeadsActionPerformed(evt);
            }
        });

        jpanel.add(jbtLeftHeads);
        jbtLeftHeads.setBounds(10, 100, 45, 30);

        jbtRightHeads.setFont(new java.awt.Font("Dialog", 1, 14));
        jbtRightHeads.setText(">");
        jbtRightHeads.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRightHeadsActionPerformed(evt);
            }
        });

        jpanel.add(jbtRightHeads);
        jbtRightHeads.setBounds(120, 100, 45, 30);

        jbtLeftBodies.setFont(new java.awt.Font("Dialog", 1, 14));
        jbtLeftBodies.setText("<");
        jbtLeftBodies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtLeftBodiesActionPerformed(evt);
            }
        });

        jpanel.add(jbtLeftBodies);
        jbtLeftBodies.setBounds(10, 180, 45, 30);

        jbtRightBodies.setFont(new java.awt.Font("Dialog", 1, 14));
        jbtRightBodies.setText(">");
        jbtRightBodies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRightBodiesActionPerformed(evt);
            }
        });

        jpanel.add(jbtRightBodies);
        jbtRightBodies.setBounds(120, 180, 45, 30);

        jbtLeftClothes.setFont(new java.awt.Font("Dialog", 1, 14));
        jbtLeftClothes.setText("<");
        jbtLeftClothes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtLeftClothesActionPerformed(evt);
            }
        });

        jpanel.add(jbtLeftClothes);
        jbtLeftClothes.setBounds(10, 260, 45, 30);

        jbtRightClothes.setFont(new java.awt.Font("Dialog", 1, 14));
        jbtRightClothes.setText(">");
        jbtRightClothes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRightClothesActionPerformed(evt);
            }
        });

        jpanel.add(jbtRightClothes);
        jbtRightClothes.setBounds(120, 260, 45, 30);

        jlblHairs.setBackground(new java.awt.Color(255, 255, 255));
        jlblHairs.setFont(new java.awt.Font("Dialog", 0, 10));
        jlblHairs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblHairs.setText("loading...");
        jlblHairs.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(100, 100, 100), 1, true));
        jlblHairs.setOpaque(true);
        jpanel.add(jlblHairs);
        jlblHairs.setBounds(60, 10, 52, 68);

        jlblHeads.setBackground(new java.awt.Color(255, 255, 255));
        jlblHeads.setFont(new java.awt.Font("Dialog", 0, 10));
        jlblHeads.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblHeads.setText("loading...");
        jlblHeads.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(100, 100, 100), 1, true));
        jlblHeads.setOpaque(true);
        jpanel.add(jlblHeads);
        jlblHeads.setBounds(60, 90, 52, 68);

        jlblBodies.setBackground(new java.awt.Color(255, 255, 255));
        jlblBodies.setFont(new java.awt.Font("Dialog", 0, 10));
        jlblBodies.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblBodies.setText("loading...");
        jlblBodies.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(100, 100, 100), 1, true));
        jlblBodies.setOpaque(true);
        jpanel.add(jlblBodies);
        jlblBodies.setBounds(60, 170, 52, 68);

        jlblClothes.setBackground(new java.awt.Color(255, 255, 255));
        jlblClothes.setFont(new java.awt.Font("Dialog", 0, 10));
        jlblClothes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblClothes.setText("loading...");
        jlblClothes.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(100, 100, 100), 1, true));
        jlblClothes.setOpaque(true);
        jpanel.add(jlblClothes);
        jlblClothes.setBounds(60, 250, 52, 68);

        jlblFinalResult.setBackground(new java.awt.Color(255, 255, 255));
        jlblFinalResult.setFont(new java.awt.Font("Dialog", 0, 10));
        jlblFinalResult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblFinalResult.setText("loading...");
        jlblFinalResult.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(100, 100, 100), 1, true));
        jlblFinalResult.setOpaque(true);
        jpanel.add(jlblFinalResult);
        jlblFinalResult.setBounds(205, 90, 52, 68);

        jsliderAnimation.setMaximum(3);
        jsliderAnimation.setSnapToTicks(true);
        jsliderAnimation.setValue(2);
        jsliderAnimation.setInverted(true);
        jsliderAnimation.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsliderAnimationStateChanged(evt);
            }
        });

        jpanel.add(jsliderAnimation);
        jsliderAnimation.setBounds(190, 170, 80, 27);

        getContentPane().add(jpanel, java.awt.BorderLayout.CENTER);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-288)/2, (screenSize.height-361)/2, 288, 361);
    }
    // </editor-fold>//GEN-END:initComponents

    /** this is called everytime the user moves the slider */
    private void jsliderAnimationStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsliderAnimationStateChanged
        animation = jsliderAnimation.getValue();
        drawFinalPlayer(jlblFinalResult.getGraphics());
        drawSinglePart(hairs[hairs_index], jlblHairs.getGraphics());
        drawSinglePart(heads[heads_index], jlblHeads.getGraphics());
        drawSinglePart(bodies[bodies_index], jlblBodies.getGraphics());
        drawSinglePart(clothes[clothes_index], jlblClothes.getGraphics());
    }//GEN-LAST:event_jsliderAnimationStateChanged


    /** when user closes this window */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        timer.cancel();
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    /** Clothes Right button */
    private void jbtRightClothesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRightClothesActionPerformed
        if (clothes_index < clothes.length - 1)
            clothes_index++;
        else
            clothes_index = 0;
        drawSinglePart(clothes[clothes_index], jlblClothes.getGraphics());
        drawFinalPlayer(jlblFinalResult.getGraphics());
    }//GEN-LAST:event_jbtRightClothesActionPerformed

    /** Clothes Left button */
    private void jbtLeftClothesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtLeftClothesActionPerformed
        if (clothes_index > 0)
            clothes_index--;
        else
            clothes_index = clothes.length - 1;
        drawSinglePart(clothes[clothes_index], jlblClothes.getGraphics());
        drawFinalPlayer(jlblFinalResult.getGraphics());
    }//GEN-LAST:event_jbtLeftClothesActionPerformed

    /** Bodies Right button */
    private void jbtRightBodiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRightBodiesActionPerformed
        if (bodies_index < bodies.length - 1)
            bodies_index++;
        else
            bodies_index = 0;
        drawSinglePart(bodies[bodies_index], jlblBodies.getGraphics());
        drawFinalPlayer(jlblFinalResult.getGraphics());
    }//GEN-LAST:event_jbtRightBodiesActionPerformed

    /** Bodies Left button */
    private void jbtLeftBodiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtLeftBodiesActionPerformed
        if (bodies_index > 0)
            bodies_index--;
        else
            bodies_index = bodies.length - 1;
        drawSinglePart(bodies[bodies_index], jlblBodies.getGraphics());
        drawFinalPlayer(jlblFinalResult.getGraphics());
    }//GEN-LAST:event_jbtLeftBodiesActionPerformed

    /** Heads Right button */
    private void jbtRightHeadsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRightHeadsActionPerformed
        if (heads_index < heads.length - 1)
            heads_index++;
        else
            heads_index = 0;
        drawSinglePart(heads[heads_index], jlblHeads.getGraphics());
        drawFinalPlayer(jlblFinalResult.getGraphics());
    }//GEN-LAST:event_jbtRightHeadsActionPerformed

    /** Heads Left button */
    private void jbtLeftHeadsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtLeftHeadsActionPerformed
        if (heads_index > 0)
            heads_index--;
        else
            heads_index = heads.length - 1;
        drawSinglePart(heads[heads_index], jlblHeads.getGraphics());
        drawFinalPlayer(jlblFinalResult.getGraphics());
    }//GEN-LAST:event_jbtLeftHeadsActionPerformed

    /** Hairs Right button */
    private void jbtRightHairsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRightHairsActionPerformed
        if (hairs_index < hairs.length - 1)
            hairs_index++;
        else
            hairs_index = 0;
        drawSinglePart(hairs[hairs_index], jlblHairs.getGraphics());
        drawFinalPlayer(jlblFinalResult.getGraphics());
    }//GEN-LAST:event_jbtRightHairsActionPerformed

    /** Hairs Left button */
    private void jbtLeftHairsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtLeftHairsActionPerformed
        if (hairs_index > 0)
            hairs_index--;
        else
            hairs_index = hairs.length - 1;
        drawSinglePart(hairs[hairs_index], jlblHairs.getGraphics());
        drawFinalPlayer(jlblFinalResult.getGraphics());
    }//GEN-LAST:event_jbtLeftHairsActionPerformed

    /** Button OK action */
    private void jbtOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtOKActionPerformed
        sendAction();

        timer.cancel();
        this.dispose();
    }//GEN-LAST:event_jbtOKActionPerformed
    
    private void sendAction()
      {
      if(client==null)
        {
        /** If running standalone, just print the outfit */
        System.out.println ("OUTFIT is: "+(bodies_index+clothes_index*100+heads_index*100*100+hairs_index*100*100*100));
        return;
        }
        
      RPAction rpaction=new RPAction();
      rpaction.put("type","outfit");
      rpaction.put("value",bodies_index+clothes_index*100+heads_index*100*100+hairs_index*100*100*100);
      client.send(rpaction);
      }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbtLeftBodies;
    private javax.swing.JButton jbtLeftClothes;
    private javax.swing.JButton jbtLeftHairs;
    private javax.swing.JButton jbtLeftHeads;
    private javax.swing.JButton jbtOK;
    private javax.swing.JButton jbtRightBodies;
    private javax.swing.JButton jbtRightClothes;
    private javax.swing.JButton jbtRightHairs;
    private javax.swing.JButton jbtRightHeads;
    private javax.swing.JLabel jlblBodies;
    private javax.swing.JLabel jlblClothes;
    private javax.swing.JLabel jlblFinalResult;
    private javax.swing.JLabel jlblHairs;
    private javax.swing.JLabel jlblHeads;
    private javax.swing.JPanel jpanel;
    private javax.swing.JSlider jsliderAnimation;
    // End of variables declaration//GEN-END:variables

    /**
     * TODO: maybe this could be improved, to reduce the flicker
     *
     * Private class that handles the update (repaint) of jLabels
     */
    private class AnimationTask extends TimerTask {
        public void run() {
            // draws single parts
            drawSinglePart(hairs[hairs_index], jlblHairs.getGraphics());
            drawSinglePart(heads[heads_index], jlblHeads.getGraphics());
            drawSinglePart(bodies[bodies_index], jlblBodies.getGraphics());
            drawSinglePart(clothes[clothes_index], jlblClothes.getGraphics());
            // draws final player
            drawFinalPlayer(jlblFinalResult.getGraphics());
        }

    }
    
    static class HeadFilter implements FilenameFilter
      {
      public boolean accept(File dir, String name)
        {
        if(name.startsWith("head"))
          {
          return true;
          }
        
        return false;
        }
      }

    static class HairFilter implements FilenameFilter
      {
      public boolean accept(File dir, String name)
        {
        if(name.startsWith("hair"))
          {
          return true;
          }
        
        return false;
        }
      }

    static class BodyFilter implements FilenameFilter
      {
      public boolean accept(File dir, String name)
        {
        if(name.startsWith("player_base"))
          {
          return true;
          }
        
        return false;
        }
      }

    static class ClothesFilter implements FilenameFilter
      {
      public boolean accept(File dir, String name)
        {
        if(name.startsWith("dress"))
          {
          return true;
          }
        
        return false;
        }
      } 
    
    private void generateAllOutfits()
      {
      /** TEST METHOD: DON'T NO USE */
      for(bodies_index=0;bodies_index<bodies.length;bodies_index++)
      for(clothes_index=0;clothes_index<clothes.length;clothes_index++)
      for(heads_index=0;heads_index<heads.length;heads_index++)
      for(hairs_index=0;hairs_index<hairs.length;hairs_index++)
        {
        String name=Integer.toString(bodies_index+clothes_index*100+heads_index*100*100+hairs_index*100*100*100);
        System.out.println ("Creating "+name+".png");
        Image image=new java.awt.image.BufferedImage(48,64,java.awt.image.BufferedImage.TYPE_INT_ARGB);
        drawFinalPlayer(image.getGraphics());
        try
          {
        javax.imageio.ImageIO.write((java.awt.image.RenderedImage)image,"png",new File("outfits\\"+name+".png"));
          }
        catch(Exception e)
          {
          e.printStackTrace();
          }
        }
      }

//  public OutfitDialog(java.awt.Frame parent, String title, int outfit, int total_hairs, int total_heads, int total_bodies, int total_clothes) {
     public static void main(String args[]) {
         new OutfitDialog(null, "Stendhal - choose outfit", 0, 14, 11, 11, 17).generateAllOutfits();
    }
}