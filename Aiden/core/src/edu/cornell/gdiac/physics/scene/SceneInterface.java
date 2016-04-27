package edu.cornell.gdiac.physics.scene;

import java.util.ArrayList;

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
import edu.cornell.gdiac.physics.character.WaterGuard;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;

public interface SceneInterface {
	
	public int getGridWidth();
	
	public int getGridHeight();
	
	public int getGridUnit();
	
	public AidenModel getAidenModel();
	
	public ArrayList<BlockAbstract> getBlocks();
	
	public ArrayList<StoneBlock> getStoneBlocks(boolean trapdoor);
	
	public ArrayList<FlammableBlock> getWoodBlocks();
	
	public ArrayList<FuelBlock> getFuelBlocks();
	
	public ArrayList<Platform> getPlatform();
	
	public ArrayList<BurnablePlatform> getBurnablePlatforms();
	
	public ArrayList<WaterGuard> getGuards();
	
	public ArrayList<Rope> getRopes();
	
	public ArrayList<TrapDoor> getTrapDoors();
	
	public ArrayList<FlammableBlock> getFlammables(boolean rope, boolean fuel, boolean bplatform);
	
	public ArrayList<Obstacle> getObstacles(boolean aiden, boolean npc);
}
