/*
 * AIController.java
 *
 * This class is an inplementation of InputController that uses AI and pathfinding
 * algorithms to determine the choice of input.
 *
 * NOTE: This is the file that you need to modify.  You should not need to 
 * modify any other files (though you may need to read Board.java heavily).
 *
 * Author: Walker M. White, Cristian Zaloj
 * Based on original AI Game Lab by Yi Xu and Don Holden, 2007
 * LibGDX version, 1/24/2015
 */
package edu.cornell.gdiac.ailab;

import java.util.*;

/** 
 * InputController corresponding to AI control.
 * 
 * REMEMBER: As an implementation of InputController you will have access to
 * the control code constants in that interface.  You will want to use them.
 */
public class AIController implements InputController {
	/**
	 * Enumeration to encode the finite state machine.
	 */
	private static enum FSMState {
		/** The ship just spawned */
		SPAWN,
		/** The ship is patrolling around without a target */
		WANDER,
		/** The ship has a target, but must get closer */
		CHASE,
		/** The ship has a target and is attacking it */
		ATTACK
	}

	// Constants for chase algorithms
	/** How close a target must be for us to chase it */
	private static final int CHASE_DIST  = 9;
	/** How close a target must be for us to attack it */
	private static final int ATTACK_DIST = 4;

	// Instance Attributes
	/** The ship being controlled by this AIController */
	private Ship ship;
	/** The game board; used for pathfinding */
	private Board board;
	/** The other ships; used to find targets */
	private ShipList fleet;
	/** The ship's current state in the FSM */
	private FSMState state;
	/** The target ship (to chase or attack). */
	private Ship target; 
	/** The ship's next action (may include firing). */
	private int move; // A ControlCode
	/** The number of ticks since we started this controller */
	private long ticks;

	// Custom fields for AI algorithms
	//#region ADD YOUR CODE: 
	private class Tile{
		int x;
		int y;
		Tile parent;
		
		public Tile(int x, int y, Tile parent){
			this.x = x;
			this.y = y;
			this.parent = parent;
		}
	}
	
	private long lastTick = 0;
	
	//#endregion

	/**
	 * Creates an AIController for the ship with the given id.
	 *
	 * @param id The unique ship identifier
	 * @param board The game board (for pathfinding)
	 * @param ships The list of ships (for targetting)
	 */
	public AIController(int id, Board board, ShipList ships) {
		this.ship = ships.get(id);
		this.board = board;
		this.fleet = ships;

		state = FSMState.SPAWN;
		move  = CONTROL_NO_ACTION;
		ticks = 0;

		// Select an initial target
		target = null;
		selectTarget();
	}

	/**
	 * Returns the action selected by this InputController
	 *
	 * The returned int is a bit-vector of more than one possible input 
	 * option. This is why we do not use an enumeration of Control Codes;
	 * Java does not (nicely) provide bitwise operation support for enums. 
	 *
	 * This function tests the environment and uses the FSM to chose the next
	 * action of the ship. This function SHOULD NOT need to be modified.  It
	 * just contains code that drives the functions that you need to implement.
	 *
	 * @return the action selected by this InputController
	 */
	public int getAction() {
		// Increment the number of ticks.
		ticks++;

		// Do not need to rework ourselves every frame. Just every 10 ticks.
		if ((ship.getId() + ticks) % 10 == 0) {
			// Process the FSM
			changeStateIfApplicable();

			// Pathfinding
			markGoalTiles();
			move = getMoveAlongPathToGoalTile();
		}

		int action = move;

		// If we're attacking someone and we can shoot him now, then do so.
		if (state == FSMState.ATTACK && canShootTarget()) {
			action |= CONTROL_FIRE;
		}

		return action;
	}

	// FSM Code for Targeting (MODIFY ALL THE FOLLOWING METHODS)

