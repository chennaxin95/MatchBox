package edu.cornell.gdiac.physics.character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.character.FSMNode.BasicFSMState;
import edu.cornell.gdiac.physics.obstacle.CapsuleObstacle;
import edu.cornell.gdiac.util.FilmStrip;

public class CharacterModel extends CapsuleObstacle{

	public enum CharacterType {
		WATER_GUARD, AIDEN, CIVILIAN
	}

	// Physics constants
	/** The density of the character */
	protected static final float DUDE_DENSITY = 1.0f;
	/** The factor to multiply by the input */
	protected static final float DUDE_FORCE = 30.0f;
	/** The amount to slow the character down */
	protected static final float DUDE_DAMPING = 10.0f;
	/** The dude is a slippery one */
	protected static final float DUDE_FRICTION = 0.5f;
	/** The maximum character speed */
	protected static final float DUDE_MAXSPEED = 5.0f;
	/** Height of the sensor attached to the player's feet */
	protected static final float SENSOR_HEIGHT = 0.05f;

	// This is to fit the image to a tigher hitbox
	/**
	 * The amount to shrink the body fixture (vertically) relative to the image
	 */
	protected static final float DUDE_VSHRINK = 0.95f;
	/**
	 * The amount to shrink the body fixture (horizontally) relative to the
	 * image
	 */
	protected static final float DUDE_HSHRINK = 0.7f;
	/**
	 * The amount to shrink the sensor fixture (horizontally) relative to the
	 * image
	 */
	protected static final float DUDE_SSHRINK = 0.6f;

	/** Spawn count down */

	protected static final float MAX_SPAWN_TIME=1f;
	
	/** Animation cool down */
	protected static final float MAX_ANIME_TIME=0.1f;
	
	/** Firing cool down */
	protected static final float MAX_FIRE_TIME=1f;
	
	/** Ground sensor to represent our feet */
	protected Fixture sensorFixture;
	protected Fixture left;
	protected Fixture right;
	protected PolygonShape sensorShape;
	protected PolygonShape leftShape;
	protected PolygonShape rightShape;

	/** Cache for internal force calculations */
	protected Vector2 forceCache = new Vector2();

	private boolean isAlive;

	protected float spawnCoolDown;
	protected float moveCoolDown;
	protected CharacterType type;
	protected FSMGraph stateMachine;
	/** The current horizontal movement of the character */
	protected float movement;
	/** Which direction is the character facing */
	protected boolean faceRight;
	/** Whether our feet are on the ground */
	protected boolean isGrounded;
	
	/** FilmStrip for animation */
	protected FilmStrip characterSprite;
	/** */
	protected float animeCoolDown; 
	/** */
	protected float fireCoolDown;
	
	/** */
	protected boolean isFiring;
	
	/** */
	protected Vector2 target;
	
	/**The proportion of height from ground where viewing ray is */
	protected float eyeProportion;

	public CharacterModel(CharacterType t, String name, float x, float y, float width, 
			float height, boolean fright){
		super(x, y, width * DUDE_HSHRINK, height * DUDE_VSHRINK);
		setDensity(DUDE_DENSITY);
		setFriction(DUDE_FRICTION); /// HE WILL STICK TO WALLS IF YOU FORGET
		setFixedRotation(true);
		
		type = t;
		createFSMGraph();

		isAlive = true;
		faceRight = fright;
		isGrounded = false;

		spawnCoolDown = MAX_SPAWN_TIME;
		
		moveCoolDown = 0;
		
		fireCoolDown = 0;

		setName(name);
		
		eyeProportion=0.5f;
	}
	
	public float getEyeProportion(){
		return this.eyeProportion;
	}
	
	public void setEyeProportion(float e){
		this.eyeProportion=e;
	}
	
	public Vector2 getEyePosition(){
		Vector2 eyePos=getPosition().cpy();
		eyePos.y=eyePos.y+getHeight()*(getEyeProportion()-1/2f);
		return eyePos;
	}
	

	public void setTarget(Vector2 t){
		this.target=t;
	}
	
	public Vector2 getTarget(){
		return this.target;
	}
	
	public boolean isSpawned() {
		return spawnCoolDown <= 0;
	}

	public boolean canChangeMove() {
		return moveCoolDown <= 0.0;
	}

	public void setMoveCoolDown(float t) {
		moveCoolDown = t;
	}

	public float getMoveCoolDown() {
		return moveCoolDown;
	}
	
	public boolean canFire() {
		switch (this.type){
		case CIVILIAN: return false;
		default: return fireCoolDown <= 0.0;
		}
	}
	
	public void setFireCoolDown(float t) {
		fireCoolDown = t;
	}
	
	public boolean isFiring() {
		return isFiring;
	}
	
	public void setFiring(boolean f){
		isFiring=f;
		if (f){
			this.setFireCoolDown(MAX_FIRE_TIME);
		}
	}

