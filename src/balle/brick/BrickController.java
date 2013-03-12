package balle.brick;

import balle.controller.Controller;
import balle.controller.ControllerListener;
import lejos.robotics.navigation.LegacyPilot;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.LCD;

import lejos.nxt.addon.RCXMotor;
import lejos.nxt.MotorPort;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;

import lejos.nxt.Sound;
import java.io.File;

/**
 * The Control class. Handles the actual driving and movement of the bot, once
 * BotCommunication has processed the commands.
 * 
 * That is -- defines the behaviour of the bot when it receives the command.
 * 
 * Adapted from SDP2011 groups 10 code -- original author shearn89
 * 
 * @author sauliusl
 */
@SuppressWarnings("deprecation")
public class BrickController implements Controller {
	LegacyPilot pilot;
    public int maxPilotSpeed = 600; // 20
                                    // for
                                    // friendlies

    private final NXTRegulatedMotor LEFT_WHEEL = Motor.B;
    private final NXTRegulatedMotor RIGHT_WHEEL = Motor.C;
    private final RCXMotor DRIBBLER = new RCXMotor(MotorPort.A);

    public final boolean INVERSE_WHEELS = true;

    public final float WHEEL_DIAMETER = 0.0863f; // metres
    public final float TRACK_WIDTH = 0.1117f; // metres

    public static final int MAXIMUM_MOTOR_SPEED = 2000;

    // Our gear ratio is 5/3
    public static final float GEAR_ERROR_RATIO = (float) 5 / 3;

    private volatile boolean isKicking = false;
    private volatile boolean isLeftKicking = false;
    private volatile boolean isRightKicking = false;
    private volatile boolean areSidesKicking = false;
    private final int tentacleKickTime = 100;
    private final int mainKickTime = 80;

    // The mux and its address
    private final I2CSensor MOTORMUX;
    private final int MUX_ADDRESS = 0xB4;
    
    // Constants for the mux
    private final byte kickSpeed = (byte)255;
    private final byte forward = (byte) 1;
    private final byte backward = (byte) 2;
    private final byte off = (byte) 0;
    
    // Constants for the mux registers
    private final int kicker1Direction = 0x01;
    private final int kicker1Speed = 0x02;
    private final int tentacleRightDirection = 0x03;
    private final int tentacleRightSpeed = 0x04;
    private final int tentacleLeftDirection = 0x05;
    private final int tentacleLeftSpeed = 0x06;
    private final int kicker2Direction = 0x07;
    private final int kicker2Speed = 0x08;
    
    public BrickController() {

    	I2CPort I2Cport = SensorPort.S4; //Assign port
    	I2Cport.i2cEnable(I2CPort.STANDARD_MODE);
    	
    	MOTORMUX = new I2CSensor(I2Cport);
    	MOTORMUX.setAddress(MUX_ADDRESS);
    	
		pilot = new LegacyPilot(WHEEL_DIAMETER, TRACK_WIDTH, LEFT_WHEEL,
                RIGHT_WHEEL, INVERSE_WHEELS);
        pilot.setMoveSpeed(maxPilotSpeed);
        pilot.setTurnSpeed(45); // 45 has been working fine.
        
        // TODO: Check the acceleration values with the robot when it's done
//        LEFT_WHEEL.setAcceleration(250);
//        RIGHT_WHEEL.setAcceleration(250);
//        pilot.regulateSpeed(true);
//        LEFT_WHEEL.regulateSpeed(true);
//        RIGHT_WHEEL.regulateSpeed(true);
//        LEFT_WHEEL.smoothAcceleration(true);
//        RIGHT_WHEEL.smoothAcceleration(true);
//        KICKER.smoothAcceleration(false);
//        KICKER.regulateSpeed(false);

    }
    
    void registerSweep() {
    	drawMessage("Start sweep");

    	int counter;
    	byte direction = (byte) 1;
    	byte speed = (byte)200;
    	    	
    	for( counter = 0; counter <65; counter ++){ 
    		MOTORMUX.sendData(0x01 + (2*counter),direction);
    		MOTORMUX.sendData(0x02 + (2*counter),speed);
    	}
    	try{
    		Thread.sleep(1000);
    	} catch (Exception e) {
    		
    	}
    	for( counter = 0; counter <65; counter ++){ 
    		MOTORMUX.sendData(0x02 + (2*counter),direction);
    		MOTORMUX.sendData(0x01 + (2*counter),speed);
    	}
    	
    	drawMessage("Sweep done!");
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#floatWheels()
     */
    @Override
    public void floatWheels() {
        LEFT_WHEEL.flt();
        RIGHT_WHEEL.flt();
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#stop()
     */
    @Override
    public void stop() {
        pilot.stop();
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#kick()
     */
    @Override
    public void kick() {

        if (isKicking) {
            return;
        }

        isKicking = true;
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MOTORMUX.sendData(kicker1Speed,kickSpeed);
                    MOTORMUX.sendData(kicker2Speed,kickSpeed);

                    MOTORMUX.sendData(kicker1Direction,backward);
                    MOTORMUX.sendData(kicker2Direction,backward);
                	
                    Thread.sleep(mainKickTime);
                    
                    MOTORMUX.sendData(kicker1Direction, forward);
                    MOTORMUX.sendData(kicker2Direction, forward);
                    
                    Thread.sleep(mainKickTime - 60);
                } catch (InterruptedException e) {
                    drawMessage("InterruptedException\nin kick");
                } finally {
                	MOTORMUX.sendData(kicker1Direction, off);
                	MOTORMUX.sendData(kicker2Direction, off);
                
                	MOTORMUX.sendData(kicker1Speed, off);
                	MOTORMUX.sendData(kicker2Speed, off);
                	isKicking = false;
                }
            }
        }).start();
    }

