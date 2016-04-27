package edu.cornell.gdiac.physics.scene;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.SoundController;

public class AssetFile {

	/** A HashMap of asset files and their identifiers */
	private HashMap<String, String> files;
	private static int FONT_SIZE = 64;
	public Array<String> assets;

	/** The texture for walls and platforms */
	public TextureRegion earthTile;
	/** The texture for the exit condition */
	public TextureRegion goalTile;
	public TextureRegion burnablePlatform;
	
	/** The font for giving messages to the player */
	public BitmapFont displayFont;
	public BitmapFont fuelFont;
	public BitmapFont panelFont;

	/** Texture asset for character avatar */
	public TextureRegion avatarTexture;
	/** Texture for woodblock */
	public TextureRegion woodTexture;
	/** Texture for fuel */
	public TextureRegion fuelTexture;
	/** texture for water */
	public TextureRegion waterTexture;
	public TextureRegion nailTexture;
	public TextureRegion stoneTexture;
	public TextureRegion MenuBack;
	public TextureRegion ropeTexture;
	public TextureRegion ropeLongTexture;
	public TextureRegion trapdoorTexture;
	public TextureRegion black;
	public TextureRegion longRope;
	public TextureRegion trapDoor;
	public TextureRegion barOutter;
	public TextureRegion barInner;
	/** Texture for background */
	public TextureRegion backGround;
	public TextureRegion homeButton;
	public TextureRegion resumeButton;
	public TextureRegion restartButton;
	public TextureRegion editorPanelTexture;
	public TextureRegion paused;
	public TextureRegion music;
	public TextureRegion sound;
	public TextureRegion music_no;
	public TextureRegion sound_no;
	
	/** Texture for aiden animation */
	public FilmStrip AidenAnimeTexture;
	public FilmStrip AidenDieTexture;
	public FilmStrip AidenJumpTexture;
	public FilmStrip AidenRunTexture;
	public FilmStrip AidenIdleTexture;
	public FilmStrip WaterWalkTexture;
	public FilmStrip WaterChaseTexture;
	public FilmStrip WaterDieTexture;
	
	/** Texture for burning animation */
	public FilmStrip[] burningTexture;

	public AssetFile() {
		this.files = new HashMap<String, String>();
		files.put("MENU_BACK", "shared/menuBack.png");
		files.put("BLACK", "shared/black.png");
		files.put("EARTH_FILE", "shared/earthtile.png");
		files.put("BURNP_FILE", "shared/burnableearth.png");
		files.put("GOAL_FILE", "platform/goaldoor.png");
		files.put("FONT_FILE", "shared/RetroGame.ttf");
		files.put("FUEL_FONT", "shared/ShadowsIntoLight.ttf");
		files.put("DUDE_FILE", "platform/dude.png");
		files.put("WATER_FILE", "platform/water.png");
		files.put("BARRIER_FILE", "platform/barrier.png");
		files.put("BULLET_FILE", "platform/bullet.png");
		files.put("ROPE_FILE", "platform/rope.png");
		files.put("WOOD_FILE", "platform/woodenBlock.png");
		files.put("FUEL_FILE", "platform/fuelBlock.png");
		files.put("AIDEN_ANIME_FILE", "platform/aidenAnime.png");
		files.put("AIDEN_DIE_FILE", "platform/die_animation.png");
		files.put("WATER_WALK", "platform/water_animation.png");
		files.put("BURNING_FILE", "platform/blockburning.png");
		files.put("STONE_FILE", "platform/stone.png");
		files.put("JUMP_FILE", "music/jump.mp3");
		files.put("PEW_FILE", "music/pew.mp3");
		files.put("POP_FILE", "music/plop.mp3");
		files.put("BGM_FILE", "music/bgm.mp3");
		files.put("BACKGROUND", "shared/background.png");
		files.put("BACKGROUND_FILE", "shared/loading.png");
		files.put("PROGRESS_FILE", "shared/progressbar.png");
		files.put("PLAY_BTN_FILE", "shared/start.png");
		files.put("MAIN_MENU", "shared/Main Menu.png");
		files.put("LEVELS", "shared/levels.png");
		files.put("SETTINGS", "shared/settings.png");
		files.put("CREDITS", "shared/credits.png");
		files.put("WATER_DIE_FILE", "platform/water-die-animation.png");
		files.put("AIDEN_JUMP_FILE", "platform/jump-s.png");
		files.put("NAIL_FILE", "platform/smoke.png");
		files.put("LONG_ROPE", "platform/rope-long.png");
		files.put("TRAP_DOOR", "platform/trapdoor.png");
		files.put("HOME_BUTTON", "shared/home.png");
		files.put("RESUME_BUTTON", "shared/resume.png");
		files.put("RESTART_BUTTON", "shared/restart.png");
		files.put("EDITOR_PANEL_FILE", "shared/panel.png");
		files.put("ROPE_LONG_FILE", "platform/rope-long.png");
		files.put("TRAPDOOR_FILE", "platform/trapdoor.png");
		files.put("BAR_OUTTER", "platform/barOuter.png");
		files.put("BAR_INNER", "platform/barInner.png");
		files.put("AIDEN_RUN", "platform/fast.png");
		files.put("AIDEN_IDLE", "platform/idle.png");
		files.put("WATER_CHASE", "platform/water-chase.png");
		files.put("PAUSED", "shared/paused.png");
		files.put("MUSIC", "shared/music.png");
		files.put("SOUND", "shared/sound.png");
		files.put("MUSIC_NO", "shared/music-no.png");
		files.put("SOUND_NO", "shared/sound-no.png");
		System.out.println(files);
	}

