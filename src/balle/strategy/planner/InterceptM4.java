package balle.strategy.planner;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Orientation;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Robot;

public class InterceptM4 extends AbstractPlanner {

	private static Logger LOG = Logger.getLogger(InterceptM4.class);

	// Strategy
	private Strategy interceptStrategy;

	// Flag that is set if there is nothing else to do
	private boolean done = false;

	// Robots and ball
	private Robot ourRobot;
	private Robot opponentRobot;
	private Ball ball;

	// Positions of robots and ball
	private Coord ourPosition;
	private Coord opponentPosition;
	private Coord ballPosition;

	// Records ball position when it moves at least 3cm
	private Coord lastBallPosition;

	// The ball's direction (if ball is moving) or the other robot's
	// facing line otherwise
	private Line referenceLine;

	private Boolean moving = false;
	private boolean rotated = false;
	private Orientation targetOrientation;

	public InterceptM4() {
		interceptStrategy = new GoToBallSafeProportional();
	}

	@Override
	protected void onStep(Controller controller, Snapshot snapshot)
			throws ConfusedException {
		// TODO Auto-generated method stub

		if (done) {
			return;
		}

		// Get robots and ball
		ourRobot = snapshot.getBalle();
		opponentRobot = snapshot.getOpponent();
		ball = snapshot.getBall();

		// Get positions of robots and ball
		ourPosition = ourRobot.getPosition();
		opponentPosition = opponentRobot.getPosition();
		ballPosition = ball.getPosition();

		// Check if our robot is actually on the pitch
		if (ourPosition == null) {
			LOG.info("Where am I? :(");
			return;
		}

		// Check if other robot is actually on the pitch
		if (opponentPosition == null) {
			LOG.info("Where is enemy? :(");
			return;
		}

		// Check if ball is actually on the pitch
		if (ballPosition == null) {
			LOG.info("Where is ball? :(");
			return;
		}
		
		if (lastBallPosition == null)
			lastBallPosition = ballPosition;

		// If the reference line is not yet the ball direction, set it to be
		// the other robot's facing line
		if (referenceLine == null) {
			referenceLine = opponentRobot.getFacingLine();
		}

		if (!rotated) {
			double angle = ourRobot.getAngleToTurnToTarget(opponentPosition);

			// convert to degrees
			angle = Math.toDegrees(angle);
			
			controller.rotate((int) angle, 150);
			LOG.info("Angle: " + (int) angle);
			
			rotated = true;
			return;
			
		}
		
		if (ballPosition.dist(lastBallPosition) > 0.01) {
			moving = true;
		}

		if (moving && rotated) {
			interceptStrategy.step(controller, snapshot);
		}
	}

	// To make it a usable stand-alone strategy
	//@FactoryMethod(designator = "Intercept M4", parameterNames = {})
	public static InterceptM4 factoryMethod() {
		return new InterceptM4();
	}

}
