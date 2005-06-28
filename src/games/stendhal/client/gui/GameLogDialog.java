package games.stendhal.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
/**
 * Summary description for GameLogDialog
 *
 */
public class GameLogDialog extends JDialog
{
	// Variables declaration
  private KTextEdit jEditArea;
  private JTextArea jTextArea;

	private JScrollPane jScrollPane;
	private JPanel contentPane;
  private JTextField playerChat;
	// End of variables declaration


  public GameLogDialog(Frame w, JTextField textField)
	  {
		super(w);
		initializeComponent(w);
    playerChat=textField;

    addFocusListener(new FocusListener()
      {
      public void focusGained(FocusEvent e)
        {
        playerChat.requestFocus();
        }
            
      public void focusLost(FocusEvent e)
        {
        }
      });     
    
    this.setVisible(true);
	  }

  private void initializeComponent(Frame w)
	  {
    jEditArea = new KTextEdit();

		contentPane = (JPanel)this.getContentPane();
		
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
    addComponent(contentPane, jEditArea, 1,0,(int)w.getSize().getWidth()-8,171);

		this.setTitle("Game chat and events log");
		
		Dimension size=w.getSize();
		Point location=w.getLocation();		
		
    this.setLocation(new Point((int)location.getX(), (int)(location.getY()+size.getHeight())));
    this.setSize(new Dimension((int)w.getSize().getWidth(), 200));
	  }

	/** Add Component Without a Layout Manager (Absolute Positioning) */
	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	  {
		c.setBounds(x,y,width,height);
		container.add(c);
  	}
 
  public void addLine(String header, String line, Color color)
    {
    jEditArea.addLine(header, line,color);
    }

  public void addLine(String header, String line)
    {
    jEditArea.addLine(header, line,Color.black);
    }

  public void addLine(String line, Color color)
    {
    jEditArea.addLine(line,color);
    }
    
  public void addLine(String line)
    {
    addLine(line,Color.black);
    }
  }
