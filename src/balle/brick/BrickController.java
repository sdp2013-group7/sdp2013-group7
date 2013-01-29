package balle.brick;

import balle.controller.Controller;
import balle.controller.ControllerListener;
import lejos.robotics.navigation.LegacyPilot;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.LCD;

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
public class BrickController implements Controller {
	LegacyPilot pilot;
    public int maxPilotSpeed = 600; // 20
                                    // for
                                    // friendlies

    public final NXTRegulatedMotor LEFT_WHEEL = Motor.B;
    public final NXTRegulatedMotor RIGHT_WHEEL = Motor.C;
    public final NXTRegulatedMotor KICKER = Motor.A;

    public final boolean INVERSE_WHEELS = false;

    public final float WHEEL_DIAMETER = 0.0816f; // metres
    public final float TRACK_WIDTH = 0.155f; // metres

    public static final int MAXIMUM_MOTOR_SPEED = 2000;

    public static final int GEAR_ERROR_RATIO = 3/5; // Gears cut our turns in half

    private volatile boolean isKicking = false;

    public BrickController() {

		pilot = new LegacyPilot(WHEEL_DIAMETER, TRACK_WIDTH, LEFT_WHEEL,
                RIGHT_WHEEL, INVERSE_WHEELS);
        pilot.setMoveSpeed(maxPilotSpeed);
        pilot.setTurnSpeed(45); // 45 has been working fine.
        LEFT_WHEEL.setAcceleration(250);
        RIGHT_WHEEL.setAcceleration(250);
        //pilot.regulateSpeed(true);
        //LEFT_WHEEL.regulateSpeed(true);
        //RIGHT_WHEEL.regulateSpeed(true);
        //LEFT_WHEEL.smoothAcceleration(true);
        //RIGHT_WHEEL.smoothAcceleration(true);
        //KICKER.smoothAcceleration(false);
        //KICKER.regulateSpeed(false);

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

        int acceleration = 12000;

        KICKER.setSpeed(MAXIMUM_MOTOR_SPEED);
        KICKER.setAcceleration(acceleration);
        KICKER.resetTachoCount();
        //KICKER.backward();
        
        KICKER.forward();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    //
                }
                KICKER.rotateTo(0);
                isKicking = false;
            }
        }).start();
    }

    public void gentleKick(int speed, int angle) {
        KICKER.setSpeed(speed);
        KICKER.resetTachoCount();
        KICKER.rotateTo(angle);
        KICKER.rotateTo(0);
    }

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

    @Override
    public void backward(int speed) {
        pilot.setMoveSpeed(speed);
        pilot.backward();
    }

    @Override
    public void forward(int speed) {
        pilot.setMoveSpeed(speed);
        pilot.forward();

    }

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