//    public void gentleKick(int speed, int angle) {
//        KICKER.setSpeed(speed);
//        KICKER.resetTachoCount();
//        KICKER.rotateTo(angle);
//        KICKER.rotateTo(0);
//    }

    public float getTravelDistance() {
        return pilot.getTravelDistance();
    }

    public void reset() {
        pilot.reset();
    }

    private void setMotorSpeed(NXTRegulatedMotor motor, int speed) {
        boolean forward = true;
        if (speed < 0) {
            forward = false;
            speed = -1 * speed;
        }

        motor.setSpeed(speed);
        if (forward)
            motor.forward();
        else
            motor.backward();
    }

    @Override
    public void setWheelSpeeds(int leftWheelSpeed, int rightWheelSpeed) {
        if (leftWheelSpeed > MAXIMUM_MOTOR_SPEED)
            leftWheelSpeed = MAXIMUM_MOTOR_SPEED;
        if (rightWheelSpeed > MAXIMUM_MOTOR_SPEED)
            rightWheelSpeed = MAXIMUM_MOTOR_SPEED;

        if (INVERSE_WHEELS) {
            leftWheelSpeed *= -1;
            rightWheelSpeed *= -1;
        }
        
//        Thread t1 = new Thread(new WheelSpeeds(leftWheelSpeed, LEFT_WHEEL));        
//        Thread t2 = new Thread(new WheelSpeeds(rightWheelSpeed, RIGHT_WHEEL));
//        
//        t1.start();
//        t2.start();

        setMotorSpeed(LEFT_WHEEL, leftWheelSpeed);
        setMotorSpeed(RIGHT_WHEEL, rightWheelSpeed);
    }

    @Override
    public int getMaximumWheelSpeed() {
        return MAXIMUM_MOTOR_SPEED;
    }

    // Not used at the moment, setWheelSpeeds gets called instead
    @Override
    public void backward(int speed) {
        pilot.setMoveSpeed(speed);
        pilot.backward();
    }

    // Not used at the moment, setWheelSpeeds gets called instead
    @Override
    public void forward(int speed) {
        pilot.setMoveSpeed(speed);
        pilot.forward();

    }

    // The function used for Milestone 1
    public void drive() {
        boolean movingForward = false;
        double ROLL_DISTANCE = 1.2;
        int ROLL_SPEED = 400;
        
        LEFT_WHEEL.resetTachoCount();
        RIGHT_WHEEL.resetTachoCount();
        
        while (true) {
            // If it's not moving then begin to move the robot forward
            if (!movingForward) {
                //drawMessage("Roll");
                movingForward = true;
                setWheelSpeeds(ROLL_SPEED, ROLL_SPEED);
            }
            else {
                // Get the distance travelled
                float distance = getTravelDistance();
                int leftCount = LEFT_WHEEL.getTachoCount();
                int rightCount = RIGHT_WHEEL.getTachoCount();
                
                if (leftCount > rightCount) {
                    setWheelSpeeds(ROLL_SPEED - 1, ROLL_SPEED);
                } else if (leftCount < rightCount) {
                    setWheelSpeeds(ROLL_SPEED, ROLL_SPEED - 1);
                } else {
                    setWheelSpeeds(ROLL_SPEED, ROLL_SPEED);
                }
                
                drawMessage(leftCount + " " + rightCount);
                
                // The constant is important to set, because this is when it stops
                if (distance >= ROLL_DISTANCE) {
                    // How we stop moving... obvious
                    stop();
                    //drawMessage("Done!!!");
                    break;
                }
            }
        }

    }

    @Override
    public void rotate(int deg, int speed) {
        pilot.setTurnSpeed(speed);
        pilot.rotate(deg / GEAR_ERROR_RATIO);
    }

    @Override
    public void penaltyKick() {
        int turnAmount = 27;
        if (Math.random() <= 0.5)
            turnAmount *= -1;
        rotate(turnAmount, 180);
        kick();

    }

    @Override
    public boolean isReady() {
        return true;
    }

	@Override
	public void addListener(ControllerListener cl) {
		// TODO make STUB
	}
	
	@Override
	public void kickLeft() {
		// TODO: Make it stay out?
		if (isLeftKicking || areSidesKicking)
			return;
		
		// Port 3 on mux
		isLeftKicking = true;		

        // TODO: Get the timings right.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MOTORMUX.sendData(tentacleLeftSpeed,kickSpeed);
                    MOTORMUX.sendData(tentacleLeftDirection,backward);
                    
                    Thread.sleep(tentacleKickTime);
                    
                    MOTORMUX.sendData(tentacleLeftDirection, off);

                    Thread.sleep(2000);
                    
                    MOTORMUX.sendData(tentacleLeftDirection, forward);

                	Thread.sleep(tentacleKickTime);
                } catch (InterruptedException e) {
                    drawMessage("InterruptedException\nin kickLeft");
                } finally {
                	MOTORMUX.sendData(tentacleLeftDirection, off);
                	MOTORMUX.sendData(tentacleLeftSpeed, off);
                	isLeftKicking = false;
                }
            }
        }).start();
		
	}
	
	@Override
	public void kickRight() {
		// TODO: Make it stay out?
		if (isRightKicking || areSidesKicking)
			return;
		
		// Port 2 on mux
		isRightKicking = true;

        // TODO: Get the timings right.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MOTORMUX.sendData(tentacleRightSpeed,kickSpeed);
                    MOTORMUX.sendData(tentacleRightDirection,forward);
                    
                    Thread.sleep(tentacleKickTime);
                    
                    MOTORMUX.sendData(tentacleRightDirection,backward);
                    
                    Thread.sleep(tentacleKickTime);                                       
                } catch (InterruptedException e) {
                    drawMessage("InterruptedException\nin kickRight");
                } finally {
                    MOTORMUX.sendData(tentacleRightDirection, off);
                    MOTORMUX.sendData(tentacleRightSpeed, off);
                    isRightKicking = false;
                }                
            }
        }).start();
	}
	
	@Override
	public void kickSides() {
		// TODO: Make them stay out?
		if (isLeftKicking || isRightKicking || areSidesKicking)
			return;
		
		areSidesKicking = true;
		
        // TODO: Get the timings right.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MOTORMUX.sendData(tentacleRightSpeed,kickSpeed);
                    MOTORMUX.sendData(tentacleLeftSpeed,kickSpeed);
                    MOTORMUX.sendData(tentacleLeftDirection,forward);
                    MOTORMUX.sendData(tentacleRightDirection,forward);
                	
                    Thread.sleep(tentacleKickTime);
                    
                    MOTORMUX.sendData(tentacleRightDirection,backward);
                    MOTORMUX.sendData(tentacleLeftDirection,backward);
                    
                    Thread.sleep(tentacleKickTime);
                } catch (InterruptedException e) {
                    drawMessage("InterruptedException\nin kickSides");
                } finally {
                    MOTORMUX.sendData(tentacleRightDirection, off);
                    MOTORMUX.sendData(tentacleLeftDirection, off);
                    MOTORMUX.sendData(tentacleRightSpeed, off);
                    MOTORMUX.sendData(tentacleLeftSpeed, off);
                    areSidesKicking = false;
                }                               
            }
        }).start();
	}
	
	@Override
	public void dribblersOn() {
		DRIBBLER.setPower(100);
		DRIBBLER.backward();
	}
	
	@Override
	public void dribblersOff() {
		DRIBBLER.stop();
	}
	
	@Override
	public void extendBoth() {
		// TODO: Method stub
	}
	
	@Override
	public void extendLeft() {
		// TODO: Method stub
	}
	
	@Override
	public void extendRight() {
		// TODO: Method stub
	}
	
	@Override
	public void retractBoth() {
		// TODO: Method stub
	}
	
	@Override
	public void retractLeft() {
		// TODO: Method stub
	}
	
	@Override
	public void retractRight() {
		// TODO: Method stub
	}
	
	@Override
	public void playVerdi() {
        // Play Verdi's Requiem
		if (Sound.getTime() == 0) {
			File verdiFile = new File("verdi.wav");
        	Sound.playSample(verdiFile, Sound.VOL_MAX);
		}
	}

	
	private static void drawMessage(String message) {
        LCD.clear();
        LCD.drawString(message, 0, 0);
        LCD.refresh();
    }
	
	private class WheelSpeeds implements Runnable {
	    private int speed;
	    private NXTRegulatedMotor motor;
	    
	    WheelSpeeds(int speed, NXTRegulatedMotor motor) {
	        super();
	        this.speed = speed;
	        this.motor = motor;
	    }
	    
	    @Override
        public void run() {
            setMotorSpeed(motor, speed);
        }
	}

}
