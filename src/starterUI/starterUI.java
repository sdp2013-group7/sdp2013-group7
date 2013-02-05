package src;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
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
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setSize(new Dimension(600, 150));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		// Make label and set size
		JLabel lblSystemStart = new JLabel("System Start");
		lblSystemStart.setBounds(12, 17, 109, 15);
		// Add label to frame
		frame.getContentPane().add(lblSystemStart);

		// First pitch 0
		JButton btnPitch = new JButton("Pitch 0");
		btnPitch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				//Run the system threaded.

				systemRunnable system = new systemRunnable();
				system.setPitch(0);
				Thread t1 = new Thread(system);
				t1.start();
				//Run the vision threaded.
				visionRunnable vision = new visionRunnable();
				vision.setPitch(0);
				Thread t2 = new Thread(vision);
				t2.start();

			}

		});
		btnPitch.setBounds(116, 12, 150, 25);
		frame.getContentPane().add(btnPitch);

		// Second Pitch (1)
		JButton btnPitch_1 = new JButton("Pitch 1");
		btnPitch_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnPitch_1.setBounds(245, 12, 150, 25);
		frame.getContentPane().add(btnPitch_1);

		// Dummy pitch
		JButton btnStartDummy = new JButton("Start Dummy");
		btnStartDummy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				//Run the system threaded.
				systemRunnable system = new systemRunnable();
				system.setPitch(1);
				system.run();
				//Run the vision threaded.
				visionRunnable vision = new visionRunnable();
				vision.setPitch(1);
				vision.run();
			}
		});

		btnStartDummy.setBounds(374, 12, 150, 25);
		frame.getContentPane().add(btnStartDummy);

		// Simulator
		JButton btnSimulator = new JButton("Start Simulator");
		btnSimulator.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// Code for dummy System start.
				Process p = null;
				try {
					// Run the command for the simulation
					p = Runtime.getRuntime().exec(
							new String[] { "bash", "-c", "sh run.sh -s" });
				} catch (IOException ex) {
					Logger.getLogger(starterUI.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				// Now read the output
				StringBuilder out = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(p
						.getInputStream()));
				String line = null, previous = null;
				try {
					while ((line = br.readLine()) != null) {
						if (!line.equals(previous)) {
							previous = line;
							out.append(line).append('\n');
							System.out.println(line);
						}
					}
				} catch (IOException ex) {
					Logger.getLogger(starterUI.class.getName()).log(
							Level.SEVERE, null, ex);
				}

			}
		});

		btnSimulator.setBounds(245, 60, 150, 25);
		frame.getContentPane().add(btnSimulator);
	}
}
