/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;
import com.badlogic.gdx.audio.Sound;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.WorldController.AssetState;
import edu.cornell.gdiac.physics.editor.LevelEditor;
import edu.cornell.gdiac.physics.scene.AssetFile;
import edu.cornell.gdiac.physics.scene.GameSave;
import edu.cornell.gdiac.physics.scene.JSONParser;
import edu.cornell.gdiac.physics.scene.Scene;

/**
 * Root class for a LibGDX.
 * 
 * This class is technically not the ROOT CLASS. Each platform has another class
 * above this (e.g. PC games use DesktopLauncher) which serves as the true root.
 * However, those classes are unique to each platform, while this class is the
 * same across all plaforms. In addition, this functions as the root class all
 * intents and purposes, and you would draw it as a root class in an
 * architecture specification.
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	private AssetManager manager;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private int current;
	/** Current scene for level */
	private int currentS;

	/** List of all WorldControllers */
	private WorldController[] controllers;
	/** List of scene objects */
	// private Scene[] scenes;
	/** A parser for JSON files */
	private JSONParser jsonParser;
	/** Track asset loading from all instances and subclasses */
	protected AssetState worldAssetState = AssetState.EMPTY;
	/** Track all loaded assets (for unloading purposes) */
	protected Array<String> assets;
	/** Where all the assets are stored */
	private AssetFile af = new AssetFile();

	/** The texture for walls and platforms */
	protected TextureRegion earthTile;
	/** The texture for the exit condition */
	protected TextureRegion goalTile;
	/** The font for giving messages to the player */
	protected BitmapFont displayFont;
	protected BitmapFont fuelFont;

	/** Texture asset for character avatar */
	private TextureRegion avatarTexture;
	/** Texture for woodblock */
	private TextureRegion woodTexture;
	/** Texture for fuel */
	private TextureRegion fuelTexture;
	/** texture for water */
	private TextureRegion waterTexture;
	private TextureRegion stoneTexture;
	private TextureRegion ropeTexture;
	/** Texture for background */
	private TextureRegion backGround;

	/** Texture for aiden animation */
	private FilmStrip AidenAnimeTexture;
	private FilmStrip AidenDieTexture;
	private FilmStrip WaterWalkTexture;
	/** Texture for burning animation */
	private FilmStrip[] burningTexture;
	
	public boolean muted = false;
	public boolean soundMute = false;
	public void setMuted(){
		muted = !muted;
	}
	public void setSound(){
		soundMute = !soundMute;
	}
	public boolean getMuted(){
		return muted;
	}
	public boolean getSound(){
		return soundMute;
	}
	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets or
	 * assign any screen.
	 */
	public GDXRoot() {
		// Start loading with the asset manager
		manager = new AssetManager();
		assets = new Array<String>();

		// Add font support to the asset manager
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class,
				new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf",
				new FreetypeFontLoader(resolver));
	}

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
		if (worldAssetState != AssetState.EMPTY) {
			return;
		}

		worldAssetState = AssetState.LOADING;
		// Load the shared tiles.
		manager.load(af.get("EARTH_FILE"), Texture.class);
		assets.add(af.get("EARTH_FILE"));
		manager.load(af.get("GOAL_FILE"), Texture.class);
		assets.add(af.get("GOAL_FILE"));
		manager.load(af.get("BURNP_FILE"), Texture.class);
		assets.add(af.get("BURNP_FILE"));

		// Load the font
		FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		FreetypeFontLoader.FreeTypeFontLoaderParameter s2p = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		size2Params.fontFileName = af.get("FONT_FILE");
		size2Params.fontParameters.size = af.fontSize();
		s2p.fontFileName = af.get("FUEL_FONT");
		s2p.fontParameters.size = af.fontSize();
		manager.load(af.get("FUEL_FONT"), BitmapFont.class, s2p);
		manager.load(af.get("FONT_FILE"), BitmapFont.class, size2Params);
		assets.add(af.get("FONT_FILE"));
		assets.add(af.get("FUEL_FONT"));
		manager.load(af.get("DUDE_FILE"), Texture.class);
		assets.add(af.get("DUDE_FILE"));
		manager.load(af.get("BARRIER_FILE"), Texture.class);
		assets.add(af.get("BARRIER_FILE"));
		manager.load(af.get("BULLET_FILE"), Texture.class);
		assets.add(af.get("BULLET_FILE"));
		manager.load(af.get("WOOD_FILE"), Texture.class);
		assets.add(af.get("WOOD_FILE"));
		manager.load(af.get("FUEL_FILE"), Texture.class);
		assets.add(af.get("FUEL_FILE"));
		manager.load(af.get("ROPE_FILE"), Texture.class);
		assets.add(af.get("ROPE_FILE"));
		manager.load(af.get("BACKGROUND"), Texture.class);
		assets.add(af.get("BACKGROUND"));
		manager.load(af.get("BACKGROUND0"), Texture.class);
		assets.add(af.get("BACKGROUND0"));
		manager.load(af.get("WATER_FILE"), Texture.class);
		assets.add(af.get("WATER_FILE"));
		manager.load(af.get("STONE_FILE"), Texture.class);
		assets.add(af.get("STONE_FILE"));
		manager.load(af.get("AIDEN_ANIME_FILE"), Texture.class);
		assets.add(af.get("AIDEN_ANIME_FILE"));
		manager.load(af.get("AIDEN_DIE_FILE"), Texture.class);
		assets.add(af.get("AIDEN_DIE_FILE"));
		manager.load(af.get("WATER_DIE_FILE"), Texture.class);
		assets.add(af.get("WATER_DIE_FILE"));
		manager.load(af.get("WATER_WALK"), Texture.class);
		assets.add(af.get("WATER_WALK"));
		manager.load(af.get("BURNING_FILE"), Texture.class);
		assets.add(af.get("BURNING_FILE"));
		manager.load(af.get("AIDEN_JUMP_FILE"), Texture.class);
		assets.add(af.get("AIDEN_JUMP_FILE"));
		manager.load(af.get("EDITOR_PANEL_FILE"), Texture.class);
		assets.add(af.get("EDITOR_PANEL_FILE"));
		manager.load(af.get("ROPE_LONG_FILE"), Texture.class);
		assets.add(af.get("ROPE_LONG_FILE"));
		manager.load(af.get("TRAPDOOR_FILE"), Texture.class);
		assets.add(af.get("TRAPDOOR_FILE"));
		manager.load(af.get("NAIL_FILE"), Texture.class);
		assets.add(af.get("NAIL_FILE"));
		manager.load(af.get("MENU_BACK"), Texture.class);
		assets.add(af.get("MENU_BACK"));
		manager.load(af.get("BLACK"), Texture.class);
		assets.add(af.get("BLACK"));
		manager.load(af.get("TRAP_DOOR"), Texture.class);
		assets.add(af.get("TRAP_DOOR"));
		manager.load(af.get("LONG_ROPE"), Texture.class);
		assets.add(af.get("LONG_ROPE"));
		manager.load(af.get("HOME_BUTTON"), Texture.class);
		assets.add(af.get("HOME_BUTTON"));
		manager.load(af.get("RESUME_BUTTON"), Texture.class);
		assets.add(af.get("RESUME_BUTTON"));
		manager.load(af.get("RESTART_BUTTON"), Texture.class);
		assets.add(af.get("RESTART_BUTTON"));
		manager.load(af.get("BAR_OUTTER"), Texture.class);
		assets.add(af.get("BAR_OUTTER"));
		manager.load(af.get("BAR_INNER"), Texture.class);
		assets.add(af.get("BAR_INNER"));
		manager.load(af.get("AIDEN_RUN"), Texture.class);
		assets.add(af.get("AIDEN_RUN"));
		manager.load(af.get("AIDEN_IDLE"), Texture.class);
		assets.add(af.get("AIDEN_IDLE"));
		manager.load(af.get("PAUSED"), Texture.class);
		assets.add(af.get("PAUSED"));
		manager.load(af.get("MUSIC"), Texture.class);
		assets.add(af.get("MUSIC"));
		manager.load(af.get("SOUND"), Texture.class);
		assets.add(af.get("SOUND"));
		manager.load(af.get("SOUND_NO"), Texture.class);
		assets.add(af.get("SOUND_NO"));
		manager.load(af.get("MUSIC_NO"), Texture.class);
		assets.add(af.get("MUSIC_NO"));
		manager.load(af.get("WATER_CHASE"), Texture.class);
		assets.add(af.get("WATER_CHASE"));
		manager.load(af.get("BAR_LOW"), Texture.class);
		assets.add(af.get("BAR_LOW"));
		manager.load(af.get("BAR_BACK"), Texture.class);
		assets.add(af.get("BAR_BACK"));
		manager.load(af.get("FIRE_BALL"), Texture.class);
		assets.add(af.get("FIRE_BALL"));
		manager.load(af.get("AIDEN_SPIRIT"), Texture.class);
		assets.add(af.get("AIDEN_SPIRIT"));
		manager.load(af.get("BAR_ICON"), Texture.class);
		assets.add(af.get("BAR_ICON"));
		manager.load(af.get("BAR_YELLOW"), Texture.class);
		assets.add(af.get("BAR_YELLOW"));
		manager.load(af.get("BAR_GRAY"), Texture.class);
		assets.add(af.get("BAR_GRAY"));
		manager.load(af.get("AIDEN_GLOW"), Texture.class);
		assets.add(af.get("AIDEN_GLOW"));
		manager.load(af.get("BAR_DIE"), Texture.class);
		assets.add(af.get("BAR_DIE"));
		manager.load(af.get("YOU_LOSE"), Texture.class);
		assets.add(af.get("YOU_LOSE"));
		manager.load(af.get("GOOD_JOB"), Texture.class);
		assets.add(af.get("GOOD_JOB"));
		manager.load(af.get("RETRY"), Texture.class);
		assets.add(af.get("RETRY"));
		manager.load(af.get("REPLAY"), Texture.class);
		assets.add(af.get("REPLAY"));
		manager.load(af.get("SKIP"), Texture.class);
		assets.add(af.get("SKIP"));
		manager.load(af.get("NEXT_LEVEL"), Texture.class);
		assets.add(af.get("NEXT_LEVEL"));
		manager.load(af.get("BAR_DIE"), Texture.class);
		assets.add(af.get("BAR_DIE"));
		manager.load(af.get("TUT4_BACK"), Texture.class);
		assets.add(af.get("TUT4_BACK"));
		manager.load(af.get("TUT1_BACK"), Texture.class);
		assets.add(af.get("TUT1_BACK"));
		manager.load(af.get("TUT2_BACK"), Texture.class);
		assets.add(af.get("TUT2_BACK"));
		manager.load(af.get("TUT3_BACK"), Texture.class);
		assets.add(af.get("TUT3_BACK"));
		manager.load(af.get("LEVEL_S"), Texture.class);
		assets.add(af.get("LEVEL_S"));
		manager.load(af.get("RESTART_ICON"), Texture.class);
		assets.add(af.get("RESTART_ICON"));
		manager.load(af.get("LEVEL_BACK1"), Texture.class);
		assets.add(af.get("LEVEL_BACK1"));
		manager.load(af.get("LEVEL_BACK2"), Texture.class);
		assets.add(af.get("LEVEL_BACK2"));
		manager.load(af.get("LEVEL_BACK3"), Texture.class);
		assets.add(af.get("LEVEL_BACK3"));
		manager.load(af.get("LEVEL_BACK4"), Texture.class);
		assets.add(af.get("LEVEL_BACK4"));
		manager.load(af.get("LEVEL_BACK5"), Texture.class);
		assets.add(af.get("LEVEL_BACK5"));
		manager.load(af.get("LEVEL_BACK6"), Texture.class);
		assets.add(af.get("LEVEL_BACK6"));
		manager.load(af.get("LEVEL_BACK7"), Texture.class);
		assets.add(af.get("LEVEL_BACK7"));
		manager.load(af.get("LEVEL_BACK8"), Texture.class);
		assets.add(af.get("LEVEL_BACK8"));
		manager.load(af.get("LEVEL_BACK9"), Texture.class);
		assets.add(af.get("LEVEL_BACK9"));
		manager.load(af.get("LEVEL_BACK10"), Texture.class);
		assets.add(af.get("LEVEL_BACK10"));
		manager.load(af.get("LEVEL_BACK11"), Texture.class);
		assets.add(af.get("LEVEL_BACK11"));
		manager.load(af.get("LEVEL_BACK12"), Texture.class);
		assets.add(af.get("LEVEL_BACK12"));
		manager.load(af.get("LEVEL_BACK13"), Texture.class);
		assets.add(af.get("LEVEL_BACK13"));
		manager.load(af.get("LEVEL_BACK14"), Texture.class);
		assets.add(af.get("LEVEL_BACK14"));
		manager.load(af.get("LEVEL_BACK15"), Texture.class);
		assets.add(af.get("LEVEL_BACK15"));
		manager.load(af.get("LEVEL_BACK16"), Texture.class);
		assets.add(af.get("LEVEL_BACK16"));
		manager.load(af.get("LEVEL_BACK17"), Texture.class);
		assets.add(af.get("LEVEL_BACK17"));
		manager.load(af.get("LEVEL_BACK18"), Texture.class);
		assets.add(af.get("LEVEL_BACK18"));
		manager.load(af.get("LEVEL_BACK19"), Texture.class);
		assets.add(af.get("LEVEL_BACK19"));
		manager.load(af.get("LEVEL_BACK20"), Texture.class);
		assets.add(af.get("LEVEL_BACK20"));
		
		
		manager.load(af.get("JUMP_FILE"), Sound.class);
		assets.add(af.get("JUMP_FILE"));
		manager.load(af.get("PEW_FILE"), Sound.class);
		assets.add(af.get("PEW_FILE"));
		manager.load(af.get("POP_FILE"), Sound.class);
		assets.add(af.get("POP_FILE"));
		manager.load(af.get("BGM_FILE"), Sound.class);
		assets.add(af.get("BGM_FILE"));
		manager.load(af.get("BURN_FILE"), Sound.class);
		assets.add(af.get("BURN_FILE"));
		manager.load(af.get("MATCH_FILE"), Sound.class);
		assets.add(af.get("MATCH_FILE"));
		manager.load(af.get("SPLASH_FILE"), Sound.class);
		assets.add(af.get("SPLASH_FILE"));
		manager.load(af.get("ROPEBURN_FILE"), Sound.class);
		assets.add(af.get("ROPEBURN_FILE"));
		manager.load(af.get("THUMP_FILE"), Sound.class);
		assets.add(af.get("THUMP_FILE"));
		manager.load(af.get("BUBBLE_FILE"), Sound.class);
		assets.add(af.get("BUBBLE_FILE"));
		manager.load(af.get("LOSER_FILE"), Sound.class);
		assets.add(af.get("LOSER_FILE"));
		manager.load(af.get("YAY_FILE"), Sound.class);
		assets.add(af.get("YAY_FILE"));
		manager.load(af.get("CLAP_FILE"), Sound.class);
		assets.add(af.get("CLAP_FILE"));
		manager.load(af.get("EXTINGUISH_FILE"), Sound.class);
		assets.add(af.get("EXTINGUISH_FILE"));
		manager.load(af.get("SPIRITING_FILE"), Sound.class);
		assets.add(af.get("SPIRITING_FILE"));
		manager.load(af.get("MADWATER_FILE"), Sound.class);
		assets.add(af.get("MADWATER_FILE"));
		
		manager.load(af.get("WATER"), Texture.class);
		assets.add(af.get("WATER"));
		
		manager.load(af.get("CHECKPOINT_FLAG"), Texture.class);
		assets.add(af.get("CHECKPOINT_FLAG"));
		
		manager.load(af.get("CASTLE"), Texture.class);
		assets.add(af.get("CASTLE"));
		manager.load(af.get("LIGHT"), Texture.class);
		assets.add(af.get("LIGHT"));
		manager.load(af.get("CIRCLE"), Texture.class);
		assets.add(af.get("CIRCLE"));
		manager.load(af.get("LEVEL_BACKGROUND"), Texture.class);
		assets.add(af.get("LEVEL_BACKGROUND"));
		
		for (int i=0; i<af.tutorialInstructions.length; i++){
			manager.load(af.get("TUTORIAL_INST"+i), Texture.class);
			assets.add(af.get("TUTORIAL_INST"+i));
		}
		
		for (int i=1; i<=20; i++){
			manager.load(af.get(String.valueOf(i)), Texture.class);
			assets.add(af.get(String.valueOf(i)));
		}
	}

	/**
	 * Returns a newly loaded texture region for the given file.
	 *
	 * This helper methods is used to set texture settings (such as scaling, and
	 * whether or not the texture should repeat) after loading.
	 *
	 * @param manager
	 *            Reference to global asset manager.
	 * @param file
	 *            The texture (region) file
	 * @param repeat
	 *            Whether the texture should be repeated
	 *
	 * @return a newly loaded texture region for the given file.
	 */
	protected TextureRegion createTexture(AssetManager manager, String file,
			boolean repeat) {
		if (manager.isLoaded(file)) {
			TextureRegion region = new TextureRegion(
					manager.get(file, Texture.class));
			region.getTexture().setFilter(Texture.TextureFilter.Linear,
					Texture.TextureFilter.Linear);
			if (repeat) {
				region.getTexture().setWrap(Texture.TextureWrap.Repeat,
						Texture.TextureWrap.Repeat);
			}
			return region;
		}
		return null;
	}

	/**
	 * Returns a newly loaded filmstrip for the given file.
	 *
	 * This helper methods is used to set texture settings (such as scaling, and
	 * the number of animation frames) after loading.
	 *
	 * @param manager
	 *            Reference to global asset manager.
	 * @param file
	 *            The texture (region) file
	 * @param rows
	 *            The number of rows in the filmstrip
	 * @param cols
	 *            The number of columns in the filmstrip
	 * @param size
	 *            The number of frames in the filmstrip
	 *
	 * @return a newly loaded texture region for the given file.
	 */
	protected FilmStrip createFilmStrip(AssetManager manager, String file,
			int rows, int cols, int size) {
		if (manager.isLoaded(file)) {
			FilmStrip strip = new FilmStrip(manager.get(file, Texture.class),
					rows, cols, size);
			strip.getTexture().setFilter(Texture.TextureFilter.Linear,
					Texture.TextureFilter.Linear);
			return strip;
		}
		return null;
	}

	/**
	 * Loads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic
	 * loaders this time. However, we still want the assets themselves to be
	 * static. So we have an AssetState that determines the current loading
	 * state. If the assets are already loaded, this method will do nothing.
	 * 
	 * @param managernn
	 *            Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager) {
		if (worldAssetState != AssetState.LOADING) {
			return;
		}
		af.loadContent(manager);
		for (WorldController c : controllers) {
			c.setAssetFile(af);
		}

		worldAssetState = AssetState.COMPLETE;
	}

	/**
	 * Called when the Application is first created.
	 * 
	 * This is method immediately loads assets for the loading screen, and
	 * prepares the asynchronous loader for all other assets.
	 */
	public void create() {

		canvas = new GameCanvas();
		loading = new LoadingMode(canvas, manager, 1);

		loading.setScreenListener(this);
		preLoadContent(manager);
		setScreen(loading);

		// Initialize the three game worlds

		int levels = 21;
		controllers = new WorldController[levels]; ////
		current = 0;
		for(int i = 0; i<5; i++){
			controllers[i] = new TutorialController(i);
		}
		
		for(int i=5;i<levels;i++){
			if (i == 7 || i == 9 || i ==11 || i == 12){
					controllers[i] = new TutorialController(i);
				}else{
					controllers[i] = new AidenController(i);
				}
			
		}
		loading.setScreenListener(this);

		controllers[levels-1] = new LevelEditor();///////
	}

	public void unloadContent(AssetManager manager) {
		for (String s : assets) {
			if (manager.isLoaded(s)) {
				manager.unload(s);
			}
		}
	}

	/**
	 * Called when the Application is destroyed.
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		unloadContent(manager);
		for (int ii = 0; ii < controllers.length; ii++) {
			// controllers[ii].unloadContent(manager);
			controllers[ii].dispose();
		}

		canvas.dispose();
		canvas = null;

		// Unload all of the resources
		manager.clear();
		manager.dispose();
		super.dispose();
	}

	/**
	 * Called when the Application is resized.
	 *
	 * This can happen at any point during a non-paused state but will never
	 * happen before a call to create().
	 *
	 * @param width
	 *            The new width in pixels
	 * @param height
	 *            The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width, height);
	}

	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen
	 *            The screen requesting to exit
	 * @param exitCode
	 *            The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		canvas.setEditor(false);
		if (screen == loading) {
			if(exitCode == 100){
				Gdx.app.exit();
			}
			exitCode = exitCode % controllers.length;
			loadContent(manager);
			for (int ii = 0; ii < controllers.length; ii++) {
				controllers[ii].setScreenListener(this);
				controllers[ii].setCanvas(canvas);
			}
			controllers[exitCode].reset();
			current = exitCode;
			setScreen(controllers[exitCode]);
		} 
		else if (exitCode == WorldController.EXIT_HOME){
			af.clap.stop();
			controllers[current].reset();
			loading.pressState = 4;
			setScreen(loading);
		}
		else if (exitCode == WorldController.EXIT_NEXT) {
			if(controllers[current] instanceof AidenController){
				((AidenController) controllers[current]).stopSound();
				af.clap.stop();
			}
			current = (current + 1) % controllers.length;
			System.out.println("start");
			controllers[current].reset();
			System.out.println("end");
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_PREV) {
			af.clap.stop();
			current = (current + controllers.length - 1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
	}

}
