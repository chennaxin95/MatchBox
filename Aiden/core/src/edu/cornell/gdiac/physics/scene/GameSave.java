package edu.cornell.gdiac.physics.scene;

import com.badlogic.gdx.utils.JsonValue;

public class GameSave {

	/** The current level */
	private int level;

	/**
	 * The index of the current checkpoint in the fuel box array. Null if
	 * default start position
	 */
	private int checkpoint;

	public GameSave(String s) {

		JSONParser jp = new JSONParser(s);
		JsonValue jv = jp.getJsonValue();
		level = jv.getInt("Level");
		checkpoint = jv.getInt("Checkpoint");

	}

	/** Getter for level */
	public int getLevel() {
		return level;
	}

	/** Setter for level */
	public void setLevel(int lvl) {
		level = lvl;
	}

	/** Getter for checkpoint */
	public int getCheckpoint() {
		return checkpoint;
	}

	/** Setter for checkpoint */
	public void setCheckpoint(int chk) {
		checkpoint = chk;
	}

}
