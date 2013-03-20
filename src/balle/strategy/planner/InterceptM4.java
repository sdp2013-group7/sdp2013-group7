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
	private boolean dribbleAndScore = false;
	private Orientation targetOrientation;

	public InterceptM4() {
		interceptStrategy = new NavigateMilestone2();
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

		// If the reference line is not yet the ball direction, set it to be
		// the other robot's facing line
		if (referenceLine == null) {
			referenceLine = opponentRobot.getFacingLine();
		}

		if (!moving && !dribbleAndScore) {
			if (Math.abs(ourRobot.getOrientation().degrees()
					- targetOrientation.degrees()) < 45
					|| Math.abs((ourRobot.getOrientation().degrees() + 360)
							- targetOrientation.degrees()) < 45
					|| Math.abs(ourRobot.getOrientation().degrees()
							- (targetOrientation.degrees() + 360)) < 45) {

				// controller.setWheelSpeeds(600, 600);
				moving = true;
				return;
			}
		}
		
		if (moving) {
			interceptStrategy.step(controller, snapshot);
		}
	}

	// To make it a usable stand-alone strategy
	@FactoryMethod(designator = "Intercept M4", parameterNames = {})
	public static InterceptM4 factoryMethod() {
		return new InterceptM4();
	}

}