	/**
	 * Change the state of the ship.
	 *
	 * A Finite State Machine (FSM) is just a collection of rules that,
	 * given a current state, and given certain observations about the
	 * environment, chooses a new state. For example, if we are currently
	 * in the ATTACK state, we may want to switch to the CHASE state if the
	 * target gets out of range.
	 */
	private void changeStateIfApplicable() {
		// Add initialization code as necessary
		//#region PUT YOUR CODE HERE
		if (ticks - lastTick < 10) return;
		lastTick = ticks;
		selectTarget();
		Random rand = new Random();
		//#endregion

		// Next state depends on current state.
		switch (state) {
		case SPAWN: // Do not pre-empt with FSMState in a case
			// Insert checks and spawning-to-??? transition code here
			//#region PUT YOUR CODE HERE
			state = FSMState.WANDER;
			//#endregion
			break;

		case WANDER: // Do not pre-empt with FSMState in a case
			// Insert checks and moving-to-??? transition code here
			//#region PUT YOUR CODE HERE
			// When a target becomes available, there is 80% possibility 
			// that the state changes to CHASE
			if (target!=null && rand.nextDouble()<0.8){
				state = FSMState.CHASE;
			}else{
				state = FSMState.WANDER;
			}
			//#endregion
			break;

		case CHASE: // Do not pre-empt with FSMState in a case
			// insert checks and chasing-to-??? transition code here
			//#region PUT YOUR CODE HERE 
			// When the ship can shoot the target, state changes to ATTACK.
			// When the target is null or the target is outside CHASE_DIST,
			// state changes to WANDER, otherwise stays in CHASE.
			if (canShootTarget()) state = FSMState.ATTACK;
			else {
				if (target == null) {
					state = FSMState.WANDER;
					break;
				}
				int tx = board.screenToBoard(target.getX());
				int ty = board.screenToBoard(target.getY());
				int sx = board.screenToBoard(ship.getX());
				int sy = board.screenToBoard(ship.getY());
				if((tx-sx)*(tx - sx)+(ty-sy)*(ty-sy)>CHASE_DIST*CHASE_DIST) {
					state = FSMState.WANDER;
				}
				else state = FSMState.CHASE;
			}
			
			//#endregion			
			break;

		case ATTACK: // Do not pre-empt with FSMState in a case
			// insert checks and attacking-to-??? transition code here
			//#region PUT YOUR CODE HERE
			// If canShootTarget, stay in ATTACK state.
			// If the target is null, state changes to WANDER.
			// Otherwise, 50% chance to CHASE and 50% chance to WANDER.
			if (canShootTarget()) state = FSMState.ATTACK;
			else if(target == null || rand.nextBoolean()) state = FSMState.WANDER;
			else state = FSMState.CHASE;
			
			//#endregion			
			break;

		default:
			// Unknown or unhandled state, should never get here
			assert (false);
			state = FSMState.WANDER; // If debugging is off
			break;
		}
	}

	/**
	 * Acquire a target to attack (and put it in field target).
	 *
	 * Insert your checking and target selection code here. Note that this
	 * code does not need to reassign <c>target</c> every single time it is
	 * called. Like all other methods, make sure it works with any number
	 * of players (between 0 and 32 players will be checked). Also, it is a
	 * good idea to make sure the ship does not target itself or an
	 * already-fallen (e.g. inactive) ship.
	 */
	private void selectTarget() {
		//#region PUT YOUR CODE HERE
		float x = ship.getX();
		float y = ship.getY();
		// 80% possibility that the ship does not have target 
		// after the previous target becomes unactive
		if (new Random().nextDouble()<0.8 && target!=null && target.isActive() == false){
			target = null; return;
		}
		// Reassign target under two conditions
		// 1. 70% possibility.
		// 2. The previous target is dead.
		if (new Random().nextDouble() < 0.3 
				|| (target!=null && target.isActive() == false)){
			// find the closest ship saved in temp and an active ship saved in temp2.
			Ship temp = null;
			Ship temp2 = null;
			float dist = Float.MAX_VALUE;
			for (Ship s: fleet){
				if (ship.getId() == s.getId() || s.isActive()==false) continue;
				temp2 = ship;
				float sx = s.getX();
				float sy = s.getY();
				float d = (sx-x)*(sx-x)+(sy-y)*(sy-y);
				if (d < dist){
					dist = d;
					temp = s;
				}
			}	
			if (new Random().nextDouble()<0.8 && temp!=null 
					&& board.inBounds(board.screenToBoard(temp.getX()),
					board.screenToBoard(temp.getY()))){
				target = temp;
			}else if (temp2!=null 
					&& board.inBounds(board.screenToBoard(temp2.getX()),
					board.screenToBoard(temp2.getY()))){
						target = temp;
					}
		}
		
		//#endregion			
	}

