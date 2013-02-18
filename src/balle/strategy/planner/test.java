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

public class test extends AbstractPlanner {

	private static final Logger LOG = Logger.getLogger(test.class);
	private static Coord startPosition;
	private static final double distToTravel = 0.35; // usually 0.35(close to
	private boolean WheelSpeedSet=false;												// 30cm)
	private boolean reachedBall = false;
	private boolean done = false;

	// create strategies to use
	Strategy goToBallSafeStrategy;

	public test() {

		// initialise strategies that will be used in milestone2
		goToBallSafeStrategy = new GoToBall2();
	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {

		goToBallSafeStrategy.stop(controller);
	}

	@Override
	public void onStep(Controller controller, Snapshot snapshot)
			throws ConfusedException {

		if (done) {

			return;
		}

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

			// if we don't have a previous start position set current to be
			// start position
			if (startPosition == null) {

				startPosition = ourRobot.getPosition();
				LOG.info(startPosition);
			}

			// get positions
			Coord ballPosition = snapshot.getBall().getPosition();
			

			// get angle from where we are facing to ball
			double angleFromBall = ourRobot.getAngleToTurnToTarget(ballPosition);

			// convert to degrees
			angleFromBall = Math.toDegrees(angleFromBall) / 2;

			// if angle large (above 10 off) then turn to ball
			if (angleFromBall > 10 || angleFromBall < -10) {
		
				controller.rotate((int) angleFromBall, 10);
				LOG.info("Angle: " + (int) angleFromBall);
			}

			Coord robotPosition = ourRobot.getPosition();

			// get distance from start position (where we got possession) to
			// where robot is now
			double dist = startPosition.dist(robotPosition);

			Coord ownGoal = snapshot.getOpponentsGoal().getPosition();
			LOG.info("OwnGoalCoord:----- " + ownGoal);
			
			
			// get angle off of goal
			//double angleFromGoal = Math.toDegrees(ourRobot.getAngleToTurnToTarget(ownGoal));



			// when distance driven reaches distance to travel then stop
				if (dist > distToTravel) {
					LOG.info("SSSSSS");
					controller.stop();
					//controller.setWheelSpeeds(0, 0);
					//stop(controller);
					done = true;
				}
				else{
				// drive forward
					if (!WheelSpeedSet){
						controller.setWheelSpeeds(100, 100);
						LOG.info("Distance travelled " + dist);
						LOG.info("Distance to travelled " + distToTravel);
						WheelSpeedSet=true;
					}
				
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

	// Factory method to make DribbleMilestone2 appear in the list of strats in
	// the simulator
	@FactoryMethod(designator = "test", parameterNames = {})
	public static test factoryMethod() {

		return new test();
	}
}
