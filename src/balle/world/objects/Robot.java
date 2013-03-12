package balle.world.objects;

import balle.misc.Globals;
import balle.world.AngularVelocity;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Orientation;
import balle.world.Velocity;

public class Robot extends RectangularObject {
	
	private final Pitch thePitch = new Pitch();

	public Robot(Coord position, Velocity velocity,
			AngularVelocity angularVelocity, Orientation orientation) {
		super(position, velocity, angularVelocity, orientation,
				Globals.ROBOT_WIDTH,
                Globals.ROBOT_LENGTH);
    }

    /**
     * Returns true if the robot is in possession of the ball. That is if the
     * ball is close enough to the kicker that it can kick it.
     * 
     * @param ball
     * @return true, if robot is in possession of the ball
     */
    public boolean possessesBall(Ball ball) {
        if ((ball.getPosition() == null) || (getPosition() == null))
            return false;

        double distance = getFrontSide().dist(ball.getPosition());
        return distance <= Globals.ROBOT_POSSESS_DISTANCE + ball.getRadius();
    }

    /**
     * Returns the line that would represents the path of the ball if the robot
     * kicked it
     * 
     * @param ball
     * @return
     */
    public Line getBallKickLine(Ball ball) {
        double x0, y0, x1, y1;
        x0 = ball.getPosition().getX();
        y0 = ball.getPosition().getY();

        Coord target = new Coord(Globals.ROBOT_MAX_KICK_DISTANCE, 0);
        target = target.rotate(getOrientation());

        x1 = x0 + target.getX();
        y1 = y0 + target.getY();

        return new Line(x0, y0, x1, y1);
    }

    /**
     * Returns that represents the robot's facing direction
     * 
     * @return the facing line
     */
    public Line getFacingLine() {
        double x0, y0, x1, y1;
        x0 = getPosition().getX();
        y0 = getPosition().getY();

        Coord target = new Coord(Globals.PITCH_WIDTH, 0);
        target = target.rotate(getOrientation());

        x1 = x0 + target.getX();
        y1 = y0 + target.getY();

        return new Line(x0, y0, x1, y1);
    }
    
    public Line getFacingLine(double length) {
        double x0, y0, x1, y1;
        x0 = getPosition().getX();
        y0 = getPosition().getY();

        Coord target = new Coord(length, 0);
        target = target.rotate(getOrientation());

        x1 = x0 + target.getX();
        y1 = y0 + target.getY();

        return new Line(x0, y0, x1, y1);
    }

    /**
     * Gets the facing line of the robot. Similar to the getFacingLine but the
     * line returned stretches both forward and backward from the robot.
     * 
     * @return the facing line both ways
     */
    public Line getFacingLineBothWays() {
        double x0, y0, x1, y1;

        Coord target = new Coord(Globals.PITCH_WIDTH, 0);
        target = target.rotate(getOrientation());

        x0 = getPosition().getX() - target.getX();
        y0 = getPosition().getY() - target.getY();
        x1 = getPosition().getX() + target.getX();
        y1 = getPosition().getY() + target.getY();

        return new Line(x0, y0, x1, y1);
    }

    /**
     * Checks if the robot can score from this position. That is if it is in
     * possession of the ball and facing the goal and other robot is is not
     * blocking the path to the goal.
     * 
     * @param ball
     * @param goal
     * @param otherRobot
     * @return true, if is in scoring position
     */
    public boolean isInScoringPosition(Ball ball, Goal goal, Robot otherRobot) {
        return possessesBall(ball) && isFacingGoal(goal)
                && ((otherRobot.getPosition() != null) && (!otherRobot
                        .intersects(getFacingLine())));
    }
    
    /**
     * Checks if the robot can score from this position. That is if it is in
     * possession of the ball, facing the goal and the ball is in a suitable 
     * range
     * 
     * @param ball
     * @param goal
     * @return true, if is in scoring position
     */
    public boolean canScoreNoOpposition(Ball ball, Goal goal) {
    	return possessesBall(ball) && isFacingGoal(goal) && 
    			(ball.getPosition().dist(goal.getPosition()) <= 0.75)
    			|| (this.getPosition().dist(goal.getPosition()) <= 0.75);
    }

    /**
     * Returns true if robot is facing the goal. Similar to isInScoringPosition
     * but does not check whether a robot has a ball and whether it is blocked
     * by other robot.
     * 
     * @param goal
     * @return
     */
    public boolean isFacingGoal(Goal goal) {

        if (getPosition() == null)
            return false;

        Line goalLine = goal.getGoalLine();
        Line facingLine = getFacingLine();

        return facingLine.intersects(goalLine);

    }

    /**
     * TODO write test
     * 
     * @return True, if robot is facing left.
     */
    public boolean isFacingLeft() {
        if (getOrientation() == null)
            return false;

        return (getOrientation().degrees() >= 90)
                && (getOrientation().degrees() <= 270);
    }

    /**
     * TODO write test
     * 
     * @return True, if the robot is facing right
     */
    public boolean isFacingRight() {
        if (getOrientation() == null)
            return false;

        return (getOrientation().degrees() <= 90)
                || (getOrientation().degrees() >= 270);
    }

