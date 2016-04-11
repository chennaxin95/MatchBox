package edu.cornell.gdiac.physics.editor;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import edu.cornell.gdiac.physics.character.CharacterModel;

public class AidenModelJsonRep implements Json.Serializable {
	public float density = 0f;
	
	public ArrayList<PointJsonRep> start_pos = new ArrayList();
	
	public float scale_x = 1f;
	public float scale_y = 1f;
	
	public float start_fuel = 30;
	public float critical_fuel = 15;
	public float max_fuel = 50;
	
	public float max_walk = 5f;
	
	public float jump_height = 11f;
	
	public float vshrink = 0.95f;
	public float hshrink = 0.7f;
	
	public boolean fright = true;
	
	public AidenModelJsonRep(CharacterModel ch){
		if (ch==null) return;
		fright=ch.isFacingRight();
		start_pos.add(new PointJsonRep(ch.getX(), ch.getY()));
	}
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("start_pos",start_pos);
		json.writeValue("scale_x",scale_x);
		json.writeValue("scale_y",scale_y);
		json.writeValue("density",density);
		json.writeValue("start_fuel",start_fuel);
		json.writeValue("critical_fuel",critical_fuel);
		json.writeValue("max_fuel",max_fuel);
		json.writeValue("max_walk",max_walk);
		json.writeValue("jump_height",jump_height);
		json.writeValue("vshrink",vshrink);
		json.writeValue("hshrink",hshrink);
		json.writeValue("fright",fright);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
	
}


