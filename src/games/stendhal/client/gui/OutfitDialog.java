/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - 2013 Stendhal                 *
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
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Outfits;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * Outfit selection dialog.
 */
class OutfitDialog extends JDialog {
	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(OutfitDialog.class);

	private static final long serialVersionUID = 4628210176721975735L;

	private static final int PLAYER_WIDTH = 48;
	private static final int PLAYER_HEIGHT = 64;
	private static final int SLIDER_WIDTH = 80;

	private final SelectorModel hair;
	private final SelectorModel eyes;
	private final SelectorModel mouth;
	private final SelectorModel head;
	private final SelectorModel body;
	private final SelectorModel dress;
	
	/**
	 * Coloring data used to get the initial colors, and to adjust colors should
	 * the player want those.
	 */
	private final OutfitColor outfitColor;

	/** Sprite direction: 0 for direction UP, 1 RIGHT, 2 DOWN and 3 LEFT. */
	private int direction = 2;

	private final SpriteStore store = SpriteStore.get();
	private final OutfitStore ostore = OutfitStore.get();
	
	private final List<ResetListener> resetListeners = new ArrayList<ResetListener>();

	/** Label containing the hair image. */
	private OutfitLabel hairLabel;
	/** Label containing the eyes image. */
	private OutfitLabel eyesLabel;
	/** Label containing the mouth image. */
	private OutfitLabel mouthLabel;
	/** Label containing the head image. */
	private OutfitLabel headLabel;
	/** Label containing the body image. */
	private OutfitLabel bodyLabel;
	/** Label containing the dress image. */
	private OutfitLabel dressLabel;
	/** Label containing the full outfit image. */
	private OutfitLabel outfitLabel;

	/** Selector for the sprite direction. */
	private JSlider directionSlider;

	/**
	 * Create a new OutfitDialog.
	 * 
	 * @param parent parent window
	 * @param title title of the dialog
	 * @param outfit number of the outfit
	 * @param outfitColor coloring information. <b>Note that outfitColor
	 *	can be modified by the dialog.</b> 
	 */
	OutfitDialog(final Frame parent, final String title, final int outfit,
			OutfitColor outfitColor) {
		this(parent, title, outfit, outfitColor, Outfits.HAIR_OUTFITS,
				Outfits.HEAD_OUTFITS, Outfits.BODY_OUTFITS,
				Outfits.CLOTHES_OUTFITS, Outfits.EYES_OUTFITS,
				Outfits.MOUTH_OUTFITS);
	}

	/**
	 * Create a new OutfitDialog.
	 * 
	 * @param parent
	 *
	 * @param title
	 *            a String with the title for the dialog
	 * @param outfit
	 *            the current outfit
	 * @param outfitColor coloring data
	 * @param total_hairs
	 *            an integer with the total of sprites with hairs
	 * @param total_heads
	 *            an integer with the total of sprites with heads
	 * @param total_bodies
	 *            an integer with the total of sprites with bodies
	 * @param total_clothes
	 *            an integer with the total of sprites with clothes
	 * @param total_eyes
	 *            an integer with the total of sprites with eyes
	 * @param total_mouths
	 *            an integer with the total of sprites with mouths
	 */
	private OutfitDialog(final Frame parent, final String title, int outfit,
			OutfitColor outfitColor, final int total_hairs,
			final int total_heads, final int total_bodies,
			final int total_clothes, final int total_eyes,
			final int total_mouths) {
		super(parent, false);
		
		this.outfitColor = outfitColor;
		
		hair = new SelectorModel(total_hairs);
		/* eyes currently only enabled through VM argument */
		if (System.getProperty("outfit.eyes") != null) {
			eyes = new SelectorModel(total_eyes);
		} else {
			eyes = null;
		}
		/* mouth currently only enabled through VM argument */
		if (System.getProperty("outfit.mouth") != null) {
			mouth = new SelectorModel(total_mouths);
		} else {
			mouth = null;
		}
		head = new SelectorModel(total_heads);
		body = new SelectorModel(total_bodies);
		dress = new SelectorModel(total_clothes);
		
		// Needs to be after initializing the models
		initComponents();
		applyStyle();
		setTitle(title);
		
		// Follow the model changes; the whole outfit follows them all
		hair.addListener(hairLabel);
		hair.addListener(outfitLabel);
		/* eyes currently only enabled through VM argument */
		if (System.getProperty("outfit.eyes") != null) {
			eyes.addListener(eyesLabel);
			eyes.addListener(outfitLabel);
		}
		/* mouth currently only enabled through VM argument */
		if (System.getProperty("outfit.mouth") != null) {
			mouth.addListener(mouthLabel);
			mouth.addListener(outfitLabel);
		}
		head.addListener(headLabel);
		head.addListener(outfitLabel);
		body.addListener(bodyLabel);
		body.addListener(outfitLabel);
		dress.addListener(dressLabel);
		dress.addListener(outfitLabel);

		// analyse current outfit
		int bodiesIndex = outfit % 100;
		outfit = outfit / 100;
		int clothesIndex = outfit % 100;
		outfit = outfit / 100;
		int headsIndex = outfit % 100;
		outfit = outfit / 100;
		// TODO: add mouth and eyes
		int mouthsIndex = 0;
		int eyesIndex = 0;
		int hairsIndex = outfit % 100;

		// reset special outfits
		if (hairsIndex >= total_hairs) {
			hairsIndex = 0;
		}
		if (eyesIndex >= total_eyes) {
			eyesIndex = 0;
		}
		if (mouthsIndex >= total_mouths) {
			mouthsIndex = 0;
		}
		if (headsIndex >= total_heads) {
			headsIndex = 0;
		}
		if (bodiesIndex >= total_bodies) {
			bodiesIndex = 0;
		}
		if (clothesIndex >= total_clothes) {
			clothesIndex = 0;
		}
		
		// Set the current outfit indices; this will update the labels as well
		hair.setIndex(hairsIndex);
		/* eyes currently only enabled through VM argument */
		if (System.getProperty("outfit.eyes") != null) {
			eyes.setIndex(eyesIndex);
		}
		/* mouth currently only enabled through VM argument */
		if (System.getProperty("outfit.mouth") != null) {
			mouth.setIndex(mouthsIndex);
		}
		head.setIndex(headsIndex);
		body.setIndex(bodiesIndex);
		dress.setIndex(clothesIndex);

		pack();
		WindowUtils.closeOnEscape(this);
		WindowUtils.trackLocation(this, "outfit");
	}

