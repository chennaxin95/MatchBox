/*
 * AidenModel.java
 *

 * Author: Aaron Sy
 * Based on original DudeModel from Lab4
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.util.FilmStrip;

/**
 * Player avatar for the plaform game.
 *
 * Note that this class returns to static loading. That is because there are no
 * other subclasses that we might loop through.
 */
public class AidenModel extends CharacterModel {
	// Physics constants

	/** The Fuel system for Aiden */
	private static final float START_FUEL = 30;
	private static final float CRITICAL_FUEL = 15;
	private static final float MAX_FUEL = 50;
	private float fuel;
	private ParticleEffect trailLeft;
	private ParticleEffect trailRight;
	private ParticleEffect trailStill;
	protected static final float MAX_JUMP_TIME=0.05f;
	private boolean smallSized = false;
	private int jumpFrame = 0;
	private int runFrame = 0;
	private boolean drawJumping = false;
	private FilmStrip jump;	
	public boolean resume = false;;

	/** Amount of time spent in spirit mode */
	private float spiritCount = 0;

	/** The current vertical movement of the character */
	private float movementY;
	/** Whether we are actively jumping */
	private boolean isJumping;
	/** Whether we are actively climbing */
	private boolean isClimbing;
	/** Whether we are moving through blocks in spirit mode */
	private boolean isSpiriting;
	/** Win state */
	private boolean complete;
	/** update time */
	private float dt;
	/** initial height */
	private float iHeight;
	/** inital width */
	private float iWidth;
	/** aiden ratio */
	private float ratio;
	private float cRatio;
	/** Texture for fire trail */
	private FilmStrip death;
	private FilmStrip run;
	private FilmStrip spirit;
	private FilmStrip expand;
	private Color preColor = Color.WHITE;
	private boolean drawFail = false;
	private boolean failed = false;
	public boolean gotFuel = false;
	
	public boolean canDrawFail(){
		return drawFail;
	}
	public boolean getFailed(){
		return failed;
	}
	
	public void setFail(boolean fail){
		failed = fail;
	}
	/**
	 * Returns up/down movement of this character.
	 * 
	 * This is the result of input times dude force.
	 *
	 * @return up/down movement of this character.
	 */
	public float getMovementY() {
		return movementY;
	}
	
	public void setJump(FilmStrip jump){
		this.jump = jump;
	}

	public void setDeath(FilmStrip die) {
		this.death = die;
		death.setFrame(0);
	}
	public void setRun(FilmStrip run){
		this.run = run;
		this.run.setFrame(0);
	}
	public void setSpirit(FilmStrip spirit){
		this.spirit = spirit;
		this.spirit.setFrame(0);
	}

	/**
	 * Sets up/down movement of this character while climbing.
	 * 
	 * This is the result of input times dude force.
	 *
	 * @param value
	 *            up/down movement of this character.
	 */

	public void setMovementY(float value) {
		movementY = value;
	}

	/**
	 * Returns true if the dude is actively jumping.
	 *
	 * @return true if the dude is actively jumping.
	 */
	public boolean isJumping() {
		return isJumping && isGrounded;
	}

	/**
	 * Sets whether the dude is actively jumping.
	 *
	 * @param value
	 *            whether the dude is actively jumping.
	 */
	public void setJumping(boolean value) {
		isJumping = value;
	}

	/**
	 * Returns true if the dude is actively climbing.
	 *
	 * @return true if the dude is actively climbing.
	 */
	public boolean isClimbing() {
		return isClimbing;
	}
	
	public float getMaxFuel(){
		return MAX_FUEL;
	}
	/**
	 * Sets whether the dude is actively climbing.
	 *
	 * @param value
	 *            whether the dude is actively climbing.
	 */
	public void setClimbing(boolean value) {
		isClimbing = value;
	}

	/**
	 * Returns true if Aiden is passing through flammable blocks in spirit mode.
	 *
	 * @return true if Aiden is passing through flammable blocks in spirit mode
	 */
	public boolean isSpiriting() {
		return isSpiriting;
	}

	/**
	 * Sets whether Aiden is passing through flammable blocks in spirit mode.
	 *
	 * @param value
	 *            Whether Aiden is passing through flammable blocks in spirit
	 *            mode.
	 */
	public void setSpiriting(boolean value) {
		isSpiriting = value;
	}
	
	public void setExpand(FilmStrip e){
		this.expand = e;
	}
	/**
	 * Sets whether Aiden has won.
	 *
	 * @param value
	 *            whether Aiden has won.
	 */
	public void setComplete(boolean value) {
		complete = value;
	}
	/**
	 * Gets whether Aiden has won.
	 *
	 * @param value
	 *            whether Aiden has won.
	 */
	public boolean getComplete() {
		return complete;
	}
	
