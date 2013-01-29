package balle.brick;

import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import balle.brick.BrickController;

/**
    Class that completes the first 
*/
public class Drive {
	
	// Define some constants :) nothing special here lol
    private static final int ROLL_SPEED = 400;
    private static final double ROLL_DISTANCE = 1.5;

    // This is to draw messages on the screen, treat it like a log
    private static void drawMessage(String message) {
        LCD.clear();
        LCD.drawString(message, 0, 0);
        LCD.refresh();
    }

    public static void main(String[] args) {

        // This class does all the controlling of the robot, 
        BrickController controller = new BrickController();
        boolean movingForward = false;
        int speed = ROLL_SPEED;
        double rollDistance = ROLL_DISTANCE;
        
        while (true) {
            // If it's not moving then begin to move the robot forward
            if (!movingForward) {
                drawMessage("Roll");
                movingForward = true;
                controller.forward(speed);
            }
            else {
                // Get the distance travelled
                float distance = controller.getTravelDistance();
                // The constant is important to set, because this is when it stops
                if (distance >= ROLL_DISTANCE) {
                    // How we stop moving... obvious
                    controller.stop();
                    drawMessage("Done!!!");
                    break;
                }
                else {
                    drawMessage(Float.toString(distance));
                }
            }
        } //end while
        
    }
}