	public FSMGraph getStateMachine() {
		return stateMachine;
	}
	
	public CharacterType getType(){
		return type;
	}

	/**
	 * Returns left/right movement of this character.
	 * 
	 * This is the result of input times dude force.
	 *
	 * @return left/right movement of this character.
	 */
	public float getMovement() {
		return movement;
	}

	/**
	 * Sets left/right movement of this character.
	 * 
	 * This is the result of input times dude force.
	 *
	 * @param value
	 *            left/right movement of this character.
	 */
	public void setMovement(float value) {
		movement = value;
		// Change facing if appropriate
		if (movement < 0) {
			faceRight = false;
		} else if (movement > 0) {
			faceRight = true;
		}
	}
	
	public void turnAround(){
		faceRight = !faceRight;
	}

	/**
	 * Returns true if the dude is on the ground.
	 *
	 * @return true if the dude is on the ground.
	 */
	public boolean isGrounded() {
		return isGrounded;
	}

	/**
	 * Sets whether the dude is on the ground.
	 *
	 * @param value
	 *            whether the dude is on the ground.
	 */
	public void setGrounded(boolean value) {
		isGrounded = value;
	}

	/**
	 * Returns true if the dude is alive.
	 *
	 * @return true if the dude is alive.
	 */
	public boolean isAlive() {
		return isAlive;
	}

	/**
	 * Sets the dude to dead.
	 *
	 */
	public void setDead() {
		isAlive = false;
	}

	/**
	 * Returns how much force to apply to get the dude moving
	 *
	 * Multiply this by the input to get the movement value.
	 *
	 * @return how much force to apply to get the dude moving
	 */
	public float getForce() {
		return DUDE_FORCE;
	}

	/**
	 * Returns ow hard the brakes are applied to get a dude to stop moving
	 *
	 * @return ow hard the brakes are applied to get a dude to stop moving
	 */
	public float getDamping() {
		return DUDE_DAMPING;
	}

	/**
	 * Returns the upper limit on dude left-right movement.
	 *
	 * This does NOT apply to vertical movement.
	 *
	 * @return the upper limit on dude left-right movement.
	 */
	public float getMaxSpeed() {
		return DUDE_MAXSPEED;
	}

	/**
	 * Returns the name of the ground sensor
	 *
	 * This is used by ContactListener
	 *
	 * @return the name of the ground sensor
	 */
	public String getSensorName() {
		return getName() + "GroundSensor";
	}

	public String getLeft() {
		return getName() + "left";
	}

	public String getRight() {
		return getName() + "right";
	}

	/**
	 * Returns true if this character is facing right
	 *
	 * @return true if this character is facing right
	 */
	public boolean isFacingRight() {
		return faceRight;
	}
	
	public void createFSMGraph(){
		FSMNode spawnNode=new FSMNode(BasicFSMState.SPAWN);
		FSMNode wanderNode=new FSMNode(BasicFSMState.WANDER);
		FSMNode chaseNode=new FSMNode(BasicFSMState.CHASE);
		FSMNode attackNode=new FSMNode(BasicFSMState.ATTACK);
		FSMNode escapeNode=new FSMNode(BasicFSMState.ESCAPE);
		switch (this.type){
		case CIVILIAN: 
			GameEvent ec1=new GameEvent();
			ec1.setSpawned(1);
			spawnNode.addNext(wanderNode, ec1);
			GameEvent ec2=new GameEvent();
			ec2.setCloseToFire(1);
			wanderNode.addNext(escapeNode, ec2);
			GameEvent ec3=new GameEvent();
			ec3.setCloseToFire(-1);
			escapeNode.addNext(wanderNode, ec3);
			stateMachine = new FSMGraph(spawnNode);
			break;
		case WATER_GUARD:
			GameEvent ew1=new GameEvent();
			ew1.setSpawned(1);
			spawnNode.addNext(wanderNode, ew1);
			GameEvent ew2=new GameEvent();
			ew2.setSeenAiden(1);
			wanderNode.addNext(chaseNode, ew2);
			GameEvent ew3=new GameEvent();
			ew3.setSeenFire(1);
			wanderNode.addNext(chaseNode, ew3);
			GameEvent ew4=new GameEvent();
			ew4.setSeenAiden(-1);
			ew4.setSeenFire(-1);
			chaseNode.addNext(wanderNode, ew4);
			GameEvent ew5=new GameEvent();
			ew5.setCanFire(1);
			chaseNode.addNext(attackNode, ew5);
			GameEvent ew6=new GameEvent();
			ew6.setCanFire(-1);
			attackNode.addNext(chaseNode, ew6);
			stateMachine = new FSMGraph(spawnNode);
			break;
		default: 
			GameEvent e=new GameEvent();
			e.setSpawned(1);
			spawnNode.addNext(wanderNode, e);
			stateMachine = new FSMGraph(spawnNode);
			break;
		}
	}

