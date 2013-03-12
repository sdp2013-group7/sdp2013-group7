package balle.strategy.planner;

import java.awt.Color;
import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.main.drawable.DrawableRectangularObject;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Orientation;
import balle.world.Snapshot;
import balle.world.objects.RectangularObject;
import balle.world.objects.Robot;

public class AvoidMilestone3 extends AbstractPlanner {

	private static Logger LOG = Logger.getLogger(DribbleAndScore.class);
	// Strategy
	private Strategy avoidStrategy;

	private boolean done = false;

	// Robots
	private Robot ourRobot;
	private Robot enemyRobot;

	// Robot status
	private boolean rotating = false;
	private boolean moving = false;
	private boolean rotatingClockwise = false;
	private boolean rotatingToAvoid = false;
	private boolean movingToAvoid = false;

	// Movement data
	private Coord startPoint;

	// Rotation data
	private boolean firstRotationClockwise = false;

	// Robot orientation
	private Orientation originalDirection = null;
	private Line faceLine;

	// Opponent status
	private boolean opponentInFront;
	private double opponentDistance;

	// Speeds
	private int motorSpeed = 200;
	private int turnSpeed = 20;
	private int turnAngle = 2;

	// Pitch
	private Line[] pitchSides = new Line[4];

	public AvoidMilestone3() {

		avoidStrategy = new GoToBallSafeProportional();

		double pitchWidth = 1.22;
		double pitchLength = 2.44;
		pitchSides[0] = new Line(0, 0, 0, pitchWidth);
		pitchSides[1] = new Line(pitchLength, 0, pitchLength, pitchWidth);
		pitchSides[2] = new Line(0, pitchWidth, pitchLength, pitchWidth);
		pitchSides[3] = new Line(0, 0, pitchLength, 0);

	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		avoidStrategy.stop(controller);
	}

	@Override
	protected void onStep(Controller controller, Snapshot snapshot)
			throws ConfusedException {

		if (done) {
			return;
		}

		// Get positions of our robot and opponent
		ourRobot = snapshot.getBalle();
		enemyRobot = snapshot.getOpponent();

		// Check if robot is actually on the pitch
		if (ourRobot.getPosition() == null) {
			LOG.info("Where am I? :(");
			return;
		}
		if (enemyRobot.getPosition() == null) {
			LOG.info("Where is enemy? :(");
			return;
		}

		// Get facing rectangle
		faceLine = ourRobot.getFacingLine(0.4);
		RectangularObject faceRect = faceLine.widen(0.20);

		// Check if opponent is in our way
		opponentInFront = faceRect.containsRobot(enemyRobot);

		opponentDistance = ourRobot.getFrontSide().dist(enemyRobot.getPosition());

		// Check if we are going in right direction
		if (originalDirection == null)
			originalDirection = ourRobot.getOrientation();

		if (rotating)
			this.rotating(controller);

		else if (moving) {
			this.moving(controller);
			return;
		}

		else {
			this.startMoving(controller);
			return;
		}

		addDrawable(new DrawableRectangularObject(faceRect, Color.MAGENTA));

	}

	protected void rotating(Controller controller) {

		if (rotatingToAvoid) {

			if (opponentInFront) {
				if (rotatingClockwise)
					controller.rotate(-turnAngle, turnSpeed);
				else
					controller.rotate(turnAngle, turnSpeed);
			}

			else {
				LOG.info("Stopping rotation. Moving in new direction.");
				rotating = false;
				moving = true;
				movingToAvoid = true;
				startPoint = ourRobot.getPosition();
				controller.setWheelSpeeds(motorSpeed, motorSpeed);
			}
		}

		else {
			LOG.info("Back to original direction. Stopping rotation.");
			if (Math.abs(ourRobot.getOrientation().degrees()
					- originalDirection.degrees()) < 4) {
				LOG.info("here");
				rotating = false;
				moving = true;
				movingToAvoid = false;
				controller.setWheelSpeeds(motorSpeed, motorSpeed);
			}
			// Continue rotating if opponent is in front of us
			else {
				if (rotatingClockwise)
					controller.rotate(-turnAngle, turnSpeed);
				else
					controller.rotate(turnAngle, turnSpeed);
			}
		}
	}

	protected void moving(Controller controller) {

		Line frontLine = ourRobot.getFacingLine(0.24);
		RectangularObject frontRect = frontLine.widen(0.30);
		addDrawable(new DrawableRectangularObject(frontRect, Color.RED));

		if (ourRobot.willHitWall()) {
			LOG.info("Too close to the wall. Stopping.");
			controller.stop();
			done = true;
		}

		else if (movingToAvoid) {
			Coord currentPosition = ourRobot.getPosition();
			if (currentPosition.dist(startPoint) > 0.30) {
				LOG.info("Opponent out of the way. Going back to previous orientation.");
				moving = false;
				rotating = true;
				rotatingToAvoid = false;
				rotatingClockwise = !firstRotationClockwise;
			} else if (opponentInFront) {
				moving = false;
				rotating = true;
				rotatingToAvoid = true;
			}
		}

		else if (opponentInFront) {

			if (opponentDistance < 0.2) {

				LOG.info("opponent too close, backing off: " + opponentDistance);

				this.backtracks(controller);

			} else {

				controller.stop();
				// Turn
				LOG.info("Opponent is in the way. Starting to turn.");

				moving = false;
				rotating = true;
				rotatingToAvoid = true;

				double angle1 = 90.0;
				double angle2 = 270.0;
				
				Line faceLine = ourRobot.getFacingLine();
				
				Line customFaceLine1 = faceLine.rotateAroundPoint(
						faceLine.getA(), new Orientation(angle1, false));
				double minDist1 = ourRobot.distanceToFacingWall(customFaceLine1);
				
				Line customFaceLine2 = faceLine.rotateAroundPoint(
						faceLine.getA(), new Orientation(angle2, false));
				double minDist2 = ourRobot.distanceToFacingWall(customFaceLine2);
				
				if (minDist1 > minDist2) {
					LOG.info("Rotating anti-clockwise.");
					rotatingClockwise = false;
					firstRotationClockwise = false;
					controller.rotate(turnAngle, turnSpeed);
				} else {
					LOG.info("Rotating clockwise.");
					rotatingClockwise = true;
					firstRotationClockwise = true;
					controller.rotate(-turnAngle, turnSpeed);
				}
			}
		}
	}

	private void backtracks(Controller controller) {

		controller.setWheelSpeeds(-motorSpeed, -motorSpeed);

	}

	protected void startMoving(Controller controller) {
		LOG.info("Starting to move.");
		moving = true;
		rotating = false;
		controller.setWheelSpeeds(motorSpeed, motorSpeed);
	}

	// To make it a usable stand-alone strategy
	@FactoryMethod(designator = "Avoid Milestone 3", parameterNames = {})
	public static AvoidMilestone3 factoryMethod() {
		return new AvoidMilestone3();
	}
}