package src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class systemRunnable implements Runnable{
	int pitch;
	
	public void setPitch(int pi){
		this.pitch = pi;
	}
	public int getPitch()
	{
	     //include validation, logic, logging or whatever you like here
	    return this.pitch;
	}
	
	public void run() {
		int pi = getPitch();
if(pi==0){
		// Code for Pitch 0.
		Process p = null;
		Process vision = null;
		try {
			// FIRST COMMAND. EDIT HERE.
			vision = Runtime.getRuntime().exec(
					new String[] { "bash", "-c",
							"sh run.sh -p0 " });

		} catch (IOException ex) {
			Logger.getLogger(starterUI.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		// Now read the output
		StringBuilder out2 = new StringBuilder();
		BufferedReader br2 = new BufferedReader(new InputStreamReader(
				vision.getInputStream()));
		String line = null, previous = null;
		try {
			while ((line = br2.readLine()) != null) {
				if (!line.equals(previous)) {
					previous = line;
					out2.append(line).append('\n');
					System.out.println(line);
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(starterUI.class.getName()).log(
					Level.SEVERE, null, ex);
		}
}	
//^ if ends here. 	
else if (pi==1){
	// Code for Pitch 0.
			Process p = null;
			Process vision = null;
			try {
				// FIRST COMMAND. EDIT HERE.
				vision = Runtime.getRuntime().exec(
						new String[] { "bash", "-c",
								"sh run.sh -p1 " });

			} catch (IOException ex) {
				Logger.getLogger(starterUI.class.getName()).log(
						Level.SEVERE, null, ex);
			}
			// Now read the output
			StringBuilder out2 = new StringBuilder();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(
					vision.getInputStream()));
			String line = null, previous = null;
			try {
				while ((line = br2.readLine()) != null) {
					if (!line.equals(previous)) {
						previous = line;
						out2.append(line).append('\n');
						System.out.println(line);
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(starterUI.class.getName()).log(
						Level.SEVERE, null, ex);
			}
}
	}

	public static void main(String args[]) {
	        (new Thread(new visionRunnable())).start();
	    }
}
