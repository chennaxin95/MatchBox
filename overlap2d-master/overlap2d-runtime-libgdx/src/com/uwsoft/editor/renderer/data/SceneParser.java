package com.uwsoft.editor.renderer.data;

import java.io.*;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.uwsoft.editor.renderer.model.AidenModel;
import com.uwsoft.editor.renderer.model.Point;
import com.uwsoft.editor.renderer.model.ProjectModel;

public class SceneParser {
	
	private ProjectInfoVO pv;
	private SceneVO sv;
	
	private ProjectModel project;
	private AidenModel aiden = new AidenModel();
	private Json json = new Json();
	
	private String AIDEN_NAME = "aiden";
	private String WOOD_NAME = "woodenBlock";
	private String WATER_NAME = "water";
	private String STONE_NAME = "stone";
	private String PLATFORM_NAME = "earthtile";
	
	public SceneParser(ProjectInfoVO project, SceneVO scene){
		this.pv = project;
		this.sv = scene;
	}
	
	public void parseScene(){
		CompositeVO composite = sv.composite;
		if (composite == null) {
			return;
		}
		ArrayList<SimpleImageVO> sImages = composite.sImages;
		PhysicsPropertiesVO physicsPropertiesVO = sv.physicsPropertiesVO;
		
		for (SimpleImageVO s:sImages){
			if(s.imageName.equals(AIDEN_NAME)){
				aiden.start_pos.add(new Point((int)s.x/2, (int)s.y/2));
				aiden.scale_x = s.scaleX;
				aiden.scale_y = s.scaleY;
				String custom = s.customVars;
				if(custom.length() == 0) break;
				for (String property:custom.split(";")){
					String property_name = property.split(":")[0];
					String property_value = property.split(":")[1];
					if(property_name.equals("density")){
						aiden.density = Float.parseFloat(property_value);
					}
					if(property_name.equals( "start_fuel")){
						aiden.start_fuel = Float.parseFloat(property_value);
					}
					if(property_name.equals("critical_fuel")){
						aiden.critical_fuel = Float.parseFloat(property_value);
					}
					if(property_name.equals("max_fuel")){
						aiden.critical_fuel = Float.parseFloat(property_value);
					}
					if(property_name.equals("max_walk")){
						aiden.max_fuel = Float.parseFloat(property_value);
					}
					if(property_name.equals("jump_heigt")){
						aiden.jump_height = Float.parseFloat(property_value);
					}
					if(property_name.equals("vshrink")){
						aiden.vshrink = Float.parseFloat(property_value);
					}
					if(property_name.equals("hshrink")){
						aiden.hshrink = Float.parseFloat(property_value);
					}
					if(property_name.equals("fright")){
						aiden.fright = Boolean.parseBoolean(property_value);
					}
				}
			}
			if(s.imageName.equals(WOOD_NAME)){
				
			}
			if(s.imageName.equals(WATER_NAME)){
				
			}
			if(s.imageName.equals(STONE_NAME)){
				
			}
			if(s.imageName.equals(PLATFORM_NAME)){
				
			}
		}
		writeJson();
	}
	
	private void writeJson(){
		json.setTypeName(null);
		json.setUsePrototypes(false);
		json.setIgnoreUnknownFields(true);
		json.setOutputType(OutputType.json);

		//String aiden_str = json.prettyPrint(aiden);
		//FileHandle file = Gdx.files.local("aiden-example.json");
		project = new ProjectModel(aiden);
		String project_str = json.prettyPrint(project);
		FileHandle file = Gdx.files.local("aiden-example.json");
		System.out.println(project_str);
		file.writeString(project_str, true);
	}
	
}
