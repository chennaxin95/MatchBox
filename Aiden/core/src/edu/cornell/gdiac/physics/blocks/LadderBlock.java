package edu.cornell.gdiac.physics.blocks;

public class LadderBlock extends StoneBlock {

	
	public LadderBlock(float x, float y, float width, float height,
			float spreadRate, float burnRate) {
		super(x, y, width, height);
	
		// TODO Auto-generated constructor stub
		material.setClimbable(true);
	}

	public LadderBlock(float width, float height, float spreadRate,
			float burnRate) {
		super(width, height);
		
		material.setClimbable(true);
		// TODO Auto-generated constructor stub
	}

}
