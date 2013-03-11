package balle.brick;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.LCD;
import lejos.nxt.Button;

public class SensorMuxTest {

	public static void main(String[] args) throws InterruptedException {
		I2CSensor MUX;
		
		I2CPort I2Cport = SensorPort.S4; //Assign port
    	I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
    	
    	
    	byte[] buffer = new byte[1];
    	
    	MUX = new I2CSensor(I2Cport);

    	
		MUX.setAddress(68);

    	while (!Button.ENTER.isPressed()) {
    		int result = MUX.getData(15, buffer, 1);
    		byte value = buffer[0];
    		
    		boolean port1 = (value & 1) == 0;
    		boolean port2 = (value & 2) == 0;
    		boolean port3 = (value & 4) == 0;
    		boolean port4 = (value & 8) == 0;
    		
    		drawMessage("port1 " + port1 + "\n" + "port2 " + port2 + "\n" + "port3 " + port3 + "\n" + "port4 " + port4 + "\n");
    		
//    		if (result != 0) {
//    			Sound.twoBeeps();
//    			Thread.sleep(1000);
//    		}
    		
    		Thread.sleep(500);
    	}
    	

	}
	
	private static void drawMessage(String message) {
        LCD.clear();
        LCD.drawString(message, 0, 0);
        LCD.refresh();
    }
}