	/**
	 * Creates the physics Body(s) for this object, adding them to the world.
	 *
	 * This method overrides the base method to keep your ship from spinning.
	 *
	 * @param world
	 *            Box2D world to store body
	 *
	 * @return true if object allocation succeeded
	 */
	public boolean activatePhysics(World world) {
		// create the box from our superclass
		if (!super.activatePhysics(world)) {
			return false;
		}

		// Ground Sensor
		// -------------
		// We only allow the dude to jump when he's on the ground.
		// Double jumping is not allowed.
		//
		// To determine whether or not the dude is on the ground,
		// we create a thin sensor under his feet, which reports
		// collisions with the world but has no collision response.
		Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
		FixtureDef sensorDef = new FixtureDef();
		sensorDef.density = DUDE_DENSITY;
		sensorDef.isSensor = true;
		sensorShape = new PolygonShape();
		sensorShape.setAsBox(DUDE_SSHRINK * getWidth() / 2.0f, SENSOR_HEIGHT,
				sensorCenter, 0.0f);
		sensorDef.shape = sensorShape;

		sensorFixture = body.createFixture(sensorDef);
		sensorFixture.setUserData(getSensorName());

		return true;
	}
	
	public void resizeFixture(float ratio){
//		Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
//		System.out.println(getHeight());
//		sensorShape = new PolygonShape();
//		sensorShape.setAsBox(getWidth() / 2.0f, SENSOR_HEIGHT, sensorCenter, 0.0f);
	}


	/**
	 * Applies the force to the body of this dude
	 *
	 * This method should be called after the force attribute is set.
	 */
	public void applyForce() {
		// TODO: want to make it more step-like walking
		if (!isActive()) {
			return;
		}
		// Don't want to be moving. Damp out motion
		if (getMovement() == 0f) {
			forceCache.set(-getDamping() * getVX(), 0);
			body.applyForce(forceCache, getPosition(), true);
		}

		// Velocity too high, clamp it
		if (Math.abs(getVX()) >= getMaxSpeed()) {
			setVX(Math.signum(getVX()) * getMaxSpeed());
		} else {
			forceCache.set(getMovement(), 0);
			body.applyForce(forceCache, getPosition(), true);
		}
	}

	/**
	 * Updates the object's physics state (NOT GAME LOGIC).
	 *
	 * We use this method to reset cooldowns.
	 *
	 * @param delta
	 *            Number of seconds since last animation frame
	 */
	public void update(float dt) {
		// Apply cooldowns
		if (spawnCoolDown>0) spawnCoolDown-=dt;
		if (moveCoolDown>0) moveCoolDown-=dt;
		if (fireCoolDown>0) fireCoolDown-=dt;
		
		animeCoolDown-=dt;
		super.update(dt);
	}

	/**
	 * Draws the physics object.
	 *
	 * @param canvas
	 *            Drawing context
	 */
	public void draw(GameCanvas canvas) {
		float effect = faceRight ? 1.0f : -1.0f;
		if (characterSprite == null) {
			if (texture==null) return;
			canvas.draw(texture, Color.WHITE, origin.x, origin.y,
					getX() * drawScale.x, 
					getY() * drawScale.y, getAngle(), effect, 
					1.0f);
			return;
		}
		else{
			animate(canvas, Color.WHITE, 1.0f);
		}
	}

	/**
	 * Draws the outline of the physics body.
	 *
	 * This method can be helpful for understanding issues with collisions.
	 *
	 * @param canvas
	 *            Drawing context
	 */
	public void drawDebug(GameCanvas canvas) {
		super.drawDebug(canvas);
		canvas.drawPhysics(sensorShape, Color.RED, getX(), getY(), getAngle(),
				drawScale.x, drawScale.y);
	}
	
	public void setCharacterSprite(FilmStrip fs){
		characterSprite=fs;
		characterSprite.setFrame(0);
	}
	
	public void animate(GameCanvas canvas, Color c, float sx, float sy){
		if (this.animeCoolDown<=0) {
			animeCoolDown=MAX_ANIME_TIME;
			characterSprite.setFrame((characterSprite.getFrame()+1)%characterSprite.getSize());
		}
		// For placement purposes, put origin in center.
		float ox = 0.5f * characterSprite.getRegionWidth();
		float oy = 0.5f * characterSprite.getRegionHeight();

		float effect = faceRight ? 1.0f : -1.0f;
		
		canvas.draw(characterSprite, c, ox, oy, getX() * drawScale.x, 
				getY() * drawScale.y, getAngle(), effect*sx, sy);
	}
	
	public void animate(GameCanvas canvas, Color c, float ratio){
		animate(canvas, c, ratio, ratio);
	}

	public boolean getFacingDir(){
		return faceRight;
	}

}