    /**
     * Returns true if this robot is facing to the half of the pitch that this
     * goal is present.
     * 
     * TODO write test
     * 
     * @param goal
     * @return
     */
    public boolean isFacingGoalHalf(Goal goal) {
        return isFacingLeft() == goal.isLeftGoal();
    }

    /**
     * Returns the angle required to turn using atan2 style radians. Positive
     * angle means to turn CCW this much radians, whereas negative means turning
     * CW that amount of radians.
     * 
     * @param currentOrientation
     *            current orientation of the robot
     * @param targetOrientation
     *            target orientation
     * @return the angle to turn
     */
    public double getAngleToTurn(Orientation targetOrientation) {
        Orientation currentOr = getOrientation();
        if ((currentOr == null) || (targetOrientation == null))
            return 0;

        double angleToTarget = targetOrientation.atan2styleradians();
        double currentOrientation = currentOr.atan2styleradians();

        double turnLeftAngle, turnRightAngle;
        if (angleToTarget > currentOrientation) {
            turnLeftAngle = angleToTarget - currentOrientation;
            turnRightAngle = currentOrientation + (2 * Math.PI - angleToTarget);
        } else {
            turnLeftAngle = (2 * Math.PI) - currentOrientation + angleToTarget;
            turnRightAngle = currentOrientation - angleToTarget;
        }

        double turnAngle;

        if (turnLeftAngle < turnRightAngle)
            turnAngle = turnLeftAngle;
        else
            turnAngle = -turnRightAngle;

        return turnAngle;
    }

    /**
     * Returns the angle the robot has to turn to face the target coordinate
     * 
     * @param targetCoord
     *            the target coordinate
     * @return the angle to turn to target
     */
    public double getAngleToTurnToTarget(Coord targetCoord) {
        Coord currentPosition = getPosition();
        if ((currentPosition == null) || (targetCoord == null))
            return 0;

        return getAngleToTurn(targetCoord.sub(currentPosition).orientation());
    }

    public Coord getFrontLeftCornerCoord() {
        Coord leftSide = new Coord(getHeight() / 2, -getWidth() / 2);
        leftSide = leftSide.rotate(getOrientation());
        return getPosition().add(leftSide);
    }

    public Coord getFrontRightCornerCoord() {
        Coord rightSide = new Coord(getHeight() / 2, getWidth() / 2);
        rightSide = rightSide.rotate(getOrientation());
        return getPosition().add(rightSide);
    }

    public boolean canReachTargetInStraightLine(FieldObject target, FieldObject obstacle) {
        if (getPosition() == null)
            return false;

        Line pathToTarget1 = new Line(getPosition(), target.getPosition());
        Line pathToTarget2 = new Line(getFrontLeftCornerCoord(),
                target.getPosition());
        Line pathToTarget3 = new Line(getFrontRightCornerCoord(),
                target.getPosition());

        // Check if it is blocking our path
        return (!obstacle.intersects(pathToTarget1)
                && !obstacle.intersects(pathToTarget2) && !obstacle
                .intersects(pathToTarget3));
    }

	public boolean isApproachingTargetFromCorrectSide(FieldObject target,
			Goal opponentsGoal) {
		return isApproachingTargetFromCorrectSide(target, opponentsGoal,
				Globals.OVERSHOOT_ANGLE_EPSILON);
	}

    public boolean isApproachingTargetFromCorrectSide(FieldObject target,
			Goal opponentsGoal, double overshootAngleEpsilon) {

        if (getPosition() == null)
            return false;
        if (target.getPosition() == null)
            return true;

        Orientation robotToTargetOrientation = target.getPosition()
                .sub(getPosition()).orientation();

        if (opponentsGoal.isLeftGoal()
				&& (robotToTargetOrientation.degrees() > 90 + overshootAngleEpsilon)
				&& (robotToTargetOrientation.degrees() < 270 - overshootAngleEpsilon)) {
            return true;
        } else if ((opponentsGoal.isRightGoal())
				&& ((robotToTargetOrientation.degrees() < 90 - overshootAngleEpsilon) || (robotToTargetOrientation
						.degrees() > 270 + overshootAngleEpsilon))) {
            return true;
        } else
            return false;

    }

    /**
     * TODO TEST!!!!!!
     * 
     * @param ourRobot
     * @param ball
     * @param cw
     *            Clock-wise rotation if true, CCW if false.
     * @return
     */
    public Orientation findMaxRotationMaintaintingPossession(Ball ball,
            boolean cw) {
        Coord fl = new Coord(10, 0);
        fl = fl.rotate(getOrientation()).add(getPosition());
        Orientation max, o = getPosition().angleBetween(fl, ball.getPosition());

        if (cw) {
            max = (new Coord(0, 0)).angleBetween(new Coord(10, 0), new Coord(
                    Globals.ROBOT_LENGTH, Globals.ROBOT_WIDTH));
        } else {
            max = (new Coord(0, 0)).angleBetween(new Coord(10, 0), new Coord(
                    Globals.ROBOT_LENGTH, -Globals.ROBOT_WIDTH));
        }
        // System.out.println("max = " + max + ",\to = " + o + ",\tm-o = "
        // + max.sub(o));

        return max.sub(o);

    }

