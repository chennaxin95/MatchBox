/*
 * AidenController.java
 *
 * 
 * Author: Aaron Sy
 * Based on original Platform Controller from Lab4 
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.blocks.FuelBlock;
import edu.cornell.gdiac.physics.blocks.LadderBlock;
import edu.cornell.gdiac.physics.blocks.WoodBlock;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.platform.DudeModel;
import edu.cornell.gdiac.physics.platform.RopeBridge;
import edu.cornell.gdiac.physics.platform.Spinner;

/**
 * Gameplay specific controller for the platformer game.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop,
 * which is much more scalable. However, we still want the assets themselves to
 * be static. This is the purpose of our AssetState variable; it ensures that
 * multiple instances place nicely with the static assets.
 */
public class AidenController extends WorldController
		implements ContactListener {
	/** The texture file for the character avatar (no animation) */
	private static final String DUDE_FILE = "platform/dude.png";
	/** The texture file for the spinning barrier */
	private static final String BARRIER_FILE = "platform/barrier.png";
	/** The texture file for the bullet */
	private static final String BULLET_FILE = "platform/bullet.png";
	/** The texture file for the bridge plank */
	private static final String ROPE_FILE = "platform/ropebridge.png";
	/** The textrue file for the woodenBlock */
	private static final String WOOD_FILE = "platform/woodenBlock.png";
	/** Texture for fuelBlock */
	private static final String FUEL_FILE = "platform/fuelBlock.png";

	/** The sound file for a jump */
	private static final String JUMP_FILE = "platform/jump.mp3";
	/** The sound file for a bullet fire */
	private static final String PEW_FILE = "platform/pew.mp3";
	/** The sound file for a bullet collision */
	private static final String POP_FILE = "platform/plop.mp3";

	/** Texture asset for character avatar */
	private TextureRegion avatarTexture;
	/** Texture for woodblock */
	private TextureRegion woodTexture;
	/** Texture for fuel */
	private TextureRegion fuelTexture;
	/** Texture asset for the spinning barrier */
	private TextureRegion barrierTexture;
	/** Texture asset for the bullet */
	private TextureRegion bulletTexture;
	/** Texture asset for the bridge plank */
	private TextureRegion bridgeTexture;

	/** Track asset loading from all instances and subclasses */
	private AssetState platformAssetState = AssetState.EMPTY;

	/**
	 * Preloads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic
	 * loaders this time. However, we still want the assets themselves to be
	 * static. So we have an AssetState that determines the current loading
	 * state. If the assets are already loaded, this method will do nothing.
	 * 
	 * @param manager
	 *            Reference to global asset manager.
	 */
	public void preLoadContent(AssetManager manager) {
		if (platformAssetState != AssetState.EMPTY) {
			return;
		}

		platformAssetState = AssetState.LOADING;
		manager.load(DUDE_FILE, Texture.class);
		assets.add(DUDE_FILE);
		manager.load(BARRIER_FILE, Texture.class);
		assets.add(BARRIER_FILE);
		manager.load(BULLET_FILE, Texture.class);
		assets.add(BULLET_FILE);
		manager.load(ROPE_FILE, Texture.class);
		assets.add(ROPE_FILE);
		manager.load(WOOD_FILE, Texture.class);
		assets.add(WOOD_FILE);
		manager.load(FUEL_FILE, Texture.class);
		assets.add(FUEL_FILE);

		manager.load(JUMP_FILE, Sound.class);
		assets.add(JUMP_FILE);
		manager.load(PEW_FILE, Sound.class);
		assets.add(PEW_FILE);
		manager.load(POP_FILE, Sound.class);
		assets.add(POP_FILE);

		super.preLoadContent(manager);
	}

	/**
	 * Load the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic
	 * loaders this time. However, we still want the assets themselves to be
	 * static. So we have an AssetState that determines the current loading
	 * state. If the assets are already loaded, this method will do nothing.
	 * 
	 * @param manager
	 *            Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager) {
		if (platformAssetState != AssetState.LOADING) {
			return;
		}
		woodTexture = createTexture(manager, WOOD_FILE, false);
		avatarTexture = createTexture(manager, DUDE_FILE, false);
		barrierTexture = createTexture(manager, BARRIER_FILE, false);
		bulletTexture = createTexture(manager, BULLET_FILE, false);
		bridgeTexture = createTexture(manager, ROPE_FILE, false);
		fuelTexture = createTexture(manager, FUEL_FILE, false);

		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager, JUMP_FILE);
		sounds.allocate(manager, PEW_FILE);
		sounds.allocate(manager, POP_FILE);
		super.loadContent(manager);
		platformAssetState = AssetState.COMPLETE;
	}

	// Physics constants for initialization
	/** The new heavier gravity for this world (so it is not so floaty) */
	private static final float DEFAULT_GRAVITY = -14.7f;
	/** The density for most physics objects */
	private static final float BASIC_DENSITY = 0.0f;
	/** The density for a bullet */
	private static final float HEAVY_DENSITY = 10.0f;
	/** Friction of most platforms */
	private static final float BASIC_FRICTION = 0.4f;
	/** The restitution for all physics objects */
	private static final float BASIC_RESTITUTION = 0.1f;
	/** The width of the rope bridge */
	private static final float BRIDGE_WIDTH = 14.0f;
	/** Offset for bullet when firing */
	private static final float BULLET_OFFSET = 0.2f;
	/** The speed of the bullet after firing */
	private static final float BULLET_SPEED = 20.0f;
	/** The volume for sound effects */
	private static final float EFFECT_VOLUME = 0.8f;

	// Since these appear only once, we do not care about the magic numbers.
	// In an actual game, this information would go in a data file.
	// Wall vertices
	private static final float[][] WALLS = {
			{ 1.0f, 0.0f, 31.0f, 0.0f, 31.0f, 1.0f, 1.0f, 1.0f },
			{ 16.0f, 18.0f, 16.0f, 17.0f, 1.0f, 17.0f,
					1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 18.0f },
			{ 32.0f, 18.0f, 32.0f, 0.0f, 31.0f, 0.0f,
					31.0f, 17.0f, 16.0f, 17.0f, 16.0f, 18.0f } };

	/** The outlines of all of the platforms */
	private static final float[][] PLATFORMS = {
			{ 8.0f, 7.0f, 31.0f, 7.0f, 31.0f, 8.0f, 8.0f, 8.0f },
			{ 1.0f, 12.0f, 9.0f, 12.0f, 9.0f, 13.0f, 1.0f, 13.0f },
			{ 12.0f, 12.0f, 25.0f, 12.0f, 25.0f, 13.0f, 12.0f, 13.0f }
	};

	/** the vertices for the boxes */

	private static final float[] BOXES = { 29.5f, 9f, 7f, 2f, 7f, 4f,
			7f, 6f, 9f, 2f, 11f, 2f
	};
	
	/** fuel blocks */
	private static final float[] FUELS = {26f, 9f};
	
	private static final float [] LADDER = {11f, 9f, 11f, 11f, 11f, 13f};

	// Other game objects
	/** The goal door position */
	private static Vector2 GOAL_POS = new Vector2(4.0f, 14.0f);
	/** The position of the spinning barrier */
	private static Vector2 SPIN_POS = new Vector2(13.0f, 12.5f);
	/** The initial position of the dude */
	private static Vector2 DUDE_POS = new Vector2(2.5f, 5.0f);
	/** The position of the rope bridge */
	private static Vector2 BRIDGE_POS = new Vector2(9.0f, 3.8f);

	// Physics objects for the game
	/** Flammable Objects */
	protected PooledList<FlammableBlock> flammables = new PooledList<FlammableBlock>();
	/** Reference to the character avatar */
	private AidenModel avatar;
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public AidenController() {
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		sensorFixtures = new ObjectSet<Fixture>();
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity());

		for (Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		fuelFont.setColor(Color.WHITE);
		world = new World(gravity, false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// Add level goal
		float dwidth = goalTile.getRegionWidth() / scale.x;
		float dheight = goalTile.getRegionHeight() / scale.y;
		goalDoor = new BoxObstacle(29.5f, 2f, dwidth, dheight);
		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setDensity(0.0f);
		goalDoor.setFriction(0.0f);
		goalDoor.setRestitution(0.0f);
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(goalTile);
		goalDoor.setName("goal");
		addObject(goalDoor);

		String wname = "wall";
		for (int ii = 0; ii < WALLS.length; ii++) {
			PolygonObstacle obj;
			obj = new PolygonObstacle(WALLS[ii], 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(BASIC_DENSITY);
			obj.setFriction(BASIC_FRICTION);
			obj.setRestitution(BASIC_RESTITUTION);
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName(wname + ii);
			addObject(obj);
		}

		String pname = "platform";
		for (int ii = 0; ii < PLATFORMS.length; ii++) {
			PolygonObstacle obj;
			obj = new PolygonObstacle(PLATFORMS[ii], 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(BASIC_DENSITY);
			obj.setFriction(BASIC_FRICTION);
			obj.setRestitution(BASIC_RESTITUTION);
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName(pname + ii);
			addObject(obj);
		}

		// Create dude
		dwidth = avatarTexture.getRegionWidth() / scale.x;
		dheight = avatarTexture.getRegionHeight() / scale.y;
		avatar = new AidenModel(1, 13, dwidth, dheight);
		avatar.setDrawScale(scale);
		avatar.setTexture(avatarTexture);
		addObject(avatar);

		// Adding boxes
		for (int ii = 0; ii < BOXES.length; ii += 2) {
			TextureRegion texture = woodTexture;
			dwidth = texture.getRegionWidth() / scale.x;
			dheight = texture.getRegionHeight() / scale.y;
			WoodBlock box = new WoodBlock(BOXES[ii], BOXES[ii + 1], dwidth,
					dheight, 1, 5, 5);
			box.setDensity(HEAVY_DENSITY);
			box.setFriction(BASIC_FRICTION);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("box" + ii);
			box.setDrawScale(scale);
			box.setTexture(texture);
			addObject(box);
			flammables.add(box);
		}
		
		// Adding boxes
		for (int ii = 0; ii < FUELS.length; ii += 2) {
			TextureRegion texture = fuelTexture;
			dwidth = texture.getRegionWidth() / scale.x;
			dheight = texture.getRegionHeight() / scale.y;
			FuelBlock box = new FuelBlock(FUELS[ii], FUELS[ii + 1], dwidth,
					dheight, 1, 5, 5);
			box.setDensity(HEAVY_DENSITY);
			box.setFriction(BASIC_FRICTION);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("box" + ii);
			box.setDrawScale(scale);
			box.setTexture(texture);
			addObject(box);
			flammables.add(box);
		}
		
		for (int ii = 0; ii < LADDER.length; ii += 2) {
			TextureRegion texture = fuelTexture;
			dwidth = texture.getRegionWidth() / scale.x;
			dheight = texture.getRegionHeight() / scale.y;
			LadderBlock box = new LadderBlock(LADDER[ii], LADDER[ii + 1], dwidth,
					dheight, 1, 5);
			box.setDensity(HEAVY_DENSITY);
			box.setFriction(BASIC_FRICTION);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("box" + ii);
			box.setDrawScale(scale);
			box.setTexture(texture);
			addObject(box);
			flammables.add(box);
		}
	}

	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time to switch to a
	 * new game mode. If not, the update proceeds normally.
	 *
	 * @param delta
	 *            Number of seconds since last animation frame
	 * 
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		if (!super.preUpdate(dt)) {
			return false;
		}

		if (!isFailure() && avatar.getY() < -1) {
			setFailure(true);
			return false;
		}

		return true;
	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class
	 * WorldController. This method is called after input is read, but before
	 * collisions are resolved. The very last thing that it should do is apply
	 * forces to the appropriate objects.
	 *
	 * @param delta
	 *            Number of seconds since last animation frame
	 */
	public void update(float dt) {
		if (avatar.getFuel() == 0) {
			setFailure(true);
		}

		// Process actions in object model
		avatar.setMovement(InputController.getInstance().getHorizontal()
				* avatar.getForce());
		avatar.setMovementY(InputController.getInstance().getVertical()
				* avatar.getForce());
		avatar.setJumping(InputController.getInstance().didSecondary());

		
		avatar.applyForce();
		if (avatar.isJumping()) {
			SoundController.getInstance().play(JUMP_FILE, JUMP_FILE, false,
					EFFECT_VOLUME);
		}
		Array<Contact> cList = world.getContactList();
		for (Contact c : cList) {
			Fixture fix1 = c.getFixtureA();
			Fixture fix2 = c.getFixtureB();
			Body body1 = fix1.getBody();
			Body body2 = fix2.getBody();
			try {
				Obstacle bd1 = (Obstacle) body1.getUserData();
				Obstacle bd2 = (Obstacle) body2.getUserData();

				/** Burning controller code */

				// checking for two flammable block's chain reaction
				if (bd1 instanceof FlammableBlock
						&& bd2 instanceof FlammableBlock) {
					FlammableBlock fb1 = (FlammableBlock) bd1;
					FlammableBlock fb2 = (FlammableBlock) bd2;
					if (fb1.canSpreadFire()
							&& (!fb2.isBurning() && !fb2.isBurnt())) {
						fb2.activateBurnTimer();
					} else if (fb2.canSpreadFire()
							&& (!fb1.isBurning() && !fb1.isBurnt())) {
						fb1.activateBurnTimer();
					}
				}
				// check for aiden and flammable
				if (bd1 == avatar) {
					if (bd2 instanceof FlammableBlock) {
						FlammableBlock fb = (FlammableBlock) bd2;
						if (!fb.isBurning() && !fb.isBurnt()) {
							fb.activateBurnTimer();
							// if it's a fuel box
							if (fb instanceof FuelBlock) {
								avatar.addFuel(((FuelBlock) fb).getFuelBonus());
							} else {
								avatar.subFuel(
										((FlammableBlock) fb).getFuelPenalty());
							}
						}
					}
				}
				if (bd2 == avatar) {
					if (bd1 instanceof FlammableBlock) {
						FlammableBlock fb = (FlammableBlock) bd1;
						if (!fb.isBurning() && !fb.isBurnt()) {
							fb.activateBurnTimer();
							// if it's a fuel box
							if (fb instanceof FuelBlock) {
								avatar.addFuel(((FuelBlock) fb).getFuelBonus());
							} else {
								avatar.subFuel(
										((FlammableBlock) fb).getFuelPenalty());
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		// update flammable objects;
		for (FlammableBlock fb : flammables) {
			fb.updateBurningState(dt);
			if (fb.isBurnt()) {
				objects.remove(fb);
				flammables.remove(fb);
				fb.markRemoved(true);
				fb.deactivatePhysics(world);
			}
		}

		// If we use sound, we must remember this.
		SoundController.getInstance().update();
	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when we first get a collision between two objects.
	 * We use this method to test if it is the "right" kind of collision. In
	 * particular, we use it to test if we made it to the win door.
	 *
	 * @param contact
	 *            The two bodies that collided
	 */
	public void beginContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		try {
			Obstacle bd1 = (Obstacle) body1.getUserData();
			Obstacle bd2 = (Obstacle) body2.getUserData();

			// See if we have landed on the ground.
			if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
					(avatar.getSensorName().equals(fd1) && avatar != bd2)) {
				avatar.setGrounded(true);
				sensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have
																	// more than
																	// one
																	// ground
			}

			// Check for win condition
			if ((bd1 == avatar && bd2 == goalDoor) ||
					(bd1 == goalDoor && bd2 == avatar)) {
				setComplete(true);
			}

			// Set climbing state
			if (bd1 == avatar && bd2 instanceof BlockAbstract) {
				avatar.setClimbing(((BlockAbstract) bd2).isClimbable());
			}
			if (bd2 == avatar && bd1 instanceof BlockAbstract) {
				avatar.setClimbing(((BlockAbstract) bd1).isClimbable());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when two objects cease to touch. The main use of
	 * this method is to determine when the characer is NOT on the ground. This
	 * is how we prevent double jumping.
	 */
	public void endContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
				(avatar.getSensorName().equals(fd1) && avatar != bd2)) {
			sensorFixtures.remove(avatar == bd1 ? fix2 : fix1);
			if (sensorFixtures.size == 0) {
				avatar.setGrounded(false);
			}
		}

		// See if no longer climbing
		if (bd1 == avatar && bd2 instanceof BlockAbstract) {
			if (((BlockAbstract) bd2).isClimbable()) {
				avatar.setClimbing(false);
			}
		}
		if (bd2 == avatar && bd1 instanceof BlockAbstract) {
			if (((BlockAbstract) bd1).isClimbable()) {
				((BlockAbstract) bd1).setClimbable(false);
			}
		}
	}

	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	/** Unused ContactListener method */
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();
		
		if (bd1 == avatar && bd2 instanceof LadderBlock
				|| bd2 == avatar && bd1 instanceof LadderBlock){
			contact.setEnabled(false);
		}
	
	}
	
	@Override
	public void draw(float delta) {
		canvas.clear();
		
		canvas.begin();
		for(Obstacle obj : objects) {
			obj.draw(canvas);
		}
		canvas.end();
		
		if (debug) {
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}
		
		
		
		// Final message
		if (isComplete() && !isFailure()) {
			displayFont.setColor(Color.YELLOW);
			canvas.begin(); // DO NOT SCALE
			canvas.drawTextCentered("VICTORY!", displayFont, 0.0f);
			canvas.end();
			avatar.setComplete(true);
		} else if (isFailure()) {
			displayFont.setColor(Color.RED);
			canvas.begin(); // DO NOT SCALE
			canvas.drawTextCentered("FAILURE!", displayFont, 0.0f);
			canvas.end();
			avatar.setComplete(true);
		}
		
		//drawing the fuel level
		if(avatar != null){
			canvas.begin();
			String fuelT = "fuel: " + (int)avatar.getFuel();
			canvas.drawText(fuelT, fuelFont, 750, 500);
			canvas.end();
		}
	}
}