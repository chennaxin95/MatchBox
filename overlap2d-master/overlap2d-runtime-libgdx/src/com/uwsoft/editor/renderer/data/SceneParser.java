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
import com.uwsoft.editor.renderer.model.Block;
import com.uwsoft.editor.renderer.model.GoalModel;
import com.uwsoft.editor.renderer.model.Point;
import com.uwsoft.editor.renderer.model.ProjectModel;
import com.uwsoft.editor.renderer.model.WaterModel;

public class SceneParser {

	private ProjectInfoVO pv;
	private SceneVO sv;

	private ProjectModel project;
	private AidenModel aiden = new AidenModel();
	private ArrayList<Block> blocks = new ArrayList();
	private ArrayList<WaterModel> waters = new ArrayList();
	private GoalModel goal = new GoalModel();
	
	private Json json = new Json();

	private int block_id = 0;

	private String AIDEN_NAME = "aiden";
	private String WOOD_NAME = "woodenBlock";
	private String WATER_NAME = "water";
	private String STONE_NAME = "stone";
	private String FUEL_NAME = "fuelBlock";
	private String PLATFORM_NAME = "earthtile";
	private String GOAL_NAME = "goaldoor";

	public SceneParser(ProjectInfoVO project, SceneVO scene){
		this.pv = project;
		this.sv = scene;
		aiden.start_pos = new ArrayList();
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
				System.out.println(aiden.start_pos.size());
				aiden.scale_x = s.scaleX;
				aiden.scale_y = s.scaleY;
				String custom = s.customVars;
				if(custom.length() != 0){
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
			}
			if(s.imageName.equals(WOOD_NAME)){
				Block block = new Block();
				block.blockType = "wood";
				blockProperty(s,block);
				String custom = s.customVars;
				if(custom.length() != 0) {
					for (String property:custom.split(";")){
						String property_name = property.split(":")[0];
						String property_value = property.split(":")[1];
						flammableProperty(block,property_name, property_value);
					}
				}
				blocks.add(block);
			}
			if(s.imageName.equals(STONE_NAME)){
				Block block = new Block();
				block.blockType = "stone";
				blockProperty(s,block);
				blocks.add(block);
			}
			if(s.imageName.equals(FUEL_NAME)){
				Block block = new Block();
				block.blockType = "fuel";
				blockProperty(s,block);
				String custom = s.customVars;
				if(custom.length() != 0) {
					for (String property:custom.split(";")){
						String property_name = property.split(":")[0];
						String property_value = property.split(":")[1];
						flammableProperty(block, property_name, property_value);
						if(property_name.equals("fuels")){
							block.fuels = Float.parseFloat(property_value);
						}
					}
				}
				blocks.add(block);
			}
			if(s.imageName.equals(PLATFORM_NAME)){
				Block block = new Block();
				block.blockType = "platform";
				block.fixed = true;
				blockProperty(s,block);
				String custom = s.customVars;
				if(custom.length() != 0) {
					for (String property:custom.split(";")){
						String property_name = property.split(":")[0];
						String property_value = property.split(":")[1];
						flammableProperty(block,property_name, property_value);
					}
				}
				blocks.add(block);
			}
			if(s.imageName.equals(WATER_NAME)){
				WaterModel water = new WaterModel();
				water.pos = new Point((int)s.x/2, (int)s.y/2);
				water.scale_x = s.scaleX;
				water.scale_y = s.scaleY;
				String custom = s.customVars;
				if(custom.length() != 0){
					for (String property:custom.split(";")){
						String property_name = property.split(":")[0];
						String property_value = property.split(":")[1];
						if(property_name.equals("density")){
							water.density = Float.parseFloat(property_value);
						}
						if(property_name.equals("name")){
							water.name = property_value;
						}
						if(property_name.equals("fright")){
							water.fright = Boolean.parseBoolean(property_value);
						}
					}
				}
				waters.add(water);
			}

			if(s.imageName.equals(GOAL_NAME)){
				goal.pos = new Point((int)s.x/2, (int)s.y/2);
				goal.scale_x = s.scaleX;
				goal.scale_y = s.scaleY;
				goal.texture = s.imageName;
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
		project = new ProjectModel(aiden, blocks,waters, goal);
		String project_str = json.prettyPrint(project);
		FileHandle file = Gdx.files.local("aiden-example.json");
		file.writeString(project_str, true);
	}
	
	private void blockProperty(SimpleImageVO s, Block block){
		block.id = block_id;
		block.pos = new Point((int)s.x/2,(int)s.y/2);

		block.texture = s.imageName;
		block.scale_x = s.scaleX;
		block.scale_y = s.scaleY;
		String custom = s.customVars;
		if(custom.length() != 0) {
			for (String property:custom.split(";")){
				String property_name = property.split(":")[0];
				String property_value = property.split(":")[1];
				if(property_name.equals("id")){
					block.id = Integer.parseInt(property_value);
				}
				if(property_name.equals("density")){
					block.density = Float.parseFloat(property_value);
				}
				if(property_name.equals("fixed")){
					block.fixed = Boolean.parseBoolean(property_value);
				}
				if(property_name.equals("link_x")){
					block.linked = true;
					block.link_pos.setX(Float.parseFloat(property_value));
				}
				if(property_name.equals("link_y")){
					block.link_pos.setY(Float.parseFloat(property_value));
				}
				if(property_name.equals("link_obj")){
					block.link_obj = Integer.parseInt(property_value);
				}
			}
		}
		block_id++;
	}
	
	private void flammableProperty(Block block, String property_name, String property_value){
		if(property_name.equals("burning")){
			block.burning = Boolean.parseBoolean(property_value);
		}
		if(property_name.equals("burn_spread")){
			block.burn_spread = Float.parseFloat(property_value);
		}
		if(property_name.equals("burn_time")){
			block.burn_time = Float.parseFloat(property_value);
		}
		if(property_name.equals("burn_left")){
			block.burn_left = Float.parseFloat(property_value);
		}
	}

}
