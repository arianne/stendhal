/****************************************************************/
/*                      LoginDialog	                            */
/*                                                              */
/****************************************************************/
package games.stendhal.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import games.stendhal.client.*;
import marauroa.client.*;

/**
 * Summary description for LoginDialog
 *
 */
public class LoginDialog extends JDialog
{
	// Variables declaration
	private JLabel UsernameLabel;
	private JLabel ServerLabel;
	private JLabel PasswordLabel;
	private JTextField UsernameField;
	private JPasswordField PasswordField;
	private JComboBox ServerField;
	private JButton LoginButton;
	private JPanel contentPane;
	// End of variables declaration
	private StendhalClient client;
	private Frame frame;
	


    public LoginDialog(Frame w, StendhalClient client)
	{
		super(w);
		this.client=client;
		frame=w;
		initializeComponent();
		//
		// TODO: Add any constructor code after initializeComponent call
		//

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
		UsernameLabel = new JLabel();
		ServerLabel = new JLabel();
		PasswordLabel = new JLabel();
		UsernameField = new JTextField();
		PasswordField = new JPasswordField();
		ServerField = new JComboBox();
		LoginButton = new JButton();
		contentPane = (JPanel)this.getContentPane();

		//
		// UsernameLabel
		//
		UsernameLabel.setText("Type your username");
		//
		// ServerLabel
		//
		ServerLabel.setText("Choose your Stendhal Server");
		//
		// PasswordLabel
		//
		PasswordLabel.setText("Type your password");
		//
		// ServerField
		//
		ServerField.addItem("stendhal.game-server.cc");
		ServerField.addItem("localhost");

		//
		// LoginButton
		//
		LoginButton.setText("Login to Server");
		LoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				LoginButton_actionPerformed(e);
			}

		});
		//
		// contentPane
		//
		contentPane.setLayout(null);
		contentPane.setBorder(BorderFactory.createEtchedBorder());
        addComponent(contentPane, ServerLabel,   10,10,190,22);
        addComponent(contentPane, UsernameLabel, 10,45,190,20);
		addComponent(contentPane, PasswordLabel, 10,70,190,20);
        addComponent(contentPane, ServerField,   200,10,190,22);
        addComponent(contentPane, UsernameField, 200,45,190,20);
		addComponent(contentPane, PasswordField, 200,70,190,20);
		
		addComponent(contentPane, LoginButton,   200,120,190,30);
		//
		// LoginDialog
		//
		this.setTitle("Login to Server");
		this.setLocation(new Point(202, 124));
		this.setResizable(false);
		this.setSize(new Dimension(410, 190));
	}

	/** Add Component Without a Layout Manager (Absolute Positioning) */
	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	{
		c.setBounds(x,y,width,height);
		container.add(c);
	}

	private void LoginButton_actionPerformed(ActionEvent e)
	{
        String username=UsernameField.getText();
        String password=new String(PasswordField.getPassword());
        String server=(String)ServerField.getSelectedItem();

    try
      {
      client.connect(server,32160);
      }
    catch(Exception ex)
      {
      JOptionPane.showMessageDialog(this, "Stendhal can't find a Internet connection for getting online");
      System.exit(0);
      }

    try
      {
      if(client.login(username,password)==false)
        {
        JOptionPane.showMessageDialog(this, "Invalid username or password","Login status",JOptionPane.ERROR_MESSAGE);
        }
      else
        {
        this.setVisible(false);
        frame.setVisible(false);
        stendhal.doLogin=true;
        }
      }
    catch(ariannexpTimeoutException ex)
      {
      JOptionPane.showMessageDialog(this, "Can't connect to server. Server down?","Login status",JOptionPane.ERROR_MESSAGE);
      }
    }
}
