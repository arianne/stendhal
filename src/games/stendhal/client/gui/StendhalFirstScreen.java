/****************************************************************/
/*                      LoginGUI	                            */
/*                                                              */
/****************************************************************/
package games.stendhal.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import games.stendhal.client.*;


/**
 * Summary description for LoginGUI
 *
 */
public class StendhalFirstScreen extends JFrame
{
	// Variables declaration
	private JButton loginButton;
	private JButton createAccountButton;
	private JButton exitButton;
	private JPanel contentPane;
	// End of variables declaration
    private StendhalClient client;
    private Image background;


    public StendhalFirstScreen(StendhalClient client)
	{
		super();
		this.client=client;
		//
		// TODO: Add any constructor code after initializeComponent call
		//
        ImageIcon imageIcon = new ImageIcon("data/StendhalSplash.jpg");
        background=imageIcon.getImage();

        initializeComponent();
        
        this.setVisible(true);
	}
	

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always regenerated
	 * by the Windows Form Designer. Otherwise, retrieving design might not work properly.
	 * Tip: If you must revise this method, please backup this GUI file for JFrameBuilder
	 * to retrieve your design properly in future, before revising this method.
	 */
	private void initializeComponent()
	{
		loginButton = new JButton();
		createAccountButton = new JButton();
		exitButton = new JButton();
		this.setContentPane(new JPanel()
		  {
		  {
		  setOpaque(false);
		  this.setPreferredSize(new Dimension(640,480));
		  }
          public void paint(Graphics g) 
          {
          g.drawImage(background,0,0,this);
          super.paint(g);
          }
    
          });
        contentPane = (JPanel)this.getContentPane();

		//
		// loginButton
		//
		loginButton.setText("Login to Stendhal");
		loginButton.setToolTipText("Press this button to Login to a Stendhal server");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
            new LoginDialog(StendhalFirstScreen.this, client);
			}

		});
		//
		// createAccountButton
		//
		createAccountButton.setText("Create an account");
		createAccountButton.setToolTipText("Press this button to create an account on a stendhal server.");
        createAccountButton.setEnabled(false);
		createAccountButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
			}

		});
		//
		// exitButton
		//
		exitButton.setText("Exit");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}

		});
		
        addWindowListener(new WindowAdapter() 
          {
          public void windowClosing(WindowEvent e) 
            {
                System.exit(0);
            }
          });
          
        //
		// contentPane
		//
		contentPane.setLayout(null);
        addComponent(contentPane, loginButton, 220,340,200,32);
        addComponent(contentPane, createAccountButton, 220,380,200,32);
		addComponent(contentPane, exitButton, 220,420,200,32);
		//
		// LoginGUI
		//
        setTitle("Stendhal Java 2D");
        this.setLocation(new Point(100, 100));
        
        this.setIconImage(new ImageIcon("data/StendhalIcon.gif").getImage());
		pack();
	}

	/** Add Component Without a Layout Manager (Absolute Positioning) */
	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	{
		c.setBounds(x,y,width,height);
		container.add(c);
	}
}
