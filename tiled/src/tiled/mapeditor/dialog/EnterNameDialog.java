/**
 * 
 */
package tiled.mapeditor.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * A Dialog which simply asks for a name.
 * @author mtotz
 */
public class EnterNameDialog extends JDialog
{
  private static final long serialVersionUID = 1L;
  
  private String text;

  private JTextField textField;
  
  public EnterNameDialog(JFrame parent, String text)
  {
    super(parent, text, false);
    
    Container contentPane = getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
    
    contentPane.add(new JLabel(text));
    textField = new JTextField("text");
    contentPane.add(textField);
    JButton okButton = new JButton("Ok");
    okButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        EnterNameDialog.this.text = textField.getText();
        EnterNameDialog.this.setVisible(false);
      }
    });
    contentPane.add(okButton,BorderLayout.SOUTH);
    setVisible(true);
  }

  /** returns the entered text */
  public String getText()
  {
    return text;
  }
}
