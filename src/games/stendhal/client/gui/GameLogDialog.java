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
		jTextArea = new JTextArea();
		jScrollPane = new JScrollPane();
		contentPane = (JPanel)this.getContentPane();
		
		jTextArea.setLineWrap(true);

		jScrollPane.setViewportView(jTextArea);
    jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		contentPane.setLayout(null);
    addComponent(contentPane, jScrollPane, 1,0,(int)w.getSize().getWidth()-8,171);

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
 
  public void addLine(String line, Color color)
    {
    //NOTE:TODO: Fix this. It must change the color of the line just added.
    //jTextArea.setForeground(color);
    jTextArea.append(line+"\n");
    jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
    }
    
  public void addLine(String line)
    {
    addLine(line,Color.BLACK);
    }
  }
