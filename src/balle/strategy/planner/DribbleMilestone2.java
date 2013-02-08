package balle.strategy.planner;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.strategy.executor.movement.GoToObjectPFN;
import balle.strategy.executor.turning.IncFaceAngle;
import balle.strategy.executor.turning.RotateToOrientationExecutor;
import balle.strategy.planner.AbstractPlanner;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Robot;
import balle.world.Coord;

public class DribbleMilestone2 extends AbstractPlanner {

	private static final Logger LOG = Logger.getLogger(DribbleMilestone2.class);
	private static Coord startPosition;
	private static final double distToTravel = 0.35;
	private boolean reachedBall = false;
	private boolean done = false;

	// create strategies to use
	Strategy goToBallSafeStrategy;

	public DribbleMilestone2() {

		// initialise strategies that will be used in milestone2
		goToBallSafeStrategy = new GoToBallNoGoals(new GoToObjectPFN(0.15f));
	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		
		goToBallSafeStrategy.stop(controller);
	}

	@Override
	public void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {

		if (done)
			return;
		
		// get position of our robot
		Robot ourRobot = snapshot.getBalle();
		
		// check if robot is actually on the pitch
		if (ourRobot.getPosition() == null) {
			
		LOG.info("where am i?!?!?!");
		return;
		}
		
		// get position of the ball
		Ball ball = snapshot.getBall();
		

		// when we have possession
		if (ourRobot.possessesBall(ball)) {
			
			// if we reach ball stop
			if (!reachedBall) {
				
				goToBallSafeStrategy.stop(controller);
				reachedBall = true;
			}
			
			LOG.info("Posess ball");
			
			// if we don't have a previous start position set current to be start position
			if (startPosition == null) {
				
				startPosition = ourRobot.getPosition();
				LOG.info(startPosition);
			}
			
			
			Coord ballPosition = snapshot.getBall().getPosition();
			
			// get angle from where we are facing to ball
			double angle = ourRobot.getAngleToTurnToTarget(ballPosition);
			
			// convert to degrees
			angle = Math.toDegrees(angle) / 2;
						
			// if angle large (above 10 off) then turn to ball
			if (angle > 10 || angle < -10) {
				
				controller.rotate((int) angle, 50);
				LOG.info("Angle: " + (int) angle);
			}
			
			Coord robotPosition = ourRobot.getPosition();
			
			// get distance from start position (where we got possession) to where robot is now
			double dist = startPosition.dist(robotPosition);
			
			// drive forward
			controller.setWheelSpeeds(150, 150);
			
			LOG.info("Distance travelled " + dist);
			
			// when distance driven reaches distance to travel then stop
			if (dist > distToTravel) {
				
				controller.stop();
				done = true;
			}
		
		} 
		
		// if robot is not near ball
		 else {

			LOG.info("going to ball");
			
			// execute strategy to go to the ball
			goToBallSafeStrategy.step(controller, snapshot);
			// add visuals to camera stream
			addDrawables(goToBallSafeStrategy.getDrawables());
		}
	}

	// Factory method to make DribbleMilestone2 appear in the list of strats in the simulator
	@FactoryMethod(designator = "DribbleMilestone2", parameterNames = {})
	public static DribbleMilestone2 factoryMethod() {
		
		return new DribbleMilestone2();
	}
}
