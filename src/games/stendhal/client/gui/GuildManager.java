/*
 * GuildManager.java
 *
 * Created on April 11, 2007, 2:01 PM
 */

package games.stendhal.client.gui;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.User;

import javax.swing.JOptionPane;

import marauroa.common.game.RPAction;

/**
 * 
 * @author timothyb89
 */
public class GuildManager extends javax.swing.JFrame {
	private static final long serialVersionUID = -1607102841664745919L;

	/** Creates new form GuildManager. */
	public GuildManager() {
		initComponents();
		client = StendhalClient.get();
	}

	private final StendhalClient client;

	private User playerEntity;

	
	private void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		guildTextBoxCreate = new javax.swing.JTextField();
		jButton1 = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();
		jButton2 = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel3 = new javax.swing.JLabel();
		jSeparator2 = new javax.swing.JSeparator();
		invitePlayerName = new javax.swing.JTextField();
		jButton3 = new javax.swing.JButton();
		jLabel4 = new javax.swing.JLabel();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Guild Management");
		jLabel1.setFont(new java.awt.Font("Dialog", 3, 18));
		jLabel1.setText("Stendhal Guild Management");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(10, 10, 270, 22);

		guildTextBoxCreate.setText("Guild Name Here");
		guildTextBoxCreate.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(final java.awt.event.MouseEvent evt) {
				guildTextBoxCreateMouseClicked(evt);
			}
		});

		getContentPane().add(guildTextBoxCreate);
		guildTextBoxCreate.setBounds(10, 40, 280, 19);

		jButton1.setText("Create");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		getContentPane().add(jButton1);
		jButton1.setBounds(290, 40, 74, 25);

		jLabel2.setText("<html>If you would like to join a guild, please talk to a member<br>in the guild you wish to join.");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(10, 70, 356, 30);

		jButton2.setText("Leave Guild");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		getContentPane().add(jButton2);
		jButton2.setBounds(10, 180, 106, 25);

		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(0, 0, 50, 10);

		jLabel3.setText("<html>Click here to leave your current guild.");
		getContentPane().add(jLabel3);
		jLabel3.setBounds(120, 180, 234, 15);

		getContentPane().add(jSeparator2);
		jSeparator2.setBounds(0, 0, 50, 10);

		invitePlayerName.setText("Player Name");
		getContentPane().add(invitePlayerName);
		invitePlayerName.setBounds(10, 110, 290, 19);

		jButton3.setText("Invite");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				jButton3ActionPerformed(evt);
			}
		});

		getContentPane().add(jButton3);
		jButton3.setBounds(300, 110, 69, 25);

		jLabel4.setText("Invite a player to a guild you are in or have created.");
		getContentPane().add(jLabel4);
		jLabel4.setBounds(10, 140, 320, 15);

		pack();
	} 
	private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) {
		// creates the guild for the player
		createGuild(guildTextBoxCreate.getText());
		JOptionPane.showMessageDialog(null, "You have created the guild \""
				+ guildTextBoxCreate.getText() + "\".");
	} 

	private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { 
		final String guild = playerEntity.getGuild();
		inviteToGuild(invitePlayerName.getText(), guild);
		JOptionPane.showMessageDialog(null, "You have invited \""
				+ invitePlayerName.getText() + "\" to the \"" + guild
				+ "\" guild.");
	} 

	private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { 
		removeFromGuild("user");
		JOptionPane.showMessageDialog(null,
				"You have been removed from your old guild. You may now join a new one.");
	} 

	private void guildTextBoxCreateMouseClicked(final java.awt.event.MouseEvent evt) {
		// erase text
		guildTextBoxCreate.setText("");
	} 

	/**
	 * Create guild.
	 * @param guildname 
	 */
	private void createGuild(final String guildname) {
		if (client == null) {
			/** If running standalone, just give an error */
			JOptionPane.showMessageDialog(
					null,
					"It seems you are running this standalone. Please run it from the normal Stendhal game.");
			return;
		}

		final RPAction rpaction = new RPAction();
		rpaction.put("type", "createguild");
		rpaction.put("value", guildname);
		client.send(rpaction);
	}

	/**
	 * Invite to guild.
	 * @param user 
	 * @param guildName 
	 */
	private void inviteToGuild(final String user, final String guildName) {
		if (client == null) {
			// If running standalone, give an error
			JOptionPane.showMessageDialog(
					null,
					"It seems that you are running this standalone. Please from it from the normal Stendhal game.");
			return;
		}

		final RPAction action = new RPAction();
		action.put("type", "inviteGuild");
		action.put("playername", user);
		action.put("guildname", guildName);
		client.send(action);
	}

	/**
	 * Removes player from guild.
	 * @param user 
	 */
	private void removeFromGuild(final String user) { 
		// we shouldn't seed that string, but....
		if (client == null) {
			// If running standalone, give an error
			JOptionPane.showMessageDialog(
					null,
					"It seems that you are running this standalone. Please from it from the normal Stendhal game.");
			return;
		}
		final RPAction remove = new RPAction();
		remove.put("type", "removeFromGuild");
		client.send(remove);

	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(final String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GuildManager().setVisible(true);
			}
		});
	}

	private javax.swing.JTextField guildTextBoxCreate;

	private javax.swing.JTextField invitePlayerName;

	private javax.swing.JButton jButton1;

	private javax.swing.JButton jButton2;

	private javax.swing.JButton jButton3;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel2;

	private javax.swing.JLabel jLabel3;

	private javax.swing.JLabel jLabel4;

	private javax.swing.JSeparator jSeparator1;

	private javax.swing.JSeparator jSeparator2;
	

}
