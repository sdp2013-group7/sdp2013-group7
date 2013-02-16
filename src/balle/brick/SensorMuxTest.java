package balle.brick;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.LCD;

public class SensorMuxTest {

	public static void main(String[] args) throws InterruptedException {
		I2CSensor MUX;
		
		I2CPort I2Cport = SensorPort.S4; //Assign port
    	I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
    	
    	MUX = new I2CSensor(I2Cport);
    	MUX.setAddress(0x22);
    	
    	byte[] buffer = new byte[1];
    	
    	for (int i = 0; i < 20; i++) {
    		int result = MUX.getData(i, buffer, 1);
    		drawMessage(i + "\n" + result + "\n" + buffer[0]);
    	
    		Thread.sleep(500);
    	}

	}
	
	private static void drawMessage(String message) {
        LCD.clear();
        LCD.drawString(message, 0, 0);
        LCD.refresh();
    }
}
