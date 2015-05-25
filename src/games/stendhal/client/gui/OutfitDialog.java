/***************************************************************************
 *                      (C) Copyright 2003 - 2015 Stendhal                 *
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
import games.stendhal.common.constants.Actions;
import games.stendhal.common.constants.Testing;

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
	
	private static final int PLAYER_WIDTH = 48;
	private static final int PLAYER_HEIGHT = 64;
	private static final int SLIDER_WIDTH = 80;

	private final SelectorModel hair = new SelectorModel(Outfits.HAIR_OUTFITS);
	private final SelectorModel eyes = new SelectorModel(Outfits.EYES_OUTFITS);
	private final SelectorModel mouth = new SelectorModel(Outfits.MOUTH_OUTFITS);
	private final SelectorModel head = new SelectorModel(Outfits.HEAD_OUTFITS);
	private final SelectorModel body = new SelectorModel(Outfits.BODY_OUTFITS);
	private final SelectorModel dress = new SelectorModel(Outfits.CLOTHES_OUTFITS);
	
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
	OutfitDialog(final Frame parent, final String title, int outfit,
			final OutfitColor outfitColor) {
		super(parent, false);
		
		this.outfitColor = outfitColor;
		
		// Needs to be after initializing the models
		initComponents();
		applyStyle();
		setTitle(title);
		
		// Follow the model changes; the whole outfit follows them all
		hair.addListener(hairLabel);
		hair.addListener(outfitLabel);
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
		int hairsIndex = outfit % 100;

		// reset special outfits
		hairsIndex = checkIndex(hairsIndex, hair);
		headsIndex = checkIndex(headsIndex, head);
		bodiesIndex = checkIndex(bodiesIndex, body);
		clothesIndex = checkIndex(clothesIndex, dress);
		
		// Set the current outfit indices; this will update the labels as well
		hair.setIndex(hairsIndex);
		head.setIndex(headsIndex);
		body.setIndex(bodiesIndex);
		dress.setIndex(clothesIndex);

		pack();
		WindowUtils.closeOnEscape(this);
		WindowUtils.trackLocation(this, "outfit", false);
	}
	
	/**
	 * Create a new outfit dialog with extended outfit features: Currently
	 * mouth and eyes.
	 * 
	 * @param parent
	 * 		The parent object of this dialog
	 * @param title
	 * 		Text to be displayed in title bar
	 * @param outfit
	 * 		10-digit code representing original outfit features (old outfit
	 * 		system)
	 * @param outfitColor
	 * 		Coloring information for outfit parts (<b>Note that outfitColor can
	 * 		be modified by the dialog</b>)
	 */
	OutfitDialog(final Frame parent, final String title, long outfit,
			final OutfitColor outfitColor) {
		this(parent, title, (int)(outfit / 10000), outfitColor);
		
		// Follow the model changes
		eyes.addListener(eyesLabel);
		eyes.addListener(outfitLabel);
		mouth.addListener(mouthLabel);
		mouth.addListener(outfitLabel);
		
		// Analyze current outfit
		int mouthsIndex = (int) (outfit % 100);
		outfit = outfit / 100;
		int eyesIndex = (int) (outfit % 100);
		
		// Reset special outfits
		mouthsIndex = checkIndex(mouthsIndex, mouth);
		eyesIndex = checkIndex(eyesIndex, eyes);
		
		// Set the current outfit indices; this will update the labels as well
		eyes.setIndex(eyesIndex);
		mouth.setIndex(mouthsIndex);
		
		pack();
	}
	
	/**
	 * Check an index is within player accessible limits.
	 * 
	 * @param index current index
	 * @param model to determine the limits
	 * @return index, if the supplied index is within limits, otherwise 0
	 */
	private int checkIndex(int index, SelectorModel model) {
		if (!model.isAllowed(index)) {
			return 0;
		}
		return index;
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
		
		/* TODO: Remove condition after outfit testing is finished. */
		SpriteRetriever eyesRetriever = null, mouthRetriever = null;
		if (Testing.OUTFITS) {
			// Eyes
			eyesRetriever = new SpriteRetriever() {
				@Override
				public Sprite getSprite() {
					return getEyesSprite();
				}
			};
			eyesLabel = new OutfitLabel(eyesRetriever);
			partialsColumn.add(createSelector(eyes, eyesLabel));
			
			// Mouth
			mouthRetriever = new SpriteRetriever() {
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
		JComponent selector = createColorSelector("Hair", OutfitColor.HAIR,
				hairLabel);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		column.add(selector);
		
		// TODO: Remove condition after outfit testing is finished
		if (Testing.OUTFITS) {
			/* eyes color */
			selector = createColorSelector("Eyes", OutfitColor.EYES, eyesLabel);
			selector.setAlignmentX(CENTER_ALIGNMENT);
			column.add(selector);
			
			/* skin color */
			selector = createColorSelector("Skin", OutfitColor.SKIN, true,
					bodyLabel, headLabel);
			selector.setAlignmentX(CENTER_ALIGNMENT);
			column.add(selector);
		}
		/* dress color */
		selector = createColorSelector("Dress", OutfitColor.DRESS, dressLabel);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		column.add(selector);
		SBoxLayout.addSpring(column);
		
		// --------- whole outfit side ----------
		column = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		column.setAlignmentY(CENTER_ALIGNMENT);
		content.add(column);
		
		/* TODO: Remove condition after outfit testing is finished. */
		if (Testing.OUTFITS) {
			outfitLabel = new OutfitLabel(bodyRetriever, dressRetriever,
					headRetriever, mouthRetriever, eyesRetriever,
					hairRetriever);
		} else {
			outfitLabel = new OutfitLabel(bodyRetriever, dressRetriever,
					headRetriever, hairRetriever);
		}
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
		/* TODO: Remove condition after outfit testing is finished. */
		if (Testing.OUTFITS) {
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
	 * @param labels outfit part displays that should be kept up to date with
	 *	the color changes (in addition of the whole outfit display)
	 * @return color selection component
	 */
	private JComponent createColorSelector(String niceName, String key,
			OutfitLabel... labels) {
		return this.createColorSelector(niceName, key, false, labels);
	}
	
	/**
	 * Create a color selection component for an outfit part optionally with
	 * defined skin colors only.
	 * 
	 * @param niceName
	 * 		Outfit part name that is capitalizes for user to see
	 * @param key
	 * 		Outfit part identifier
	 * @param labels
	 * 		List of outfit part display that should be kept up to date with
	 * 		the color changes (in addition of the whole outfit display)
	 * @param skinPalette
	 * 		Use skin colors only
	 * @return
	 * 		color selection component
	 */
	private JComponent createColorSelector(final String niceName, final String key,
			boolean skinPalette, final OutfitLabel... labels) {
		
		final JComponent container = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		final JCheckBox enableToggle = new JCheckBox(niceName + " color");
		
		container.add(enableToggle);
		// get the current state
		boolean colored = outfitColor.getColor(key) != null;
		enableToggle.setSelected(colored);
		
		final AbstractColorSelector<?> selector;
		if (skinPalette) {
			selector = new SkinColorSelector();
		} else {
			selector = new ColorSelector();
		}
		selector.setEnabled(colored);
		selector.setAlignmentX(CENTER_ALIGNMENT);
		container.add(selector);
		final ColorSelectionModel model = selector.getSelectionModel();
		model.setSelectedColor(outfitColor.getColor(key));
		model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ev) {
				outfitColor.setColor(key, model.getSelectedColor());
				for (OutfitLabel label : labels) {
					label.changed();
				}
				outfitLabel.changed();
			}
		});
		
		enableToggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (enableToggle.isSelected()) {
					// restore previously selected color, if any
					outfitColor.setColor(key, model.getSelectedColor());
				} else {
					// use default coloring
					outfitColor.setColor(key, null);
				}
				selector.setEnabled(enableToggle.isSelected());
				for (OutfitLabel label : labels) {
					label.changed();
				}
				outfitLabel.changed();
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
		Color color;

		final RPAction rpOutfitAction = new RPAction();
		/* TODO: Remove condition when outfit testing is finished */
		if (Testing.OUTFITS) {
			rpOutfitAction.put(Actions.TYPE, "outfit_extended");
			long value = (body.getIndex() + (dress.getIndex() * 100)
					+ (head.getIndex() * (int)Math.pow(100, 2))
					+ (hair.getIndex() * (int)Math.pow(100, 3))
					+ (mouth.getIndex() * (int)Math.pow(100, 5))
					+ (eyes.getIndex() * (int)Math.pow(100, 6)));
			rpOutfitAction.put(Actions.VALUE, value);
		} else {
			rpOutfitAction.put(Actions.TYPE, "outfit");
			rpOutfitAction.put(Actions.VALUE, body.getIndex()
					+ (dress.getIndex() * 100)
					+ (head.getIndex() * 100 * 100)
					+ (hair.getIndex() * 100 * 100 * 100));
		}
		
		/* hair color */
		color = outfitColor.getColor(OutfitColor.HAIR);
		if (color != null) {
			rpOutfitAction.put(OutfitColor.HAIR, color.getRGB());
		}
		
		/* body and head color */
		color = outfitColor.getColor(OutfitColor.SKIN);
		if (color != null) {
			rpOutfitAction.put(OutfitColor.SKIN, color.getRGB());
		}
		
		/* dress color */
		color = outfitColor.getColor(OutfitColor.DRESS);
		if (color != null) {
			rpOutfitAction.put(OutfitColor.DRESS, color.getRGB());
		}
		
		/* TODO: Remove condition after outfit testing is finished. */
		if (Testing.OUTFITS) {
			/* eyes color */
			color = outfitColor.getColor(OutfitColor.EYES);
			if (color != null) {
				rpOutfitAction.put(OutfitColor.EYES, color.getRGB());
			}
		}
		
		client.send(rpOutfitAction);
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
			/* TODO: Remove condition after outfit testing is finished */
			if (Testing.OUTFITS) {
				eyesLabel.setBorder(style.getBorderDown());
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
		/* TODO: Remove condition after outfit testing is finished. */
		if (Testing.OUTFITS) {
			outfitColor.setColor(OutfitColor.SKIN, colors.getColor(OutfitColor.SKIN));
		}
		
		// analyze the outfit code
		int bodiesIndex = outfit % 100;
		outfit = outfit / 100;
		int clothesIndex = outfit % 100;
		outfit = outfit / 100;
		int headsIndex = outfit % 100;
		outfit = outfit / 100;
		int hairsIndex = outfit % 100;
		
		body.setIndex(bodiesIndex);
		dress.setIndex(clothesIndex);
		head.setIndex(headsIndex);
		hair.setIndex(hairsIndex);

		// Color selectors, and their toggles
		for (ResetListener l : resetListeners) {
			l.reset();
		}
	}
	
	/**
	 * Set the state of the selector for extened outfit features: Currently
	 * mouth and eyes.
	 * 
	 * @param outfit
	 * 		14-digit integer representing extended features
	 * @param colors
	 * 		Color state of outfit parts (will not be modiefied like the one
	 * 		passed to constructor)
	 */
	void setState(long outfit, final OutfitColor colors) {
		// Analyze the outfit code
		int mouthsIndex = (int) (outfit % 100);
		outfit = outfit / 100;
		int eyesIndex = (int) (outfit % 100);
		outfit = outfit / 100;
		
		mouth.setIndex(mouthsIndex);
		eyes.setIndex(eyesIndex);
		
		// Run code for original (old) outfit system to update listeners
		this.setState((int)outfit, colors);
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
			if (!isAllowed(index)) {
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
		 * Check if an index is within allowed limits.
		 * 
		 * @param index checked index
		 * @return <code>true</code> if the index is valid, otherwise
		 * 	<code>false</code>
		 */
		boolean isAllowed(int index) {
			return (index >= 0) && (index < n);
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
