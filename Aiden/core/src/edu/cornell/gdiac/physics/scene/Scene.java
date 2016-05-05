package edu.cornell.gdiac.physics.scene;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.blocks.BurnablePlatform;
import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.blocks.FuelBlock;
import edu.cornell.gdiac.physics.blocks.Platform;
import edu.cornell.gdiac.physics.blocks.Rope;
import edu.cornell.gdiac.physics.blocks.StoneBlock;
import edu.cornell.gdiac.physics.blocks.TrapDoor;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.CharacterModel.CharacterType;
import edu.cornell.gdiac.physics.character.WaterGuard;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;

public class Scene implements SceneInterface {

	private AssetFile af;

	private int width = 32;
	private int height = 18;
	
	private AidenModel aidenModel;
	private ArrayList<BlockAbstract> blocks;
	private ArrayList<FlammableBlock> woodBlocks = new ArrayList<FlammableBlock>();
	private ArrayList<FuelBlock> fuelBlocks = new ArrayList<FuelBlock>();
	private ArrayList<StoneBlock> stoneBlocks = new ArrayList<StoneBlock>();
	private ArrayList<WaterGuard> guards = new ArrayList<WaterGuard>();
	private ArrayList<Platform> platforms = new ArrayList<Platform>();
	private ArrayList<Rope> ropes = new ArrayList<Rope>();
	private ArrayList<TrapDoor> trapdoors = new ArrayList<TrapDoor>();
	private ArrayList<BurnablePlatform> bplatforms = new ArrayList<BurnablePlatform>();

	private BlockAbstract goalDoor;

	/** Sets asset file */
	public void setAssetFile(AssetFile a) {
		this.af = a;
	}

	public Scene(String s) {

		JSONParser jp = new JSONParser(s);
		JsonValue jv = jp.getJsonValue();
		String background = jv.getString("background");
		JsonValue aiden = jv.get("aiden");
		JsonValue objects = jv.get("blocks");
		JsonValue jguards = jv.get("waters");
		JsonValue exit = jv.get("goal");
		if (jv.has("width")){
			width = jv.getInt("width");
			height = jv.getInt("height");
		}

		
		// Aiden

		if (aiden != null) {
			JsonValue start_pos_array = aiden.get("start_pos");
			if (start_pos_array != null && start_pos_array.size > 0) {
				JsonValue start_pos = start_pos_array.get(0);
				float start_pos_x = start_pos.getFloat("x");
				float start_pos_y = start_pos.getFloat("y");
				float density = aiden.getFloat("density");
				float scale_x = aiden.getFloat("scale_x");
				float scale_y = aiden.getFloat("scale_y");
				boolean fright = aiden.getBoolean("fright");
				int max_walk_speed = aiden.getInt("max_walk");
				int jump_height = aiden.getInt("jump_height");
				int start_fuel = aiden.getInt("start_fuel");
				aidenModel = new AidenModel(start_pos_x, start_pos_y,
						scale_x, scale_y, fright);
			}

		}

		// Blocks
		if (objects != null) {
			int n = objects.size;
			for (int i = 0; i < n; i++) {
				JsonValue obj = objects.get(i);
				int id = obj.getInt("id");
				String material = obj.getString("blockType");
				boolean fixed = obj.getBoolean("fixed");
				int burn_spread = obj.getInt("burn_spread");
				int burn_time = obj.getInt("burn_time");
				JsonValue position = obj.get("pos");
				float x = position.getFloat("x");
				float y = position.getFloat("y");
				String texture = obj.getString("texture");
				float b_scale_x = obj.getFloat("scale_x");
				float b_scale_y = obj.getFloat("scale_y");
				float b_density = obj.getFloat("density");
				JsonValue link_pos = obj.get("link_pos");
				float link_x = link_pos.getFloat("x");
				float link_y = link_pos.getFloat("y");
				int fuels = obj.getInt("fuels");
				if (material.equals("wood")) {
					woodBlocks
					.add(new FlammableBlock(x, y, b_scale_x, b_scale_y,
							burn_spread, burn_time));
				} else {
					if (material.equals("stone")) {
						stoneBlocks.add(
								new StoneBlock(x, y, b_scale_x, b_scale_y));
					} else {
						if (material.equals("fuel")) {
							fuelBlocks.add(
									new FuelBlock(x, y, 1/*b_scale_x*/,1/*b_scale_y*/,
											burn_spread, burn_time, fuels,
											false));
						} else {
							if (material.equals("platform")) {
								platforms.add(new Platform(
										new Rectangle(x - b_scale_x / 2f,
												y - b_scale_y / 2f, b_scale_x,
												b_scale_y),
										1));
							} else {
								if(material.equals("rope")){
									ropes.add(new Rope(x,y,0.25f,0.25f));
								}else{
									boolean is_left = obj.getBoolean("isLeft");
									if(material.equals("trapdoor")){
										trapdoors.add(new TrapDoor(x,y,4f, 0.25f, is_left));
									}else{
										if(material.equals("burnable_platform")){
											bplatforms.add(new BurnablePlatform(
													new Rectangle(x - b_scale_x / 2f,
															y - b_scale_y / 2f, b_scale_x,
															b_scale_y),1, null));
										}else{
											System.err
											.println("new material : " + material);
										}
									}
								}

							}
						}
					}
				}

			}
		}

		// Water guards
		if (jguards != null) {
			int guard_n = jguards.size;
			for (int i = 0; i < guard_n; i++) {
				JsonValue guard = jguards.get(i);
				String guard_name = guard.getString("name");
				JsonValue g_start_pos = guard.get("pos");
				float g_x = g_start_pos.getFloat("x");
				float g_y = g_start_pos.getFloat("y");
				float g_scale_x = guard.getFloat("scale_x");
				float g_scale_y = guard.getFloat("scale_y");
				boolean g_fright = guard.getBoolean("fright");
				WaterGuard water = new WaterGuard(
						CharacterType.WATER_GUARD,
						guard_name, g_x, g_y, g_scale_x, g_scale_y, g_fright);
				guards.add(water);
			}
		}

		// Exit
		if (exit != null) {
			String ex_texture = exit.getString("texture");
			JsonValue exit_pos = exit.get("pos");
			float exit_x = exit_pos.getFloat("x");
			float exit_y = exit_pos.getFloat("y");
			float e_scale_x = exit.getFloat("scale_x");
			float e_scale_y = exit.getFloat("scale_y");
			goalDoor = new StoneBlock(exit_x, exit_y, 3, 3);
			goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
			goalDoor.setDensity(0.0f);
			goalDoor.setFriction(0.0f);
			goalDoor.setRestitution(0.0f);
			goalDoor.setSensor(true);
			//
			goalDoor.setName("goal");
		}

	}