	/**
	 * Create the component layout.
	 */
	private void initComponents() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);

		final JComponent content = (JComponent) getContentPane();
		final int pad = SBoxLayout.COMMON_PADDING;
		content.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		
		content.setLayout(new SBoxLayout(SBoxLayout.HORIZONTAL, pad));
		final JComponent partialsColumn = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		content.add(partialsColumn);

		// --------- outfit parts column ----------
		
		// Hair
		SpriteRetriever hairRetriever = new SpriteRetriever() {
			@Override
			public Sprite getSprite() {
				return getHairSprite();
			}
		};
		hairLabel = new OutfitLabel(hairRetriever);
		partialsColumn.add(createSelector(hair, hairLabel));
		
		// Eyes
		/* eyes currently only enabled through VM argument */
		if (System.getProperty("outfit.eyes") != null) {
			SpriteRetriever eyesRetriever = new SpriteRetriever() {
				@Override
				public Sprite getSprite() {
					return getEyesSprite();
				}
			};
			eyesLabel = new OutfitLabel(eyesRetriever);
			partialsColumn.add(createSelector(eyes, eyesLabel));
		}
		
		// Mouth
		/* mouth currently only enabled through VM argument */
		if (System.getProperty("outfit.mouth") != null) {
			SpriteRetriever mouthRetriever = new SpriteRetriever() {
				@Override
				public Sprite getSprite() {
					return getMouthSprite();
				}
			};
			mouthLabel = new OutfitLabel(mouthRetriever);
			partialsColumn.add(createSelector(mouth, mouthLabel));
		}
		
		// Head
		SpriteRetriever headRetriever = new SpriteRetriever() {
			@Override
			public Sprite getSprite() {
				return getHeadSprite();
			}
		};
		headLabel = new OutfitLabel(headRetriever);
		partialsColumn.add(createSelector(head, headLabel));
		
		// Body
		SpriteRetriever bodyRetriever = new SpriteRetriever() {
			@Override
			public Sprite getSprite() {
				return getBodySprite();
			}
		};
		bodyLabel = new OutfitLabel(bodyRetriever);
		partialsColumn.add(createSelector(body, bodyLabel));
		
		// Dress
		SpriteRetriever dressRetriever = new SpriteRetriever() {
			@Override
			public Sprite getSprite() {
				return getDressSprite();
			}
		};
		dressLabel = new OutfitLabel(dressRetriever);
		partialsColumn.add(createSelector(dress, dressLabel));
		
		// --------- Color selection column ---------
		JComponent column = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		content.add(column, SLayout.EXPAND_Y);
		/* hair color */
		JComponent selector = createColorSelector("Hair", OutfitColor.HAIR, hairLabel);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		column.add(selector);
		SBoxLayout.addSpring(column);
		/* eyes color (currently only enabled through VM argument) */
		if (System.getProperty("outfit.eyes") != null) {
			selector = createColorSelector("Eyes", OutfitColor.EYES, eyesLabel);
			selector.setAlignmentX(CENTER_ALIGNMENT);
			column.add(selector);
		}
		/* body color */
		selector = createColorSelector("Body", OutfitColor.BODY, bodyLabel);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		column.add(selector);
		/* dress color */
		selector = createColorSelector("Dress", OutfitColor.DRESS, dressLabel);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		column.add(selector);
		
		// --------- whole outfit side ----------
		column = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		column.setAlignmentY(CENTER_ALIGNMENT);
		content.add(column);

		outfitLabel = new OutfitLabel(bodyRetriever, dressRetriever,
				headRetriever, hairRetriever);
		outfitLabel.setAlignmentX(CENTER_ALIGNMENT);
		column.add(outfitLabel);

		directionSlider = new JSlider();
		directionSlider.setMaximum(3);
		directionSlider.setSnapToTicks(true);
		directionSlider.setValue(2);
		directionSlider.setInverted(true);
		Dimension d = directionSlider.getPreferredSize();
		d.width = SLIDER_WIDTH;
		directionSlider.setPreferredSize(d);
		directionSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent evt) {
				sliderDirectionStateChanged();
			}
		});
		column.add(directionSlider);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				okActionPerformed();
			}
		});
		okButton.setAlignmentX(CENTER_ALIGNMENT);
		column.add(okButton);
	}

	/**
	 * Create a selector for outfit part.
	 * 
	 * @param model model that the buttons should modify
	 * @param label central image label
	 * @return selector component
	 */
	private JComponent createSelector(final SelectorModel model, OutfitLabel label) {
		JComponent row = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
	
		JButton button = new JButton("<");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.scrollDown();
			}
		});
		row.add(button);
		row.add(label);
		button = new JButton(">");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.scrollUp();
			}
		});
		row.add(button);
		
		return row;
	}

	/**
	 * This is called every time the user moves the slider.
	 */
	private void sliderDirectionStateChanged() {
		direction = directionSlider.getValue();

		outfitLabel.changed();
		hairLabel.changed();
		/* eyes currently only enabled through VM argument */
		if (System.getProperty("outfit.eyes") != null) {
			eyesLabel.changed();
		}
		headLabel.changed();
		bodyLabel.changed();
		dressLabel.changed();
	}
	
	/**
	 * Get the hair sprite.
	 * 
	 * @return hair sprite
	 */
	private Sprite getHairSprite() {
		return store.getTile(ostore.getHairSprite(hair.getIndex(), outfitColor),
				PLAYER_WIDTH, direction * PLAYER_HEIGHT, PLAYER_WIDTH,
				PLAYER_HEIGHT);
	}
	
	/**
	 * Get the eyes sprite.
	 * 
	 * @return eyes sprite
	 */
	private Sprite getEyesSprite() {
		return store.getTile(ostore.getEyesSprite(eyes.getIndex(), outfitColor),
				PLAYER_WIDTH, direction * PLAYER_HEIGHT, PLAYER_WIDTH,
				PLAYER_HEIGHT);
	}
	
	/**
	 * Get the mouth sprite.
	 * 
	 * @return mouth sprite
	 */
	private Sprite getMouthSprite() {
		return store.getTile(ostore.getMouthSprite(mouth.getIndex()),
				PLAYER_WIDTH, direction * PLAYER_HEIGHT, PLAYER_WIDTH,
				PLAYER_HEIGHT);
	}
	
	/**
	 * Get the head sprite.
	 * 
	 * @return head sprite
	 */
	private Sprite getHeadSprite() {
		return store.getTile(ostore.getHeadSprite(head.getIndex(), outfitColor),
				PLAYER_WIDTH, direction * PLAYER_HEIGHT, PLAYER_WIDTH,
				PLAYER_HEIGHT);
	}
	
	/**
	 * Get the body sprite.
	 * 
	 * @return body sprite
	 */
	private Sprite getBodySprite() {
		return store.getTile(ostore.getBodySprite(body.getIndex(), outfitColor),
				PLAYER_WIDTH, direction * PLAYER_HEIGHT, PLAYER_WIDTH,
				PLAYER_HEIGHT);
	}
	
	/**
	 * Get the dress sprite.
	 * 
	 * @return dress sprite
	 */
	private Sprite getDressSprite() {
		return store.getTile(ostore.getDressSprite(dress.getIndex(), outfitColor), PLAYER_WIDTH,
				direction * PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
	}
	
	/**
	 * Create a color selection component for an outfit part.
	 * 
	 * @param niceName outfit part name that is capitalizes for user to see
	 * @param key outfit part identifier
	 * @param label outfit part display that should be kept up to date with the
	 * 	color changes (in addition of the whole outfit display)
	 * @return color selection component
	 */
	private JComponent createColorSelector(final String niceName, final String key, final OutfitLabel label) {
		final JComponent container = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		final JCheckBox enableToggle = new JCheckBox(niceName + " color");
		
		container.add(enableToggle);
		// get the current state
		boolean colored = outfitColor.getColor(key) != null;
		enableToggle.setSelected(colored);
		final ColorSelector selector = new ColorSelector();
		selector.setEnabled(colored);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		container.add(selector);
		final ColorSelectionModel model = selector.getSelectionModel(); 
		model.setSelectedColor(outfitColor.getColor(key));
		selector.getSelectionModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ev) {
				outfitColor.setColor(key, model.getSelectedColor());
				label.changed();
				outfitLabel.changed();
			}
		});
		
		enableToggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (enableToggle.isSelected()) {
					// restore previously selected color, if any
					outfitColor.setColor(key, model.getSelectedColor());
					label.changed();
					outfitLabel.changed();
					selector.setEnabled(true);
				} else {
					// use default coloring
					outfitColor.setColor(key, null);
					label.changed();
					outfitLabel.changed();
					selector.setEnabled(false);
				}
			}
		});
		
		// For restoring the state
		resetListeners.add(new ResetListener() {
			@Override
			public void reset() {
				Color color = outfitColor.getColor(key);
				boolean colored = color != null;
				if (colored) {
					/*
					 * Changing the model triggers setting the color in
					 * outfitColor, and null color is interpreted as grey in the
					 * selector model, so avoid setting that.
					 * 
					 * As a side effect, the color selector remembers the
					 * previously selected color for non colored outfit parts.
					 * That is likely a better default than mid grey anyway.  
					 */
					model.setSelectedColor(color);
				}
				selector.setEnabled(colored);
				enableToggle.setSelected(colored);
			}
		});
		
		return container;
	}

	/**
	 * OK Button action.
	 */
	private void okActionPerformed() {
		sendAction();
		this.dispose();
	} 

	/**
	 * Sent the outfit change action to the server.
	 */
	private void sendAction() {
		StendhalClient client = StendhalClient.get();

		final RPAction rpaction = new RPAction();
		rpaction.put("type", "outfit");
		rpaction.put("value", body.getIndex() + dress.getIndex() * 100 + head.getIndex()
				* 100 * 100 + hair.getIndex() * 100 * 100 * 100);
		/* hair color */
		Color color = outfitColor.getColor("hair");
		if (color != null) {
			rpaction.put(OutfitColor.HAIR, color.getRGB());
		}
		/* body and head color */
		color = outfitColor.getColor(OutfitColor.BODY);
		if (color != null) {
			rpaction.put(OutfitColor.BODY, color.getRGB());
		}
		/* dress color */
		color = outfitColor.getColor(OutfitColor.DRESS);
		if (color != null) {
			rpaction.put(OutfitColor.DRESS, color.getRGB());
		}
		client.send(rpaction);
	}
		
	/**
	 * Apply Stendhal style to all components.
	 */
	private void applyStyle() {
		Style style = StyleUtil.getStyle();
		if (style != null) {
			// Labels (Images). Making all JLabels bordered would be undesired
			bodyLabel.setBorder(style.getBorderDown());
			dressLabel.setBorder(style.getBorderDown());
			outfitLabel.setBorder(style.getBorderDown());
			hairLabel.setBorder(style.getBorderDown());
			/* eyes currently only enabled through VM argument */
			if (System.getProperty("outfit.eyes") != null) {
				eyesLabel.setBorder(style.getBorderDown());
			}
			/* mouth currently only enabled through VM argument */
			if (System.getProperty("outfit.mouth") != null) {
				mouthLabel.setBorder(style.getBorderDown());
			}
			headLabel.setBorder(style.getBorderDown());
		}
	}
	
	/**
	 * Set the state of the selector.
	 * 
	 * @param outfit outfit code
	 * @param colors color state. Unlike the one passed to the constructor, this
	 * 	will not be modified
	 */
	void setState(int outfit, OutfitColor colors) {
		// Copy the original colors
		outfitColor.setColor(OutfitColor.DRESS, colors.getColor(OutfitColor.DRESS));
		outfitColor.setColor(OutfitColor.HAIR, colors.getColor(OutfitColor.HAIR));
		outfitColor.setColor(OutfitColor.BODY, colors.getColor(OutfitColor.BODY));
		
		// analyze the outfit code
		int bodiesIndex = outfit % 100;
		outfit = outfit / 100;
		int clothesIndex = outfit % 100;
		outfit = outfit / 100;
		int headsIndex = outfit % 100;
		outfit = outfit / 100;
		// TODO: add mouth and eyes
		int mouthsIndex = 0;
		int eyesIndex = 0;
		int hairsIndex = outfit % 100;
		
		body.setIndex(bodiesIndex);
		dress.setIndex(clothesIndex);
		head.setIndex(headsIndex);
		/* eyes currently only enabled through VM argument */
		if (System.getProperty("outfit.eyes") != null) {
			eyes.setIndex(eyesIndex);
		}
		/* mouth currently only enabled through VM argument */
		if (System.getProperty("outfit.mouth") != null) {
			mouth.setIndex(mouthsIndex);
		}
		hair.setIndex(hairsIndex);

		// Color selectors, and their toggles
		for (ResetListener l : resetListeners) {
			l.reset();
		}
	}
	
	/**
	 * Interface for components that can be reseted to a default state. 
	 */
	private interface ResetListener {
		void reset();
	}
	
	/**
	 * An image label for outfit and outfit parts.
	 */
	private static class OutfitLabel extends JLabel implements IndexChangeListener {
		final SpriteRetriever[] retrievers;
		
		/**
		 * Create a new OutfitLabel.
		 * 
		 * @param retrievers sprite sources used to update the image, when
		 *	changed() is called
		 */
		OutfitLabel(SpriteRetriever ... retrievers) {
			setOpaque(true);
			this.retrievers = retrievers;
		}
		
		@Override
		public void changed() {
			// Update image
			BufferedImage img = getGraphicsConfiguration().createCompatibleImage(PLAYER_WIDTH, PLAYER_HEIGHT);
			Graphics g = img.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, PLAYER_WIDTH, PLAYER_HEIGHT);
			for (SpriteRetriever retriever : retrievers) {
				retriever.getSprite().draw(g, 0, 0);
			}
			g.dispose();
			ImageIcon icon = new ImageIcon(img);
			setIcon(icon);
		}
	}
	
	/**
	 * A ranged, circular, index model.
	 */
	private static class SelectorModel {
		final int n;
		int index;
		final List<IndexChangeListener> listeners = new ArrayList<IndexChangeListener>();
		
		/**
		 * Create a new SelectorModel. Valid indices are 0 to n - 1.
		 * 
		 * @param n maximum value
		 */
		SelectorModel(int n) {
			if (n <= 0) {
				throw new IllegalArgumentException("Can not create a model with " + n + " elements");
			}
			this.n = n;
		}
		
		/**
		 * Add a new listener for value changes.
		 * 
		 * @param listener added listener
		 */
		void addListener(IndexChangeListener listener) {
			listeners.add(listener);
		}
		
		/**
		 * Set index.
		 *  
		 * @param index new index
		 */
		void setIndex(int index) {
			if ((index < 0) || (index >= n)) {
				LOGGER.warn("Index out of allowed range [0-" + n + "]: " + index, 
						new Throwable());
				index = 0;
			}
			this.index = index;
			fire();
		}
		
		/**
		 * Scroll index value downwards.
		 */
		void scrollDown() {
			// avoid negatives
			index += n - 1;
			index %= n;
			fire();
		}
		
		/**
		 * Scroll the index value upwards.
		 */
		void scrollUp() {
			index++;
			index %= n;
			fire();
		}
		
		/**
		 * Get the current index value.
		 * 
		 * @return index
		 */
		int getIndex() {
			return index;
		}
		
		/**
		 * Notify listeners that the value has changed.
		 */
		private void fire() {
			for (IndexChangeListener listener : listeners) {
				listener.changed();
			}
		}
	}

	/**
	 * An interface for objects that can fetch outfit part sprites.
	 */
	private interface SpriteRetriever {
		/**
		 * Get the sprite.
		 * 
		 * @return sprite
		 */
		Sprite getSprite();
	}
	
	/**
	 * Interface for listening SelectorModel changes.
	 */
	private interface IndexChangeListener {
		/**
		 * Called when the model changes.
		 */
		void changed();
	}
}
