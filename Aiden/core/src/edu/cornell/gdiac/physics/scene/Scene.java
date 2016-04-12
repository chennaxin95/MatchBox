package edu.cornell.gdiac.physics.scene;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.blocks.FuelBlock;
import edu.cornell.gdiac.physics.blocks.Platform;
import edu.cornell.gdiac.physics.blocks.StoneBlock;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.CharacterModel.CharacterType;
import edu.cornell.gdiac.physics.character.WaterGuard;
import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;

public class Scene implements SceneInterface {

	private AssetFile af;

	private AidenModel aidenModel;
	private ArrayList<BlockAbstract> blocks;
	private ArrayList<FlammableBlock> woodBlocks = new ArrayList();
	private ArrayList<FuelBlock> fuelBlocks = new ArrayList();
	private ArrayList<StoneBlock> stoneBlocks = new ArrayList();
	private ArrayList<CharacterModel> guards = new ArrayList();
	private ArrayList<Platform> platforms = new ArrayList();

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
				System.out.println(af.avatarTexture);
				aidenModel.setTexture(af.avatarTexture);
				aidenModel.setCharacterSprite(af.AidenAnimeTexture);
				aidenModel.setDeath(af.AidenDieTexture);
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
					FlammableBlock flamb = new FlammableBlock(x, y, b_scale_x, b_scale_y,
							burn_spread, burn_time);
					woodBlocks.add(flamb);
					flamb.setTexture(af.woodTexture);
				} else {
					if (material.equals("stone")) {
						StoneBlock stoneb = new StoneBlock(x, y, b_scale_x, b_scale_y);
						stoneBlocks.add(stoneb);
						stoneb.setTexture(af.stoneTexture);
								;
					} else {
						if (material.equals("fuel")) {
							FuelBlock fuelb = new FuelBlock(x, y, b_scale_x, b_scale_y,
									burn_spread, burn_time, fuels);
							fuelBlocks.add(fuelb);
						} else {
							if (material.equals("platform")) {
								Platform platf = new Platform(new Rectangle(x - b_scale_x / 2f,
										y - b_scale_y / 2f, b_scale_x,
										b_scale_y),1);
								platforms.add(platf);
								platf.setTexture(af.earthTile);
							} else {
								System.err
										.println("new material : " + material);
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
				water.setTexture(af.waterTexture);
				water.setCharacterSprite(af.WaterWalkTexture);
				water.setDeath(af.WaterDieTexture);
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
			goalDoor = new StoneBlock(exit_x, exit_y, e_scale_x, e_scale_y);
			goalDoor.setTexture(af.goalTile);
		}

	}

	public AidenModel getAidenModel() {
		return aidenModel;
	}

	/** All the blocks but goal door */
	public ArrayList<BlockAbstract> getBlocks() {
		ArrayList<BlockAbstract> container = new ArrayList<BlockAbstract>();

		container.addAll(this.getWoodBlocks());
		container.addAll(this.getStoneBlocks());
		container.addAll(this.getPlatform());
		container.addAll(this.getFuelBlocks());
		return container;
	}

	public ArrayList<FlammableBlock> getWoodBlocks() {
		return woodBlocks;
	}

	public ArrayList<CharacterModel> getGuards() {
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
	public ArrayList<StoneBlock> getStoneBlocks() {
		return stoneBlocks;
	}

	@Override
	public ArrayList<FuelBlock> getFuelBlocks() {
		return fuelBlocks;
	}

	public BlockAbstract getGoalDoor() {
		return goalDoor;
	}

}
