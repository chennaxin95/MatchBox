package edu.cornell.gdiac.physics.scene;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;

public interface SceneInterface {
	
	public int getGridWidth();
	
	public int getGridHeight();
	
	public int getGridUnit();
	
	public AidenModel getAidenModel();
	
	public BlockAbstract[] getBlocks();
	
	public PolygonObstacle[] getPlatform();
	
	public CharacterModel[] getGuards();
}
