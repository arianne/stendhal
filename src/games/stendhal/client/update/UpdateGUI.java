package games.stendhal.client.update;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class UpdateGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane;

	private JPanel jPanel;

	private JProgressBar jProgressBar;

	private JButton jButton;

	private JButton jButton1;

	private JEditorPane jEditorPane;

	/**
	 * This method initializes jPanel.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJProgressBar(), BorderLayout.NORTH);
			jPanel.add(getJButton(), BorderLayout.WEST);
			jPanel.add(getJButton1(), BorderLayout.EAST);
		}
		return jPanel;
	}

	/**
	 * This method initializes jProgressBar.
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setValue(50);
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jButton.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Download Update");
			jButton.setSelected(true);
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Start Old Version");
		}
		return jButton1;
	}

	/**
	 * This method initializes jEditorPane.
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new JEditorPane();
			jEditorPane.setEditable(false);
			jEditorPane.setText("<html>\n  <head>\n\n  </head>\n  <body>\n   <h1>Version 0.55.1</h1>\n  </body>\n</html>\n");
			jEditorPane.setContentType("text/html");
		}
		return jEditorPane;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				UpdateGUI thisClass = new UpdateGUI();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * Default constructor.
	 */
	public UpdateGUI() {
		super();
		initialize();
	}

	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.SOUTH);
			jContentPane.add(getJEditorPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

}
