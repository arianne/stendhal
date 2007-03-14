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
package games.stendhal.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * Displays a credits dialog box
 */
public class CreditsDialog extends JDialog {
	private static final Logger logger = Logger.getLogger(CreditsDialog.class);
	private static String[] credits = { "Thanks everyone! Credits goes to: ",
		"",
		"",
		"Arlindo",
		"he knows who ate all the pies",
		"",
		"Anna, George and Jens",
		"the bored children of Ados Park",
		"",
		"Balduin",
		"for appreciating the fine items we bring him",
		"",
		"Bario",
		"we hope we keep him warm",
		"",
		"Bill the Bookie",
		"who helps us win ... and lose",
		"",
		"Carmen",
		"for her gentle healing",
		"",
		"Ceryl",
		"for his wise words",
		"",
		"Conual",
		"making example of himself in Jail",
		"",
		"Coralia",
		"whose customers are all out of town",
		"",
		"Dagobert",
		"who keeps our money safe",
		"",
		"Debuggera",
		"for insisting on taking the garbage out",
		"",
		"Diogenes",
		"for providing advertisement",
		"",
		"Dr. Feelgood",
		"for his moderately priced potions",
		"",
		"Eonna",
		"the housewife who thinks we're heroes",
		"",
		"Elodrin",
		"even if he pays more to the elves he buys from",
		"",
		"Erna",
		"for baking our daily bread",
		"",
		"Fidorea",
		"for her fabulous costumes",
		"",
		"Gamblos",
		"who tests our thinking",
		"",
		"Hackim Easso",
		"for helping Xoderos the blacksmith",
		"",
		"Haizen",
		"for not summoning a red dragon",
		"",
		"Hayunn Naratha",
		"for his tales of his adventures",
		"",
		"Henry",
		"who despite his fear, has something to give",
		"",
		"Hogart",
		"although his stories keep us awake at night",
		"",
		"Ilisa",
		"for her healing and wise words",
		"",
		"Io Flotto",
		"who knows what we want before we ask",
		"",
		"Jenny",
		"who mills our grain",
		"",
		"Julius",
		"for protecting Ados against pillagers, but letting us through",
		"",
		"Jynath",
		"the witch who'll heal - for a price",
		"",
		"Katinka",
		"for looking after the animals",
		"",
		"Ketteh Wehoh",
		"for her advice on decorum",
		"",
		"Leander",
		"and his wonderful sandwiches",
		"",
		"Loretta",
		"for paying adventurers to bring essential resources",
		"",
		"Lorithien",
		"the only friendly elf in Nalwor",
		"",
		"Maerion",
		"even though he doesn't seem to tell the whole truth",
		"",
		"Marcus",
		"the jailkeeper who keeps Semos safe",
		"",
		"Margaret",
		"whose lovely food and drink liven up the Inn",
		"",
		"Maria",
		"she's on hand to provide refreshment",
		"",
		"Markovich",
		"for being the only vampire we aren't afraid of",
		"",
		"Mayor Sakhs",
		"for giving us something to do each day",
		"",
		"McPegleg",
		"for his 'business' .. we won't say what",
		"",
		"Monogenes",
		"for showing us the way",
		"",
		"Nishiya",
		"whose sheep always grow strong",
		"",
		"Nomyr Ahba",
		"for protecting Semos against a bunch of rats",
		"",
		"Ouchit",
		"his bow and arrows fly true",
		"",
		"Plink",
		"whom we were glad we could help",
		"",
		"Ricardo",
		"luck be a lady, tonight",
		"",
		"Sally",
		"for keeping her fire burning bright",
		"",
		"Sato",
		"who buys well fed sheep",
		"",
		"Sergeant James",
		"for rewarding those as brave as he is",
		"",
		"Sten Tanquilos",
		"who guards the basement of Semos Jail",
		"",
		"Stefan",
		"he endures a boring and thankless guard on the bridge",
		"",
		"Susi",
		"for being our friend at the Mines",
		"",
		"Tad",
		"for being the first friendly voice we hear",
		"",
		"Thanatos",
		"who bails us when the going gets tough",
		"",
		"Thonatus",
		"for challenging adventurers, no matter what skill level",
		"",
		"Tor'Koom",
		"for looking after our fat sheep (we hope...)",
		"",
		"Xin Blanca",
		"for his small business in the Inn",
		"",
		"Xoderos",
		"a wonderful blacksmith",
		"",
		"Zynn Iwuhos",
		"for giving us great knowledge",
		"",
		"",
		"Miguel Angel Blanch Lardin",
		"For without him, we'd wouldn't have this wonderful game!",
		"",
		"All contributors out there who keep the project going",
		"",
		"And finally, you, for choosing to download this game and (hopefully) spread the word about it"};
	private ScrollerPanel sp;
	private JPanel buttonPane = new JPanel();
	private JButton closeButton = new JButton("Close");
	
	private Color backgroundColor = Color.white;
	
	private Font textFont = new Font("SansSerif", Font.BOLD, 12);
	private Color textColor = new Color(85, 85, 85);
	
	public CreditsDialog(Frame owner) throws HeadlessException {
		super(owner, true);
		initGUI(owner);
		logger.debug("about dialog initialized");
		eventHandling();
		logger.debug("about dialog event handling ready");

		this.setTitle("Stendhal Credits");
//		this.setResizable(false);
//		this.pack();
		if (owner != null) {
			this.setLocationRelativeTo(owner);
			this.setSize(owner.getSize());
		} else {
			this.setLocationByPlatform(true);
			this.setSize(600, 420);
		}
		this.setVisible(true);
	}
	
	private void initGUI(Frame owner) {
		this.getContentPane().setLayout( new BorderLayout());
		this.getContentPane().setBackground( backgroundColor );
		sp = new ScrollerPanel( credits, textFont, 0, textColor, backgroundColor, 20 );		
		
		buttonPane.setOpaque(false);
		buttonPane.add( closeButton );

		this.getContentPane().add( sp, BorderLayout.CENTER );
		this.getContentPane().add( buttonPane, BorderLayout.SOUTH);
	}
	
	/**
	 * setting up the listeners an event handling
	 */
	private void eventHandling() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
	}
	
	/**
	 * exit Credits Dialog
	 */
	private void exit() {
		sp.stop();
		this.setVisible( false );
		this.dispose();
		logger.debug("about dialog closed");
	}
	
	
	/*public static void main(String[] args) {
		new CreditsDialog(null);
	}*/
}
