package edu.cornell.gdiac.physics.editor;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;

public class GoalModelJsonRep {
	public PointJsonRep pos = new PointJsonRep(1,1);
	
	public float scale_x=1;
	public float scale_y=1;
	
	public String texture = "platform/goaldoor.png";
	
	public GoalModelJsonRep(BlockAbstract block){
		if (block==null) return;
		pos = new PointJsonRep(block.getX(), block.getY());
		scale_x=block.getWidth();
		scale_y=block.getHeight();
	}
}
