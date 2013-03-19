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
import balle.world.objects.Pitch;
import balle.world.objects.Robot;

public class Intercept extends AbstractPlanner {

	private static Logger LOG = Logger.getLogger(DribbleAndScore.class);
	
	// Strategy
	private Strategy interceptStrategy;

	// Flag that is set if there is nothing else to do
	private boolean done = false;
	
	// Pitch
	private Pitch pitch;
	
	// Robots and ball
	private Robot ourRobot;
	private Robot otherRobot;
	private Ball ball;
	
	// Positions of robots and ball
	private Coord ourPosition;
	private Coord otherPosition;
	private Coord ballPosition;
	
	// Records ball position when it moves at least 3cm
	private Coord lastBallPosition;
	
	// The ball's direction (if ball is moving) or the other robot's 
	// facing line otherwise
	private Line referenceLine;
	
	// Do we need to get in front of the ball? 
	private Boolean rotateToReferenceLine = false;
	
	private Boolean moving = false;
	
	private Orientation targetOrientation;
	
	private boolean ballMoved = false;
	
	

	public Intercept() {

		interceptStrategy = new GoToBallSafeProportional();

	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		interceptStrategy.stop(controller);
	}

	@Override
	protected void onStep(Controller controller, Snapshot snapshot)
			throws ConfusedException {

		if (done) {
			return;
		}
		
		// Get pitch
		pitch = snapshot.getPitch();
		
		// Get robots and ball
		ourRobot = snapshot.getBalle();
		otherRobot = snapshot.getOpponent();
		ball = snapshot.getBall();
		
		// Get positions of robots and ball
		ourPosition = ourRobot.getPosition();
		otherPosition = otherRobot.getPosition();
		ballPosition = ball.getPosition();

		// Check if our robot is actually on the pitch
		if (ourPosition == null) {
			LOG.info("Where am I? :(");
			return;
		}
		
		// Check if other robot is actually on the pitch
		if (otherPosition == null) {
			LOG.info("Where is enemy? :(");
			return;
		}
		
		// Check if ball is actually on the pitch
		if (ballPosition == null) {
			LOG.info("Where is ball? :(");
			return;
		}
		
		// Set the previously recorded ball position for the first time
		if (lastBallPosition == null)
			lastBallPosition = ballPosition;
		
		// If the reference line is not yet the ball direction, set it to be
		// the other robot's facing line
		if (referenceLine == null)
			referenceLine = otherRobot.getFacingLine();
		
		// Update the reference line if the ball has moved at least 3cm
		if (ballPosition.dist(lastBallPosition) > 0.03) {
			
			ballMoved = true;
			
			double referenceSlope = (ballPosition.y - lastBallPosition.y)/(ballPosition.x - lastBallPosition.x);
			double referenceIntercept = ballPosition.y - (referenceSlope*ballPosition.x);
			
			double newX;
			double newY;
			
			if (ballPosition.x > lastBallPosition.x)
				newX = ballPosition.x + 3;
			else
				newX = 0;
			
			newY = (referenceSlope*newX) + referenceIntercept;
				
			referenceLine = new Line(lastBallPosition, new Coord(newX, newY));
			lastBallPosition = ballPosition;
		}
		
		if (moving) {
			if (referenceLine.dist(ourPosition) < 0.25) {
				controller.stop();
				moving = false;
				done = true;
				return;
			}
		}
		
		if (rotateToReferenceLine) {
			
			double ourOrientation = ((ourRobot.getOrientation().degrees() % 180) + 180) % 180;
			double targetOrientationModulo = ((targetOrientation.degrees() % 180) + 180) % 180;

			if (Math.abs(ourOrientation - targetOrientationModulo) < 5) {
				if (ballMoved) {
					rotateToReferenceLine = false;
					moving = true;
					controller.setWheelSpeeds(600, 600);
				}
				else 
					controller.stop();
				return;
			}
			
		}
		
		if (!moving && !rotateToReferenceLine) {
			
			// The point we should go towards, i.e. closest point on
			// reference line to our robot's position
			Coord target = referenceLine.closestPoint(ourPosition);
			
			// We want our robot's facing line to have the same direction
			// as the line from our robot's centre to the target
			Line targetLine = new Line (ourPosition, target);
			
			// The orientation we want to achieve is the same as targetLine's orientation
			targetOrientation = targetLine.angle();
			
			if (referenceLine.dist(ourPosition) < 0.05) {
				done = true;
				return;
			}
			
			else if (Math.abs(ourRobot.getOrientation().degrees() - targetOrientation.degrees()) < 45
					|| Math.abs((ourRobot.getOrientation().degrees() + 360) - targetOrientation.degrees()) < 45
					|| Math.abs(ourRobot.getOrientation().degrees() - (targetOrientation.degrees() + 360)) < 45) {
				if (ballMoved) {
				
					controller.setWheelSpeeds(600, 600);
					moving = true;
				}
				return;
			}
			
			else {
				// Get the orientation we should turn in order to achieve that orientation
				double orientationToTurn = ourRobot.getAngleToTurn(targetOrientation);

				controller.rotate((int)Math.toDegrees(orientationToTurn), 80);

				return;
			}
		}
		
		if (moving) 
			return;
		
		 {
			LOG.info("here");

		}
		
		


		// If we should rotate towards the reference line
		if (false){
			// The point we should go towards, i.e. closest point on
			// reference line to our robot's position
			Coord target = referenceLine.closestPoint(ourPosition);
			
			// We want our robot's facing line to have the same direction
			// as the line from our robot's centre to the target
			Line targetLine = new Line (target, ourPosition);
			
			// The orientation we want to achieve is the same as targetLine's orientation
			Orientation targetOrientation = targetLine.angle();
			
			// Get the orientation we should turn in order to achieve that orientation
			double orientationToturn = ourRobot.getAngleToTurn(targetOrientation);


			
			double ourOrientation = ((ourRobot.getOrientation().degrees() % 180) + 180) % 180;
			double targetOrientationModulo = ((targetOrientation.degrees() % 180) + 180) % 180;

			if (Math.abs(ourOrientation - targetOrientationModulo) < 5) {
				if (ballMoved) {
					rotateToReferenceLine = false;
					controller.setWheelSpeeds(600, 600);
				}
				return;
			}
			
//			if (Math.abs(ourRobot.getOrientation().degrees() - targetOrientation.degrees()) < 5) {
//				rotateToReferenceLine = false;
//				controller.setWheelSpeeds(200, 200);
//				return;
//			}
			
			else
				controller.rotate(-(int)Math.toDegrees(orientationToturn), 30);
			
			return;
			
		}
		

		
//		if (rotateBackToBall == true) {
//			Line targetLine = new Line (referenceLine.getB(), referenceLine.getA());
//			Orientation targetOrientation = targetLine.angle();
//
//
//
//			double orientationToturn = ourRobot.getAngleToTurn(targetOrientation);
//
//
//			
//			//double ourOrientation = ((ourRobot.getOrientation().degrees() % 180) + 180) % 180;
//			//double targetOrientationModulo = ((targetOrientation.degrees() % 180) + 180) % 180;
//			
//			LOG.info("Our " + ourRobot.getOrientation().degrees());
//			LOG.info("Opponent " + targetOrientation.degrees());
//
//			if (Math.abs(ourRobot.getOrientation().degrees() - targetOrientation.degrees()) < 5) {
//				done = true;
//				controller.stop();
//				return;
//			}
//			
//			else
//				controller.rotate((int)Math.toDegrees(orientationToturn), 30);
//			
//			return;
//			
//		}
//		
//		else {
//			double distanceToReference = referenceLine.dist(ourPosition);
//			
//			if (distanceToReference < 0.25) {
//				rotateBackToBall = true;
//			}
//		}
	}

