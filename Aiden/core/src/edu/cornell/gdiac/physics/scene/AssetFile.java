package edu.cornell.gdiac.physics.scene;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

public class AssetFile {

	/** A HashMap of asset files and their identifiers */
	private HashMap<String, String> files;
	private static int FONT_SIZE = 64;
	public Array<String> assets;

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

	}

	public String get(String s) {
		return files.get(s);
	}

	public int fontSize() {
		return FONT_SIZE;
	}

}
