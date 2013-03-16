package balle.strategy.planner;

import java.awt.Color;
import balle.strategy.bezierNav.BezierNav;
import balle.strategy.curve.CustomCHI;
import balle.strategy.executor.movement.MovementExecutor;
import balle.strategy.executor.movement.OrientedMovementExecutor;
import balle.strategy.pathFinding.SimplePathFinder;
import balle.controller.Controller;
import balle.main.drawable.DrawableRectangularObject;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Pitch;
import balle.world.objects.Point;
import balle.world.objects.RectangularObject;
import balle.world.objects.Robot;

public class CurvyIntercept extends AbstractPlanner {
	
	Strategy GO;
	private MovementExecutor movementExecutor;
	private OrientedMovementExecutor orientedMovementExecutor;

	
    public CurvyIntercept(MovementExecutor movementExecutor,OrientedMovementExecutor orientedMovementExecutor) {
    	this.movementExecutor = movementExecutor;
        this.orientedMovementExecutor = orientedMovementExecutor;
    }

    @FactoryMethod(designator = "Curvy Intercept Strategy", parameterNames = {})
    public static final CurvyIntercept factoryNCPBZR() {
		return new CurvyIntercept (null, new BezierNav(
                new SimplePathFinder(
                new CustomCHI())));
    }


	///////////////////////////////////////ON STEP///////////////////////////////////
	
	
	
	
	
	@Override
	public void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {

		// Variables Used 
		Ball ball = snapshot.getBall();
		Pitch pitch = snapshot.getPitch();
		Robot opponent = snapshot.getOpponent();
		Robot ourRobot = snapshot.getBalle();		
		Line opponentLine = opponent.getFacingLine();
		Line pitchLine;
		RectangularObject goalRect;
		
		if (ball.getPosition() == null)
			return;
		if (ourRobot.getPosition() == null) 
			return;
		
		// Check for opponent position. If it is on left side then we need to intercept
		//towards right. Else towards left.
		if (opponent.getPosition().getX() >= 1.2){
			pitchLine = pitch.getBottomWall();
			goalRect = pitchLine.widen(0.2);
			pitchLine = goalRect.getRightSide();
			
		}else {
			pitchLine = pitch.getTopWall();
			goalRect = pitchLine.widen(0.2);
			pitchLine = goalRect.getLeftSide();
			
		}
		// Draw the rectangular object that is used for the interception	
		addDrawable(new DrawableRectangularObject(goalRect, Color.MAGENTA));
		
		Coord target = opponentLine.getIntersect(pitchLine);
		
		if (movementExecutor != null) {
            movementExecutor.updateTarget(new Point(target));
            addDrawables(movementExecutor.getDrawables());
            movementExecutor.step(controller, snapshot);

        } else if (orientedMovementExecutor != null) {
            orientedMovementExecutor.updateTarget(new Point(target),
                    snapshot.getOpponentsGoal().getGoalLine().midpoint()
                            .sub(target).orientation());
            addDrawables(orientedMovementExecutor.getDrawables());
            orientedMovementExecutor.step(controller, snapshot);

        }
    } 


}


