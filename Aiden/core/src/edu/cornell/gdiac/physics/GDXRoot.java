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
	/** List of all WorldControllers */
	private WorldController[] controllers;
	/** List of scene objects */
	private Scene[] scenes;
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
		System.out.println(af.get("EARTH_FILE"));
		assets.add(af.get("EARTH_FILE"));
		manager.load(af.get("GOAL_FILE"), Texture.class);
		assets.add(af.get("GOAL_FILE"));

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

		manager.load(af.get("JUMP_FILE"), Sound.class);
		assets.add(af.get("JUMP_FILE"));
		manager.load(af.get("PEW_FILE"), Sound.class);
		assets.add(af.get("PEW_FILE"));
		manager.load(af.get("POP_FILE"), Sound.class);
		assets.add(af.get("POP_FILE"));

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

		// Initialize the three game worlds
		controllers = new WorldController[4];
		scenes = new Scene[2];
		controllers[0] = new AidenController(0);
		// controllers[0].preLoadContent(manager);
		controllers[1] = new AidenController(1);
		// controllers[1].preLoadContent(manager);
		// scenes[0] = new Scene("./json/level0.json");
		current = 0;
		controllers[2] = new AidenController(2);
		// controllers[2].preLoadContent(manager);
		loading.setScreenListener(this);

		controllers[3] = new LevelEditor();
		// controllers[3].preLoadContent(manager);
		loading.setScreenListener(this);
		preLoadContent(manager);
		setScreen(loading);
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
			loadContent(manager);
			for (int ii = 0; ii < controllers.length; ii++) {
				// controllers[ii].loadContent(manager);
				controllers[ii].setScreenListener(this);
				controllers[ii].setCanvas(canvas);
			}
			controllers[current].reset();
			setScreen(controllers[current]);

			loading.dispose();
			loading = null;
		} else if (exitCode == WorldController.EXIT_NEXT) {
			current = (current + 1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_PREV) {
			current = (current + controllers.length - 1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		}
	}

}
