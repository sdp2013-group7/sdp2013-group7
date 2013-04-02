package balle.strategy;

import java.awt.Color;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import balle.controller.Controller;
import balle.main.drawable.Circle;
import balle.main.drawable.Drawable;
import balle.main.drawable.DrawableLine;
import balle.main.drawable.DrawableRectangularObject;
import balle.main.drawable.Label;
import balle.misc.Globals;
import balle.simulator.SnapshotPredictor;
import balle.strategy.bezierNav.BezierNav;
import balle.strategy.curve.CustomCHI;
import balle.strategy.executor.movement.GoToObjectPFN;
import balle.strategy.executor.movement.ModifiedGoToObjectPFN;
import balle.strategy.executor.turning.IncFaceAngle;
import balle.strategy.executor.turning.RotateToOrientationExecutor;
import balle.strategy.pathFinding.SimplePathFinder;
import balle.strategy.planner.AbstractPlanner;
import balle.strategy.planner.BackingOffStrategy;
import balle.strategy.planner.DefensiveStrategy;
import balle.strategy.planner.GoToBall;
import balle.strategy.planner.GoToBallSafeProportional;
import balle.strategy.planner.InitialBezierStrategy;
import balle.strategy.Dribble2;
import balle.strategy.NewDribble;
import balle.strategy.planner.SimpleGoToBallFaceGoal;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Goal;
import balle.world.objects.Pitch;
import balle.world.objects.RectangularObject;
import balle.world.objects.Robot;

public class Game2 extends AbstractPlanner {

    private static final Logger LOG = Logger.getLogger(Game2.class);
    
	// Strategies that we will need make sure to call stop() for each of them
	
    protected final BackingOffStrategy backingOffStrategy;
	protected final RotateToOrientationExecutor turningExecutor;
    protected final Dribble2 kickingStrategy;
    protected final NewDribble pickBallFromWallStrategy;
    protected final Strategy defensiveStrategy;
    protected final Strategy opponentKickDefendStrategy;
    protected final Strategy goToBallPFN;
	protected final Strategy goToBallBezier;
    protected final Strategy goToBallPrecision;
    protected Strategy initialStrategy;

    protected boolean initial;

    private String currentStrategy = null;

    
    // return the strategy that is currently being run
    public String getCurrentStrategy() {
    	
        return currentStrategy;
    }

    // return the first strategy that was used
    public Strategy getInitialStrategy() {
    	
        return initialStrategy;
    }

    // set the first strategy that is to be used
    public void setInitialStrategy(Strategy initialStrategy) {
    	
        this.initialStrategy = initialStrategy;
    }

    @Override
    public ArrayList<Drawable> getDrawables() {
    	
        ArrayList<Drawable> drawables = super.getDrawables();
        
        if (currentStrategy != null){
        	
            drawables.add(new Label(currentStrategy, new Coord(
                    Globals.PITCH_WIDTH - 0.5, Globals.PITCH_HEIGHT + 0.2),
                    Color.WHITE));
        }
        
        return drawables;
    }

    // set strategy that is to be used right when this function is called
    public void setCurrentStrategy(String currentStrategy) {
    	
        this.currentStrategy = currentStrategy;
    }

    // put strategy into simulator
//    @FactoryMethod(designator = "Game2", parameterNames = { "init", "no bounce shots" })
    
    public static Game2 Game2FactoryTesting2(boolean init, boolean notTriggerHappy) {
    	
        Game2 g = new Game2(init);
        return g;
    }
    
    @FactoryMethod(designator = "Game2", parameterNames = {} )
    public static Game2 game2ForTournament() {
    	Game2 g = new Game2(true);
    	//g.setTriggerHappy(false);
    	return g;
    }

    // test method? seems like they were experimenting with values to see if it would improve initial performance
//    @FactoryMethod(designator = "Game2InitTest", parameterNames = { "angle (deg)" })
    
    public static Game2 Game2InitTest(double angle) {
    	
        Game2 g = new Game2(true);
        g.setInitialStrategy(new InitialBezierStrategy(angle));
        return g;
    }


