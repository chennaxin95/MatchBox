/*
 * AidenController.java
 *
 * 
 * Based on original Platform Controller from Lab4 
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import java.util.ArrayList;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.ai.AIController;
import edu.cornell.gdiac.physics.blocks.*;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.scene.AssetFile;
import edu.cornell.gdiac.physics.scene.Scene;
import edu.cornell.gdiac.physics.character.*;
import edu.cornell.gdiac.physics.character.CharacterModel.CharacterType;
import edu.cornell.gdiac.physics.CollisionController;

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

	private AssetFile af;

	/** Track asset loading from all instances and subclasses */
	// private AssetState platformAssetState = AssetState.EMPTY;

	/**
	 * Mode in which Aiden behaves more like a spirit instead of a solid being.
	 * Enables him to pass through burning objects and travel faster through
	 * them. Toggled with the Tab key.
	 */
	private boolean spirit = true;

	// Physics constants for initialization
	/** The new heavier gravity for this world (so it is not so floaty) */
	private static final float DEFAULT_GRAVITY = -14.7f;
	/** The density for most physics objects */
	private static final float BASIC_DENSITY = 0.0f;
	/** The density for a bullet */
	private static final float HEAVY_DENSITY = 50.0f;
	/** Friction of most platforms */
	// private static final float BASIC_FRICTION = 0.4f;
	/** The restitution for all physics objects */
	private static final float BASIC_RESTITUTION = 0.0f;
	/** The volume for sound effects */
	private static final float EFFECT_VOLUME = 0.8f;

	// Since these appear only once, we do not care about the magic numbers.
	// In an actual game, this information would go in a data file.
	// Wall vertices

	private static final float[][] START = {
			{ 1.0f, 5.0f },
			{ 1.0f, 20.0f },
			{ 1.0f, 13.0f },
			{ 1.1125f, 16.5f }
	};
	private static final float[][][] WALLS2 = {
			{ { 1.0f, 0.0f, 30.0f, 1.0f },
					{ 0.0f, 0.0f, 1f, 22f },
					{ 1.0f, 21f, 30f, 1f },
					{ 31.0f, 0.0f, 1f, 22f } },

			{ { 1.0f, 0.0f, 30.0f, 1f },
					{ 0.0f, 0.0f, 1f, 22f },
					{ 1.0f, 21f, 30f, 1f },
					{ 31.0f, 0.0f, 1f, 22f } },

			{ { 1.0f, 0.0f, 30.0f, 1f },
					{ 0.0f, 0.0f, 1f, 22f },
					{ 1.0f, 21f, 30f, 1f },
					{ 31.0f, 0.0f, 1f, 22f },
					{ 16.25f, 6f, 1f, 15f } },
			{ { 1.0f, 0.0f, 30.0f, 1f },
					{ 0.0f, 0.0f, 1f, 22f },
					{ 1.0f, 21f, 30f, 1f },
					{ 31.0f, 0.0f, 1f, 22f },
			}
	};

	private static final float[][][] PLATFORMS2 = {
			{},

			{ { 6.5f, 8.0f, 10.0f, 1f },
					{ 15.5f, 5.0f, 1f, 3f },
					{ 16.5f, 5.0f, 10.0f, 1f },
					{ 25.0f, 6.0f, 1f, 2f },
					{ 25.0f, 8.0f, 6f, 1f },
					{ 1.0f, 16.0f, 15.0f, 1f },
					{ 18.0f, 16.0f, 7f, 1f } },

			{ { 1.0f, 10.0f, 3.0f, 1f },
					{ 3.0f, 5.0f, 4.0f, 1f },
					{ 10.0f, 5.0f, 4.0f, 1f },
					{ 26.0f, 7.0f, 5.0f, 1f }
			},
			{ { 1f, 7f, 6f, 1f },
					{ 1f, 15f, 6f, 1f },
					{ 7f, 11f, 1f, 5f },
					{ 8f, 11f, 5f, 1f },
					{ 13f, 11f, 1f, 3f },
					{ 14f, 8f, 1f, 6f },
					{ 15f, 13f, 5f, 1f },
					{ 19f, 11f, 1f, 2f },
					{ 20f, 11f, 6f, 1f },
					{ 25f, 12f, 1f, 5f },
					{ 26f, 16f, 3f, 1f },
					{ 26f, 6f, 5f, 1f },
					{ 17.1f, 5f, 5f, 1f },
			}
	};
	
	private static final float[][][] BPLAT = {
			{{20f, 2f, 4f, 1f}},
			{},
			{},
			{}
	};

	/** the vertices for the boxes */

	private static final float[][] BOXES = {
			{ 6f, 2f, 6f, 4f, 6f, 6f, 6f, 8f, 6f, 10f, 6f, 12f,
					8f, 2f, 8f, 4f, 8f, 6f, 8f, 8f, 8f, 10f, 8f, 12f,
					10f, 2f, 10f, 4f, 10f, 6f, 10f, 8f, 10f, 10f, 10f, 12f,
					12f, 2f, 12f, 4f, 12f, 6f, 12f, 8f, 12f, 10f, 12f, 12f,
					14f, 2f, 14f, 4f, 14f, 6f, 14f, 8f, 14f, 10f, 14f, 12f,
					16f, 2f, 16f, 4f, 16f, 6f, 16f, 8f, 16f, 10f, 16f, 12f },

			{ 26.5f, 9f, 28.5f, 9f, 26.5f, 10f, 7f, 2f, 7f, 4f,
					7f, 6f, 9f, 2f, 11f, 2f,
					9f, 4f, 11f, 4f
			},
			{ 13.5f, 7f, 20.75f, 2f, 20.75f, 6f, 22.75f, 2f, 22.75f, 4f, 24.75f,
					2f, 24.75f, 8f,
					8f, 2f, 10f, 2f, 15.5f, 9f },
			{ 2f, 2f, 6f, 2f, 4f, 2f, 8f, 2f, 10f, 2f, 12f, 2f, 6f, 4f, 6f, 6f,
					4f, 6f, 2f, 6f, 8f, 6f, 10f, 6f, 8f, 8f, 8f, 10f, 6f, 16f,
					6f, 17f, 27f, 1f, 29f, 1f, 25f, 1f, 16.5f, 7f } };

	/** the vertices for stone boxes */

	private static final float[][] STONE_BOXES = {
			{},

			{ 16.0f, 1.0f, },

			{ 20.75f, 4f, 22.75f, 6f, 22.75f, 8f, 24.75f, 4f, 24.75f, 6f,

					24.75f, 10f, 15.5f, 11f/* , 20.75f, 13.0f */ },
			{ 2f, 4f, 4f, 4f, 10f, 4f, 12f, 4f, 12f, 6f, 12f, 8f, 8f, 4f,
					12f, 10f, 10f, 10f, 10f, 8f, 16f, 9f } };

	/** WaterGuard Positions */
	private static final float[][] WATERGUARDS = { {}, { 21.0f, 11.0f },
			{ 21.0f, 11.0f },
			{ 16f, 1f, 9f, 16f } };
	/** fuel blocks */
	private static final float[][] FUELS = { { 2f, 2f }, { 29.5f, 9f },
			{ 13f, 8f },
			{ 17f, 14f, 18f, 7f } };

	private static final float[][] ROPE = { {}, {},
			{ 4f, 10.5f, 3f, 5.5f },
			{ 22f, 19f, 22.1f, 5f } };

	private static final float[][] GOAL = { { 29f, 2f }, { 29f, 2f },
			{ 29f, 9f }, { 2.5f, 9.25f } };

	// Physics objects for the game
	// Characters
	/** Reference to the character avatar */
	private AidenModel avatar;
	/** Reference to the list of non-player characters */
	private ArrayList<CharacterModel> npcs = new ArrayList<CharacterModel>();
	// Blocks
	/** Flammable Objects */
	protected PooledList<FlammableBlock> flammables = new PooledList<FlammableBlock>();
	// Ropes
	protected PooledList<Rope> ropes = new PooledList<Rope>();
	// Exit
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;
	protected ObjectSet<Fixture> contactFixtures;

	// Controllers for the game
	private AIController aiController;
	// // Temp
	// private NavBoard board;

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public AidenController(int level) {
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		sensorFixtures = new ObjectSet<Fixture>();
		contactFixtures = new ObjectSet<Fixture>();
		this.level = level;
		spirit = true;

		// FileHandle file = Gdx.files.local("aiden-example.json");
		scene = new Scene("aiden-example.json");
		this.aiController = new AIController(scene, 0, 0, 35, 25, 1f, 1f,
				objects);
		// board=new NavBoard(0,0, 35, 25, 1, 1);
		blocks = new ArrayList<BlockAbstract>();
	}

	/**
	 * Temporarily hard-code levels
	 */
	public int level = 0;

	/** Sets asset file */
	public void setAssetFile(AssetFile a) {
		this.af = a;
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
		for (FlammableBlock fb : flammables) {
			fb.deactivatePhysics(world);
		}
		objects.clear();
		flammables.clear();
		addQueue.clear();
		npcs.clear();
		world.dispose();
		af.fuelFont.setColor(Color.WHITE);
		world = new World(gravity, false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);

		// board.clear();
		blocks.clear();

		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// Add level goal
		float dwidth = af.goalTile.getRegionWidth() / scale.x;
		float dheight = af.goalTile.getRegionHeight() / scale.y;
		float x = GOAL[level][0];
		float y = GOAL[level][1];
		goalDoor = new BoxObstacle(x, y, dwidth, dheight);
		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setDensity(0.0f);
		goalDoor.setFriction(0.0f);
		goalDoor.setRestitution(0.0f);
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(af.goalTile);
		goalDoor.setName("goal");
		addObject(goalDoor);

		String wname = "wall";
		for (int ii = 0; ii < WALLS2[level].length; ii++) {
			// PolygonObstacle obj;
			Platform p = new Platform(
					new Rectangle(WALLS2[level][ii][0], WALLS2[level][ii][1],
							WALLS2[level][ii][2], WALLS2[level][ii][3]),
					1);
			p.setDensity(BASIC_DENSITY);
			p.setFriction(0);
			p.setRestitution(BASIC_RESTITUTION);
			p.setDrawScale(scale);
			p.setTexture(af.earthTile);
			p.setName(wname + ii);
			addObject(p);
		}

		String pname = "platform";
		for (int ii = 0; ii < PLATFORMS2[level].length; ii++) {
			Platform p = new Platform(
					new Rectangle(PLATFORMS2[level][ii][0],
							PLATFORMS2[level][ii][1],
							PLATFORMS2[level][ii][2], PLATFORMS2[level][ii][3]),
					1);
			p.setDensity(BASIC_DENSITY);
			p.setFriction(0);
			p.setRestitution(BASIC_RESTITUTION);
			p.setDrawScale(scale);
			p.setTexture(af.earthTile);
			p.setName(pname + ii);
			addObject(p);
		}
		
		pname = "bPlat";
		for (int ii = 0; ii < BPLAT[level].length; ii++) {
			BurnablePlatform p = new BurnablePlatform(
					new Rectangle(BPLAT[level][ii][0],
							BPLAT[level][ii][1],
							BPLAT[level][ii][2], BPLAT[level][ii][3]),
					1);
			p.setDensity(BASIC_DENSITY);
			p.setFriction(0);
			p.setRestitution(BASIC_RESTITUTION);
			p.setDrawScale(scale);
			p.setTexture(af.burnablePlatform);
			p.setName(pname + ii);
			addObject(p);
			flammables.add(p);
		}

		// Adding boxes
		for (int ii = 0; ii < BOXES[level].length; ii += 2) {
			TextureRegion texture = af.woodTexture;
			dwidth = texture.getRegionWidth() / scale.x;
			dheight = texture.getRegionHeight() / scale.y;

			FlammableBlock box = new FlammableBlock(BOXES[level][ii],
					BOXES[level][ii + 1], dwidth,

					dheight, 1f, 3f);

			box.setFixedRotation(true);
			box.setDensity(HEAVY_DENSITY);
			box.setFriction(0);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("box" + ii);
			box.setDrawScale(scale);
			box.setTexture(texture);
			box.setBurningTexture(
					af.burningTexture[(ii / 2) % af.burningTexture.length], 2);
			addObject(box);
			flammables.add(box);
		}

		// Adding stone boxes
		for (int ii = 0; ii < STONE_BOXES[level].length; ii += 2) {
			TextureRegion texture = af.woodTexture;
			dwidth = texture.getRegionWidth() / scale.x;
			dheight = texture.getRegionHeight() / scale.y;
			Stone box = new Stone(STONE_BOXES[level][ii],
					STONE_BOXES[level][ii + 1], dwidth,
					dheight);
			box.setFixedRotation(true);
			box.setDensity(HEAVY_DENSITY);
			box.setFriction(0);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("stone_box" + ii);
			box.setDrawScale(scale);
			box.setTexture(af.stoneTexture);
			addObject(box);
		}

		// Adding boxes
		for (int ii = 0; ii < FUELS[level].length; ii += 2) {
			TextureRegion texture = af.fuelTexture;
			dwidth = texture.getRegionWidth() / scale.x / 2;
			dheight = texture.getRegionHeight() / scale.y / 2;
			FuelBlock box = new FuelBlock(FUELS[level][ii],
					FUELS[level][ii + 1], dwidth,

					dheight, 1, 1, 18, false);

			box.setDensity(HEAVY_DENSITY);
			box.setFriction(0);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("fuelbox" + ii);
			box.setDrawScale(scale);
			box.ratio = new Vector2(.5f, .5f);
			box.setTexture(texture);
			addObject(box);
			flammables.add(box);
		}
		for (int ii = 0; ii < ROPE[level].length; ii += 2) {
			dwidth = af.ropeTexture.getRegionWidth() / scale.x;
			dheight = af.ropeTexture.getRegionHeight() / scale.y;
			Rope r = new Rope(ROPE[level][ii], ROPE[level][ii + 1],
					dwidth, dheight);
			r.setDrawScale(scale);
			r.setTexture(af.ropeTexture);
			addObject(r);
			ropes.add(r);
		}
		// Create Aiden
		dwidth = af.avatarTexture.getRegionWidth() / scale.x - 0.5f;
		dheight = af.avatarTexture.getRegionHeight() / scale.y - 0.4f;
		avatar = new AidenModel(START[level][0], START[level][1], dwidth,
				dheight, true);
		avatar.setDrawScale(scale);
		avatar.setTexture(af.avatarTexture);
		avatar.setDeath(af.AidenDieTexture);
		avatar.setFriction(0);
		avatar.setLinearDamping(.1f);
		avatar.setRestitution(0f);
		avatar.setJump(af.AidenJumpTexture);
		avatar.setCharacterSprite(af.AidenAnimeTexture);
		avatar.setName("aiden");
		addObject(avatar);

		// Create NPCs
		dwidth = af.waterTexture.getRegionWidth() / scale.x - 0.8f;
		dheight = (af.waterTexture.getRegionHeight() / scale.y) - 0.8f;

		for (int ii = 0; ii < WATERGUARDS[level].length; ii += 2) {

			WaterGuard ch1 = new WaterGuard(CharacterType.WATER_GUARD,
					"WaterGuard",
					WATERGUARDS[level][ii], WATERGUARDS[level][ii + 1] - 0.5f,
					dwidth,
					dheight, (level != 2));
			ch1.setDrawScale(scale);

			ch1.setTexture(af.waterTexture);
			ch1.setName("wg"+ii);
			npcs.add(ch1);
			ch1.setDeath(af.WaterDieTexture);
			ch1.setCharacterSprite(af.WaterWalkTexture);

			addObject(ch1);
		}
	}

	// Temp
	Scene scene;
	ArrayList<BlockAbstract> blocks;

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

		if (isFailure()) {
			avatar.setFail(true);
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
		if (avatar.getFuel() == 0 || !avatar.isAlive()) {
			setFailure(true);

		}

		// if not in spirit mode or not on ladder, then not climbing
		avatar.setClimbing(false);
		avatar.setGravityScale(1);
		avatar.setSpiriting(false);
		aiController.nextMove(npcs);

		Array<Contact> cList = world.getContactList();
		CollisionController CollControl = new CollisionController();
		boolean notFailure = CollControl.getCollisions(cList, avatar);
		if (!notFailure) {
			setFailure(true);
		}

		double accX = (spirit)
				? InputController.getInstance().getHorizontal() * 1.5
				: InputController.getInstance().getHorizontal();
		double accY = (spirit)
				? InputController.getInstance().getVertical() * 1.5
				: InputController.getInstance().getVertical();

		// Process actions in object model
		avatar.setMovement((float) accX * 9);
		avatar.setMovementY((float) accY * 8);
		avatar.setJumping(InputController.getInstance().didPrimary());
		avatar.setDt(dt);
		avatar.applyForce();
		if (avatar.isJumping()) {
			SoundController.getInstance().play(af.get("JUMP_FILE"),
					af.get("JUMP_FILE"), false,
					EFFECT_VOLUME);
		}

		// Update movements of npcs, including all interactions/side effects
		for (CharacterModel npc : npcs) {
			npc.applyForce();
		}

		// Detect contacts -- should be moved to a separate Controller

		BurnController BurnControl = new BurnController();
		BurnControl.getBurning(flammables, objects, dt, world);

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
				sensorFixtures.add(avatar == bd1 ? fix2 : fix1);
			}

			// Check for win condition
			if ((bd1 == avatar && bd2 == goalDoor) ||
					(bd1 == goalDoor && bd2 == avatar)) {
				setComplete(true);
			}

			// Check for aiden top
			if ((avatar.getTopName().equals(fd2) && avatar != bd1
					&& bd1 instanceof Stone)) {
				if (Math.abs(bd1.getVY()) >= 1) {
					setFailure(true);
				}
			}
			if ((avatar.getTopName().equals(fd1) && avatar != bd2
					&& bd2 instanceof Stone)) {
				if (Math.abs(bd2.getVY()) >= 1) {
					setFailure(true);
				}
			}

			// Check for aiden down water top
			if ((avatar.getTopName().equals(fd2) && avatar != bd1
					&& bd1 instanceof WaterGuard) ||
					(avatar.getTopName().equals(fd1) && avatar != bd2
							&& bd2 instanceof WaterGuard)) {
				setFailure(true);
			}

			// Check for water top
			for (CharacterModel wg : npcs) {
				WaterGuard w = (WaterGuard) wg;
				if ((w.getTopName().equals(fd2) && w != bd1
						&& bd1 instanceof StoneBlock) ||
						(w.getTopName().equals(fd1) && w != bd2
								&& bd2 instanceof StoneBlock)) {

					fix1.setRestitution(0);
					fix2.setRestitution(0);

					w.setDead(true);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when two objects cease to touch. The main use of
	 * this method is to determine when the character is NOT on the ground. This
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
	}

	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	/**
	 * ContactListener method, lets Aiden pass through ladders. Also passes
	 * through burning blocks if spirit mode is enabled.
	 */
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if (bd1 == avatar && bd2 instanceof WheelObstacle
				|| bd2 == avatar && bd1 instanceof WheelObstacle) {
			contact.setEnabled(false);
		}

		if (spirit) {
			if (bd1 == avatar && bd2 instanceof FlammableBlock && 
					!(bd2 instanceof BurnablePlatform)
						|| bd2 == avatar && bd1 instanceof FlammableBlock
							&& !(bd1 instanceof BurnablePlatform)) {
				contact.setEnabled(false);
			}
		}

		if (bd1 instanceof BlockAbstract){
			Vector2 velocity  = ((BlockAbstract) bd1).getLinearVelocity();
			((BlockAbstract) bd1).setLinearVelocity(new Vector2(0, velocity.y));
		}
		if (bd2 instanceof BlockAbstract){
			Vector2 velocity  = ((BlockAbstract) bd2).getLinearVelocity();
			((BlockAbstract) bd2).setLinearVelocity(new Vector2(0, velocity.y));
		}

	}

	@Override
	public void draw(float delta) {
		canvas.clear();
		canvas.begin(avatar.getX(), avatar.getY());
		// canvas.draw(backGround, 0, 0);
		canvas.draw(af.backGround, new Color(1f, 1f, 1f, 1f), 0f, 0f,
				canvas.getWidth(), canvas.getHeight() / 18 * 22);
		for (Obstacle obj : objects) {
			if (obj == avatar) {
				if (!isFailure()) {
					obj.draw(canvas);
				} else {
					avatar.drawDead(canvas);
				}
			} else if (obj instanceof WaterGuard
					&& ((WaterGuard) obj).isDead()) {
				((WaterGuard) obj).drawDead(canvas);
			} else {
				obj.draw(canvas);
			}
		}
		canvas.end();
		if (debug) {
			canvas.beginDebug(1, 1);
			for (Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			aiController.drawDebug(canvas, scale, npcs);
			canvas.endDebug();
		}

		// Final message
		if (isComplete() && !isFailure()) {
			af.displayFont.setColor(Color.YELLOW);
			// canvas.begin();
			Vector2 pos = canvas.relativeVector(340, 320);
			canvas.begin(avatar.getX(), avatar.getY()); // DO NOT SCALE
			canvas.drawText("VICTORY!", af.displayFont, pos.x, pos.y);
			canvas.end();
			avatar.setComplete(true);
		} else if (avatar.canDrawFail()) {
			af.displayFont.setColor(Color.RED);
			// canvas.begin();
			Vector2 pos = canvas.relativeVector(340, 320);
			canvas.begin(avatar.getX(), avatar.getY()); // DO NOT SCALE
			canvas.drawText("FAILURE!", af.displayFont, pos.x, pos.y);
			canvas.end();
			avatar.setComplete(true);
		}

		// drawing the fuel level
		if (avatar != null) {
			canvas.begin(avatar.getX(), avatar.getY());
			// canvas.begin();
			Vector2 pos = canvas.relativeVector(512, 400);
			String fuelT = "fuel: " + (int) avatar.getFuel();
			canvas.drawText(fuelT, af.fuelFont, pos.x, pos.y);
			canvas.end();

		}
	}
}