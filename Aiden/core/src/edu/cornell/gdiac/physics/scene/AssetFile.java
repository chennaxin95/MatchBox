package edu.cornell.gdiac.physics.scene;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
	public Music bgm;
	public Music jump;
	public Music burn;
	public Music match;
	public Sound splash;
	public Music ropeburn;
	public Sound thump;
	public Music bubble;
	public Music loser;
	public Music yay;
	public Music clap;
	public Music extinguish;
	public Music spiriting;
	public Music madwater;
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
	public TextureRegion barLow;
	public TextureRegion barBack;
	public TextureRegion barIcon;
	public TextureRegion barYellow;
	public TextureRegion barGray;
	public TextureRegion barDie;
	public TextureRegion nextLevel;
	
	/** Texture for background */
	public TextureRegion backGround;
	public TextureRegion backGround0;
	public TextureRegion tutorial4;
	public TextureRegion tutorial3;
	public TextureRegion tutorial2;
	public TextureRegion tutorial1;
	public TextureRegion homeButton;
	public TextureRegion resumeButton;
	public TextureRegion restartButton;
	public TextureRegion youWin;
	public TextureRegion youLose;
	public TextureRegion retry;
	public TextureRegion replay;
	public TextureRegion skip;
	public TextureRegion levelSelect;
	public TextureRegion restartIcon;
	
	
	public TextureRegion editorPanelTexture;
	public TextureRegion paused;
	public TextureRegion music;
	public TextureRegion sound;
	public TextureRegion music_no;
	public TextureRegion sound_no;
	public TextureRegion water;
	public TextureRegion castle;
	public TextureRegion light;
	public TextureRegion circle;
	
	/** Texture for aiden animation */
	public FilmStrip AidenGlow;
	public FilmStrip AidenAnimeTexture;
	public FilmStrip AidenSpiritTexture;
	public FilmStrip AidenDieTexture;
	public FilmStrip AidenJumpTexture;
	public FilmStrip AidenRunTexture;
	public FilmStrip AidenIdleTexture;
	public FilmStrip WaterWalkTexture;
	public FilmStrip WaterChaseTexture;
	public FilmStrip WaterDieTexture;
	public FilmStrip FireBall;
	
	/** Texture for burning animation */
	public FilmStrip[] burningTexture;
	public FilmStrip[] fireBall;
	public FilmStrip[] WaterWalkTextures;
	public TextureRegion checkpointTexture;
	
	public TextureRegion[] tutorialInstructions;
	public TextureRegion level_background;
	public TextureRegion[] numberTextures;

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
		files.put("BURN_FILE", "music/burn.mp3");
		files.put("MATCH_FILE", "music/match.mp3");
		files.put("SPLASH_FILE", "music/splash.mp3");
		files.put("ROPEBURN_FILE", "music/ropeburn.mp3");
		files.put("BUBBLE_FILE", "music/bubble.mp3");
		files.put("THUMP_FILE", "music/thump.mp3");
		files.put("LOSER_FILE", "music/loser.mp3");
		files.put("YAY_FILE", "music/yay.mp3");
		files.put("CLAP_FILE", "music/clap.mp3");
		files.put("EXTINGUISH_FILE", "music/extinguish.mp3");
		files.put("SPIRITING_FILE", "music/spiriting.mp3");
		files.put("MADWATER_FILE", "music/madwater.mp3");
		files.put("BACKGROUND0", "background/tutorial0background2.png");
		files.put("BACKGROUND", "background/background.png");
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
		files.put("BAR_OUTTER", "platform/empty.png");
		files.put("BAR_INNER", "platform/red.png");
		files.put("BAR_LOW", "platform/dark red.png");
		files.put("AIDEN_RUN", "platform/fast.png");
		files.put("AIDEN_IDLE", "platform/idle.png");
		files.put("WATER_CHASE", "platform/water-chase.png");
		files.put("PAUSED", "shared/paused.png");
		files.put("MUSIC", "shared/music.png");
		files.put("SOUND", "shared/sound.png");
		files.put("MUSIC_NO", "shared/music-no.png");
		files.put("SOUND_NO", "shared/sound-no.png");
		files.put("BAR_BACK", "platform/white.png");
		files.put("FIRE_BALL", "platform/fuel ball-s64.png");
		files.put("BAR_ICON", "platform/color_icon.png");
		files.put("BAR_YELLOW", "platform/yellow_icon.png");
		files.put("BAR_GRAY", "platform/grey_icon.png");
		files.put("AIDEN_GLOW", "platform/aidenGlow.png");
		files.put("BAR_DIE", "platform/empty grey.png");
		files.put("GOOD_JOB", "shared/Good Job.png");
		files.put("RETRY", "shared/retry.png");
		files.put("REPLAY", "shared/replay.png");
		files.put("SKIP", "shared/skip.png");
		files.put("YOU_LOSE", "shared/you lose.png");
		files.put("NEXT_LEVEL", "shared/nextlevel.png");
		files.put("TUT4_BACK", "background/tutorial4.png");
		files.put("TUT1_BACK", "background/tutorial1.png");
		files.put("TUT2_BACK", "background/tutorial2.png");
		files.put("TUT3_BACK", "background/tutorial3.png");
		files.put("LEVEL_S", "shared/levels.png");
		files.put("RESTART_ICON", "shared/restart icon.png");
		files.put("CASTLE", "shared/castle.png");
		files.put("LIGHT", "shared/gradient.png");
		files.put("CIRCLE", "shared/circle.png");
		files.put("LEVEL_BACKGROUND", "background/background2.png");
		
