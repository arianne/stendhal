package games.stendhal.client.gui;

import java.awt.*;
import javax.swing.*;
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
	// End of variables declaration


  public GameLogDialog(Frame w)
	{
		super(w);
		initializeComponent(w);

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
 
  public void addLine(String line)
    {
    jTextArea.append(line+"\n");
    jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
    }
  }
