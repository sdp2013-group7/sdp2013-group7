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

public class Dribblerstest extends AbstractPlanner {

	private static final Logger LOG = Logger.getLogger(Dribblerstest.class);
	private static Coord startPosition = null;
														// 30cm)
	private boolean reachedBall = false;
	private boolean done = false;
	private boolean rotated = false;
	private boolean rotatedToBall = false;
	private boolean setSpeeds = false;

	// create strategies to use
	Strategy goToBallSafeStrategy;

	public Dribblerstest() {

		// initialise strategies that will be used in milestone2
		goToBallSafeStrategy = new GoToBallSafeProportional();
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
		if (ourRobot.possessesBall(ball) || reachedBall) {
			controller.dribblersOn();
			// if we reach ball stop
			if (!reachedBall) {
				controller.dribblersOff();
				
				goToBallSafeStrategy.stop(controller);
				reachedBall = true;
			}

			LOG.info("Posess ball");

			// if we don't have a previous start position set current to be
			// start position
			

			// get positions
			Coord ballPosition = snapshot.getBall().getPosition();

			// get angle from where we are facing to ball
			double angleFromBall = ourRobot
					.getAngleToTurnToTarget(ballPosition);

			// convert to degrees
			angleFromBall = Math.toDegrees(angleFromBall) / 4;

			// if angle large (above 10 off) then turn to ball
			if ((angleFromBall > 10 || angleFromBall < -10) && !rotatedToBall) {

				controller.rotate((int) angleFromBall, 50);
				LOG.info("Angle: " + (int) angleFromBall);
				rotatedToBall = true;
			}

			Coord robotPosition = ourRobot.getPosition();

			// get distance from start position (where we got possession) to
			// where robot is now
			
			Coord ownGoal = snapshot.getOpponentsGoal().getPosition();
			LOG.info("OwnGoalCoord:----- " + ownGoal);
			Coord turnTo = new Coord(ownGoal.getX(), ballPosition.getY());

			// get angle off of goal
			double angleFromGoal = Math.toDegrees(ourRobot
					.getAngleToTurnToTarget(turnTo)) / 2;

			// if angle large (above 10 off) then turn to goal
			boolean angleLarge = angleFromGoal > 10 || angleFromGoal < -10;

			if (angleLarge && !rotated) {

				controller.rotate((int) angleFromGoal, 50);
				LOG.info("Anglefromgoal: " + (int) angleFromGoal);
				LOG.info("Rotating");
				rotated = true;
			}

			if (!angleLarge || rotated) {

			LOG.info("Dribbling!");

			
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
	@FactoryMethod(designator = "Dribblerstest", parameterNames = {})
	public static Dribblerstest factoryMethod() {

		return new Dribblerstest();
	}
}