	/** set the update delta time */
	public void setDt(float dt) {
		this.dt = dt;
	}

	/**
	 * Creates a new dude avatar at the given position.
	 *
	 * The size is expressed in physics units NOT pixels. In order for drawing
	 * to work properly, you MUST set the drawScale. The drawScale converts the
	 * physics units to pixels.
	 *
	 * @param x
	 *            Initial x position of the avatar center
	 * @param y
	 *            Initial y position of the avatar center
	 * @param width
	 *            The object width in physics units
	 * @param height
	 *            The object width in physics units
	 */
	public AidenModel(float x, float y, float width, float height,
			boolean fright) {
		super(CharacterType.AIDEN, "Aiden", x, y, width, height, fright);
		fuel = START_FUEL;
		// Gameplay attributes
		isJumping = false;
		complete = false;
		isClimbing = false;
		setName("Aiden");
		iWidth = width;
		iHeight = height;
		trailLeft = new ParticleEffect();
		trailLeft.load(Gdx.files.internal("platform/left.p"),
				Gdx.files.internal("platform"));
		trailRight = new ParticleEffect();
		trailRight.load(Gdx.files.internal("platform/right.p"),
				Gdx.files.internal("platform"));
		trailStill = new ParticleEffect();
		trailStill.load(Gdx.files.internal("platform/still.p"),
				Gdx.files.internal("platform"));
		trailLeft.setPosition(getX() * drawScale.x,
				(getY() - 0.5f) * drawScale.y);
		trailRight.setPosition(getX() * drawScale.x,
				(getY() - 0.5f) * drawScale.y);
		trailStill.setPosition(getX() * drawScale.x,
				(getY() - 0.5f) * drawScale.y);
	}


	/**
	 * Applies the force to the body of this dude
	 *
	 * This method should be called after the force attribute is set.
	 */
	@Override
	public void applyForce() {
		
		if(failed || complete){
			body.setLinearVelocity(0, 0);
			return;
		}

		if (!isActive()) {
			return;
		}
		float temp = movement;
		float tempy = movementY;

		if (!isClimbing && !isSpiriting) {
			movementY = getVY();
			movementY -= dt * 11;
			movement += getVX();
			movement = Math.max(-10, Math.min(movement, 10));
			if (temp == 0) {
				movement *= 0.85;

				if (Math.abs(movement) <= 0.1) {
					movement = 0;
				}

			}
		}
		if (!isClimbing && !isSpiriting && isGrounded && isJumping) {
			movementY = 14;
		}
		if (!isGrounded) {
			movement = movement * 0.9f;
		}

		if (isClimbing) {
			movement = Math.min(movement, 5);
			movementY = Math.min(movementY, 5);
		}
		if (isSpiriting) {
			float signx = (Math.abs(getVX()) <= 2) ? 0 : Math.signum(getVX());
			float signy = (Math.abs(getVY()) <= 2) ? 0 : Math.signum(getVY());
			float xaccel = (spiritCount >= 0.5 / dt) ? 0.99f : 1.03f;
			float yaccel = (spiritCount >= 0.5 / dt) ? 0.99f : 1.05f;
			float vx = (Math.signum(getVX()) != Math.signum(temp)) ? 0
					: getVX();
			float vy = (Math.signum(getVY()) != Math.signum(tempy)) ? 0
					: getVY();
			movement = (temp == 0) ? Math.min(15,
					Math.abs(getVX()) * xaccel) * signx
					: Math.min(15, Math.abs(vx) * 1.03f
							+ Math.min(Math.abs(temp), 1.0f))
							* Math.signum(temp);
			movementY = (tempy == 0) ? Math.min(15,
					Math.abs(getVY()) * yaccel) * signy
					: Math.min(15, Math.abs(vy) * 1.05f
							+ Math.min(Math.abs(tempy), 1.5f))
							* Math.signum(tempy);
			if (!(temp != 0 && tempy != 0)) {
				if (temp != 0) {
					movementY *= 0.25;
				}
				if (tempy != 0) {
					movement *= 0.25;
				}
			}
		}
		movementY = Math.max(Math.min(movementY, 10.5f), -10);
		movement = Math.max(Math.min(movement, 8), -8);
		body.setLinearVelocity(movement, movementY);
	}

	/** Add fuel when touch fuel box */
	public void addFuel(float i) {
		fuel = Math.min(fuel + i, MAX_FUEL);
	}