	/**
	 * Returns true if we can hit a target from here.
	 *
	 * Insert code to return true if a shot fired from the given (x,y) would
	 * be likely to hit the target. We can hit a target if it is in a straight
	 * line from this tile and within attack range. The implementation must take
	 * into consideration whether or not the source tile is a Power Tile.
	 *
	 * @param x The x-index of the source tile
	 * @param y The y-index of the source tile
	 *
	 * @return true if we can hit a target from here.
	 */
	private boolean canShootTargetFrom(int x, int y) {
		//#region PUT YOUR CODE HERE
		if (target==null || board.isDestroyedAt(x, y)) return false;
		float targetX = target.getX();
		float targetY = target.getY();
		int targetXInd = board.screenToBoard(targetX);
		int targetYInd = board.screenToBoard(targetY);
		// for all tiles
		if ((targetYInd == y && Math.abs(targetXInd-x) <= ATTACK_DIST) 
				|| (targetXInd == x && Math.abs(targetYInd - y)<= ATTACK_DIST)){
			return true;
		}
		// for power tile
		if (board.isPowerTileAt(x, y)){
			if (Math.abs(targetXInd - x) == Math.abs(targetYInd - y) 
					&& 2*Math.pow((Math.abs(targetXInd) - x),2) <= ATTACK_DIST*ATTACK_DIST){
				return true;
			}
		} 
		return false;
		//#endregion			
	}

	/**
	 * Returns true if we can both fire and hit our target
	 *
	 * If we can fire now, and we could hit the target from where we are, 
	 * we should hit the target now.
	 *
	 * @return true if we can both fire and hit our target
	 */
	private boolean canShootTarget() {
		//#region PUT YOUR CODE HERE
		int x = board.screenToBoard(ship.getX());
		int y = board.screenToBoard(ship.getY());
		if (ship.canFire() && canShootTargetFrom(x,y)){
			return true;
		}
		return false;
		//#endregion			
	}

	// Pathfinding Code (MODIFY ALL THE FOLLOWING METHODS)

	/** 
	 * Mark all desirable tiles to move to.
	 *
	 * This method implements pathfinding through the use of goal tiles.
	 * It searches for all desirable tiles to move to (there may be more than
	 * one), and marks each one as a goal. Then, the pathfinding method
	 * getMoveAlongPathToGoalTile() moves the ship towards the closest one.
	 *
	 * POSTCONDITION: There is guaranteed to be at least one goal tile
	 * when completed.
	 */
	private void markGoalTiles() {
		// Clear out previous pathfinding data.
		board.clearMarks(); 
		boolean setGoal = false; // Until we find a goal

		// Add initialization code as necessary
		//#region PUT YOUR CODE HERE

		//#endregion

		switch (state) {
		case SPAWN: // Do not pre-empt with FSMState in a case
			// insert code here to mark tiles (if any) that spawning ships
			// want to go to, and set setGoal to true if we marked any.
			// Ships in the spawning state will immediately move to another
			// state, so there is no need for goal tiles here.

			//#region PUT YOUR CODE HERE
			
			//#endregion
			break;

		case WANDER: // Do not pre-empt with FSMState in a case
			// Insert code to mark tiles that will cause us to move around;
			// set setGoal to true if we marked any tiles.
			// NOTE: this case must work even if the ship has no target
			// (and changeStateIfApplicable should make sure we are never
			// in a state that won't work at the time)

			//#region PUT YOUR CODE HERE
			Random rand = new Random();
			int x = rand.nextInt(board.getWidth());
			int y = rand.nextInt(board.getHeight());
			while (!board.isSafeAt(x, y)){
				x = rand.nextInt(board.getWidth());
				y = rand.nextInt(board.getHeight());
			}
			board.setGoal(x, y);
			setGoal = true;
			//#endregion
			break;

		case CHASE: // Do not pre-empt with FSMState in a case
			// Insert code to mark tiles that will cause us to chase the target;
			// set setGoal to true if we marked any tiles.

			//#region PUT YOUR CODE HERE
			x = board.screenToBoard(target.getX());
			y = board.screenToBoard(target.getY());
			for (int i = -ATTACK_DIST; i<=ATTACK_DIST;i++){
				for (int j = -ATTACK_DIST; j<=ATTACK_DIST; j++){
					if (this.canShootTargetFrom(x+i, y+j)) board.setGoal(x+i, y+j);
				}
			}
			setGoal = true;			
			//#endregion
			break;

		case ATTACK: // Do not pre-empt with FSMState in a case
			// Insert code here to mark tiles we can attack from, (see
			// canShootTargetFrom); set setGoal to true if we marked any tiles.

			//#region PUT YOUR CODE HERE
			x = board.screenToBoard(target.getX());
			y = board.screenToBoard(target.getY());
			for (int i = -ATTACK_DIST; i<=ATTACK_DIST;i++){
				for (int j = -ATTACK_DIST; j<=ATTACK_DIST; j++){
					if (this.canShootTargetFrom(x+i, y+j)) board.setGoal(x+i, y+j);
				}
			}
			setGoal = true;
			//#endregion
			break;
		}

		// If we have no goals, mark current position as a goal
		// so we do not spend time looking for nothing:
		if (!setGoal) {
			int sx = board.screenToBoard(ship.getX());
			int sy = board.screenToBoard(ship.getY());
			board.setGoal(sx, sy);
		}
	}

