package balle.strategy.planner;

import java.awt.Color;

import org.apache.log4j.Logger;
import org.mockito.internal.util.ArrayUtils;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import balle.controller.Controller;
import balle.main.drawable.DrawableRectangularObject;
import balle.main.drawable.Dot;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Orientation;
import balle.world.Snapshot;
import balle.world.objects.Pitch;
import balle.world.objects.RectangularObject;
import balle.world.objects.Robot;
import java.util.Arrays;
import java.util.Collections;

public class AvoidMilestone3 extends AbstractPlanner {

	
	private static Logger LOG = Logger.getLogger(DribbleAndScore.class);
	private Robot ourRobot;
	private Robot enemyRobot;
	private int speed = 200;
	private Line[] pitchSides = new Line[4];
	private Strategy avoidStrategy;
	private boolean opponentInFront;
	private boolean rotating = false;
	private boolean followingOriginalDirection = true;
	private Orientation originalDirection;
	
	

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
		Line faceLine = ourRobot.getFacingLine(0.4);
		RectangularObject faceRect = faceLine.widen(0.24);
		
		// Check if opponent is in our way
		opponentInFront = contains(ourRobot, enemyRobot, faceRect);
		
		// Stop if facing wall is too close
		if (closeToWall(faceLine, pitchSides))
			controller.stop();
		
		else {
		
			// Move if opponent is not in front of us
			if (!opponentInFront) {
				LOG.info("No opponent in the way!");
				controller.setWheelSpeeds(speed, speed);
			}
			
			else {
				if (rotating && !opponentInFront) {
					LOG.info("No opponent in the way!");
					rotating = false;
					followingOriginalDirection = !followingOriginalDirection;
					controller.setWheelSpeeds(speed, speed);
				}
				// Otherwise turn
				else if (!rotating) {
					LOG.info("Opponent!");
					
					originalDirection = ourRobot.getOrientation();
					rotating = true;
					
					double angle1 = 90.0;
					double angle2 = 270.0;
					double minDist1 = distanceToClosestWall(ourRobot.getFacingLine(), pitchSides, angle1);
					double minDist2 = distanceToClosestWall(ourRobot.getFacingLine(), pitchSides, angle2);
					
					if (minDist1 > minDist2)
						controller.rotate(90, 50);
					else
						controller.rotate(-90, 50);
				}
			}
		}

		
		addDrawable(new DrawableRectangularObject(faceRect, Color.MAGENTA));
			
	}
	
	public static boolean closeToWall (Line facingLine, Line[] sides) {
		
		for (Line side : sides) {
			if (facingLine.intersects(side))
				return true;
		}
		
		return false;
	}
	

	public static double distanceToClosestWall (Line facingLine, Line[] pitchSides, double rotationAngle) {
		
		Coord startFaceLine = facingLine.getA();
		double minDistance = -1;
		
		Line side;
		Coord endFaceLine;
		Line distanceLine;
		
		
		facingLine = facingLine.rotateAroundPoint(startFaceLine, new Orientation(rotationAngle, false));
		
		for (int i = 0; i < 4; i++) {
			
			side = pitchSides[i];
			endFaceLine = facingLine.getIntersect(side);
			
			System.out.println(rotationAngle + ", " + startFaceLine.getOrientation() + ','+ i);
			if (endFaceLine != null) {
				distanceLine = new Line(startFaceLine, endFaceLine);
				if (distanceLine.length() < minDistance || minDistance == -1)
					minDistance = distanceLine.length();
			}
		}
		
		return minDistance;
	}
	
	public static boolean overOrUnderLine(Coord p1, Coord p2, Coord ptest) {
		double p1x = p1.x;
		double p1y = p1.y;
		double p2x = p2.x;
		double p2y = p2.y;
		
		double a = -(p2y - p1y);
		double b = p2x - p1x;
		double c = -(a*p1x + b*p1y);
		
		return (a*ptest.x + b*ptest.y + c) >= 0;
		
	}
	
	public static boolean contains(Robot ourRobot, Robot enemyRobot, RectangularObject areaLine) {
		
		Line leftSide = areaLine.getLeftSide();
		Line rightSide = areaLine.getRightSide();
		Line frontSide = areaLine.getFrontSide();
		Line backSide = areaLine.getBackSide();
		
		Coord corner1 = leftSide.getIntersect(frontSide);
		Coord corner2 = leftSide.getIntersect(backSide);
		Coord corner3 = rightSide.getIntersect(frontSide);
		Coord corner4 = rightSide.getIntersect(backSide);
		
		// Enemy robot
		Line enemyLeftSide = enemyRobot.getLeftSide();
		Line enemyRightSide = enemyRobot.getRightSide();
		Line enemyFrontSide = enemyRobot.getFrontSide();
		Line enemyBackSide = enemyRobot.getBackSide();
		
		
		Coord[] corners = new Coord[4];
		corners[0] = enemyLeftSide.getIntersect(enemyFrontSide);
		corners[1] = enemyLeftSide.getIntersect(enemyBackSide);
		corners[2] = enemyRightSide.getIntersect(enemyFrontSide);
		corners[3] = enemyRightSide.getIntersect(enemyBackSide);
		
		for (Coord point: corners) {
			boolean betweenLR = overOrUnderLine(corner1, corner2, point) != overOrUnderLine(corner3, corner4, point);
			boolean betweenFR = overOrUnderLine(corner1, corner3, point) != overOrUnderLine(corner2, corner4, point);
			
			if (betweenLR && betweenFR)
				return true;
		}
		
		return false;
	}
	

	// To make it a usable stand-alone strategy
	@FactoryMethod(designator = "Avoid Milestone 3", parameterNames = {})
	public static AvoidMilestone3 factoryMethod() {
		return new AvoidMilestone3();
	}
}