	/** subtract fuel from Aiden */
	public void subFuel(float i) {

		float fuelloss = (float) Math.max(0, 0.008
				* Math.sqrt(getVX() * getVX() + getVY() * getVY()));
		if (isSpiriting) {
			fuelloss *= 0.75;
		}
		fuel = (float) Math.max(0, fuel - fuelloss);
	}

	/** return the current level of fuel */
	public float getFuel() {
		return fuel;
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
		if (!complete) {
			subFuel(dt);
		}
		if (isSpiriting) {
			spiritCount += 1;
		} else {
			spiritCount = Math.max(0, spiritCount - 1 / dt);
		}
		super.update(dt);
		ratio = fuel / MAX_FUEL;
		ratio = Math.max(0.8f, ratio);
		ratio = Math.min(1.0f, ratio);
		this.setDimension(iWidth * ratio, iHeight * ratio);
		this.resize(getWidth(), getHeight());
		this.resizeFixture(ratio);
		if(ratio <= 0.85 && !smallSized){
			resizeSensor();
			smallSized = true;
		}
		if(ratio >= 0.9 && smallSized){
			resizeSensor();
			smallSized = false;
		}
		cRatio = Math.max(.4f, Math.min(1f, fuel / CRITICAL_FUEL));

		trailLeft.update(dt);
		trailRight.update(dt);
		trailStill.update(dt);
		trailLeft.setPosition(getX() * drawScale.x,
				(getY() - 0.5f) * drawScale.y);
		trailRight.setPosition(getX() * drawScale.x,
				(getY() - 0.5f) * drawScale.y);
		trailStill.setPosition(getX() * drawScale.x,
				(getY() - 0.5f) * drawScale.y);
	}
	
	@Override
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
		sensorShape.setAsBox(DUDE_SSHRINK * getWidth() / 2.5f, SENSOR_HEIGHT,
				sensorCenter, 0.0f);
		sensorDef.shape = sensorShape;

		sensorFixture = body.createFixture(sensorDef);
		sensorFixture.setUserData(getSensorName());
		
		//top Sensor
		sensorCenter.y = getHeight()/2;
		FixtureDef topDef = new FixtureDef();
		topDef.density = DUDE_DENSITY;
		topDef.isSensor = true;
		topShape = new PolygonShape();
		topShape.setAsBox(DUDE_SSHRINK * getWidth() / 2.5f, SENSOR_HEIGHT,
				sensorCenter, 0.0f);
		topDef.shape = topShape;

