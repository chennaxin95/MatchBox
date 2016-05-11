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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.ai.AIController;
import edu.cornell.gdiac.physics.blocks.*;
import edu.cornell.gdiac.physics.blocks.BurnablePlatform.FlamePlatform;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.scene.AssetFile;
import edu.cornell.gdiac.physics.scene.GameSave;
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

	// private Scene[] scenes;

	/** The game save shared across all levels */
	private static GameSave gs = new GameSave("savedGame.json");

	/** Track asset loading from all instances and subclasses */
	// private AssetState platformAssetState = AssetState.EMPTY;

	/**
	 * Mode in which Aiden behaves more like a spirit instead of a solid being.
	 * Enables him to pass through burning objects and travel faster through
	 * them. Toggled with the Tab key.
	 */
	private boolean spirit = true;

	private Array<FuelBlock> checkpoints = new Array<FuelBlock>();

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

	private CameraController cc = new CameraController();

	// Since these appear only once, we do not care about the magic numbers.
	// In an actual game, this information would go in a data file.
	// Wall vertices

	// Physics objects for the game
	// Characters
	/** Reference to the character avatar */
	private AidenModel avatar;
	Vector2 prevMovement;
	/** Reference to the list of non-player characters */
	private ArrayList<CharacterModel> npcs = new ArrayList<CharacterModel>();
	// Blocks
	/** Flammable Objects */
	protected PooledList<FlammableBlock> flammables = new PooledList<FlammableBlock>();
	// Ropes
	protected PooledList<ComplexObstacle> ropes = new PooledList<ComplexObstacle>();
	// Exit
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;
	protected ObjectSet<Fixture> contactFixtures;
	public float sScaleX;
	public float sScaleY;
	public Sound bgm;
	public Sound jump;

	// Controllers for the game
	private AIController aiController;
	// // Temp
	// private NavBoard board;

	private boolean beginCam = true;
	private int beginCamFrame = 0;

	private TextureRegion backgroundTexture;

	// -------------------------------------------------------------
	// -----------------------menu stuff----------------------------
	// -------------------------------------------------------------

	public Vector2 posTemp;
	public Vector2 pauseT;
	public Vector2 largeBut;
	public Vector2 smallBut;
	private Vector2 fuelBarSize;
	public Vector2 largeSize = new Vector2(320, 128);
	public Vector2 smallSize = new Vector2(100, 96);
	private Vector2 fuelBarInner;
	private Vector2 fuelInnerPos;
	private Vector2 fuelBarPos;
	
	private Vector2 losePos;
	private Vector2 loseSize;
	private Vector2 winPos;
	private Vector2 winSize;

	public Vector2 pScreen;
	public Vector2 pPos;

	public Color resuC = Color.WHITE;
	public Vector2 resuScreen;
	public Vector2 resuPos;

	public Color restC = Color.WHITE;
	public Vector2 restScreen;
	public Vector2 restPos;

	public Color homeC = Color.WHITE;
	public Vector2 homeScreen;
	public Vector2 homePos;

	public Color mC = Color.WHITE;
	public Vector2 mScreen;
	public Vector2 muPos;

	public Color sC = Color.WHITE;
	public Vector2 sScreen;
	public Vector2 sPos;

	// -------------------------------------------------------------
	// -----------------------end menu------------------------------
	// -------------------------------------------------------------

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
	}

	/**
	 * Temporarily hard-code levels
	 */
	public int level = 0;

	/** Sets asset file */
	public void setAssetFile(AssetFile a) {
		this.af = a;
	}

	/** Sets game save file */
	public void setGameSave(GameSave s) {
		gs = s;
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		af.bgm.stop();
		resetPos();
		
		Vector2 gravity = new Vector2(world.getGravity());
		beginCamFrame = 0;
		
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
		world = new World(gravity, false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);

		createScenes(level);
		setScene(this.scene);
		if (jump != null) {
			jump.dispose();
			bgm.dispose();
		}
		populateLevel();
		confeti = new ParticleEffect();
		confeti.load(Gdx.files.internal("platform/confetti.p"),
				Gdx.files.internal("platform"));
		Vector2 pos = canvas.relativeVector(-100, -100);
		confeti.setPosition(pos.x, pos.y);
		if (listener.getMuted()) {
			this.musicMuted = true;
		}
		if (listener.getSound()) {
			this.soundMuted = true;
		}
		if (!musicMuted) {
			af.bgm.play();
			wasPlaying = true;
		}
	}
	
	public void resetPos(){
		pauseT = new Vector2(480, 110);
		loseSize = new Vector2(649, 110);
		winSize = new Vector2(707, 110);
		largeBut = new Vector2(320, 128);
		smallBut = new Vector2(100, 96);

		fuelBarSize = new Vector2(449, 91);
		fuelBarInner = new Vector2(377, 91);
		sScaleX = (float) canvas.getWidth() / 1920f;
		sScaleY = (float) canvas.getHeight() / 1080f;

		pauseT = pauseT.scl(sScaleX, sScaleX);
		loseSize.scl(sScaleX);
		winSize.scl(sScaleX);
		largeBut = largeBut.scl(sScaleX, sScaleX);
		smallBut = smallBut.scl(sScaleX, sScaleX);
		fuelBarSize.scl(sScaleX, sScaleX);
		fuelBarInner.scl(sScaleX);
		setPos(canvas.getZoom());
		resuC = Color.WHITE;
		restC = Color.WHITE;
		homeC = Color.WHITE;
	}
	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// Add level goal
		// if (goalDoor!=null) return;
		float dwidth = af.goalTile.getRegionWidth() / scale.x;
		float dheight = af.goalTile.getRegionHeight() / scale.y;
		goalDoor = scene.getGoalDoor();
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(af.goalTile);
		addObject(goalDoor);

		String pname = "platform";
		for (int ii = 0; ii < scene.getPlatform().size(); ii++) {
			// PolygonObstacle obj;
			Platform p = scene.getPlatform().get(ii);
			if (p instanceof WaterPlatform){
				p.setTexture(af.water);
			}
			else{
				p.setTexture(af.earthTile);
			}
			p.setDensity(BASIC_DENSITY);
			p.setFriction(0);
			p.setRestitution(BASIC_RESTITUTION);
			p.setDrawScale(scale);
			p.setName(pname + ii);
			addObject(p);
		}

		// Adding boxes
		for (int ii = 0; ii < scene.getWoodBlocks().size(); ii++) {
			TextureRegion texture = af.woodTexture;
			dwidth = texture.getRegionWidth() / scale.x;
			dheight = texture.getRegionHeight() / scale.y;

			FlammableBlock box = scene.getWoodBlocks().get(ii);
			box.setFixedRotation(true);
			box.setDensity(HEAVY_DENSITY);
			box.setFriction(0);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("box" + ii);
			box.setDrawScale(scale);
			box.setTexture(texture);
			box.setBurningTexture(
					af.burningTexture[ii % af.burningTexture.length], 2);
			addObject(box);
			flammables.add(box);
		}

		// Adding stone boxes
		for (int ii = 0; ii < scene.getStoneBlocks(false).size(); ii++) {
			TextureRegion texture = af.woodTexture;
			dwidth = texture.getRegionWidth() / scale.x;
			dheight = texture.getRegionHeight() / scale.y;
			StoneBlock box = scene.getStoneBlocks(false).get(ii);
			box.setFixedRotation(true);
			box.setDensity(HEAVY_DENSITY);
			box.setFriction(0);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("stone_box" + ii);
			box.setDrawScale(scale);
			box.setTexture(af.stoneTexture);
			addObject(box);
		}

		// Adding fuel boxes
		for (int ii = 0; ii < scene.getFuelBlocks().size(); ii++) {
			TextureRegion texture = af.fuelTexture;
			dwidth = texture.getRegionWidth() / scale.x;
			dheight = texture.getRegionHeight() / scale.y;
			FuelBlock box = scene.getFuelBlocks().get(ii);
			box.setDensity(HEAVY_DENSITY);
			box.setFriction(0);
			box.setTexture(af.fireBall[(ii % (af.fireBall.length))]);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("fuelbox" + ii);
			box.setDrawScale(scale);
			box.ratio = new Vector2(1f, 1f);
			addObject(box);
			flammables.add(box);
			if (box.isCheckpoint()) {
				checkpoints.add(box);
			}

		}
		// Adding burnable platforms
		for (int ii = 0; ii < scene.getBurnablePlatforms().size(); ii++) {
			BurnablePlatform bp = scene.getBurnablePlatforms().get(ii);
			TextureRegion texture = af.burnablePlatform;
			bp.setTexture(texture);
			bp.getPlatform().setBurningTexture(
					af.burningTexture[(ii + af.burningTexture.length / 2)
							% af.burningTexture.length],
					2);
			bp.setDensity(BASIC_DENSITY);
			bp.setFriction(0);
			bp.setRestitution(BASIC_RESTITUTION);
			bp.setName("burnable_platform" + ii);
			bp.setDrawScale(scale);
			addObject(bp);
			flammables.add(bp.getPlatform());
		}
		// Adding ropes
		for (int ii = 0; ii < scene.getRopes().size(); ii++) {
			Rope r = scene.getRopes().get(ii);
			r.setDrawScale(scale);
			addObject(r);
			r.setTexture(af.ropeTexture, af.nailTexture);
			ropes.add(r);
		}

		// Create Aiden
		dwidth = af.avatarTexture.getRegionWidth() / scale.x;
		dheight = af.avatarTexture.getRegionHeight() / scale.y;
		avatar = scene.getAidenModel();
		avatar.setMass(0);
		avatar.setDensity(0);
		avatar.setDrawScale(scale);
		avatar.setTexture(af.avatarTexture);
		avatar.setDeath(af.AidenDieTexture);
		avatar.setSpirit(af.AidenSpiritTexture);
		avatar.setExpand(af.AidenGlow);
		avatar.setFriction(0);
		avatar.setLinearDamping(.1f);
		avatar.setRestitution(0f);
		avatar.setJump(af.AidenJumpTexture);
		avatar.setRun(af.AidenRunTexture);
		avatar.setCharacterSprite(af.AidenIdleTexture);
		avatar.setName("aiden");
		if (gs.getLevel() == level && gs.getCheckpoint() != -1) {
			avatar.setPosition(
					checkpoints.get(gs.getCheckpoint()).getPosition());
		}
		addObject(avatar);

		// Create NPCs
		dwidth = af.waterTexture.getRegionWidth() / scale.x;
		dheight = (af.waterTexture.getRegionHeight() / scale.y);

		for (int ii = 0; ii < scene.getGuards().size(); ii++) {

			WaterGuard ch1 = scene.getGuards().get(ii);
			ch1.setDrawScale(scale);
			ch1.setChase(af.WaterChaseTexture);
			ch1.setTexture(af.waterTexture);
			ch1.setName("wg" + ii);
			npcs.add(ch1);
			ch1.setDeath(af.WaterDieTexture);
			ch1.setCharacterSprite(af.WaterWalkTextures[ii % (af.WaterWalkTextures.length)]);
			addObject(ch1);
		}

		// bgm = Gdx.audio.newSound(Gdx.files.internal("music/bgm.mp3"));
		// jump = Gdx.audio.newSound(Gdx.files.internal("music/jump.mp3"));

		int ii = 0;
		for (TrapDoor td : scene.getTrapDoors()) {
			td.setDrawScale(scale);
			addObject(td);
			td.createJoints(world);
			ropes.add(td);
			td.setChildrenTexture(af.trapDoor, af.ropeLongTexture,
					af.nailTexture);
			td.setDensity(HEAVY_DENSITY);
			td.setFriction(0);
			td.setRestitution(BASIC_RESTITUTION);
			td.setMass(1000f);
			td.setName("trapdoor" + ii);
			ii++;
		}

		this.aiController = new AIController(scene, 0, 0, scene.getWidth(),
				scene.getHeight(), 1f, 1f, objects);
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

		if (isFailure()) {
			avatar.setFail(true);
		}

		return true;
	}

	// ---------------------------------------------------------------------//
	// ---------------------------------------------------------------------//

	public void setPos(float zoom) {
		float w = canvas.getWidth();
		float h = canvas.getHeight() * 4 / 5;
		float xsOff = smallBut.x * 1.5f;
		float mOff = largeBut.x / 2;
		float yOff = largeBut.y * 1.5f;
		float tOff = pauseT.x / 2;

		pScreen = new Vector2(w/2 - tOff, h);
		winPos = new Vector2(w/2 - winSize.x/2, h);
		losePos = new Vector2(w/2 - loseSize.x/2, h);
		pPos = new Vector2(w / 2 - tOff, 9 * h);
		resuScreen = new Vector2(w / 2 - mOff, h - yOff);
		resuPos = new Vector2(w / 2 - mOff, h - yOff);
		restScreen = new Vector2(w / 2 - mOff, h - 2 * yOff);
		restPos = new Vector2(w / 2 - mOff, h - 2 * yOff);
		homeScreen = new Vector2(w / 2 - mOff, h - 3 * yOff);
		homePos = new Vector2(w / 2 - mOff, h - 3 * yOff);
		mScreen = new Vector2(w / 2 - xsOff, h - 4 * yOff);
		muPos = new Vector2(w / 2 - xsOff, h - 4 * yOff);
		sScreen = new Vector2(w / 2 + (xsOff / 2.98f), h - 4 * yOff);
		sPos = new Vector2(w / 2 + (xsOff / 2.98f), h - 4 * yOff);
		fuelBarPos = new Vector2(w / 8, h);
		fuelInnerPos = new Vector2(fuelBarPos.x + 68 * sScaleX, h);
	}

	public float cooldown = 0.5f;

	public void buttonPressed(float dt) {

		boolean isPressed = InputController.getInstance().didTertiary();
		cooldown -= dt;

		if (isPressed && instr == 0 && cooldown <= 0) {
			cooldown = 0.5f;
			Vector2 pos = InputController.getInstance().getCrossHair();
			Vector2 mPos = new Vector2(pos.x, canvas.getHeight() - pos.y);

			if (mPos.x >= homePos.x && mPos.x <= homePos.x + largeSize.x &&
					mPos.y >= homePos.y && mPos.y <= homePos.y + largeSize.y) {
				homeC = Color.GRAY;
				instr = 2;
				return;
			}
			if (mPos.x >= resuPos.x && mPos.x <= resuPos.x + largeSize.x &&
					mPos.y >= resuPos.y && mPos.y <= resuPos.y + largeSize.y) {
				resuC = Color.GRAY;
				if(!isComplete() && !isFailure()){
					instr = 1;
				}
				else{
					instr = 6;
				}
				return;
			}
			if (mPos.x >= restPos.x && mPos.x <= restPos.x + largeSize.x &&
					mPos.y >= restPos.y && mPos.y <= restPos.y + largeSize.y) {
				restC = Color.GRAY;
				instr = 3;
				return;
			}
			if (mPos.x >= sPos.x && mPos.x <= sPos.x + smallSize.x &&
					mPos.y >= sPos.y && mPos.y <= sPos.y + smallSize.y) {
				instr = 4;
				return;
			}
			if (mPos.x >= muPos.x && mPos.x <= muPos.x + smallSize.x &&
					mPos.y >= muPos.y && mPos.y <= muPos.y + smallSize.y) {
				instr = 5;
				return;
			}
		}
		if (count == 0.4f) {
			mC = Color.WHITE;
			sC = Color.WHITE;
			resuC = Color.WHITE;
			homeC = Color.WHITE;
			restC = Color.WHITE;
		}
	}

	public float jumpCD = 0.5f;
	public boolean wasPaused = false;
	public boolean drawCrit = false;
	public float blinkCD = 0.07f;

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
		confeti.update(dt);
		if (pause) {
			if (musicMuted) {
				af.bgm.pause();
			} else {
				if (!wasPlaying) {
					af.bgm.play();
					wasPlaying = true;
				}
			}
			avatar.resume = true;
			prevMovement = avatar.getLinearVelocity();
			buttonPressed(dt);
			return;
		}

		else{
			this.homeC=Color.WHITE;
			this.restC=Color.WHITE;
			this.resuC=Color.WHITE;
		}
		if(avatar.getFuel() /avatar.getMaxFuel() < 0.3){
			blinkCD -= dt;
			if(blinkCD <= 0){
				drawCrit = !drawCrit;
				blinkCD = 0.3f;
			 }
			 }
		else{
			 drawCrit = false;
		}
	
		if (this.isActive() && level != gs.getLevel()) {
			gs.setLevel(level);
			gs.setCheckpoint(-1);
			gs.exportToJson();
		}

		if (avatar.getFuel() == 0 || !avatar.isAlive()) {
			setFailure(true);
		}

		if (avatar.resume) {
			avatar.setLinearVelocity(prevMovement);
			avatar.resume = false;
		}

		// if not in spirit mode or not on ladder, then not climbing
		avatar.setClimbing(false);
		avatar.setGravityScale(1);
		avatar.setSpiriting(false);
		aiController.nextMove(npcs);
		for (ComplexObstacle co : ropes) {
			co.updateParts(world);
		}

		Array<Contact> cList = world.getContactList();
		CollisionController CollControl = new CollisionController();
		boolean notFailure = CollControl.getCollisions(cList, avatar, gs,
				checkpoints);
		if (CollControl.getCheckpoint() != -1) {
			gs.setCheckpoint(CollControl.getCheckpoint());
			gs.exportToJson();
		}

		if (!notFailure && !avatar.getComplete()) {
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

		if (jumpCD < 0.5f) {
			jumpCD -= dt;
			if (jumpCD <= 0) {
				jumpCD = 0.5f;
			}
		}

		if (avatar.isJumping() && !soundMuted && jumpCD == 0.5f) {
			af.jump.play();
			jumpCD -= dt;
		}

		// Update movements of npcs, including all interactions/side effects
		for (CharacterModel npc : npcs) {
			npc.applyForce();
		}

		BurnController BurnControl = new BurnController();
		BurnControl.getBurning(flammables, objects, dt, world);

		// If we use sound, we must remember this.
		SoundController.getInstance().update();
		if (isComplete() && !isFailure() && gs.getUnlocked() == level) {
			gs.setUnlocked(level + 1);
		}

		if (InputController.getInstance().getHorizontal() != 0) {
			beginCamFrame = 400;
		}
		
		
		if(beginCamFrame == 0){
			canvas.setCamPos(avatar.getX(), avatar.getY());
		}
		
		if (beginCamFrame < 200) {
			float a = (2 * ((float) scene.getWidth()) / (float) 72);
			float b = (2 * ((float) scene.getHeight()) / (float) 44);
			canvas.updateCam(Math.max(Math.max(a, b),1f));
			canvas.translate(scene.getWidth() / 2, scene.getHeight() / 2,
					scene.getWidth(), scene.getHeight());
		}

		if (beginCamFrame > 300) {
			canvas.updateCam(1f);
			canvas.translate(avatar.getX(), avatar.getY(), scene.getWidth(),
					scene.getHeight());
		}
		if (beginCamFrame < 300) {
			beginCamFrame++;
		}

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
				confeti.start();
				avatar.setComplete(true);
			}

			// Check for aiden top
			if ((avatar.getTopName().equals(fd2) && avatar != bd1
					&& bd1 instanceof StoneBlock)) {
				if (Math.abs(bd1.getVY()) >= 1 && !avatar.isSpiriting()) {
					if (!isComplete()) {
						setFailure(true);
					}
				}
			}
			if ((avatar.getTopName().equals(fd1) && avatar != bd2
					&& bd2 instanceof StoneBlock)) {
				if (Math.abs(bd2.getVY()) >= 1 && !avatar.isSpiriting()) {
					if (!isComplete()) {
						setFailure(true);
					}
				}
			}

			// Check for aiden down water top
			if (avatar.getTopName().equals(fd2) && avatar != bd1
					&& bd1 instanceof WaterGuard){
				if ((!isComplete())&&(!((WaterGuard) bd1).isDead())) {
					setFailure(true);
				}
			}
			if (avatar.getTopName().equals(fd1) && avatar != bd2
					&& bd2 instanceof WaterGuard){
				if ((!isComplete())&&(!((WaterGuard) bd2).isDead())) {
					setFailure(true);
				}
			}
					

			// Check for water top
			for (CharacterModel wg : npcs) {
				WaterGuard w = (WaterGuard) wg;
				if (w.getTopName().equals(fd2) && w != bd1
						&& bd1 instanceof StoneBlock &&
						bd1.getVY() <= -2) {
					w.setDead(true);
				}
				if (w.getTopName().equals(fd1) && w != bd2
						&& bd2 instanceof StoneBlock &&
						bd2.getVY() <= -2) {
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
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if (bd1 instanceof BlockAbstract) {
			Vector2 velocity = ((BlockAbstract) bd1).getLinearVelocity();
			((BlockAbstract) bd1).setLinearVelocity(new Vector2(0, velocity.y));
		}
		if (bd2 instanceof BlockAbstract) {
			Vector2 velocity = ((BlockAbstract) bd2).getLinearVelocity();
			((BlockAbstract) bd2).setLinearVelocity(new Vector2(0, velocity.y));
		}
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
		// Disable collision between goal door and NPCs
		if (bd1 instanceof CharacterModel && bd2 instanceof GoalDoor
				|| bd2 instanceof CharacterModel && bd1 instanceof GoalDoor) {
			contact.setEnabled(false);
		}
		// Disable collision between fire balls and NPCs
		if (bd1 instanceof FuelBlock || bd2 instanceof FuelBlock) {
			contact.setEnabled(false);
		}

		// Disable collision between fire balls and NPCs
		if ((bd1 instanceof RopePart || bd2 instanceof RopePart) && (bd1 instanceof WaterGuard || bd2 instanceof WaterGuard)) {
			contact.setEnabled(false);
		}
		
		// Disable collision between water platform and NPCs
		if (bd1 instanceof WaterPlatform || bd2 instanceof WaterPlatform) {
			contact.setEnabled(false);
		}
	
		if (spirit) {
			if ((bd1 == avatar && bd2 instanceof FlammableBlock &&
					!(bd2 instanceof FlamePlatform))
					|| (bd2 == avatar && bd1 instanceof FlammableBlock
							&& !(bd1 instanceof FlamePlatform))) {
				contact.setEnabled(false);
			}
			if ((bd1 == avatar && bd2 instanceof BurnablePlatform)
					|| (bd2 == avatar && bd1 instanceof BurnablePlatform)) {
				contact.setEnabled(true);
			}
		}

		if (bd1 instanceof BlockAbstract) {
			Vector2 velocity = ((BlockAbstract) bd1).getLinearVelocity();
			((BlockAbstract) bd1).setLinearVelocity(new Vector2(0, velocity.y));
		}
		if (bd2 instanceof BlockAbstract) {
			Vector2 velocity = ((BlockAbstract) bd2).getLinearVelocity();
			((BlockAbstract) bd2).setLinearVelocity(new Vector2(0, velocity.y));
		}

	}
	
	//---------------------------------confetti-------------------------------//
	public ParticleEffect confeti;
	
	
	
	//-------------------------------------------------------------------------//

	@Override
	public void draw(float delta) {
		canvas.clear();
		canvas.begin(avatar.getX(), avatar.getY(), scene.getWidth(),
				scene.getHeight(), beginCamFrame);
		// canvas.draw(backGround, 0, 0);
		canvas.draw(backgroundTexture, new Color(1f, 1f, 1f, 1f), 0f, 0f,
				scene.getWidth() * scale.x, scene.getHeight() * scale.y);
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

		float zoom = canvas.getZoom();
		if (avatar != null) {
			Vector2 pos = canvas.relativeVector(fuelBarPos.x, fuelBarPos.y);
			Vector2 iPos = canvas.relativeVector(fuelInnerPos.x, fuelInnerPos.y);
			float sx = avatar.getFuel() /avatar.getMaxFuel();
			canvas.draw(af.barBack, Color.WHITE, iPos.x, iPos.y, fuelBarInner.x*zoom, fuelBarInner.y*zoom );
			if (sx < 0.3f){
				canvas.draw(af.barLow, Color.WHITE, iPos.x, iPos.y, fuelBarInner.x*sx*zoom, fuelBarInner.y*zoom);
			}
			else{
				canvas.draw(af.barInner, Color.WHITE, iPos.x, iPos.y, fuelBarInner.x*sx*zoom, fuelBarInner.y*zoom);
			}
			if(sx == 0){
				canvas.draw(af.barGray, Color.WHITE, pos.x, pos.y, fuelBarSize.x*zoom, fuelBarSize.y*zoom);
				canvas.draw(af.barDie, Color.WHITE, pos.x, pos.y, fuelBarSize.x*zoom, fuelBarSize.y*zoom);
			}
			else if(drawCrit){
				canvas.draw(af.barYellow, Color.WHITE, pos.x, pos.y, fuelBarSize.x*zoom, fuelBarSize.y*zoom);
				canvas.draw(af.barOutter, Color.WHITE, pos.x, pos.y, fuelBarSize.x*zoom, fuelBarSize.y*zoom);
			}
			else{
				canvas.draw(af.barIcon, Color.WHITE, pos.x, pos.y, fuelBarSize.x*zoom, fuelBarSize.y*zoom);
				canvas.draw(af.barOutter, Color.WHITE, pos.x, pos.y, fuelBarSize.x*zoom, fuelBarSize.y*zoom);
			}
		}
		
		if (isComplete()) {
			posTemp = canvas.relativeVector(winPos.x, winPos.y);
			canvas.draw(af.youWin, Color.WHITE, posTemp.x, posTemp.y,
					winSize.x * zoom, winSize.y * zoom);
		}
		else if(isFailure()){
			posTemp = canvas.relativeVector(losePos.x, losePos.y);
			canvas.draw(af.youLose, Color.WHITE, posTemp.x, posTemp.y,
					loseSize.x * zoom, loseSize.y * zoom);
		}
		
		if (pause) {
			resetPos();
			Vector2 pos1 = canvas.relativeVector(0, 0);
			canvas.draw(af.black, Color.WHITE, pos1.x, pos1.y,
					1920 * sScaleX * zoom, 1080 * sScaleY * zoom);
			
			posTemp = canvas.relativeVector(pScreen.x, pScreen.y);
			if(!isComplete()){
				if (!isFailure()){
					canvas.draw(af.paused, Color.WHITE, posTemp.x, posTemp.y,
							pauseT.x * zoom, pauseT.y * zoom);
					//resume
					posTemp = canvas.relativeVector(resuScreen.x, resuScreen.y);
					canvas.draw(af.resumeButton, resuC, posTemp.x, posTemp.y,
							largeBut.x * zoom, largeBut.y * zoom);
					//restart
					posTemp = canvas.relativeVector(restScreen.x, restScreen.y);
					canvas.draw(af.restartButton, restC, posTemp.x, posTemp.y,
							largeBut.x * zoom, largeBut.y * zoom);
					//home
					posTemp = canvas.relativeVector(homeScreen.x, homeScreen.y);
					canvas.draw(af.homeButton, homeC, posTemp.x, posTemp.y,
							largeBut.x * zoom, largeBut.y * zoom);
				}
				else{
					posTemp = canvas.relativeVector(losePos.x, losePos.y);
					canvas.draw(af.youLose, Color.WHITE, posTemp.x, posTemp.y,
							loseSize.x * zoom, loseSize.y * zoom);
					//skip
					posTemp = canvas.relativeVector(resuScreen.x, resuScreen.y);
					canvas.draw(af.skip, resuC, posTemp.x, posTemp.y,
							largeBut.x * zoom, largeBut.y * zoom);
					//retry
					posTemp = canvas.relativeVector(restScreen.x, restScreen.y);
					canvas.draw(af.restartButton, restC, posTemp.x, posTemp.y,
							largeBut.x * zoom, largeBut.y * zoom);
					//home
					posTemp = canvas.relativeVector(homeScreen.x, homeScreen.y);
					canvas.draw(af.homeButton, homeC, posTemp.x, posTemp.y,
							largeBut.x * zoom, largeBut.y * zoom);
				}
			}
			else{
				posTemp = canvas.relativeVector(winPos.x, winPos.y);
				canvas.draw(af.youWin, Color.WHITE, posTemp.x, posTemp.y,
						winSize.x * zoom, winSize.y * zoom);
				//nextlevel
				posTemp = canvas.relativeVector(resuScreen.x, resuScreen.y);
				canvas.draw(af.nextLevel, resuC, posTemp.x, posTemp.y,
						largeBut.x * zoom, largeBut.y * zoom);
				//replay
				posTemp = canvas.relativeVector(restScreen.x, restScreen.y);
				canvas.draw(af.restartButton, restC, posTemp.x, posTemp.y,
						largeBut.x * zoom, largeBut.y * zoom);
				//home
				posTemp = canvas.relativeVector(homeScreen.x, homeScreen.y);
				canvas.draw(af.homeButton, homeC, posTemp.x, posTemp.y,
						largeBut.x * zoom, largeBut.y * zoom);
			}
			//sound stuff
			posTemp = canvas.relativeVector(mScreen.x, mScreen.y);
			canvas.draw(musicMuted ? af.music_no : af.music, mC, posTemp.x,
					posTemp.y, smallBut.x * zoom, smallBut.y * zoom);
			
			posTemp = canvas.relativeVector(sScreen.x, sScreen.y);
			canvas.draw(soundMuted ? af.sound_no : af.sound, sC, posTemp.x,
					posTemp.y, smallBut.x * zoom, smallBut.y * zoom);
		}
		if((pause&&isComplete()) || isComplete()){
			Vector2 pos = canvas.relativeVector(canvas.getWidth()/2, canvas.getHeight()*1.05f);
			confeti.setPosition(pos.x, pos.y);
			canvas.drawParticle(confeti);
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
			avatar.setComplete(true);
		} else if (avatar.canDrawFail()) {
			avatar.setComplete(true);
		}

	}

	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public void stopSound() {
		af.bgm.stop();
	}

	private void createScenes(int level) {

		switch (level) {
		
		//======================Tutorials========================//
		case 0:
			this.scene = new Scene("Tutorial0.json"); // super easy tutorial
													  // level
			backgroundTexture = af.backGround0;
			break;
			
		case 1:
			this.scene = new Scene("Tutorial4.json"); //spirit mode to the top	
			
			backgroundTexture = af.backGround;
			break;
		case 2:
			this.scene = new Scene("Level01.json"); // spirit mode going down
			
			backgroundTexture = af.backGround;
			break;
		case 3:
			this.scene = new Scene("Med2.json"); // stonesss // pretty easy 
			backgroundTexture = af.backGround;
			break;
		
		case 4:
			this.scene = new Scene("Easy2.json"); //spirit boost changed
			backgroundTexture = af.backGround;
			break;
			
		//======================Easy========================//
		case 5:

			this.scene = new Scene("Easy1.json");  //gap introduce water guard
			backgroundTexture = af.backGround;
			break;
			
		case 6:

			this.scene = new Scene("Tutorial6.json");  //gap introduce water guard
			backgroundTexture = af.backGround;
			break;
			
		case 7:
		
			this.scene = new Scene("Tutorial3.json");  //avoid water guard
			backgroundTexture = af.backGround;
			break;
			
		case 8:
			this.scene = new Scene("Easy3.json"); //spirit boost with rope and water
			backgroundTexture = af.backGround;
			break;
		case 9:
			this.scene = new Scene("Tutorial2.json"); //save the block

			backgroundTexture = af.backGround;
			break;
			
		//======================Medium========================//
		case 10:
			this.scene = new Scene("Med1.json"); // boxes line on the bottom
			backgroundTexture = af.backGround;
			break;
						
		case 11: 
			this.scene = new Scene("Med4.json"); // wooden boxessssssssssssssssssss
			backgroundTexture = af.backGround;
			break;
		case 12:
			this.scene = new Scene("Med3.json"); // vertical // add more fuel and move the rope
			backgroundTexture = af.backGround;
			break;
	
		case 13:
			this.scene = new Scene("Level2.json");  // L 
			backgroundTexture = af.backGround;
			break;
		case 14:
			this.scene = new Scene("Level3.json"); //trick + tunnel
			backgroundTexture = af.backGround;
			break;
		//======================Hard========================//
		case 15:
			this.scene = new Scene("Hard1.json");  // square
			backgroundTexture = af.backGround;
			break;
		case 16:
			this.scene = new Scene("Hard2.json");
			backgroundTexture = af.backGround;
			break;
			
		default:
			this.scene = new Scene("Hard1.json");
			backgroundTexture = af.backGround;
			break;
		}

	}

}