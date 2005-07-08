package games.stendhal.client.gui;
import java.awt.Frame;
import java.net.URL;
import javax.swing.*;


public class ProgressBar extends JFrame {
    
    private Frame frame;
    private JPanel contentPane;
    private JProgressBar m_progressBar;
    
    public Thread m_run;
    private int m_sleepTime=210;
    private int m_stepSize=2;//makes for 10 normal steps. 100/10
    private int m_stepSizeMultiplier=1;
    private int m_stepCounter=0;//keeps track of how amny times it has lookp with a multiplier greater then 0
    
    private boolean m_con = true;//continue while true
    
    public ProgressBar(Frame w) {
        super("Connecting...");
        URL url = this.getClass().getClassLoader().getResource("data/StendhalIcon.png");
        setIconImage(new ImageIcon(url).getImage());
        this.frame = w;
        
        initializeComponents();
        
        this.setSize(200, 80);
        this.setLocationRelativeTo(frame);
    }
    
    private void initializeComponents() {
        contentPane = (JPanel)this.getContentPane();
        
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        contentPane.add(new JLabel("Connecting..."));
        contentPane.add(Box.createVerticalStrut(5));
        
        m_progressBar = new JProgressBar(0,100);
        m_progressBar.setStringPainted(false);
        m_progressBar.setValue(0);
        contentPane.add(m_progressBar);
    }
    
    public void setTotalTimeEstimate(int time) {
        m_stepSize = time/5250;
    }
    
    public void start() {
        m_run = new Thread() {
            int counter=0;
            public void run() {
                while (m_con && counter < 100) {
                    try {
                        Thread.sleep(m_sleepTime);
                        counter += m_stepSize*m_stepSizeMultiplier;
                        
                        Runnable updateRunner = new Runnable() {
                            public void run() {
                                m_progressBar.setValue(counter);
                            }
                        };
                        SwingUtilities.invokeLater(updateRunner);
                        
                        if (m_stepCounter <= 0) {
                            m_stepCounter=0;
                            m_stepSizeMultiplier=1;
                        }
                        m_stepCounter--;
                    }catch (InterruptedException ie) {}
                }
                ProgressBar.this.dispose();
            }
        };
        
        this.setVisible(true);
        m_run.start();
    }
    
    public void step() {//temporary speed up bar
        m_stepCounter = 3;
        m_stepSizeMultiplier=2;
    }
    
    public void finish() {
        m_stepCounter = 20;//speed up to quickly finish
        m_stepSizeMultiplier=2;
        m_sleepTime = 15;
    }
    
    public void cancel() {//exit quickly
        m_con = false;
        this.dispose();
    }
    
}
