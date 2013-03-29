package balle.brick;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import balle.bluetooth.messages.*;
import balle.controller.Controller;

class ListenerThread extends Thread {
    DataInputStream input;
    boolean         shouldStop;
    int             command;
    boolean         commandConsumed;
    int				missedCommands;
    int				commandsSinceRead;
    int 			totalCommandsReceived;

    ListenerThread(DataInputStream input) {
        this.input = input;
        this.shouldStop = false;
        this.commandConsumed = true;
    }

    @Override
    public void run() {

        while (!shouldStop) {
            try {
                int command = input.readInt();
                setCommand(command);
            } catch (IOException e) {
                shouldStop = true;
            }
        }
    }

    private synchronized void setCommand(int command) {
    	commandsSinceRead++;
    	totalCommandsReceived++;
        this.command = command;
        commandConsumed = false;
    }

    public synchronized int getCommand() {
    	missedCommands += commandsSinceRead-1;
    	commandsSinceRead = 0;
        commandConsumed = true;
        return command;
    }

    public synchronized boolean available() {
        return !commandConsumed;
    }
    
    public synchronized int getMissed() {
    	return missedCommands;
    }
    
    public synchronized int getAll() {
    	return totalCommandsReceived;
    }

    public void cancel() {
        shouldStop = true;
    }

}

/**
 * Create a connection to Roboto from the computer. execute commands send from
 * the computer test out movements of Roboto.
 * 
 * @author s0815695
 */
public class Kraken {

	
    private static I2CSensor SENSORMUX;
    private static final int MUX_ADDRESS = 68;
    
    /**
     * Processes the decoded message and issues correct commands to controller
     * 
     * @param decodedMessage
     *            the decoded message
     * @param controller
     * @return true, if successful
     */
    public static boolean processMessage(AbstractMessage decodedMessage,
            Controller controller) {
        String name = decodedMessage.getName();

        // TODO: Decoding the messages for the dribblers and tentacles
        if (name.equals(MessageKick.NAME)) {
            MessageKick messageKick = (MessageKick) decodedMessage;
            if (messageKick.isPenalty()) {
                controller.penaltyKick();
            } else {
                controller.kick();
            }
        } else if (name.equals(MessageMove.NAME)) {
            MessageMove messageMove = (MessageMove) decodedMessage;
            controller.setWheelSpeeds(messageMove.getLeftWheelSpeed(),
                    messageMove.getRightWheelSpeed());
        } else if (name.equals(MessageStop.NAME)) {
            MessageStop messageStop = (MessageStop) decodedMessage;
            if (messageStop.floatWheels())
                controller.floatWheels();
            else
                controller.stop();
        } else if (name.equals(MessageRotate.NAME)) {
            MessageRotate messageRotate = (MessageRotate) decodedMessage;
            controller.rotate(messageRotate.getAngle(),
                    messageRotate.getSpeed());
        } else if (name.equals(MessageForward.NAME)) {
        	MessageForward messageForward = (MessageForward) decodedMessage;
        	controller.forward(messageForward.getArgument());
        } else if (name.equals(MessageKickTentacle.NAME)) {
        	int leftRightBoth = ((MessageKickTentacle) decodedMessage).getArgument();
        	if (leftRightBoth == 0)
        		controller.kickLeft();
        	else if (leftRightBoth == 1)
        		controller.kickRight();
        	else
        		controller.kickSides();
        } else if (name.equals(MessageDribblers.NAME)) {
        	int onOff = ((MessageDribblers) decodedMessage).getArgument();
        	if (onOff == 0)
        		controller.dribblersOff();
        	else
        		controller.dribblersOn();
        } else if (name.equals(MessageVerdi.NAME)) {
        	controller.playVerdi();
        } else if (name.equals(MessageMoveTentacle.NAME)) {
        	int leftRightBoth = ((MessageMoveTentacle) decodedMessage).getTentacle();
        	int extendRetract = ((MessageMoveTentacle) decodedMessage).getAction();
        	
        	if (leftRightBoth == 0) {
        		// Only left
        		
        		if (extendRetract == 0)
        			controller.extendLeft();
        		else
        			controller.retractLeft();
        		
        	} else if (leftRightBoth == 1) {
        		// Only right
        		
        		if (extendRetract == 0)
        			controller.extendRight();
        		else
        			controller.retractRight();
        		
        	} else {
        		// Both
        		
        		if (extendRetract == 0)
        			controller.extendBoth();
        		else
        			controller.retractBoth();
        	}
        			
        } else {
            return false;
        }
        return true;
    }

