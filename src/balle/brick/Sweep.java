package balle.brick;

public class Sweep {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BrickController control = new BrickController();
		
		control.registerSweep();
		try {
			Thread.sleep(1000);
		} catch (Exception e){
			// 
		}
	}

}
