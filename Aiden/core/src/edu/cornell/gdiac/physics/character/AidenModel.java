/*
 * AidenModel.java
 *

 * Author: Aaron Sy
 * Based on original DudeModel from Lab4
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics.character;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.physics.*;

/**
 * Player avatar for the plaform game.
 *
 * Note that this class returns to static loading. That is because there are no
 * other subclasses that we might loop through.
 */
public class AidenModel extends CharacterModel {
	// Physics constants
	/** The impulse for the character jump */
	private static final float DUDE_JUMP = 18f;

	/** The unit distance away for fire trail */
	private static final float UNIT_TRAIL_DIST = 0.2f;

	/** The Fuel system for Aiden */
	private static final float START_FUEL = 30;
	private static final float MAX_FUEL = 50;
	private float fuel;

	/** The current vertical movement of the character */
	private float movementY;
	/** Whether we are actively jumping */
	private boolean isJumping;
	/** Whether we are actively climbing */
	private boolean isClimbing;
	/** Whether we are moving through blocks in spirit mode */
	private boolean isSpiriting;
	/** if Aiden is touch another box */
	private boolean isContacting;
	/** Win state */
	private boolean complete;
	/** update time */
	private float dt;

	/** Texture for fire trail */
	private TextureRegion trailTexture;

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

	/**
	 * Sets whether Aiden has won.
	 *
	 * @param value
	 *            whether Aiden has won.
	 */
	public void setComplete(boolean value) {
		complete = value;
	}

	public boolean isContacting() {
		return isContacting;
	}

	/** set the update delta time */
	public void setDt(float dt) {
		this.dt = dt;
	}

	/** setting the contacting state */
	public void setContacting(boolean c) {
		isContacting = c;
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

	}

	/**
	 * Set texture for special effect
	 * 
	 * @param t
	 */
	public void setTraillTexture(TextureRegion t) {
		trailTexture = t;
	}

	/**
	 * Applies the force to the body of this dude
	 *
	 * This method should be called after the force attribute is set.
	 */
	@Override
	public void applyForce() {
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

			}

		}
		if (isJumping && !isClimbing && !isSpiriting && isGrounded) {
			movementY = 11;

		}

		if (!isGrounded) {
			movement = movement * 0.9f;

			if (isContacting && !isClimbing && !isSpiriting) {
				movement = movement * 0.2f;
			}

		}
		if (isGrounded && isClimbing) {
			movement += getVX();
			movement = Math.max(-10, Math.min(movement, 10));
			if (temp == 0) {
				movement *= 0.85;

			}
		}

		if (isSpiriting) {
			movement = Math.max(-15, Math.min(15,
					getVX() + temp / 5));
			movementY = Math.max(-15, Math.min(15,
					getVY() + tempy / 5));
		}

		body.setLinearVelocity(movement, movementY);
	}

	/** Add fuel when touch fuel box */
	public void addFuel(float i) {
		fuel = Math.max(fuel + i, MAX_FUEL);
	}

	/** subtract fuel from Aiden */
	public void subFuel(float i) {
		fuel = (float) Math.max(0,
				fuel - Math.max(0.015, 0.01
						* Math.sqrt(getVX() * getVX() + getVY() * getVY())));
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
		super.update(dt);

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
		if (this.isSpiriting) {
			c.a = 0.75f;
		}
		// Draw fire trail
		if (trailTexture != null) {
			canvas.draw(trailTexture, c, origin.x, origin.y,
					(getX() - getVX() * UNIT_TRAIL_DIST) * drawScale.x,
					(getY() - this.getHeight() / 4) * drawScale.y, getAngle(),
					(getVX() * UNIT_TRAIL_DIST) * drawScale.x
							/ trailTexture.getRegionWidth(),
					0.4f);
		}
		// Draw Character
		if (characterSprite == null) {
			if (texture==null) return;
			canvas.draw(texture, c, origin.x, origin.y,
					getX() * drawScale.x, 
					getY() * drawScale.y, getAngle(), effect, 
					1.0f);
			return;
		}
		else {
			animate(canvas, c);
		}
	}
}