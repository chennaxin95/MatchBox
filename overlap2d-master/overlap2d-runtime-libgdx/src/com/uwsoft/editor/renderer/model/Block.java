package com.uwsoft.editor.renderer.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Block implements Json.Serializable{
	
	private String blockType = "wood";
	private String texture = "shared/woodenBlock.png";
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
	
}