    /**
     * Main program
     * 
     * @param args
     */
    public static void main(String[] args) {
    	
    	// Assign a port to the sensor multiplexer
    	SensorPort.S4.i2cEnable(I2CPort.STANDARD_MODE);
    	
    	// Set up the multiplexer
    	SENSORMUX = new I2CSensor(SensorPort.S4);
    	SENSORMUX.setAddress(MUX_ADDRESS);
    	byte[] sensorBuffer = new byte[1];
    	
    	// Set up the other sensors
        TouchSensor touchRight = new TouchSensor(SensorPort.S2);
        TouchSensor touchLeft = new TouchSensor(SensorPort.S1);

        while (true) {
            // Enter button click will halt the program
            if (Button.ENTER.isPressed())
                break;

            drawMessage("Connecting...");
            Sound.twoBeeps();

            BTConnection connection = Bluetooth.waitForConnection();

            drawMessage("Connected");
            Sound.beep();

            DataInputStream input = connection.openDataInputStream();
            ListenerThread listener = new ListenerThread(input);

            BrickController controller = new BrickController();
            MessageDecoder decoder = new MessageDecoder();

            listener.start();
            
            controller.dribblersOn();

            while (true) {
                // Enter button click will halt the program
                if (Button.ENTER.isPressed()) {
                    controller.stop();
                    listener.cancel();
                    break;
                }
                if (Button.ESCAPE.isPressed()) {
                    return;
                }
                try {
                	
                    // Get the data from the multiplexer
                    SENSORMUX.getData(15, sensorBuffer, 1);
            		byte sensorValues = sensorBuffer[0]; // (byte) 15;
            		
            		boolean backLeft = (sensorValues & 1) == 0; // Port 1
            		boolean lowerFrontLeft = (sensorValues & 2) == 0; // Port 2
            		boolean lowerFrontRight = (sensorValues & 4) == 0; // Port 3
            		boolean backRight = (sensorValues & 8) == 0; // Port 4
                	
                    // Check for the front sensors
                    if (touchLeft.isPressed() || touchRight.isPressed() || lowerFrontLeft || lowerFrontRight) {
                        controller.setWheelSpeeds(
                                -controller.getMaximumWheelSpeed(),
                                -controller.getMaximumWheelSpeed());
                        drawMessage("Obstacle detected\nBacking up");
                        Thread.sleep(150);
                        controller.stop();
                    }

                    // Check for back sensors as well
                    if (backRight || backLeft) {
                        controller.setWheelSpeeds(
                                controller.getMaximumWheelSpeed(),
                                controller.getMaximumWheelSpeed());
                        drawMessage("Obstacle detected\nForward!");
                        Thread.sleep(150);
                        controller.stop();
                    }
                    


                    if (!listener.available())
                        continue;

                    int hashedMessage = listener.getCommand();
                    AbstractMessage message = decoder
                            .decodeMessage(hashedMessage);
                    if (message == null) {
                        drawMessage("Could not decode: \n" + hashedMessage);
                        Thread.sleep(10000);
                        break;
                    }
                    String name = message.getName();
                    //drawMessage(name);

                    boolean successful = processMessage(message, controller);
                    if (!successful) {
                        drawMessage("Unknown message received: \n"
                                + hashedMessage);
                        Thread.sleep(10000);
                        break;
                    }
                    
                    if(name.equals("STOP")) {
                    	drawMessage(listener.getMissed() + "\n" + listener.getAll());
                    }

                } catch (Exception e1) {
                	//drawMessage("Error in MainLoop: " + e1.getMessage());
                	// TODO: Make it throw it instead? Will get a full stacktrace on the LCD.
                }
            }

            connection.close();
            controller.muxOff();
            controller.dribblersOff();

        }
        
        
    }

    private static void drawMessage(String message) {
        LCD.clear();
        LCD.drawString(message, 0, 0);
        LCD.refresh();
    }

}