//		files.put("1", "shared/1.png");
//		files.put("2", "shared/2.png");
//		files.put("3", "shared/3.png");
//		files.put("4", "shared/4.png");
//		files.put("5", "shared/1.png");
//		files.put("6", "shared/1.png");
//		files.put("7", "shared/1.png");
//		files.put("8", "shared/1.png");
//		files.put("9", "shared/1.png");
//		files.put("10", "shared/1.png");
//		files.put("11", "shared/1.png");
//		files.put("12", "shared/1.png");
//		files.put("13", "shared/1.png");
//		files.put("14", "shared/1.png");
//		files.put("15", "shared/1.png");
//		files.put("16", "shared/1.png");
//		files.put("17", "shared/1.png");
//		files.put("18", "shared/1.png");
//		files.put("19", "shared/1.png");
//		files.put("20", "shared/1.png");
		files.put("AIDEN_SPIRIT", "platform/spirit-s64.png");
		files.put("WATER", "shared/water.png");
		files.put("CHECKPOINT_FLAG", "shared/flag.png");
		
		tutorialInstructions=new TextureRegion[18];
		numberTextures=new TextureRegion[20];
		for (int i=0; i<tutorialInstructions.length; i++){
			files.put("TUTORIAL_INST"+i, "platform/tutorial/instruction_"+i+".png");
		}
		for (int i=1; i<=20; i++){
			files.put(String.valueOf(i), "shared/numbers/"+String.valueOf(i)+".png");
		}
		
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
		youLose = createTexture(manager, files.get("YOU_LOSE"), false);
		youWin = createTexture(manager, files.get("GOOD_JOB"), false);
		retry = createTexture(manager, files.get("RETRY"), false);
		replay = createTexture(manager, files.get("REPLAY"), false);
		skip = createTexture(manager, files.get("SKIP"), false);
		nextLevel = createTexture(manager, files.get("NEXT_LEVEL"), false);
		levelSelect = createTexture(manager, files.get("LEVEL_S"), false);
		restartIcon = createTexture(manager, files.get("RESTART_ICON"), false);
		
		longRope = createTexture(manager, files.get("LONG_ROPE"), false);
		trapDoor = createTexture(manager, files.get("TRAP_DOOR"), false);
		barOutter = createTexture(manager, files.get("BAR_OUTTER"),false);
		barInner = createTexture(manager, files.get("BAR_INNER"), false);
		paused = createTexture(manager, files.get("PAUSED"), false);
		music = createTexture(manager, files.get("MUSIC"), false);
		music_no = createTexture(manager, files.get("MUSIC_NO"), false);
		sound = createTexture(manager, files.get("SOUND"), false);
		sound_no = createTexture(manager, files.get("SOUND_NO"), false);
		barLow = createTexture(manager, files.get("BAR_LOW"), false);
		barBack = createTexture(manager, files.get("BAR_BACK"), false);
		barIcon = createTexture(manager, files.get("BAR_ICON"), false);
		barYellow = createTexture(manager, files.get("BAR_YELLOW"),false);
		barGray = createTexture(manager, files.get("BAR_GRAY"),false);
		barDie = createTexture(manager, files.get("BAR_DIE"), false);
		
		tutorial4 = createTexture(manager, files.get("TUT4_BACK"), false);
		tutorial1 = createTexture(manager, files.get("TUT1_BACK"), false);
		tutorial2 = createTexture(manager, files.get("TUT2_BACK"), false);
		tutorial3 = createTexture(manager, files.get("TUT3_BACK"), false);
		
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
		backGround0 = createTexture(manager, files.get("BACKGROUND0"),false);
		waterTexture = createTexture(manager, files.get("WATER_FILE"), false);
		stoneTexture = createTexture(manager, files.get("STONE_FILE"), false);
		ropeLongTexture = createTexture(manager, files.get("ROPE_LONG_FILE"), false);
		trapdoorTexture = createTexture(manager, files.get("TRAPDOOR_FILE"), false);
		nailTexture = createTexture(manager, files.get("NAIL_FILE"), false);
		checkpointTexture=createTexture(manager, files.get("CHECKPOINT_FLAG"), false);
		castle=createTexture(manager, files.get("CASTLE"), false);
		light=createTexture(manager, files.get("LIGHT"), false);
		circle=createTexture(manager, files.get("CIRCLE"), false);
		level_background = createTexture(manager, files.get("LEVEL_BACKGROUND"), false);
		
		
		AidenSpiritTexture = createFilmStrip(manager, files.get("AIDEN_SPIRIT"), 5, 1, 5);
