package edu.cornell.gdiac.physics.editor;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.character.CharacterModel;

public class WaterModelJsonRep {
	public float density = 0f;
	
	public PointJsonRep pos = new PointJsonRep(1,1);
	
	public float scale_x = 1;
	public float scale_y = 1;
	
	public String name = "a";
	
	public boolean fright = true;
	
	
	public WaterModelJsonRep(CharacterModel ch){
		if (ch==null) return;
		this.name = ch.getName();
		this.fright=ch.isFacingRight();
		pos = new PointJsonRep(ch.getX(), ch.getY());
	}
}
