package balle.strategy;

import java.awt.Color;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.main.drawable.Dot;
import balle.misc.Globals;
import balle.world.Coord;
import balle.world.Orientation;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Goal;
import balle.world.objects.Robot;

public class DefenseForPenaltiesRotateToOpponent extends Game {

	
	private boolean finishedRotating = false;
	
	
	
	
	
	
	
	
	
    private int SPEED;

    private static Logger LOG = Logger.getLogger(DefenseForPenaltiesRotateToOpponent.class);

	private Snapshot firstSnapshot = null;
	String robotState = "Center";
	int rotateSpeed = 0;

    private enum MovementDirection {
        FORWARD, BACKWARD, NONE
    };

	private boolean finished = false;

    public DefenseForPenaltiesRotateToOpponent(int speed) {
        super();
        SPEED = speed;
	}

    public DefenseForPenaltiesRotateToOpponent() {
        this(200);
    }

	
    @FactoryMethod(designator = "Game (Penalty Defence) rotate to", parameterNames = {})
	public static DefenseForPenaltiesRotateToOpponent gameFromPenaltyDefenceFactory()
	{
        return new DefenseForPenaltiesRotateToOpponent();
	}

	public boolean isStillInPenaltyDefence(Snapshot snapshot) {

		Coord ball = snapshot.getBall().getPosition();
		if ((ball == null) && snapshot.getOwnGoal().isLeftGoal())
			ball = new Coord(0.5, 0.6); // assume that ball is on penalty spot,
										// if
									// we cannot see it.
		if ((ball == null) && !snapshot.getOwnGoal().isLeftGoal())
			ball = new Coord(1.8, 0.6); // assume that ball is on penalty spot,
										// if
									// we cannot see it.

		double minX = 0;
		double maxX = 0.75;
		if (!snapshot.getOwnGoal().isLeftGoal()) {
			maxX = snapshot.getPitch().getMaxX();
			minX = maxX - 0.75;
		}

		if (ball.isEstimated()
				|| (ball.getY() < snapshot.getOwnGoal().getMaxY())
				&& (ball.getY() > snapshot.getOwnGoal().getMinY())
				&& (ball.getX() > minX) && (ball.getX() < maxX)) {

			return true;
		}

		finished = true;
		return false;
	}

	@Override
	public void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {

		if (finished || !isStillInPenaltyDefence(snapshot)) {
			LOG.info("tentacles have been retracted");
        	controller.retractBoth();
			super.onStep(controller, snapshot);
			return;
		}

		if (snapshot.getBalle().getPosition() == null) {
			return;
		}

		if ((firstSnapshot == null)
				&& (snapshot.getBall().getPosition() != null)) {
			firstSnapshot = snapshot;
		}

		else{
			
			
			Robot ourRobot = snapshot.getBalle();
			Ball ball = snapshot.getBall();
			
			Coord targetCoord = ball.getPosition();
			double angleToRotate = ourRobot.getAngleToTurnToTarget(targetCoord);
			double finalAngle = angleToRotate *100;
			
			
			LOG.info("angle to turn: "+finalAngle);
			
			
			
			
			// tweek this error value till it is good in real life
			if(angleToRotate>-0.01&& angleToRotate<0.01){
				controller.stop();
				finishedRotating =  true;
				
			}
			
			else if (finishedRotating == false){ 
				LOG.info("tentacles have been extended");
				
				// tweek the rotation speed to suitable for real life
				controller.rotate((int)finalAngle, 25);
			}
			else if (finishedRotating == true){
				
				LOG.info("tentacles have been extended");
				controller.extendBoth();
        	
			}
		}
	}
}