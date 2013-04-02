package balle.brick.presentation;

import balle.brick.BrickController;
import lejos.util.Delay;

public class ExtendTentacles {

	public static void main(String[] args) {

		BrickController controller = new BrickController();
		
		controller.extendBoth();
		
		Delay.msDelay(1000);
		
		controller.muxOff();		

	}

}
