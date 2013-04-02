package balle.brick.presentation;

import balle.brick.BrickController;

import lejos.nxt.Button;

public class Dribble {


	public static void main(String[] args) {

		BrickController controller = new BrickController();
		
		controller.dribblersOn();
		
		// If the button hasn't been pressed, busywait
		while (Button.ENTER.isUp()) {}
		
		controller.dribblersOff();
		controller.muxOff();
		
	}

}
