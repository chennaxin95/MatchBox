package com.uwsoft.editor.renderer.model;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class AidenModel implements Json.Serializable {
	public static float density = 0f;
	
	public static ArrayList<Point> start_pos = new ArrayList();
	
	public static float scale_x = 1f;
	public static float scale_y = 1f;
	
	public static float start_fuel = 30;
	public static float critical_fuel = 15;
	public static float max_fuel = 50;
	
	public static float max_walk = 5f;
	
	public static float jump_height = 11f;
	
	public static float vshrink = 0.95f;
	public static float hshrink = 0.7f;
	
	public static boolean fright = true;
	

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


