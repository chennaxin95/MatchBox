package edu.cornell.gdiac.physics.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;


import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;

/* The main parsing functions are in Scene class, this class is currently more like 
 * a JSON loader.
 */
public class JSONParser {
	private JsonValue jv;
	public JSONParser(String s){
		FileHandle fh = Gdx.files.local(s);
		JsonReader jr = new JsonReader();
		jv = jr.parse(fh);
	}
	
	public JsonValue getJsonValue(){
		return jv;
	}
		
		
		
}