	public AidenModel getAidenModel() {
		return aidenModel;
	}

	/** All the blocks but goal door and ropes */
	public ArrayList<Obstacle> getBlocks() {
		ArrayList<Obstacle> container = new ArrayList<Obstacle>();

		container.addAll(this.getWoodBlocks());
		container.addAll(this.getStoneBlocks(true));
		container.addAll(this.getPlatform());
		container.addAll(this.getFuelBlocks());
		container.addAll(this.bplatforms);
		return container;
	}

	public ArrayList<FlammableBlock> getWoodBlocks() {
		return woodBlocks;
	}

	public ArrayList<WaterGuard> getGuards() {
		return guards;
	}

	@Override
	public int getGridWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGridHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGridUnit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<Platform> getPlatform() {
		return platforms;
	}

	@Override
	public ArrayList<StoneBlock> getStoneBlocks(boolean trapdoor) {
		if(trapdoor){
			ArrayList<StoneBlock> container = new ArrayList<StoneBlock>();
			container.addAll(stoneBlocks);
		}
		return stoneBlocks;
	}

	@Override
	public ArrayList<FuelBlock> getFuelBlocks() {
		return fuelBlocks;
	}

	public BlockAbstract getGoalDoor() {
		return goalDoor;
	}

	@Override
	public ArrayList<Rope> getRopes() {
		// TODO Auto-generated method stub
		return ropes;
	}

	@Override
	public ArrayList<TrapDoor> getTrapDoors() {
		// TODO Auto-generated method stub
		return trapdoors;
	}

	@Override
	public ArrayList<FlammableBlock> getFlammables(boolean rope, boolean fuel, boolean bplatform) {
		// TODO Auto-generated method stub
		ArrayList<FlammableBlock> container = new ArrayList<FlammableBlock>();
		container.addAll(woodBlocks);
		if (rope){
			//container.addAll(ropes);
		}
		if(fuel){
			container.addAll(fuelBlocks);
		}
		if(bplatform){
			for (BurnablePlatform bp : bplatforms){
				container.add(bp.getPlatform());
			}
		}
		return container;
	}

	@Override
	public ArrayList<Obstacle> getObstacles(boolean aiden, boolean npc) {
		// TODO Auto-generated method stub
		ArrayList<Obstacle> container = new ArrayList<Obstacle>();
		container.addAll(getBlocks());
		container.addAll(ropes);
		if(aiden){
			container.add(aidenModel);
		}
		if(npc){
			container.addAll(this.getGuards());
		}
		return container;
	}
	
	public ArrayList<BurnablePlatform> getBurnablePlatforms(){
		return bplatforms;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}



}
