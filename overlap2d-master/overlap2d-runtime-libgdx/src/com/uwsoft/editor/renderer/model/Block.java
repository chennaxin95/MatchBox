package com.uwsoft.editor.renderer.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Block implements Json.Serializable{
	
	public int id = 0;
	
	public String blockType = "wood";
	public String texture = "shared/woodenBlock.png";
	
	public Point pos = new Point(1,1);
	
	public float scale_x = 1;
	public float scale_y = 1;
	
	public float density = 1;
	
	public boolean fixed = false;
	public boolean linked = false;
	
	public Point link_pos = new Point(0,0);
	public int link_obj = 0;
	
	public boolean burning = false;
	public float burn_spread = 5;
	public float burn_time = 3;
	public float burn_left = 1;
	public float fuels = 30;
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("id", id);
		json.writeValue("blockType",blockType);
		json.writeValue("texture",texture);
		json.writeValue("pos", pos);
		json.writeValue("scale_x",scale_x);
		json.writeValue("scale_y",scale_y);
		json.writeValue("density",density);
		json.writeValue("fixed",fixed);
		json.writeValue("linked",linked);
		json.writeValue("link_pos",link_pos);
		json.writeValue("burning", burning);
		json.writeValue("burn_spread", burn_spread);
		json.writeValue("burn_time",burn_time);
		json.writeValue("burn_left", burn_left);
		json.writeValue("fuels",fuels);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
	
}