	public String get(String s) {
		return files.get(s);
	}

	public int fontSize() {
		return FONT_SIZE;
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

	public void loadContent(AssetManager manager) {

		// files.get("")
		// Allocate the tiles
		earthTile = createTexture(manager, files.get("EARTH_FILE"), true);
		goalTile = createTexture(manager, files.get("GOAL_FILE"), true);
		burnablePlatform = createTexture(manager, files.get("BURNP_FILE"), true);
		MenuBack = createTexture(manager, files.get("MENU_BACK"), false);
		black = createTexture(manager, files.get("BLACK"), false);
		homeButton = createTexture(manager, files.get("HOME_BUTTON"), false);
		resumeButton = createTexture(manager, files.get("RESUME_BUTTON"), false);
		restartButton = createTexture(manager, files.get("RESTART_BUTTON"), false);
		longRope = createTexture(manager, files.get("LONG_ROPE"), false);
		trapDoor = createTexture(manager, files.get("TRAP_DOOR"), false);
		barOutter = createTexture(manager, files.get("BAR_OUTTER"),false);
		barInner = createTexture(manager, files.get("BAR_INNER"), false);
		paused = createTexture(manager, files.get("PAUSED"), false);
		music = createTexture(manager, files.get("MUSIC"), false);
		music_no = createTexture(manager, files.get("MUSIC_NO"), false);
		sound = createTexture(manager, files.get("SOUND"), false);
		sound_no = createTexture(manager, files.get("SOUND_NO"), false);
		
		
		// Allocate the font
		if (manager.isLoaded(files.get("FONT_FILE"))) {
			displayFont = manager.get(files.get("FONT_FILE"), BitmapFont.class);
			fuelFont = manager.get(files.get("FUEL_FONT"), BitmapFont.class);
			fuelFont.getData().setScale(0.5f, 0.5f);
			panelFont = manager.get(files.get("FUEL_FONT"), BitmapFont.class);
			panelFont.getData().setScale(0.35f, 0.35f);
			
		} else {
			displayFont = null;
		}
		editorPanelTexture=createTexture(manager, files.get("EDITOR_PANEL_FILE"), false);
		woodTexture = createTexture(manager, files.get("WOOD_FILE"), false);
		avatarTexture = createTexture(manager, files.get("DUDE_FILE"), false);
		fuelTexture = createTexture(manager, files.get("FUEL_FILE"), false);
		ropeTexture = createTexture(manager, files.get("ROPE_FILE"), true);
		backGround = createTexture(manager, files.get("BACKGROUND"), false);
		waterTexture = createTexture(manager, files.get("WATER_FILE"), false);
		stoneTexture = createTexture(manager, files.get("STONE_FILE"), false);
		ropeLongTexture = createTexture(manager, files.get("ROPE_LONG_FILE"), false);
		trapdoorTexture = createTexture(manager, files.get("TRAPDOOR_FILE"), false);
		nailTexture = createTexture(manager, files.get("NAIL_FILE"), false);
		WaterWalkTexture = createFilmStrip(manager, files.get("WATER_WALK"), 4,
				1,
				4);
		AidenDieTexture = createFilmStrip(manager, files.get("AIDEN_DIE_FILE"),
				12,
				1, 12);
		AidenAnimeTexture = createFilmStrip(manager,
				files.get("AIDEN_ANIME_FILE"),
				12, 1,
				12);
		AidenJumpTexture = createFilmStrip(manager,
				files.get("AIDEN_JUMP_FILE"), 12, 1, 12);
		WaterDieTexture = createFilmStrip(manager,
				files.get("WATER_DIE_FILE"),
				12, 1,
				12);
		AidenRunTexture = createFilmStrip(manager, files.get("AIDEN_RUN"), 12, 1, 12);
		AidenIdleTexture = createFilmStrip(manager, files.get("AIDEN_IDLE"), 12, 1, 12);
		WaterChaseTexture = createFilmStrip(manager, files.get("WATER_CHASE"), 4, 1, 4);

		burningTexture = new FilmStrip[200];
		for (int i = 0; i < 200; i++) {
			burningTexture[i] = createFilmStrip(manager,
					files.get("BURNING_FILE"),
					7, 1, 7);
		}

		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager, files.get("JUMP_FILE"));
		sounds.allocate(manager, files.get("PEW_FILE"));
		sounds.allocate(manager, files.get("POP_FILE"));
		sounds.allocate(manager, files.get("BGM_FILE"));
	}

}
