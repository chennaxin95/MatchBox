package edu.cornell.gdiac.physics.scene;

import com.badlogic.gdx.utils.JsonValue;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.blocks.FuelBlock;
import edu.cornell.gdiac.physics.blocks.StoneBlock;
import edu.cornell.gdiac.physics.blocks.WoodBlock;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.CharacterModel.CharacterType;

public class Scene implements SceneInterface{

	private AidenModel aidenModel;
	private BlockAbstract[] blocks;
	private CharacterModel[] guards;

	public Scene(String s){
		JSONParser jp = new JSONParser(s);
		JsonValue jv = jp.getJsonValue();
		String background = jv.getString("Background");
		JsonValue aiden = jv.get("Aiden");
		JsonValue objects = jv.get("Objects");
		JsonValue jguards = jv.get("Guards");
		JsonValue exit = jv.get("Exit");

		// Aiden
		JsonValue start_pos = aiden.get("start_pos");
		int start_pos_x = start_pos.getInt("x");
		int start_pos_y = start_pos.getInt("y");
		int mass = aiden.getInt("mass");
		int a_width = aiden.getInt("width");
		int a_height = aiden.getInt("height");
		boolean fright = aiden.getBoolean("fright");
		int max_walk_speed = aiden.getInt("max_walk_speed");
		int jump_height = aiden.getInt("jump_height");
		int start_fuel = aiden.getInt("start_fuel");
		aidenModel = new AidenModel(start_pos_x, start_pos_y, a_width, a_height, fright);

		// Blocks
		int n = objects.size;
		blocks = new BlockAbstract[n];
		for(int i = 0; i<n; i++){
			JsonValue obj = objects.get(i);
			int id = obj.getInt("obj_id");
			String material = obj.getString("material");
			boolean fixed = obj.getBoolean("fixed");
			int burn_spread = obj.getInt("burn_spread");
			int burn_time = obj.getInt("burn_time");
			JsonValue position = obj.get("position");
			int x = position.getInt("x");
			int y = position.getInt("y");
			String texture = obj.getString("texture");
			int width = obj.getInt("width");
			int height = obj.getInt("height");
			int weight = obj.getInt("weight");
			JsonValue link_pos = obj.get("link_pos");
			int link_x = link_pos.getInt("x");
			int link_y = link_pos.getInt("y");
			int fuels = obj.getInt("fuels");
			if(material.equals("wood")){
				blocks[i] = new WoodBlock(x,y,width,height,burn_spread,burn_time, 0);
			}else{
				if(material.equals("stone")){
					blocks[i] = new StoneBlock(x,y,width,height);
				}else{
					if(material.equals("fuel")){
						blocks[i] = new FuelBlock(x,y,width,height,burn_spread,burn_time,fuels);
					}else{
						System.err.println("new material : "+material);
					}
				}
			}

		}

		// Water guards
		int guard_n = jguards.size;
		guards = new CharacterModel[guard_n];
		for(int i = 0; i<guard_n; i++){
			JsonValue guard = jguards.get(i);
			String guard_name = guard.getString("guard_name");
			JsonValue g_start_pos = guard.get("start_pos");
			float g_x = g_start_pos.getFloat("x");
			float g_y = g_start_pos.getFloat("y");
			float g_w = guard.getFloat("width");
			float g_h = guard.getFloat("height");
			boolean g_fright = guard.getBoolean("fright");
			
			guards[i] = new CharacterModel(CharacterType.WATER_GUARD, guard_name, g_x,g_y,g_w, g_h,g_fright);
		}

		//Exit
		String ex_texture = exit.getString("texture");
		JsonValue exit_pos = exit.get("exit_pos");
		int exit_x = exit_pos.getInt("x");
		int exit_y = exit_pos.getInt("y");

	}


	public AidenModel getAidenModel(){
		return aidenModel;
	}

	public BlockAbstract[] getBlocks(){
		return blocks;
	}

	public CharacterModel[] getGuards(){
		return guards;
	}
}