	/**
	 * Returns a movement direction that moves towards a goal tile.
	 *
	 * This is one of the longest parts of the assignment. Implement
	 * breadth-first search (from 2110) to find the best goal tile
	 * to move to. However, just return the movement direction for
	 * the next step, not the entire path.
	 * 
	 * The value returned should be a control code.  See PlayerController
	 * for more information on how to use control codes.
	 *
	 * @return a movement direction that moves towards a goal tile.
	 */
	private int getMoveAlongPathToGoalTile() {		
		//#region PUT YOUR CODE HERE
		// If the ship can shoot the target, it should fire.
		if(state == FSMState.ATTACK && canShootTarget()) return CONTROL_FIRE;
		int sx = board.screenToBoard(ship.getX());
		int sy = board.screenToBoard(ship.getY());
		// A linkedlist for the tiles that we need to check. 
		// Starting from the current position.
		LinkedList<Tile> l = new LinkedList<Tile>();
		Tile r = new Tile(sx,sy,null);
		l.add(r);
		board.setVisited(sx, sy);
		// A mark for outerloop so that we can exit when we reach the goal.
		outerloop:
		while (!l.isEmpty()){
			Tile current = l.poll();
			// The four surrounding directions that we need to look at.
			int[] is = new int[]{1,-1,0,0};
			int[] js = new int[]{0,0,1,-1};
			for (int k = 0;k<4;k++){
				int i = is[k];
				int j = js[k];
				int x = current.x + i;
				int y = current.y + j;
				// If a surrounding tile is not visited yet and is in bounds, 
				// add it to the linkedlist.
				if (!board.isVisited(x,y) && board.inBounds(x, y)){
					board.setVisited(x, y);
					if (board.isSafeAt(x, y)){
						l.add(new Tile(x,y,current));
					}
				}
				if (board.isGoal(x, y)) break outerloop;
			}
		}
		// If the list is empty at the end, the goal cannot be reached.
		if (l.isEmpty()) return CONTROL_NO_ACTION;
		// Find the last tile in the list, which is the goal.
		Tile t = l.pollLast();
		Tile next = null;
		// Trace back through the parent field in the Tile class.
		while (t.parent!=null) {
			next = t;
			t = t.parent;		
		}
		// next is the step when we start from root.	
		if (next.x - r.x == 1) return CONTROL_MOVE_RIGHT;
		if (next.x - r.x == -1) return CONTROL_MOVE_LEFT;
		if (next.y - r.y == 1) return CONTROL_MOVE_DOWN;
		if (next.y - r.y == -1) return CONTROL_MOVE_UP;
		return CONTROL_NO_ACTION;
		//#endregion
	}

	// Add any auxiliary methods or data structures here
	//#region PUT YOUR CODE HERE

	//#endregion
}