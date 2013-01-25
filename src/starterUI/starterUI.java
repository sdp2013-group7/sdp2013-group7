package src;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class starterUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					starterUI window = new starterUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public starterUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 540, 82);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblSystemStart = new JLabel("System Start");
		lblSystemStart.setBounds(12, 17, 109, 15);
		frame.getContentPane().add(lblSystemStart);
		
		JButton btnPitch = new JButton("Pitch 0");
		btnPitch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				//Code for Pitch 0.
                Process p = null;
                Process vision = null;
        try {
        	//FIRST COMMAND. EDIT HERE.
            p = Runtime.getRuntime().exec(new String[]{"bash","-c","sh run.sh -p0 "});
            vision = Runtime.getRuntime().exec(new String[]{"bash","-c","sh vision/runvision.sh -p0 -r"});
            
        } catch (IOException ex) {
            Logger.getLogger(starterUI.class.getName()).log(Level.SEVERE, null, ex);
        }
         //Now read the output
                StringBuilder out = new StringBuilder();
                StringBuilder out2 = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(vision.getInputStream()));
                String line = null, previous = null;
        try {
            while ((line = br.readLine()) != null){
                if (!line.equals(previous)) {
                    previous = line;
                    out.append(line).append('\n');
                    System.out.println(line);
            }
            }
        } catch (IOException ex) {
            Logger.getLogger(starterUI.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
        
			}
			
		});
		btnPitch.setBounds(116, 12, 117, 25);
		frame.getContentPane().add(btnPitch);
		
		JButton btnPitch_1 = new JButton("Pitch 1");
		btnPitch_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				//Code for Pitch 1.
                Process p = null;
                Process vision = null;
        try {
        	//FIRST COMMAND. EDIT HERE.
            p = Runtime.getRuntime().exec(new String[]{"bash","-c","sh run.sh -p1 "});
            vision = Runtime.getRuntime().exec(new String[]{"bash","-c","sh vision/runvision.sh -p1 -r"});
            
        } catch (IOException ex) {
            Logger.getLogger(starterUI.class.getName()).log(Level.SEVERE, null, ex);
        }
         //Now read the output
                StringBuilder out = new StringBuilder();
                StringBuilder out2 = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(vision.getInputStream()));
                String line = null, previous = null;
        try {
            while ((line = br.readLine()) != null){
                if (!line.equals(previous)) {
                    previous = line;
                    out.append(line).append('\n');
                    System.out.println(line);
            }
            }
        } catch (IOException ex) {
            Logger.getLogger(starterUI.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
        
			}
			
		});
		btnPitch_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnPitch_1.setBounds(245, 12, 117, 25);
		frame.getContentPane().add(btnPitch_1);
		
		JButton btnStartDummy = new JButton("Start Dummy");
		btnStartDummy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				//Code for dummy System start.
                Process p = null;
                Process vision = null;
        try {
        	//FIRST COMMAND. EDIT HERE.
            p = Runtime.getRuntime().exec(new String[]{"bash","-c","sh run.sh -p0 -d"});
            vision = Runtime.getRuntime().exec(new String[]{"bash","-c","sh vision/runvision.sh -p0 -r"});
            
        } catch (IOException ex) {
            Logger.getLogger(starterUI.class.getName()).log(Level.SEVERE, null, ex);
        }
         //Now read the output
                StringBuilder out = new StringBuilder();
                StringBuilder out2 = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(vision.getInputStream()));
                String line = null, previous = null;
        try {
            while ((line = br.readLine()) != null){
                if (!line.equals(previous)) {
                    previous = line;
                    out.append(line).append('\n');
                    System.out.println(line);
            }
            }
        } catch (IOException ex) {
            Logger.getLogger(starterUI.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
        
			}
		});
		btnStartDummy.setBounds(374, 12, 150, 25);
		frame.getContentPane().add(btnStartDummy);
	}
}