		top = body.createFixture(topDef);
		top.setUserData(getTopName());
		return true;
	}
	

	public void resizeSensor(){
		Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
		FixtureDef sensorDef = new FixtureDef();
		sensorDef.density = DUDE_DENSITY;
		sensorDef.isSensor = true;
		sensorShape = new PolygonShape();
		sensorShape.setAsBox(DUDE_SSHRINK * getWidth() / 2.5f, SENSOR_HEIGHT,
				sensorCenter, 0.0f);
		sensorDef.shape = sensorShape;

		sensorFixture = body.createFixture(sensorDef);
		sensorFixture.setUserData(getSensorName());
		
		//top Sensor
		sensorCenter.y = getHeight()/2;
		FixtureDef topDef = new FixtureDef();
		topDef.density = DUDE_DENSITY;
		topDef.isSensor = true;
		topShape = new PolygonShape();
		topShape.setAsBox(DUDE_SSHRINK * getWidth() / 2.5f, SENSOR_HEIGHT,
				sensorCenter, 0.0f);
		topDef.shape = topShape;

		top = body.createFixture(topDef);
		top.setUserData(getTopName());
	}

	public void simpleDraw(GameCanvas canvas) {
		super.draw(canvas);
	}

	/**
	 * Draws the physics object.
	 *
	 * @param canvas
	 *            Drawing context
	 */
	@Override
	public void draw(GameCanvas canvas) {
		float effect = faceRight ? 1.0f : -1.0f;
		Color c = Color.WHITE.cpy();

		if (Math.abs(getVX()) > 2) {
			if (faceRight) {
				canvas.drawParticle(trailRight);
			} else {
				canvas.drawParticle(trailLeft);
			}
		}
		canvas.drawParticle(trailStill);

		if (this.isSpiriting && !gotFuel) {
			c.a = 0.8f;
			drawSpirit(canvas, ratio, c);
			return;
		}
		else if (characterSprite == null) {
			if (texture == null)
				return;
			canvas.draw(texture, c, origin.x, origin.y,
					getX() * drawScale.x,
					getY() * drawScale.y, getAngle(), effect * ratio,
					1.0f * ratio);
			return;

		} else {
			if(isJumping && drawJumping == false && isGrounded()){
				drawJumping = true;
			}
			c.r = Math.min(1, cRatio * 2);
			c.g = cRatio;
			c.b = c.g;
			preColor = c;
			if(gotFuel){
				drawFuel(canvas, ratio);
				if(drawJumping){
					drawJumping = false;
				}
			}
			else if (drawJumping){
				drawJump(canvas, ratio);
			}
			else if (Math.abs(this.getVX()) >= 5){
				drawRun(canvas, ratio);
			}
			else{
				animate(canvas, c, ratio);
			}
		}
	}

	public void drawDead(GameCanvas canvas) {
		if (this.animeCoolDown <= 0) {
			animeCoolDown = MAX_ANIME_TIME;
			death.setFrame(Math.min(death.getFrame() + 1, 11));
		}

		// For placement purposes, put origin in center.
		float ox = 0.5f * characterSprite.getRegionWidth();
		float oy = 0.5f * characterSprite.getRegionHeight();

		float effect = faceRight ? 1.0f : -1.0f;
		Color c = Color.WHITE;
		if (death.getFrame() <= 6) {
			c = preColor;
		}
		canvas.draw(death, c, ox, oy, getX() * drawScale.x,
				getY() * drawScale.y + 30*ratio, getAngle(), effect*ratio, ratio);
		if(death.getFrame() == death.getSize()-1){
			drawFail = true;
		}
	}
	
	public void drawJump(GameCanvas canvas, float ratio) {
		if (this.animeCoolDown<=0) {
			animeCoolDown=MAX_JUMP_TIME;
			jumpFrame++;
			jump.setFrame(jumpFrame);
		}

		// For placement purposes, put origin in center.
		float ox = 0.5f * characterSprite.getRegionWidth();
		float oy = 0.5f * characterSprite.getRegionHeight();

		float effect = faceRight ? 1.0f : -1.0f;
		canvas.draw(jump, preColor, ox, oy, getX() * drawScale.x,
				getY() * drawScale.y + 30*ratio, getAngle(), -effect*ratio, ratio);
		if (jumpFrame == jump.getSize()-1){
			jumpFrame = 0;
			drawJumping = false;
		}
	}
	
	public void drawRun(GameCanvas canvas, float ratio) {
		if (this.animeCoolDown<=0) {
			animeCoolDown=MAX_ANIME_TIME;
			run.setFrame((run.getFrame()+1)%12);
		}

		// For placement purposes, put origin in center.
		float ox = 0.5f * characterSprite.getRegionWidth();
		float oy = 0.5f * characterSprite.getRegionHeight();

		float effect = faceRight ? 1.0f : -1.0f;
		canvas.draw(run, preColor, ox, oy, getX() * drawScale.x,
				getY() * drawScale.y+30*ratio, getAngle(), -effect*ratio, ratio);
	}
	public void drawSpirit(GameCanvas canvas, float ratio, Color c){
		if (this.animeCoolDown<=0) {
			animeCoolDown=MAX_ANIME_TIME;
			spirit.setFrame((spirit.getFrame()+1)%3);
		}
		
		// For placement purposes, put origin in center.
		float ox = 0.5f * characterSprite.getRegionWidth();
		float oy = 0.5f * characterSprite.getRegionHeight();

		float effect = faceRight ? -1.0f : 1.0f;
		canvas.draw(spirit, c, ox, oy, getX() * drawScale.x,
				getY() * drawScale.y+30*ratio, getAngle(), -effect*ratio, ratio);
	}
	
	public int cycles = 0;
	public void drawFuel(GameCanvas canvas, float ratio){
		if (this.animeCoolDown<=0) {
			animeCoolDown=MAX_ANIME_TIME;
			expand.setFrame((expand.getFrame()+1)%6);
		}
		// For placement purposes, put origin in center.
		float ox = 0.5f * characterSprite.getRegionWidth();
		float oy = 0.5f * characterSprite.getRegionHeight();

		float effect = faceRight ? -1.0f : 1.0f;
		canvas.draw(expand, preColor, ox, oy, getX() * drawScale.x,
				getY() * drawScale.y + 30*ratio, getAngle(), -effect*ratio, ratio);
		if (expand.getFrame() == expand.getSize()-1){
			cycles++;
			if(cycles == 6){
				cycles = 0;
				gotFuel = false;
				expand.setFrame(0);
			}
		}
	}
	
}