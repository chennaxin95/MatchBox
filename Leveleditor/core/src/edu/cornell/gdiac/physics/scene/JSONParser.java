package edu.cornell.gdiac.physics.scene;

import com.badlogic.gdx.utils.JsonReader;

import java.io.File;

import javax.print.DocFlavor.URL;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;

/* The main parsing functions are in Scene class, this class is currently more like 
 * a JSON loader.
 */
public class JSONParser {
	private JsonValue jv;
	public JSONParser(String s){
		File f = new File(s);
		FileHandle fh = new FileHandle(f);
		JsonReader jr = new JsonReader();
		jv = jr.parse(fh);
	}
	
	public JsonValue getJsonValue(){
		return jv;
	}
		
		
		
}
