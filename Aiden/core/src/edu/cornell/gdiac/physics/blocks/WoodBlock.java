package edu.cornell.gdiac.physics.blocks;

public class WoodBlock extends FlammableBlock{
	

	public WoodBlock(float x, float y, float width, float height,
			float spreadRate, float burnRate) {
		super(x, y, width, height, spreadRate, burnRate);
	}
	
	public WoodBlock(float width, float height,float spreadRate, float burnRate) {
		super(width, height, spreadRate, burnRate);
		// TODO Auto-generated constructor stub
	}	
}
