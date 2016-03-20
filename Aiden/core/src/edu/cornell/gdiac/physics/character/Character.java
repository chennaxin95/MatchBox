package edu.cornell.gdiac.physics.character;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import edu.cornell.gdiac.physics.obstacle.CapsuleObstacle;
import edu.cornell.gdiac.physics.obstacle.CapsuleObstacle.Orientation;

public class Character extends CapsuleObstacle {
	private static final int MAX_SPAWN_TIME=10;
	
	/** The current horizontal movement of the character */
	private float movement;
	/** The current vertical movement of the character */
	private float movementY;
	/** Which direction is the character facing */
	private boolean faceRight;
	
	/** Ground sensor to represent our feet */
	private Fixture sensorFixture;
	private PolygonShape sensorShape;
	private boolean complete;
	
	/** Cache for internal force calculations */
	private Vector2 forceCache = new Vector2();
	
	
	
	private int spawnCount;
	private boolean isAlive;
	private CharacterType type;
	private BasicFSMState state; 
	private Move currentMove;
	
	public Character(CharacterType type, float x, float y, float width, float height){
		
		super(x, y, width, height); // TODO
		
		isAlive=true;
		this.type=type;
		spawnCount=MAX_SPAWN_TIME;
		state=BasicFSMState.SPAWN;
		currentMove=Move.STAY;
	}
	
	public boolean isSpawned(){
		return spawnCount<=0;
	}
	
	public void spawnCountDown(){
		if (!isSpawned()) spawnCount--;
	}
	
	public BasicFSMState getState(){
		return state;
	}
	
	public void setState(BasicFSMState s){
		state=s;
	}
	
	public void setMove(Move m){
		currentMove=m;
	}
	
	public enum Move{
		STAY, LEFT, RIGHT 
	}	
	public enum BasicFSMState{
		SPAWN, WANDER
	}
	public enum CharacterType{
		WATER_GUARD, AIDEN, CIVILIAN
	}
}
