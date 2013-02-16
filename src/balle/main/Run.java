package balle.main;

import java.io.*;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.lang.Integer;

import balle.bluetooth.Communicator;
import balle.controller.BluetoothController;

/*
    A simple class to control the robot over bluetooth by
    typing different commands on the command line. Kraken.nxj must
    be running on the robot before running this.
*/
public class Run {
    
    	public static void main(String[] args) {
    	    
    	    // Set up the bluetooth connection
    	    BluetoothController controller = new BluetoothController(new Communicator());
    	    
    	    try {
    	        Thread.sleep(500);
    	    } catch (InterruptedException e){
    	        System.out.println(e);
    	    }
    	    
    	    while (true) {
                //  open up standard input
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                String readLine = null;

                //  read the command from the command-line; need to use try/catch with the
                //  readLine() method
                try {
                   readLine = br.readLine();
                } catch (IOException ioe) {
                   System.out.println("IO error trying to read your name!");
                   System.exit(1);
                }
            	
            	String[] s = readLine.split(" ");
            	
            	String command = s[0];
            	
            	if (command.equals("kick")) {
            	    controller.kick();
            	} else if (command.equals("forward")) {
            	    int speed;
            	    try {
            	        speed = Integer.parseInt(s[1]);
            	    } catch (Exception e) {
            	        speed = 100;
            	    }
            	    controller.forward(speed);
            	} else if (command.equals("backward")) {
            	    int speed;
            	    try {
            	        speed = Integer.parseInt(s[1]);
            	    } catch (Exception e) {
            	        speed = 100;
            	    }
            	    controller.backward(speed);
            	} else if (command.equals("rotate")) {
            	    int speed;
            	    int angle;
            	    try {
            	        angle = Integer.parseInt(s[1]);
            	        speed = Integer.parseInt(s[2]);
            	    } catch (Exception e) {
            	        speed = 100;
            	        angle = 90;
            	    }
            	    controller.rotate(angle, speed);
            	} else if (command.equals("stop")) {
            	    controller.stop();
            	} else if (command.equals("floatWheels")){
            	    controller.floatWheels();
            	} else if (command.equals("setWheelSpeeds")) {
            	    int speed1;
            	    int speed2;
            	    try {
            	        speed1 = Integer.parseInt(s[1]);
            	        speed2 = Integer.parseInt(s[2]);
            	    } catch (Exception e) {
            	        speed1 = 100;
            	        speed2 = 100;
            	    }
            	    controller.setWheelSpeeds(speed1, speed2);
            	} else if (command.equals("penaltyKick")) {
            	    controller.penaltyKick();
            	} else if (command.equals("verdi")) {
            		controller.playVerdi();
            	} else if (command.equals("dribbleOff")) {
            		controller.dribblersOff();
            	} else if (command.equals("dribbleOn")) {
            		controller.dribblersOn();
            	} else if (command.equals("kickl")) {
            		controller.kickLeft();
            	} else if (command.equals("kickr")) {
            		controller.kickRight();
            	} else if (command.equals("kicks")) {
            		controller.kickSides();
            	} else if (command.equals("quit")) {
            	    break;
            	}
            	
    	    }
    	    
    }
}
