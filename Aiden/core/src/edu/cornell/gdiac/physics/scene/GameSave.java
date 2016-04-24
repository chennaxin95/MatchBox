package edu.cornell.gdiac.physics.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import edu.cornell.gdiac.physics.editor.ProjectModelJsonRep;

public class GameSave {

	/** The current level */
	private int level;

	/**
	 * The index of the current checkpoint in the fuel box array. Equals -1 if
	 * default start position
	 */
	private int checkpoint;

	/**
	 * How many levels the player has unlocked. I.e. the highest level the
	 * player has reached
	 */
	private int unlocked;

	private String filename;

	public GameSave(String s) {

		filename = s;
		JSONParser jp = new JSONParser(s);
		JsonValue jv = jp.getJsonValue();
		level = jv.getInt("level");
		checkpoint = jv.getInt("checkpoint");
		unlocked = jv.getInt("unlocked");

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

	/** Getter for unlocked */
	public int getUnlocked() {
		return unlocked;
	}

	/** Setter for unlocked */
	public void setUnlocked(int unlck) {
		unlocked = unlck;
	}

	/** Getter for filename */
	public String getFilename() {
		return filename;
	}

	/** Setter for filename */
	public void setFilename(String s) {
		filename = s;
	}

	/** Saves game to json */
	public void exportToJson() {
		Json json = new Json();

		String save_str = json.prettyPrint(this);

		FileHandle file = Gdx.files
				.absolute(Gdx.files.getLocalStoragePath() + filename);

		file.writeString(save_str, false);
	}

}
