package games.stendhal.client.gui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * User: lsoubrev122203
 * Date: May 9, 2005
 * Time: 10:02:40 AM
 */

public class KTextEdit extends JPanel
  {
  private static final int TEXT_SIZE = 11;
  private static final Color HEADER_COLOR = Color.gray;
  private JTextPane textPane;
  private JScrollPane paneScrollPane;
  private int lineNumber;

  /**
   * Basic Constructor
   */
  public KTextEdit()
    {
    buildGUI();
    }

  /**
   * This method builds the Gui
   */
  private void buildGUI()
    {
    textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setAutoscrolls(true);
    initStylesForTextPane(textPane);
    setLayout(new BorderLayout());
    paneScrollPane = new JScrollPane(textPane);
    add(paneScrollPane, BorderLayout.CENTER);
    }

  /**
   * @param textPane the active text component
   */
  private void initStylesForTextPane(JTextPane textPane)
    {
    //Initialize the basics styles.
    Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    Style regular = textPane.addStyle("regular", def);
    StyleConstants.setFontFamily(def, "Dialog");
    StyleConstants.setFontSize(regular, TEXT_SIZE);
    Style s = textPane.addStyle("normal", regular);
    StyleConstants.setBold(s, true);
    StyleConstants.setForeground(s, HEADER_COLOR);
    s = textPane.addStyle("header", regular);
    StyleConstants.setItalic(s, true);
    StyleConstants.setFontSize(s, TEXT_SIZE);
    StyleConstants.setForeground(s, HEADER_COLOR);
    }

  /**
   * @param desiredColor the color with wich the text must be colored
   * @return the colored style
   */
  public Style getColor(Color desiredColor)
    {
    Style s = textPane.getStyle("normal");
    StyleConstants.setForeground(s, desiredColor);
    return s;
    }

  /**
   * clear the text
   */
  public void clearText()
    {
    textPane.setText("");
    }

  /**
   * insert a header
   */
  public void insertHeader(String header)
    {
    Document doc = textPane.getDocument();
    try 
      {
      if(header.length() > 0)
        {
        doc.insertString(doc.getLength(), "<" + header + "> ", textPane.getStyle("header"));
        }
      } 
    catch (BadLocationException ble) 
      {
      System.err.println("Couldn't insert initial text.");
      }
    }

  /**
   * The implemented method
   *
   * @param header a string with the header name
   * @param line   a string representing the line to be printed
   * @param color  the desired color
   */
  public void addLine(String header, String line, Color color)
    {
    Document doc = textPane.getDocument();
    try 
      {
      insertHeader(header);
      doc.insertString(doc.getLength(), line + "\r\n", getColor(color));
      textPane.setCaretPosition(textPane.getDocument().getLength ());
      lineNumber++;
      } 
    catch (BadLocationException ble) 
      {
      System.err.println("Couldn't insert initial text.");
      }
    }

  /**
   * give the number of inserted lines
   * @return the number of inserted lines
   */
  public int getLineNumber()
    {
    return lineNumber;
    }

  /**
   * @param line  a string representing the line to be printed
   * @param color the desired color
   */
  public void addLine(String line, Color color)
    {
    addLine("", line, color);
    }

  /**
   * Da main to make unit tests
   */
  public static void main(String args[])
    {
    /* BUG: This short example doesn't work. It doesn't throw any exception or anything.
     *  It just doesn't work. If you remove all teh edit.addLine but one it works.
     *  Can anyone with Swing/AWT abilities have a look to it? */
    JFrame frame = new JFrame("KTextEdit Test-Frame");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    KTextEdit edit = new KTextEdit();
    frame.getContentPane().add(edit);
    edit.addLine("Well, there is really not much to tell about. !", Color.red);
    edit.addLine("Well, there is really not much to tell about. !", Color.blue);
    edit.addLine("Well, there is really not much to tell about. !", Color.green);
    edit.addLine("Well, there is really not much to tell about. !", new Color(240, 123, 56));
    edit.addLine("Well, there is really not much to tell about. !", Color.yellow);

    edit.addLine("Shaku", "Well, there is really not much to tell about. !", Color.red);
    edit.addLine("Keanu", "Well, there is really not much to tell about. !", Color.blue);
    edit.addLine("Bob", "Well, there is really not much to tell about. !", Color.green);
    edit.addLine("Dragon", "Well, there is really not much to tell about. !", new Color(240, 123, 56));
    edit.addLine("Little ant", "Well, there is really not much to tell about. !", Color.yellow);

    frame.setPreferredSize(new Dimension(400, 200));
    frame.pack();
    frame.setVisible(true);
    }
  }

