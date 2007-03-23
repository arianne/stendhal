/**
 * 
 */
package games.stendhal.test.gui;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

/**
 * @author mtotz
 * 
 */
public class Test {

	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame();
		frame.setBounds(0, 0, 500, 500);
		frame.setTitle("Title");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JDesktopPane desktop = new JDesktopPane();
		desktop.setDragMode(JDesktopPane.LIVE_DRAG_MODE);

		SInternalFrame internalFrame = new SInternalFrame("internal");
		internalFrame.setLocation(20, 20);
		internalFrame.setSize(400, 400);
		internalFrame.setVisible(true);

		SButton button = new SButton("test");
		SLabel label = new SLabel("test", SwingConstants.CENTER);

		internalFrame.getContentPane().setLayout(new BorderLayout());
		internalFrame.getContentPane().add(button, BorderLayout.NORTH);
		internalFrame.getContentPane().add(label, BorderLayout.CENTER);
		internalFrame.moveToFront();
		internalFrame.setSelected(true);

		desktop.add(internalFrame);
		frame.setContentPane(desktop);
		frame.setVisible(true);

		System.out.println(internalFrame.getUI());

		System.out.println("done");

	}

}
