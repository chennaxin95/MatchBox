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
	/** The font for giving messages to the player */
	public BitmapFont displayFont;
	public BitmapFont fuelFont;

	/** Texture asset for character avatar */
	public TextureRegion avatarTexture;
	/** Texture for woodblock */
	public TextureRegion woodTexture;
	/** Texture for fuel */
	public TextureRegion fuelTexture;
	/** texture for water */
	public TextureRegion waterTexture;

	public TextureRegion stoneTexture;
	public TextureRegion ropeTexture;
	/** Texture for background */
	public TextureRegion backGround;

	/** Texture for aiden animation */
	public FilmStrip AidenAnimeTexture;
	public FilmStrip AidenDieTexture;
	public FilmStrip AidenJumpTexture;
	public FilmStrip WaterWalkTexture;
	public FilmStrip WaterDieTexture;
	/** Texture for burning animation */
	public FilmStrip[] burningTexture;

	public AssetFile() {
		this.files = new HashMap<String, String>();
		files.put("EARTH_FILE", "shared/earthtile.png");
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
		files.put("JUMP_FILE", "platform/jump.mp3");
		files.put("PEW_FILE", "platform/pew.mp3");
		files.put("POP_FILE", "platform/plop.mp3");
		files.put("BACKGROUND", "shared/background.png");
		files.put("BACKGROUND_FILE", "shared/loading.png");
		files.put("PROGRESS_FILE", "shared/progressbar.png");
		files.put("PLAY_BTN_FILE", "shared/play.png");
		files.put("WATER_DIE_FILE", "platform/water-die-animation.png");
		files.put("AIDEN_JUMP_FILE", "platform/jump-s.png");
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

		// Allocate the font
		if (manager.isLoaded(files.get("FONT_FILE"))) {
			displayFont = manager.get(files.get("FONT_FILE"), BitmapFont.class);
			fuelFont = manager.get(files.get("FUEL_FONT"), BitmapFont.class);
			fuelFont.getData().setScale(0.5f, 0.5f);
		} else {
			displayFont = null;
		}
		woodTexture = createTexture(manager, files.get("WOOD_FILE"), false);
		avatarTexture = createTexture(manager, files.get("DUDE_FILE"), false);
		fuelTexture = createTexture(manager, files.get("FUEL_FILE"), false);
		ropeTexture = createTexture(manager, files.get("ROPE_FILE"), true);

		backGround = createTexture(manager, files.get("BACKGROUND"), false);
		waterTexture = createTexture(manager, files.get("WATER_FILE"), false);
		stoneTexture = createTexture(manager, files.get("STONE_FILE"), false);
		WaterWalkTexture = createFilmStrip(manager, files.get("WATER_WALK"), 4,
				1,
				4);
		AidenDieTexture = createFilmStrip(manager, files.get("AIDEN_DIE_FILE"),
				13,
				1, 13);
		AidenAnimeTexture = createFilmStrip(manager,
				files.get("AIDEN_ANIME_FILE"),
				12, 1,
				12);
		AidenJumpTexture = createFilmStrip(manager, files.get("AIDEN_JUMP_FILE"), 5, 1, 5);
		WaterDieTexture = createFilmStrip(manager,
				files.get("WATER_DIE_FILE"),
				12, 1,
				12);

		burningTexture = new FilmStrip[10];
		for (int i = 0; i < 10; i++) {
			burningTexture[i] = createFilmStrip(manager,
					files.get("BURNING_FILE"),
					7, 1, 7);
		}

		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager, files.get("JUMP_FILE"));
		sounds.allocate(manager, files.get("PEW_FILE"));
		sounds.allocate(manager, files.get("POP_FILE"));

	}

}
