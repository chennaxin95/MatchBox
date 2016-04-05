package edu.cornell.gdiac.physics.scene;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;

public interface SceneInterface {
	
	public AidenModel getAidenModel();
	
	public BlockAbstract[] getBlocks();
	
	public CharacterModel[] getGuards();
}