//		WaterWalkTexture = createFilmStrip(manager, files.get("WATER_WALK"), 4,
//				1,
//				4);
		WaterWalkTextures=new FilmStrip[10];
		for (int i = 0; i < 10; i++){
			WaterWalkTextures[i] = createFilmStrip(manager, files.get("WATER_WALK"), 4,
					1,
					4);
		}
		AidenDieTexture = createFilmStrip(manager, files.get("AIDEN_DIE_FILE"),
				12,
				1, 12);
		AidenAnimeTexture = createFilmStrip(manager,
				files.get("AIDEN_ANIME_FILE"),
				12, 1,
				12);
		AidenJumpTexture = createFilmStrip(manager,
				files.get("AIDEN_JUMP_FILE"), 12, 1, 12);
		AidenGlow = createFilmStrip(manager,
				files.get("AIDEN_GLOW"), 6, 1, 6);
		WaterDieTexture = createFilmStrip(manager,
				files.get("WATER_DIE_FILE"),
				12, 1,
				12);
		AidenRunTexture = createFilmStrip(manager, files.get("AIDEN_RUN"), 12, 1, 12);
		AidenIdleTexture = createFilmStrip(manager, files.get("AIDEN_IDLE"), 12, 1, 12);
		WaterChaseTexture = createFilmStrip(manager, files.get("WATER_CHASE"), 4, 1, 4);

		burningTexture = new FilmStrip[20];
		for (int i = 0; i < 20; i++) {
			burningTexture[i] = createFilmStrip(manager,
					files.get("BURNING_FILE"),
					7, 1, 7);
		}
		fireBall = new FilmStrip[10];
		for (int i = 0; i < 10; i++){
			fireBall[i] = createFilmStrip(manager, files.get("FIRE_BALL"), 4, 1, 4);
		}
		
		for (int i=0; i<this.tutorialInstructions.length; i++){
			tutorialInstructions[i]=createTexture(manager, files.get("TUTORIAL_INST"+i), false);
		}
		
		for (int i=0; i<this.numberTextures.length; i++){
			numberTextures[i]=createTexture(manager, files.get(String.valueOf(i+1)), false);
		}
		
		bgm = Gdx.audio.newMusic(Gdx.files.internal("music/bgm.mp3"));
		jump = Gdx.audio.newMusic(Gdx.files.internal("music/jump.mp3"));
		burn = Gdx.audio.newMusic(Gdx.files.internal("music/burn.mp3"));
		match = Gdx.audio.newMusic(Gdx.files.internal("music/match.mp3"));
		splash = Gdx.audio.newSound(Gdx.files.internal("music/splash.mp3"));
		ropeburn = Gdx.audio.newMusic(Gdx.files.internal("music/ropeburn.mp3"));
		thump = Gdx.audio.newSound(Gdx.files.internal("music/thump.mp3"));
		bubble = Gdx.audio.newMusic(Gdx.files.internal("music/bubble.mp3"));
		loser = Gdx.audio.newMusic(Gdx.files.internal("music/loser.mp3"));
		yay = Gdx.audio.newMusic(Gdx.files.internal("music/yay.mp3"));
		clap = Gdx.audio.newMusic(Gdx.files.internal("music/clap.mp3"));
		extinguish = Gdx.audio.newMusic(Gdx.files.internal("music/extinguish.mp3"));
		spiriting = Gdx.audio.newMusic(Gdx.files.internal("music/spiriting.mp3"));
		madwater = Gdx.audio.newMusic(Gdx.files.internal("music/madwater.mp3"));
		
		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager, files.get("JUMP_FILE"));
		sounds.allocate(manager, files.get("PEW_FILE"));
		sounds.allocate(manager, files.get("POP_FILE"));
		sounds.allocate(manager, files.get("BGM_FILE"));
		sounds.allocate(manager, files.get("BURN_FILE"));
		sounds.allocate(manager, files.get("MATCH_FILE"));
		sounds.allocate(manager, files.get("SPLASH_FILE"));
		sounds.allocate(manager, files.get("ROPEBURN_FILE"));
		sounds.allocate(manager, files.get("THUMP_FILE"));
		sounds.allocate(manager, files.get("BUBBLE_FILE"));
		sounds.allocate(manager, files.get("LOSER_FILE"));
		sounds.allocate(manager, files.get("YAY_FILE"));
		sounds.allocate(manager, files.get("CLAP_FILE"));
		sounds.allocate(manager, files.get("EXTINGUISH_FILE"));
		sounds.allocate(manager, files.get("SPIRITING_FILE"));
		sounds.allocate(manager, files.get("MADWATER_FILE"));
		// water platform
		water=createTexture(manager, files.get("WATER"), true);
	}

}
