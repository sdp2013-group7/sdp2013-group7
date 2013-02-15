package balle.brick;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.LCD;

public class MultiplexerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		I2CSensor MULTIPLEXER;
		
		I2CPort I2Cport = SensorPort.S4; //Assign port
    	I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
    	
    	MULTIPLEXER = new I2CSensor(I2Cport);

    	//drawMessage(Integer.toString(I2Cport.i2cStatus()));
    	
    	int counter;
    	byte direction = (byte) 1;
    	byte speed = (byte)200;
    	
    	MULTIPLEXER.setAddress(0xB4);
    	
    	// Register sweep
//    	for( counter = 0; counter <65; counter ++){ 
//    		MULTIPLEXER.sendData(0x01 + (2*counter),direction);
//    		MULTIPLEXER.sendData(0x02 + (2*counter),speed);
//    	}
//    	try{
//    		Thread.sleep(1000);
//    	} catch (Exception e) {
//    		
//    	}
//    	for( counter = 0; counter <65; counter ++){ 
//    		MULTIPLEXER.sendData(0x02 + (2*counter),direction);
//    		MULTIPLEXER.sendData(0x01 + (2*counter),speed);
//    	}
//    	
    	
    	int res1 = MULTIPLEXER.sendData(0x01,direction);
    	drawMessage(Integer.toString(res1));
    	int res2 = MULTIPLEXER.sendData(0x02,speed);
    	drawMessage(Integer.toString(res2));
    	
    	try {
    		Thread.sleep(5000);
    	} catch (Exception e) {
    		
    	}
    	MULTIPLEXER.sendData(0x01,(byte)0);
    	MULTIPLEXER.sendData(0x02,(byte)0);
    	
    	
	}
	
	private static void drawMessage(String message) {
        LCD.clear();
        LCD.drawString(message, 0, 0);
        LCD.refresh();
    }

}
