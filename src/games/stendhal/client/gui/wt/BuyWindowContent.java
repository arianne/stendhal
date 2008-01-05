/*
 * BuyWindowContent.java
 *
 * Created on April 22, 2007, 11:51 AM
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJButton;

import java.awt.CardLayout;

import javax.swing.JOptionPane;

/**
 *
 * @author timothyb89
 */
public class BuyWindowContent extends javax.swing.JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -1607102841664745919L;

	/**
	 * The UI.
	 */
	protected StendhalUI ui;

	/**
	 * The card name for CardLayout.
	 */
	private String card = "intro";

	/**
	 * The string to be built.
	 */
	private String buildMe;

	/**
	 * The amount of items.
	 */
	private int amount;

	/**
	 * If there is an extra option, are we using it (only buy at this point).
	 */
	private boolean buying;

	/**
	 * Our managedWindow.
	 */
	private BuyWindow bw;

	/** Creates new form BuyWindowContent. */
	public BuyWindowContent(StendhalUI ui, BuyWindow bw) {
		this.ui = ui;
		this.bw = bw;
		initComponents();
	}

	/**
	 * The hashmap is used to take an item name (such as "Knife ($15)") and
	 * translates it into something an NPC can understand (such as, simply,
	 * 'knife').
	 */
	// private HashMap items = new HashMap();
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	@SuppressWarnings("serial")
	private void initComponents() {
		WoodStyle style = new WoodStyle();
		nextButton = new StyledJButton(style);
		mainPanel = new javax.swing.JPanel();
		intro_panel = new javax.swing.JPanel();
		jLabel5 = new javax.swing.JLabel();
		jScrollPane4 = new javax.swing.JScrollPane();
		npc_chooser = new javax.swing.JList();
		food_drink = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		foodList = new javax.swing.JList();
		jLabel1 = new javax.swing.JLabel();
		xin_weapons = new javax.swing.JPanel();
		jScrollPane2 = new javax.swing.JScrollPane();
		xinSell = new javax.swing.JList();
		jLabel2 = new javax.swing.JLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		xinBuy = new javax.swing.JList();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		potions = new javax.swing.JPanel();
		jScrollPane5 = new javax.swing.JScrollPane();
		potionList = new javax.swing.JList();
		amountChooser = new javax.swing.JPanel();
		itemAmount = new javax.swing.JSpinner();
		jLabel6 = new javax.swing.JLabel();
		resetButton = new StyledJButton(style);

		setLayout(null);

		setMinimumSize(new java.awt.Dimension(400, 250));
		setOpaque(false);
		nextButton.setText("Next");
		nextButton.setEnabled(false);
		nextButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				nextButtonActionPerformed(evt);
			}
		});

		add(nextButton);
		nextButton.setBounds(330, 210, 62, 25);

		mainPanel.setLayout(new java.awt.CardLayout());

		mainPanel.setOpaque(false);
		intro_panel.setLayout(null);

		intro_panel.setOpaque(false);
		jLabel5.setText("Choose an NPC and click 'Continue':");
		intro_panel.add(jLabel5);
		jLabel5.setBounds(0, 10, 224, 15);

		npc_chooser.setModel(new javax.swing.AbstractListModel() {
			private String[] strings = { "Xin Blanca", "Food and Drink Selling",
					"Potions" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		npc_chooser
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						npc_chooserValueChanged(evt);
					}
				});

		jScrollPane4.setViewportView(npc_chooser);

		intro_panel.add(jScrollPane4);
		jScrollPane4.setBounds(20, 40, 125, 131);

		mainPanel.add(intro_panel, "intro");

		food_drink.setLayout(null);

		food_drink.setMaximumSize(new java.awt.Dimension(350, 225));
		food_drink.setOpaque(false);
		foodList.setModel(new javax.swing.AbstractListModel() {
			private String[] strings = { "beer ($10)", "wine ($15)", "flask ($5)",
					"cheese ($20)", "apple ($10)", "carrot ($10)",
					"meat ($40)", "ham ($80)" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane1.setViewportView(foodList);

		food_drink.add(jScrollPane1);
		jScrollPane1.setBounds(0, 0, 140, 200);

		jLabel1
				.setText("<html>Select a food or drink item from the <br>list and click 'Next'.");
		food_drink.add(jLabel1);
		jLabel1.setBounds(150, 10, 224, 30);

		mainPanel.add(food_drink, "food&drink");

		xin_weapons.setLayout(null);

		xin_weapons.setOpaque(false);
		xinSell.setModel(new javax.swing.AbstractListModel() {
			private String[] strings = { "knife ($15)", "club ($10)", "dagger ($25)",
					"wooden_shield ($25)", "dress ($25)",
					"leather_helmet ($25)", "cloak ($30)", "leather_legs ($35)" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		xinSell
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						xinSellValueChanged(evt);
					}
				});

		jScrollPane2.setViewportView(xinSell);

		xin_weapons.add(jScrollPane2);
		jScrollPane2.setBounds(0, 30, 135, 139);

		jLabel2.setText("Selling:");
		xin_weapons.add(jLabel2);
		jLabel2.setBounds(40, 10, 45, 15);

		xinBuy.setModel(new javax.swing.AbstractListModel() {
			private String[] strings = { "short_sword ($15)", "sword ($60)",
					"studded_shield ($20)", "studded_armor ($22)",
					"studded_legs ($20)", "chain_armor ($29)",
					"chain_helmet ($25)", "chain_legs ($27)" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		xinBuy
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						xinBuyValueChanged(evt);
					}
				});

		jScrollPane3.setViewportView(xinBuy);

		xin_weapons.add(jScrollPane3);
		jScrollPane3.setBounds(150, 30, 136, 139);

		jLabel3.setText("Buying:");
		xin_weapons.add(jLabel3);
		jLabel3.setBounds(180, 10, 46, 15);

		jLabel4
				.setText("<html>Please choose an<br>item from <u><b>one</u></b><br>of the lists before<br>continuing.");
		xin_weapons.add(jLabel4);
		jLabel4.setBounds(290, 40, 112, 60);

		mainPanel.add(xin_weapons, "xin");

		potions.setLayout(null);

		potions.setOpaque(false);
		potionList.setModel(new javax.swing.AbstractListModel() {
			private String[] strings = { "minor_potion ($100)", "potion ($250)",
					"greater_potion ($500)", "mega_potion", "antidote ($40)",
					"greater_antidote ($40)" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane5.setViewportView(potionList);

		potions.add(jScrollPane5);
		jScrollPane5.setBounds(0, 0, 170, 190);

		mainPanel.add(potions, "potions");

		amountChooser.setLayout(null);

		amountChooser.setOpaque(false);
		amountChooser.add(itemAmount);
		itemAmount.setBounds(20, 50, 130, 20);

		jLabel6
				.setText("Please choose the amount of items you wish to buy or sell.");
		amountChooser.add(jLabel6);
		jLabel6.setBounds(20, 10, 370, 15);

		mainPanel.add(amountChooser, "amountChooser");

		add(mainPanel);
		mainPanel.setBounds(0, 0, 410, 200);

		resetButton.setText("Reset");
		resetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetButtonActionPerformed(evt);
			}
		});

		add(resetButton);
		resetButton.setBounds(20, 210, 88, 25);

	} // </editor-fold>//GEN-END:initComponents

	private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_resetButtonActionPerformed
		// resets everything
		card = "intro";
		amount = 0;
		buying = false;
		buildMe = null;
		itemAmount.setValue(0);

		// set card
		CardLayout cl = (CardLayout) (mainPanel.getLayout());
		cl.show(mainPanel, card);
	} // GEN-LAST:event_resetButtonActionPerformed

	private void xinBuyValueChanged(javax.swing.event.ListSelectionEvent evt) { // GEN-FIRST:event_xinBuyValueChanged
		if (xinSell.getSelectedValue() != null) {
			JOptionPane.showMessageDialog(null,
					"You can only choose one item from 1 category at a time!");
			xinBuy.setSelectedValue(null, true);
			xinSell.setSelectedValue(null, true);
		}
	} // GEN-LAST:event_xinBuyValueChanged

	private void xinSellValueChanged(javax.swing.event.ListSelectionEvent evt) { // GEN-FIRST:event_xinSellValueChanged
		if (xinBuy.getSelectedValue() != null) {
			JOptionPane.showMessageDialog(null,
					"You can only choose one item from 1 category at a time!");
			xinBuy.setSelectedValue(null, true);
			xinSell.setSelectedValue(null, true);
		}
	} // GEN-LAST:event_xinSellValueChanged

	private void npc_chooserValueChanged(
			javax.swing.event.ListSelectionEvent evt) { // GEN-FIRST:event_npc_chooserValueChanged
		nextButton.setEnabled(true);
	} // GEN-LAST:event_npc_chooserValueChanged

	private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_nextButtonActionPerformed

		/**
		 * CardLayout stuff
		 */
		CardLayout cl = (CardLayout) (mainPanel.getLayout());
		// put all the 'init' code for the cards here.
		if (card.contains("intro")) {
			card = String.valueOf(npc_chooser.getSelectedValue());
			// == Set cards (officially) here ==
			if (card.toLowerCase().contains("xin")) {
				card = "xin";
				System.out.println("Choose shop xin"); // for debug
			} else if (card.toLowerCase().contains("food")) {
				card = "food&drink";
				System.out.println("Choose shop food&drink"); // for debug
			}

			cl.show(mainPanel, card); // show the card

		} else if (card.contains("xin")) {
			if (xinSell.getSelectedValue() != null) {
				buildMe = String.valueOf(xinSell.getSelectedValue());
			} else {
				buildMe = String.valueOf(xinBuy.getSelectedValue()); // not
																		// getSelectedValues
																		// for a
																		// reason...
				buying = true;
			}

			card = "amountChooser";
			cl.show(mainPanel, card);
		} else if (card.contains("food")) {
			buildMe = String.valueOf(foodList.getSelectedValue());
			card = "amountChooser";
			cl.show(mainPanel, card);

		} else if (card.contains("potions")) {
			buildMe = String.valueOf(potionList.getSelectedValue());
			card = "amontChooser";
			cl.show(mainPanel, card);
		} else if (card.contains("amount")) {
			amount = Integer.parseInt(String.valueOf(itemAmount.getValue()));

			// finish
			String final_msg = null;
			if (buying) {
				final_msg = "sell";
			} else {
				final_msg = "buy";
			}
			// add the amounts
			final_msg += " " + String.valueOf(amount);
			// now for the item name...
			final_msg += " " + buildMe;

			// set the chat bar
			ui.setChatLine(final_msg);
			// ui.setChatLine("test");
			ui.addEventLine("Please tell an NPC what is now in your chat bar.");
			JOptionPane.showMessageDialog(null,
					"Please tell an NPC what is now in you chat bar.");
			bw.setVisible(false);

		}

	} // GEN-LAST:event_nextButtonActionPerformed
	// /**
	// *Adds items to the items hashmap
	// */
	// private void addToMap() {
	// // put entries here. maybe this isn't the best way to do things, but it
	// should work if everything is typed correctly
	// /*
	// // Xin shop
	// items.put("Knife ($15)", "knife");
	// items.put("Club ($10)", "club");
	// items.put("Dagger ($25)", "dagger");
	// items.put("Wooden Shield ($25)", "wooden_shield");
	// items.put("Dress ($25)", "dress");
	// items.put("Leather Helmet ($25)", "leather_helmet");
	// items.put("Cloak ($30)", "cloak");
	// items.put("Leather Legs ($35)", "leather_legs");
	// items.put("Short Sword ($15)", "short_sword");
	// items.put("Sword ($60)", "sword");
	// items.put("Studded Shield ($20)", "studded_shield");
	// items.put("Studded Armor ($22)", "studded_armor");
	// items.put("Studded Legs ($20)", "studded_legs");
	// items.put("Chain Armor ($29)", "chain_armor");
	// items.put("Chain Helmet ($25)", "chain_helmet");
	// items.put("Chain Legs ($27)", "chain_legs");
	//
	// // food&drink shop
	// items.put("Beer ($10)", "beer");
	// items.put("Wine ($15)", "wine");
	// items.put("Flask ($5)", "flask");
	// items.put("Cheese ($20)", "cheese");
	// items.put("Apple ($10)", "apple");
	// items.put("Carrot ($10)", "carrot");
	// items.put("Meat ($40)", "meat");
	// items.put("Ham ($80)", "ham");
	// */ //commented for other fix
	//
	// }

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel amountChooser;

	private javax.swing.JList foodList;

	private javax.swing.JPanel food_drink;

	private javax.swing.JPanel intro_panel;

	private javax.swing.JSpinner itemAmount;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel2;

	private javax.swing.JLabel jLabel3;

	private javax.swing.JLabel jLabel4;

	private javax.swing.JLabel jLabel5;

	private javax.swing.JLabel jLabel6;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JScrollPane jScrollPane2;

	private javax.swing.JScrollPane jScrollPane3;

	private javax.swing.JScrollPane jScrollPane4;

	private javax.swing.JScrollPane jScrollPane5;

	private javax.swing.JPanel mainPanel;

	private StyledJButton nextButton;

	private javax.swing.JList npc_chooser;

	private javax.swing.JList potionList;

	private javax.swing.JPanel potions;

	private StyledJButton resetButton;

	private javax.swing.JList xinBuy;

	private javax.swing.JList xinSell;

	private javax.swing.JPanel xin_weapons;
	// End of variables declaration//GEN-END:variables

}