    // initialise strategies that are to be used
    public Game2() {
    	
    	defensiveStrategy = new GoToBallSafeProportional(0.5, 0.4, true);
        opponentKickDefendStrategy = new DefensiveStrategy(new GoToObjectPFN(0));
        pickBallFromWallStrategy = new NewDribble(new ModifiedGoToObjectPFN(0));
		backingOffStrategy = new BackingOffStrategy();
        turningExecutor = new IncFaceAngle();
        kickingStrategy = new Dribble2();
        initialStrategy = new GoToBall(new GoToObjectPFN(0), false);
		goToBallPFN = new GoToBallSafeProportional();
		goToBallBezier = new SimpleGoToBallFaceGoal(new BezierNav(new SimplePathFinder(new CustomCHI())));
        goToBallPrecision = new GoToBall(new GoToObjectPFN(0), false);
        initial = false;
    }

    // check if robot is in it's starting position/has not moved yet.
    public boolean isInitial(Snapshot snapshot) {
    	
        if (initial == false){
        	
            return false;
        }

        // Check if we have ball
        Ball ball = snapshot.getBall();
        
        if (ball.getPosition() == null){
        	
            return initial; // Return whatever is set to initial if we do not see it
        }
        
        Coord centerOfPitch = new Coord(Globals.PITCH_WIDTH / 2, Globals.PITCH_HEIGHT / 2);
        Robot ourRobot = snapshot.getBalle();
        
        // If we have the ball, turn off initial strategy
        if ((ourRobot.getPosition() != null) && (ourRobot.possessesBall(ball))){
        	
            LOG.info("We possess the ball. Turning off initial strategy");
            setInitial(false);
        }
        
        // else If ball has moved 5 cm, turn off initial strategy
        else if (ball.getPosition().dist(centerOfPitch) > 0.05) {
        	
            LOG.info("Ball has moved. Turning off initial strategy");
            setInitial(false);
        }
        
        return initial;
    }

    // set if robot is to use initial strategy
    public void setInitial(boolean initial) {
    	
        this.initial = initial;
    }

    // starts initial strategy, is only called at the start of a match
    public Game2(boolean startWithInitial) {
    	
        this();
        initial = startWithInitial;
        LOG.info("Starting Game2 strategy with initial strategy turned on");
    }

    // stop strategies that would not normally stop themselves
    @Override
    public void stop(Controller controller) {
    	pickBallFromWallStrategy.stop(controller);
        defensiveStrategy.stop(controller);
        backingOffStrategy.stop(controller);
    }

    
    
    ////////////////////////////////////////////////////////////// ON STEP //////////////////////////////////////////////////////////////////////////////////
    
    
    @Override
    public void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {

    	// get our robot and the ball
        Robot ourRobot = snapshot.getBalle();
        Ball ball = snapshot.getBall();

        // if neither the ball nor the robot can be found then return
        if ((ourRobot.getPosition() == null) || (ball.getPosition() == null)){
        
            return;
        }
        
        // if the robot needs to back off of something then backing off strategy will override whatever strategy is currently being used (steal the step)
		if (backingOffStrategy.shouldStealStep(snapshot)) {
			
			backingOffStrategy.step(controller, snapshot);
			return;
		}

		// if it is the start of the match use initial strategy (InitialBezierStrategy)
        if (isInitial(snapshot)) {
        	
            setCurrentStrategy(initialStrategy.getClass().getName());
            initialStrategy.step(controller, snapshot);

            addDrawables(initialStrategy.getDrawables());
            return;
        }

        //SnapshotPredictor sp = snapshot.getSnapshotPredictor();
        // addDrawable(new Circle(ourRobot.getFrontSide().midpoint(), 0.07,
        // Color.GREEN));
        // addDrawable(new Circle(ourRobot.getFrontSide().midpoint(), 0.1,
        // Color.RED));

        // set current strategy as old and line up next strategy for use
        String oldStrategy = getCurrentStrategy();
		Strategy strategy = getStrategy(snapshot);
        LOG.debug("Selected strategy: " + strategy.getClass().getName());
        
        // use strategy that was chosen
		setCurrentStrategy(strategy.getClass().getName());
		
		// if dribble_M4 doesn't equal the new strategy
        if ("balle.strategy.Dribble_M4".equals(oldStrategy)&& !oldStrategy.equals(getCurrentStrategy())) {
        	
            LOG.info("Stopped using Dribble2 for " + getCurrentStrategy());
            LOG.info(ourRobot.getOrientation().degrees());
            LOG.info(ourRobot.getFrontSide().midpoint() .dist(ball.getPosition()));
        }
        
        // try to use strategy in step
        try {
        	
            strategy.step(controller, snapshot);
        
        } catch (ConfusedException e) {
        	
            // If a strategy does not know what to do
			LOG.error("Game2 catch block.", e);
            // Default to goToBallPFN
            goToBallPFN.step(controller, snapshot);
        }

		addDrawables(strategy.getDrawables());
    }

