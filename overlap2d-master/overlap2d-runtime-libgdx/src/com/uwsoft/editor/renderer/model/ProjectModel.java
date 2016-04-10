package com.uwsoft.editor.renderer.model;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ProjectModel implements Json.Serializable{
	private int level = 1;
	
	private String background = "shared/background.png";
	private AidenModel aiden;
	
	public ProjectModel (int level, String bg, AidenModel aiden){
		this.level = level;
		this.background = bg;
		this.aiden = aiden;
	}
	
	public ProjectModel(AidenModel aiden){
		this.aiden = aiden;
	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("level",level);
		json.writeValue("background", background);
		json.writeValue("aiden",aiden);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
	
}