	public static final Coord relL = new Coord(-Globals.ROBOT_TRACK_WIDTH / 2,
			0);
	public static final Coord relR = new Coord(Globals.ROBOT_TRACK_WIDTH / 2, 0);

	private double helperWheelSpeed(Coord rel) {
		Orientation dAng = new Orientation( getAngularVelocity().radians() );
		
		Coord s, f, delta;
		s = rel;
		f = getVelocity().add(rel.rotate(dAng));
		delta = f.sub(s);

		double cst = Math.PI - 2;
		double var = dAng.radians() / Math.PI;
		double abs = delta.abs();

		double out = (abs + abs * var * cst) / Globals.MAX_WHEEL_SPEED;

		if (delta.getY() < 0)
			return out;
		else
			return out;
	}

	public double getLeftWheelSpeed() {
		return helperWheelSpeed(relL);
	}

	public double getRightWheelSpeed() {
		return helperWheelSpeed(relR);
	}
	
	/**
	 * Returns a facing rectangle for the robot, with appropriate 
	 * dimensions to detect whether the robot will hit a wall
	 * 
	 * @return a rectangle that acts as a wall-detector
	 */
	private RectangularObject wallDetectionRect() {
		// Get facing line of appropriate length
		Line frontLine = this.getFacingLine(0.24);
		
		// Widen facing line to obtain a wall-detecting facing rectangle
		RectangularObject frontRect = frontLine.widen(0.30);
		
		return frontRect;
	}
	
    /**
     * Determines whether the robot is going to hit any wall, provided
     * it goes it a straight line.
     * 
     * @return true if it will hit a wall, false otherwise
     */
	public boolean willHitWall() {
		
		return willHitLeftWall() || willHitRightWall() || willHitTopWall() || willHitBottomWall();
	}
	
    /**
     * Determines whether the robot is going to hit the left wall, 
     * provided it goes it a straight line.
     * 
     * @return true if robot will hit wall, false otherwise
     */
	public boolean willHitLeftWall() {
		
		return wallDetectionRect().intersects(thePitch.getLeftWall());
	}
	
    /**
     * Determines whether the robot is going to hit the right wall, 
     * provided it goes it a straight line.
     * 
     * @return true if robot will hit wall, false otherwise
     */
	public boolean willHitRightWall() {
		
		return wallDetectionRect().intersects(thePitch.getRightWall());
	}
	
    /**
     * Determines whether the robot is going to hit the top wall, 
     * provided it goes it a straight line.
     * 
     * @return true if robot will hit wall, false otherwise
     */
	public boolean willHitTopWall() {
		
		return wallDetectionRect().intersects(thePitch.getTopWall());
	}
	
    /**
     * Determines whether the robot is going to hit the bottom wall, 
     * provided it goes it a straight line.
     * 
     * @return true if robot will hit wall, false otherwise
     */
	public boolean willHitBottomWall() {
		
		return wallDetectionRect().intersects(thePitch.getBottomWall());
	}
	
    /**
     * Computes the distance from the robot to the wall the robot is 
     * facing. If the wall is facing between two walls, it returns the
     * distance from the robot to the closer wall.
     * 
     * @return true if robot will hit wall, false otherwise
     */
	public double distanceToFacingWall() {
		
		return this.distanceToFacingWall(this.getFacingLine());
	}
	
    /**
     * Computes the distance from the robot to the wall the robot is 
     * facing, using a custom facing line. If the wall is facing between two 
     * walls, it returns the distance from the robot to the closer wall.
     * 
     * @param the custom facing line
     * @return true if robot will hit wall, false otherwise
     */
	public double distanceToFacingWall(Line customFaceLine) {
		
		// Start point of the facing line
		Coord startFaceLine = customFaceLine.getA();
		
		// Point representing the intersection between the facing line
		// and a wall
		Coord intersectionFaceLineWall;
		
		// Line from startFaceLine to intersectionFaceLineWall
		Line faceLineUntilWall;
		
		// Sides of the pitch
		Line pitchSide;
		Line[] pitchSides = thePitch.getWalls();
		
		// Initiate the minimum distance to negative
		double minDistance = -1;

		// Iterate over the four walls
		for (int i = 0; i < 4; i++) {

			pitchSide = pitchSides[i];
			intersectionFaceLineWall = customFaceLine.getIntersect(pitchSide);

			// If there is an intersection between wall and facing line
			if (intersectionFaceLineWall != null) {
				
				// Get line from startFaceLine to intersectionFaceLineWall
				faceLineUntilWall = new Line(startFaceLine, intersectionFaceLineWall);
				
				// If length of the line is smaller than minDistance or
				// minDistance has never been updated, update minDistance as
				// the length of the line
				if (faceLineUntilWall.length() < minDistance || minDistance == -1)
					minDistance = faceLineUntilWall.length();
			}
		}

		return minDistance;
	}
}