	// To make it a usable stand-alone strategy
	@FactoryMethod(designator = "Intercept", parameterNames = {})
	public static Intercept factoryMethod() {
		return new Intercept();
	}
}






//COMPUTE POINT WHERE BALL WILL STOP. 
//DON'T NEED FOR THE MOMENT
//	
//
//	if (originalBallPosition == null) {
//	originalBallPosition = ballPosition;
//	return;
//}
//
//if (ballPosition.dist(originalBallPosition) < 0.02) 
//	return;
//		if (true) {
//			if (lastPosition == null) {
//				lastPosition = ballPosition;
//				lastTime = System.currentTimeMillis();
//				return;
//			}
//			
//			double currentSpeed = Math.abs(ballPosition.dist(lastPosition))/Math.abs(System.currentTimeMillis() - lastTime);
//			
//			if (lastSpeed == -1) {
//				lastSpeed = currentSpeed;
//				return;
//			}
//			
//			if (currentSpeed > lastSpeed) {
//				lastPosition = ballPosition;
//				lastSpeed = currentSpeed;
//				return;
//			}
//			
//			// Predict how much time will pass before ball stops
//			double travellingTime = currentSpeed/decelerationConstant;
//			
//			// Predict how far the ball will travel
//			double ballTravelDistance = decelerationConstant * Math.pow(travellingTime, 2)/2;
//			
//			double lineSlope = (originalBallPosition.y - ballPosition.y)/(originalBallPosition.x - ballPosition.x);
//			
//			double futureX = ballPosition.x;
//			double deltaX = Math.abs(ballTravelDistance*Math.cos((Math.atan(lineSlope))));
//			if (ballPosition.x >= originalBallPosition.x)
//				futureX += deltaX;
//			else 
//				futureX -= deltaX;
//			
//			double futureY = ballPosition.y;
//			double deltaY = Math.abs(ballTravelDistance*Math.sin((Math.atan(lineSlope))));
//			if (ballPosition.y >= originalBallPosition.y)
//				futureY += deltaY;
//			else 
//				futureY -= deltaY;
//			
//			if (ballStopPoint == null) {
//				ballStopPoint = new Coord(futureX, futureY);
//			}
//			
//
//			
//		
//		if (ballStopPoint != null)
//			addDrawable(new Dot(ballStopPoint, Color.BLACK));