package com.uwsoft.editor.renderer.model;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ProjectModel implements Json.Serializable{
	private int level = 1;
	
	private String background = "shared/background.png";
	private AidenModel aiden;
	private ArrayList<Block> blocks;
	private ArrayList<WaterModel> waters;
	private GoalModel goal;
	
	public ProjectModel (int level, String bg, AidenModel aiden, GoalModel goal){
		this.level = level;
		this.background = bg;
		this.aiden = aiden;
		this.goal = goal;
	}
	
	public ProjectModel(AidenModel aiden, ArrayList<Block> blocks, 
			ArrayList<WaterModel> waters, GoalModel goal){
		this.aiden = aiden;
		this.blocks = blocks;
		this.waters = waters;
		this.goal = goal;
	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("level",level);
		json.writeValue("background", background);
		json.writeValue("aiden",aiden);
		json.writeValue("blocks",blocks);
		json.writeValue("waters",waters);
		json.writeValue("goal",goal);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
	
}
