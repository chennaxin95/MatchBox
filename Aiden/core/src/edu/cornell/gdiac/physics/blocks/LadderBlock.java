package edu.cornell.gdiac.physics.blocks;

public class LadderBlock extends FlammableBlock {

	
	public LadderBlock(float x, float y, float width, float height,
			float spreadRate, float burnRate) {
		super(x, y, width, height, spreadRate, burnRate);
	
		// TODO Auto-generated constructor stub
		flammable = true;
		breakable = false;
		climbable = true;
		moveable = false;
		
	}

	public LadderBlock(float width, float height, float spreadRate,
			float burnRate) {
		super(width, height, spreadRate, burnRate);
		flammable = true;
		breakable = false;
		climbable = true;
		moveable = false;
		
		// TODO Auto-generated constructor stub
	}

}