    // function to choose strategy to be used
	private Strategy getStrategy(Snapshot snapshot) {
		
		// get all objects
		Robot ourRobot = snapshot.getBalle();
		Robot opponent = snapshot.getOpponent();
		Ball ball = snapshot.getBall();
		Goal ownGoal = snapshot.getOwnGoal();
		Goal opponentsGoal = snapshot.getOpponentsGoal();
		Pitch pitch = snapshot.getPitch();

        addDrawable(new Circle(ourRobot.getFrontSide().midpoint(), 0.4,Color.BLUE));

        
        SnapshotPredictor sp = snapshot.getSnapshotPredictor();
        Snapshot newsnap = sp.getSnapshotAfterTime(50);
        RectangularObject dribbleBox = ourRobot.getFrontSide().extendBothDirections(0.01).widen(0.25);
        addDrawable(new DrawableRectangularObject(dribbleBox, Color.CYAN));
        

        addDrawable(new DrawableLine(newsnap.getBalle().getFrontSide(), Color.red));

		boolean nearLeftOrRight =(pitch.getTopWall().dist(ball.getPosition())<0.1||pitch.getBottomWall().dist(ball.getPosition())<0.1);
        
		if (ball.isNearWall(snapshot.getPitch())&&!nearLeftOrRight){
        	
            return pickBallFromWallStrategy;
        }
        
        // if kickingstrat dribbling the ball and it's position is estimated and ball less than 2 robot lengths away or (ball in the dribblebox and robot not facing own goal)
        if ((kickingStrategy.isDribbling() && ball.getPosition().isEstimated() && ball
                .getPosition().dist(ourRobot.getPosition()) < Globals.ROBOT_LENGTH * 3)
                || (dribbleBox.containsCoord(ball.getPosition()) && !ourRobot
                        .isFacingGoalHalf(ownGoal))) {
        	
            addDrawable(new Label("DRIBBLING", ball.getPosition().sub(
                    new Coord(0.1, 0.1)), Color.CYAN));
            
            // continue using kicking strategy
            return kickingStrategy;
		}
        
        // if opponent has a position with the ball and is facing our goal and our robot doesn't intersect the kick line from the opponent
        if ((opponent.getPosition() != null)
                && (opponent.possessesBall(ball) && (opponent
                        .isFacingGoal(ownGoal)))
                && (!ourRobot.intersects(opponent.getBallKickLine(ball)))) {
        	
        	// use this strategy
            return opponentKickDefendStrategy;
        }

		// create line that goes from our robot to the ball, as wide as the ball + 0.5
		RectangularObject corridor = new Line(ourRobot.getPosition(),ball.getPosition()).widen(0.5);
		
		// draw line on screen
        addDrawable(new DrawableRectangularObject(corridor, Color.BLACK));
        
        // if opponent is in above line, and doesn't possess ball and isn't facing our goal
        if ((corridor.containsCoord(opponent.getPosition()))&& !(opponent.possessesBall(ball) && (opponent.isFacingGoalHalf(ownGoal)))) {
        	
        	//use this strat
			return goToBallBezier;
		}
		
        // estimate where the ball is going to go
        Line ballMovementLine = new Line(ball.getPosition(), snapshot.getBallEstimator().estimatePosition(40));
		
        // draw line
        addDrawable(new DrawableLine(ballMovementLine, Color.MAGENTA));
        
        // if ball is moving towards our goal
        if (ballMovementLine.intersects(snapshot.getOwnGoal().getGoalLine().extendBothDirections(0.5))) {
        	
            return defensiveStrategy;
        }
		    

        // if the robot isn't approaching the ball fromt he correct side
		if (!ourRobot.isApproachingTargetFromCorrectSide(ball, opponentsGoal, 25)) {
			
			return goToBallPFN;
		}

		// if distance from our robot to the ball is greater than a meter
		if (ourRobot.getPosition().dist(ball.getPosition()) > 1) {
			
			return goToBallPFN;
		}

		// if robot is near wall and (ball isn't near wall or robot is farther than 0.5m away)
		if (ourRobot.isNearWall(pitch)
				&& (!ball.isNearWall(pitch) || ourRobot.getPosition().dist(ball.getPosition()) > 0.5)) {
			
			return goToBallPFN;
		}
		


        // if not any of the above, use this one
		return goToBallPFN;

	}
}
